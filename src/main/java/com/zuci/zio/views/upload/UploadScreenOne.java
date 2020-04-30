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
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.zuci.zio.commonFuntions.ConfigRead;
import com.zuci.zio.commonFuntions.ZipFileHandling;
import com.zuci.zio.dao.PipelineConfigDao;
import com.zuci.zio.model.EditPipelineConfig;
import com.zuci.zio.views.main.MainView;

@Route(value = "upload-screen-one", layout = MainView.class)
@PageTitle("Upload")
@CssImport(value = "./styles/my-grid-styles.css", themeFor = "vaadin-grid")
@CssImport(value = "./styles/upload.css")
@CssImport(value = "./styles/upload.css", themeFor = "vaadin-button")
@CssImport(value = "./styles/tabs.css", themeFor = "vaadin-tabs")
@CssImport(value = "./styles/text-area.css", themeFor = "vaadin-text-area")
@CssImport(value = "./styles/text-field.css", themeFor = "vaadin-text-field")
public class UploadScreenOne extends Div implements AfterNavigationObserver {

	@Autowired
	private static PipelineConfigDao pipelineConfigDao;

	private VerticalLayout verticalLayout;

	static ConfigRead configRead = new ConfigRead();
	private static String downloadPath = configRead.readProperty("downloadPath");

	private String fileName = "";

	private String pipelineName = "";

	private String extractFolderPath = "";

	private Button next = new Button("Next", new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT));

	public UploadScreenOne(PipelineConfigDao pipelineConfigDao) throws IOException {

		this.pipelineConfigDao = pipelineConfigDao;
		next.setIconAfterText(true);
		next.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		next.getStyle().set("margin-left", "auto");
		next.addClassName("delete-button");
//		next.getStyle().set("background", "#58d2cc");
//		next.getStyle().set("border-radius", "6px");
//		next.getStyle().set("color", "#4b483f");

		verticalLayout = new VerticalLayout();
		verticalLayout.setHeightFull();
		verticalLayout.setWidthFull();

		setUploadComponent();

		add(verticalLayout);
	}

	public void setUploadComponent() {

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

		// Div rootDiv = new Div();

		TextArea rootTextArea = new TextArea();
		rootTextArea.getStyle().set("border-radius", "0px");

		File rootFileDirectory = new File(extractFolderPath);

		File[] rootFileList = rootFileDirectory.listFiles();
		for (File f : rootFileList) {
			System.out.println(f);

			if (!f.isDirectory()) {
				data = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
				pipelineName = f.getName().substring(0, f.getName().lastIndexOf("."));
				System.out.println(data);

				Tab rootTab = new Tab("Root");

				/*
				 * rootDiv.setText(data); rootDiv.setVisible(true);
				 * 
				 * pages.add(rootDiv);
				 * 
				 * tabsToPages.put(rootTab, rootDiv);
				 */

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

						Tab calleeTab = new Tab("Callee " + i);

						/*
						 * Div calleeDiv = new Div(); calleeDiv.setText(data);
						 * calleeDiv.setVisible(false);
						 * 
						 * pages.add(calleeDiv);
						 * 
						 * tabsToPages.put(calleeTab, calleeDiv);
						 */

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
			verticalLayout.remove(next);

			HorizontalLayout addField = new HorizontalLayout();
			addField.getStyle().set("display", "table-column-group");

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
				Notification.show(localVariableField.getValue());
			});

			Icon removeIcon = new Icon(VaadinIcon.TRASH);
			removeIcon.getStyle().set("cursor", "pointer");
			removeIcon.getStyle().set("width", "20px");
			removeIcon.getStyle().set("height", "20px");
			removeIcon.addClickListener(removeEvent -> {
				addField.remove(localVariableField);
				addField.remove(localValueField);
				addField.remove(saveIcon);
				addField.remove(removeIcon);
				Notification.show("Removed");
			});

			addField.add(localVariableField);
			addField.add(localValueField);
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

			TextField localVariableField = new TextField();
			localVariableField.setPlaceholder("Variable");
			localVariableField.setValue(temp.getVariable());

			/*
			 * if(i[0] == 1) { localVariableField.setLabel("Variable"); }
			 */

			TextField localValueField = new TextField();
			localValueField.setPlaceholder("Value");
			localValueField.setValue(temp.getValue());

			/*
			 * if(i[0] == 1) { localValueField.setLabel("Value"); }
			 */

			// i[0]= i[0] + 1;

			Icon saveIcon = new Icon(VaadinIcon.FILE_O);
			saveIcon.getStyle().set("cursor", "pointer");
			saveIcon.getStyle().set("width", "20px");
			saveIcon.getStyle().set("height", "20px");
			saveIcon.addClickListener(saveEvent -> {
				saveVariable(localVariableField.getValue(), localValueField.getValue());
				Notification.show(localVariableField.getValue());
			});

			Icon removeIcon = new Icon(VaadinIcon.TRASH);
			removeIcon.getStyle().set("cursor", "pointer");
			removeIcon.getStyle().set("width", "20px");
			removeIcon.getStyle().set("height", "20px");
			removeIcon.addClickListener(removeEvent -> {
				addField.remove(localVariableField);
				addField.remove(localValueField);
				addField.remove(saveIcon);
				addField.remove(removeIcon);
				Notification.show("Removed");
			});

			addField.add(localVariableField);
			addField.add(localValueField);
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
		verticalLayout.add(next);

	}

	public void saveVariable(String variable, String value) {

		List<String> variableList = this.pipelineConfigDao.getVariableByPipeline(pipelineName);
		System.out.println(variableList);

		if (variableList.contains(variable)) {
			saveConfirmDialog(variable, value);
		} else {

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

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		// TODO Auto-generated method stub

	}

}
