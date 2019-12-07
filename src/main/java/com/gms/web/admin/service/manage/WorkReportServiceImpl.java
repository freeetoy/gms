package com.gms.web.admin.service.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.OrderExtVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.mapper.manage.WorkReportMapper;

@Service
public class WorkReportServiceImpl implements WorkReportService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private WorkReportMapper workMapper;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private ProductService productService;
	
	@Override
	public List<WorkReportVO> getWorkReportList(WorkReportVO param) {

		String searchDt = param.getSearchDt();	
		
						
		if(param.getSearchDt() == null || param.getSearchDt().length() == 0) {						
			
			param.setSearchDt(DateUtils.getDate("yyyy/MM/dd"));
		}		
		logger.info("WorkReportServiceImpl getWorkReportList Work_Dt= "+ param.getSearchDt());
		
		return workMapper.selectWorkReportList(param);
	}

	@Override
	@Transactional
	public int registerWorkReport(WorkReportVO param) {
		logger.info("WorkReportServiceImpl registerWorkReport start ");
		int result = 0;
		try {
			
			logger.debug("WorkReportServiceImpl registerWorkReport orderId =" + param.getOrderId());
			logger.debug("WorkReportServiceImpl registerWorkReport bottleIds =" + param.getBottlesIds());
			
			//Order 정보가져오기
			OrderExtVO  orderInfo = orderService.getOrder(param.getOrderId());
			
			//Bottle 정보 가져오기
			BottleVO bottle = new BottleVO();
			if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				List<String> list = StringUtils.makeForeach(param.getBottlesIds(), ","); 		
				bottle.setBottList(list);
			}	
			
			List<BottleVO> bottleList = bottleService.getBottleDetails(bottle);			
		
			//Work_Report_Seq 가져오기
			int workReportSeq = getWorkReportSeq();
			logger.debug("WorkReportServiceImpl registerWorkReport workReportSeq =" + workReportSeq);
			//TB_Work_Reprot 등록
			param.setWorkReportSeq(workReportSeq);
			param.setUserId(param.getCreateId());
			param.setCustomerId(orderInfo.getOrder().getCustomerId());	
			
			List<String> list = null;
			
			if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				list = StringUtils.makeForeach(param.getBottlesIds(), ","); 		
				param.setBottList(list);
			}	
			logger.debug("WorkReportServiceImpl registerWorkReport bottleWorkCd =" + param.getBottleWorkCd() );
			if(param.getBottleWorkCd() == PropertyFactory.getProperty("common.bottle.status.0308")) {
				param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Full"));
			}else {
				param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
			}
			param.setOrderAmount(orderInfo.getOrder().getOrderTotalAmount());			
			logger.debug("WorkReportServiceImpl registerWorkReport orderAmount =" + orderInfo.getOrder().getOrderTotalAmount() );
			result = workMapper.insertWorkReport(param);
			
			
			//TB_Work_Bottle 등록	
			
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();
			
			List<OrderProductVO> orderProductList = orderInfo.getOrderProduct();
			OrderProductVO tempOrderProduct = null;
			
			WorkBottleVO workBottle = null;
			BottleVO tempBottle = null;
			
			for(int i = 0; i < bottleList.size() ; i++) {		
				
				tempBottle = bottleList.get(i);
				
				workBottle = new WorkBottleVO();
				
				logger.debug("WorkReportServiceImpl registerWorkReport tempBottle.getBottleId() =" + tempBottle.getBottleId() );
				workBottle.setBottleId(tempBottle.getBottleId());
				workBottle.setBottleBarCd(tempBottle.getBottleBarCd());
				workBottle.setWorkReportSeq(workReportSeq);
				workBottle.setCreateId(param.getCreateId());
				workBottle.setCustomerId(orderInfo.getOrder().getCustomerId());
				workBottle.setBottleWorkCd(param.getBottleWorkCd());	
				
				logger.debug("WorkReportServiceImpl registerWorkReport *** workBottle.getWorkReportSeq() =" + workBottle.getWorkReportSeq() );
				logger.debug("WorkReportServiceImpl registerWorkReport tempBottle.getBottleBarCd() =" + tempBottle.getBottleBarCd() );
				logger.debug("WorkReportServiceImpl registerWorkReport orderInfo.getOrder().getCustomerId() =" + orderInfo.getOrder().getCustomerId());
				logger.debug("WorkReportServiceImpl registerWorkReport param.getBottleWorkCd() =" + param.getBottleWorkCd() );
				
				for(int j=0; j< orderInfo.getOrderProduct().size() ; j++) {
					tempOrderProduct = orderProductList.get(j);
					
					logger.debug("WorkReportServiceImpl registerWorkReport tempBottle.getGasId() =" + tempBottle.getGasId() +" ** tempOrderProduct.getGasId() == "+ tempOrderProduct.getGasId() );
					logger.debug("WorkReportServiceImpl registerWorkReport tempBottle.getBottleCapa =" + tempBottle.getBottleCapa() +" ** tempOrderProduct.getBottleCapa == "+ tempOrderProduct.getProductCapa() );
					
					if(tempBottle.getGasId() == tempOrderProduct.getGasId() 
							&& (tempBottle.getBottleCapa()).equals(tempOrderProduct.getProductCapa()) ){
						
						workBottle.setGasId(tempBottle.getGasId());
						workBottle.setProductId(tempOrderProduct.getProductId());
						workBottle.setProductPriceSeq(tempOrderProduct.getOrderProductSeq());						
						
						//TB_Order_Product 설정						
						orderInfo.getOrderProduct().get(j).setBottleId(tempBottle.getBottleId());
						orderInfo.getOrderProduct().get(j).setBottleWorkCd(param.getBottleWorkCd());
						orderInfo.getOrderProduct().get(j).setBottleType(param.getBottleType());
						
						
						//TB_Bottle 설정
						/*
						bottleList.get(i).setOrderId(param.getOrderId());
						bottleList.get(i).setOrderProductSeq(tempOrderProduct.getOrderProductSeq());
						bottleList.get(i).setCustomerId(orderInfo.getOrder().getCustomerId());
						*/
						//TB_Order TB_Order_Product 업데이드	;	
						result = orderService.modifyOrderComplete(tempOrderProduct);
						
						//TB_Bottle_ 업데이트
						//result = bottleService.modifyBottleOrder(bottleList.get(i));
					}
							//if(list[i] == tempBottle.getBottleId())
				}
			
				workBottleList.add(workBottle);							
			}
			orderInfo.getOrder().setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.04"));
			orderInfo.getOrder().setChOrderId(orderInfo.getOrder().getOrderId());
			orderInfo.getOrder().setUpdateId(param.getUpdateId());
			
			result = orderService.changeOrderProcessCd(orderInfo.getOrder());
						
			result = workMapper.insertWorkBottles(workBottleList);			
		
			
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
	public List<WorkBottleVO> getWorkBottleList(Integer workReportSeq) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int registerWorkBottle(WorkBottleVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int registerWorkBottleList(List<WorkBottleVO> param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWorkReportSeq() {
		return  workMapper.selectWorkReportSeq();
	}

}
