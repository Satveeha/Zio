package com.zuci.zio.dao;

import java.util.List;

import com.zuci.zio.dto.InstanceGridDTO;
import com.zuci.zio.model.ChannelConfig;
import com.zuci.zio.model.ChannelMaster;

public interface ChannelConfigDao {
	
	public List<ChannelConfig> findAll();
	
	public List<InstanceGridDTO> getGrid();
	
	public List<ChannelConfig> findByChannel(String channel);
	
	public List<ChannelConfig> findByPipeline(String pipeline);
	
	public List<ChannelMaster> findByPipelineAndChannel(String pipeline, String Channel);
	
	public ChannelConfig insert(final ChannelConfig channelConfig);
	
	public List<ChannelConfig> insertBatch(final List<ChannelConfig> channelConfig);
	
	public ChannelMaster insertInstance(final ChannelMaster channelMaster);
	
	public ChannelConfig insertAudit(final ChannelConfig channelConfig);
	
	public Boolean deleteById(Long id);
	
}
