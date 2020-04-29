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

@Repository
public class PipelineConfigDaoImpl implements PipelineConfigDao{

	private final String FETCH_ALL = "select process,count(*) as count from spw_process_config group by process";
	
	private final String FETCH_ALL_BY_PIPELINE = "select * from spw_process_config where process = ?";
	
	private final String INSERT_DATA = "insert into spw_process_config (id,process,variable,value,active,version) values (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE process = ?, variable = ?, value = ?";
	
	private final String INSERT_AUDIT_DATA = "insert into spw_process_config_audit (id,process,variable,value,active,version) values (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE process = ?, variable = ?, value = ?";
	
	private final String DELETE_BY_ID = "delete from spw_process_config where id = ?";
	
	private final String FETCH_PIPELINE_GRID = "select process, count(process) as channelCount, count(process) as variableCount,group_concat(distinct variable) as variables,0 as overriddenCount from spw_process_config group by process";
	
	private final String FETCH_DISTINCT_COMMON_VARIABLES = "select distinct variable from spw_common_config";
	
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
	public List<EditPipelineConfig> findByPipeline(String process) {
		
		List<EditPipelineConfig> returnData = new ArrayList<EditPipelineConfig>();
		
		try {
			returnData = jdbcTemplate.query(FETCH_ALL_BY_PIPELINE,new Object[]{process},new BeanPropertyRowMapper(EditPipelineConfig.class));
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
}
