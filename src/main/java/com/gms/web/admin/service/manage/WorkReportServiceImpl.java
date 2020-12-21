package com.gms.web.admin.service.manage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.gms.web.admin.domain.manage.CashFlowVO;
import com.gms.web.admin.domain.manage.CustomerProductVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.OrderBottleVO;
import com.gms.web.admin.domain.manage.OrderExtVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.domain.manage.WorkBottleRegisterVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.domain.manage.WorkReportViewVO;
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
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private CashFlowService cashService;
	
	@Override
	public WorkReportVO getWorkReport(Integer workReportSeq) {
		
		return workMapper.selectWorkReport(workReportSeq);
	}
	
	@Override
	public List<WorkReportVO> getWorkReportList(WorkReportVO param) {
			
		if(param.getSearchDt() == null || param.getSearchDt().length() == 0) {						
			
			param.setSearchDt(DateUtils.getDate("yyyy/MM/dd"));
		}
		
		return workMapper.selectWorkReportList(param);
	}
	

	@Override
	public List<WorkReportViewVO> getWorkReportListAll(WorkReportVO param) {		
		
		List<WorkReportViewVO> viewList = new ArrayList<WorkReportViewVO>();
						
		if(param.getSearchDt() == null || param.getSearchDt().length() == 0) {			
			param.setSearchDt(DateUtils.getDate("yyyy/MM/dd"));
		}		
		
		List<WorkBottleVO> workBottleList = workMapper.selectWorkReportListAll(param);
		
		List<WorkReportVO> reportList = workMapper.selectWorkReportOnlyListAll(param);
		
		for(int i = 0 ; i < reportList.size() ; i++ ) {
			
			WorkReportViewVO temp = new WorkReportViewVO();
			temp.setWorkReportSeq(reportList.get(i).getWorkReportSeq());
			temp.setReceivedAmount(reportList.get(i).getReceivedAmount());
			if(reportList.get(i).getIncomeWay()!=null && reportList.get(i).getIncomeWay().equals("CASH")) 
				temp.setIncomeWay("(현금)");
			else if(reportList.get(i).getIncomeWay()!=null && reportList.get(i).getIncomeWay().equals("CARD")) 
				temp.setIncomeWay("(카드)");
			else
				temp.setIncomeWay("");
			
			temp.setCustomerNm(reportList.get(i).getCustomerNm());
			List<WorkBottleVO> tempBottle = new ArrayList<WorkBottleVO>();
			List<WorkBottleVO> tempBottle1 = new ArrayList<WorkBottleVO>();
			
			temp.setBackBottles(tempBottle);
			temp.setSalesBottles(tempBottle1);
			viewList.add(temp);			
		}		
		
		for(int i = 0 ; i < workBottleList.size() ; i++ ) {
			
			WorkBottleVO workBottle = workBottleList.get(i);
			//logger.debug("WorkReportServiceImpl getWorkReportListAll workBottle.getProductCapa().indexOf(Kg) "+ workBottle.getProductCapa().indexOf("Kg"));
			//if(workBottle.getProductCapa().indexOf("Kg") < 0) workBottle.setProductCapa(workBottle.getProductCapa() +"L");
			if(workBottle.getGasId() > 0) {
				if(workBottle.getProductCapa().indexOf("Kg") < 0) workBottle.setProductCapa(workBottle.getProductCapa() +"L");
			}
			
			for(int j=0; j < viewList.size() ; j++) {
				WorkReportViewVO temp = viewList.get(j);
				
				// Work_Report_Seq 동일
				if((temp.getWorkReportSeq() - workBottle.getWorkReportSeq())==0) {
					
					temp.setCustomerId(workBottle.getCustomerId());
					temp.setCustomerNm(workBottle.getCustomerNm());					
					
					// 회수
					if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))
							|| workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freeback"))
							|| workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.buyback")) ) {
						
						temp.getBackBottles().add(workBottle);
						
					}else {
						//logger.debug("WorkReportServiceImpl getWorkReportList sales workBottle.getProductId= "+ workBottle.getProductId());
						temp.getSalesBottles().add(workBottle);
					}
					
				}else {					
					
				}
			}
		}
		//logger.debug("WorkReportServiceImpl getWorkReportListAll viewList.size= "+ viewList.size());
		int totalAmount=0;
		for(int i=0;i<viewList.size() ; i++) {
			totalAmount = 0;
			
			for(int j=0;j<viewList.get(i).getSalesBottles().size();j++) {
				totalAmount +=viewList.get(i).getSalesBottles().get(j).getProductPrice()*viewList.get(i).getSalesBottles().get(j).getProductCount();
				
				//logger.debug("WorkReportServiceImpl getWorkReportListAll viewList.get(i).getSalesBottles().size() .getProductId= "+ viewList.get(i).getSalesBottles().get(j).getProductNm());
			}
			viewList.get(i).setOrderAmount(totalAmount);
			for(int j=0;j<viewList.get(i).getBackBottles().size();j++) {
				//logger.debug("WorkReportServiceImpl getWorkReportList viewList.get(i).getBackBottles().size() .getProductId= "+ viewList.get(i).getBackBottles().get(j).getProductNm());
			}
		}
		
		return viewList;
	}

	
	@Override
	@Transactional
	public int registerWorkReportForOrder(WorkReportVO param) {
		logger.debug("****registerWorkReportForOrder start =" );
		int result = 1;
		try {
			//Order 정보가져오기
			OrderExtVO  orderInfo = orderService.getOrderNotDelivery(param.getOrderId());		
			OrderVO order = orderInfo.getOrder();
			//Order_Product 비교
			List<OrderProductVO> orderProductList = orderService.getOrderProductList(param.getOrderId());
			
			List<OrderBottleVO> orderBottleListNot = orderService.getOrderBottleListNotDelivery(param.getOrderId());
			//Bottle 정보 가져오기
			List<BottleVO> bottleList = getBottleList(param);

			List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();	
			List<OrderProductVO> newOrderProductList = new ArrayList<OrderProductVO>();
			
			//Work_Report_Seq 가져오기
			boolean registerFlag = false;
			param.setCustomerId(orderInfo.getOrder().getCustomerId());
			if(param.getUserId() == null) {
				param.setUserId(param.getCreateId());
				param.setUpdateId(param.getCreateId());
			}
			int workReportSeq = getWorkReportSeqForCustomerToday(param);
			int workSeq=1;
			if(workReportSeq <= 0) {
				workReportSeq = getWorkReportSeq();
				registerFlag = true;
			}else {
				workSeq = workMapper.selectWorkBottleSeq(workReportSeq);
			}
			param.setWorkReportSeq(workReportSeq);
			param.setWorkCd(param.getBottleWorkCd());
					
			if(registerFlag)
				result = workMapper.insertWorkReport(param);
			
			boolean updateOrderAddFlag = true;
			int receivableAmount = 0 ;
			
			for(int i = 0 ; i < bottleList.size() ; i++) {
				BottleVO soldBottle = bottleList.get(i);				
				soldBottle.setDeleteYn("N");	
				soldBottle.setCustomerId(param.getCustomerId());
				soldBottle.setUpdateId(param.getUserId());
				soldBottle.setBottleWorkCd(param.getBottleWorkCd());
				
				if(orderBottleListNot.size() > 0 ) {
					for(int j = orderBottleListNot.size()-1 ; j >= 0  ; j--) {
						OrderBottleVO orderBottle = orderBottleListNot.get(j);
						String strBottleSaleYn ="";
						OrderProductVO orderProduct = null;
						for(int k=0 ; k < orderProductList.size() ; k++) {
							orderProduct = orderProductList.get(k);
							//logger.debug("WorkReportServiceImpl registerWorkReportForOrder orderProduct =" + orderProduct.getOrderProductSeq());
							if(orderBottle.getOrderProductSeq() == orderProduct.getOrderProductSeq()) {
								strBottleSaleYn = orderProduct.getBottleSaleYn();	
								break;
							}						
						}
						
						if(soldBottle.getProductId() == orderBottle.getProductId() 
								&& soldBottle.getProductPriceSeq() == orderBottle.getProductPriceSeq() 
								&& ( (strBottleSaleYn.equals("Y") && param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) )
										|| (strBottleSaleYn.equals("N") && param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent")) ) )   ) {
							
							
							if(orderBottle.getBottleBarCd() == null) {
								orderBottle.setBottleBarCd(soldBottle.getBottleBarCd());
								
								soldBottle.setDeleteYn("Y");									
								soldBottle.setCustomerId(param.getCustomerId());
								
								ProductTotalVO productTotal = productService.getPrice(soldBottle);	
								
								WorkBottleVO workBottle = makeWorkBottle(soldBottle);
								workBottle.setWorkReportSeq(param.getWorkReportSeq());
								workBottle.setWorkSeq(workSeq++);								
						
								if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {	
									workBottle.setBottleSaleYn("Y");
									if(productTotal.getCustomerBottlePrice() > 0) 
										workBottle.setProductPrice(productTotal.getCustomerBottlePrice());
									else 
										workBottle.setProductPrice( productTotal.getProductBottlePrice());										
								}else {
									workBottle.setBottleSaleYn("N");
									if(productTotal.getCustomerProductPrice() > 0)
										workBottle.setProductPrice(productTotal.getCustomerProductPrice());
									else
										workBottle.setProductPrice(productTotal.getProductPrice());														
								}
								
								workBottleList.add(workBottle); 						
								
								result = orderService.modifyOrderBottle(orderBottle);
								orderBottleListNot.remove(j);
								receivableAmount += workBottle.getProductPrice();
								break;							
							}
						}
						
					} //for(int j = 0 ; j < orderInfo.getOrderProduct().size() ; j++) {		
				}else { //if(orderBottleListNot.size() > 0 ) {
					//신규 추가
					
					for(int k=0 ; k < orderProductList.size() ; k++) {
						OrderProductVO orderProduct = orderProductList.get(k);
						String strBottleSaleYn =orderProduct.getBottleSaleYn();
						
						if(soldBottle.getProductId() == orderProduct.getProductId() 
								&& soldBottle.getProductPriceSeq() == orderProduct.getProductPriceSeq() 
								&& ( (strBottleSaleYn.equals("Y") && param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) )
										|| (strBottleSaleYn.equals("N") && param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent")) ) )   ) {
							
							
							int productPrice = orderProduct.getOrderAmount() / orderProduct.getOrderCount();
							
							WorkBottleVO workBottle = makeWorkBottle(soldBottle);
							workBottle.setWorkReportSeq(param.getWorkReportSeq());
							workBottle.setWorkSeq(workSeq++);
							workBottle.setProductPrice(productPrice);
							workBottle.setBottleSaleYn(strBottleSaleYn);
							
							workBottleList.add(workBottle); 
							
							OrderBottleVO orderBottle = makeOrderBottle(orderProduct, soldBottle);
							orderBottleList.add(orderBottle);
							receivableAmount += workBottle.getProductPrice();
						}										
					}					
				}
				
			} //for(int i = 0 ; i < bottleList.size() ; i++) {			
			
			// 남은 용기 
			int orderProductSeq = orderProductList.size() +1;
			
			for(int i = 0 ; i < bottleList.size() ; i++) {
				BottleVO soldBottle = bottleList.get(i);		
				soldBottle.setCustomerId(param.getCustomerId());
				soldBottle.setCreateId(param.getUserId());
				soldBottle.setBottleWorkCd(param.getBottleWorkCd());				
				
				boolean regiFlag = true;		
				boolean alreadyFlag = true;
				
				if(soldBottle.getDeleteYn().equals("N")) {
					updateOrderAddFlag = false;
					for(int k=0 ; k < orderProductList.size() ; k++) {
						OrderProductVO orderProduct = orderProductList.get(k);
						
						if(soldBottle.getProductId() == orderProduct.getProductId() && soldBottle.getProductPriceSeq() == orderProduct.getProductPriceSeq()) {
							int productPrice = orderProduct.getOrderAmount() / orderProduct.getOrderCount();
							orderProduct.setOrderCount(orderProduct.getOrderCount()+1);
							orderProduct.setOrderAmount(orderProduct.getOrderAmount()+productPrice);
							orderProduct.setBottleId(soldBottle.getBottleId());
							//logger.debug("WorkReportServiceImpl registerWorkReportForOrder orderProduct =" + orderProduct.getProductId());
							regiFlag = false;
							
						}
					}						
				
					if(regiFlag) {	
						//logger.debug("WorkReportServiceImpl registerWorkReportForOrder newOrderProductList.siz111 =" + newOrderProductList.size());
						for(int j = 0 ; j < newOrderProductList.size() ; i++) {
							
							if(soldBottle.getProductId() == newOrderProductList.get(j).getProductId() && soldBottle.getProductPriceSeq() == newOrderProductList.get(j).getProductPriceSeq()) {
								alreadyFlag = false;
								
								int orderCount = newOrderProductList.get(j).getOrderCount();
								int productPrice = newOrderProductList.get(j).getOrderAmount() / newOrderProductList.get(j).getOrderCount();
								newOrderProductList.get(j).setOrderCount(orderCount+1);
								newOrderProductList.get(j).setOrderAmount(newOrderProductList.get(j).getOrderAmount()+productPrice);														
								
								WorkBottleVO workBottle = makeWorkBottle(soldBottle);
								workBottle.setWorkReportSeq(param.getWorkReportSeq());
								workBottle.setWorkSeq(workSeq++);
								workBottle.setProductPrice(newOrderProductList.get(j).getOrderAmount());
								workBottle.setBottleSaleYn(newOrderProductList.get(j).getBottleSaleYn());
								//workBottle.setBottleWorkCd(param.getBottleWorkCd());
								
								workBottleList.add(workBottle); 
								receivableAmount += workBottle.getProductPrice();
								
								OrderBottleVO orderBottle = makeOrderBottle(newOrderProductList.get(j), soldBottle);
								orderBottleList.add(orderBottle);
								
								break;
							}								
						}
						 
						if(alreadyFlag) {
							OrderProductVO newOrderProduct = new OrderProductVO();
							
							ProductTotalVO productTotal = productService.getPrice(soldBottle);	
							
							newOrderProduct.setOrderId(param.getOrderId());		
							newOrderProduct.setOrderProductSeq(orderProductSeq++);
							newOrderProduct.setProductId(soldBottle.getProductId());
							newOrderProduct.setProductPriceSeq(soldBottle.getProductPriceSeq());
							
							newOrderProduct.setBottleBarCd(soldBottle.getBottleBarCd());
							newOrderProduct.setGasId(soldBottle.getGasId());
							newOrderProduct.setCreateId(param.getUserId());
							newOrderProduct.setOrderCount(1);
							
							if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {									
								newOrderProduct.setBottleChangeYn("N");
								newOrderProduct.setBottleSaleYn("Y");
								
								if(productTotal.getCustomerBottlePrice() > 0) 
									newOrderProduct.setOrderAmount(productTotal.getCustomerBottlePrice());
								else 
									newOrderProduct.setOrderAmount( productTotal.getProductBottlePrice());									
							}else {
								newOrderProduct.setBottleChangeYn("Y");
								newOrderProduct.setBottleSaleYn("N");
								
								if(productTotal.getCustomerProductPrice() > 0)
									newOrderProduct.setOrderAmount(productTotal.getCustomerProductPrice());
								else
									newOrderProduct.setOrderAmount(productTotal.getProductPrice());						
							}
							newOrderProductList.add(newOrderProduct);
							
							WorkBottleVO workBottle = makeWorkBottle(soldBottle);
							workBottle.setWorkReportSeq(param.getWorkReportSeq());
							workBottle.setWorkSeq(workSeq++);
							workBottle.setProductPrice(newOrderProduct.getOrderAmount());
							workBottle.setBottleSaleYn(newOrderProduct.getBottleSaleYn());
							workBottle.setBottleWorkCd(param.getBottleWorkCd());							
							//20201220
							workBottle.setCustomerAgencyYn(param.getCustomerAgencyYn());
							
							workBottleList.add(workBottle); 
							receivableAmount += workBottle.getProductPrice();
							
							OrderBottleVO orderBottle = makeOrderBottle(newOrderProduct, soldBottle);
							orderBottleList.add(orderBottle);							
							
						} 	// if(regiFlag) {						
					} 				
				}				
			}
			

			for(int i =0 ; i < orderProductList.size() ; i++) {
				OrderProductVO orderProduct = orderProductList.get(i);
				//logger.debug("WorkReportServiceImpl registerWorkReportForOrder orderProduct =" + orderProduct.getBottleId());
				if(orderProduct.getBottleId() != null) {
					orderProduct.setUpdateId(param.getCreateId());
					result = orderService.modifyOrderProductCount(orderProduct);
				}
			}

			if(newOrderProductList.size() > 0)
				result = orderService.registerOrderProducts(newOrderProductList);
			
			if(orderBottleList.size() > 0 )
				result = orderService.registerOrderBottles(orderBottleList);
			
			if(workBottleList.size() > 0)
				result = workMapper.insertWorkBottles(workBottleList);			
							
			// Order 상태 정보 변경
			List<OrderProductVO> allOrderProductList = orderService.getOrderProductList(param.getOrderId());
			int remainCount = 0;
			int orderAmount = 0;
			
			boolean orderCompleted = false;
			for(int j=0; j < allOrderProductList.size() ; j++) {
				OrderProductVO orderProduct = allOrderProductList.get(j);
				remainCount += allOrderProductList.get(j).getSalesCount();
				orderAmount += allOrderProductList.get(j).getOrderAmount();
				String strBottleSaleYn = orderProduct.getBottleSaleYn();
				
				for(int i = 0 ; i < bottleList.size() ; i++) {
					BottleVO soldBottle = bottleList.get(i);
					if(soldBottle.getProductId() == orderProduct.getProductId() 
							&& soldBottle.getProductPriceSeq() == orderProduct.getProductPriceSeq() 
							&& ( (strBottleSaleYn.equals("Y") && param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) )
									|| (strBottleSaleYn.equals("N") && param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent")) ) )   ) {
					
						soldBottle.setOrderProductSeq(orderProduct.getOrderProductSeq());
					}
				}
			}
			
			if(remainCount == 0) orderCompleted = true;			
			
			if(allOrderProductList.size() > 1) {
				order.setOrderProductNm(allOrderProductList.get(0).getProductNm()+" 외 "+(allOrderProductList.size()-1));
				order.setOrderProductCapa(allOrderProductList.get(0).getProductCapa()+" 외 "+(allOrderProductList.size()-1));
			}
			if(!updateOrderAddFlag && orderAmount > 0) {
				order.setOrderTotalAmount(orderAmount);	
				order.setUpdateId(param.getUserId());
				
				result = orderService.modifyOrderAdditionBottles(order);		
			}
			
			//WorkReprot
			param.setOrderId(order.getOrderId());			
			param.setWorkProductNm(order.getOrderProductNm());
			param.setWorkProductCapa(order.getOrderProductCapa());
			
			result = workMapper.modifyWorkReportProduct(param);			
			
			if(orderCompleted) {
				order.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
				order.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.delivery"));	
				order.setSalesId(param.getUserId());
				order.setChOrderId(order.getOrderId());
				order.setUpdateId(param.getUserId());
				result = orderService.changeOrderProcessCd(order);
			}
			//CashFlow
			result = registerCashFlow(param,receivableAmount);
						
			//Customer Bottle_Own_Count 증가
			//TODO 
			// common.bottle.status.salesgas / common.bottle.status.agencyRent 처리
			if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) || param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesgas")) ) {
				
				CustomerVO customer = new CustomerVO();
				customer.setCustomerId(param.getCustomerId());
				customer.setUpdateId(param.getCreateId());
				customer.setBottleOwnCount(workBottleList.size());
				//20201220
				customer.setAgencyYn(param.getCustomerAgencyYn());
				
				result = changeCustomerProduct(workBottleList);	//Customer_Product 등록				
				result = customerService.modifyCustomerBottleCount(customer);
			}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent")) || param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyRent")) ) {
				
				CustomerVO customer = new CustomerVO();
				customer.setCustomerId(param.getCustomerId());
				customer.setUpdateId(param.getCreateId());
				customer.setBottleRentCount(workBottleList.size());
				//20201220
				customer.setAgencyYn(param.getCustomerAgencyYn());
				
				result = changeCustomerProduct(workBottleList);	//Customer_Product 등록				
				result = customerService.modifyCustomerBottleRentCount(customer);
			}
			
			for(int i = 0 ; i < bottleList.size() ; i++) {
				BottleVO bottle  = bottleList.get(i);
				// Bottle 정보 업데이트
				bottle.setOrderId(order.getOrderId());				
				bottle.setCustomerId(param.getCustomerId());
				bottle.setBottleWorkId(param.getUserId());
				bottle.setUpdateId(param.getUserId());
				bottle.setBottleType(param.getBottleType());
				bottle.setBottleWorkCd(param.getBottleWorkCd());
				
				result = bottleService.modifyBottleOrder(bottle);
			}
			
			
		} catch (DataAccessException e) {
			result = 0;
			e.printStackTrace();
			logger.error("WorkReportSeviceImpl", e.toString());
		} catch (Exception e) {			
			result = 0;
			e.printStackTrace();
			logger.error("WorkReportSeviceImpl", e.toString());
		}
		return result;		
	}
	
	

	@Override
	@Transactional
	public int registerWorkReportNoOrder(WorkReportVO param) {
		logger.debug("=====================  registerWorkReportNoOrder start ===============");
		logger.debug(" registerWorkReportNoOrder bottleIds =" + param.getBottlesIds());			
		logger.debug(" registerWorkReportNoOrder customerId =" + param.getCustomerId());			
		
		int result = 0;
		
		try {		
			//기존 주문이 있는지 여부 확인
			OrderVO orderTemp =  orderService.getLastOrderForCustomer(param.getCustomerId());
			
			if(orderTemp != null ) {
				param.setOrderId(orderTemp.getOrderId());
				//logger.debug("WorkReportServiceImpl registerWorkReportNoOrder getOrderId =" + param.getOrderId());		
				//result = registerWorkReportForOrder(param);
				result = registerWorkReportForOrder(param);
			}else {
				//Bottle 정보 가져오기
				List<BottleVO> bottleList = getBottleList(param);
				
				if(param.getUserId() == null)
					param.setUserId(param.getCreateId());
				//Work_Report_Seq 가져오기
				boolean registerFlag = false;
				int workSeq=1;
				int workReportSeq = getWorkReportSeqForCustomerToday(param);
				
				if(workReportSeq <= 0) {
					workReportSeq = getWorkReportSeq();
					registerFlag = true;
				}else {
					workSeq = workMapper.selectWorkBottleSeq(workReportSeq);
				}
				
				//logger.debug("WorkReportServiceImpl registerWorkReportNoOrder workReportSeq =" + workReportSeq);
				//TB_Work_Reprot 등록
				param.setWorkReportSeq(workReportSeq);							
				
				//TB_Work_Bottle 등록					
				List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();			
				List<OrderProductVO> orderProductList = new ArrayList<OrderProductVO>();
				List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();	
				
				OrderVO order = new OrderVO();
				
				int orderId = orderService.getOrderId();
				
				order.setOrderId(orderId);
				if(param.getUserId() != null && param.getUserId().length() > 0)
					order.setCreateId(param.getUserId());
				else
					order.setCreateId(param.getCreateId());
				order.setUpdateId(param.getCreateId());
				order.setCustomerId(param.getCustomerId());
				order.setMemberCompSeq(1);
				order.setOrderTypeCd(PropertyFactory.getProperty("common.code.order.type.order"));				
				
				Calendar cal = Calendar.getInstance();
				int amPm = cal.get(Calendar.AM_PM);
				order.setDeliveryReqDt(cal.getTime());
				
				if (amPm == 1 ) order.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.PM"));
				else  order.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.AM"));
				
				order.setOrderEtc("현장주문");
				order.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.delivery"));
				if(param.getUserId() != null && param.getUserId().length() > 0)
					order.setSalesId(param.getUserId());
				else
					order.setSalesId(param.getCreateId());
				order.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
				
				// Order 먼저 등록  2020-08-28
				result = orderService.registerOrder(order);
				
				OrderProductVO tempOrderProduct = null;
				
				WorkBottleVO workBottle = null;
				BottleVO tempBottle = null;
				ProductTotalVO tempProductTotal = null;
				
				//용기 정보를 이횽해 상품 정보 가져옴				
				int orderProductSeq = 1;
				int orderBottleSeq= 1;
				int orderCount = 1;
				int orderTotalAmount = 0;		
				int receivableAmount = 0;
				String orderProductNm = "";
				String orderProductCapa = "";				
				
				for(int i = 0; i < bottleList.size() ; i++) {		
					
					tempBottle = bottleList.get(i);
					tempBottle.setCustomerId(param.getCustomerId());
					tempBottle.setUpdateId(param.getUserId());
					tempBottle.setBottleWorkCd(param.getBottleWorkCd());
					//logger.debug(" registerWorkReportNoOrder tempBottle.getBottleBarCD =" + tempBottle.getBottleBarCd());	
					//workBottle = new WorkBottleVO();					
					tempOrderProduct = new OrderProductVO();
					
					//tempProductTotal = productService.getBottleGasCapa(tempBottle);		
					tempBottle.setCustomerId(param.getCustomerId());
					tempProductTotal = productService.getPrice(tempBottle);		//2020-06-15
					
					tempOrderProduct.setProductId(tempProductTotal.getProductId());
					tempOrderProduct.setProductPriceSeq(tempProductTotal.getProductPriceSeq());
					tempOrderProduct.setOrderId(orderId);
					
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {
						tempOrderProduct.setBottleChangeYn("N");
						tempOrderProduct.setBottleSaleYn("Y");						
					}else {
						tempOrderProduct.setBottleChangeYn("Y");
						tempOrderProduct.setBottleSaleYn("N");
					}
					
					//tempOrderProduct.setProductDeliveryDt(cal.getTime());
					tempOrderProduct.setOrderProductEtc("주문없이추가");					
					
					// TB_Order Product_Nm, Product_Capa 초기값
					if(i==0) {
						orderProductNm = tempProductTotal.getProductNm();
						orderProductCapa = tempProductTotal.getProductCapa();
					}
					
					OrderBottleVO tempOrderBottle = new OrderBottleVO();
					tempOrderBottle.setOrderId(orderId);
					tempOrderBottle.setOrderProductSeq(orderBottleSeq++);
					tempOrderBottle.setCreateId(param.getCreateId());
					tempOrderBottle.setUpdateId(param.getCreateId());	
					
					//orderProduct 					
					if(i > 0 ) {
						BottleVO tempBottle1 = bottleList.get(i-1);
						
						// 이전 용기의 상품 비교
						if(tempBottle.getProductId() == tempBottle1.getProductId() 
								&& tempBottle.getProductPriceSeq() == tempBottle1.getProductPriceSeq()) {
							orderCount++;							
							
							orderProductList.get(orderProductSeq-1).setOrderCount(orderCount);
							if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) ) {								
								if(tempProductTotal.getCustomerBottlePrice() > 0)
									orderProductList.get(orderProductSeq-1).setOrderAmount(orderCount*tempProductTotal.getCustomerBottlePrice());	
								else
									orderProductList.get(orderProductSeq-1).setOrderAmount(orderCount*tempProductTotal.getProductBottlePrice());									
							} else {
								if(tempProductTotal.getCustomerProductPrice() > 0)
									orderProductList.get(orderProductSeq-1).setOrderAmount(orderCount*tempProductTotal.getCustomerProductPrice());	
								else
									orderProductList.get(orderProductSeq-1).setOrderAmount(orderCount*tempProductTotal.getProductPrice());								
							}
							receivableAmount += orderProductList.get(orderProductSeq-1).getOrderAmount()/orderCount;
						}else {
							orderProductSeq++;
							orderCount  = 1;
							tempOrderProduct.setOrderCount(orderCount);
							if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) ) {
								if(tempProductTotal.getCustomerBottlePrice() > 0)
									tempOrderProduct.setOrderAmount(tempProductTotal.getCustomerBottlePrice());	
								else
									tempOrderProduct.setOrderAmount(tempProductTotal.getProductBottlePrice());								
							}else {
								if(tempProductTotal.getCustomerProductPrice() > 0)
									tempOrderProduct.setOrderAmount(tempProductTotal.getCustomerProductPrice());
								else
									tempOrderProduct.setOrderAmount(tempProductTotal.getProductPrice());									
							}
							tempOrderProduct.setOrderProductSeq(orderProductSeq);
							
							orderProductList.add(tempOrderProduct);			
							receivableAmount += tempOrderProduct.getOrderAmount();
						}							
					}else {
						
						tempOrderProduct.setOrderCount(orderCount);
						if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) ) {
							if(tempProductTotal.getCustomerBottlePrice() > 0)
								tempOrderProduct.setOrderAmount(orderCount*tempProductTotal.getCustomerBottlePrice());	
							else
								tempOrderProduct.setOrderAmount(orderCount*tempProductTotal.getProductBottlePrice());																							
						}else {
							if(tempProductTotal.getCustomerProductPrice() > 0)
								tempOrderProduct.setOrderAmount(orderCount*tempProductTotal.getCustomerProductPrice());		
							else
								tempOrderProduct.setOrderAmount(orderCount*tempProductTotal.getProductPrice());
						}
						tempOrderProduct.setOrderProductSeq(orderProductSeq);
						tempOrderProduct.setCreateId(param.getCreateId());
						orderProductList.add(tempOrderProduct);				
						receivableAmount += tempOrderProduct.getOrderAmount()/orderCount;
					}
					workBottle = makeWorkBottle(tempBottle);
					
					workBottle.setWorkReportSeq(param.getWorkReportSeq());
					workBottle.setWorkSeq(workSeq++);			
					//20201220
					workBottle.setCustomerAgencyYn(param.getCustomerAgencyYn());
					
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) ) {
						workBottle.setBottleSaleYn("Y");
						if(tempProductTotal.getCustomerBottlePrice() > 0)
							workBottle.setProductPrice(tempProductTotal.getCustomerBottlePrice()); 
						else
							workBottle.setProductPrice(tempProductTotal.getProductBottlePrice());						 
					} else {
						workBottle.setBottleSaleYn("N");
						if(tempProductTotal.getCustomerProductPrice() > 0)
							workBottle.setProductPrice(tempProductTotal.getCustomerProductPrice());
						else
							workBottle.setProductPrice(tempProductTotal.getProductPrice());				
					}					
									
					tempBottle.setBottleWorkCd(param.getBottleWorkCd());
					tempBottle.setBottleWorkId(param.getCreateId());
					tempBottle.setCustomerId(param.getCustomerId());
					tempBottle.setBottleType(workBottle.getBottleType());
					tempBottle.setUpdateId(param.getCreateId());				
					tempBottle.setOrderId(orderId);
					tempBottle.setOrderProductSeq(i+1);
					
					result = bottleService.modifyBottleOrder(tempBottle);					
					
					// TB_Order_Bottle
					tempOrderBottle.setProductId(tempBottle.getProductId());
					tempOrderBottle.setProductPriceSeq(tempBottle.getProductPriceSeq());
					tempOrderBottle.setBottleBarCd(tempBottle.getBottleBarCd()); 		
					tempOrderBottle.setUpdateId(param.getCreateId());
					
					orderBottleList.add(tempOrderBottle);		
					workBottleList.add(workBottle);							
				}
				
				if( orderProductList.size() > 1) {
					orderProductNm = orderProductNm + " 외 " + (orderProductList.size()-1);
					orderProductCapa = orderProductCapa + " 외 " + (orderProductList.size()-1);
				}
				order.setOrderProductNm(orderProductNm);
				order.setOrderProductCapa(orderProductCapa);
				
				for(int k=0; k < workBottleList.size() ; k++) { 
					orderTotalAmount += workBottleList.get(k).getProductPrice();
					//logger.debug("WorkReportServiceImpl registerWorkReportNoOrder workBottleList.get(k).getProductPrice() =" + workBottleList.get(k).getProductPrice());
				}
				order.setOrderTotalAmount(orderTotalAmount);				
				
				//TB_Order 등록TB_Order_Product			등록
				//result  = orderService.registerOrderAndProduct(order, orderProductList);		//2020-080-28 
				result  = orderService.modifyOrderRegiProduct(order, orderProductList);
				
				// Order_Bottle 등록
				if(orderBottleList.size() > 0)
					result = orderService.registerOrderBottles(orderBottleList);
				
				param.setWorkCd(param.getBottleWorkCd());
				param.setWorkProductNm(orderProductNm);
				param.setWorkProductCapa(orderProductCapa);
				param.setOrderId(orderId);
				param.setOrderAmount(orderTotalAmount);
				param.setWorkDt(cal.getTime());
				//param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Full"));
				
				//TODO
				// common.bottle.status.salesgas / common.bottle.status.agencyRent 처리 필요
				if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) || param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesgas"))) {
				
					CustomerVO customer = new CustomerVO();
					customer.setCustomerId(param.getCustomerId());
					customer.setUpdateId(param.getCreateId());					
					customer.setBottleOwnCount(workBottleList.size());
					customer.setAgencyYn(param.getCustomerAgencyYn());
					
					result = changeCustomerProduct(workBottleList);	//Customer_Product 등록
					result = customerService.modifyCustomerBottleCount(customer);
				}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))  || param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyRent")) ) {
					
					CustomerVO customer = new CustomerVO();
					customer.setCustomerId(param.getCustomerId());
					customer.setUpdateId(param.getCreateId());
					customer.setBottleRentCount(workBottleList.size());
					customer.setAgencyYn(param.getCustomerAgencyYn());
					
					result = changeCustomerProduct(workBottleList);	//Customer_Product 등록
					result = customerService.modifyCustomerBottleRentCount(customer);
				}
				
				if(registerFlag) {
					result = workMapper.insertWorkReport(param);
				}else {
					
					param.setUpdateId(param.getCreateId());
					result = workMapper.modifyWorkReportOrderId(param);
				}
				if(workBottleList.size() > 0)
					result = workMapper.insertWorkBottles(workBottleList);	
			
				//CashFlow
				result = registerCashFlow(param,receivableAmount);
			
			}
		} catch (DataAccessException e) {			
			e.printStackTrace();
			
			logger.error("registerWorkReportNoOrder DataAccessException==="+e.toString());
		} catch (Exception e) {			
			e.printStackTrace();
			
			logger.error("registerWorkReportNoOrder Exception==="+e.toString());
		}
		return result;
	}	
	
	
	@Override
	@Transactional
	public int registerWorkReportByBottle(WorkReportVO param, List<BottleVO> bottleList) {
		logger.debug(" registerWorkReport start ");
		int result = 0;
		boolean insertFlag = false;
		try {			
			
			logger.debug(" registerWorkReportByBottle bottleIds =" + param.getBottlesIds());			
			
			int workReportSeq = 0;
			int workSeq = 1;
			if(param.getUserId() != null && param.getUserId().length() > 0)
				param.setUserId(param.getUserId());
			else
				param.setUserId(param.getCreateId());
			
			if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back")) 
					|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freeback"))
					|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.buyback"))
					|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freechange")) 
					|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesBack")) 
					|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyBack")) 
					) {
				workReportSeq = getWorkReportSeqForCustomerToday(param);
			}
			//Work_Report_Seq 가져오기
			//int workReportSeq = getWorkReportSeqForCustomerToday(param);
			
			if(workReportSeq <= 0) {
				workReportSeq = getWorkReportSeq();
				insertFlag = true;
			}else {
				workSeq = workMapper.selectWorkBottleSeq(workReportSeq);
			}
			logger.debug(" registerWorkReportByBottle workReportSeq =" + workReportSeq);
			logger.debug(" registerWorkReportByBottle workSeq =" + workSeq);
			//TB_Work_Reprot 등록
			param.setWorkReportSeq(workReportSeq);
						
			//TB_Work_Bottle 등록				
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();			
						
			WorkBottleVO workBottle = null;
			BottleVO tempBottle = null;				
			
			String orderProductNm = "";
			String orderProductCapa = "";			
			
			for(int i = 0; i < bottleList.size() ; i++) {		
				
				tempBottle = bottleList.get(i);
				tempBottle.setCustomerId(param.getCustomerId());
				tempBottle.setUpdateId(param.getUserId());
				tempBottle.setBottleWorkCd(param.getBottleWorkCd());
				//workBottle = new WorkBottleVO();
				
				workBottle = makeWorkBottle(tempBottle);
				
				workBottle.setWorkReportSeq(param.getWorkReportSeq());
				workBottle.setWorkSeq(workSeq++);			
				workBottle.setBottleType(param.getBottleType());
				workBottle.setBottleWorkCd(param.getBottleWorkCd());
				if(i==0) {
					orderProductNm = tempBottle.getProductNm();
					orderProductCapa = tempBottle.getBottleCapa();
				}
				
				workBottleList.add(workBottle);							
			}			
			
			if( bottleList.size() > 1) {
				orderProductNm = orderProductNm + " 외 " + (bottleList.size()-1);
				orderProductCapa = orderProductCapa + " 외" + (bottleList.size()-1);
			}
			
			//TB_Order 등록
			//result = orderService.registerOrder(request, param)(orderInfo.getOrder());
			Calendar cal = Calendar.getInstance();
			param.setWorkDt(cal.getTime());
			param.setWorkCd(param.getBottleWorkCd());
			param.setWorkProductNm(orderProductNm);
			param.setWorkProductCapa(orderProductCapa);
			//logger.debug("WorkReportServiceImpl registerWorkReportByBottle insertFlag =" + insertFlag);
			
			//Customer Bottle_Own_Count 감소
			//TODO
			// common.bottle.status.agencyBack / common.bottle.status.salesBack 처리 필요
			if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freeback"))
					|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.buyback"))) {
				
				CustomerVO customer = new CustomerVO();
				customer.setCustomerId(param.getCustomerId());
				customer.setUpdateId(param.getCreateId());
				customer.setBottleOwnCount(workBottleList.size()*-1);
				customer.setAgencyYn(param.getCustomerAgencyYn());
				
				result = changeCustomerProduct(workBottleList);	//Customer_Product 등록
				
				result = customerService.modifyCustomerBottleCount(customer);
			}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back")) || param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyBack"))  ){
				
				CustomerVO customer = new CustomerVO();
				customer.setCustomerId(param.getCustomerId());
				customer.setUpdateId(param.getCreateId());
				customer.setBottleRentCount(workBottleList.size()*-1);
				customer.setAgencyYn(param.getCustomerAgencyYn());
				
				result = changeCustomerProduct(workBottleList);	//Customer_Product 등록
				
				result = customerService.modifyCustomerBottleRentCount(customer);
			}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesBack")) ){
				CustomerVO customer = new CustomerVO();
				customer.setCustomerId(param.getCustomerId());
				customer.setUpdateId(param.getCreateId());
				customer.setBottleOwnCount(workBottleList.size()*1);
				customer.setAgencyYn(param.getCustomerAgencyYn());
				
				result = changeCustomerProduct(workBottleList);	//Customer_Product 등록				
				result = customerService.modifyCustomerBottleCount(customer);
			}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freechange")) ){
				
				
			}
			
			if(insertFlag) {
				result = workMapper.insertWorkReport(param);
			}
			result = workMapper.insertWorkBottles(workBottleList);				
			
		} catch (DataAccessException e) {			
			e.printStackTrace();
			logger.error("registerWorkReportByBottle Exception==="+e.toString());
			return result;
		} catch (Exception e) {			
			e.printStackTrace();
			logger.error("registerWorkReportByBottle Exception==="+e.toString());
			return result;
		}
		return result;
	}
	
	@Override
	public List<WorkBottleVO> getWorkBottleList(Integer workReportSeq) {
		
		return workMapper.selectWorkBottleList(workReportSeq);
	}
	

	@Override
	public List<WorkBottleVO> getWorkBottleListOfOrder(Integer orderId) {
		// TODO Auto-generated method stub
		return workMapper.selectWorkBottleListOfOrder(orderId);	
	}
	
	
	@Override
	public List<WorkBottleRegisterVO> getWorkBottleListAndCountOfOrder(Integer orderId) {		
		return workMapper.selectWorkBottleListAndCountOfOrder(orderId);	
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

	@Override
	public Map<String,Object> getWorkBottleListTotal(BottleVO param) {
							  
		logger.debug("****** getWorkBottleListTotal *****start===*");	
				
		int currentPage = param.getCurrentPage();
		int ROW_PER_PAGE = param.getRowPerPage();
		
		int startPageNum =1;
		
		int lastPageNum = ROW_PER_PAGE;
		
		if(currentPage > (ROW_PER_PAGE/2)) {
			lastPageNum += (startPageNum-1);
		}
		
		int startRow = (currentPage-1) * ROW_PER_PAGE;
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		map.put("startRow", startRow);
		map.put("rowPerPage", ROW_PER_PAGE);	
		map.put("searchCustomerNm", param.getSearchCustomerNm());	
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
		String searchChargeDt = param.getSearchChargeDt();	
		
		if(searchChargeDt != null && searchChargeDt.length() > 20) {
			map.put("searchChargeDt", searchChargeDt);
			
			searchChargeDtFrom = searchChargeDt.substring(0, 10) ;			
			map.put("searchChargeDtFrom", searchChargeDtFrom);
			
			searchChargeDtEnd = searchChargeDt.substring(13, searchChargeDt.length()) ;			
			map.put("searchChargeDtEnd", searchChargeDtEnd);
		}						
		
		int bottleCount = workMapper.selectWorBottleCountTotal(map);		
		
		//int lastPage = (int)(Math.ceil(bottleCount/ROW_PER_PAGE));
		int lastPage = (int)((double)bottleCount/ROW_PER_PAGE+0.95);
		
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		if(lastPageNum ==0) lastPageNum=1;
		
		//수정 Start
		int pages = (bottleCount == 0) ? 1 : (int) ((bottleCount - 1) / ROW_PER_PAGE) + 1; // * 정수형이기때문에 소숫점은 표시안됨		
        //int blocks;
        int block;
        //blocks = (int) Math.ceil(1.0 * pages / ROW_PER_PAGE); // *소숫점 반올림
        block = (int) Math.ceil(1.0 * currentPage / ROW_PER_PAGE); // *소숫점 반올림
        startPageNum = (block - 1) * ROW_PER_PAGE + 1;
        lastPageNum = block * ROW_PER_PAGE;        
        
        if (lastPageNum > pages){
        	lastPageNum = pages;
        }
		//수정 end
		
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<BottleVO> bottleList = workMapper.selectWorBottleListTotal(map);
		
		resutlMap.put("list",  bottleList);				
		resutlMap.put("currentPage", currentPage);
		resutlMap.put("lastPage", lastPage);
		resutlMap.put("startPageNum", startPageNum);
		resutlMap.put("lastPageNum", lastPageNum);
		resutlMap.put("totalCount", bottleCount);		
		
		return  resutlMap;
	}

	@Override
	public List<BottleVO> getWorkBottleListToday(BottleVO param) {		
		List<BottleVO>  bottleList = workMapper.selectWorBottleListToday(param);
		
		return bottleList;
	}

	@Override
	public int registerWorkReport0310(WorkReportVO param) {
		logger.debug(" registerWorkReport start ");
		int result = 0;		
		
		try {						
			
			//Bottle 정보 가져오기
			BottleVO bottle = new BottleVO();
			//TB_Work_Bottle 등록				
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();			
			
			List<String> list = null;
			if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				list = StringUtils.makeForeach(param.getBottlesIds(), ","); 		
				bottle.setBottList(list);
				param.setBottList(list);
			}				
			
			List<BottleVO> bottleList = bottleService.getBottleDetails(bottle);			
			
			//Work_Report_Seq 가져오기
			if(bottleList.size() > 0) {
				param.setCustomerId(bottleList.get(0).getCustomerId());
			}
			boolean registerFlag = false;
			param.setUserId(param.getCreateId());
			int workReportSeq = getWorkReportSeqForCustomerToday(param);
			
			if(workReportSeq <= 0) {
				workReportSeq = getWorkReportSeq();
				registerFlag = true;
			}
			WorkBottleVO workBottle = null;
			BottleVO tempBottle = null;				
			
			String orderProductNm = "";
			String orderProductCapa = "";
			int workSeq = 1;
			for(int i = 0; i < bottleList.size() ; i++) {		
				
				tempBottle = bottleList.get(i);
				
				workBottle = new WorkBottleVO();
				
				if(i==0) {
					orderProductNm = tempBottle.getProductNm();
					orderProductCapa = tempBottle.getBottleCapa();
				}
				workBottle.setWorkReportSeq(workReportSeq);
				workBottle.setBottleId(tempBottle.getBottleId());
				workBottle.setBottleBarCd(tempBottle.getBottleBarCd());				
				workBottle.setCreateId(param.getCreateId());
				workBottle.setCustomerId(param.getCustomerId());
				workBottle.setBottleWorkCd(param.getBottleWorkCd());
				workBottle.setGasId(tempBottle.getGasId());
				workBottle.setProductId(tempBottle.getProductId());
				workBottle.setProductPriceSeq(tempBottle.getProductPriceSeq());	
				workBottle.setBottleType(param.getBottleType());
				workBottle.setWorkSeq(workSeq++);
				
				workBottleList.add(workBottle);							
			}			
			
			if( bottleList.size() > 1) {
				orderProductNm = orderProductNm + " 외 " + (bottleList.size()-1);
				orderProductCapa = orderProductCapa + " 외" + (bottleList.size()-1);
			}
			
			// bottle Bottle_Work_CD 업데이트
			bottle.setBottleIds(param.getBottlesIds());
			bottle.setBottleWorkCd(param.getBottleWorkCd());
			bottle.setUpdateId(param.getCreateId());
			bottle.setBottleType(param.getBottleType());
			bottle.setCustomerId(param.getCustomerId());
			
			result = bottleService.changeBottlesWorkCdOnly(bottle);
			
			//TB_Order 등록
			//result = orderService.registerOrder(request, param)(orderInfo.getOrder());
			param.setWorkReportSeq(workReportSeq);
			Calendar cal = Calendar.getInstance();
			param.setWorkDt(cal.getTime());
			param.setWorkCd(param.getBottleWorkCd());
			param.setWorkProductNm(orderProductNm);
			param.setWorkProductCapa(orderProductCapa);
			
			if(registerFlag)
				result = workMapper.insertWorkReport(param);			
			
			result = workMapper.insertWorkBottles(workBottleList);	
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
			return result;
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
			return result;
		}
		
		return result;		
	}

	@Override
	public int getWorkReportSeqForCustomerToday(WorkReportVO param) {		
		return workMapper.selectWorkReportSeqForCustomerToday(param);
	}

	
	private List<BottleVO> getBottleList(WorkReportVO param){
		BottleVO bottle = new BottleVO();
		
		if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
			//bottleIds= request.getParameter("bottleIds");
			List<String> list = StringUtils.makeForeach(param.getBottlesIds(), ","); 		
			bottle.setBottList(list);
		}		
		
		return  bottleService.getBottleDetails(bottle);		
	}
	
