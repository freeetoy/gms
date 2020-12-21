package com.gms.web.admin.service.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.domain.statistics.StatisticsAgencyVO;
import com.gms.web.admin.mapper.statistics.StatisticsAgencyMapper;

@Service
public class StatisticsAgencyServiceImpl implements StatisticsAgencyService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private StatisticsAgencyMapper statMapper;
	
	@Override
	public List<StatisticsAgencyVO> getDailylStatisticsAgencyList(StatisticsAgencyVO param) {
		logger.info("****** getDailylStatisticsCustomerList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();		

		//map.put("searchCustomerId", param.getSearchCustomerId());	
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
			logger.debug("****** getDailylStatisticsCustomerList *****getSearchStatDt===*"+param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
			logger.debug("****** getDailylStatisticsCustomerList *****getSearchStatDtFrom===*"+param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
			logger.debug("****** getDailylStatisticsCustomerList *****getSearchStatDtEnd===*"+param.getSearchStatDtEnd());
		}	
				
		List<StatisticsAgencyVO> statList = statMapper.selectDailylStatisticsAgencyList(map);	
		
				
		return statList;
	}

	@Override
	public List<StatisticsAgencyVO> getMontlylStatisticsAgencyList(StatisticsAgencyVO param) {

		logger.info("****** getMonthlyStatisticsCustomerList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();		

		//map.put("searchCustomerId", param.getSearchCustomerId());			
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
			logger.debug("****** getMonthlylStatisticsCustomerList *****getSearchStatDt===*"+param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
			logger.debug("****** getMonthlylStatisticsCustomerList *****getSearchStatDtFrom===*"+param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
			logger.debug("****** getMonthlylStatisticsCustomerList *****getSearchStatDtEnd===*"+param.getSearchStatDtEnd());
		}	
				
		List<StatisticsAgencyVO> statList = statMapper.selectMontlylStatisticsAgencyList(map);	
		
				
		return statList;
	}

	@Override
	public int registerDailyStatisticsAgency() {
		// TODO Auto-generated method stub
		return statMapper.inserDailyStatisticsAgncy();
	}

	@Override
	public int registerMonthlyStatisticsAgency() {
		// TODO Auto-generated method stub
		return statMapper.inserMonthlyStatisticsAgency();
	}

	

}
