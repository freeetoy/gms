package com.gms.web.admin.controller.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.gms.web.admin.service.common.ScheduleService;
import com.gms.web.admin.service.statistics.StatisticsOrderService;

@Controller
public class ScheduleController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Scheduled(cron="* 0 10 * * *")
	private void scheduleDaily() { 
		
		//Daiyl 통계 데이타 등록
		//statOrderService.
		
		int result = scheduleService.registerDailyStatistics();		
		
	}
	/*
	
	//@Scheduled(cron="0 1 1 * * ")
	private void scheduleMonthly() { 
		logger.error("hello jeong-pro"); 
		
	}
*/	
}
