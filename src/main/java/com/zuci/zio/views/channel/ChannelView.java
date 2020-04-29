package com.zuci.zio.views.channel;

import org.springframework.beans.factory.annotation.Autowired;
import com.zuci.zio.dao.ChannelConfigDao;
import com.zuci.zio.dto.InstanceGridDTO;
import com.zuci.zio.model.ChannelConfig;
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

@Route(value = "channel", layout = MainView.class)
@PageTitle("Channel")
@CssImport("styles/views/channel/channel-view.css")
@CssImport(value = "./styles/my-grid-styles.css", themeFor = "vaadin-grid")
public class ChannelView extends Div implements AfterNavigationObserver {

	@Autowired
	private static ChannelConfigDao channelConfigDao;

	//Grid Variable
	private Grid<InstanceGridDTO> channel;
	private Grid<ChannelConfig> editChannelConfig;

	//Fields for configuration form
	private TextField instance = new TextField();
	private TextField process = new TextField();
	private TextField variable = new TextField();
	private TextField value = new TextField();
	private Button add = new Button("Add");
	private Button cancel = new Button("Cancel");
	private Button save = new Button("Save");
	
	//Page layout in horizontal
	private HorizontalLayout horizontalLayout;

	//Binder binds the object
	private Binder<ChannelConfig> binder;
	private ChannelConfig channelConfig;

	public ChannelView(ChannelConfigDao channelConfigDao) {

		this.channelConfigDao = channelConfigDao;
		
		setId("channel-view");
		
		instance.setEnabled(false);
		process.setEnabled(false);
		
		//Configure Object
		binder = new Binder<>(ChannelConfig.class);
		binder.bindInstanceFields(this);

		horizontalLayout = new HorizontalLayout();
		
		// Configure Main Page Grid
		configureMainGrid();
	}
	
	//Configure Main Page Grid
	private void configureMainGrid() {
		channel = new Grid<>();
		channel.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		channel.setWidth("90%");
		channel.getStyle().set("font-family", "Lato, sans-serif");
		channel.getStyle().set("margin-left", "50px");
		channel.getStyle().set("border", "none");
		channel.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		channel.setHeightByRows(true);
		channel.addColumn(InstanceGridDTO::getInstance).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Channel</div>"));
		channel.addColumn(InstanceGridDTO::getProcess).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Pipeline</div>"));
		channel.addColumn(InstanceGridDTO::getVariableCount).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Variable Count</div>"));
		channel.addColumn(InstanceGridDTO::getOverriddenCommonCount).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Overridden Common Count</div>"));
		channel.addColumn(InstanceGridDTO::getOverriddenPipelineCount).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Overridden Pipeline Count</div>"));

		channel.asSingleSelect().addValueChangeListener(event -> openDialog(event.getValue()));
		
		channel.setItems(this.channelConfigDao.getGrid());
		
		horizontalLayout.removeAll();
		horizontalLayout.setHeightFull();
		horizontalLayout.setWidthFull();
		
		createMainGridLayout(horizontalLayout);

		add(horizontalLayout);
	}

	//Open Configuration Dialog
	private void openDialog(InstanceGridDTO item) {

		Dialog dialog = new Dialog();
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);
		
		ChannelConfig initializeProcessInstance = new ChannelConfig();
		initializeProcessInstance.setProcess(item.getProcess());
		initializeProcessInstance.setInstance(item.getInstance());
		populateForm(initializeProcessInstance);

		Div content = new Div();
		content.addClassName("my-style");

		HorizontalLayout horizontalLayout = new HorizontalLayout();

		//Configure Configuration Grid
		editChannelConfig = new Grid<>();
		editChannelConfig.setItems(channelConfigDao.findByChannel(item.getInstance()));
		editChannelConfig.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		editChannelConfig.setWidth("90%");
		editChannelConfig.getStyle().set("font-family", "Lato, sans-serif");
		editChannelConfig.getStyle().set("margin-left", "2px");
		editChannelConfig.getStyle().set("border", "none");
		editChannelConfig.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		editChannelConfig.setHeightByRows(true);
		editChannelConfig.addColumn(ChannelConfig::getInstance).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Channel</div>"));
		editChannelConfig.addColumn(ChannelConfig::getProcess).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Pipeline</div>"));
		editChannelConfig.addColumn(ChannelConfig::getVariable).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Variable</div>"));
		editChannelConfig.addColumn(ChannelConfig::getValue).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#f8ca34;color:#4b483f'>Value</div>"));

		editChannelConfig.addComponentColumn(iterateItem -> createTrashIcon(editChannelConfig, iterateItem)).setHeader("");

		editChannelConfig.asSingleSelect().addValueChangeListener(event -> {
			populateForm(event.getValue());
		});

