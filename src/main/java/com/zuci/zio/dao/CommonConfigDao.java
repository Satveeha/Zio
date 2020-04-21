package com.zuci.zio.dao;

import java.util.List;

import com.zuci.zio.model.CommonConfig;

public interface CommonConfigDao {

	//public CommonConfig insert(final CommonConfig commonConfig);
	
	public CommonConfig insert(final CommonConfig commonConfig);
	
	public CommonConfig insertAudit(final CommonConfig commonConfig);
	
	//public CommonConfig update(final CommonConfig commonConfig);

	public List<CommonConfig> findAll();
	
	public void deleteById(Long id); 
}
