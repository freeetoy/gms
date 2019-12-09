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
	
	@Scheduled(cron="0 5 9 * * *")
	private void scheduleDaily() { 
		
		//Daiyl 통계 데이타 등록
		//statOrderService.
		logger.info("************* ScheduleController scheduleDaily Start *************");
		int result = scheduleService.registerDailyStatistics();		
		
		logger.info("******************* ScheduleController scheduleDaily End*************** ");
		
	}
	
	 
	@Scheduled(cron="0 7 10 6 * *")
	private void scheduleMonthly() { 
		logger.info("************* ScheduleController scheduleMonthly Start *************");
		int result = scheduleService.registerMonthlyStatistics();	
		logger.info("************* ScheduleController scheduleMonthly end *************");
		
	}
	
}
