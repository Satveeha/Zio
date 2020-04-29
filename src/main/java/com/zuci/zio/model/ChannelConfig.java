package com.zuci.zio.model;

public class ChannelConfig {

	Long id;
	
	String instance;
	
	String process;
	
	String variable;
	
	String value;

	String active;
	
	Integer version;
	
	Integer seedConfig;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getSeedConfig() {
		return seedConfig;
	}

	public void setSeedConfig(Integer seedConfig) {
		this.seedConfig = seedConfig;
	}
	
	
}
