package com.gms.web.admin.service.statistics;

import java.util.List;

import com.gms.web.admin.domain.statistics.StatisticsAgencyVO;

public interface StatisticsAgencyService {

	public List<StatisticsAgencyVO> getDailylStatisticsAgencyList(StatisticsAgencyVO param);	
	
	public List<StatisticsAgencyVO> getMontlylStatisticsAgencyList(StatisticsAgencyVO param);	
	
	public int registerDailyStatisticsAgency();

	public int registerMonthlyStatisticsAgency();

}
