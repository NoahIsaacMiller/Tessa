package com.noahmiller.tessa.user.handler;

import com.noahmiller.tessa.user.entity_old.City;
import com.noahmiller.tessa.user.entity_old.UserStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(UserStatus.class)
@MappedJdbcTypes(JdbcType.BIGINT)
public class CityTypeHandler extends BaseTypeHandler<City> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, City parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, parameter.getId());
    }

    @Override
    public City getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Long id = rs.getObject(columnName, Long.class);
        if (id == null) {
            return null;
        } else {
            return new City(id, null, null);
        }
    }

    @Override
    public City getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Long id = rs.getObject(columnIndex, Long.class);
        if (id == null) {
            return null;
        } else {
            return new City(id, null, null);
        }

    }

    @Override
    public City getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Long id = cs.getObject(columnIndex, Long.class);
        if (id == null) {
            return null;
        } else {
            return new City(id, null, null);
        }
    }
}

