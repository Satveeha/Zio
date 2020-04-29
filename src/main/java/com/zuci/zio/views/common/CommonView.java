package com.zuci.zio.views.common;

import org.springframework.beans.factory.annotation.Autowired;
import com.zuci.zio.dao.CommonConfigDao;
import com.zuci.zio.model.CommonConfig;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.zuci.zio.views.main.MainView;

@Route(value = "commons", layout = MainView.class)
@PageTitle("Commons")
//@CssImport("styles/views/commonconfigview/commons-run-console-run-console-view.css")
@CssImport(value = "./styles/my-grid-styles.css", themeFor = "vaadin-grid")
public class CommonView extends Div implements AfterNavigationObserver {

	@Autowired
	private static CommonConfigDao commonConfigDao;

	//Grid Variable
	private Grid<CommonConfig> commons;

	//Fields for configuration form
	private TextField variable = new TextField();
	private TextField value = new TextField();
	private Button cancel = new Button("Cancel");
	private Button save = new Button("Save");
	private Button add = new Button("Add");
	
	//Page layout in horizontal
	private HorizontalLayout horizontalLayout;

	//Binder binds the object
	private Binder<CommonConfig> binder;
	private CommonConfig commonConfig;

	//Constructor
	public CommonView(CommonConfigDao commonConfigDao) {

		this.commonConfigDao = commonConfigDao;
		
		setId("commons-run-console-run-console-view");
		
		horizontalLayout = new HorizontalLayout();
		horizontalLayout.setHeightFull();
		horizontalLayout.setWidthFull();

		// Configure Grid
		commons = new Grid<>();
		commons.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		commons.setWidth("90%");
		commons.getStyle().set("font-family", "Lato, sans-serif");
		commons.getStyle().set("margin-left", "85px");
		commons.getStyle().set("border", "none");
		commons.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		commons.setHeightByRows(true);
		commons.addColumn(CommonConfig::getVariable).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Variable</div>"));
		commons.addColumn(CommonConfig::getValue).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Value</div>"));

		commons.addComponentColumn(item -> createTrashIcon(commons, item)).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'></div>"));

		commons.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

		// Configure Form
		binder = new Binder<>(CommonConfig.class);
		binder.bindInstanceFields(this);

		// the grid valueChangeEvent will clear the form too
		cancel.addClickListener(e -> populateForm(new CommonConfig()));

		save.addClickListener(e -> {

			if (this.commonConfig == null) {
				this.commonConfig = new CommonConfig();
				this.commonConfig.setId(0L);
				this.commonConfig.setActive("Y");
				this.commonConfig.setVersion(0);
			}else if(this.commonConfig.getId() == null) {
				this.commonConfig = new CommonConfig();
				this.commonConfig.setId(0L);
				this.commonConfig.setActive("Y");
				this.commonConfig.setVersion(0);
			}

			String updatedVariable = variable.getValue();
			String updatedValue = value.getValue();

			this.commonConfig.setVariable(updatedVariable);
			this.commonConfig.setValue(updatedValue);

			CommonConfig insertData = this.commonConfigDao.insert(this.commonConfig);
			
			if(insertData != null) {
				Notification.show("Saved Successfully!");
				commons.setItems(commonConfigDao.findAll());
				populateForm(new CommonConfig());
			}else {
				Notification.show("Saved Failure!");
			}

		});

		add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		add.getStyle().set("margin-left", "89%");
		add.getStyle().set("background", "#58d2cc");
		add.getStyle().set("color", "#4b483f");
		add.getStyle().set("border-radius", "6px");
		add.addClickListener(e -> {
			populateForm(new CommonConfig());
			commons.asSingleSelect().clear();
		});

		createGridLayout(horizontalLayout);
		createEditorLayout(horizontalLayout);

		add(horizontalLayout);
	}

	//Create trash icon in grid
	private Icon createTrashIcon(Grid<CommonConfig> grid, CommonConfig item) {

		Icon trashIcon = new Icon(VaadinIcon.TRASH);
		trashIcon.setColor("#b2b5a6");
		trashIcon.getStyle().set("margin-left", "50%");
		trashIcon.addClickListener(event -> {
			System.out.println(item.getId());
			deleteConfirmDialog(grid, item);
		});

		return trashIcon;
	}

