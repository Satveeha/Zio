package com.zuci.zio.views.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.zuci.zio.commonFuntions.ConfigRead;
import com.zuci.zio.commonFuntions.ZipFileHandling;
import com.zuci.zio.views.main.MainView;

@Route(value = "upload", layout = MainView.class)
@PageTitle("Upload")
@CssImport(value = "./styles/my-grid-styles.css", themeFor = "vaadin-grid")
public class UploadView extends Div implements AfterNavigationObserver {

	private VerticalLayout verticalLayout;
	
	static ConfigRead configRead = new ConfigRead();
	private static String downloadPath = configRead.readProperty("downloadPath");
	
	private String fileName = "";
	
	private String extractFolderPath = "";
	
	public UploadView() throws IOException {
		
		verticalLayout = new VerticalLayout();
		verticalLayout.setHeightFull();
		verticalLayout.setWidthFull();
		
		setUploadComponent();
		
		add(verticalLayout);
	}
	
	public void setUploadComponent() {
		
		MemoryBuffer memoryBuffer = new MemoryBuffer();
		
		Upload upload = new Upload(memoryBuffer);
		//upload.setMaxFileSize(1024*8388608);
		
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
				FileUtils.copyInputStreamToFile(inputStream, new File(downloadPath+fileName));
				
				new ZipFileHandling().unzip(downloadPath+fileName, downloadPath+fileName.substring(0, fileName.indexOf(".")));
				
				extractFolderPath = downloadPath+fileName.substring(0, fileName.indexOf("."));
				System.out.println(extractFolderPath);
				
				File directory=new File(extractFolderPath);
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
		
		Div rootDiv = new Div();
		
		File rootFileDirectory = new File(extractFolderPath);
		
		File[] rootFileList = rootFileDirectory.listFiles();
		for(File f: rootFileList){
			System.out.println(f);
			
			if(!f.isDirectory()) {
				data = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath()))); 
			    System.out.println(data);
			    
			    Tab rootTab = new Tab("Root");
			    
			    //rootDiv.setTitle(f.getName());
			    rootDiv.setText(data);
			    rootDiv.setVisible(true);
			    
			    pages.add(rootDiv);
			    
			    tabsToPages.put(rootTab, rootDiv);
			    tabs.add(rootTab);
			    break;
			}
			
		}
		
		int i = 1;
		
		for(File f: rootFileList){
			System.out.println(f);
			
			if(f.isDirectory()) {
				System.out.println(f);
				File[] subProcess = f.listFiles();
				
				for(File subFile: subProcess){
					if(!subFile.isDirectory()) {
						data = new String(Files.readAllBytes(Paths.get(subFile.getAbsolutePath()))); 
					    System.out.println(data);
					    
					    Tab calleeTab = new Tab("Callee "+i);
					    i++;
					    
					    Div calleeDiv = new Div();
					    //calleeDiv.setTitle(subFile.getName());
					    calleeDiv.setText(data);
					    calleeDiv.setVisible(false);
					    
					    pages.add(calleeDiv);
					    
					    tabsToPages.put(calleeTab, calleeDiv);
					    tabs.add(calleeTab);
					}
				}
					
			}
		}
		
		Set<Component> pagesShown  = Stream.of(rootDiv)
	            .collect(Collectors.toSet());
		
		tabs.addSelectedChangeListener(event -> {
		    pagesShown.forEach(page -> page.setVisible(false));
		    pagesShown.clear();
		    Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
		    selectedPage.setVisible(true);
		    pagesShown.add(selectedPage);
		});
		
		verticalLayout.add(tabs);
		verticalLayout.add(pages);
		
	}
	
	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		// TODO Auto-generated method stub
		
	}

}
