package com.zuci.zio.views.commonconfigview;

import org.springframework.beans.factory.annotation.Autowired;
import com.zuci.zio.dao.CommonConfigDao;
import com.zuci.zio.model.CommonConfig;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.model.Label;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.zuci.zio.views.main.MainView;

@Route(value = "commons", layout = MainView.class)
@PageTitle("Commons")
//@CssImport("styles/views/commonconfigview/commons-run-console-run-console-view.css")
@CssImport(value = "./styles/my-grid-styles.css", themeFor = "vaadin-grid")
public class CommonConfigView extends Div implements AfterNavigationObserver {

	@Autowired
	private static CommonConfigDao commonConfigDao;

	private Grid<CommonConfig> commons;

	private TextField variable = new TextField();
	private TextField value = new TextField();

	private Button cancel = new Button("Cancel");
	private Button save = new Button("Save");
	private Button add = new Button("Add");

	private Binder<CommonConfig> binder;

	// private SplitLayout splitLayout;

	private CommonConfig commonConfig;

	private HorizontalLayout horizontalLayout;

	public CommonConfigView(CommonConfigDao commonConfigDao) {

		this.commonConfigDao = commonConfigDao;
		setId("commons-run-console-run-console-view");

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
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#002f5d;color:#fff'>Variable</div>"));
		commons.addColumn(CommonConfig::getValue).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#002f5d;color:#fff'>Value</div>"));

		commons.addComponentColumn(item -> createTrashIcon(commons, item))
        .setHeader("");
		
		// when a row is selected or deselected, populate form
		commons.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

		// Configure Form
		binder = new Binder<>(CommonConfig.class);

		// Bind fields. This where you'd define e.g. validation rules
		binder.bindInstanceFields(this);

		// the grid valueChangeEvent will clear the form too
		cancel.addClickListener(e -> commons.asSingleSelect().clear());

		save.addClickListener(e -> {

			// Notification.show("Not implemented");
			if (this.commonConfig == null) {
				this.commonConfig = new CommonConfig();
				this.commonConfig.setId(0L);
				this.commonConfig.setActive("Y");
				this.commonConfig.setVersion(0);
			}

			String updatedVariable = variable.getValue();
			String updatedValue = value.getValue();

			this.commonConfig.setVariable(updatedVariable);
			this.commonConfig.setValue(updatedValue);

			this.commonConfigDao.insert(this.commonConfig);

			commons.setItems(commonConfigDao.findAll());
		});

		add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		add.getStyle().set("margin-left", "90%");
		add.getStyle().set("background", "#002f5d");
		add.getStyle().set("border-radius", "6px");
		add.addClickListener(e -> {
			populateForm(new CommonConfig());
			commons.asSingleSelect().clear();
		});

		// splitLayout = new SplitLayout();
		// splitLayout.setSizeFull();

		horizontalLayout = new HorizontalLayout();
		horizontalLayout.setHeightFull();
		horizontalLayout.setWidthFull();

		createGridLayout(horizontalLayout);
		createEditorLayout(horizontalLayout);

		add(horizontalLayout);
	}
	
	private Icon createTrashIcon(Grid<CommonConfig> grid, CommonConfig item) {

	    Icon trashIcon = new Icon(VaadinIcon.TRASH);
		trashIcon.addClickListener(
		        event -> {
		        	System.out.println(item.getId());
		        	deleteConfirmDialog(item.getId(), grid, item);
		        });
		
		return trashIcon;
	}
	
	private void deleteConfirmDialog(Long id, Grid<CommonConfig> grid, CommonConfig item) {
		Dialog dialog = new Dialog();

		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);

		Label messageLabel = new Label();

		NativeButton confirmButton = new NativeButton("Confirm", event -> {
		    messageLabel.setText("Confirmed!");
		    this.commonConfigDao.deleteById(id);
		    populateForm(new CommonConfig());
		    dialog.close();
		    
		    ListDataProvider<CommonConfig> dataProvider = (ListDataProvider<CommonConfig>) grid
 	                .getDataProvider();
 	        dataProvider.getItems().remove(item);
 	        dataProvider.refreshAll();
		});
		NativeButton cancelButton = new NativeButton("Cancel", event -> {
		    messageLabel.setText("Cancelled...");
		    dialog.close();
		});
		dialog.add(confirmButton, cancelButton);
		
		dialog.open();
	}

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

	private void createButtonLayout(Div editorDiv) {

		HorizontalLayout buttonLayout = new HorizontalLayout();

		buttonLayout.setId("button-layout");
		buttonLayout.setWidthFull();
		buttonLayout.setSpacing(true);

		cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		save.getStyle().set("background", "#002f5d");
		save.getStyle().set("border-radius", "6px");

		buttonLayout.add(cancel, save);

		editorDiv.add(buttonLayout);
	}

	private void createGridLayout(HorizontalLayout horizontalLayout) {

		Div wrapper = new Div();

		wrapper.setId("wrapper");
		wrapper.setWidthFull();
		horizontalLayout.add(wrapper);
		wrapper.add(add);
		wrapper.add(commons);
	}

	private void addFormItem(Div wrapper, FormLayout formLayout, AbstractField field, String fieldName) {

		formLayout.addFormItem(field, fieldName);

		wrapper.add(formLayout);

		field.getElement().getClassList().add("full-width");
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {

		// Lazy init of the grid items, happens only when we are sure the view will be
		// shown to the user
		commons.setItems(commonConfigDao.findAll());
	}

	private void populateForm(CommonConfig value) {

		// Value can be null as well, that clears the form
		// createEditorLayout(horizontalLayout);

		binder.readBean(value);

		this.commonConfig = value;
	}

}
