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

import com.zuci.zio.dao.PipelineConfigDao;
import com.zuci.zio.model.EditPipelineConfig;
import com.zuci.zio.model.PipelineConfig;

@Repository
public class PipelineConfigDaoImpl implements PipelineConfigDao{

	private final String FETCH_ALL = "select process,count(*) as count from spw_process_config group by process";
	
	private final String FETCH_ALL_BY_PIPELINE = "select * from spw_process_config where process = ?";
	
	private final String INSERT_DATA = "insert into spw_process_config (id,process,variable,value,active,version,description,shortName) values (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE process = ?, description = ?, variable = ?, value = ?, shortName = ?";
	
	private final String DELETE_BY_ID = "delete from spw_process_config where id = ?";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<PipelineConfig> findAll() {
		
		return jdbcTemplate.query(FETCH_ALL, new PipelineConfigMapper());
	}
	
	@Override
	public void deleteById(Long id) {
		
		jdbcTemplate.update(DELETE_BY_ID, id);
	}

	@Override
	public List<EditPipelineConfig> findByPipeline(String process) {
		
		return jdbcTemplate.query(FETCH_ALL_BY_PIPELINE, new Object[]{process}, new EditPipelineConfigMapper());
	}

	@Override
	public EditPipelineConfig insert(EditPipelineConfig pipelineConfig) {
		
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
				ps.setString(7, pipelineConfig.getDescription());
				ps.setString(8, pipelineConfig.getShortName());
				ps.setString(9, pipelineConfig.getProcess());
				ps.setString(10, pipelineConfig.getDescription());
				ps.setString(11, pipelineConfig.getVariable());
				ps.setString(12, pipelineConfig.getValue());
				ps.setString(13, pipelineConfig.getShortName());
				return ps;
			}

		}, holder);

		if(pipelineConfig.getId() == null) {
			Long newCommonConfigId = (long) holder.getKey().intValue();
			pipelineConfig.setId(newCommonConfigId);
		}
		
		return pipelineConfig;
	}
}

class PipelineConfigMapper implements RowMapper {

	@Override
	public PipelineConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		PipelineConfig pipelineConfig = new PipelineConfig();
		pipelineConfig.setProcess(rs.getString("process"));
		pipelineConfig.setPipelineCount(rs.getInt("count"));
		
		return pipelineConfig;
	}

}

class EditPipelineConfigMapper implements RowMapper {

	@Override
	public EditPipelineConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		EditPipelineConfig editPipelineConfig = new EditPipelineConfig();
		editPipelineConfig.setId(rs.getLong("id"));
		editPipelineConfig.setProcess(rs.getString("process"));
		editPipelineConfig.setVariable(rs.getString("variable"));
		editPipelineConfig.setValue(rs.getString("value"));
		editPipelineConfig.setActive(rs.getString("active"));
		editPipelineConfig.setVersion(rs.getInt("version"));
		editPipelineConfig.setDescription(rs.getString("description"));
		editPipelineConfig.setShortName(rs.getString("shortName"));
		
		return editPipelineConfig;
	}

}
