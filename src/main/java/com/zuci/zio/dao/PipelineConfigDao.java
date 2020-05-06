package com.zuci.zio.dao;

import java.util.List;

import com.zuci.zio.dto.PipeLineGridDTO;
import com.zuci.zio.model.EditPipelineConfig;
import com.zuci.zio.model.PipelineConfig;
import com.zuci.zio.model.PipelineMaster;

public interface PipelineConfigDao {

	public List<PipelineConfig> findAll();
	
	public List<EditPipelineConfig> findByPipeline(String pipeline);
	
	public List<PipelineMaster> findByPipelineInMaster(String pipeline);
	
	public EditPipelineConfig insert(EditPipelineConfig pipelineConfig);
	
	public EditPipelineConfig insertAudit(EditPipelineConfig pipelineConfig);
	
	public PipelineMaster insertPipeline(PipelineMaster channelMaster);
	
	public List<PipeLineGridDTO> getGrid();
	
	public Boolean deleteById(Long id);
	
	public Boolean deleteByVariableAndValueAndPipeline(String variable, String value, String pipeline);
	
	public List<String> getVariableByPipeline(String pipeline);
	
	public Boolean updateVariableByPipeline(String pipeline, String variable, String value);
	
	public List<EditPipelineConfig> getProcessDetailsByPipelineAndAlias(String pipeline, String alias);
	
	public List<EditPipelineConfig> getProcessDetailsExcludedByPipelineAndAlias(String pipeline, String alias);
}