	//Conformation dialog for delete
	private void deleteConfirmDialog(Grid<CommonConfig> grid, CommonConfig item) {
		
		Dialog dialog = new Dialog();

		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);

		NativeButton confirmButton = new NativeButton("Confirm", event -> {
			
			CommonConfig insertValue = this.commonConfigDao.insertAudit(item);
			
			if(insertValue != null) {
				if(this.commonConfigDao.deleteById(item.getId())) {
					Notification.show("Deleted Successfully!");
					populateForm(new CommonConfig());
					dialog.close();

					ListDataProvider<CommonConfig> dataProvider = (ListDataProvider<CommonConfig>) grid.getDataProvider();
					dataProvider.getItems().remove(item);
					dataProvider.refreshAll();
				}else {
					Notification.show("Delete Failed!");
				}
				
			}else {
				Notification.show("Delete Failed!");
			}
			
		});
		
		confirmButton.getStyle().set("color", "#4b483f");
		confirmButton.getStyle().set("background-color", "#58d2cc");
		confirmButton.getStyle().set("padding", "0.5rem");
		confirmButton.getStyle().set("border-radius", "6px");
		confirmButton.getStyle().set("margin", "20px");
		confirmButton.getStyle().set("font-size", "16px");
		confirmButton.getStyle().set("border", "none");
		confirmButton.getStyle().set("font-weight", "600");

		NativeButton cancelButton = new NativeButton("Cancel", event -> {
			populateForm(new CommonConfig());
			dialog.close();
		});

		cancelButton.getStyle().set("color", "#4b483f");
		cancelButton.getStyle().set("background-color", "#58d2cc");
		cancelButton.getStyle().set("padding", "0.5rem");
		cancelButton.getStyle().set("border-radius", "6px");
		cancelButton.getStyle().set("font-size", "16px");
		cancelButton.getStyle().set("margin", "5px");
		cancelButton.getStyle().set("border", "none");
		cancelButton.getStyle().set("font-weight", "600");

		dialog.add(confirmButton, cancelButton);

		dialog.open();
	}

	//Create form
	private void createEditorLayout(HorizontalLayout horizontalLayout) {

		Div editorDiv = new Div();
		editorDiv.setId("editor-layout");

		FormLayout formLayout = new FormLayout();

		addFormItem(editorDiv, formLayout, variable, "Variable");
		addFormItem(editorDiv, formLayout, value, "Value");

		createButtonLayout(editorDiv);

		horizontalLayout.setHeightFull();
		horizontalLayout.setWidthFull();
		horizontalLayout.add(editorDiv);
	}

	//Create button layout
	private void createButtonLayout(Div editorDiv) {

		HorizontalLayout buttonLayout = new HorizontalLayout();

		buttonLayout.setId("button-layout");
		buttonLayout.setWidthFull();
		buttonLayout.setSpacing(true);

		cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		save.getStyle().set("background", "#58d2cc");
		save.getStyle().set("border-radius", "6px");
		save.getStyle().set("color", "#4b483f");

		buttonLayout.add(cancel, save);

		editorDiv.add(buttonLayout);
	}

	//Create grid layout
	private void createGridLayout(HorizontalLayout horizontalLayout) {

		Div wrapper = new Div();

		wrapper.setId("wrapper");
		wrapper.setWidthFull();
		horizontalLayout.add(wrapper);
		wrapper.add(add);
		wrapper.add(commons);
	}

	//Add fields to form
	private void addFormItem(Div wrapper, FormLayout formLayout, AbstractField field, String fieldName) {

		formLayout.addFormItem(field, fieldName);

		wrapper.add(formLayout);

		field.getElement().getClassList().add("full-width");
	}

	//Call after the execution of constructor
	@Override
	public void afterNavigation(AfterNavigationEvent event) {

		// Lazy init of the grid items, happens only when we are sure the view will be
		// shown to the user
		commons.setItems(commonConfigDao.findAll());
	}

	//Set Form Field Values
	private void populateForm(CommonConfig value) {

		//if value is null, then it clear the form value
		binder.readBean(value);

		this.commonConfig = value;
	}

}
