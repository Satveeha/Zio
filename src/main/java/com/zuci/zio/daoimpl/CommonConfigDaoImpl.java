package com.zuci.zio.daoimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.zuci.zio.dao.CommonConfigDao;
import com.zuci.zio.model.CommonConfig;

@Repository
public class CommonConfigDaoImpl implements CommonConfigDao{

	private final String FETCH_ALL = "select id,variable,value,active,version from spw_common_config";
	
	//private final String INSERT_DATA = "insert into spw_common_config (variable,value,active,version) values (?,?,?,?)";
	
	//private static String UPDATE_DATA = "UPDATE spw_common_config SET variable = ?,value = ?,active = ?,version = ? WHERE id = ?";
	
	private final String INSERT_DATA = "insert into spw_common_config (id,variable,value,active,version) values (?,?,?,?,?) ON DUPLICATE KEY UPDATE variable = ?, value = ?";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public CommonConfig insert(CommonConfig commonConfig) {
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(INSERT_DATA, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, commonConfig.getId());
				ps.setString(2, commonConfig.getVariable());
				ps.setString(3, commonConfig.getValue());
				ps.setString(4, commonConfig.getActive());
				ps.setInt(5, commonConfig.getVersion());
				ps.setString(6, commonConfig.getVariable());
				ps.setString(7, commonConfig.getValue());
				return ps;
			}

		}, holder);

		if(commonConfig.getId() == null) {
			
			Long newCommonConfigId = (long) holder.getKey().intValue();
			commonConfig.setId(newCommonConfigId);
		}
		
		return commonConfig;
	}
	
	/*@Override
	public CommonConfig insert(CommonConfig commonConfig) {
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(INSERT_DATA, Statement.RETURN_GENERATED_KEYS);
				ps.setString(2, commonConfig.getVariable());
				ps.setString(3, commonConfig.getValue());
				ps.setString(4, commonConfig.getActive());
				ps.setInt(5, commonConfig.getVersion());
				return ps;
			}

		}, holder);

		Long newCommonConfigId = (long) holder.getKey().intValue();
		commonConfig.setId(newCommonConfigId);
		
		return commonConfig;
	}
	
	@Override
	public CommonConfig update(CommonConfig commonConfig) {
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(UPDATE_DATA, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, commonConfig.getVariable());
				ps.setString(2, commonConfig.getValue());
				ps.setString(3, commonConfig.getActive());
				ps.setInt(4, commonConfig.getVersion());
				ps.setLong(5, commonConfig.getId());
				return ps;
			}

		}, holder);
		
		return commonConfig;
	}*/

	@Override
	public List<CommonConfig> findAll() {
		
		return jdbcTemplate.query(FETCH_ALL, new CommonConfigMapper());
	}
}

class CommonConfigMapper implements RowMapper {

	@Override
	public CommonConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		CommonConfig commonConfig = new CommonConfig();
		commonConfig.setId(rs.getLong("id"));
		commonConfig.setVariable(rs.getString("variable"));
		commonConfig.setValue(rs.getString("value"));
		commonConfig.setActive(rs.getString("active"));
		commonConfig.setVersion(rs.getInt("version"));
		
		return commonConfig;
	}

}
