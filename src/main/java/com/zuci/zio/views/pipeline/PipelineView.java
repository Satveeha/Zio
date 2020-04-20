package com.zuci.zio.views.pipeline;

import org.springframework.beans.factory.annotation.Autowired;
import com.zuci.zio.dao.PipelineConfigDao;
import com.zuci.zio.model.EditPipelineConfig;
import com.zuci.zio.model.PipelineConfig;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
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

	private Grid<PipelineConfig> pipeline;
	private Grid<EditPipelineConfig> editPipelineConfig;

	private TextField description = new TextField();
	private TextField variable = new TextField();
	private TextField value = new TextField();
	private TextField shortName = new TextField();
	private TextField process = new TextField();

	private Button add = new Button("Add");

	private Button cancel = new Button("Cancel");
	private Button save = new Button("Save");

	private Button addCancel = new Button("Cancel");
	private Button addSave = new Button("Save");

	private SplitLayout splitLayout;

	private Binder<EditPipelineConfig> binder;

	private EditPipelineConfig pipelineConfig;

	public PipelineView(PipelineConfigDao pipelineConfigDao) {

		this.pipelineConfigDao = pipelineConfigDao;

		setId("pipeline-view");

		// Configure Grid
		pipeline = new Grid<>();
		pipeline.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		pipeline.setWidth("90%");
		pipeline.getStyle().set("font-family", "Lato, sans-serif");
		pipeline.getStyle().set("margin-left", "50px");
		pipeline.getStyle().set("border", "none");
		pipeline.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		pipeline.setHeightByRows(true);
		pipeline.addColumn(PipelineConfig::getProcess).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#002f5d;color:#fff'>Pipeline</div>"));
		pipeline.addColumn(PipelineConfig::getPipelineCount).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#002f5d;color:#fff'>Pipeline Count</div>"));

		// when a row is selected or deselected, populate form
		pipeline.asSingleSelect().addValueChangeListener(event -> openDialog(event.getValue().getProcess()));

		binder = new Binder<>(EditPipelineConfig.class);
		binder.bindInstanceFields(this);

		add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		add.getStyle().set("margin-left", "90%");
		add.getStyle().set("background", "#002f5d");
		add.getStyle().set("border-radius", "6px");
		add.addClickListener(e -> {
			createAddLayout(splitLayout);
		});

		splitLayout = new SplitLayout();
		splitLayout.setSizeFull();

		createGridLayout(splitLayout);

		add(splitLayout);
	}

	private void openDialog(String process) {

		Dialog dialog = new Dialog();

		Div content = new Div();
		content.addClassName("my-style");

		editPipelineConfig = new Grid<>();
		editPipelineConfig.setItems(pipelineConfigDao.findByPipeline(process));
		editPipelineConfig.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		editPipelineConfig.setWidth("90%");
		editPipelineConfig.getStyle().set("font-family", "Lato, sans-serif");
		editPipelineConfig.getStyle().set("margin-left", "20px");
		editPipelineConfig.getStyle().set("border", "none");
		editPipelineConfig.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		editPipelineConfig.setHeightByRows(true);
		editPipelineConfig.addColumn(EditPipelineConfig::getDescription).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#002f5d;color:#fff'>Description</div>"));
		editPipelineConfig.addColumn(EditPipelineConfig::getVariable).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#002f5d;color:#fff'>Variable</div>"));
		editPipelineConfig.addColumn(EditPipelineConfig::getValue).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#002f5d;color:#fff'>Value</div>"));

		editPipelineConfig.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

		SplitLayout splitLayout = new SplitLayout();
		splitLayout.setSizeFull();

		createPopupGridLayout(splitLayout);

		dialog.add(splitLayout);
		dialog.setWidth("800px");
		dialog.setHeight("500px");

		cancel.addClickListener(e -> editPipelineConfig.asSingleSelect().clear());

		save.addClickListener(e -> {

			if (this.pipelineConfig != null) {

				this.pipelineConfig.setProcess(this.process.getValue());
				this.pipelineConfig.setDescription(description.getValue());
				this.pipelineConfig.setVariable(variable.getValue());
				this.pipelineConfig.setValue(value.getValue());
				this.pipelineConfig.setShortName(shortName.getValue());

				PipelineView.pipelineConfigDao.insert(this.pipelineConfig);

				// dialog.close();
				editPipelineConfig.setItems(pipelineConfigDao.findByPipeline(process));
				// pipeline.setItems(pipelineConfigDao.findAll());
			}
		});

		createEditorLayout(splitLayout);

		dialog.open();
	}

	private void createEditorLayout(SplitLayout splitLayout) {

		Div editorDiv = new Div();
		editorDiv.setId("editor-layout");

		FormLayout formLayout = new FormLayout();
		addFormItem(editorDiv, formLayout, process, "Pipeline");
		addFormItem(editorDiv, formLayout, description, "Description");
		addFormItem(editorDiv, formLayout, variable, "Variable");
		addFormItem(editorDiv, formLayout, value, "Value");
		addFormItem(editorDiv, formLayout, shortName, "Short Name");

		createButtonLayout(editorDiv);
		splitLayout.addToSecondary(editorDiv);
	}

	private void createAddButtonLayout(Div editorDiv) {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setId("button-layout");
		buttonLayout.setWidthFull();
		buttonLayout.setSpacing(true);
		addCancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		addSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		addSave.getStyle().set("background", "#002f5d");
		addSave.getStyle().set("border-radius", "6px");
		buttonLayout.add(addCancel, addSave);
		editorDiv.add(buttonLayout);
	}

	private void createAddLayout(SplitLayout splitLayout) {

		Div editorDiv = new Div();
		editorDiv.setId("editor-layout");

		FormLayout formLayout = new FormLayout();

		TextField pipeline = new TextField();
		pipeline.setLabel("Pipeline");

		TextField description = new TextField();
		description.setLabel("Description");

		TextField variable = new TextField();
		variable.setLabel("Variable");

		TextField value = new TextField();
		value.setLabel("Value");

		TextField shortName = new TextField();
		shortName.setLabel("Short Name");

		formLayout.add(pipeline, description, variable, value, shortName);

		editorDiv.add(formLayout);

		createAddButtonLayout(editorDiv);

		addSave.addClickListener(e -> {

			this.pipelineConfig = new EditPipelineConfig();

			if (this.pipelineConfig != null) {

				this.pipelineConfig.setId(0L);
				this.pipelineConfig.setProcess(pipeline.getValue());
				this.pipelineConfig.setDescription(description.getValue());
				this.pipelineConfig.setVariable(variable.getValue());
				this.pipelineConfig.setValue(value.getValue());
				this.pipelineConfig.setShortName(shortName.getValue());
				this.pipelineConfig.setActive("Y");
				this.pipelineConfig.setVersion(0);

				PipelineView.pipelineConfigDao.insert(this.pipelineConfig);

			}

			this.pipeline.setItems(pipelineConfigDao.findAll());
		});

		splitLayout.addToSecondary(editorDiv);
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

	private void createGridLayout(SplitLayout splitLayout) {

		Div wrapper = new Div();
		wrapper.setId("wrapper");
		wrapper.setWidthFull();

		splitLayout.addToPrimary(wrapper);

		wrapper.add(add);
		wrapper.add(pipeline);
	}

	private void createPopupGridLayout(SplitLayout splitLayout) {

		Div wrapper = new Div();
		wrapper.setId("wrapper");
		wrapper.setWidthFull();

		splitLayout.addToPrimary(wrapper);

		wrapper.add(editPipelineConfig);
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
		pipeline.setItems(pipelineConfigDao.findAll());
	}

	private void populateForm(EditPipelineConfig value) {

		// Value can be null as well, that clears the form
		binder.readBean(value);

		this.pipelineConfig = value;
	}

}
