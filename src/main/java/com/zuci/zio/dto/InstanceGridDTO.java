package com.zuci.zio.dto;

import java.util.List;

public class InstanceGridDTO {

	String instance;
	
	String process;
	
	int channelCount;
	
	int variableCount;
	
	List<String> variables;
	
	int overriddenCommonCount;
	
	int overriddenPipelineCount;

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public int getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

	public int getVariableCount() {
		return variableCount;
	}

	public void setVariableCount(int variableCount) {
		this.variableCount = variableCount;
	}

	public List<String> getVariables() {
		return variables;
	}

	public void setVariables(List<String> variables) {
		this.variables = variables;
	}

	public int getOverriddenCommonCount() {
		return overriddenCommonCount;
	}

	public void setOverriddenCommonCount(int overriddenCommonCount) {
		this.overriddenCommonCount = overriddenCommonCount;
	}

	public int getOverriddenPipelineCount() {
		return overriddenPipelineCount;
	}

	public void setOverriddenPipelineCount(int overriddenPipelineCount) {
		this.overriddenPipelineCount = overriddenPipelineCount;
	}
	
}
