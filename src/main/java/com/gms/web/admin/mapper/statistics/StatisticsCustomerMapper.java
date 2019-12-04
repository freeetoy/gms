package com.gms.web.admin.mapper.statistics;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.statistics.StatisticsCustomerVO;

@Mapper
public interface StatisticsCustomerMapper {
	
	public List<StatisticsCustomerVO> selectDailylStatisticsCustomerList(Map<String, Object> map);	
	
	public List<StatisticsCustomerVO> selectMontlylStatisticsCustomerList(Map<String, Object> map);	
	
	public int inserDailyStatisticsCustomer();

	public int inserMonthlyStatisticsCustomer();

}
