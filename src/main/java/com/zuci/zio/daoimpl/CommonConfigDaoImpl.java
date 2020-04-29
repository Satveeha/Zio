package com.zuci.zio.daoimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.zuci.zio.dao.CommonConfigDao;
import com.zuci.zio.model.CommonConfig;
import com.zuci.zio.model.PipelineConfig;

@Repository
public class CommonConfigDaoImpl implements CommonConfigDao{

	private final String FETCH_ALL = "select id,variable,value,active,version from spw_common_config";
	
	private final String INSERT_DATA = "insert into spw_common_config (id,variable,value,active,version) values (?,?,?,?,?) ON DUPLICATE KEY UPDATE variable = ?, value = ?";
	
	private final String INSERT_AUDIT_DATA = "insert into spw_common_config_audit (id,variable,value,active,version) values (?,?,?,?,?) ON DUPLICATE KEY UPDATE variable = ?, value = ?";
	
	private final String DELETE_BY_ID = "delete from spw_common_config where id = ?";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public CommonConfig insert(CommonConfig commonConfig) {
		
		try {
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
			
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
		
		return commonConfig;
	}
	
	@Override
	public List<CommonConfig> findAll() {
		
		List<CommonConfig> returnData = new ArrayList<CommonConfig>();
		
		try {
			returnData = jdbcTemplate.query(FETCH_ALL,new BeanPropertyRowMapper(CommonConfig.class));
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
		
		//return jdbcTemplate.query(FETCH_ALL, new CommonConfigMapper());
		return returnData;
	}

	@Override
	public Boolean deleteById(Long id) {
		
		Boolean returnData = false;
		
		try {
			jdbcTemplate.update(DELETE_BY_ID, id);
			returnData = true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		
		return returnData;
	}

	@Override
	public CommonConfig insertAudit(CommonConfig commonConfig) {
		
		try {
			KeyHolder holder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(new PreparedStatementCreator() {
				
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_AUDIT_DATA, Statement.RETURN_GENERATED_KEYS);
					ps.setLong(1, 0);
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
			
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
		
		return commonConfig;
	}
}

/*class CommonConfigMapper implements RowMapper {

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

}*/
