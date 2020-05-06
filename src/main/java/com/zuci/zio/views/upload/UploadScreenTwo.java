package com.zuci.zio.views.upload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dnd.GridDragEndEvent;
import com.vaadin.flow.component.grid.dnd.GridDragStartEvent;
import com.vaadin.flow.component.grid.dnd.GridDropEvent;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.zuci.zio.dao.ChannelConfigDao;
import com.zuci.zio.dao.CommonConfigDao;
import com.zuci.zio.dao.PipelineConfigDao;
import com.zuci.zio.model.ChannelConfig;
import com.zuci.zio.model.ChannelMaster;
import com.zuci.zio.model.EditPipelineConfig;
import com.zuci.zio.views.main.MainView;

@Route(value = "upload-screen-two", layout = MainView.class)
@PageTitle("Upload")
@CssImport(value = "./styles/my-grid-styles.css", themeFor = "vaadin-grid")
@CssImport(value = "./styles/upload.css")
@CssImport(value = "./styles/upload.css", themeFor = "vaadin-button")
@CssImport(value = "./styles/tabs.css", themeFor = "vaadin-tabs")
@CssImport(value = "./styles/text-area.css", themeFor = "vaadin-text-area")
@CssImport(value = "./styles/text-field.css", themeFor = "vaadin-text-field")
public class UploadScreenTwo extends AppLayout {

	@Autowired
	private static PipelineConfigDao pipelineConfigDao;

	@Autowired
	private static ChannelConfigDao channelConfigDao;

	@Autowired
	private static CommonConfigDao commonConfigDao;

	private Grid<ChannelConfig> editChannelConfig;

	// Fields for configuration form
	private TextField instance = new TextField();
	private TextField description = new TextField();
	private TextField alias = new TextField();
	private Button add = new Button("Add");
	private Button cancel = new Button("Cancel");
	private Button save = new Button("Save");

	private VerticalLayout verticalLayout;

	private String pipelineName;

	private String filePath;

	// Binder binds the object
	private Binder<ChannelConfig> binder;

	private ChannelConfig channelConfig;

	private List<EditPipelineConfig> draggedItems;

	private Grid<EditPipelineConfig> dragSource;

	private Grid<EditPipelineConfig> grid = new Grid<>(EditPipelineConfig.class);
	private Grid<EditPipelineConfig> grid2 = new Grid<>(EditPipelineConfig.class);

	private List<String> currentlyAddedVariables = new ArrayList<String>();
	private HashMap<String, String> varaiableValue = new HashMap<String, String>();

