package com.zuci.zio.views.pipelineDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.zuci.zio.commonFuntions.ConfigRead;
import com.zuci.zio.dao.PipelineConfigDao;
import com.zuci.zio.model.EditPipelineConfig;
import com.zuci.zio.model.PipelineConfig;
import com.zuci.zio.views.main.MainView;
import com.zuci.zio.views.pipeline.PipelineView;

@Route(value = "pipelineDefinition", layout = MainView.class)
@PageTitle("Pipeline Definition")
@CssImport("styles/views/pipeline/pipeline-view.css")
public class PipelineDefinitionView extends Div implements AfterNavigationObserver {

	@Autowired
	private static PipelineConfigDao pipelineConfigDao;

	private Grid<PipelineConfig> pipeline;

	private HorizontalLayout horizontalLayout;
	
	static ConfigRead configRead = new ConfigRead();
	private static String downloadPath = configRead.readProperty("downloadPath");

	public PipelineDefinitionView(PipelineConfigDao pipelineConfigDao){

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
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#fff;color:#4b483f'>Pipeline</div>"));
		pipeline.addColumn(PipelineConfig::getPipelineCount).setHeader(new Html(
				"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#fff;color:#4b483f'>Pipeline Count</div>"));

		// when a row is selected or deselected, populate form
		pipeline.asSingleSelect().addValueChangeListener(event -> openDialog(event.getValue().getProcess()));

		horizontalLayout = new HorizontalLayout();
		horizontalLayout.setHeightFull();
		horizontalLayout.setWidthFull();

		createGridLayout(horizontalLayout);

		add(horizontalLayout);
	}

	private void openDialog(String process) {
		
		Dialog dialog = new Dialog();

		Div content = new Div();
		content.addClassName("my-style");

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();
		
		MemoryBuffer memoryBuffer = new MemoryBuffer();

		Upload upload = new Upload(memoryBuffer);
		upload.addFinishedListener(e -> {
		    InputStream inputStream = memoryBuffer.getInputStream();
		    
		    downloadPath = downloadPath.replaceAll("user.home", System.getProperty("user.home"));
		    System.out.println(downloadPath);
		    
		    File uploadFile = new File(downloadPath);

			if (!uploadFile.exists()) {
				uploadFile.mkdirs();
				System.out.println("Make Directory");
			}
			
			try {
				FileUtils.copyInputStreamToFile(inputStream, new File(downloadPath+"Pipeline.java"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		    // read the contents of the buffered memory
		    // from inputStream
		});
		
		TextArea textArea = new TextArea("Description");
		textArea.getStyle().set("minHeight", "100px");
		textArea.getStyle().set("minWidth", "600px");
		textArea.setPlaceholder("Write here ...");
		
		horizontalLayout.add(upload);
		horizontalLayout.add(textArea);

		dialog.add(horizontalLayout);
		dialog.setWidth("1000px");
		dialog.setHeight("500px");

		dialog.open();
	}

	private void createGridLayout(HorizontalLayout horizontalLayout) {

		Div wrapper = new Div();
		wrapper.setId("wrapper");
		wrapper.setWidthFull();

		horizontalLayout.add(wrapper);

		wrapper.add(pipeline);
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {

		// Lazy init of the grid items, happens only when we are sure the view will be
		// shown to the user
		pipeline.setItems(pipelineConfigDao.findAll());
	}

}
