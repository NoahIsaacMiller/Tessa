package com.noahmiller.tessa.user.handler;

import com.noahmiller.tessa.user.entity_old.Gender;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(Gender.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
@Component
public class GenderHandler extends BaseTypeHandler<Gender> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Gender parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public Gender getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String genderValue = rs.getString(columnName);
        return rs.wasNull() ? Gender.UNKNOWN : Gender.fromValue(genderValue);
    }

    @Override
    public Gender getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String genderValue = rs.getString(columnIndex);
        return rs.wasNull() ? Gender.UNKNOWN : Gender.fromValue(genderValue);
    }

    @Override
    public Gender getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String genderValue = cs.getString(columnIndex);
        return cs.wasNull() ? Gender.UNKNOWN : Gender.fromValue(genderValue);
    }
}