	private Button next = new Button("Next", new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT));
	private Button prev = new Button("Prev", new Icon(VaadinIcon.ANGLE_DOUBLE_LEFT));

	public UploadScreenTwo(PipelineConfigDao pipelineConfigDao, ChannelConfigDao channelConfigDao,
			CommonConfigDao commonConfigDao, String pipelineName, String filePath) {
		this.pipelineConfigDao = pipelineConfigDao;
		this.channelConfigDao = channelConfigDao;
		this.commonConfigDao = commonConfigDao;

//		add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	add.getStyle().set("margin-left", "auto");
//		add.getStyle().set("background", "#58d2cc");
//		add.getStyle().set("border-radius", "6px");
//		add.getStyle().set("color", "#4b483f");
		add.addClassName("add-button");

		// Configure Object
		binder = new Binder<>(ChannelConfig.class);
		binder.bindInstanceFields(this);

		verticalLayout = new VerticalLayout();
		verticalLayout.setHeightFull();
		verticalLayout.setWidthFull();

		this.pipelineName = pipelineName;

		this.filePath = filePath;

		next.setIconAfterText(true);
//		next.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		next.getStyle().set("margin-left", "auto");
		next.addClassName("add-button");

		next.addClickListener(e -> {
			setContent(new UploadScreenThree(this.pipelineConfigDao, this.channelConfigDao, this.commonConfigDao,
					this.pipelineName, filePath));
		});

		prev.setIconAfterText(true);
//		prev.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		prev.getStyle().set("margin-left", "auto");
		prev.addClassName("delete-button");

		prev.addClickListener(e -> {
			try {
				setContent(new UploadScreenOne(this.pipelineConfigDao, this.commonConfigDao, this.channelConfigDao,
						this.filePath));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		setChannelGrid();

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.getStyle().set("margin-left", "auto");
		horizontalLayout.getStyle().set("margin-bottom", "20px");

		// horizontalLayout.setWidthFull();
		horizontalLayout.add(prev);
		horizontalLayout.add(next);
		verticalLayout.add(horizontalLayout);

		setContent(verticalLayout);
	}

	public void setChannelGrid() {

		createEditorLayout();

		editChannelConfig = new Grid<>();
		editChannelConfig.setItems(channelConfigDao.findByPipeline(this.pipelineName));
		editChannelConfig.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		editChannelConfig.setWidth("100%");
		editChannelConfig.getStyle().set("font-family", "Lato, sans-serif");
		editChannelConfig.getStyle().set("margin-left", "2px");
		editChannelConfig.getStyle().set("border", "none");
		editChannelConfig.getStyle().set("display", "table-caption");

		editChannelConfig.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		editChannelConfig.setHeightByRows(true);
		Grid.Column<ChannelConfig> instanceColumn = editChannelConfig.addColumn(ChannelConfig::getInstance)
				.setHeader(new Html(
						"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#fff;color:#4b483f'>Channel</div>"));
		Grid.Column<ChannelConfig> variableColumn = editChannelConfig.addColumn(ChannelConfig::getVariable)
				.setHeader(new Html(
						"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#fff;color:#4b483f'>Variable</div>"));
		Grid.Column<ChannelConfig> valueColumn = editChannelConfig.addColumn(ChannelConfig::getValue)
				.setHeader(new Html(
						"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#fff;color:#4b483f'>Value</div>"));

		// editChannelConfig.addComponentColumn(iterateItem ->
		// createTrashIcon(editChannelConfig, iterateItem)).setHeader("");

		editChannelConfig.asSingleSelect().addValueChangeListener(event -> {
			// populateForm(event.getValue());
		});

		add.addClickListener(e -> {
			ChannelConfig initializeFormVariable = new ChannelConfig();
			populateForm(initializeFormVariable);
		});

		save.addClickListener(e -> {

			System.out.println(varaiableValue);

			List<ChannelConfig> channelConfigList = new ArrayList<ChannelConfig>();

			List<ChannelMaster> masterInstance = this.channelConfigDao.findByPipelineAndChannel(this.pipelineName,
					this.instance.getValue());

			if (masterInstance == null || masterInstance.isEmpty()) {
				ChannelMaster newInstance = new ChannelMaster();
				newInstance.setId(0L);
				newInstance.setProcess(this.pipelineName);
				newInstance.setInstance(this.instance.getValue());
				newInstance.setDescription(description.getValue());
				newInstance.setAlias(alias.getValue());
				newInstance.setShortName("");

				ChannelMaster insertedInstance = this.channelConfigDao.insertInstance(newInstance);

				if (insertedInstance != null) {
					/*
					 * if (this.channelConfig == null) { this.channelConfig = new ChannelConfig();
					 * this.channelConfig.setId(0L); this.channelConfig.setActive("Y");
					 * this.channelConfig.setVersion(0); this.channelConfig.setSeedConfig(0); }else
					 * if(this.channelConfig.getId() == null) { this.channelConfig = new
					 * ChannelConfig(); this.channelConfig.setId(0L);
					 * this.channelConfig.setActive("Y"); this.channelConfig.setVersion(0);
					 * this.channelConfig.setSeedConfig(0); }
					 */

					if (description.getValue() == null || description.getValue().equals("") || alias.getValue() == null
							|| alias.getValue().equals("") || this.instance.getValue() == null
							|| this.instance.getValue().equals("")) {
						Notification.show("Channel, Description or Alias should not be empty");
					} else {
						this.channelConfig.setInstance(this.instance.getValue());
						this.channelConfig.setProcess(this.pipelineName);

						varaiableValue.forEach((variable, value) -> {

							this.channelConfig = new ChannelConfig();
							this.channelConfig.setId(0L);
							this.channelConfig.setActive("Y");
							this.channelConfig.setVersion(0);
							this.channelConfig.setSeedConfig(0);

							this.channelConfig.setVariable(variable);
							this.channelConfig.setValue(value);

							channelConfigList.add(this.channelConfig);
						});

						List<ChannelConfig> insertData = this.channelConfigDao.insertBatch(channelConfigList);

						// ChannelConfig insertData = this.channelConfigDao.insert(this.channelConfig);

						if (insertData != null) {
							editChannelConfig.setItems(channelConfigDao.findByPipeline(this.pipelineName));

							ChannelConfig initializeProcessInstanceData = new ChannelConfig();
							populateForm(initializeProcessInstanceData);

							Notification.show("Saved Successfully!");
						} else {
							Notification.show("Saved Failure!");
						}

					}
				} else {
					Notification.show("Saved Failure!");
				}
			} else {

				if (description.getValue() == null || description.getValue().equals("") || alias.getValue() == null
						|| alias.getValue().equals("") || this.instance.getValue() == null
						|| this.instance.getValue().equals("")) {
					Notification.show("Channel, Description or Alias should not be empty");
				} else {
					varaiableValue.forEach((variable, value) -> {

						this.channelConfig = new ChannelConfig();
						this.channelConfig.setId(0L);
						this.channelConfig.setActive("Y");
						this.channelConfig.setVersion(0);
						this.channelConfig.setSeedConfig(0);

						this.channelConfig.setInstance(this.instance.getValue());
						this.channelConfig.setProcess(this.pipelineName);

						this.channelConfig.setVariable(variable);
						this.channelConfig.setValue(value);

						channelConfigList.add(this.channelConfig);
					});

					List<ChannelConfig> insertData = this.channelConfigDao.insertBatch(channelConfigList);

					if (insertData != null) {
						editChannelConfig.setItems(channelConfigDao.findByPipeline(this.pipelineName));

						ChannelConfig initializeProcessInstanceData = new ChannelConfig();
						populateForm(initializeProcessInstanceData);

						Notification.show("Saved Successfully!");
					} else {
						Notification.show("Saved Failure!");
					}
				}
			}

		});

		verticalLayout.add(add);

		verticalLayout.add(editChannelConfig);

		setDragAndDropGrid();

	}

	void setDragAndDropGrid() {

		ComponentEventListener<GridDragStartEvent<EditPipelineConfig>> dragStartListener = event -> {
			draggedItems = event.getDraggedItems();
			dragSource = event.getSource();
			grid.setDropMode(GridDropMode.BETWEEN);
			grid.getStyle().set("display", "table-caption");

			grid2.setDropMode(GridDropMode.BETWEEN);
			grid2.getStyle().set("display", "table-caption");

		};

		ComponentEventListener<GridDragEndEvent<EditPipelineConfig>> dragEndListener = event -> {
			draggedItems = null;
			dragSource = null;
			grid.setDropMode(null);
			grid2.setDropMode(null);
		};

		ComponentEventListener<GridDropEvent<EditPipelineConfig>> dropListener = event -> {
			currentlyAddedVariables.clear();

			Optional<EditPipelineConfig> target = event.getDropTargetItem();
			if (target.isPresent() && draggedItems.contains(target.get())) {
				return;
			}

			// Remove the items from the source grid
			@SuppressWarnings("unchecked")
			ListDataProvider<EditPipelineConfig> sourceDataProvider = (ListDataProvider<EditPipelineConfig>) dragSource
					.getDataProvider();
			List<EditPipelineConfig> sourceItems = new ArrayList<>(sourceDataProvider.getItems());
			sourceItems.removeAll(draggedItems);
			dragSource.setItems(sourceItems);

			// Add dragged items to the target Grid
			Grid<EditPipelineConfig> targetGrid = event.getSource();
			targetGrid.getStyle().set("display", "table-caption");

			@SuppressWarnings("unchecked")
			ListDataProvider<EditPipelineConfig> targetDataProvider = (ListDataProvider<EditPipelineConfig>) targetGrid
					.getDataProvider();
			List<EditPipelineConfig> targetItems = new ArrayList<>(targetDataProvider.getItems());

			int index = target.map(
					person -> targetItems.indexOf(person) + (event.getDropLocation() == GridDropLocation.BELOW ? 1 : 0))
					.orElse(0);
			targetItems.addAll(index, draggedItems);
			targetGrid.setItems(targetItems);

			for (EditPipelineConfig e : targetItems) {
				currentlyAddedVariables.add(e.getVariable());
				varaiableValue.put(e.getVariable(), e.getValue());
			}
		};

		grid.setItems(pipelineConfigDao.findByPipeline(this.pipelineName));
		grid.setSelectionMode(Grid.SelectionMode.MULTI);
		grid.addDropListener(dropListener);
		grid.addDragStartListener(dragStartListener);
		grid.addDragEndListener(dragEndListener);
		grid.setRowsDraggable(true);
		grid.setHeightByRows(true);

		grid.setColumns("variable", "value");

		grid.addDropListener(e -> {
			System.out.println("grid 1");

			for (String removeVariable : currentlyAddedVariables) {
				varaiableValue.remove(removeVariable);
			}

		});

		// grid2.setItems(pipelineConfigDao.findByPipeline(pipelineName));
		grid2.setSelectionMode(Grid.SelectionMode.MULTI);
		grid2.addDropListener(dropListener);
		grid2.addDragStartListener(dragStartListener);
		grid2.addDragEndListener(dragEndListener);
		grid2.setRowsDraggable(true);
		grid2.setHeightByRows(true);
		grid2.setColumns("variable", "value");

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setWidthFull();
		horizontalLayout.add(grid);
		horizontalLayout.add(grid2);
		verticalLayout.add(horizontalLayout);

		// verticalLayout.add(grid);
		// verticalLayout.add(grid2);

	}

	// Create Form Layout
	private void createEditorLayout() {
		Div editorDiv = new Div();
		editorDiv.setId("editor-layout");
		FormLayout formLayout = new FormLayout();
		addFormItem(editorDiv, formLayout, instance, "Channel");
		addFormItem(editorDiv, formLayout, description, "Description");
		addFormItem(editorDiv, formLayout, alias, "Alias");
		createButtonLayout(editorDiv);
		verticalLayout.add(editorDiv);
	}

	// Create Button for Form Layout
	private void createButtonLayout(Div editorDiv) {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setId("button-layout");
		buttonLayout.setWidthFull();
		buttonLayout.setSpacing(true);
		cancel.addClassName("delete-button");
		save.addClassName("add-button");

//		cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//		save.getStyle().set("background", "#58d2cc");
//		save.getStyle().set("border-radius", "6px");
//		save.getStyle().set("color", "#4b483f");

		buttonLayout.add(cancel, save);
		editorDiv.add(buttonLayout);
	}

	// Add Field for Form Layout
	private void addFormItem(Div wrapper, FormLayout formLayout, AbstractField field, String fieldName) {
		formLayout.addFormItem(field, fieldName);
		wrapper.add(formLayout);
		field.getElement().getClassList().add("full-width");
	}

	// Creat Trash Icon in Grid
	private Icon createTrashIcon(Grid<ChannelConfig> grid, ChannelConfig item) {

		Icon trashIcon = new Icon(VaadinIcon.TRASH);
		trashIcon.addClickListener(event -> {
			System.out.println(item.getId());
			// deleteConfirmDialog(grid, item);
		});
		trashIcon.setColor("#b2b5a6");
		trashIcon.getStyle().set("margin-left", "50%");
		return trashIcon;
	}

	// Set Form Field Values
	private void populateForm(ChannelConfig value) {

		// if value is null, then it clear the form value
		binder.readBean(value);
		this.channelConfig = value;
	}
}
