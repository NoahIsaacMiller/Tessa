package com.noahmiller.tessa.user.handler;

import com.noahmiller.tessa.user.entity.UserStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(UserStatus.class) // 声明处理的枚举类型
@MappedJdbcTypes(JdbcType.VARCHAR) // 声明对应的数据库类型
@Component
public class UserStatusTypeHandler extends BaseTypeHandler<UserStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UserStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue()); // 将枚举值写入数据库
    }

    @Override
    public UserStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : UserStatus.fromValue(value); // 从数据库读取值并转换为枚举
    }

    @Override
    public UserStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {

        return rs.getString(columnIndex) == null ? null : UserStatus.fromValue(rs.getString(columnIndex));
    }

    @Override
    public UserStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getString(columnIndex) == null ? null : UserStatus.fromValue(cs.getString(columnIndex));
    }
}