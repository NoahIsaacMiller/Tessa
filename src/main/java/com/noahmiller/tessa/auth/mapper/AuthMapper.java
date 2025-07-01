package com.noahmiller.tessa.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuthMapper {
    @Select("SELECT COUNT(1) FROM users WHERE email = #{email}")
    boolean existsByEmail(String email);
}
