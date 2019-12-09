package com.gms.web.admin.service.manage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.gms.web.admin.domain.manage.ProductTotalVO;
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
						workBottle.setProductPriceSeq(tempOrderProduct.getProductPriceSeq());						
						
						//TB_Order_Product 설정						
						orderInfo.getOrderProduct().get(j).setBottleId(tempBottle.getBottleId());
						orderInfo.getOrderProduct().get(j).setBottleWorkCd(param.getBottleWorkCd());
						orderInfo.getOrderProduct().get(j).setBottleType(param.getBottleType());						
						
						
						result = bottleService.modifyBottleOrder(tempBottle);
					
					}
						
				}
			
				workBottleList.add(workBottle);							
			}
			orderInfo.getOrder().setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.04"));
			orderInfo.getOrder().setChOrderId(orderInfo.getOrder().getOrderId());
			orderInfo.getOrder().setUpdateId(param.getUpdateId());
			Date today = new Date();
			orderInfo.getOrder().setOrderDeliveryDt(today);
			result = orderService.changeOrderProcessCd(orderInfo.getOrder());
						
			result = workMapper.insertWorkReport(param);			
			
			result = workMapper.insertWorkBottles(workBottleList);			
		
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			result = 0;
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			result = 0;
			e.printStackTrace();
		}
		return result;
	}

	@Override
	@Transactional
	public int registerWorkReportNoOrder(WorkReportVO param) {
		logger.info("WorkReportServiceImpl registerWorkReport start ");
		int result = 0;
		try {			
			
			logger.debug("WorkReportServiceImpl registerWorkReportNoOrder bottleIds =" + param.getBottlesIds());
			
			//Bottle 정보 가져오기
			BottleVO bottle = new BottleVO();
			
			List<String> list = null;
			if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				list = StringUtils.makeForeach(param.getBottlesIds(), ","); 		
				bottle.setBottList(list);
				param.setBottList(list);
			}	
			
			List<BottleVO> bottleList = bottleService.getBottleDetails(bottle);			
		
			//Work_Report_Seq 가져오기
			int workReportSeq = getWorkReportSeq();
			
			logger.debug("WorkReportServiceImpl registerWorkReportNoOrder workReportSeq =" + workReportSeq);
			//TB_Work_Reprot 등록
			param.setWorkReportSeq(workReportSeq);
			param.setUserId(param.getCreateId());
			
			logger.debug("WorkReportServiceImpl registerWorkReportNoOrder bottleWorkCd =" + param.getBottleWorkCd() );
			
			if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0308")) ) {
				param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Full"));
			}else {
				param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
			}
					
			
			//TB_Work_Bottle 등록	
			
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();
			
			List<OrderProductVO> orderProductList = new ArrayList<OrderProductVO>();
			
			OrderVO order = new OrderVO();
			
			int orderId = orderService.getOrderId();
			
			order.setOrderId(orderId);
			order.setCreateId(param.getCreateId());
			order.setUpdateId(param.getCreateId());
			order.setCustomerId(param.getCustomerId());
			order.setMemberCompSeq(1);
			order.setOrderTypeCd(PropertyFactory.getProperty("common.code.order.type.01"));
			
			
			Calendar cal = Calendar.getInstance();
			int amPm = cal.get(Calendar.AM_PM);
			order.setDeliveryReqDt(cal.getTime());
			logger.debug("WorkReportServiceImpl registerWorkReportNoOrder cal.getTime() =" + order.getDeliveryReqDt());
			
			if (amPm == 1 ) order.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.PM"));
			else  order.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.AM"));
			logger.debug("WorkReportServiceImpl registerWorkReportNoOrder AMPM =" + order.getDeliveryReqAmpm());			
			
			order.setOrderEtc("현장주문");
			order.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.04"));
			order.setSalesId(param.getCreateId());
			order.setOrderDeliveryDt(cal.getTime());
			
			OrderProductVO tempOrderProduct = null;
			
			WorkBottleVO workBottle = null;
			BottleVO tempBottle = null;
			ProductTotalVO tempProductTotal = null;
			
			//용기 정보를 이횽해 상품 정보 가져옴
			
			int orderProductSeq = 1;
			int orderCount = 0;
			int orderTotalAmount = 0;
			
			int tempGasId=0;
			String tempCapa = "";
			
			String orderProductNm = "";
			String orderProductCapa = "";
			
			for(int i = 0; i < bottleList.size() ; i++) {		
				
				tempBottle = bottleList.get(i);
				
				workBottle = new WorkBottleVO();
				
				tempOrderProduct = new OrderProductVO();
				
				tempProductTotal = productService.getBottleGasCapa(tempBottle);				
				
				tempOrderProduct.setProductId(tempProductTotal.getProductId());
				tempOrderProduct.setProductPriceSeq(tempProductTotal.getProductPriceSeq());
				tempOrderProduct.setOrderId(orderId);
				tempOrderProduct.setBottleChangeYn("N");
				tempOrderProduct.setOrderProductEtc("");
				
				//tempOrderProduct.setBottleWorkCd(param.getBottleWorkCd());
				//tempOrderProduct.setBottleType(param.getBottleType());
				
				// TB_Order Product_Nm, Product_Capa 초기값
				if(i==0) {
					orderProductNm = tempProductTotal.getProductNm();
					orderProductCapa = tempProductTotal.getProductCapa();
				}
				
				//TODO
				//Order_Count ,Order_Amount,				
				
				if(i > 0) {
					
					//이전 것고 비교
					if(tempBottle.getGasId() == tempGasId
							&& (tempBottle.getBottleCapa()).equals(tempCapa) ){
						tempOrderProduct.setOrderProductSeq(orderProductSeq);
						int iTemp = i-1;
						orderProductList.remove(iTemp);
						orderCount++;
						tempOrderProduct.setOrderCount(orderCount);
						tempOrderProduct.setOrderAmount(orderCount*tempProductTotal.getProductPrice());
					}else {
						tempOrderProduct.setOrderCount(1);
						tempOrderProduct.setOrderAmount(tempProductTotal.getProductPrice());
						tempOrderProduct.setOrderProductSeq(orderProductSeq++);
					}					
					
				}else {
					tempOrderProduct.setOrderProductSeq(orderProductSeq++);
					tempOrderProduct.setOrderCount(1);
					tempOrderProduct.setOrderAmount(tempProductTotal.getProductPrice());
				}
				
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder tempBottle.getBottleId() =" + tempBottle.getBottleId() );
				
				workBottle.setBottleId(tempBottle.getBottleId());
				workBottle.setBottleBarCd(tempBottle.getBottleBarCd());
				workBottle.setWorkReportSeq(workReportSeq);
				workBottle.setCreateId(param.getCreateId());
				workBottle.setCustomerId(param.getCustomerId());
				workBottle.setBottleWorkCd(param.getBottleWorkCd());
				workBottle.setGasId(tempBottle.getGasId());
				workBottle.setProductId(tempProductTotal.getProductId());
				workBottle.setProductPriceSeq(tempProductTotal.getProductPriceSeq());	
				workBottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.Full"));
								
				tempBottle.setBottleWorkCd(param.getBottleWorkCd());
				tempBottle.setBottleWorkId(param.getCreateId());
				tempBottle.setCustomerId(param.getCustomerId());
				tempBottle.setBottleType(workBottle.getBottleType());
				
				
				result = bottleService.modifyBottleOrder(tempBottle);
				
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder *** workBottle.getWorkReportSeq() =" + workBottle.getWorkReportSeq() );
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder tempBottle.getBottleBarCd() =" + tempBottle.getBottleBarCd() );
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder orderInfo.getOrder().getCustomerId() =" + param.getCustomerId());
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder param.getBottleWorkCd() =" + param.getBottleWorkCd() );
				
				
				tempGasId = tempBottle.getGasId();
				tempCapa = tempBottle.getBottleCapa();
				
				orderProductList.add(tempOrderProduct);
				workBottleList.add(workBottle);							
			}
			
			if( bottleList.size() > 1) {
				orderProductNm = orderProductNm + " 외 " + (bottleList.size()-1);
				orderProductCapa = orderProductCapa + " 외" + (bottleList.size()-1);
			}
			order.setOrderProductNm(orderProductNm);
			order.setOrderProductCapa(orderProductCapa);
			
			for(int k=0; k < orderProductList.size() ; k++) { 
				orderTotalAmount += orderProductList.get(k).getOrderAmount();
			}
			order.setOrderTotalAmount(orderTotalAmount);
			
			//TB_Order 등록
			//result = orderService.registerOrder(request, param)(orderInfo.getOrder());
			// TB_Order_Product			등록
			result  = orderService.registerOrderAndProduct(order, orderProductList);
			
			param.setOrderId(orderId);
			param.setOrderAmount(orderTotalAmount);
			param.setWorkDt(cal.getTime());
			param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Full"));
			
			result = workMapper.insertWorkReport(param);
			
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
