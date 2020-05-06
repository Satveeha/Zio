package com.zuci.zio.views.upload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.zuci.zio.dao.ChannelConfigDao;
import com.zuci.zio.dao.CommonConfigDao;
import com.zuci.zio.dao.PipelineConfigDao;
import com.zuci.zio.dto.InstanceGridDTO;
import com.zuci.zio.dto.UploadPageThreeMasterDTO;
import com.zuci.zio.model.ChannelConfig;
import com.zuci.zio.model.ChannelMaster;

public class UploadScreenThree extends AppLayout {

	@Autowired
	private static PipelineConfigDao pipelineConfigDao;

	@Autowired
	private static ChannelConfigDao channelConfigDao;

	@Autowired
	private static CommonConfigDao commonConfigDao;

	private VerticalLayout verticalLayout;

	private String pipelineName = "";
	private String fileName = "";
	private String filePath = "";

	private TextField channelName = new TextField();
	ComboBox<String> alias = new ComboBox<>();
	private TextField variableCount = new TextField();
	private TextField uploadFileName = new TextField();

	private Button run = new Button("Run");
	private Button deploy = new Button("Deploy");

	private Grid<ChannelConfig> variableValueGrid;

	private Button viewLog = new Button("View Log");
	private Button cancel = new Button("Cancel");
	private Button back = new Button("Back");
	private Button finish = new Button("Finish");

	public UploadScreenThree(PipelineConfigDao pipelineConfigDao, ChannelConfigDao channelConfigDao,
			CommonConfigDao commonConfigDao, String pipelineName, String filePath) {

		this.pipelineConfigDao = pipelineConfigDao;
		this.channelConfigDao = channelConfigDao;
		this.commonConfigDao = commonConfigDao;

		this.pipelineName = pipelineName;
		this.filePath = filePath;
		this.fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());

		channelName.setLabel("Name");
		channelName.setEnabled(false);

		alias.setLabel("Alias");

		variableCount.setLabel("Variables");
		variableCount.setEnabled(false);

		uploadFileName.setLabel("Upload File Name");
		uploadFileName.setEnabled(false);
		uploadFileName.setValue(this.fileName);

		verticalLayout = new VerticalLayout();
		verticalLayout.setHeightFull();
		verticalLayout.setWidthFull();

		setChannelConfiguration();
		setRunDeployButton();
		setTabAndGrid();
		setFinalButtons();

		setContent(verticalLayout);
	}

	public void setChannelConfiguration() {

		HorizontalLayout horizontalLayout;

		List<ChannelMaster> channelMasterList = this.channelConfigDao.findMasterByPipeline(pipelineName);

		List<String> aliasList = new ArrayList<String>();

		channelMasterList.forEach(e -> {
			aliasList.add(e.getAlias());
		});

		alias.setItems(aliasList);

		alias.addValueChangeListener(e -> {
			System.out.println(e.getValue());
			setChannelConfiguration(e.getValue());
		});

		horizontalLayout = new HorizontalLayout();
		horizontalLayout.add(channelName);
		horizontalLayout.add(alias);

		verticalLayout.add(horizontalLayout);

		horizontalLayout = new HorizontalLayout();
		horizontalLayout.add(variableCount);
		horizontalLayout.add(uploadFileName);

		verticalLayout.add(horizontalLayout);
	}

	public void setChannelConfiguration(String alias) {

		List<UploadPageThreeMasterDTO> channelVariableList = this.channelConfigDao.groupChannelByAlias(alias);

		if (!channelVariableList.isEmpty()) {
			channelName.setValue(channelVariableList.get(0).getInstance());
			variableCount.setValue(String.valueOf(channelVariableList.get(0).getVariableCount()));
		}

		variableValueGrid
				.setItems(channelConfigDao.findChannelByPipelineAndChannel(pipelineName, channelName.getValue()));
	}

	public void setTabAndGrid() {

		Tab textLog = new Tab("Text Log");
		Tab topLevel = new Tab("Top Level");
		Tab macroLevel = new Tab("Macro Level");
		Tab statementLevel = new Tab("Statement Level");
		Tabs tabs = new Tabs(textLog, topLevel, macroLevel, statementLevel);
		tabs.setFlexGrowForEnclosedTabs(1);
		tabs.getStyle().set("margin-right", "100px");
		tabs.getStyle().set("width", "100%");

		variableValueGrid = new Grid<>();
		variableValueGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		variableValueGrid.setWidth("40%");
		variableValueGrid.getStyle().set("margin-left", "auto");
		variableValueGrid.getStyle().set("font-family", "Lato, sans-serif");
		variableValueGrid.getStyle().set("margin-left", "2px");
		variableValueGrid.getStyle().set("border", "none");
		variableValueGrid.getStyle().set("display", "table-caption");

		variableValueGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		variableValueGrid.setHeightByRows(true);
		Grid.Column<ChannelConfig> variableColumn = variableValueGrid.addColumn(ChannelConfig::getVariable)
				.setHeader(new Html(
						"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#fff;color:#4b483f'>Variable</div>"));
		Grid.Column<ChannelConfig> valueColumn = variableValueGrid.addColumn(ChannelConfig::getValue)
				.setHeader(new Html(
						"<div style='font-weight:bold;font-size:16px;text-orientation: mixed;background:#fff;color:#4b483f'>Value</div>"));

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setWidthFull();
		horizontalLayout.add(tabs);
		horizontalLayout.add(variableValueGrid);

		verticalLayout.add(horizontalLayout);

	}

	public void setRunDeployButton() {
		run.addClassName("add-button");
		deploy.addClassName("delete-button");

//		run.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//		run.getStyle().set("background", "#58d2cc");
//		run.getStyle().set("border-radius", "6px");
//		run.getStyle().set("color", "#4b483f");

//		deploy.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//		deploy.getStyle().set("background", "#58d2cc");
//		deploy.getStyle().set("border-radius", "6px");
//		deploy.getStyle().set("color", "#4b483f");

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setWidthFull();
		horizontalLayout.add(run);
		horizontalLayout.add(deploy);

		verticalLayout.add(horizontalLayout);
	}

	public void setFinalButtons() {

//		viewLog.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//		viewLog.getStyle().set("background", "#58d2cc");
//		viewLog.getStyle().set("border-radius", "6px");
//		viewLog.getStyle().set("color", "#4b483f");
		viewLog.addClassName("add-button");
		viewLog.getStyle().set("margin-left", "auto");

		cancel.addClassName("delete-button");

//		cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

//		back.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//		back.getStyle().set("background", "#58d2cc");
//		back.getStyle().set("border-radius", "6px");
//		back.getStyle().set("color", "#4b483f");
		back.addClassName("add-button");
		finish.addClassName("delete-button");

		back.addClickListener(e -> {
			setContent(new UploadScreenTwo(this.pipelineConfigDao, this.channelConfigDao, this.commonConfigDao,
					pipelineName, filePath));
		});
//		
//		finish.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//		finish.getStyle().set("background", "#58d2cc");
//		finish.getStyle().set("border-radius", "6px");
//		finish.getStyle().set("color", "#4b483f");
		finish.getStyle().set("margin-right", "20px");

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.getStyle().set("margin-top", "auto");
		horizontalLayout.getStyle().set("margin-bottom", "20px");
		horizontalLayout.setWidthFull();
		horizontalLayout.add(viewLog);
		horizontalLayout.add(cancel);
		horizontalLayout.add(back);
		horizontalLayout.add(finish);

		verticalLayout.add(horizontalLayout);
	}

}
