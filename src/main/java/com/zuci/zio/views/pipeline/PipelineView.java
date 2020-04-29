package com.zuci.zio.views.pipeline;

import org.springframework.beans.factory.annotation.Autowired;
import com.zuci.zio.dao.PipelineConfigDao;
import com.zuci.zio.dto.PipeLineGridDTO;
import com.zuci.zio.model.EditPipelineConfig;
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

@Route(value = "pipeline", layout = MainView.class)
@PageTitle("Pipeline")
@CssImport("styles/views/pipeline/pipeline-view.css")
public class PipelineView extends Div implements AfterNavigationObserver {

	@Autowired
	private static PipelineConfigDao pipelineConfigDao;

	//Grid Variable
	private Grid<PipeLineGridDTO> pipeline;
	private Grid<EditPipelineConfig> editPipelineConfig;

	//Fields for configuration form
	private TextField variable = new TextField();
	private TextField value = new TextField();
	private TextField process = new TextField();
	private Button add = new Button("Add");
	private Button cancel = new Button("Cancel");
	private Button save = new Button("Save");

	//Page layout in horizontal
	private HorizontalLayout horizontalLayout;

	//Binder binds the object
	private Binder<EditPipelineConfig> binder;
	private EditPipelineConfig pipelineConfig;

	//Constructor
	public PipelineView(PipelineConfigDao pipelineConfigDao) {

		try {
			
			this.pipelineConfigDao = pipelineConfigDao;
			
			setId("pipeline-view");
			
			process.setEnabled(false);

			horizontalLayout = new HorizontalLayout();
			
			// Configure Main Page Grid
			configureMainGrid();

			//Configure Object
			binder = new Binder<>(EditPipelineConfig.class);
			binder.bindInstanceFields(this);

		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	//Configure Main Page Grid
	private void configureMainGrid() {
		pipeline = new Grid<>();
		pipeline.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		pipeline.setWidth("90%");
		pipeline.getStyle().set("font-family", "Lato, sans-serif");
		pipeline.getStyle().set("margin-left", "50px");
		pipeline.getStyle().set("border", "none");
		pipeline.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		pipeline.setHeightByRows(true);
		pipeline.addColumn(PipeLineGridDTO::getProcess).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Pipeline</div>"));
		pipeline.addColumn(PipeLineGridDTO::getChannelCount).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Channel Count</div>"));
		pipeline.addColumn(PipeLineGridDTO::getVariableCount).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Variable Count</div>"));
		pipeline.addColumn(PipeLineGridDTO::getOverriddenCount).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Overridden Count</div>"));

		pipeline.asSingleSelect().addValueChangeListener(event -> openDialog(event.getValue().getProcess()));
		
		pipeline.setItems(pipelineConfigDao.getGrid());
		
		horizontalLayout.removeAll();
		horizontalLayout.setHeightFull();
		horizontalLayout.setWidthFull();
		
		createMainGridLayout(horizontalLayout);

		add(horizontalLayout);
	}

	//Open Configuration Dialog
	private void openDialog(String process) {
		
		EditPipelineConfig intialProcessValue = new EditPipelineConfig();
		intialProcessValue.setProcess(process);
		
		populateForm(intialProcessValue);

		Dialog dialog = new Dialog();
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);

		Div content = new Div();
		content.addClassName("my-style");

		//Configure Configuration Grid
		editPipelineConfig = new Grid<>();
		editPipelineConfig.setItems(pipelineConfigDao.findByPipeline(process));
		editPipelineConfig.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		editPipelineConfig.setWidth("90%");
		editPipelineConfig.getStyle().set("font-family", "Lato, sans-serif");
		editPipelineConfig.getStyle().set("margin-left", "20px");
		editPipelineConfig.getStyle().set("border", "none");
		editPipelineConfig.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		editPipelineConfig.setHeightByRows(true);
		editPipelineConfig.addColumn(EditPipelineConfig::getVariable).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Variable</div>"));
		editPipelineConfig.addColumn(EditPipelineConfig::getValue).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Value</div>"));

		editPipelineConfig.addComponentColumn(item -> createTrashIcon(editPipelineConfig, item)).setHeader("");

		editPipelineConfig.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));
		
		add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		add.getStyle().set("margin-left", "90%");
		add.getStyle().set("background", "#58d2cc");
		add.getStyle().set("border-radius", "6px");
		add.getStyle().set("color", "#4b483f");
		add.addClickListener(e -> {
			EditPipelineConfig initialProcessValue = new EditPipelineConfig();
			initialProcessValue.setProcess(process);
			populateForm(initialProcessValue);
		});

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();
		
		createPopupGridLayout(horizontalLayout);

		dialog.add(horizontalLayout);
		dialog.setWidth("1000px");
		dialog.setHeight("500px");
		
		cancel.addClickListener(e -> {
			dialog.close();
			configureMainGrid();
		});

