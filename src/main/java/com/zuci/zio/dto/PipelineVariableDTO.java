package com.zuci.zio.dto;

import java.util.List;

public class PipelineVariableDTO {

	String process;
	
	List<String> variables;

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public List<String> getVariables() {
		return variables;
	}

	public void setVariables(List<String> variables) {
		this.variables = variables;
	}
	
}
