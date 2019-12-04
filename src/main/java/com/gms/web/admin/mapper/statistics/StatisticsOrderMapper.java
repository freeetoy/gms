package com.gms.web.admin.mapper.statistics;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.statistics.StatisticsOrderVO;

@Mapper
public interface StatisticsOrderMapper {

	public List<StatisticsOrderVO> selectDailylStatisticsOrderList(Map<String, Object> map);	
	
	public List<StatisticsOrderVO> selectMontlylStatisticsOrderList(Map<String, Object> map);	
	
	public int inserDailyStatisticsOrder();

	public int inserMonthlyStatisticsOrder();
}
