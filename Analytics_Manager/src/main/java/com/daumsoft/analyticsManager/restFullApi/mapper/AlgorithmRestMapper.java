package com.daumsoft.analyticsManager.restFullApi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlgorithmRestMapper {

	List<Map<String, Object>> algorithms();

	Map<String, Object> algorithm(Integer id);

	List<Map<String, Object>> searchAlgorithms(String searchValue);   


}