		add.addClickListener(e -> {
			ChannelConfig initializeFormVariable = new ChannelConfig();
			initializeFormVariable.setProcess(item.getProcess());
			initializeFormVariable.setInstance(item.getInstance());
			populateForm(initializeFormVariable);
		});

		createPopupGridLayout(horizontalLayout);
		createEditorLayout(horizontalLayout);

		dialog.add(horizontalLayout);
		dialog.setWidth("1000px");
		dialog.setHeight("500px");

		save.addClickListener(e -> {

			if (this.channelConfig == null) {
				this.channelConfig = new ChannelConfig();
				this.channelConfig.setId(0L);
				this.channelConfig.setActive("Y");
				this.channelConfig.setVersion(0);
				this.channelConfig.setSeedConfig(0);
			}else if(this.channelConfig.getId() == null) {
				this.channelConfig = new ChannelConfig();
				this.channelConfig.setId(0L);
				this.channelConfig.setActive("Y");
				this.channelConfig.setVersion(0);
				this.channelConfig.setSeedConfig(0);
			}
			
			if(variable.getValue() == null || variable.getValue().equals("") || value.getValue() == null || value.getValue().equals("")) {
				Notification.show("Variable or Value should not be empty");
			}else {
				this.channelConfig.setInstance(this.instance.getValue());
				this.channelConfig.setProcess(this.process.getValue());
				this.channelConfig.setVariable(variable.getValue());
				this.channelConfig.setValue(value.getValue());

				ChannelConfig insertData = this.channelConfigDao.insert(this.channelConfig);
				
				if(insertData != null) {
					editChannelConfig.setItems(channelConfigDao.findByChannel(item.getInstance()));
					
					ChannelConfig initializeProcessInstanceData = new ChannelConfig();
					initializeProcessInstanceData.setProcess(item.getProcess());
					initializeProcessInstanceData.setInstance(item.getInstance());
					populateForm(initializeProcessInstanceData);
					
					Notification.show("Saved Successfully!");
				}else {
					Notification.show("Saved Failure!");
				}
			}

		});
		
		cancel.addClickListener(e -> {
			dialog.close();
			configureMainGrid();
		});

		dialog.open();
	}

	//Creat Trash Icon in Grid
	private Icon createTrashIcon(Grid<ChannelConfig> grid, ChannelConfig item) {

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
	private void deleteConfirmDialog(Grid<ChannelConfig> grid, ChannelConfig item) {
		Dialog dialog = new Dialog();

		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);

		Label messageLabel = new Label();

		NativeButton confirmButton = new NativeButton("Confirm", event -> {
			
			messageLabel.setText("Confirmed!");
			
			ChannelConfig insertData = this.channelConfigDao.insertAudit(item);
			
			if(insertData != null) {
				
				if(this.channelConfigDao.deleteById(item.getId())) {
					Notification.show("Deleted Successfully!");
					
					//Remove item from grid
					ListDataProvider<ChannelConfig> dataProvider = (ListDataProvider<ChannelConfig>) grid.getDataProvider();
					dataProvider.getItems().remove(item);
					dataProvider.refreshAll();
				}else {
					Notification.show("Delete Failure!");
				}
				
			}else {
				Notification.show("Delete Failure!");
			}
			
			
			ChannelConfig initializeProcessInstance = new ChannelConfig();
			initializeProcessInstance.setProcess(item.getProcess());
			initializeProcessInstance.setInstance(item.getInstance());
			populateForm(initializeProcessInstance);
			
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

	//Create popup grid layout
	private void createPopupGridLayout(HorizontalLayout horizontalLayout) {

		Div wrapper = new Div();
		wrapper.setId("wrapper");
		wrapper.setWidthFull();

		horizontalLayout.add(wrapper);

		add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		add.getStyle().set("margin-left", "80%");
		add.getStyle().set("background", "#58d2cc");
		add.getStyle().set("border-radius", "6px");
		add.getStyle().set("color", "#4b483f");
		wrapper.add(add);
		wrapper.add(editChannelConfig);
	}

	//Create Form Layout
	private void createEditorLayout(HorizontalLayout horizontalLayout) {
		Div editorDiv = new Div();
		editorDiv.setId("editor-layout");
		FormLayout formLayout = new FormLayout();
		addFormItem(editorDiv, formLayout, instance, "Channel");
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

	//Create main grid layout
	private void createMainGridLayout(HorizontalLayout horizontalLayout) {
		Div wrapper = new Div();
		wrapper.setId("wrapper");
		wrapper.setWidthFull();
		horizontalLayout.add(wrapper);
		wrapper.add(channel);
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

		channel.setItems(this.channelConfigDao.getGrid());
	}

	//Set Form Field Values
	private void populateForm(ChannelConfig value) {

		//if value is null, then it clear the form value
		binder.readBean(value);
		this.channelConfig = value;
	}
}