//TODO	단품상품 판매 후 가스 판매시 order 상태변경 안됨

	@Override
	@Transactional
	public int registerWorkNoBottle(WorkBottleVO param) {
		
		logger.debug(" registerWorkNoBottle  productId=" + param.getProductId() );
		logger.debug(" registerWorkNoBottle  productPriceSeq=" + param.getProductPriceSeq() );
		logger.debug(" registerWorkNoBottle  getBottleWorkCd=" + param.getBottleWorkCd() );
		
		boolean registerFlag = false;
		int result = 0;
		int workSeq=1;
		int receivableAmount =0;
		
		if(param.getProductId() != null && param.getProductPriceSeq() != null) {
			
			BottleVO bottle = new BottleVO();
			bottle.setProductId(param.getProductId());
			bottle.setProductPriceSeq(param.getProductPriceSeq());
			bottle.setCustomerId(param.getCustomerId());
			
			ProductTotalVO productTotal = productService.getPrice(bottle);
			// 금일 작업 이력 확인 및 WorkReporSeq 가져오기
			WorkReportVO workReport = new WorkReportVO();
			workReport.setCustomerId(param.getCustomerId());
			workReport.setWorkCd(param.getBottleWorkCd());
			workReport.setCreateId(param.getCreateId());
			workReport.setUpdateId(param.getCreateId());		
			if(param.getUserId() !=null && param.getUserId().length() > 0)
				workReport.setUserId(param.getUserId());
			else
				workReport.setUserId(param.getCreateId());
						
			//WorkReportSeq 가져오기
			int workReportSeq = getWorkReportSeqForCustomerToday(workReport);
			
			if(workReportSeq <= 0) {
				workReportSeq = getWorkReportSeq();
				registerFlag = true;
			}else {
				workSeq = workMapper.selectWorkBottleSeq(workReportSeq);
			}
			//logger.debug("WorkReportServiceImpl registerWorkNoBottle  workReportSeq=" + workReportSeq +" workSeq=" + workSeq );
			
			//주문정보 확인
			//기존 주문이 있는지 여부 확인
			OrderVO orderTemp =  orderService.getLastOrderForCustomer(param.getCustomerId());			
			
			List<OrderProductVO> orderProductList= null;		
			List<OrderProductVO> tempOrderProductList= null;	
			
			if(orderTemp !=null) { // 기존 주문 여부 확인
				param.setOrderId(orderTemp.getOrderId());
				
				orderTemp.setChOrderId(orderTemp.getOrderId());
				orderTemp.setUpdateId(param.getCreateId());
				//order 정보 가져와서 
				orderProductList = orderService.getOrderProductList(orderTemp.getOrderId());
				tempOrderProductList = orderProductList;
				
				//해당 주문 상품이 이미 처리되어 있는지 확인 WorkReport & WorkBottle 조회
				List<WorkBottleRegisterVO> registeredWorkBottleList = getWorkBottleListAndCountOfOrder(orderTemp.getOrderId());
				
				// 주문 상품수 새로 설정				
				for(int i = 0 ; i < orderProductList.size();i++) {
					OrderProductVO tempOrderProduct = orderProductList.get(i);					
					
					for(int j = 0 ; j < registeredWorkBottleList.size() ; j++) {
						WorkBottleRegisterVO tempRegisteredBottle = registeredWorkBottleList.get(j);
						
						if(tempOrderProduct.getProductId() == tempRegisteredBottle.getProductId() && tempOrderProduct.getProductPriceSeq() == tempRegisteredBottle.getProductPriceSeq()) {
							int leftCount  = tempOrderProduct.getOrderCount()-tempRegisteredBottle.getRegisteredCount();
							
							if(leftCount <= 0) leftCount = 0;
							
							// 주문 상품의 카운트 셋팅(0이면 이미 주문 처리된 것임)
							orderProductList.get(i).setOrderCount(leftCount);							
						}
					}					
				}
				
				// 해당 상품이 기존 주문에 있는지 확인
				boolean isNewProduct = true;
				
				for(int i = 0 ; i < orderProductList.size();i++) {
					OrderProductVO tempOrderProduct = orderProductList.get(i);											
					//
					// 기존 주문 상품과 비교
					if(tempOrderProduct.getProductId() == param.getProductId() && tempOrderProduct.getProductPriceSeq() == param.getProductPriceSeq()) {
						//기존 상품과 수량 비교(다르면 TB_order 업데이트, TB_order_product 업데이트), 같으면 주문상태 확인 후 업데이트 
						int addCount = param.getProductCount() - tempOrderProduct.getOrderCount();
						
						if(addCount > 0) {	// 기존 주문과 동일 상품의 추가가 있는 경우(TB_order 업데이트, TB_order_product 업데이트)
							orderProductList.get(i).setOrderCount(tempOrderProduct.getOrderCount()+addCount);
							
							if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
									&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") )
									&& param.getProductPrice()> 100) 
							{								
								orderProductList.get(i).setOrderAmount(tempOrderProduct.getOrderAmount()+param.getProductPrice() );								
							}else {
								if(productTotal.getCustomerProductPrice() > 0)
									orderProductList.get(i).setOrderAmount(tempOrderProduct.getOrderAmount()+(param.getProductCount()* productTotal.getCustomerBottlePrice() ) );
								else 
									orderProductList.get(i).setOrderAmount(tempOrderProduct.getOrderAmount()+(param.getProductCount()* productTotal.getProductBottlePrice()) );
							}
							orderProductList.get(i).setUpdateId(param.getCreateId());
							
							// 주문상품 업데이트
							result = orderService.modifyOrderProductCount(orderProductList.get(i));
							
							//주문정보 업데이트
							if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
									&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") )
									&& param.getProductPrice()> 100 ) 
							{								
								orderTemp.setOrderTotalAmount(orderTemp.getOrderTotalAmount()+(addCount*param.getProductPrice()) );								
							}else {
								if(productTotal.getCustomerBottlePrice() > 0)
									orderTemp.setOrderTotalAmount(orderTemp.getOrderTotalAmount()+(addCount*productTotal.getCustomerBottlePrice() ));
								else
									orderTemp.setOrderTotalAmount(orderTemp.getOrderTotalAmount()+(addCount*productTotal.getProductBottlePrice()));
							}
							result = orderService.modifyOrderAdditionBottles(orderTemp);
							orderProductList.get(i).setOrderCount(0);	
							
							if(orderTemp.getProductCount() > 0)
								receivableAmount += orderTemp.getOrderTotalAmount()/orderTemp.getProductCount();
							else
								receivableAmount += orderTemp.getOrderTotalAmount();
						}else if(addCount < 0) { // 기존 주문 상품을 전체 납품을 못할 경우
							
						}else if(addCount == 0) {	//해당 상품 납품 완료							
							orderProductList.get(i).setOrderCount(0);
						}
						orderProductList.get(i).setOrderCount(addCount);
						isNewProduct = false;
					}else {
						// 다를경우 판매상품 주문 등록(TB_Order 업데이트, TB_order_product 추가)										
					}
				}

				boolean orderCompleted = true;
				for(int i = 0 ; i < orderProductList.size();i++) {
					OrderProductVO tempOrderProduct = orderProductList.get(i);	
					
					if(tempOrderProduct.getOrderCount() > 0 ) orderCompleted = false;
				}

				//logger.debug("WorkReportServiceImpl registerWorkNoBottle  isNewProduct=" + isNewProduct );
				if(isNewProduct) {	// 신규 상품 주문 추가
					
					OrderProductVO newOrderProduct = new OrderProductVO();
					newOrderProduct.setOrderId(orderTemp.getOrderId());
					newOrderProduct.setOrderProductSeq(orderProductList.size()+1);
					newOrderProduct.setProductId(param.getProductId());
					newOrderProduct.setProductPriceSeq(param.getProductPriceSeq());
					newOrderProduct.setOrderCount(param.getProductCount());
					if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
							&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") ) 
							&& param.getProductPrice()> 100 ) 
					{
						newOrderProduct.setOrderAmount(param.getProductCount() * param.getProductPrice() );
					}else {
						if( productTotal.getCustomerBottlePrice() > 0)
							newOrderProduct.setOrderAmount(productTotal.getCustomerBottlePrice()*param.getProductCount());
						else 
							newOrderProduct.setOrderAmount(productTotal.getProductBottlePrice()*param.getProductCount());
					}
					newOrderProduct.setOrderProductEtc("추가상품");
					newOrderProduct.setCreateId(param.getCreateId());
					newOrderProduct.setUpdateId(param.getUpdateId());					
					newOrderProduct.setBottleSaleYn("N");
					newOrderProduct.setBottleChangeYn("N");
				
					// 주문상품 추가
					result = orderService.registerOrderProduct(newOrderProduct);
					
					// 주문 정보 업데이트					
					orderTemp.setOrderTotalAmount(orderTemp.getOrderTotalAmount()+newOrderProduct.getOrderAmount());					
					orderTemp.setOrderProductNm(orderProductList.get(0).getProductNm()+" 외 "+orderProductList.size());
					orderTemp.setOrderProductCapa(orderProductList.get(0).getProductCapa()+" 외 "+orderProductList.size());					
					orderTemp.setUpdateId(param.getUpdateId());
					
					result = orderService.modifyOrderAdditionBottles(orderTemp);
					
					receivableAmount += newOrderProduct.getOrderAmount();
				}				
				
				if(!registerFlag) {	//WorkReport 업데이트
					workReport.setWorkProductNm(orderProductList.get(0).getProductNm()+" 외 "+(orderProductList.size()-1));
					workReport.setWorkProductCapa(orderProductList.get(0).getProductCapa()+" 외 "+(orderProductList.size()-1));
					workReport.setWorkReportSeq(workReportSeq);
					workReport.setOrderId(orderTemp.getOrderId());
					result = workMapper.modifyWorkReportProduct(workReport);
					
				}else {	// WorkReport 신규 등록
					workReport.setWorkReportSeq(workReportSeq);
					workReport.setOrderId(orderTemp.getOrderId());
					workReport.setWorkProductNm(productTotal.getProductNm());
					workReport.setWorkProductCapa(productTotal.getProductCapa());
					workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.sale"));
					
					result = workMapper.insertWorkReport(workReport);
				}
								
				List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();
				
				for(int i = 0 ; i < param.getProductCount() ; i++) {					
				
					WorkBottleVO addWorkBottle = new WorkBottleVO();
					
					addWorkBottle.setWorkReportSeq(workReportSeq);
					addWorkBottle.setCustomerId(param.getCustomerId());
					/*
					if(registeredWorkBottleList.size()>=1)
						addWorkBottle.setWorkSeq(registeredWorkBottleList.size()+1);					
					else 
					*/
					addWorkBottle.setWorkSeq(workSeq++);
					addWorkBottle.setBottleWorkCd(param.getBottleWorkCd());
					addWorkBottle.setProductId(param.getProductId());
					addWorkBottle.setProductPriceSeq(param.getProductPriceSeq());
					if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
							&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") )
							&& param.getProductPrice()> 100 ) 
					{
						addWorkBottle.setProductPrice(param.getProductPrice()); 
					}else {
						if(productTotal.getCustomerProductPrice() > 0)
							addWorkBottle.setProductPrice(productTotal.getCustomerBottlePrice()); 
						else
							addWorkBottle.setProductPrice(productTotal.getProductBottlePrice());
					}
					
					addWorkBottle.setProductCapa(productTotal.getProductCapa());
					addWorkBottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.sale"));
					addWorkBottle.setBottleSaleYn("N");
					addWorkBottle.setCreateId(param.getCreateId());
					addWorkBottle.setUpdateId(param.getCreateId());			
					
					workBottleList.add(addWorkBottle);
				}
				//
				result = workMapper.insertWorkBottles(workBottleList);
				
				//Order 정보 확인 후  업데이트 필요
				orderProductList = orderService.getOrderProductList(orderTemp.getOrderId());
				registeredWorkBottleList = getWorkBottleListAndCountOfOrder(orderTemp.getOrderId());
								
				for(int i = 0 ; i < orderProductList.size();i++) {
					OrderProductVO tempOrderProduct = orderProductList.get(i);	
					
					for(int j = 0 ; j < registeredWorkBottleList.size() ; j++) {
						
						WorkBottleRegisterVO tempRegisteredBottle = registeredWorkBottleList.get(j);
						
						if(tempOrderProduct.getProductId() == tempRegisteredBottle.getProductId() && tempOrderProduct.getProductPriceSeq() == tempRegisteredBottle.getProductPriceSeq()) {
							int leftCount  = tempOrderProduct.getOrderCount()-tempRegisteredBottle.getRegisteredCount();
							//logger.debug("WorkReportServiceImpl ****** - registerWorkNoBottle  leftCount=" + leftCount );
							//leftOrderProduct +=leftCount;
							if(leftCount < 0) leftCount = 0;
							// 주문 상품의 카운트 셋팅(0이면 이미 주문 처리된 것임)
							orderProductList.get(i).setOrderCount(leftCount);							
						}
					}					
				}
				
				//int leftOrderProduct = 0;
				for(int i = 0 ; i < orderProductList.size();i++) {
					OrderProductVO tempOrderProduct = orderProductList.get(i);	
					//logger.debug("WorkReportServiceImpl ****** - registerWorkNoBottle  tempOrderProduct.getOrderCount()=" + tempOrderProduct.getOrderCount() );
					//leftOrderProduct += tempOrderProduct.getOrderCount();					
				}				
				// Order 상태 정보 변경
				List<OrderProductVO> orderProduct = orderService.getOrderProductList(orderTemp.getOrderId());
				int remainCount = 0;
				for(int j=0; j < orderProduct.size() ; j++) {
					remainCount += orderProduct.get(j).getSalesCount();
				}
				logger.debug(" ----------- WorkReportServiceImpl registerWorkReportForOrder  remainCount=" + remainCount );
				if(remainCount == 0) orderCompleted = true;			
				//if(isGasCompleted && leftOrderProduct <= 0) {
				if(orderCompleted) {
					
					orderTemp.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.delivery"));
					orderTemp.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
					orderTemp.setSalesId(param.getCreateId());
					result = orderService.changeOrderProcessCd(orderTemp);
				}
			}else {
				// order 정보 등록 (Tb_Order, Tb_Order_Product)
				orderTemp = new OrderVO();
				int orderId = orderService.getOrderId();
				
				orderTemp.setOrderId(Integer.valueOf(orderId));
				orderTemp.setMemberCompSeq(Integer.parseInt(PropertyFactory.getProperty("common.Member.Comp.Daehan")) );
				orderTemp.setCustomerId(param.getCustomerId());
				orderTemp.setOrderTypeCd(PropertyFactory.getProperty("common.code.order.type.order"));	//상품주문
				orderTemp.setProductCount(param.getProductCount());
				
				Calendar cal = Calendar.getInstance();
				int amPm = cal.get(Calendar.AM_PM);
				orderTemp.setDeliveryReqDt(cal.getTime());
				
				if (amPm == 1 ) orderTemp.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.PM"));
				else  orderTemp.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.AM"));		
				
				orderTemp.setOrderEtc("현장주문");
				orderTemp.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.delivery"));
				if(param.getUserId()!=null && param.getUserId().length() > 0) {
					orderTemp.setSalesId(param.getUserId());
					orderTemp.setCreateId(param.getUserId());
				}else {
					orderTemp.setSalesId(param.getCreateId());
					orderTemp.setCreateId(param.getCreateId());
				}
				orderTemp.setOrderProductNm(productTotal.getProductNm());
				orderTemp.setOrderProductCapa(productTotal.getProductCapa());
				orderTemp.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
				
				if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
						&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") ) 
						&& param.getProductPrice()> 100 ) 
				{
					orderTemp.setOrderTotalAmount(param.getProductPrice());					
				}else {
					if(productTotal.getCustomerBottlePrice()> 0 )
						orderTemp.setOrderTotalAmount(param.getProductCount()*productTotal.getCustomerBottlePrice());
					else
						orderTemp.setOrderTotalAmount(param.getProductCount()*productTotal.getProductBottlePrice());
				}
				
				//order정보 등록
				result = orderService.registerOrder(orderTemp);
				receivableAmount += orderTemp.getOrderTotalAmount();
				
				OrderProductVO orderProduct = new OrderProductVO();
				
				orderProduct.setOrderId(orderTemp.getOrderId());
				orderProduct.setOrderProductSeq(1);
				orderProduct.setProductId(param.getProductId());
				orderProduct.setProductPriceSeq(param.getProductPriceSeq());
				orderProduct.setProductCapa(productTotal.getProductCapa());
				orderProduct.setOrderCount(param.getProductCount());
				if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
						&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") ) 
						&& param.getProductPrice()> 100 ) 
				{
					orderProduct.setOrderAmount(param.getProductPrice() );
				}else {
					if(productTotal.getCustomerBottlePrice() > 0)
						orderProduct.setOrderAmount(param.getProductCount()* productTotal.getCustomerBottlePrice() );
					else 
						orderProduct.setOrderAmount(param.getProductCount()* productTotal.getProductBottlePrice() );
				}
				//orderProduct.setOrderAmount(param.getProductCount()*productTotal.getProductPrice());
				orderProduct.setOrderProductEtc("무주문상품");
				orderProduct.setCreateId(param.getCreateId());
				orderProduct.setUpdateId(param.getCreateId());
				orderProduct.setBottleChangeYn("N");
				orderProduct.setBottleSaleYn("N");
				//order product 등록
				result = orderService.registerOrderProduct(orderProduct);
				
				//WorkReport 등록 및 업데이트				
				workReport.setWorkReportSeq(workReportSeq);
				workReport.setOrderId(orderTemp.getOrderId());
				workReport.setOrderProductNm(productTotal.getProductNm());
				workReport.setOrderProductCapa(productTotal.getProductCapa());
				workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.sale"));				
				
				//WorkReport 등록
				if(registerFlag)
					result = workMapper.insertWorkReport(workReport);	
				
				//WorkReportBottle 등록
				List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();
				for(int i = 0 ; i < param.getProductCount() ; i++) {		
					
					WorkBottleVO addWorkBottle = new WorkBottleVO();
					
					addWorkBottle.setWorkReportSeq(workReportSeq);
					addWorkBottle.setCustomerId(param.getCustomerId());
					addWorkBottle.setWorkSeq(workSeq++);
					addWorkBottle.setBottleWorkCd(param.getBottleWorkCd());
					addWorkBottle.setProductId(param.getProductId());
					addWorkBottle.setProductPriceSeq(param.getProductPriceSeq());
					
					if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
							&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") ) 
							&& param.getProductPrice()> 100 ) 
					{
						addWorkBottle.setProductPrice(param.getProductPrice()); 
					}else {
						
						if(productTotal.getCustomerBottlePrice() > 0)
							addWorkBottle.setProductPrice(productTotal.getCustomerBottlePrice()); 
						else
							addWorkBottle.setProductPrice(productTotal.getProductBottlePrice());
					}
					addWorkBottle.setProductCapa(productTotal.getProductCapa());
					addWorkBottle.setBottleSaleYn("N");
					if(param.getUserId()!=null && param.getUserId().length() > 0) 
						addWorkBottle.setCreateId(param.getUserId());
					else
						addWorkBottle.setCreateId(param.getCreateId());
					addWorkBottle.setUpdateId(param.getCreateId());
					 
					workBottleList.add(addWorkBottle);
				}
				logger.debug("--registerWorkNoBottle  workBottleList.size=" + workBottleList.size() );
				result = workMapper.insertWorkBottles(workBottleList);			
				
			}			
		}
		CashFlowVO cashFlow = new CashFlowVO();
		cashFlow.setCustomerId(param.getCustomerId());
		cashFlow.setReceivableAmount(receivableAmount);
		if(param.getUserId()!=null && param.getUserId().length() > 0) 
			cashFlow.setCreateId(param.getUserId());
		else
			cashFlow.setCreateId(param.getCreateId());
		
		result = cashService.registerCashFlow(cashFlow);
		return result;
	}


	@Override
	public int modifyWorkReportReceivedAmount(WorkReportVO param) {		
		return workMapper.updateWorkReportReceivedAmount(param);
	}


	@Override
	public int registerWorkReportOnly(WorkReportVO param) {		
		return workMapper.insertWorkReport(param);
	}


	@Override
	public int getWorkReportSeqForCustomer(WorkReportVO param) {
		return workMapper.selectWorkReportSeqForCustomer(param);
	}
	

	@Override
	public int modifyWorkBottlePrice(WorkBottleVO param) {		
		return workMapper.updateWorkBottlePrice(param);
	}

	@Override
	public int modifyWorkBottleManual(HttpServletRequest request, WorkReportVO param) {
		
		int result = 0;
		try {
			int productCount = 0;
			
			if(request.getParameter("productCount") !=null) productCount = Integer.parseInt(request.getParameter("productCount"));
			
			List<WorkBottleVO> beforeWorkBottleList = workMapper.selectWorkBottleList(param.getWorkReportSeq());
			
			List<OrderProductVO> orderProductList = orderService.getOrderProductList(param.getOrderId());
			
			List<WorkBottleVO> afterWorkBottleList = new ArrayList<WorkBottleVO>();							
			
			for(int i=0 ; i < productCount ; i++ ) {
				WorkBottleVO workBottle = new WorkBottleVO();
				workBottle.setCreateId(param.getUserId());
				workBottle.setUpdateId(param.getUpdateId());
				workBottle.setCustomerId(param.getCustomerId());
				workBottle.setOrderId(param.getOrderId());
				
				boolean rightYn = true;
				
				if(request.getParameter("bottleWorkCd_"+i) !=null) workBottle.setBottleWorkCd(request.getParameter("bottleWorkCd_"+i)); 
				else rightYn = false;
				
				if(request.getParameter("productId_"+i) !=null) workBottle.setProductId(Integer.parseInt(request.getParameter("productId_"+i))); 
				else rightYn = false;
				
				if(request.getParameter("productPriceSeq_"+i) !=null) workBottle.setProductPriceSeq(Integer.parseInt(request.getParameter("productPriceSeq_"+i)));
				else rightYn = false;
				
				if(request.getParameter("productCount_"+i) !=null) workBottle.setProductCount(Integer.parseInt(request.getParameter("productCount_"+i)));
				else rightYn = false;
				
				workBottle.setNewYn("Y");
				workBottle.setNewProductYn("Y");
				//logger.debug("WorkReportServiceImpl --modifyWorkBottleManual  workBottle.bottleWorkCd=" + workBottle.getBottleWorkCd() );
				if(rightYn) afterWorkBottleList.add(workBottle);				
			}
			
			//이전 WorkBottle 과 비교
			
			Integer customerId = 0;
			for(int j=0; j< afterWorkBottleList.size() ; j++) {
				WorkBottleVO afterWorkBottle = afterWorkBottleList.get(j);		
				afterWorkBottleList.get(j).setWorkReportSeq(param.getWorkReportSeq());				
				
				for(int i =0 ; i< beforeWorkBottleList.size() ; i++) {
					WorkBottleVO beforeWorkBottle = beforeWorkBottleList.get(i);		
					customerId = beforeWorkBottle.getCustomerId();
					beforeWorkBottle.setCreateId(param.getCreateId());;
					beforeWorkBottle.setWorkReportSeq(param.getWorkReportSeq());
					
					if(beforeWorkBottle.getBottleWorkCd().equals(afterWorkBottle.getBottleWorkCd() )
							&& beforeWorkBottle.getProductId() == afterWorkBottle.getProductId() 
							&& beforeWorkBottle.getProductPriceSeq() == afterWorkBottle.getProductPriceSeq()){		
						
						int remainCount = afterWorkBottle.getProductCount() - beforeWorkBottle.getProductCount();
						afterWorkBottle.setGasId(beforeWorkBottle.getGasId());
						//afterWorkBottle.setProductCount(remainCount);
						afterWorkBottle.setNewYn("N");
						afterWorkBottle.setProductPrice(beforeWorkBottle.getProductPrice());
						afterWorkBottle.setBottleType(beforeWorkBottle.getBottleType());
						afterWorkBottle.setProductPrice(beforeWorkBottle.getProductPrice());
						
						beforeWorkBottle.setProductCount(remainCount);						
						//logger.debug("WorkReportServiceImpl --modifyWorkBottleManual  remainCount=" + remainCount );
						// 남은처리 workBottle 처리 
						if(remainCount > 0) { // 추가							
							if(beforeWorkBottle.getGasId() > 0 ) {
								result = addWorkBottle(beforeWorkBottle);		
								result = modifyCustomerProduct(beforeWorkBottle);
							}else {
								result = addWorkBottleNoGas(beforeWorkBottle);	
							}
						}else if(remainCount < 0 ){
							result = modifyCustomerProduct(beforeWorkBottle);
							result = minusWorkBottle(beforeWorkBottle);							
						}
						// orderProduct처리 -orderBottle도 처리
					}					
				}
				
				//판매 & 대여
				if(!afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))) {
					for(int i =0 ; i <orderProductList.size() ; i++) {
						OrderProductVO orderProduct = orderProductList.get(i);
						
						if(orderProduct.getProductId() == afterWorkBottle.getProductId() 
								&& orderProduct.getProductPriceSeq() == afterWorkBottle.getProductPriceSeq()){
							
							// 같은 경우
							if( (orderProduct.getBottleChangeYn().equals("Y") && afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent")) )
									|| (orderProduct.getBottleChangeYn().equals("N") && afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) )  ) {
								
								afterWorkBottle.setNewProductYn("N");
								int remainCount = afterWorkBottle.getProductCount() - orderProduct.getOrderCount();
								
								orderProduct.setSalesCount(remainCount);
								
								if(remainCount > 0) {
									if(orderProduct.getGasId() > 0) {
										result = addOrderProduct(orderProduct);	
									}else {
										result = addOrderProductNoGas(orderProduct);	
									}
								}else if(remainCount < 0 ){
									orderProduct.setCustomerId(customerId);
									result = minusOrderProduct(orderProduct);
								}
							}
						}
					} //for(int i =0 ; i <orderProductList.size() ; i++) {
				}						
			}			
			// afeterWorkBottle 의 productCount 만큼 가감
			//result = changeWorkBottle(afterWorkBottleList);			
			
			result = addNewWorkBottle(getNewWorkBottle(afterWorkBottleList,1));			
			
			result = addNewOrderProduct(getNewWorkBottle(afterWorkBottleList,-1));		
			
			//order 처리
			result = modifyOrderInfo(param);
			
		} catch (DataAccessException e) {		
			e.printStackTrace();
			logger.error("WorkReportServiceImpl modifyWorkBottleManual", e.toString());
		} catch (Exception e) {			
			e.printStackTrace();
			logger.error("WorkReportServiceImpl modifyWorkBottleManual", e.toString());
		}
		return result;
	}
	
	private int addWorkBottle(WorkBottleVO param) {
		int result= 1;
		
		BottleVO bottle = new BottleVO();
		bottle.setProductId(param.getProductId());
		bottle.setProductPriceSeq(param.getProductPriceSeq());
		bottle.setGasId(param.getGasId());
		
		bottle = bottleService.getDummyBottle(bottle);
		
		List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();		
		int workSeq = workMapper.selectWorkBottleSeq(param.getWorkReportSeq());
		
		for(int i=0; i < param.getProductCount() ; i++) {
			WorkBottleVO workBottle = param;
			
			workBottle.setWorkSeq(workSeq++);
			workBottle.setBottleId(bottle.getBottleId());
			workBottle.setBottleBarCd(bottle.getBottleBarCd());
			
			workBottleList.add(workBottle);
		}
		if(workBottleList.size() > 0)
			result = workMapper.insertWorkBottles(workBottleList);
		
		return result;
	}
	
	private int addWorkBottleNoGas(WorkBottleVO param) {
		int result= 1;

		List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();		
		int workSeq = workMapper.selectWorkBottleSeq(param.getWorkReportSeq());
		
		for(int i=0; i < param.getProductCount() ; i++) {
			WorkBottleVO workBottle = param;
			
			workBottle.setWorkSeq(workSeq++);		
			workBottle.setProductPrice(param.getProductPrice());
			
			workBottleList.add(workBottle);
		}
		if(workBottleList.size() > 0)
			result = workMapper.insertWorkBottles(workBottleList);
		return result;
	}
	
	private int minusWorkBottle(WorkBottleVO param) {
		int result= 1;

		param.setProductCount(param.getProductCount()*-1);
		List<WorkBottleVO> workBottleList = workMapper.selectWorkBottleListOfProduct(param);
	
		List<Integer> workSeqList = new ArrayList<Integer>();		
		//param.getProductCount()
		int getI=workBottleList.size()-1;
		for(int i=0; i < param.getProductCount() ; i++) {
			workSeqList.add(workBottleList.get(getI--).getWorkSeq());
		}		
	
		param.setWorkSeqList(workSeqList);
		result = workMapper.deleteWorkBottleOfProduct(param);
		return result;
	}
	
	@Transactional
	private int addOrderProduct(OrderProductVO param) {
		int result= 1;
		
		BottleVO bottle = new BottleVO();
		bottle.setProductId(param.getProductId());
		bottle.setProductPriceSeq(param.getProductPriceSeq());
		bottle.setGasId(param.getGasId());
		
		bottle = bottleService.getDummyBottle(bottle);
		
		List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();				
		
		for(int i=0; i < param.getSalesCount() ; i++) {
			OrderBottleVO orderBottle = new OrderBottleVO();			
			
			orderBottle.setOrderId(param.getOrderId());
			orderBottle.setOrderProductSeq(param.getOrderProductSeq());
			
			orderBottle.setProductId(param.getProductId());
			orderBottle.setProductPriceSeq(param.getProductPriceSeq());
			orderBottle.setCreateId(param.getCreateId());
			orderBottle.setBottleBarCd(bottle.getBottleBarCd());
			
			orderBottleList.add(orderBottle);
		}
		if(orderBottleList.size() > 0)
			result = orderService.registerOrderBottles(orderBottleList);
		
		int orderPrice = param.getOrderAmount() / param.getOrderCount();
		
		param.setOrderCount(param.getOrderCount()+param.getSalesCount());
		param.setOrderAmount(param.getOrderAmount()+orderPrice*param.getSalesCount());		
		
		result = orderService.modifyOrderProductCount(param);		
		
		return result;
	}
	
	private int addOrderProductNoGas(OrderProductVO param) {
		int result= 1;

		int orderPrice = param.getOrderAmount() / param.getOrderCount();
		
		param.setOrderCount(param.getOrderCount()+param.getSalesCount());
		param.setOrderAmount(param.getOrderAmount()+orderPrice*param.getSalesCount());		
		
		result = orderService.modifyOrderProductCount(param);
		
		return result;
	}

	@Transactional
	private int minusOrderProduct(OrderProductVO param) {
		int result= 1;

		int orderProductCount = 1;
		
		if(param.getGasId() > 0){
			// OrderBottlte 삭제
			param.setSalesCount(param.getSalesCount()*-1);
			List<OrderBottleVO> orderBottleList = orderService.getOrderBottleListOfProduct(param);
			if(orderBottleList.size() == param.getSalesCount()) orderProductCount = 0;
			
			OrderBottleVO orderBottle = new OrderBottleVO();
			List<Integer> orderList = new ArrayList<Integer>();
			
			int getI=orderBottleList.size()-1;
			
			for(int i = 0 ; i < param.getSalesCount() ; i++) {
				orderList.add(orderBottleList.get(getI).getOrderBottleSeq());
				
				if(orderBottleList.get(getI).getBottleBarCd()!=null && orderBottleList.get(getI).getBottleBarCd().length() > 0) {
					BottleVO bottle = new BottleVO();
					bottle.setBottleBarCd(orderBottleList.get(getI).getBottleBarCd());
					bottle.setCustomerId(param.getCustomerId());
					bottle.setUpdateId(param.getCreateId());
					
					result = bottleService.deleteCustomerIdOfBottle(bottle);
				}
			}
			
			orderBottle.setOrderBottleList(orderList);
			
			if(orderList.size() > 0) {
				orderService.deleteOrderBottle(orderBottle);
			}
		}

		if(orderProductCount > 0) {
		// orderProduct 수정s
			int orderPrice = param.getOrderAmount() / param.getOrderCount();
			
			param.setOrderCount(param.getOrderCount()-param.getSalesCount());
			param.setOrderAmount(param.getOrderAmount()-orderPrice*param.getSalesCount());		
			
			result = orderService.modifyOrderProductCount(param);
		}else {
			result = orderService.deleteOrderProduct(param);
		}
		return result;
	}
	
	private List<WorkBottleVO> getNewWorkBottle(List<WorkBottleVO> params, int flag) {		
		
		List<WorkBottleVO> addWorkBottleList = new ArrayList<WorkBottleVO>();
		
		for(int i = 0 ; i < params.size() ; i++) {
			if(flag > 0) {
				if(params.get(i).getNewYn().equals("Y")) {
					addWorkBottleList.add(params.get(i));				
				}
			}else {
				if(params.get(i).getNewProductYn().equals("Y")) {
					addWorkBottleList.add(params.get(i));				
				}
			}
		}		
		return addWorkBottleList;		
	}
	
	@Transactional
	private int addNewWorkBottle(List<WorkBottleVO> params) {		
		
		int result = 0;
		List<WorkBottleVO> addWorkBottleList = new ArrayList<WorkBottleVO>();
		
		if(params.size() > 0) {
			int workSeq = workMapper.selectWorkBottleSeq(params.get(0).getWorkReportSeq());
			//logger.debug("WorkReportServiceImpl --workSeq" + workSeq);
			for(int i = 0 ; i < params.size() ; i++) {
				
				WorkBottleVO workBottle = params.get(i);
				
				BottleVO bottle = new BottleVO();
				bottle.setProductId(workBottle.getProductId());
				bottle.setProductPriceSeq(workBottle.getProductPriceSeq());
				bottle.setCurrentPage(workBottle.getCustomerId());
							
				ProductTotalVO productTotal = productService.getPrice(bottle);
				
				BottleVO dummy = bottleService.getDummyBottle(bottle);
				
				for(int j=0 ; j < params.get(i).getProductCount() ; j++) {
					WorkBottleVO addWorkBottle = new WorkBottleVO();
					
					addWorkBottle.setWorkReportSeq(workBottle.getWorkReportSeq());
					addWorkBottle.setCustomerId(workBottle.getCustomerId());
					addWorkBottle.setProductId(workBottle.getProductId());
					addWorkBottle.setProductPriceSeq(workBottle.getProductPriceSeq());
					addWorkBottle.setBottleWorkCd(workBottle.getBottleWorkCd());
					addWorkBottle.setCreateId(workBottle.getCreateId());
					addWorkBottle.setBottleWorkCd(workBottle.getBottleWorkCd());
					
					addWorkBottle.setWorkSeq(workSeq);
					workSeq++;
					if(dummy != null) {
						addWorkBottle.setGasId(dummy.getGasId());
						addWorkBottle.setBottleId(dummy.getBottleId());
						addWorkBottle.setBottleBarCd(dummy.getBottleBarCd());
					}
					if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))) {
						addWorkBottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
					}else {
						addWorkBottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.FULL"));
					}
					
					if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))) {
						if(productTotal !=null && productTotal.getCustomerProductPrice() > 0) addWorkBottle.setProductPrice(productTotal.getCustomerProductPrice());
						else addWorkBottle.setProductPrice(productTotal.getProductPrice());
						addWorkBottle.setBottleSaleYn("N");
					}else if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {
						if(productTotal !=null && productTotal.getCustomerBottlePrice() > 0) addWorkBottle.setProductPrice(productTotal.getCustomerBottlePrice());	
						else addWorkBottle.setProductPrice(productTotal.getProductBottlePrice());		
						addWorkBottle.setBottleSaleYn("Y");
					}
					
					addWorkBottleList.add(addWorkBottle);
				}
			}		
			
			if(addWorkBottleList.size() > 0)
				result = workMapper.insertWorkBottles(addWorkBottleList);
			
			result = changeCustomerProduct(addWorkBottleList);
		}else
			result = 1;
		
		return result;		
	}
	
	@Transactional
	private int addNewOrderProduct(List<WorkBottleVO> params) {		
		
		int result = 0;
		
		if(params.size() > 0) {
			List<OrderProductVO> addOrderProductList = new ArrayList<OrderProductVO>();
			List<OrderBottleVO> addOrderBottletList = new ArrayList<OrderBottleVO>();
			
			int orderProductSeq = orderService.getNextOrderProductSeq(params.get(0).getOrderId());
			
			for(int i = 0 ; i < params.size() ; i++) {
				
				OrderProductVO orderProduct = new OrderProductVO();
				WorkBottleVO workBottle = params.get(i);
				if(!workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))) {
					BottleVO bottle = new BottleVO();
					bottle.setProductId(workBottle.getProductId());
					bottle.setProductPriceSeq(workBottle.getProductPriceSeq());
					bottle.setCurrentPage(workBottle.getCustomerId());
								
					ProductTotalVO productTotal = productService.getPrice(bottle);
					
					BottleVO dummy = bottleService.getDummyBottle(bottle);
					
					orderProduct.setOrderId(workBottle.getOrderId());
					orderProduct.setOrderProductSeq(orderProductSeq++);
					orderProduct.setProductId(workBottle.getProductId());
					orderProduct.setProductPriceSeq(workBottle.getProductPriceSeq());
					orderProduct.setOrderCount(workBottle.getProductCount());
					
					orderProduct.setOrderProductEtc(PropertyFactory.getProperty("order.etc.message.work"));
					
					logger.debug("for in --workBottle.getBottleWorkCd()=" + workBottle.getBottleWorkCd());
					if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))) {
						orderProduct.setBottleChangeYn("Y");
						orderProduct.setBottleSaleYn("N");
						
						if(productTotal !=null && productTotal.getCustomerProductPrice() > 0) orderProduct.setOrderAmount(workBottle.getProductCount()*productTotal.getCustomerProductPrice());
						else orderProduct.setOrderAmount(workBottle.getProductCount()*productTotal.getProductPrice());
					}else if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {
						orderProduct.setBottleChangeYn("N");
						orderProduct.setBottleSaleYn("Y");
						
						if(productTotal !=null && productTotal.getCustomerBottlePrice() > 0) orderProduct.setOrderAmount(workBottle.getProductCount()*productTotal.getCustomerBottlePrice());
						else orderProduct.setOrderAmount(workBottle.getProductCount()*productTotal.getProductBottlePrice());
					}
					for(int j=0; j < orderProduct.getOrderCount() ; j++) {
						OrderBottleVO orderBottle = new OrderBottleVO();
						
						orderBottle.setOrderId(orderProduct.getOrderId());
						orderBottle.setOrderProductSeq(orderProduct.getOrderProductSeq());
						orderBottle.setProductId(orderProduct.getProductId());
						orderBottle.setProductPriceSeq(orderProduct.getProductPriceSeq());
						orderBottle.setBottleBarCd(dummy.getBottleBarCd());
						orderBottle.setCreateId(workBottle.getCreateId());
						
						addOrderBottletList.add(orderBottle);					
					}
					orderProduct.setCreateId(workBottle.getCreateId());		
					
					addOrderProductList.add(orderProduct);		
				}
			}		
		
			if(addOrderProductList.size() > 0) {
				result = orderService.registerOrderProducts(addOrderProductList);
				
				result = orderService.registerOrderBottles(addOrderBottletList);
			}else {
				result = 1;
			}
		}
		return result;		
	}

	private int modifyOrderInfo(WorkReportVO param) {
		int result = 0;
		
		List<OrderProductVO> orderProductList = orderService.getOrderProductList( param.getOrderId());
		logger.debug(" --modifyOrderInfo  orderProductList.size=" + orderProductList.size() );
		String orderProductNm = "";
		String orderProductCapa = "";
		int orderTotalAmount = 0;		
		
		for(int i=0 ;i < orderProductList.size() ; i++) {
			orderTotalAmount +=orderProductList.get(i).getOrderAmount();
			if(i==0) {
				orderProductNm = orderProductList.get(i).getProductNm();
				orderProductCapa = orderProductList.get(i).getProductCapa();
			}
		}
		
		if(orderProductList.size()  > 1) {
			orderProductNm +="외 "+ (orderProductList.size()-1); 
			orderProductCapa +="외 "+ (orderProductList.size()-1); 
		}
		OrderVO order = new OrderVO();
		
		order.setOrderId(param.getOrderId());
		order.setUpdateId(param.getUpdateId());
		
		if(orderProductList.size() > 0) {
			order.setOrderProductNm(orderProductNm);
			order.setOrderProductCapa(orderProductCapa);
			order.setOrderTotalAmount(orderTotalAmount);
			
			result = orderService.modifyOrderAdditionBottles(order);
		}else {
			result = orderService.deleteOrder(order);
		}
		return result ;
	}
	
	private int modifyCustomerProduct(WorkBottleVO param) {
		
		int result = 0;
		CustomerProductVO customerProduct = new CustomerProductVO();
		
		customerProduct.setCustomerId(param.getCustomerId());
		customerProduct.setUpdateId(param.getCreateId());
		customerProduct.setProductId(param.getProductId());
		customerProduct.setProductPriceSeq(param.getProductPriceSeq());
		
		if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) ) {			
			customerProduct.setBottleOwnCount(param.getProductCount());		
			result = customerService.modifyCustomerProductOwnCount(customerProduct);
		}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent")) ) {			
			customerProduct.setBottleRentCount(param.getProductCount());			
			result = customerService.modifyCustomerProductRentCount(customerProduct);			
		}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back")) ) {		
			customerProduct.setBottleRentCount(param.getProductCount()*-1);			
			result = customerService.modifyCustomerProductRentCount(customerProduct);
		}
		return result;
	}
	

	@Override
	public List<WorkBottleVO> getWorkBottleListOfUser(WorkReportVO param) {
		
		return workMapper.selectWorkReportListAll(param);
	}

	@Override
	@Transactional
	public int registerWorkReportMassByBottle(WorkReportVO param) {
		
		int result = 0;
		try {
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();		
			List<OrderProductVO> orderProductList = new ArrayList<OrderProductVO>();	
			List<String> bottles = new ArrayList<String>();	
			List<String> productCounts = new ArrayList<String>();	
			
			List<String> strBottleList = null;
			
			if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				strBottleList = StringUtils.makeForeach(param.getBottlesIds(), ","); 							
			}		
			
			List<String> list = null;
			String strBottle = "";
			for(int i = 0 ; i < strBottleList.size() ; i++) {
				list = StringUtils.makeForeach(strBottleList.get(i), "-");
				
				if(list.size()==2) {
					bottles.add(list.get(0));
					strBottle += list.get(0)+",";
					productCounts.add(list.get(1));					
				}				
			}
			param.setBottlesIds(strBottle);
			
			List<BottleVO> bottleList = getBottleList(param);
			
			if(param.getUserId() == null)
				param.setUserId(param.getCreateId());
			
			boolean registerFlag = false;
			int workSeq=1;
			int workReportSeq = getWorkReportSeqForCustomerToday(param);
			
			if(workReportSeq <= 0) {
				workReportSeq = getWorkReportSeq();
				registerFlag = true;
			}else {
				workSeq = workMapper.selectWorkBottleSeq(workReportSeq);
			}
						
			param.setWorkReportSeq(workReportSeq);
			if(registerFlag)
				result = workMapper.insertWorkReport(param);	
			
			for(int i = 0 ; i < bottleList.size() ; i++) {
				BottleVO bottle = bottleList.get(i);
				
				bottle.setCustomerId(param.getCustomerId());
				bottle.setUpdateId(param.getUserId());
				bottle.setBottleWorkCd(param.getBottleWorkCd());
				
				OrderProductVO orderProduct = new OrderProductVO();
				orderProduct.setProductId(bottle.getProductId());
				orderProduct.setProductPriceSeq(bottle.getProductPriceSeq());
				orderProduct.setBottleWorkCd(param.getBottleWorkCd());
				orderProduct.setCustomerId(param.getCustomerId());
				
				for(int j=0;j<bottles.size() ; j++) {

					if(bottle.getBottleId().equals(bottles.get(j)) ) {
						int productCount = Integer.parseInt(productCounts.get(j));
						orderProduct.setOrderCount(Integer.parseInt(productCounts.get(j))) ;
						
						for(int k=0 ; k < productCount ; k++) {							
							
							WorkBottleVO workBottle = new WorkBottleVO();
							workBottle.setWorkReportSeq(param.getWorkReportSeq());
							workBottle.setWorkSeq(workSeq++);
							workBottle.setBottleWorkCd(param.getBottleWorkCd());
							workBottle.setBottleId(bottle.getBottleId());
							workBottle.setBottleBarCd(bottle.getBottleBarCd());
							workBottle.setCustomerId(param.getCustomerId());
							workBottle.setGasId(bottle.getGasId());
							workBottle.setProductId(bottle.getProductId());
							workBottle.setProductPriceSeq(bottle.getProductPriceSeq());
							workBottle.setBottleType(param.getBottleType());										
							workBottle.setCreateId(param.getUserId());
							
							workBottleList.add(workBottle);						
						}
						break;
					}					
				} //for(int j=0;j<bottles.size() ; j++) {
				orderProductList.add(orderProduct);
			}
			
			result = workMapper.insertWorkBottles(workBottleList);			
			
			result = modifyCustomerProductMass(param, orderProductList);
			
		} catch (Exception e) {			
			e.printStackTrace();
			logger.error("registerWorkReportMassByBottle Exception==="+e.toString());
			return -1;
		}		
		
		return result;
	}
	
	@Override
	@Transactional
	public int registerWorkReportMassNoOrder(WorkReportVO param) {
		
		int result = 0;
		try {		
			//기존 주문이 있는지 여부 확인
			OrderVO order =  orderService.getLastOrderForCustomer(param.getCustomerId());
			
			//Bottle 정보 가져오기
			
			List<String> bottles = new ArrayList<String>();	
			List<String> productCounts = new ArrayList<String>();	
			
			List<String> strBottleList = null;
			
			if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				strBottleList = StringUtils.makeForeach(param.getBottlesIds(), ","); 							
			}		
			
			List<String> list = null;
			String strBottle = "";
			for(int i = 0 ; i < strBottleList.size() ; i++) {
				list = StringUtils.makeForeach(strBottleList.get(i), "-");
				
				if(list.size()==2) {
					bottles.add(list.get(0));
					strBottle += list.get(0)+",";
					productCounts.add(list.get(1));					
				}				
			}
			
			if(param.getUserId() == null)
				param.setUserId(param.getCreateId());
			param.setBottlesIds(strBottle);
			
			List<BottleVO> bottleList = getBottleList(param);
						
			//Work_Report_Seq 가져오기
			boolean registerFlag = false;
			int workSeq=1;
			int workReportSeq = getWorkReportSeqForCustomerToday(param);
			
			if(workReportSeq <= 0) {
				workReportSeq = getWorkReportSeq();
				registerFlag = true;
			}else {
				workSeq = workMapper.selectWorkBottleSeq(workReportSeq);
			}
			
			logger.debug(" registerWorkReportMassNoOrder workReportSeq =" + workReportSeq);
			//TB_Work_Reprot 등록
			param.setWorkReportSeq(workReportSeq);
			if(registerFlag)
				result = workMapper.insertWorkReport(param);		
			
			if(order != null ) {
				param.setOrderId(order.getOrderId());
				//logger.debug("WorkReportServiceImpl registerWorkReportMassNoOrder getOrderId =" + param.getOrderId());	
				WorkBottleVO workBottle = new WorkBottleVO();
				workBottle.setWorkReportSeq(workReportSeq);
				workBottle.setWorkSeq(workSeq);
				
				result = registerWorkReportMassForOrder(param, workBottle, order, bottleList, bottles,productCounts);
			}else {
											
				//Order & Order_Product & order_bottle CustomerProduct
				OrderVO orderResult = registerOrderInfo(param,bottleList,bottles,productCounts);			
				
				//WorkReprot
				param.setOrderId(orderResult.getOrderId());
				//param.setOrderAmount(orderResult.getOrderTotalAmount());
				param.setWorkProductNm(orderResult.getOrderProductNm());
				param.setWorkProductCapa(orderResult.getOrderProductCapa());
				param.setUpdateId(param.getUserId());
				
				result = workMapper.modifyWorkReportProduct(param);
				
				//Cash_Flow
				result = registerCashFlow(param,orderResult.getOrderTotalAmount());
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("registerWorkReportMassNoOrder Exception==="+e.toString());
		}
		return result;
	}

	@Transactional
	private OrderVO registerOrderInfo(WorkReportVO param, List<BottleVO> params, List<String> bottles, List<String> productCounts) {
		
		try {
			int result = 0;
			//Order
			OrderVO order = new OrderVO();
			
			int orderId = orderService.getOrderId();
			order.setOrderId(orderId);
			order.setCreateId(param.getUpdateId());
			order.setCustomerId(param.getCustomerId());
			
			order.setMemberCompSeq(1);
			order.setOrderTypeCd(PropertyFactory.getProperty("common.code.order.type.order"));				
			
			Calendar cal = Calendar.getInstance();
			int amPm = cal.get(Calendar.AM_PM);
			order.setDeliveryReqDt(cal.getTime());
			
			if (amPm == 1 ) order.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.PM"));
			else  order.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.AM"));
			
			order.setOrderEtc("현장주문-대량판매");
			order.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.delivery"));
			if(param.getUserId() != null && param.getUserId().length() > 0)
				order.setSalesId(param.getUserId());
			else
				order.setSalesId(param.getCreateId());
			order.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
			order.setUpdateId(param.getUserId());
			
			result = orderService.registerOrder(order);			
			
			List<OrderProductVO> orderProductList = new ArrayList<OrderProductVO>();	
			List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();			
			
			int orderTotalAmount = 0 ;
			String orderProductNm = "";
			String orderProductCapa = "";
			int workSeq = 1;
			
			for(int i = 0 ; i < params.size() ; i++) {
				BottleVO bottle = params.get(i);
				bottle.setCustomerId(param.getCustomerId());
				bottle.setBottleType(param.getBottleType());
				bottle.setUpdateId(param.getUserId());
				bottle.setBottleWorkCd(param.getBottleWorkCd());
				
				ProductTotalVO productTotal = productService.getPrice(bottle);		
				
				if(i==0) {
					orderProductNm =productTotal.getProductNm();
					orderProductCapa = productTotal.getProductCapa();
				}
				OrderProductVO orderProduct = new OrderProductVO();
				
				orderProduct.setOrderId(orderId);
				orderProduct.setOrderProductSeq(i+1);
				orderProduct.setProductId(bottle.getProductId());
				orderProduct.setProductPriceSeq(bottle.getProductPriceSeq());
				orderProduct.setCreateId(param.getUserId());
				orderProduct.setOrderProductEtc(PropertyFactory.getProperty("order.etc.message.customer"));
				
				for(int j=0;j<bottles.size() ; j++) {
					
					if(bottle.getBottleId().equals(bottles.get(j)) ) {
						orderProduct.setOrderCount(Integer.parseInt(productCounts.get(j))) ;
						break;
					}
					else orderProduct.setOrderCount(1);
				}
				
				if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {					
					if(productTotal.getCustomerBottlePrice() > 0) 
						orderProduct.setOrderAmount(orderProduct.getOrderCount() * productTotal.getCustomerBottlePrice());
					else 
						orderProduct.setOrderAmount(orderProduct.getOrderCount() * productTotal.getProductBottlePrice());					
					
					orderProduct.setBottleChangeYn("N");
					orderProduct.setBottleSaleYn("Y");
				}else {
					if(productTotal.getCustomerProductPrice() > 0)
						orderProduct.setOrderAmount(orderProduct.getOrderCount() * productTotal.getCustomerProductPrice());
					else
						orderProduct.setOrderAmount(orderProduct.getOrderCount() * productTotal.getProductPrice());
					
					orderProduct.setBottleChangeYn("Y");
					orderProduct.setBottleSaleYn("N");
				}
				orderTotalAmount += orderProduct.getOrderAmount();
				
				orderProductList.add(orderProduct);
				
				for(int k =0 ; k < orderProduct.getOrderCount() ; k++) {
					OrderBottleVO orderBottle = new OrderBottleVO();
					orderBottle.setCreateId(param.getUserId());
					orderBottle.setOrderId(orderId);					
					orderBottle.setOrderProductSeq(orderProduct.getOrderProductSeq());
					orderBottle.setBottleBarCd(bottle.getBottleBarCd());
					orderBottle.setProductId(bottle.getProductId());
					orderBottle.setProductPriceSeq(bottle.getProductPriceSeq());
					
					orderBottleList.add(orderBottle);
					
					WorkBottleVO workBottle = makeWorkBottle(bottle);
					workBottle.setWorkReportSeq(param.getWorkReportSeq());
					workBottle.setWorkSeq(workSeq++);
					workBottle.setBottleSaleYn(orderProduct.getBottleSaleYn());
					workBottle.setProductPrice(orderProduct.getOrderAmount()/orderProduct.getOrderCount());	
					/*
					WorkBottleVO workBottle = new WorkBottleVO();
					workBottle.setWorkReportSeq(param.getWorkReportSeq());
					workBottle.setWorkSeq(workSeq++);
					workBottle.setBottleWorkCd(param.getBottleWorkCd());
					workBottle.setBottleId(bottle.getBottleId());
					workBottle.setBottleBarCd(bottle.getBottleBarCd());
					workBottle.setCustomerId(param.getCustomerId());
					workBottle.setGasId(bottle.getGasId());
					workBottle.setProductId(bottle.getProductId());
					workBottle.setProductPriceSeq(bottle.getProductPriceSeq());
					workBottle.setBottleType(param.getBottleType());
					workBottle.setBottleSaleYn(orderProduct.getBottleSaleYn());
					workBottle.setProductPrice(orderProduct.getOrderAmount()/orderProduct.getOrderCount());					
					workBottle.setCreateId(param.getUserId());
					*/
					workBottleList.add(workBottle);
				}
				
			}
			if(params.size() > 1) {
				orderProductNm +="외"+ (params.size()-1);
				orderProductCapa +="외"+ (params.size()-1);
			}
			order.setOrderProductNm(orderProductNm);
			order.setOrderProductCapa(orderProductCapa);			
			order.setOrderTotalAmount(orderTotalAmount);
			
			//result = orderService.registerOrderAndProduct(order,orderProductList); 2020-08-28 수저
			result = orderService.modifyOrderRegiProduct(order,orderProductList);
			
			if(orderBottleList.size() > 0)
				result = orderService.registerOrderBottles(orderBottleList);
			
			if(workBottleList.size() > 0)
				result = workMapper.insertWorkBottles(workBottleList);
			
			result = modifyCustomerProductMass(param, orderProductList);
			
			if(result > 0) 			
				return order;
			else
				return null;
		} catch (DataAccessException e) {
			e.printStackTrace();
			logger.error("registerWorkReportForOrder Exception==="+e.toString());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("registerWorkReportForOrder Exception==="+e.toString());
			return null;
		}
	}
	
		
	private int modifyCustomerProductMass(WorkReportVO param, List<OrderProductVO> params) {
		int result = 0 ;
		try {
			List<CustomerProductVO> customerProductList = customerService.getCustomerProductList(param.getCustomerId());
			List<CustomerProductVO> registerCustomerProductList = new ArrayList<CustomerProductVO>();
			
			for(int i = 0 ; i < params.size() ; i++ ) {
				OrderProductVO orderProduct = params.get(i);
				boolean isReg = true;
				
				if(customerProductList !=null) {
					for(int j=0 ; j < customerProductList.size() ; j++) {
						CustomerProductVO  customerProduct = customerProductList.get(j);
						customerProduct.setCustomerId(param.getCustomerId());
						customerProduct.setUpdateId(param.getUserId());
						
						if(orderProduct.getProductId()== customerProduct.getProductId() && orderProduct.getProductPriceSeq() == customerProduct.getProductPriceSeq()) {
							if(orderProduct.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {
								customerProduct.setBottleOwnCount(orderProduct.getOrderCount());
								result = customerService.modifyCustomerProductOwnCount(customerProduct);
							}else if(orderProduct.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))) {
								customerProduct.setBottleRentCount(orderProduct.getOrderCount());
								result = customerService.modifyCustomerProductRentCount(customerProduct);
							}else if(orderProduct.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))) {
								customerProduct.setBottleRentCount(orderProduct.getOrderCount()*-1);
								result = customerService.modifyCustomerProductRentCount(customerProduct);
							}
							isReg = false;						
						}
					}
				}
				if(isReg) {
					CustomerProductVO customerP = new CustomerProductVO();
					customerP.setCreateId(param.getUserId());
					customerP.setCustomerId(param.getCustomerId());
					customerP.setProductId(orderProduct.getProductId());
					customerP.setProductPriceSeq(orderProduct.getProductPriceSeq());
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {
						customerP.setBottleOwnCount(orderProduct.getOrderCount());
					}else if(orderProduct.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))) {
						customerP.setBottleRentCount(orderProduct.getOrderCount());
					}else if(orderProduct.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))) {
						customerP.setBottleRentCount(orderProduct.getOrderCount()*-1);
					}					
					registerCustomerProductList.add(customerP);
				}
			}
			
			if(registerCustomerProductList.size() > 0) {
				result = customerService.registerCustomerProducts(registerCustomerProductList);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("registerWorkReportForOrder Exception==="+e.toString());
			return -1;
		}
		return result ;
		
	}
	
	
	
	
	@Transactional
	private int registerOrderProductDummy(WorkBottleVO param, OrderProductVO orderProduct) {
		int result = 0;
		
		List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();	
		
		orderProduct.setProductId(param.getProductId());
		orderProduct.setOrderCount(param.getProductCount());
				
		orderProduct.setOrderProductEtc(PropertyFactory.getProperty("order.etc.message.noOrder"));					
		
		if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {
			orderProduct.setBottleChangeYn("N");
			orderProduct.setBottleSaleYn("Y");
		}else {
			orderProduct.setBottleChangeYn("Y");
			orderProduct.setBottleSaleYn("N");
		}
		orderProduct.setCreateId(param.getUserId());
		
		result = orderService.registerOrderProduct(orderProduct);
		
		//Order_Bottle
		for(int i =0 ; i < param.getProductCount() ; i++) {
			OrderBottleVO orderBottle = new OrderBottleVO();
			orderBottle.setOrderId(orderProduct.getOrderId());
			orderBottle.setOrderProductSeq(orderProduct.getOrderProductSeq());
			orderBottle.setProductId(param.getProductId());
			orderBottle.setProductPriceSeq(param.getProductPriceSeq());
			orderBottle.setBottleBarCd(param.getBottleBarCd());
			orderBottle.setCreateId(param.getUserId());
			
			orderBottleList.add(orderBottle);					
		}		
		// Order_Bottle 등록
		if(orderBottleList.size() > 0)
			result = orderService.registerOrderBottles(orderBottleList);
		
		return result;		
	}	
	
	private int registerCashFlow(WorkReportVO param,int receivableAmount) {
		int result = 0;
		
		CashFlowVO cashFlow = new CashFlowVO();
		cashFlow.setCustomerId(param.getCustomerId());
		cashFlow.setReceivableAmount(receivableAmount);
		cashFlow.setIncomeWay("");
		if(param.getUserId() != null && param.getUserId().length() > 0)
			cashFlow.setCreateId(param.getUserId());
		else
			cashFlow.setCreateId(param.getCreateId());
		
		result = cashService.registerCashFlow(cashFlow);
				
		return result;
		
	}

	@Transactional
	private int registerWorkReportMassForOrder(WorkReportVO param, WorkBottleVO workB, OrderVO order, List<BottleVO> bottleList, List<String> bottles, List<String> productCounts) {
		int result = 0;
		
		try {
			int workSeq = workB.getWorkSeq();
			List<OrderProductVO> soldOrderProductList = new ArrayList<OrderProductVO>();	
			List<OrderProductVO> remainOrderProductList = new ArrayList<OrderProductVO>();	
			List<OrderProductVO> addOrderProductList = new ArrayList<OrderProductVO>();	
			
			List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();	
			
			//Order_Product 비교
			List<OrderProductVO> orderProductList = orderService.getOrderProductList(order.getOrderId());
			
			List<OrderBottleVO> orderBottleListNot = orderService.getOrderBottleListNotDelivery(order.getOrderId());
			int addOrderTotalAmount = 0;
			int orderTotalAmount = 0;
			
			for(int i = 0 ; i < bottleList.size() ; i++) {
				BottleVO bottle = bottleList.get(i);
				OrderProductVO orderProduct = new OrderProductVO();
				
				orderProduct.setOrderId(order.getOrderId());			
				orderProduct.setProductId(bottle.getProductId());
				orderProduct.setProductPriceSeq(bottle.getProductPriceSeq());
				orderProduct.setBottleId(bottle.getBottleId());
				orderProduct.setBottleBarCd(bottle.getBottleBarCd());
				orderProduct.setGasId(bottle.getGasId());
				
				for(int j=0;j<bottles.size() ; j++) {
					if(bottle.getBottleId().equals(bottles.get(j)) ) {						
						orderProduct.setOrderCount(Integer.parseInt(productCounts.get(j))) ;
						break;
					}
					else orderProduct.setOrderCount(1);
				}
				soldOrderProductList.add(orderProduct);
			}
			
			int orderProductCount = 0;
			
			
			for(int j=0; j < soldOrderProductList.size() ; j++) {
				OrderProductVO orderProduct = soldOrderProductList.get(j);
				orderProductCount = orderProduct.getOrderCount();				
				logger.debug(" registerWorkReportMassForOrder orderProductCount0 "+orderProductCount);
				
				BottleVO bottle = new BottleVO();
				bottle.setCustomerId(param.getCustomerId());
				bottle.setProductId(orderProduct.getProductId());
				bottle.setProductPriceSeq(orderProduct.getProductPriceSeq());
				
				ProductTotalVO productTotal = productService.getPrice(bottle);	
				
				for(int k =0 ; k < orderProduct.getOrderCount() ; k++) {					
					
					for(int i = 0 ; i < orderBottleListNot.size() ; i++) {
						OrderBottleVO orderBottle = orderBottleListNot.get(i);
						
						if(orderProductCount > 0 && orderBottle.getBottleBarCd() ==null
								&& orderBottle.getProductId() == orderProduct.getProductId() && orderBottle.getProductPriceSeq() == orderProduct.getProductPriceSeq()) {
							orderBottle.setBottleBarCd(orderProduct.getBottleBarCd());
							
							WorkBottleVO workBottle = new WorkBottleVO();
							
							workBottle.setWorkReportSeq(param.getWorkReportSeq());
							workBottle.setWorkSeq(workSeq++);
							workBottle.setCustomerId(param.getCustomerId());
							workBottle.setBottleId(orderProduct.getBottleId());
							workBottle.setBottleBarCd(orderProduct.getBottleBarCd());
							workBottle.setGasId(orderProduct.getGasId());						
							workBottle.setCreateId(param.getUserId());
							workBottle.setProductId(orderProduct.getProductId());
							workBottle.setProductPriceSeq(orderProduct.getProductPriceSeq());		
							workBottle.setBottleWorkCd(param.getBottleWorkCd());
							workBottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.FULL"));
							
							if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {	
								workBottle.setBottleSaleYn("Y");
								if(productTotal.getCustomerBottlePrice() > 0) 
									workBottle.setProductPrice(productTotal.getCustomerBottlePrice());
								else 
									workBottle.setProductPrice( productTotal.getProductBottlePrice());	
							}else {
								workBottle.setBottleSaleYn("N");
								if(productTotal.getCustomerProductPrice() > 0)
									workBottle.setProductPrice(productTotal.getCustomerProductPrice());
								else
									workBottle.setProductPrice(productTotal.getProductPrice());					
							}
							orderTotalAmount += workBottle.getProductPrice();
							workBottleList.add(workBottle); 
							orderProductCount--;
							
						}
					}
				} //for(int k =0 ; k < orderProduct.getOrderCount() ; k++) {
				logger.debug(" registerWorkReportMassForOrder orderProductCount2 ="+orderProductCount);
				if(orderProductCount > 0 ) {
					orderProduct.setOrderCount(orderProductCount);		
					
					remainOrderProductList.add(orderProduct);
				}
			}
			
			for(int i = 0 ; i < orderBottleListNot.size() ; i++) {
				OrderBottleVO orderBottle = orderBottleListNot.get(i);
				//logger.debug("WorkReportServiceImpl registerWorkReportMassForOrder getOrderBottleSeq "+orderBottle.getOrderBottleSeq());
				//logger.debug("WorkReportServiceImpl registerWorkReportMassForOrder orderBottleID "+orderBottle.getBottleId());
				orderBottle.setUpdateId(param.getUserId());
				if(orderBottle.getBottleBarCd() != null) {
					result = orderService.modifyOrderBottle(orderBottle);
					//orderBottleList.add(orderBottle);
				}
			}			
			
			int addOrderProductSeq = orderProductList.size()+1;
			
			for(int i = 0 ; i < remainOrderProductList.size() ; i++) {
				boolean regiFlag = true;
				OrderProductVO remainOrderProduct = remainOrderProductList.get(i);
				//logger.debug("WorkReportServiceImpl registerWorkReportMassForOrder addOrderProductList getProductId "+remainOrderProduct.getProductId());
								
				for (int j =0 ; j < orderProductList.size() ; j++) {
					if(remainOrderProductList.get(i).getProductId() == orderProductList.get(j).getProductId()
							&& remainOrderProductList.get(i).getProductPriceSeq() == orderProductList.get(j).getProductPriceSeq() ) {
						
						int orginPrice = orderProductList.get(j).getOrderAmount() / orderProductList.get(j).getOrderCount();
						
						orderProductList.get(j).setOrderCount(orderProductList.get(j).getOrderCount()+remainOrderProduct.getOrderCount());
						orderProductList.get(j).setOrderAmount( orderProductList.get(j).getOrderCount()*orginPrice);
						//logger.debug("WorkReportServiceImpl registerWorkReportMassForOrder orderProductList.get(j).getOrderCount() "+orderProductList.get(j).getOrderCount());
						
						result = orderService.modifyOrderProductCount(orderProductList.get(j));
						regiFlag = false;
						
						//WorkBottle orderBottle 추가
						for(int k=0 ; k < remainOrderProduct.getOrderCount() ; k++) {
							OrderBottleVO orderBottle = new OrderBottleVO();
							
							orderBottle.setOrderId(order.getOrderId());
							orderBottle.setOrderProductSeq(orderProductList.get(j).getOrderProductSeq());
							orderBottle.setProductId(remainOrderProduct.getProductId());
							orderBottle.setProductPriceSeq(remainOrderProduct.getProductPriceSeq());
							orderBottle.setBottleBarCd(remainOrderProduct.getBottleBarCd());
							orderBottle.setCreateId(param.getUserId());
							
							orderBottleList.add(orderBottle);
							
							WorkBottleVO workBottle = new WorkBottleVO();
							
							workBottle.setWorkReportSeq(param.getWorkReportSeq());
							workBottle.setWorkSeq(workSeq++);
							workBottle.setCustomerId(param.getCustomerId());
							workBottle.setBottleId(remainOrderProduct.getBottleId());
							workBottle.setBottleBarCd(remainOrderProduct.getBottleBarCd());
							workBottle.setGasId(remainOrderProduct.getGasId());						
							workBottle.setCreateId(param.getUserId());
							workBottle.setProductId(remainOrderProduct.getProductId());
							workBottle.setProductPriceSeq(remainOrderProduct.getProductPriceSeq());		
							workBottle.setBottleWorkCd(param.getBottleWorkCd());
							workBottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.FULL"));
							workBottle.setProductPrice(orginPrice);
							workBottle.setBottleSaleYn("Y");
							
							workBottleList.add(workBottle);
						}
					}	
				}
				
				if(regiFlag) {
					BottleVO bottle = new BottleVO();
					bottle.setCustomerId(param.getCustomerId());
					bottle.setProductId(remainOrderProduct.getProductId());
					bottle.setProductPriceSeq(remainOrderProduct.getProductPriceSeq());
					
					ProductTotalVO productTotal = productService.getPrice(bottle);	
					
					remainOrderProduct.setOrderProductSeq(addOrderProductSeq++);
					
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {					
						if(productTotal.getCustomerBottlePrice() > 0) 
							remainOrderProduct.setOrderAmount(remainOrderProduct.getOrderCount() * productTotal.getCustomerBottlePrice());
						else 
							remainOrderProduct.setOrderAmount(remainOrderProduct.getOrderCount() * productTotal.getProductBottlePrice());					
						
						remainOrderProduct.setBottleChangeYn("N");
						remainOrderProduct.setBottleSaleYn("Y");
					}else {
						if(productTotal.getCustomerProductPrice() > 0)
							remainOrderProduct.setOrderAmount(remainOrderProduct.getOrderCount() * productTotal.getCustomerProductPrice());
						else
							remainOrderProduct.setOrderAmount(remainOrderProduct.getOrderCount() * productTotal.getProductPrice());
						
						remainOrderProduct.setBottleChangeYn("Y");
						remainOrderProduct.setBottleSaleYn("N");
					}
					remainOrderProduct.setCreateId(param.getUserId());
					
					addOrderProductList.add(remainOrderProduct);
					
				}
			}
			
			//Order_Bottle, Work_Bottle
			for(int i = 0 ; i < addOrderProductList.size() ; i++) {
				OrderProductVO orderProduct = addOrderProductList.get(i);
				
				for(int j = 0 ; j < orderProduct.getOrderCount() ;j++) {
					OrderBottleVO orderBottle = new OrderBottleVO();
					
					orderBottle.setOrderId(order.getOrderId());
					orderBottle.setOrderProductSeq(orderProduct.getOrderProductSeq());
					orderBottle.setProductId(orderProduct.getProductId());
					orderBottle.setProductPriceSeq(orderProduct.getProductPriceSeq());
					orderBottle.setBottleBarCd(orderProduct.getBottleBarCd());
					orderBottle.setCreateId(param.getUserId());
					
					orderBottleList.add(orderBottle);
					
					WorkBottleVO workBottle = new WorkBottleVO();
					
					workBottle.setWorkReportSeq(param.getWorkReportSeq());
					workBottle.setWorkSeq(workSeq++);
					workBottle.setCustomerId(param.getCustomerId());
					workBottle.setBottleId(orderProduct.getBottleId());
					workBottle.setBottleBarCd(orderProduct.getBottleBarCd());
					workBottle.setGasId(orderProduct.getGasId());						
					workBottle.setCreateId(param.getUserId());
					workBottle.setProductId(orderProduct.getProductId());
					workBottle.setProductPriceSeq(orderProduct.getProductPriceSeq());		
					workBottle.setBottleWorkCd(param.getBottleWorkCd());
					workBottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.FULL"));
					workBottle.setProductPrice(orderProduct.getOrderAmount() / orderProduct.getOrderCount() );
					workBottle.setBottleSaleYn("Y");
					workBottleList.add(workBottle);
					
					addOrderTotalAmount += workBottle.getProductPrice();
				}							
			}
			
			if(addOrderProductList.size() > 0)
				result = orderService.registerOrderProducts(addOrderProductList);
			if(orderBottleList.size() > 0)
				result = orderService.registerOrderBottles(orderBottleList);
			if(workBottleList.size() > 0)
				result = workMapper.insertWorkBottles(workBottleList);			
						
			//WorkReprot
			param.setOrderId(order.getOrderId());
			//param.setOrderAmount(orderResult.getOrderTotalAmount());
			param.setWorkProductNm(order.getOrderProductNm());
			param.setWorkProductCapa(order.getOrderProductCapa());
			param.setUpdateId(param.getUserId());
			
			result = workMapper.modifyWorkReportProduct(param);
			
			//Order Update
			boolean orderCompleted = false;
			
			// Order 상태 정보 변경
			List<OrderProductVO> allOrderProductList = orderService.getOrderProductList(order.getOrderId());
			int remainCount = 0;
			for(int j=0; j < allOrderProductList.size() ; j++) {
				//logger.debug(" ----------- WorkReportServiceImpl registerWorkReportForOrder  allOrderProductList.get(j).getSalesCount()=" + allOrderProductList.get(j).getSalesCount() );
				remainCount += allOrderProductList.get(j).getSalesCount();
			}
			//logger.debug(" ----------- WorkReportServiceImpl registerWorkReportForOrder  remainCount=" + remainCount );
			if(remainCount == 0) orderCompleted = true;			
			
			if(allOrderProductList.size() > 1) {
				order.setOrderProductNm(allOrderProductList.get(0).getProductNm()+" 외"+(allOrderProductList.size()-1));
				order.setOrderProductCapa(allOrderProductList.get(0).getProductCapa()+" 외"+(allOrderProductList.size()-1));
			}
			if(addOrderTotalAmount > 0) {
				order.setOrderTotalAmount(order.getOrderTotalAmount() + addOrderTotalAmount);	
				
				result = orderService.modifyOrderAdditionBottles(order);		
			}
			
			if(orderCompleted) {
				order.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
				order.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.delivery"));	
				order.setSalesId(param.getUserId());
				order.setChOrderId(order.getOrderId());
				result = orderService.changeOrderProcessCd(order);
			}
			
			result = registerCashFlow(param,orderTotalAmount+addOrderTotalAmount);
						
		} catch (DataAccessException e) {		
			e.printStackTrace();
			logger.error(" registerWorkReportMassForOrder", e.toString());
		} catch (Exception e) {			
			e.printStackTrace();
			logger.error(" registerWorkReportMassForOrder", e.toString());
		}
		return result;
	}
	
	
	@Override
	@Transactional
	public int deleteWorkReport(WorkReportVO param) {
		int result = 0;

		try {
			//work_report 업데이트
			WorkReportVO workReport = workMapper.selectWorkReportOnly(param.getWorkReportSeq());
			
			List<WorkBottleVO> beforeWorkBottleList = workMapper.selectWorkBottleList(param.getWorkReportSeq());			
			
			List<OrderProductVO> orderProductList = orderService.getOrderProductList(workReport.getOrderId());					
			
			int totalAmount = 0;
			String bottleIds = "";
			//Order_product Order_Bottle
			for(int j=0 ; j < beforeWorkBottleList.size() ; j++) {
				WorkBottleVO workBottle = beforeWorkBottleList.get(j);		
				int workBottleProductCount = workBottle.getProductCount();
				workBottle.setProductCount(0);			
				bottleIds += workBottle.getBottleId()+",";
				totalAmount += workBottle.getProductPrice();
				//Work_bottle
				if(!workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))) {
					for(int i =0 ; i <orderProductList.size() ; i++) {
						OrderProductVO orderProduct = orderProductList.get(i);
						
						if(orderProduct.getProductId() == workBottle.getProductId() 
								&& orderProduct.getProductPriceSeq() == workBottle.getProductPriceSeq()){
							
							// 같은 경우
							if( (orderProduct.getBottleChangeYn().equals("Y") && workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent")) )
									|| (orderProduct.getBottleChangeYn().equals("N") && workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) )  ) {
																
								int remainCount = workBottle.getProductCount() - orderProduct.getOrderCount();
								
								orderProduct.setSalesCount(remainCount);
								orderProduct.setCustomerId(workReport.getCustomerId());
								
								result = minusOrderProduct(orderProduct);
								if(result <=0 ) return -1;
								
							}							
						}
					} // for(int i =0 ; i <orderProductList.size() ; i++) {
				}		
				
				workBottle.setProductCount(workBottleProductCount*-1);
				workBottle.setUpdateId(param.getCreateId());
				
				result = modifyCustomerProduct(workBottle);
				if(result <=0 ) return -1;
				
				//Bottle정보 업데이트
			}			   
			
			result = workMapper.deleteWorkBottle(param.getWorkReportSeq());
			if(result <=0 ) return -1;
					
			result = workMapper.deleteWorkReport(param);		
			if(result <=0 ) return -1;			
			
			//Order
			workReport.setUpdateId(param.getCreateId());
			result = modifyOrderInfo(workReport);			
			if(result <=0 ) return -1;
			
			//Cash_flow
			CashFlowVO cashFlow = new CashFlowVO();
			cashFlow.setCustomerId(workReport.getCustomerId());
			cashFlow.setReceivableAmount(totalAmount*-1);
			if(workReport.getReceivedAmount() > 0)
				cashFlow.setIncomeAmount(workReport.getReceivedAmount()*-1);
			cashFlow.setIncomeWay("");
			if(param.getUserId() != null && param.getUserId().length() > 0)
				cashFlow.setCreateId(param.getUserId());
			else
				cashFlow.setCreateId(param.getCreateId());
			
			result = cashService.registerCashFlow(cashFlow);
			
		} catch (DataAccessException e) {		
			e.printStackTrace();
			logger.error("WorkReportServiceImpl", e.toString());
		} catch (Exception e) {			
			e.printStackTrace();
			logger.error("WorkReportServiceImpl", e.toString());
		}
		
		return result;
	}
	
	private WorkBottleVO makeWorkBottle(BottleVO bottle) {
		
		WorkBottleVO workBottle = new WorkBottleVO();
				
		workBottle.setCustomerId(bottle.getCustomerId());
		workBottle.setBottleId(bottle.getBottleId());
		workBottle.setBottleBarCd(bottle.getBottleBarCd());
		workBottle.setGasId(bottle.getGasId());						
		workBottle.setCreateId(bottle.getUpdateId());
		workBottle.setProductId(bottle.getProductId());
		workBottle.setProductPriceSeq(bottle.getProductPriceSeq());		
		workBottle.setBottleWorkCd(bottle.getBottleWorkCd());
		workBottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.FULL"));
		
		return workBottle;		
	}
	
	private OrderBottleVO makeOrderBottle(OrderProductVO orderProduct, BottleVO bottle) {
		
		OrderBottleVO orderBottle = new OrderBottleVO();				
		
		orderBottle.setBottleBarCd(bottle.getBottleBarCd());
		orderBottle.setOrderId(orderProduct.getOrderId());
		orderBottle.setOrderProductSeq(orderProduct.getOrderProductSeq());
		orderBottle.setCreateId(bottle.getUpdateId());
		orderBottle.setProductId(bottle.getProductId());
		orderBottle.setProductPriceSeq(bottle.getProductPriceSeq());	
		
		
		return orderBottle;		
	}
	
	
	private int changeCustomerProduct(List<WorkBottleVO> param) {
		int result = 0;
		
		// 거래처별 상품별 등록 여부 확인
		for(int i=0 ; i < param.size() ; i++) {
			
			if(param.get(i).getGasId()!= null && param.get(i).getGasId() > 0) {	// 용기만 해당
				CustomerProductVO temp = new CustomerProductVO();
				temp.setCustomerId(param.get(i).getCustomerId());
				temp.setProductId(param.get(i).getProductId());
				temp.setProductPriceSeq(param.get(i).getProductPriceSeq());
				temp.setCreateId(param.get(i).getCreateId());
				
				CustomerProductVO customerProduct = customerService.getCustomerProduct(temp);
				
				if(customerProduct != null) {
					
					if(param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesBack"))	//20201220
							) {
						customerProduct.setBottleOwnCount(1);						
					}else if (param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyRent"))	//20201220
							) {						
						customerProduct.setBottleRentCount(1);
					}else if (param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyBack"))	//20201220
							) {						
						customerProduct.setBottleRentCount(-1);
					}else if (param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freeback"))
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.buyback")) 
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesgas"))	//20201220
							) {
						customerProduct.setBottleOwnCount(-1);
					}					
					
					if(param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freeback"))
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.buyback")) 
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesgas")) 
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesBack")) 
							) {
						result = customerService.modifyCustomerProductOwnCount(customerProduct);
						
					}else if (param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyRent"))
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyBack"))
							) {
						result = customerService.modifyCustomerProductRentCount(customerProduct);
					}
					
				}else {
					customerProduct = temp;		
					
					if(param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesBack"))	//20201220
							) {
						customerProduct.setBottleOwnCount(1);
						
					}else if (param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyRent"))	//20201220
							) {						
						customerProduct.setBottleRentCount(1);
					}else if (param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyBack"))	//20201220
							) {						
						customerProduct.setBottleRentCount(-1);
					}else if (param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freeback"))
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.buyback")) 
							|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesgas"))
							) {
						customerProduct.setBottleOwnCount(-1);
					}
					
					result = customerService.registerCustomerProduct(customerProduct);
					
				}
			} //if(param.get(i).getGasId()!= null && param.get(i).getGasId() > 0)  end
		}
		return result;
	}
	
	
}
