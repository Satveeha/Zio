package com.zuci.zio.views.upload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.zuci.zio.dao.ChannelConfigDao;
import com.zuci.zio.model.ChannelMaster;

public class UploadScreenThree extends AppLayout {
	
	@Autowired
	private static ChannelConfigDao channelConfigDao;
	
	private VerticalLayout verticalLayout;
	
	private String pipelineName = "";
	
	private TextField channelName = new TextField();
	
	ComboBox<String> alias = new ComboBox<>();
	
	public UploadScreenThree(ChannelConfigDao channelConfigDao, String pipelineName) {
		
		this.channelConfigDao = channelConfigDao;
		
		this.pipelineName = pipelineName;
		
		channelName.setLabel("Name");
		alias.setLabel("Alias");
		
		verticalLayout = new VerticalLayout();
		verticalLayout.setHeightFull();
		verticalLayout.setWidthFull();
		
		setChannelConfiguration();
		
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
		
		horizontalLayout = new HorizontalLayout();
		horizontalLayout.add(channelName);
		horizontalLayout.add(alias);
		
		verticalLayout.add(horizontalLayout);
	}

}
