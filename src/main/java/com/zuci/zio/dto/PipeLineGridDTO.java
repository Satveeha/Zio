package com.zuci.zio.dto;

import java.util.List;

public class PipeLineGridDTO {

	String process;
	
	int channelCount;
	
	int variableCount;
	
	List<String> variables;
	
	int overriddenCount;

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

	public int getOverriddenCount() {
		return overriddenCount;
	}

	public void setOverriddenCount(int overriddenCount) {
		this.overriddenCount = overriddenCount;
	}
	
	
	
}
