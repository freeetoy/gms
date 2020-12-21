package com.gms.web.admin.mapper.statistics;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.statistics.StatisticsAgencyVO;

@Mapper
public interface StatisticsAgencyMapper {
	
	public List<StatisticsAgencyVO> selectDailylStatisticsAgencyList(Map<String, Object> map);	
	
	public List<StatisticsAgencyVO> selectMontlylStatisticsAgencyList(Map<String, Object> map);	
		
	public int inserDailyStatisticsAgncy();

	public int inserMonthlyStatisticsAgency();

}
