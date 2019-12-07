package com.gms.web.admin.service.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.domain.manage.BottleHistoryVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.mapper.manage.BottleMapper;

@Service
public class BottleServiceImpl implements BottleService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private BottleMapper bottleMapper;
	
	
	@Override
	public Map<String,Object> getBottleList(BottleVO param) {
		logger.info("****** getBottleList *****start===*");
		
		
		logger.info("****** getBottleList *****param.getSearchBottleId===*" + param.getSearchBottleId());
		logger.info("****** getBottleList *****param.getRowPerPage===*" + param.getRowPerPage());
		
		int currentPage = param.getCurrentPage();
		int ROW_PER_PAGE = param.getRowPerPage();
		
		int starPageNum =1;
		
		int lastPageNum = ROW_PER_PAGE;
		
		if(currentPage > (ROW_PER_PAGE/2)) {
			lastPageNum += (starPageNum-1);
		}
		
		int startRow = (currentPage-1) * ROW_PER_PAGE;
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		map.put("startRow", startRow);
		map.put("rowPerPage", ROW_PER_PAGE);	
		map.put("searchBottleId", param.getSearchBottleId());	
		
		if(param.getSearchGasId() != null) {
			map.put("searchGasId", param.getSearchGasId());
			logger.info("****** getBottleList *****searchGasId===*"+param.getSearchGasId());
		}
		
		if(param.getSearchChargeDt() != null) {
			map.put("searchChargeDt", param.getSearchChargeDt());
			logger.info("****** getBottleList *****getSearchChargeDt===*"+param.getSearchChargeDt());
		}		
		
		if(param.getSearchChargeDtFrom() != null) {
			map.put("searchChargeDtFrom", param.getSearchChargeDtFrom());
			logger.info("****** getBottleList *****getSearchChargeDtFrom===*"+param.getSearchChargeDtFrom());
		}
		
		if(param.getSearchChargeDtEnd() != null) {
			map.put("searchChargeDtEnd", param.getSearchChargeDtEnd());
			logger.info("****** getBottleList *****getSearchChargeDtEnd===*"+param.getSearchChargeDtEnd());
		}
		
		if(param.getSearchSalesYn() != null) {
			map.put("searchSalesYn", param.getSearchSalesYn());
			logger.info("****** getBottleList *****getSearchSalesYn===*"+param.getSearchSalesYn());
		}
		
		logger.info("****** getBottleList *****currentPage===*"+currentPage);
		
		
		int bottleCount = bottleMapper.selectBottleCount(map);
		
		logger.info("****** getBottleList.bottleCount *****===*"+bottleCount);
		
		//int lastPage = (int)(Math.ceil(bottleCount/ROW_PER_PAGE));
		int lastPage = (int)((double)bottleCount/ROW_PER_PAGE+0.95);
		
		logger.info("****** getBottleList.lastPage *****===*"+lastPage);
		
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<BottleVO> bottleList = bottleMapper.selectBottleList(map);
		
		logger.info("****** getBottleList.bottleList *****===*"+bottleList.size());
		
		resutlMap.put("list",  bottleList);
		
		resutlMap.put("currentPage", currentPage);
		resutlMap.put("lastPage", lastPage);
		resutlMap.put("startPageNum", starPageNum);
		resutlMap.put("lastPageNum", lastPageNum);
		resutlMap.put("totalCount", bottleCount);
		
		return resutlMap;
	}
	
	@Override
	public List<BottleVO> getBottleListToExcel(BottleVO param) {
		logger.info("****** getBottleListToExcel *****start===*");
		
		
		logger.info("****** getBottleListToExcel *****param.getSearchBottleId===*" + param.getSearchBottleId());
		logger.info("****** getBottleListToExcel *****param.getRowPerPage===*" + param.getRowPerPage());
		
		int currentPage = param.getCurrentPage();
		int ROW_PER_PAGE = param.getRowPerPage();
		
		int starPageNum =1;
		
		int lastPageNum = ROW_PER_PAGE;
		
		if(currentPage > (ROW_PER_PAGE/2)) {
			lastPageNum += (starPageNum-1);
		}
		
		int startRow = (currentPage-1) * ROW_PER_PAGE;
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		map.put("startRow", startRow);
		map.put("rowPerPage", ROW_PER_PAGE);	
		map.put("searchBottleId", param.getSearchBottleId());	
		
		if(param.getSearchGasId() != null) {
			map.put("searchGasId", param.getSearchGasId());
			logger.info("****** getBottleList *****searchGasId===*"+param.getSearchGasId());
		}
		
		if(param.getSearchChargeDt() != null) {
			map.put("searchChargeDt", param.getSearchChargeDt());
			logger.info("****** getBottleList *****getSearchChargeDt===*"+param.getSearchChargeDt());
		}		
		
		if(param.getSearchChargeDtFrom() != null) {
			map.put("searchChargeDtFrom", param.getSearchChargeDtFrom());
			logger.info("****** getBottleList *****getSearchChargeDtFrom===*"+param.getSearchChargeDtFrom());
		}
		
		if(param.getSearchChargeDtEnd() != null) {
			map.put("searchChargeDtEnd", param.getSearchChargeDtEnd());
			logger.info("****** getBottleListToExcel *****getSearchChargeDtEnd===*"+param.getSearchChargeDtEnd());
		}
		
		if(param.getSearchSalesYn() != null) {
			map.put("searchSalesYn", param.getSearchSalesYn());
			logger.info("****** getBottleListToExcel *****getSearchSalesYn===*"+param.getSearchSalesYn());
		}
		
		logger.info("****** getBottleListToExcel *****currentPage===*"+currentPage);
				
		
		List<BottleVO> bottleList = bottleMapper.selectBottleListToExcel(map);
		
		
		return bottleList;
	}
	
	@Override
	public List<BottleVO> getCustomerBottleList(Integer customerId) {
		return bottleMapper.selectCustomerBottleList(customerId);	
	}
	
	
	@Override
	public BottleVO getBottleDetail(String bottleId) {
		return bottleMapper.selectBottleDetail(bottleId);	
	}
	
	@Override
	public int getBottleCount(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBottleIdCheck(BottleVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBottleBarCdCheck(BottleVO param) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	@Transactional
	public int registerBottle(BottleVO param) {

		// 정보 등록
		logger.info("****** registerBottle.getBottleId()()) *****===*"+param.getBottleId());
		int result = 0;
		result =  bottleMapper.insertBottle(param);		
		if(result > 0 ) result = bottleMapper.insertBottleHistory(param.getBottleId());
		
		return result;
		
	}

	@Override
	@Transactional
	public int modifyBottle(BottleVO param) {
				
		// 정보 등록
		logger.info("****** modifyBottle.getBottleId()()) *****===*"+param.getBottleId());
		int result = 0;
		
		result =  bottleMapper.updateBottle(param);
		
		if(result > 0 ) result = bottleMapper.insertBottleHistory(param.getBottleId());
		
		return result;
	}
	
	@Override
	@Transactional
	public int changeBottleWorkCd(BottleVO param) {
		// 정보 등록
		logger.info("****** modifyBottle.getChBottleId()()) *****===*"+param.getChBottleId());
		int result = 0;
		result =  bottleMapper.updateBottleWorkCd(param);
		
		if(result > 0 ) result = bottleMapper.insertBottleHistory(param.getBottleId());
		
		return result;
	}

	@Override
	@Transactional
	public int deleteBottle(BottleVO param) {
		logger.info("****** deleteBottle *****===*"+param.getBottleId());
		
		int result = 0;
		result =   bottleMapper.deleteBottle(param);
		
		if(result > 0 ) result = bottleMapper.insertBottleHistory(param.getBottleId());
		
		return result;
		
	}

	@Override
	@Transactional
	public int deleteBottles(BottleVO param) {
		return bottleMapper.deleteBottles(param);
	}

	
	@Override
	public Map<String, Object> checkBottleIdDuplicate(BottleVO param) {		
			// 중복체크
			int count = bottleMapper.selectBottleIdCheck(param);
			// 결과 변수
			Map<String, Object> result = new HashMap<String, Object>();
			
			if(count > 0){
				result.put("result", "fail");
				result.put("message", "용기코드가 존재 합니다. 확인 후 입력해 주세요.");
				return result;
			}
			
			result.put("result", "success");
			
			return result;	
	}

	@Override
	public List<BottleHistoryVO> selectBottleHistoryList(String bottleId){
		
		return bottleMapper.selectBottleHistoryList(bottleId);		
	}
	
	
	private int insertBottleHistory(String bottleId) {
		// 정보 등록
		logger.info("****** registerBottle.getBottleId()()) *****===*" +bottleId);
		
		return bottleMapper.insertBottleHistory(bottleId);		
	}

	@Override
	public int modifyBottleOrder(BottleVO param) {
		
		int result = 0; 
				
		result = bottleMapper.updateBottleOrderId(param);	
		
		if(result > 0 ) result = insertBottleHistory(param.getBottleId());
		
		return result ;
	}

	@Override
	public List<BottleVO> getBottleDetails(BottleVO param) {
		
		List<BottleVO> list = (List<BottleVO>) bottleMapper.selectBottleDetails(param);	
		
		return list;
				
	}

	
	
}
