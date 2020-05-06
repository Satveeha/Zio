package com.zuci.zio.daoimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.zuci.zio.dao.PipelineConfigDao;
import com.zuci.zio.dto.PipeLineGridDTO;
import com.zuci.zio.model.EditPipelineConfig;
import com.zuci.zio.model.PipelineConfig;
import com.zuci.zio.model.PipelineMaster;

@Repository
public class PipelineConfigDaoImpl implements PipelineConfigDao{

	private final String FETCH_ALL = "select process,count(*) as count from spw_process_config group by process";
	
	private final String FETCH_ALL_BY_PIPELINE = "select * from spw_process_config where process = ?";
	
	private final String FETCH_ALL_BY_PIPELINE_IN_MASTER = "select * from spw_process where process = ?";
	
	private final String INSERT_DATA = "insert into spw_process_config (id,process,variable,value,active,version) values (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE process = ?, variable = ?, value = ?";
	
	private final String INSERT_AUDIT_DATA = "insert into spw_process_config_audit (id,process,variable,value,active,version) values (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE process = ?, variable = ?, value = ?";
	
	private final String INSERT_PIPELINE_DATA = "insert into spw_process (id,process,description,shortName) values (?,?,?,?) ON DUPLICATE KEY UPDATE process = ?, description = ?, shortName = ?";
	
	private final String DELETE_BY_ID = "delete from spw_process_config where id = ?";
	
	private final String DELETE_BY_VARIABLE_VALUE = "delete from spw_process_config where variable = ? and value = ? and process = ?";
	
	private final String FETCH_PIPELINE_GRID = "select process, count(process) as channelCount, count(process) as variableCount,group_concat(distinct variable) as variables,0 as overriddenCount from spw_process_config group by process";
	
	private final String FETCH_DISTINCT_COMMON_VARIABLES = "select distinct variable from spw_common_config";
	
	private final String FETCH_VARIABLE_BY_PIPELINE = "select variable from spw_process_config where process = ?";
	
	private final String UPDATE_BY_PIPELINE = "update spw_process_config set value = ? where process = ? and variable = ?";
	
	private final String GET_PIPELINE_BY_PIPELINE_ALIAS = "select * from spw_process_config where variable in (select c.variable from spw_instance_config c inner join spw_instance m on c.instance = m.instance and c.process = m.process where m.alias = ?) and process = ?";
	
	private final String GET_EXCLUDED_VARIABLE_PIPELINE_BY_PIPELINE_ALIAS = "select * from spw_process_config where variable not in (select c.variable from spw_instance_config c inner join spw_instance m on c.instance = m.instance and c.process = m.process where m.alias = ?) and process = ?";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<PipelineConfig> findAll() {
		
		List<PipelineConfig> returnData=new ArrayList<PipelineConfig>();
		
		try {
			returnData = jdbcTemplate.query(FETCH_ALL,new BeanPropertyRowMapper(PipelineConfig.class));
		} catch (Exception e) {
			System.out.println(e);
			return returnData;
		}
		
		return returnData;
	}
	
	@Override
	public Boolean deleteById(Long id) {
		
		try {
			jdbcTemplate.update(DELETE_BY_ID, id);
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		
		return true;
	}

	@Override
	public List<EditPipelineConfig> findByPipeline(String pipeline) {
		
		List<EditPipelineConfig> returnData = new ArrayList<EditPipelineConfig>();
		
		try {
			returnData = jdbcTemplate.query(FETCH_ALL_BY_PIPELINE,new Object[]{pipeline},new BeanPropertyRowMapper(EditPipelineConfig.class));
		} catch (Exception e) {
			System.out.println(e);
			return returnData;
		}
		
		return returnData;
	}

	@Override
	public EditPipelineConfig insert(EditPipelineConfig pipelineConfig) {
		
		try {
			KeyHolder holder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(new PreparedStatementCreator() {
				
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_DATA, Statement.RETURN_GENERATED_KEYS);
					ps.setLong(1, pipelineConfig.getId());
					ps.setString(2, pipelineConfig.getProcess());
					ps.setString(3, pipelineConfig.getVariable());
					ps.setString(4, pipelineConfig.getValue());
					ps.setString(5, pipelineConfig.getActive());
					ps.setInt(6, pipelineConfig.getVersion());
					ps.setString(7, pipelineConfig.getProcess());
					ps.setString(8, pipelineConfig.getVariable());
					ps.setString(9, pipelineConfig.getValue());
					return ps;
				}

			}, holder);

			if(pipelineConfig.getId() == null) {
				Long newCommonConfigId = (long) holder.getKey().intValue();
				pipelineConfig.setId(newCommonConfigId);
			}
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
		
		return pipelineConfig;
	}

