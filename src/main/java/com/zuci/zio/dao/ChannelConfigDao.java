package com.zuci.zio.dao;

import java.util.List;

import com.zuci.zio.model.ChannelConfig;

public interface ChannelConfigDao {
	
	public List<ChannelConfig> findAll();
	
	public List<ChannelConfig> findByChannel(String instance);
	
	public ChannelConfig insert(final ChannelConfig channelConfig);
	
}
