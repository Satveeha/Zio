package com.zuci.zio.views.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.zuci.zio.commonFuntions.ConfigRead;
import com.zuci.zio.commonFuntions.ZipFileHandling;
import com.zuci.zio.dao.ChannelConfigDao;
import com.zuci.zio.dao.CommonConfigDao;
import com.zuci.zio.dao.PipelineConfigDao;
import com.zuci.zio.model.CommonConfig;
import com.zuci.zio.model.EditPipelineConfig;
import com.zuci.zio.model.PipelineMaster;
import com.zuci.zio.views.main.MainView;

@Route(value = "upload-screen-one", layout = MainView.class)
@PageTitle("Upload")
@CssImport(value = "./styles/my-grid-styles.css", themeFor = "vaadin-grid")
@CssImport(value = "./styles/upload.css")
@CssImport(value = "./styles/upload.css", themeFor = "vaadin-button")
@CssImport(value = "./styles/tabs.css", themeFor = "vaadin-tabs")
@CssImport(value = "./styles/text-area.css", themeFor = "vaadin-text-area")
@CssImport(value = "./styles/text-field.css", themeFor = "vaadin-text-field")
public class UploadScreenOne extends AppLayout {

	@Autowired
	private static PipelineConfigDao pipelineConfigDao;
	
	@Autowired
	private static CommonConfigDao commonConfigDao;
	
	@Autowired
	private static ChannelConfigDao channelConfigDao;

	private VerticalLayout verticalLayout;

	static ConfigRead configRead = new ConfigRead();
	private static String downloadPath = configRead.readProperty("downloadPath");

	private String fileName = "";
	
	private String pipelineName = "";

	private String extractFolderPath = "";
	
	private EditPipelineConfig pipelineConfig;
	
	private JSONObject routerData = new JSONObject();