	@Override
	public EditPipelineConfig insertAudit(EditPipelineConfig pipelineConfig) {
		
		try {
			KeyHolder holder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(new PreparedStatementCreator() {
				
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_AUDIT_DATA, Statement.RETURN_GENERATED_KEYS);
					ps.setLong(1, 0);
					ps.setString(2, pipelineConfig.getProcess());
					ps.setString(3, pipelineConfig.getVariable());
					ps.setString(4, pipelineConfig.getValue());
					ps.setString(5, pipelineConfig.getActive());
					ps.setInt(6, pipelineConfig.getVersion());
					ps.setString(7, pipelineConfig.getProcess());
					ps.setString(8, pipelineConfig.getVariable());
					ps.setString(9, pipelineConfig.getValue());
					return ps;
				}

			}, holder);

			if(pipelineConfig.getId() == null) {
				Long newCommonConfigId = (long) holder.getKey().intValue();
				pipelineConfig.setId(newCommonConfigId);
			}
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
		
		return pipelineConfig;
	}

	@Override
	public List<PipeLineGridDTO> getGrid() {
		
		List<PipeLineGridDTO> returnData = new ArrayList<PipeLineGridDTO>();
		try {
			List<PipeLineGridDTO> pipeLineGrid = jdbcTemplate.query(FETCH_PIPELINE_GRID,new BeanPropertyRowMapper(PipeLineGridDTO.class));
					//jdbcTemplate.queryForObject(FETCH_PIPELINE_GRID, List.class);
			List<String> uniqueVariables = jdbcTemplate.queryForList(FETCH_DISTINCT_COMMON_VARIABLES, String.class);
			pipeLineGrid.stream().forEach(p -> {
				
				p.setOverriddenCount((int) uniqueVariables.stream().filter(s-> p.getVariables().contains(s)).count());
			});
			
			returnData = pipeLineGrid;
		} catch (Exception e) {
			System.out.println(e);
			return returnData;
		}
		
		return returnData;
	}

	@Override
	public List<String> getVariableByPipeline(String pipeline) {
		
		List<String> variables = new ArrayList<String>();
		try {
			variables = jdbcTemplate.queryForList(FETCH_VARIABLE_BY_PIPELINE,new Object[]{pipeline},String.class);
		} catch (Exception e) {
			System.out.println(e);
			return variables;
		}
		
		return variables;
	}

	@Override
	public Boolean updateVariableByPipeline(String pipeline, String variable, String value) {
		Boolean returnData = false;
		
		try {
			jdbcTemplate.update(UPDATE_BY_PIPELINE, value, pipeline, variable);
			returnData = true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		
		return returnData;
	}

	@Override
	public List<PipelineMaster> findByPipelineInMaster(String pipeline) {
		
		List<PipelineMaster> returnData = new ArrayList<PipelineMaster>();
		
		try {
			returnData = jdbcTemplate.query(FETCH_ALL_BY_PIPELINE_IN_MASTER,new Object[]{pipeline},new BeanPropertyRowMapper(PipelineMaster.class));
		} catch (Exception e) {
			System.out.println(e);
			return returnData;
		}
		
		return returnData;
	}

	@Override
	public PipelineMaster insertPipeline(PipelineMaster channelMaster) {
		
		try {
			KeyHolder holder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(new PreparedStatementCreator() {
				
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_PIPELINE_DATA, Statement.RETURN_GENERATED_KEYS);
					ps.setLong(1, channelMaster.getId());
					ps.setString(2, channelMaster.getProcess());
					ps.setString(3, channelMaster.getDescription());
					ps.setString(4, channelMaster.getShortName());
					ps.setString(5, channelMaster.getProcess());
					ps.setString(6, channelMaster.getDescription());
					ps.setString(7, channelMaster.getShortName());
					return ps;
				}

			}, holder);

			if(channelMaster.getId() == null) {
				Long newCommonConfigId = (long) holder.getKey().intValue();
				channelMaster.setId(newCommonConfigId);
			}
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
		
		return channelMaster;
	}

	@Override
	public Boolean deleteByVariableAndValueAndPipeline(String variable, String value, String pipeline) {
		
		try {
			jdbcTemplate.update(DELETE_BY_VARIABLE_VALUE, variable, value, pipeline);
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		
		return true;
	}

	@Override
	public List<EditPipelineConfig> getProcessDetailsByPipelineAndAlias(String pipeline, String alias) {
		
		List<EditPipelineConfig> returnData = new ArrayList<EditPipelineConfig>();
		
		try {
			returnData = jdbcTemplate.query(GET_PIPELINE_BY_PIPELINE_ALIAS,new Object[]{alias,pipeline},new BeanPropertyRowMapper(EditPipelineConfig.class));
		} catch (Exception e) {
			System.out.println(e);
			return returnData;
		}
		
		return returnData;
	}

	@Override
	public List<EditPipelineConfig> getProcessDetailsExcludedByPipelineAndAlias(String pipeline, String alias) {
		
		List<EditPipelineConfig> returnData = new ArrayList<EditPipelineConfig>();
		
		try {
			returnData = jdbcTemplate.query(GET_EXCLUDED_VARIABLE_PIPELINE_BY_PIPELINE_ALIAS,new Object[]{alias,pipeline},new BeanPropertyRowMapper(EditPipelineConfig.class));
		} catch (Exception e) {
			System.out.println(e);
			return returnData;
		}
		
		return returnData;
	}

}
