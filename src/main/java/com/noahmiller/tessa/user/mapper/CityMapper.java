package com.noahmiller.tessa.user.mapper;

import com.noahmiller.tessa.user.entity_old.City;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CityMapper {
    City selectById(Long id);
    
    List<City> selectAll();
    
    List<City> selectByProvinceId(Long provinceId);
    
    City selectByName(String name);
    
    void insert(City city);
    
    int update(City city);
    
    int deleteById(Long id);
}
