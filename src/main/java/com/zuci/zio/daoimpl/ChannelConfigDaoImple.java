package com.zuci.zio.daoimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.zuci.zio.dao.ChannelConfigDao;
import com.zuci.zio.model.ChannelConfig;

@Repository
public class ChannelConfigDaoImple implements ChannelConfigDao{

	private final String FETCH_ALL = "select * from spw_instance_config";
	
	private final String FETCH_ALL_BY_CHANNEL = "select * from spw_instance_config where instance = ?";
	
	private final String INSERT_DATA = "insert into spw_instance_config (id,instance,process,variable,value,active,version,alias,seedConfig) values (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE instance = ?, process = ?, variable = ?, alias = ?, value = ?";
	
	private final String INSERT_AUDIT_DATA = "insert into spw_instance_config_audit (id,instance,process,variable,value,active,version,alias,seedConfig) values (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE instance = ?, process = ?, variable = ?, alias = ?, value = ?";
	
	private final String DELETE_BY_ID = "delete from spw_instance_config where id = ?";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<ChannelConfig> findAll() {
		
		return jdbcTemplate.query(FETCH_ALL, new ChannelConfigMapper());
	}

	@Override
	public List<ChannelConfig> findByChannel(String instance) {
		
		return jdbcTemplate.query(FETCH_ALL_BY_CHANNEL, new Object[]{instance}, new ChannelConfigMapper());
	}

	@Override
	public ChannelConfig insert(ChannelConfig channelConfig) {
		
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
				ps.setString(8, channelConfig.getAlias());
				ps.setInt(9, channelConfig.getSeedConfig());
				ps.setString(10, channelConfig.getInstance());
				ps.setString(11, channelConfig.getProcess());
				ps.setString(12, channelConfig.getVariable());
				ps.setString(13, channelConfig.getAlias());
				ps.setString(14, channelConfig.getValue());
				return ps;
			}

		}, holder);

		if(channelConfig.getId() == null) {
			Long newCommonConfigId = (long) holder.getKey().intValue();
			channelConfig.setId(newCommonConfigId);
		}
		
		return channelConfig;
	}

	@Override
	public void deleteById(Long id) {

		jdbcTemplate.update(DELETE_BY_ID, id);
	}

	@Override
	public ChannelConfig insertAudit(ChannelConfig channelConfig) {
		
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
				ps.setString(8, channelConfig.getAlias());
				ps.setInt(9, channelConfig.getSeedConfig());
				ps.setString(10, channelConfig.getInstance());
				ps.setString(11, channelConfig.getProcess());
				ps.setString(12, channelConfig.getVariable());
				ps.setString(13, channelConfig.getAlias());
				ps.setString(14, channelConfig.getValue());
				return ps;
			}

		}, holder);

		if(channelConfig.getId() == null) {
			Long newCommonConfigId = (long) holder.getKey().intValue();
			channelConfig.setId(newCommonConfigId);
		}
		
		return channelConfig;
	}
}

class ChannelConfigMapper implements RowMapper {

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
		channelConfig.setAlias(rs.getString("alias"));
		channelConfig.setSeedConfig(rs.getInt("seedConfig"));
		
		return channelConfig;
	}

}
