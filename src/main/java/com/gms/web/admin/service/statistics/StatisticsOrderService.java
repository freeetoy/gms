package com.gms.web.admin.service.statistics;

import java.util.List;

import com.gms.web.admin.domain.statistics.StatisticsOrderVO;

public interface StatisticsOrderService {

	public List<StatisticsOrderVO> getDailylStatisticsOrderList(StatisticsOrderVO param);	
	
	public List<StatisticsOrderVO> getMontlylStatisticsOrderList(StatisticsOrderVO param);	
	
	public int registerDailyStatisticsOrder();

	public int registerMonthlyStatisticsOrder();
}
