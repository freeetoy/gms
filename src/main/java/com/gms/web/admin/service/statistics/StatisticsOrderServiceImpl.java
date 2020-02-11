package com.gms.web.admin.service.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.domain.statistics.StatisticsOrderVO;
import com.gms.web.admin.mapper.statistics.StatisticsOrderMapper;

@Service
public class StatisticsOrderServiceImpl implements StatisticsOrderService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private StatisticsOrderMapper statMapper;
	
	@Override
	public List<StatisticsOrderVO> getDailylStatisticsOrderList(StatisticsOrderVO param) {
		logger.info("****** getDailylStatisticsOrderList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
			logger.debug("****** getDailylStatisticsOrderList *****getSearchStatDt===*"+param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
			logger.debug("****** getDailylStatisticsOrderList *****getSearchStatDtFrom===*"+param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
			logger.debug("****** getDailylStatisticsOrderList *****getSearchStatDtEnd===*"+param.getSearchStatDtEnd());
		}	
				
		List<StatisticsOrderVO> statList = statMapper.selectDailylStatisticsOrderList(map);			
				
		return statList;
	}

	@Override
	public List<StatisticsOrderVO> getMontlylStatisticsOrderList(StatisticsOrderVO param) {
		logger.info("****** getMonthlyStatisticsOrderList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
			logger.debug("****** getMonthlylStatisticsOrderList *****getSearchStatDt===*"+param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
			logger.debug("****** getMonthlylStatisticsOrderList *****getSearchStatDtFrom===*"+param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
			logger.debug("****** getMonthlylStatisticsOrderList *****getSearchStatDtEnd===*"+param.getSearchStatDtEnd());
		}	
				
		List<StatisticsOrderVO> statList = statMapper.selectMontlylStatisticsOrderList(map);	
						
		return statList;
	}

	@Override
	public int registerDailyStatisticsOrder() {
		// TODO Auto-generated method stub
		return statMapper.inserDailyStatisticsOrder();
	}

	@Override
	public int registerMonthlyStatisticsOrder() {
		// TODO Auto-generated method stub
		return statMapper.inserMonthlyStatisticsOrder();
	}

}
