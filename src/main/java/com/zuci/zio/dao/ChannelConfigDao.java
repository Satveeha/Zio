package com.zuci.zio.dao;

import java.util.List;

import com.zuci.zio.dto.InstanceGridDTO;
import com.zuci.zio.model.ChannelConfig;

public interface ChannelConfigDao {
	
	public List<ChannelConfig> findAll();
	
	public List<InstanceGridDTO> getGrid();
	
	public List<ChannelConfig> findByChannel(String instance);
	
	public ChannelConfig insert(final ChannelConfig channelConfig);
	
	public ChannelConfig insertAudit(final ChannelConfig channelConfig);
	
	public Boolean deleteById(Long id);
	
}
