package com.zuci.zio.dao;

import java.util.List;

import com.zuci.zio.model.EditPipelineConfig;
import com.zuci.zio.model.EditPipelineConfig;
import com.zuci.zio.model.PipelineConfig;

public interface PipelineConfigDao {

	public List<PipelineConfig> findAll();
	
	public List<EditPipelineConfig> findByPipeline(String process);
	
	public EditPipelineConfig insert(final EditPipelineConfig pipelineConfig);
	
	public EditPipelineConfig insertAudit(final EditPipelineConfig pipelineConfig);
	
	public void deleteById(Long id);
}
