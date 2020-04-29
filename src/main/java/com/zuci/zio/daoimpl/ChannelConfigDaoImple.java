package com.zuci.zio.daoimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.zuci.zio.dao.ChannelConfigDao;
import com.zuci.zio.dto.InstanceGridDTO;
import com.zuci.zio.dto.PipelineVariableDTO;
import com.zuci.zio.dto.InstanceGridDTO;
import com.zuci.zio.model.ChannelConfig;
import com.zuci.zio.model.EditPipelineConfig;

@Repository
public class ChannelConfigDaoImple implements ChannelConfigDao{

	private final String FETCH_ALL = "select * from spw_instance_config";
	
	private final String FETCH_ALL_BY_CHANNEL = "select * from spw_instance_config where instance = ?";
	
	private final String INSERT_DATA = "insert into spw_instance_config (id,instance,process,variable,value,active,version,seedConfig) values (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE instance = ?, process = ?, variable = ?, value = ?";
	
	private final String INSERT_AUDIT_DATA = "insert into spw_instance_config_audit (id,instance,process,variable,value,active,version,seedConfig) values (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE instance = ?, process = ?, variable = ?, value = ?";
	
	private final String DELETE_BY_ID = "delete from spw_instance_config where id = ?";
	
	private final String FETCH_CHANNEL_GRID = "select instance, process, count(instance) as channelCount, count(instance) as variableCount,group_concat(distinct variable) as variables,0 as overriddenCommonCount, 0 as overriddenPipelineCount from spw_instance_config group by instance,process;";
	
	private final String FETCH_DISTINCT_COMMON_VARIABLES = "select distinct variable from spw_common_config";
	
	private final String FETCH_PROCESS_VARIABLE = "select process, GROUP_CONCAT(variable) as variables from spw_process_config GROUP BY process";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<ChannelConfig> findAll() {
		
		List<ChannelConfig> returnData = new ArrayList<ChannelConfig>();
		
		try {
			returnData = jdbcTemplate.query(FETCH_ALL,new BeanPropertyRowMapper(ChannelConfig.class));
		} catch (Exception e) {
			System.out.println(e);
			return returnData;
		}
		
		return returnData;
	}

	@Override
	public List<ChannelConfig> findByChannel(String instance) {
		
		List<ChannelConfig> returnData = new ArrayList<ChannelConfig>();
		
		try {
			returnData = jdbcTemplate.query(FETCH_ALL_BY_CHANNEL, new Object[]{instance}, new BeanPropertyRowMapper(ChannelConfig.class));
		} catch (Exception e) {
			System.out.println(e);
			return returnData;
		}
		
		return returnData;
	}

	@Override
	public ChannelConfig insert(ChannelConfig channelConfig) {
		
		try {
			KeyHolder holder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(new PreparedStatementCreator() {
				
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_DATA, Statement.RETURN_GENERATED_KEYS);
					ps.setLong(1, channelConfig.getId());
					ps.setString(2, channelConfig.getInstance());
					ps.setString(3, channelConfig.getProcess());
					ps.setString(4, channelConfig.getVariable());
					ps.setString(5, channelConfig.getValue());
					ps.setString(6, channelConfig.getActive());
					ps.setInt(7, channelConfig.getVersion());
					ps.setInt(8, channelConfig.getSeedConfig());
					ps.setString(9, channelConfig.getInstance());
					ps.setString(10, channelConfig.getProcess());
					ps.setString(11, channelConfig.getVariable());
					ps.setString(12, channelConfig.getValue());
					return ps;
				}

			}, holder);

			if(channelConfig.getId() == null) {
				Long newCommonConfigId = (long) holder.getKey().intValue();
				channelConfig.setId(newCommonConfigId);
			}
			
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
		
		return channelConfig;
	}

	@Override
	public Boolean deleteById(Long id) {
		
		Boolean returnData = false;
		
		try {
			jdbcTemplate.update(DELETE_BY_ID, id);
			returnData = true;
		} catch (Exception e) {
			System.out.println(e);
			return returnData;
		}
		
		return returnData;
	}

	@Override
	public ChannelConfig insertAudit(ChannelConfig channelConfig) {
		
		try {
			KeyHolder holder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(new PreparedStatementCreator() {
				
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_AUDIT_DATA, Statement.RETURN_GENERATED_KEYS);
					ps.setLong(1, 0);
					ps.setString(2, channelConfig.getInstance());
					ps.setString(3, channelConfig.getProcess());
					ps.setString(4, channelConfig.getVariable());
					ps.setString(5, channelConfig.getValue());
					ps.setString(6, channelConfig.getActive());
					ps.setInt(7, channelConfig.getVersion());
					ps.setInt(8, channelConfig.getSeedConfig());
					ps.setString(9, channelConfig.getInstance());
					ps.setString(10, channelConfig.getProcess());
					ps.setString(11, channelConfig.getVariable());
					ps.setString(12, channelConfig.getValue());
					return ps;
				}

			}, holder);

			if(channelConfig.getId() == null) {
				Long newCommonConfigId = (long) holder.getKey().intValue();
				channelConfig.setId(newCommonConfigId);
			}
			
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
		
		return channelConfig;
	}

	@Override
	public List<InstanceGridDTO> getGrid() {
		
		List<InstanceGridDTO> returnData =new ArrayList<InstanceGridDTO>();
		try {
			List<InstanceGridDTO> instanceGrid = jdbcTemplate.query(FETCH_CHANNEL_GRID,new BeanPropertyRowMapper(InstanceGridDTO.class));
			List<PipelineVariableDTO> pipelineVariableDTO = jdbcTemplate.query(FETCH_PROCESS_VARIABLE,new BeanPropertyRowMapper(PipelineVariableDTO.class));
			List<String> uniqueVariables = jdbcTemplate.queryForList(FETCH_DISTINCT_COMMON_VARIABLES, String.class);
			instanceGrid.stream().forEach(p -> {
				
				p.setOverriddenCommonCount((int) uniqueVariables.stream().filter(s-> p.getVariables().contains(s)).count());
				
				p.getVariables().forEach( e -> {
					int count = p.getOverriddenPipelineCount();	
					count += pipelineVariableDTO.stream().filter(s-> s.getProcess().equals(p.getProcess()) && s.getVariables().contains(e)).count();
					p.setOverriddenPipelineCount(count);
				});
				
			});
			
			returnData = instanceGrid;
		} catch (Exception e) {
			System.out.println(e);
			return returnData;
		}
		
		return returnData;
	}
}

/*class ChannelConfigMapper implements RowMapper {

	@Override
	public ChannelConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		ChannelConfig channelConfig = new ChannelConfig();
		channelConfig.setId(rs.getLong("id"));
		channelConfig.setInstance(rs.getString("instance"));
		channelConfig.setProcess(rs.getString("process"));
		channelConfig.setVariable(rs.getString("variable"));
		channelConfig.setValue(rs.getString("value"));
		channelConfig.setActive(rs.getString("active"));
		channelConfig.setVersion(rs.getInt("version"));
		channelConfig.setSeedConfig(rs.getInt("seedConfig"));
		
		return channelConfig;
	}

}*/
