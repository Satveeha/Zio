package com.zuci.zio.views.commonconfigview;

import org.springframework.beans.factory.annotation.Autowired;
import com.zuci.zio.dao.CommonConfigDao;
import com.zuci.zio.model.CommonConfig;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
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

@Route(value = "commons", layout = MainView.class)
@PageTitle("Commons")
@CssImport("styles/views/commonconfigview/commons-run-console-run-console-view.css")
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
    
    private SplitLayout splitLayout;
    
    private CommonConfig commonConfig;

    public CommonConfigView(CommonConfigDao commonConfigDao) {
    	
    	this.commonConfigDao = commonConfigDao;
        setId("commons-run-console-run-console-view");
        
        // Configure Grid
        commons = new Grid<>();
        commons.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        commons.setHeightFull();
        commons.addColumn(CommonConfig::getVariable).setHeader("Variable");
        commons.addColumn(CommonConfig::getValue).setHeader("Value");

        //when a row is selected or deselected, populate form
        commons.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        // Configure Form
        binder = new Binder<>(CommonConfig.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        // the grid valueChangeEvent will clear the form too
        cancel.addClickListener(e -> commons.asSingleSelect().clear());

        save.addClickListener(e -> {
            
        	//Notification.show("Not implemented");
        	if(this.commonConfig == null) {
        		this.commonConfig = new CommonConfig();
        		this.commonConfig.setId(0L);
        		this.commonConfig.setActive("Y");
        		this.commonConfig.setVersion(0);
        	}
        	
            String updatedVariable = variable.getValue();
            String updatedValue = value.getValue();
            
            this.commonConfig.setVariable(updatedVariable);
            this.commonConfig.setValue(updatedValue);
            
            this.commonConfigDao.insert( this.commonConfig);
            
            commons.setItems(commonConfigDao.findAll());
        });
        
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add.getStyle().set("margin-left", "90%");
        add.addClickListener(e -> {
        	populateForm(new CommonConfig());
        	commons.asSingleSelect().clear();});

        splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);

        add(splitLayout);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        
    	Div editorDiv = new Div();
        editorDiv.setId("editor-layout");
        
        FormLayout formLayout = new FormLayout();
        
        addFormItem(editorDiv, formLayout, variable, "Variable");
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
    	createEditorLayout(splitLayout);
       
    	binder.readBean(value);
        
    	this.commonConfig = value;
    }
    
}
