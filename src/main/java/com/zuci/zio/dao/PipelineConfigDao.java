package com.zuci.zio.dao;

import java.util.List;

import com.zuci.zio.dto.PipeLineGridDTO;
import com.zuci.zio.model.EditPipelineConfig;
import com.zuci.zio.model.PipelineConfig;

public interface PipelineConfigDao {

	public List<PipelineConfig> findAll();
	
	public List<EditPipelineConfig> findByPipeline(String pipeline);
	
	public EditPipelineConfig insert(final EditPipelineConfig pipelineConfig);
	
	public EditPipelineConfig insertAudit(final EditPipelineConfig pipelineConfig);
	
	public List<PipeLineGridDTO> getGrid();
	
	public Boolean deleteById(Long id);
	
	public List<String> getVariableByPipeline(String pipeline);
	
	public Boolean updateVariableByPipeline(String pipeline, String variable, String value);
}
