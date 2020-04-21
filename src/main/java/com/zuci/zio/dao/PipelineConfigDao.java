package com.zuci.zio.dao;

import java.util.List;

import com.zuci.zio.model.EditPipelineConfig;
import com.zuci.zio.model.EditPipelineConfig;
import com.zuci.zio.model.PipelineConfig;

public interface PipelineConfigDao {

	public List<PipelineConfig> findAll();
	
	public List<EditPipelineConfig> findByPipeline(String process);
	
	public EditPipelineConfig insert(final EditPipelineConfig commonConfig);
	
	public void deleteById(Long id);
}
