package com.gms.web.admin.service.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.domain.statistics.StatisticsProductVO;
import com.gms.web.admin.mapper.statistics.StatisticsProductMapper;

@Service
public class StatisticsProductServiceImpl implements StatisticsProductService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private StatisticsProductMapper statMapper;

	@Override
	public List<StatisticsProductVO>  getDailylStatisticsProductList(StatisticsProductVO param) {
		logger.debug("****** getDailylStatisticsProductList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();		

		map.put("searchProductId", param.getSearchProductId());	
		
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
			logger.debug("****** getDailylStatisticsProductList *****getSearchStatDt===*"+param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
			logger.info("****** getDailylStatisticsProductList *****getSearchStatDtFrom===*"+param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
			logger.info("****** getDailylStatisticsProductList *****getSearchStatDtEnd===*"+param.getSearchStatDtEnd());
		}	
				
		List<StatisticsProductVO> statList = statMapper.selectDailylStatisticsProductList(map);	
		
				
		return statList;
	}

	@Override
	public List<StatisticsProductVO>  getMontlylStatisticsProductList(StatisticsProductVO param) {
		logger.debug("****** getMonthlyStatisticsProductList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();		

		map.put("searchProductId", param.getSearchProductId());	
		
		
		if(param.getSearchStatDt() != null) {
			map.put("searchOrderDt", param.getSearchStatDt());
			logger.debug("****** getMonthlylStatisticsProductList *****getSearchStatDt===*"+param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchOrderDtFrom", param.getSearchStatDtFrom());
			logger.info("****** getMonthlylStatisticsProductList *****getSearchStatDtFrom===*"+param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchOrderDtEnd", param.getSearchStatDtEnd());
			logger.info("****** getMonthlylStatisticsProductList *****getSearchStatDtEnd===*"+param.getSearchStatDtEnd());
		}	
				
		List<StatisticsProductVO> statList = statMapper.selectMontlylStatisticsProductList(map);	
		
				
		return statList;
	}

	@Override
	public int registerDailyStatisticsProduct(StatisticsProductVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int registerMonthlyStatisticsProduct(StatisticsProductVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

}
