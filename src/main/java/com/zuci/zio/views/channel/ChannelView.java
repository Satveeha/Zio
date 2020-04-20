package com.zuci.zio.views.channel;

import org.springframework.beans.factory.annotation.Autowired;
import com.zuci.zio.backend.BackendService;
import com.zuci.zio.backend.Employee;
import com.zuci.zio.dao.ChannelConfigDao;
import com.zuci.zio.dao.CommonConfigDao;
import com.zuci.zio.model.ChannelConfig;
import com.zuci.zio.model.EditPipelineConfig;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.zuci.zio.views.main.MainView;
import com.zuci.zio.views.pipeline.PipelineView;

@Route(value = "channel", layout = MainView.class)
@PageTitle("Channel")
@CssImport("styles/views/channel/channel-view.css")
public class ChannelView extends Div implements AfterNavigationObserver {
    
	@Autowired
    private static ChannelConfigDao channelConfigDao;

    private Grid<ChannelConfig> channel;
    private Grid<ChannelConfig> editChannelConfig;

    private TextField instance = new TextField();
    private TextField process = new TextField();
    private TextField variable = new TextField();
    private TextField alias = new TextField();
    private TextField value = new TextField();
    
    private Button add = new Button("Add");

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<ChannelConfig> binder;
    
    private ChannelConfig channelConfig;

    public ChannelView(ChannelConfigDao channelConfigDao) {
    	
    	this.channelConfigDao = channelConfigDao;
        setId("channel-view");
        // Configure Grid
        channel = new Grid<>();
        channel.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        channel.setHeightFull();
        channel.addColumn(ChannelConfig::getInstance).setHeader("Channel");
        channel.addColumn(ChannelConfig::getProcess).setHeader("Pipeline");
        channel.addColumn(ChannelConfig::getAlias).setHeader("Alias");

        //when a row is selected or deselected, populate form
        channel.asSingleSelect().addValueChangeListener(event -> openDialog(event.getValue().getInstance()));

        // Configure Form
        binder = new Binder<>(ChannelConfig.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);
        // note that password field isn't bound since that property doesn't exist in
        // Employee

        // the grid valueChangeEvent will clear the form too
        cancel.addClickListener(e -> channel.asSingleSelect().clear());

        save.addClickListener(e -> {
            Notification.show("Not implemented");
        });

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        //createEditorLayout(splitLayout);

        add(splitLayout);
    }
    
    private void openDialog(String instance) {
    	
    	Dialog dialog = new Dialog();
    	
    	Div content = new Div();
    	content.addClassName("my-style");
    	
    	SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
    	
    	editChannelConfig = new Grid<>();
    	editChannelConfig.setItems(channelConfigDao.findByChannel(instance));
    	editChannelConfig.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    	editChannelConfig.setHeightFull();
    	editChannelConfig.addColumn(ChannelConfig::getInstance).setHeader("Channel");
    	editChannelConfig.addColumn(ChannelConfig::getProcess).setHeader("Pipeline");
    	editChannelConfig.addColumn(ChannelConfig::getVariable).setHeader("Variable");
    	editChannelConfig.addColumn(ChannelConfig::getAlias).setHeader("Alias");
    	editChannelConfig.addColumn(ChannelConfig::getValue).setHeader("Value");
    	
    	editChannelConfig.asSingleSelect().addValueChangeListener(event -> {
    		populateForm(event.getValue());
    		createEditorLayout(splitLayout);
    	});
    	
    	add.addClickListener(e -> {
        	createEditorLayout(splitLayout);
        	editChannelConfig.asSingleSelect().clear();
        });
    	
        createPopupGridLayout(splitLayout);

    	dialog.add(splitLayout);
    	dialog.setWidth("800px");
    	dialog.setHeight("500px");
    	
    	//cancel.addClickListener(e -> editChannelConfig.asSingleSelect().clear());

        save.addClickListener(e -> {
        	
        	if(this.channelConfig == null) {
        		this.channelConfig = new ChannelConfig();
        		this.channelConfig.setId(0L);
        		this.channelConfig.setActive("Y");
        		this.channelConfig.setVersion(0);
        		this.channelConfig.setSeedConfig(0);
        	}
        	
        	this.channelConfig.setInstance(this.instance.getValue());
    		this.channelConfig.setProcess(this.process.getValue());
    		this.channelConfig.setVariable(variable.getValue());
    		this.channelConfig.setValue(value.getValue());
    		this.channelConfig.setAlias(alias.getValue());
    		
    		this.channelConfigDao.insert(this.channelConfig);
    		
    		//dialog.close();
    		editChannelConfig.setItems(channelConfigDao.findByChannel(instance));
    		//channel.setItems(this.channelConfigDao.findAll());
        });
    	
    	dialog.open();
    }
    
    private void createPopupGridLayout(SplitLayout splitLayout) {
        
    	Div wrapper = new Div();
        wrapper.setId("wrapper");
        wrapper.setWidthFull();
        
        splitLayout.addToPrimary(wrapper);
        
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add.getStyle().set("margin-left", "80%");
        
        wrapper.add(add);
        wrapper.add(editChannelConfig);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorDiv = new Div();
        editorDiv.setId("editor-layout");
        FormLayout formLayout = new FormLayout();
        addFormItem(editorDiv, formLayout, instance, "Channel");
        addFormItem(editorDiv, formLayout, process, "Pipeline");
        addFormItem(editorDiv, formLayout, variable, "Variable");
        addFormItem(editorDiv, formLayout, alias, "Alias");
        addFormItem(editorDiv, formLayout, value, "Value");
        createButtonLayout(editorDiv);
        splitLayout.addToSecondary(editorDiv);
    }

    private void createButtonLayout(Div editorDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(cancel, save);
        editorDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(channel);
    }

    private void addFormItem(Div wrapper, FormLayout formLayout,
            AbstractField field, String fieldName) {
        formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        channel.setItems(this.channelConfigDao.findAll());
    }

    private void populateForm(ChannelConfig value) {
        
    	// Value can be null as well, that clears the form
        binder.readBean(value);
        this.channelConfig = value;
    }
}