		save.addClickListener(e -> {

			if (this.pipelineConfig == null) {
				this.pipelineConfig.setId(0L);
				this.pipelineConfig.setVersion(0);
				this.pipelineConfig.setActive("Y");
			}else if(this.pipelineConfig.getId() == null) {
				this.pipelineConfig.setId(0L);
				this.pipelineConfig.setVersion(0);
				this.pipelineConfig.setActive("Y");
			}
			
			if(variable.getValue() == null || variable.getValue().equals("") || value.getValue() == null || value.getValue().equals("")) {
				Notification.show("Variable or Value should not be empty");
			}else {
				this.pipelineConfig.setProcess(this.process.getValue());
				this.pipelineConfig.setVariable(variable.getValue());
				this.pipelineConfig.setValue(value.getValue());

				EditPipelineConfig insertData = this.pipelineConfigDao.insert(this.pipelineConfig);
				
				if(insertData != null) {
					editPipelineConfig.setItems(pipelineConfigDao.findByPipeline(process));
					
					EditPipelineConfig initialProcessValue = new EditPipelineConfig();
					initialProcessValue.setProcess(process);
					populateForm(initialProcessValue);
					
					Notification.show("Saved Successfully!");
				}else {
					Notification.show("Save Failure!");
				}
			}
			
		});

		createEditorLayout(horizontalLayout);

		dialog.open();
	}

	//Creat Trash Icon in Grid
	private Icon createTrashIcon(Grid<EditPipelineConfig> grid, EditPipelineConfig item) {

		Icon trashIcon = new Icon(VaadinIcon.TRASH);
		trashIcon.addClickListener(event -> {
			System.out.println(item.getId());
			deleteConfirmDialog(grid, item);
		});
		trashIcon.setColor("#b2b5a6");
		trashIcon.getStyle().set("margin-left", "50%");
		return trashIcon;
	}

	//Confirmation Dialog
	private void deleteConfirmDialog(Grid<EditPipelineConfig> grid, EditPipelineConfig item) {
		
		Dialog dialog = new Dialog();

		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);

		Label messageLabel = new Label();

		NativeButton confirmButton = new NativeButton("Confirm", event -> {
			messageLabel.setText("Confirmed!");
			EditPipelineConfig insertData = this.pipelineConfigDao.insertAudit(item);
			
			if(insertData != null) {
				if(this.pipelineConfigDao.deleteById(item.getId())) {
					//Remove item from grid
					ListDataProvider<EditPipelineConfig> dataProvider = (
							ListDataProvider<EditPipelineConfig>) grid
							.getDataProvider();
					dataProvider.getItems().remove(item);
					dataProvider.refreshAll();
					
					Notification.show("Deleted Successfully!");
				}else {
					Notification.show("Delete Failure!");
				}
			}else {
				Notification.show("Delete Failure!");
			}
			
			EditPipelineConfig intialProcessValue = new EditPipelineConfig();
			intialProcessValue.setProcess(item.getProcess());
			populateForm(intialProcessValue);
			
			dialog.close();
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
			messageLabel.setText("Cancelled...");
			EditPipelineConfig intialProcessValue = new EditPipelineConfig();
			intialProcessValue.setProcess(item.getProcess());
			populateForm(intialProcessValue);
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

	//Create Form Layout
	private void createEditorLayout(HorizontalLayout horizontalLayout) {

		Div editorDiv = new Div();
		editorDiv.setId("editor-layout");

		FormLayout formLayout = new FormLayout();
		addFormItem(editorDiv, formLayout, process, "Pipeline");
		addFormItem(editorDiv, formLayout, variable, "Variable");
		addFormItem(editorDiv, formLayout, value, "Value");

		createButtonLayout(editorDiv);
		horizontalLayout.add(editorDiv);
	}

	//Create Button for Form Layout
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

	//Create Main Grid Layout
	private void createMainGridLayout(HorizontalLayout horizontalLayout) {

		Div wrapper = new Div();
		wrapper.setId("wrapper");
		wrapper.setWidthFull();

		wrapper.add(pipeline);
		horizontalLayout.add(wrapper);
	}

	//Create Popup Grid Layout
	private void createPopupGridLayout(HorizontalLayout horizontalLayout) {

		Div wrapper = new Div();
		wrapper.setId("wrapper");
		wrapper.setWidthFull();

		wrapper.add(add);
		wrapper.add(editPipelineConfig);
		
		horizontalLayout.add(wrapper);
	}

	//Add Field for Form Layout
	private void addFormItem(Div wrapper, FormLayout formLayout, AbstractField field, String fieldName) {

		formLayout.addFormItem(field, fieldName);

		wrapper.add(formLayout);

		field.getElement().getClassList().add("full-width");
	}

	//Calls after the constructor
	@Override
	public void afterNavigation(AfterNavigationEvent event) {

		pipeline.setItems(pipelineConfigDao.getGrid());
	}

	//Set Form Field Values
	private void populateForm(EditPipelineConfig value) {

		//if value is null, then it clear the form value
		binder.readBean(value);

		this.pipelineConfig = value;
	}

}