	private Button next = new Button("Next", new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT));
	
	public UploadScreenOne(PipelineConfigDao pipelineConfigDao, CommonConfigDao commonConfigDao, ChannelConfigDao channelConfigDao, String filePath) throws IOException {

		this.pipelineConfigDao = pipelineConfigDao;
		this.commonConfigDao = commonConfigDao;
		this.channelConfigDao = channelConfigDao;
		
		extractFolderPath = filePath;
		
		next.setIconAfterText(true);
//		next.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		next.getStyle().set("margin-left", "auto");
		next.getStyle().set("margin-bottom", "20px");
		next.addClassName("delete-button");
		
		next.addClickListener(e -> {
			setContent(new UploadScreenTwo(this.pipelineConfigDao, this.channelConfigDao, this.commonConfigDao, pipelineName, extractFolderPath));
			//setContent(new UploadScreenTwo());
			//getUI().get().navigate(UploadScreenTwo.class,pipelineName+"."+fileNameWithoutExtension);
		});
		
		verticalLayout = new VerticalLayout();
		verticalLayout.setHeightFull();
		verticalLayout.setWidthFull();
		
		setUploadComponent();

		setContent(verticalLayout);
	}

	public void setUploadComponent() throws IOException {

		MemoryBuffer memoryBuffer = new MemoryBuffer();

		Upload upload = new Upload(memoryBuffer);
		upload.addClassName("upload-layout");
		// upload.setMaxFileSize(1024*8388608);

		upload.addFinishedListener(e -> {

			fileName = memoryBuffer.getFileName();
			InputStream inputStream = memoryBuffer.getInputStream();

			downloadPath = downloadPath.replaceAll("user.home", System.getProperty("user.home"));
			System.out.println(downloadPath);

			File uploadFile = new File(downloadPath);

			if (!uploadFile.exists()) {
				uploadFile.mkdirs();
				System.out.println("Make Directory");
			}

			try {
				FileUtils.copyInputStreamToFile(inputStream, new File(downloadPath + fileName));

				new ZipFileHandling().unzip(downloadPath + fileName,
						downloadPath + fileName.substring(0, fileName.indexOf(".")));

				extractFolderPath = downloadPath + fileName.substring(0, fileName.indexOf("."));
				System.out.println(extractFolderPath);

				File directory = new File(extractFolderPath);
				int fileCount = directory.list().length;

				System.out.println(fileCount);
				
				setTab();

			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});

		verticalLayout.add(upload);
		
		if(extractFolderPath != "") {
			setTab();
		}
	}

	void setTab() throws IOException {

		String data = "";

		Map<Tab, Component> tabsToPages = new HashMap<>();
		Tabs tabs = new Tabs();
		Div pages = new Div();

		tabs.getStyle().set("width", "-webkit-fill-available");
		tabs.getStyle().set("margin-bottom", "-18px");
		tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
		pages.getStyle().set("width", "-webkit-fill-available");
		pages.getStyle().set("border-radius", "0px");

		TextArea rootTextArea = new TextArea();
		rootTextArea.getStyle().set("border-radius", "0px");

		File rootFileDirectory = new File(extractFolderPath);

		File[] rootFileList = rootFileDirectory.listFiles();
		for (File f : rootFileList) {
			System.out.println(f);

			if (!f.isDirectory()) {
				data = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
				pipelineName = f.getName().substring(0, f.getName().lastIndexOf("."));
				
				routerData.put("filePath", extractFolderPath);
				routerData.put("pipelineName", pipelineName);
				
				System.out.println(data);

				Tab rootTab = new Tab(f.getName());

				rootTextArea.setValue("Root \n" + data);
				rootTextArea.setHeight("500px");
				rootTextArea.setWidth("100%");

				rootTextArea.setVisible(true);

				pages.add(rootTextArea);

				tabsToPages.put(rootTab, rootTextArea);

				tabs.add(rootTab);
				break;
			}

		}

		int i = 1;

		for (File f : rootFileList) {
			System.out.println(f);

			if (f.isDirectory()) {
				System.out.println(f);
				File[] subProcess = f.listFiles();

				for (File subFile : subProcess) {
					if (!subFile.isDirectory()) {
						data = new String(Files.readAllBytes(Paths.get(subFile.getAbsolutePath())));
						System.out.println(data);
						System.out.println(subFile.getName());

						Tab calleeTab = new Tab(subFile.getName());

						TextArea calleeTextArea = new TextArea();
						calleeTextArea.setValue("Callee " + i + "\n" + data);
						calleeTextArea.setHeight("500px");
						calleeTextArea.setWidth("100%");

						calleeTextArea.setVisible(false);
						i++;

						pages.add(calleeTextArea);

						tabsToPages.put(calleeTab, calleeTextArea);

						tabs.add(calleeTab);
					}
				}

			}
		}

		Set<Component> pagesShown = Stream.of(rootTextArea).collect(Collectors.toSet());

		tabs.addSelectedChangeListener(event -> {
			pagesShown.forEach(page -> page.setVisible(false));
			pagesShown.clear();
			Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
			selectedPage.setVisible(true);
			pagesShown.add(selectedPage);
		});

		verticalLayout.add(tabs);
		verticalLayout.add(pages);

		setConfigurationComponent();
	}

	public void setConfigurationComponent() {

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.addClassName("add-variable-layout");
		Label addLabel = new Label("Add Variable");

		Icon addIcon = new Icon(VaadinIcon.PLUS);
		addIcon.getStyle().set("cursor", "pointer");
		addIcon.getStyle().set("width", "20px");
		addIcon.getStyle().set("height", "20px");
		addIcon.addClickListener(event -> {
			
			if(pipelineName == null || pipelineName == "") {
				next.setEnabled(false);
			}
			
			verticalLayout.remove(next);

			HorizontalLayout addField = new HorizontalLayout();
			addField.getStyle().set("display", "table-column-group");
			addField.getStyle().set("width", "100%");

			TextField localVariableField = new TextField();
			localVariableField.setPlaceholder("Variable");

			TextField localValueField = new TextField();
			localValueField.setPlaceholder("Value");

			Icon saveIcon = new Icon(VaadinIcon.FILE_O);
			saveIcon.getStyle().set("cursor", "pointer");
			saveIcon.getStyle().set("width", "20px");
			saveIcon.getStyle().set("height", "20px");
			saveIcon.addClickListener(saveEvent -> {
				saveVariable(localVariableField.getValue(), localValueField.getValue());
			});

			Icon removeIcon = new Icon(VaadinIcon.TRASH);
			removeIcon.getStyle().set("cursor", "pointer");
			removeIcon.getStyle().set("width", "20px");
			removeIcon.getStyle().set("height", "20px");
			removeIcon.addClickListener(removeEvent -> {
				deleteConfirmDialog(localVariableField, localValueField, pipelineName, addField, saveIcon, removeIcon);
			});
			
			Button makeCommon = new Button("Make Common");
			
			makeCommon.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			makeCommon.getStyle().set("background", "#58d2cc");
			makeCommon.getStyle().set("border-radius", "6px");
			makeCommon.getStyle().set("color", "#4b483f");
			
			makeCommon.addClickListener(e -> {
				makeAsCommon(localVariableField.getValue(), localValueField.getValue());
			});

			addField.add(localVariableField);
			addField.add(localValueField);
			addField.add(makeCommon);
			addField.add(saveIcon);
			addField.add(removeIcon);

			verticalLayout.add(addField);
			verticalLayout.add(next);

			Notification.show("added");
		});

		horizontalLayout.add(addLabel);
		horizontalLayout.add(addIcon);

		List<EditPipelineConfig> variableList = pipelineConfigDao.findByPipeline(pipelineName);

		int[] i = { 1 };

		variableList.forEach((temp) -> {
			HorizontalLayout addField = new HorizontalLayout();
			addField.getStyle().set("display", "table-column-group");
			addField.getStyle().set("width", "100%");

			TextField localVariableField = new TextField();
			localVariableField.setPlaceholder("Variable");
			localVariableField.setValue(temp.getVariable());

			TextField localValueField = new TextField();
			localValueField.setPlaceholder("Value");
			localValueField.setValue(temp.getValue());

			Icon saveIcon = new Icon(VaadinIcon.FILE_O);
			saveIcon.getStyle().set("cursor", "pointer");
			saveIcon.getStyle().set("width", "20px");
			saveIcon.getStyle().set("height", "20px");
			saveIcon.addClickListener(saveEvent -> {
				saveVariable(localVariableField.getValue(), localValueField.getValue());
			});

			Icon removeIcon = new Icon(VaadinIcon.TRASH);
			removeIcon.getStyle().set("cursor", "pointer");
			removeIcon.getStyle().set("width", "20px");
			removeIcon.getStyle().set("height", "20px");
			removeIcon.addClickListener(removeEvent -> {
				deleteConfirmDialog(localVariableField, localValueField, pipelineName, addField, saveIcon, removeIcon);
			});
			
			Button makeCommon = new Button("Make Common");
			makeCommon.addClassName("add-button");

			
			makeCommon.addClickListener(e -> {
				makeAsCommon(localVariableField.getValue(), localValueField.getValue());
			});

			addField.add(localVariableField);
			addField.add(localValueField);
			addField.add(makeCommon);
			addField.add(saveIcon);
			addField.add(removeIcon);

			verticalLayout.add(addField);
		});

		verticalLayout.add(horizontalLayout);

		/*
		 * HorizontalLayout horizontalLayout = new HorizontalLayout();
		 * 
		 * TextField variableField = new TextField();
		 * variableField.setPlaceholder("Variable");
		 * 
		 * TextField valueField = new TextField(); valueField.setPlaceholder("Value");
		 * 
		 * horizontalLayout.add(variableField); horizontalLayout.add(valueField);
		 */

		verticalLayout.add(horizontalLayout);
		
		if(pipelineName == null || pipelineName == "") {
			next.setEnabled(false);
		}
		
		verticalLayout.add(next);

	}
	
	private void makeAsCommon(String variable, String value) {
		
		CommonConfig commonConfig = new CommonConfig();
		
		commonConfig = new CommonConfig();
		commonConfig.setId(0L);
		commonConfig.setActive("Y");
		commonConfig.setVersion(0);
		commonConfig.setVariable(variable);
		commonConfig.setValue(value);
		
		CommonConfig insertData = this.commonConfigDao.insert(commonConfig);
		
		if(insertData != null) {
			Notification.show("Saved Successfully!");
		}else {
			Notification.show("Saved Failure!");
		}
	}
	
	//Confirmation Dialog
	private void deleteConfirmDialog(TextField variable, TextField value, String pipeline, HorizontalLayout addField, Icon saveIcon, Icon removeIcon) {
		
		Dialog dialog = new Dialog();

		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);

		Label messageLabel = new Label();

		NativeButton confirmButton = new NativeButton("Confirm", event -> {
			
			if(this.pipelineConfigDao.deleteByVariableAndValueAndPipeline(variable.getValue(), value.getValue(), pipelineName)) {
				addField.remove(variable);
				addField.remove(value);
				addField.remove(saveIcon);
				addField.remove(removeIcon);
				
				Notification.show("Deleted Successfully!");
			}else {
				Notification.show("Deletion Failure!");
			}
			
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

	public void saveVariable(String variable, String value) {

		List<String> variableList = this.pipelineConfigDao.getVariableByPipeline(pipelineName);
		System.out.println(variableList);

		if (variableList.contains(variable)) {
			saveConfirmDialog(variable, value);
		} else {
			
			List<PipelineMaster> pipelineMaster = this.pipelineConfigDao.findByPipelineInMaster(pipelineName);
			
			if(pipelineMaster == null || pipelineMaster.isEmpty()) {
				PipelineMaster insertValue = new PipelineMaster();
				insertValue.setId(0L);
				insertValue.setProcess(pipelineName);
				insertValue.setDescription("");
				insertValue.setShortName("");
				
				PipelineMaster insertedData = this.pipelineConfigDao.insertPipeline(insertValue);
				
				if(insertedData != null) {
					
					this.pipelineConfig = new EditPipelineConfig();
					
					this.pipelineConfig.setId(0L);
					this.pipelineConfig.setVersion(0);
					this.pipelineConfig.setActive("Y");
			
					
					if(variable == null || variable.equals("") || value == null || value.equals("")) {
						Notification.show("Variable or Value should not be empty");
					}else {
						this.pipelineConfig.setProcess(pipelineName);
						this.pipelineConfig.setVariable(variable);
						this.pipelineConfig.setValue(value);

						EditPipelineConfig insertData = this.pipelineConfigDao.insert(this.pipelineConfig);
						
						if(insertData != null) {
							Notification.show("Saved Successfully!");
						}else {
							Notification.show("Save Failure!");
						}
					}
				}else {
					Notification.show("Save Failure!");
				}
			}else {
				
				this.pipelineConfig = new EditPipelineConfig();
				
				this.pipelineConfig.setId(0L);
				this.pipelineConfig.setVersion(0);
				this.pipelineConfig.setActive("Y");
		
				
				if(variable == null || variable.equals("") || value == null || value.equals("")) {
					Notification.show("Variable or Value should not be empty");
				}else {
					this.pipelineConfig.setProcess(pipelineName);
					this.pipelineConfig.setVariable(variable);
					this.pipelineConfig.setValue(value);

					EditPipelineConfig insertData = this.pipelineConfigDao.insert(this.pipelineConfig);
					
					if(insertData != null) {
						Notification.show("Saved Successfully!");
					}else {
						Notification.show("Save Failure!");
					}
				}
			
			}
		}
	}

	// Confirmation Dialog
	private void saveConfirmDialog(String variable, String value) {

		Dialog dialog = new Dialog();

		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);

		VerticalLayout dialogLayout = new VerticalLayout();

		HorizontalLayout messageLabelLayout = new HorizontalLayout();
		messageLabelLayout.setWidthFull();
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setWidthFull();
		Label messageLabel = new Label();
		messageLabel.setText("Variable already exists, Do you want to save?");
		messageLabel.addClassName("label-bold");
		messageLabelLayout.add(messageLabel);
		NativeButton confirmButton = new NativeButton("Confirm", event -> {

			if (this.pipelineConfigDao.updateVariableByPipeline(pipelineName, variable, value)) {
				Notification.show("Updated Successfully!");
			} else {
				Notification.show("Updation Fail!");
			}

			dialog.close();
		});

		confirmButton.addClassName("add-button");
		confirmButton.getStyle().set("margin-left", "auto");
		NativeButton cancelButton = new NativeButton("Cancel", event -> {
			dialog.close();
		});

		cancelButton.addClassName("delete-button");
		cancelButton.getStyle().set("margin-right", "auto");

		buttonLayout.add(confirmButton, cancelButton);
		dialogLayout.add(messageLabelLayout, buttonLayout);
		dialog.add(dialogLayout);

		dialog.open();
	}

}
