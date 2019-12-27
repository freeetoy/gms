package com.gms.web.admin.service.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.manage.BottleHistoryVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.ProductPriceVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.domain.manage.ProductVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.mapper.manage.BottleMapper;

@Service
public class BottleServiceImpl implements BottleService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private BottleMapper bottleMapper;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private WorkReportService workService;
	
	@Override
	public Map<String,Object> getBottleList(BottleVO param) {
		logger.info("****** getBottleList *****start===*");
		
		
		logger.debug("****** getBottleList *****param.getSearchBottleId===*" + param.getSearchBottleId());
		logger.debug("****** getBottleList *****param.getRowPerPage===*" + param.getRowPerPage());
		
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
			logger.debug("****** getBottleList *****searchGasId===*"+param.getSearchGasId());
		}
		

		if(param.getSearchProductId() != null  ) {			
			map.put("searchProductId", param.getSearchProductId());
		}
		
		if(param.getSearchChargeDt() != null) {
			map.put("searchChargeDt", param.getSearchChargeDt());
			logger.debug("****** getBottleList *****getSearchChargeDt===*"+param.getSearchChargeDt());
		}		
		
		if(param.getSearchChargeDtFrom() != null) {
			map.put("searchChargeDtFrom", param.getSearchChargeDtFrom());
			logger.debug("****** getBottleList *****getSearchChargeDtFrom===*"+param.getSearchChargeDtFrom());
		}
		
		if(param.getSearchChargeDtEnd() != null) {
			map.put("searchChargeDtEnd", param.getSearchChargeDtEnd());
			logger.debug("****** getBottleList *****getSearchChargeDtEnd===*"+param.getSearchChargeDtEnd());
		}
		
		if(param.getSearchSalesYn() != null) {
			map.put("searchSalesYn", param.getSearchSalesYn());
			map.put("bottleWorkCd", param.getBottleWorkCd());
			logger.debug("****** getBottleList *****getSearchSalesYn===*"+param.getSearchSalesYn());
		}
		
		logger.debug("****** getBottleList *****currentPage===*"+currentPage);
		
		
		int bottleCount = bottleMapper.selectBottleCount(map);
		
		logger.debug("****** getBottleList.bottleCount *****===*"+bottleCount);
		
		//int lastPage = (int)(Math.ceil(bottleCount/ROW_PER_PAGE));
		int lastPage = (int)((double)bottleCount/ROW_PER_PAGE+0.95);
		
		logger.debug("****** getBottleList.lastPage *****===*"+lastPage);
		
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		if(lastPageNum ==0) lastPageNum=1;
		
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<BottleVO> bottleList = bottleMapper.selectBottleList(map);
		
		logger.debug("****** getBottleList.bottleList *****===*"+bottleList.size());
		
		resutlMap.put("list",  bottleList);
		
		resutlMap.put("currentPage", currentPage);
		resutlMap.put("lastPage", lastPage);
		resutlMap.put("startPageNum", starPageNum);
		resutlMap.put("lastPageNum", lastPageNum);
		resutlMap.put("totalCount", bottleCount);
		
		logger.debug("BottleService startPageNum "+ starPageNum);
		logger.debug("BottleService lastPageNum "+ lastPageNum);
		logger.debug("BottleService lastPage "+ lastPage);
		
		
		return resutlMap;
	}
	
	@Override
	public List<BottleVO> getBottleListToExcel(BottleVO param) {
		logger.info("****** getBottleListToExcel *****start===*");
		
		
		logger.debug("****** getBottleListToExcel *****param.getMenuType===*" + param.getMenuType());		
		
		Map<String, Object> map = new HashMap<String, Object>();			
		
		if(param.getSearchGasId() != null) {
			map.put("searchGasId", param.getSearchGasId());
			logger.debug("****** getBottleList *****searchGasId===*"+param.getSearchGasId());
		}
		
		if(param.getSearchProductId() != null  ) {			
			map.put("searchProductId", param.getSearchProductId());
		}
		
		if(param.getSearchChargeDt() != null) {
			map.put("searchChargeDt", param.getSearchChargeDt());
			logger.debug("****** getBottleList *****getSearchChargeDt===*"+param.getSearchChargeDt());
		}else {
			if(param.getMenuType()==2) {		// 용기충전
				// Date 로 구하기
			    SimpleDateFormat fm1 = new SimpleDateFormat("yyyy/MM/dd");
			    String fromDate = fm1.format(new Date());
			    logger.info("현재시간 년월일 = " + fromDate);

			    Calendar cal = Calendar.getInstance();;
			    cal.setTime(new Date());
			    cal.add(Calendar.DAY_OF_YEAR, 7); // 하루를 더한다.
			    	    
			    String endDate = fm1.format(cal.getTime());
			    logger.info("현재시간 년월일 = " + endDate);
			    
			    String searchChargeDt = fromDate+" - "+endDate;
			    
			    param.setSearchChargeDt(searchChargeDt);
			    map.put("searchChargeDt", param.getSearchChargeDt());
				
				param.setSearchChargeDtFrom(fromDate);
				param.setSearchChargeDtEnd(endDate); 
			}
		}
		
		if(param.getSearchChargeDtFrom() != null) {
			map.put("searchChargeDtFrom", param.getSearchChargeDtFrom());
			logger.debug("****** getBottleList *****getSearchChargeDtFrom===*"+param.getSearchChargeDtFrom());
		}
		
		if(param.getSearchChargeDtEnd() != null) {
			map.put("searchChargeDtEnd", param.getSearchChargeDtEnd());
			logger.debug("****** getBottleListToExcel *****getSearchChargeDtEnd===*"+param.getSearchChargeDtEnd());
		}
		
		if(param.getSearchSalesYn() != null) {
			map.put("searchSalesYn", param.getSearchSalesYn());
			map.put("bottleWorkCd", param.getBottleWorkCd());
			logger.debug("****** getBottleListToExcel *****getSearchSalesYn===*"+param.getSearchSalesYn());
		}
		
		if(param.getMenuType()==3 || param.getMenuType()==3) {			//용기 판매/대여 메뉴
			param.setSearchSalesYn("Y");
			map.put("searchSalesYn", param.getSearchSalesYn());
			map.put("bottleWorkCd", param.getBottleWorkCd());
		}
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
		/*
		ProductPriceVO productPrice = new ProductPriceVO();
		productPrice.setProductId(param.getProductId());
		productPrice.setProductPriceSeq(param.getProductPriceSeq());
		
		ProductPriceVO productPrice1 = productService.getProductPriceDetails(productPrice);
		
		logger.debug("****** registerBottle.botteLCapa()()) *****===*"+productPrice1.getProductCapa());
		*/
		ProductTotalVO product = productService.getBottleGasCapa(param);
		
		param.setBottleCapa(product.getProductCapa());
		param.setGasId(product.getGasId());		
		
		param.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0388"));
		param.setBottleWorkId(param.getCreateId());
		param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
		//param.setBottleCapa(productPrice1.getProductCapa());
		
		result =  bottleMapper.insertBottle(param);		
		if(result > 0 ) result = bottleMapper.insertBottleHistory(param);
		
		return result;
		
	}

	
	@Override
	public int registerBottles(List<BottleVO> param) {

		int result = 0;
		result =  bottleMapper.insertBottles(param);	
		if(result > 0 ) result = bottleMapper.insertBottleHistorys(param);
		return result;
	}
	
	
	@Override
	public int modifyBottle(BottleVO param) {
				
		// 정보 등록
		logger.debug("****** modifyBottle.getBottleId()()) *****===*"+param.getBottleId());
		int result = 0;
				
		ProductTotalVO product = productService.getBottleGasCapa(param);
		
		param.setBottleCapa(product.getProductCapa());
		param.setGasId(product.getGasId());		
		
		result =  bottleMapper.updateBottle(param);
		
		//if(result > 0 ) result = bottleMapper.insertBottleHistory(param.getBottleId());
		
		return result;
	}
	
	@Override
	@Transactional
	public int changeBottleWorkCd(BottleVO param) {
		// 정보 등록
		logger.debug("****** modifyBottle.getChBottleId()()) *****===*"+param.getChBottleId());
		int result = 0;
		result =  bottleMapper.updateBottleWorkCd(param);	
		
		
		param.setBottleId(param.getChBottleId());
		
		if(result > 0 ) result = bottleMapper.insertBottleHistory(param);
		
		//TODO TB_Work_Report 추가
		param = getBottleDetail(param.getChBottleId());
		
		// TB_Work_Report & TB_Work_Bottle 등록
		WorkReportVO workReport = new WorkReportVO();
		
		//Work_Report_Seq 가져오기
		//int workReportSeq = workService.getWorkReportSeq();

		List<BottleVO> bottleList = new ArrayList<BottleVO>();
		
		bottleList.add(param );
		
		//workReport.setWorkReportSeq(workReportSeq);
		workReport.setBottleWorkCd(param.getBottleWorkCd());
		workReport.setBottleType(param.getBottleType());
		workReport.setUserId(param.getCreateId());
		workReport.setCreateId(param.getCreateId());
		workReport.setBottlesIds(param.getBottleIds());		
		workReport.setCustomerId(param.getCustomerId());
		
		
		result = workService.registerWorkReportByBottle(workReport, bottleList);
		if(result <= 0) return result;
		
		return result;
	}

	@Override
	@Transactional
	public int changeBottlesWorkCd(BottleVO param) {
		// TODO Auto-generated method stub
		int result = 0;
		
		logger.debug("BottleServiceImpl getBottlesId "+ param.getBottleIds());
		logger.debug("BottleServiceImpl bottleWorkdId "+ param.getBottleWorkId());
		logger.debug("BottleServiceImpl bottleWorkCd "+ param.getBottleWorkCd());
		logger.debug("BottleServiceImpl customerId "+ param.getCustomerId());
		logger.debug("BottleServiceImpl bottleType "+ param.getBottleType());
		
		try {		
			List<String> list = null;
			
			if(param.getBottleIds()!=null && param.getBottleIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				list = StringUtils.makeForeach(param.getBottleIds(), ","); 		
				param.setBottList(list);
			}				
			
			if(param.getBottleType() == null) {
				if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0305")) 
						|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0306"))
						|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0307")) 
						|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0308")) )
					param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Full"));
				else
					param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
			}
			
			List<BottleVO> bottleList = getBottleDetails(param);
			
			// TB_Work_Report & TB_Work_Bottle 등록
			WorkReportVO workReport = new WorkReportVO();
			
			//Work_Report_Seq 가져오기
			//int workReportSeq = workService.getWorkReportSeq();
			
			//workReport.setWorkReportSeq(workReportSeq);
			workReport.setBottleWorkCd(param.getBottleWorkCd());
			workReport.setBottleType(param.getBottleType());
			workReport.setUserId(param.getCreateId());
			workReport.setCreateId(param.getCreateId());
			workReport.setBottlesIds(param.getBottleIds());		
			workReport.setCustomerId(param.getCustomerId());
			//workReport.set
			
			//logger.debug("WorkReportServiceImpl registerWorkReport workReportSeq =" + workReportSeq);
			
			result = workService.registerWorkReportByBottle(workReport, bottleList);
			if(result <= 0) return result;
			
			result =  bottleMapper.updateBottlesWorkCd(param);
			
			if(result > 0 ) result = bottleMapper.insertBottleHistorys(bottleList);
		
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
		
		return result;
	}

	
	
	@Override
	@Transactional
	public int deleteBottle(BottleVO param) {
		logger.info("****** deleteBottle *****===*"+param.getBottleId());
		
		int result = 0;
		result =   bottleMapper.deleteBottle(param);
		
		if(result > 0 ) result = bottleMapper.insertBottleHistory(param);
		
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
		logger.debug("****** registerBottle.insertBottleHistory()()) *****===*" +bottleId);
		
		BottleVO bottle = bottleMapper.selectBottleDetail(bottleId);
		
		//TODO bottleId만 넘겨줘도 될듯..
		return bottleMapper.insertBottleHistory(bottle);		
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
