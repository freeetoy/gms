package com.gms.web.admin.service.manage;

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
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.OrderBottleVO;
import com.gms.web.admin.domain.manage.OrderExtVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.ProductPriceVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.domain.manage.TempProductVO;
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
	
	
	@Override
	public List<WorkReportVO> getWorkReportList(WorkReportVO param) {

		String searchDt = param.getSearchDt();		
		
		List<WorkReportViewVO> viewList = new ArrayList<WorkReportViewVO>();
						
		if(param.getSearchDt() == null || param.getSearchDt().length() == 0) {						
			
			param.setSearchDt(DateUtils.getDate("yyyy/MM/dd"));
		}
		
		return workMapper.selectWorkReportList(param);
	}
	
	
	@Override
	public List<WorkReportViewVO> getWorkReportList1(WorkReportVO param) {

		String searchDt = param.getSearchDt();		
		
		List<WorkReportViewVO> viewList = new ArrayList<WorkReportViewVO>();
						
		if(param.getSearchDt() == null || param.getSearchDt().length() == 0) {						
			
			param.setSearchDt(DateUtils.getDate("yyyy/MM/dd"));
		}				
		
		List<WorkBottleVO> workBottleList = workMapper.selectWorkReportList1(param);
		
		List<WorkReportVO> reportList = workMapper.selectWorkReportOnlyList(param);
		
		for(int i = 0 ; i < reportList.size() ; i++ ) {
			
			WorkReportViewVO temp = new WorkReportViewVO();
			temp.setWorkReportSeq(reportList.get(i).getWorkReportSeq());
			
			List<WorkBottleVO> tempBottle = new ArrayList<WorkBottleVO>();
			List<WorkBottleVO> tempBottle1 = new ArrayList<WorkBottleVO>();
			
			temp.setBackBottles(tempBottle);
			temp.setSalesBottles(tempBottle1);
			viewList.add(temp);			
		}
		
		for(int i = 0 ; i < workBottleList.size() ; i++ ) {
			
			WorkBottleVO workBottle = workBottleList.get(i);
			
			for(int j=0; j < viewList.size() ; j++) {
				WorkReportViewVO temp = viewList.get(j);
				// Work_Report_Seq 동일
				if(temp.getWorkReportSeq() - workBottle.getWorkReportSeq()==0) {
					temp.setCustomerId(workBottle.getCustomerId());
					temp.setCustomerNm(workBottle.getCustomerNm());
					temp.setOrderAmount(workBottle.getOrderTotalAmount());
					
					logger.debug("WorkReportServiceImpl getWorkReportList1 temp.getWorkReportSeq()= "+ temp.getWorkReportSeq());
					logger.debug("WorkReportServiceImpl getWorkReportList1 workBottle.getBottleWorkCd()= "+ workBottle.getBottleWorkCd());
					
					// 회수 & 무료회수
					if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0310"))
							|| workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freeback"))
							|| workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.buyback")) ) {
						
						logger.debug("WorkReportServiceImpl getWorkReportList1 back workBottle.getProductId= "+ workBottle.getProductId());
						temp.getBackBottles().add(workBottle);
						
					}else {
						logger.debug("WorkReportServiceImpl getWorkReportList sales workBottle.getProductId= "+ workBottle.getProductId());
						temp.getSalesBottles().add(workBottle);
					}
					
				}
			}
		}
		
		for(int i=0;i<viewList.size() ; i++) {
			/*
			logger.debug("WorkReportServiceImpl getWorkReportList sviewList= "+ viewList.get(i).getCustomerNm());
			logger.debug("WorkReportServiceImpl getWorkReportList salesize= "+ viewList.get(i).getSalesBottles().size());
			logger.debug("WorkReportServiceImpl getWorkReportList backsize= "+ viewList.get(i).getBackBottles().size());
			
			for(int j=0;j<viewList.get(i).getSalesBottles().size();j++) {
				logger.debug("WorkReportServiceImpl getWorkReportList viewList.get(i).getSalesBottles().size() .getProductId= "+ viewList.get(i).getSalesBottles().get(j).getProductNm());
			}
			
			for(int j=0;j<viewList.get(i).getBackBottles().size();j++) {
				logger.debug("WorkReportServiceImpl getWorkReportList viewList.get(i).getBackBottles().size() .getProductId= "+ viewList.get(i).getBackBottles().get(j).getProductNm());
			}
			*/
		}
		
		return viewList;
	}

	@Override
	public List<WorkReportViewVO> getWorkReportListAll(WorkReportVO param) {

		String searchDt = param.getSearchDt();		
		
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
			if(workBottle.getProductCapa().indexOf("Kg") < 0) workBottle.setProductCapa(workBottle.getProductCapa() +"L");
			
			for(int j=0; j < viewList.size() ; j++) {
				WorkReportViewVO temp = viewList.get(j);
				
				// Work_Report_Seq 동일
				if((temp.getWorkReportSeq() - workBottle.getWorkReportSeq())==0) {
					
					temp.setCustomerId(workBottle.getCustomerId());
					temp.setCustomerNm(workBottle.getCustomerNm());					
					
					// 회수
					if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0310"))
							|| workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freeback"))
							|| workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.buyback")) ) {
						
						temp.getBackBottles().add(workBottle);
						
					}else {
						//logger.debug("WorkReportServiceImpl getWorkReportList sales workBottle.getProductId= "+ workBottle.getProductId());
						temp.getSalesBottles().add(workBottle);
					}
					
				}else {					
					//logger.debug("###########WorkReportServiceImpl getWorkReportList1 workBottle.getWorkReportSeq()= "+ workBottle.getWorkReportSeq());
					//logger.debug("############WorkReportServiceImpl getWorkReportList1 temp.getWorkReportSeq()= "+ temp.getWorkReportSeq());
					//logger.debug("############WorkReportServiceImpl getWorkReportList1 temp.getWorkReportSeq(-workBottle.getWorkReportSeq())= "+ (temp.getWorkReportSeq()- workBottle.getWorkReportSeq()));
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

	@Transactional
	public int registerWorkReportForOrderBottle(WorkReportVO param) {
		int result = 0;
		try {
			//Order 정보가져오기(가스주문정보만)
			OrderExtVO  orderInfo = orderService.getOrderNotDelivery(param.getOrderId());			
			orderInfo.getOrder().setUpdateId(param.getCreateId());			
			
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
	public int registerWorkReportForOrder(WorkReportVO param) {
		logger.debug("WorkReportServiceImpl registerWorkReportForOrder start ");
		int result = 0;
		try {
			logger.debug("WorkReportServiceImpl registerWorkReportForOrder orderId =" + param.getOrderId());
			logger.debug("WorkReportServiceImpl registerWorkReportForOrder bottleIds =" + param.getBottlesIds());
			
			//Order 정보가져오기
			OrderExtVO  orderInfo = orderService.getOrderNotDelivery(param.getOrderId());			
			
			orderInfo.getOrder().setUpdateId(param.getCreateId());
			
			//Bottle 정보 가져오기
			List<BottleVO> bottleList = getBottleList(param);
			
			//logger.debug("*****************  WorkReportServiceImpl registerWorkReportForOrder bottleList.size()=="+ bottleList.size());
			//Work_Report_Seq 가져오기
			boolean registerFlag = false;
			param.setCustomerId(orderInfo.getOrder().getCustomerId());
			
			int workReportSeq = getWorkReportSeqForCustomerToday(param);
			int workSeq=1;
			if(workReportSeq <= 0) {
				workReportSeq = getWorkReportSeq();
				registerFlag = true;
			}else {
				workSeq = workMapper.selectWorkBottleSeq(workReportSeq);
			}
			//logger.debug("WorkReportServiceImpl registerWorkReportForOrder workReportSeq =" + workReportSeq +" worktSeq =" + workSeq);
			
			//TB_Work_Reprot 등록
			param.setWorkReportSeq(workReportSeq);
			param.setUserId(param.getCreateId());
			param.setCustomerId(orderInfo.getOrder().getCustomerId());	
			
			List<OrderProductVO> orderProductList = orderInfo.getOrderProduct();
			
			// bottle > orderProduct 많은 경우 예외처리
			
			OrderProductVO tempOrderProduct = null;
			int orderProductTotalCount = 0;	// 총 주문 상품수
			
			// 용기수 > 주문한 상품  , 주문 상품 추가
			List<OrderProductVO> addOrderProductList = new ArrayList<OrderProductVO>();			
			List<TempProductVO> tempOrderProductList = new ArrayList<TempProductVO>();
			
			orderProductTotalCount =orderInfo.getOrderProduct().size();		
			String bottleSaleYn = "N";
			boolean isNoGasCompleted = true;
			
			// 배송되지 않은 상품 목록
			for(int j=0; j< orderInfo.getOrderProduct().size() ; j++) {
				
				tempOrderProduct = orderProductList.get(j);		
				
				bottleSaleYn = tempOrderProduct.getBottleSaleYn();
				
				TempProductVO tempProduct = new TempProductVO();
				tempProduct.setProductId(tempOrderProduct.getProductId());
				tempProduct.setProductPriceSeq(tempOrderProduct.getProductPriceSeq());
				tempProduct.setProductPrice(tempOrderProduct.getOrderAmount()/tempOrderProduct.getOrderCount());
				tempProduct.setOrderId(tempOrderProduct.getOrderId());
				tempProduct.setOrderProductSeq(tempOrderProduct.getOrderProductSeq());
				tempProduct.setOrderBottleSeq(tempOrderProduct.getOrderBottleSeq());
				tempProduct.setBottleSaleYn(tempOrderProduct.getBottleSaleYn());
				tempOrderProductList.add(tempProduct);				
				
			}			
			
			BottleVO tempBottle = null;
			WorkBottleVO workBottle = null;
			//List<String> inBottleId = new ArrayList<String>();
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();
			List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();					
			
			List<Integer> chi = new ArrayList<Integer>();
			//tempbottleList = bottleList;				
			
			boolean isGo = false;
			int checkBottle = 0;					
			
			for(int i = 0; i < bottleList.size() ; i++) {
				isGo = true;
				tempBottle = bottleList.get(i);			
				logger.debug("~~~~~~~~~~~~~~~~~~# WorkReportServiceImpl registerWorkReportForOrder tempBottle.getBottleId ==" +i+"=="+ tempBottle.getBottleId());
				
				for(int j = 0 ; j < tempOrderProductList.size() ; j++) {
					
					TempProductVO tempProduct = tempOrderProductList.get(j);					
					
					//logger.debug("WorkReportServiceImpl registerWorkReportForOrder tempBottle.getProductId() =="+ tempBottle.getProductId() +" ==tempBottle.getProductPriceSeq() =="+ tempBottle.getProductPriceSeq());
					//logger.debug("WorkReportServiceImpl registerWorkReportForOrder tempProduct.getProductId() =="+ tempProduct.getProductId() + "==tempProduct.getProductPriceSeq() ==" + tempProduct.getProductPriceSeq());
									
					//TODO 2020-06-16 order의 BottleChangeYN 과 판매/대여 비교
					if(isGo && tempBottle.getProductId() == tempProduct.getProductId() 
							&& tempBottle.getProductPriceSeq() == tempProduct.getProductPriceSeq() 
							&& ( (tempProduct.getBottleSaleYn().equals("Y") && param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0308")) )
									|| (tempProduct.getBottleSaleYn().equals("N") && param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0309")) ) )   ) {

						// 동일 상품
						checkBottle++;
						bottleList.get(i).setOrderId(tempProduct.getOrderId());
						bottleList.get(i).setOrderProductSeq(tempProduct.getOrderProductSeq());
						
						tempBottle.setOrderId(tempProduct.getOrderId());
						tempBottle.setOrderProductSeq(tempProduct.getOrderProductSeq());
						tempBottle.setCustomerId(orderInfo.getOrder().getCustomerId());
						tempBottle.setBottleWorkId(param.getCreateId());
						tempBottle.setUpdateId(param.getCreateId());
						tempBottle.setBottleType(param.getBottleType());
						tempBottle.setBottleWorkCd(param.getBottleWorkCd());
						
						// Bottle 정보 업데이트
						result = bottleService.modifyBottleOrder(tempBottle);
						
						OrderBottleVO tempOrderBottle = new OrderBottleVO();
						tempOrderBottle.setOrderId(tempProduct.getOrderId());
						tempOrderBottle.setOrderProductSeq(tempProduct.getOrderProductSeq());
						tempOrderBottle.setBottleId(tempBottle.getBottleId());
						tempOrderBottle.setCreateId(param.getCreateId());
						tempOrderBottle.setUpdateId(param.getCreateId());
						tempOrderBottle.setOrderBottleSeq(tempProduct.getOrderBottleSeq());
						
						result =orderService.modifyOrderBottle(tempOrderBottle);						
						
						tempOrderProductList.remove(j);						
						
						chi.add(Integer.valueOf(i));
						
						isGo = false;
						
						// 주문 외 추가 된 용기 정보 -> 주문 상품 정보 추가를 위해 List에 댬기
						//inBottleId.add(tempBottle.getBottleId());
						//addOrderProductList.add(e)
						workBottle = new WorkBottleVO();
						
						workBottle.setBottleId(tempBottle.getBottleId());
						workBottle.setBottleBarCd(tempBottle.getBottleBarCd());
						workBottle.setWorkReportSeq(workReportSeq);
						workBottle.setCreateId(param.getCreateId());
						workBottle.setCustomerId(orderInfo.getOrder().getCustomerId());
						workBottle.setBottleWorkCd(param.getBottleWorkCd());		
						workBottle.setBottleType(param.getBottleType());
						workBottle.setGasId(tempBottle.getGasId());
						workBottle.setProductId(tempBottle.getProductId());
						workBottle.setProductPriceSeq(tempBottle.getProductPriceSeq());	
						workBottle.setProductPrice(tempProduct.getProductPrice());
						workBottle.setWorkSeq(workSeq++);
						workBottle.setBottleSaleYn(tempProduct.getBottleSaleYn());
						
						workBottleList.add(workBottle);	
					}
				}				
			}			
						
			if(chi.size() > 0) {
				for(int i = 0; i < chi.size() ; i++) {
					logger.debug("***********  WorkReportServiceImpl registerWorkReportForOrder chi.size()-i-1==" + (chi.size()-i-1));
					bottleList.remove(chi.get(chi.size()-i-1).intValue());					
				}
			}			
				
			logger.debug("***********  WorkReportServiceImpl registerWorkReportForOrder orderService.bottleList.size() ==" + bottleList.size());
		
			orderBottleList.clear();
			
			OrderProductVO tempOrderProduct1 = null;
			int lastOrderProductSeq = 0;
			int addOrderTotalAmount = 0;
			
			if(bottleList.size() > 0){	// 추가 용기가 있는 경우
				BottleVO temp = null;
				
				lastOrderProductSeq = orderService.getNextOrderProductSeq(orderInfo.getOrder().getOrderId());
				
				for(int i=0;i<bottleList.size();i++) {
					temp = bottleList.get(i);					
					
					//ordrProdict 추가
					tempOrderProduct1 = new OrderProductVO();
					temp.setCustomerId(orderInfo.getOrder().getCustomerId());		
					
					ProductTotalVO tempProductTotal = productService.getPrice(temp);		
					
					tempOrderProduct1.setOrderId(orderInfo.getOrder().getOrderId());					
					tempOrderProduct1.setOrderProductSeq(lastOrderProductSeq);
					
					tempOrderProduct1.setProductId(temp.getProductId());
					tempOrderProduct1.setProductPriceSeq(temp.getProductPriceSeq());
					tempOrderProduct1.setOrderId(orderInfo.getOrder().getOrderId());
					tempOrderProduct1.setBottleId(temp.getBottleId());
					
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0308"))) {
						tempOrderProduct1.setBottleChangeYn("N");
						tempOrderProduct1.setBottleSaleYn("Y");
						
						if(tempProductTotal!=null && tempProductTotal.getCustomerBottlePrice() > 0)
							tempOrderProduct1.setOrderAmount(tempProductTotal.getCustomerBottlePrice());
						else
							tempOrderProduct1.setOrderAmount(tempProductTotal.getProductBottlePrice());
					}else {
						tempOrderProduct1.setBottleChangeYn("Y");
						tempOrderProduct1.setBottleSaleYn("N");
						
						if(tempProductTotal!=null && tempProductTotal.getCustomerProductPrice() > 0)
							tempOrderProduct1.setOrderAmount(tempProductTotal.getCustomerProductPrice());
						else
							tempOrderProduct1.setOrderAmount(tempProductTotal.getProductPrice());
					}
					
					tempOrderProduct1.setOrderProductEtc("초과 용기");
					tempOrderProduct1.setOrderCount(1);					
					tempOrderProduct1.setCreateId(param.getCreateId());
					tempOrderProduct1.setUpdateId(param.getCreateId());
					
					logger.debug("**  WorkReportServiceImpl registerWorkReportForOrder tempOrderProduct1.getBottleSaleYn ==" + tempOrderProduct1.getBottleSaleYn() );
					addOrderProductList.add(tempOrderProduct1);
					
					addOrderTotalAmount = addOrderTotalAmount= + tempOrderProduct1.getOrderAmount();
					
					// Bottle 정보 업데이트
					temp.setOrderId(orderInfo.getOrder().getOrderId());
					temp.setOrderProductSeq(tempOrderProduct1.getOrderProductSeq());
					temp.setCustomerId(orderInfo.getOrder().getCustomerId());
					temp.setBottleWorkId(param.getCreateId());
					temp.setUpdateId(param.getCreateId());
					temp.setBottleType(param.getBottleType());
					temp.setBottleWorkCd(param.getBottleWorkCd());
					
					result = bottleService.modifyBottleOrder(temp);
					
					OrderBottleVO tempOrderBottle = new OrderBottleVO();
					tempOrderBottle.setOrderId(orderInfo.getOrder().getOrderId());
					tempOrderBottle.setOrderProductSeq(lastOrderProductSeq++);
					tempOrderBottle.setProductId(temp.getProductId());
					tempOrderBottle.setProductPriceSeq(temp.getProductPriceSeq());
					tempOrderBottle.setBottleId(temp.getBottleId());					
					tempOrderBottle.setCreateId(param.getCreateId());
					tempOrderBottle.setUpdateId(param.getCreateId());					
					
					orderBottleList.add(tempOrderBottle);		
					
					orderInfo.getOrder().setOrderTotalAmount(orderInfo.getOrder().getOrderTotalAmount()+tempOrderProduct1.getOrderAmount());
										
					workBottle = new WorkBottleVO();
					workBottle.setBottleId(temp.getBottleId());
					workBottle.setBottleBarCd(temp.getBottleBarCd());
					workBottle.setWorkReportSeq(workReportSeq);
					workBottle.setCreateId(param.getCreateId());
					workBottle.setBottleType(param.getBottleType());
					workBottle.setCustomerId(orderInfo.getOrder().getCustomerId());
					workBottle.setBottleWorkCd(param.getBottleWorkCd());	
					workBottle.setGasId(temp.getGasId());
					workBottle.setProductId(temp.getProductId());
					workBottle.setProductPriceSeq(temp.getProductPriceSeq());	
					workBottle.setProductPrice(tempOrderProduct1.getOrderAmount()); // 상품이 하나이므로
					workBottle.setWorkSeq(workSeq++);
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0308"))) {
						workBottle.setBottleSaleYn("Y");
					}else
						workBottle.setBottleSaleYn("N");
					workBottleList.add(workBottle);						
				}							
			}
			logger.debug("---------------- WorkReportServiceImpl registerWorkReportForOrder checkBottle== "+checkBottle+"==orderProductTotalCount "+orderProductTotalCount);
			
			if(checkBottle == orderProductTotalCount || tempOrderProductList.size() == 0  ) {
				orderInfo.getOrder().setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.04"));	
			}
			// 주문 상품과 용기정보가 매칭			

			if(addOrderProductList.size() > 0) {				
				
				result = orderService.registerOrderProducts(addOrderProductList);
				
				logger.debug("---------------- WorkReportServiceImpl registerWorkReportForOrder OrderProductNm "+orderInfo.getOrder().getOrderProductNm() + " OrderProductCapa "+orderInfo.getOrder().getOrderProductCapa() );
				logger.debug("---------------- WorkReportServiceImpl registerWorkReportForOrder getOrderTotalAmount "+orderInfo.getOrder().getOrderTotalAmount());
				//order정보 업데이트

				//TODO order 셋팅 필요
				if(lastOrderProductSeq > 1) {
					List<OrderProductVO> orderProduct = orderService.getOrderProductList(orderInfo.getOrder().getOrderId());
				
					orderInfo.getOrder().setOrderProductNm(orderProduct.get(0).getProductNm()+" 외"+(orderProduct.size()-1));
					orderInfo.getOrder().setOrderProductCapa(orderProduct.get(0).getProductCapa()+" 외"+(orderProduct.size()-1));
					orderInfo.getOrder().setOrderTotalAmount(orderInfo.getOrder().getOrderTotalAmount() + addOrderTotalAmount);			
				}
				//orderInfo.getOrder().setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.04"));				
				
				result = orderService.modifyOrderAdditionBottles(orderInfo.getOrder());
				
				//result = orderService.modifyOrderProductDeliveryDt(tempOrderProduct);
			}
			orderInfo.getOrder().setChOrderId(orderInfo.getOrder().getOrderId());
			orderInfo.getOrder().setUpdateId(param.getCreateId());
			orderInfo.getOrder().setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
					
			// Order_Bottle 등록
			if(orderBottleList.size() > 0)
				result = orderService.registerOrderBottles(orderBottleList);
			
			param.setWorkCd(param.getBottleWorkCd());
			param.setWorkProductNm(orderInfo.getOrder().getOrderProductNm());
			param.setWorkProductCapa(orderInfo.getOrder().getOrderProductCapa());			
			param.setOrderAmount(orderInfo.getOrder().getOrderTotalAmount());			
			logger.debug("WorkReportServiceImpl registerWorkReportForOrder orderAmount =" + orderInfo.getOrder().getOrderTotalAmount() );
			
			// TB_Work_Report 등록 여부 확인
			if(registerFlag) {
				result = workMapper.insertWorkReport(param);			
				if(result <= 0) return 0;
			}else {
				// TB_Work_Report 의 Order_ID 업데이트	
				param.setUpdateId(param.getCreateId());
				param.setWorkReportSeq(workReportSeq);
				result = workMapper.modifyWorkReportOrderId(param);
				if(result <= 0) return 0;
			}
			
			result = workMapper.insertWorkBottles(workBottleList);	
			if(result <= 0) return 0;
			
			// NoGas 상품 주문 판매 여부 확인
			List<OrderProductVO> orderProductListAll= null;	
			orderProductListAll = orderService.getOrderProductList(orderInfo.getOrder().getOrderId());
			
			//해당 주문 상품이 이미 처리되어 있는지 확인 WorkReport & WorkBottle 조회
			List<WorkBottleRegisterVO> registeredWorkBottleList = getWorkBottleListAndCountOfOrder(param.getOrderId());
			
			int ZeroCount = 0;
			for(int i=0; i<orderProductListAll.size();i++) {
				OrderProductVO tempOrderProduct11  = orderProductListAll.get(i);
				
				for(int j = 0 ; j < registeredWorkBottleList.size() ; j++) {
					WorkBottleRegisterVO tempRegisteredBottle = registeredWorkBottleList.get(j);
					
					if(tempOrderProduct11.getProductId() == tempRegisteredBottle.getProductId() 
							&& tempOrderProduct11.getProductPriceSeq() == tempRegisteredBottle.getProductPriceSeq()
							&& tempOrderProduct11.getBottleChangeYn().equals(tempRegisteredBottle.getBottleSaleYn()) ) {
						
						int leftCount  = tempOrderProduct11.getOrderCount()-tempRegisteredBottle.getRegisteredCount();
						
						logger.debug("WorkReportServiceImpl registerWorkReportForOrder  leftCount=" + leftCount );
						if(leftCount <= 0) {
							leftCount = 0;
							ZeroCount++;
						}
						// 주문 상품의 카운트 셋팅(0이면 이미 주문 처리된 것임)
						orderProductListAll.get(i).setOrderCount(leftCount);							
					}
				}			
			}
			
			boolean orderCompleted = false;
			
			// Order 상태 정보 변경
			List<OrderProductVO> orderProduct = orderService.getOrderProductList(orderInfo.getOrder().getOrderId());
			int remainCount = 0;
			for(int j=0; j < orderProduct.size() ; j++) {
				remainCount += orderProduct.get(j).getSalesCount();
			}
			logger.debug(" ----------- WorkReportServiceImpl registerWorkReportForOrder  remainCount=" + remainCount );
			if(remainCount == 0) orderCompleted = true;			
			
			if(orderCompleted) {
				orderInfo.getOrder().setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
				orderInfo.getOrder().setSalesId(param.getCreateId());
				result = orderService.changeOrderProcessCd(orderInfo.getOrder());
			}
			//Customer Bottle_Own_Count 증가
			if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0308")) ) {
				
				CustomerVO customer = new CustomerVO();
				customer.setCustomerId(param.getCustomerId());
				customer.setUpdateId(param.getCreateId());
				customer.setBottleOwnCount(bottleList.size());
				
				result = customerService.modifyCustomerBottleCount(customer);
			}
			/// End of Order & Bottle			
			// Start Work_Report , Work_Bottle
					
		
			
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
		logger.debug("===================== WorkReportServiceImpl registerWorkReportNoOrder start ===============");
		logger.debug("WorkReportServiceImpl registerWorkReportNoOrder bottleIds =" + param.getBottlesIds());			
		logger.debug("WorkReportServiceImpl registerWorkReportNoOrder customerId =" + param.getCustomerId());			
		
		int result = 0;
		
		try {		
			//기존 주문이 있는지 여부 확인
			OrderVO orderTemp =  orderService.getLastOrderForCustomer(param.getCustomerId());
			
			if(orderTemp != null ) {
				param.setOrderId(orderTemp.getOrderId());
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder getOrderId =" + param.getOrderId());		
				result = registerWorkReportForOrder(param);
			}else {
				//Bottle 정보 가져오기
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
				
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder workReportSeq =" + workReportSeq);
				//TB_Work_Reprot 등록
				param.setWorkReportSeq(workReportSeq);
				param.setUserId(param.getCreateId());				
				
				//TB_Work_Bottle 등록	
				
				List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();			
				List<OrderProductVO> orderProductList = new ArrayList<OrderProductVO>();
				List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();	
				
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
				
				if (amPm == 1 ) order.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.PM"));
				else  order.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.AM"));
				
				order.setOrderEtc("현장주문");
				order.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.04"));
				order.setSalesId(param.getCreateId());
				order.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
				
				OrderProductVO tempOrderProduct = null;
				
				WorkBottleVO workBottle = null;
				BottleVO tempBottle = null;
				ProductTotalVO tempProductTotal = null;
				
				//용기 정보를 이횽해 상품 정보 가져옴
				
				int orderProductSeq = 1;
				int orderBottleSeq= 1;
				int orderCount = 1;
				int orderTotalAmount = 0;		
				String orderProductNm = "";
				String orderProductCapa = "";
				
				for(int i = 0; i < bottleList.size() ; i++) {		
					
					tempBottle = bottleList.get(i);
					
					workBottle = new WorkBottleVO();					
					tempOrderProduct = new OrderProductVO();
					
					//tempProductTotal = productService.getBottleGasCapa(tempBottle);		
					tempBottle.setCustomerId(param.getCustomerId());
					tempProductTotal = productService.getPrice(tempBottle);		//2020-06-15
					
					tempOrderProduct.setProductId(tempProductTotal.getProductId());
					tempOrderProduct.setProductPriceSeq(tempProductTotal.getProductPriceSeq());
					tempOrderProduct.setOrderId(orderId);
					
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0308"))) {
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
							if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0309")) ) 
								orderProductList.get(orderProductSeq-1).setOrderAmount(orderCount*tempProductTotal.getProductPrice());	
							else
								orderProductList.get(orderProductSeq-1).setOrderAmount(orderCount*tempProductTotal.getProductBottlePrice());	
							
						}else {
							orderProductSeq++;
							orderCount  = 1;
							tempOrderProduct.setOrderCount(orderCount);
							if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0309")) ) 
								tempOrderProduct.setOrderAmount(tempProductTotal.getProductPrice());	
							else
								tempOrderProduct.setOrderAmount(tempProductTotal.getProductBottlePrice());	
							tempOrderProduct.setOrderProductSeq(orderProductSeq);
							
							orderProductList.add(tempOrderProduct);							
						}							
					}else {
						
						tempOrderProduct.setOrderCount(orderCount);
						if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0309")) ) 
							tempOrderProduct.setOrderAmount(orderCount*tempProductTotal.getProductPrice());			
						else
							tempOrderProduct.setOrderAmount(orderCount*tempProductTotal.getProductBottlePrice());	
						tempOrderProduct.setOrderProductSeq(orderProductSeq);
						
						orderProductList.add(tempOrderProduct);						
					}
					
					workBottle.setBottleId(tempBottle.getBottleId());
					workBottle.setBottleBarCd(tempBottle.getBottleBarCd());
					workBottle.setWorkReportSeq(workReportSeq);
					workBottle.setCreateId(param.getCreateId());
					workBottle.setCustomerId(param.getCustomerId());
					workBottle.setBottleWorkCd(param.getBottleWorkCd());
					workBottle.setGasId(tempBottle.getGasId());
					workBottle.setProductId(tempProductTotal.getProductId());
					workBottle.setProductPriceSeq(tempProductTotal.getProductPriceSeq());						
					workBottle.setBottleType(param.getBottleType());
					
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0309")) ) {
						workBottle.setProductPrice(tempProductTotal.getProductPrice()); 
						workBottle.setBottleSaleYn("N");
					} else {
						workBottle.setBottleSaleYn("Y");
						workBottle.setProductPrice(tempProductTotal.getProductBottlePrice()); 
					}
					workBottle.setWorkSeq(workSeq++);
									
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
					tempOrderBottle.setBottleId(tempBottle.getBottleId());			
					tempOrderBottle.setUpdateId(param.getCreateId());
					
					orderBottleList.add(tempOrderBottle);		
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
				// TB_Order_Product			등록
				result  = orderService.registerOrderAndProduct(order, orderProductList);
				
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
				
				if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0308")) ) {
				
					CustomerVO customer = new CustomerVO();
					customer.setCustomerId(param.getCustomerId());
					customer.setUpdateId(param.getCreateId());
					customer.setBottleOwnCount(bottleList.size());
					
					result = customerService.modifyCustomerBottleCount(customer);
				}
				
				if(registerFlag) {
					result = workMapper.insertWorkReport(param);
				}else {
					//TODO
					param.setUpdateId(param.getCreateId());
					result = workMapper.modifyWorkReportOrderId(param);
				}
				
				result = workMapper.insertWorkBottles(workBottleList);	
			
			}
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
	public int registerWorkReportByBottle(WorkReportVO param, List<BottleVO> bottleList) {
		logger.debug("WorkReportServiceImpl registerWorkReport start ");
		int result = 0;
		boolean insertFlag = false;
		try {			
			
			logger.debug("WorkReportServiceImpl registerWorkReportByBottle bottleIds =" + param.getBottlesIds());			
			
			int workReportSeq = 0;
			int workSeq = 1;
			
			if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0310")) 
					|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freeback"))
					|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.buyback"))) {
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
			logger.debug("WorkReportServiceImpl registerWorkReportByBottle workReportSeq =" + workReportSeq);
			logger.debug("WorkReportServiceImpl registerWorkReportByBottle workSeq =" + workSeq);
			//TB_Work_Reprot 등록
			param.setWorkReportSeq(workReportSeq);
			
			//logger.debug("WorkReportServiceImpl registerWorkReportByBottle bottleWorkCd =" + param.getBottleWorkCd() );
			
			//TB_Work_Bottle 등록				
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();			
						
			WorkBottleVO workBottle = null;
			BottleVO tempBottle = null;				
			
			String orderProductNm = "";
			String orderProductCapa = "";			
			
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
				//workBottle.setProductPrice(tempOrderProduct.getOrderAmount()/tempOrderProduct.getOrderCount()); 
				workBottle.setBottleType(param.getBottleType());
				workBottle.setWorkSeq(workSeq++);
								
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
			if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freeback"))
					|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.buyback"))) {
				CustomerVO customer = new CustomerVO();
				customer.setCustomerId(param.getCustomerId());
				customer.setUpdateId(param.getCreateId());
				customer.setBottleOwnCount(bottleList.size()*-1);
				
				result = customerService.modifyCustomerBottleCount(customer);
			}
			
			if(insertFlag) {
				result = workMapper.insertWorkReport(param);
			}
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
	public List<WorkBottleVO> getWorkBottleList(Integer workReportSeq) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public List<WorkBottleVO> getWorkBottleListOfOrder(Integer orderId) {
		// TODO Auto-generated method stub
		return workMapper.selectWorkBottleListOfOrder(orderId);	
	}
	
	
	@Override
	public List<WorkBottleRegisterVO> getWorkBottleListAndCountOfOrder(Integer orderId) {
		// TODO Auto-generated method stub
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
        int blocks;
        int block;
        blocks = (int) Math.ceil(1.0 * pages / ROW_PER_PAGE); // *소숫점 반올림
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
		// TODO Auto-generated method stub
		return workMapper.selectWorBottleListToday(param);
	}

	@Override
	public int registerWorkReport0310(WorkReportVO param) {
		logger.debug("WorkReportServiceImpl registerWorkReport start ");
		int result = 0;		
		
		try {						
			logger.debug("WorkReportServiceImpl registerWorkReport0310 bottleIds =" + param.getBottlesIds());
			
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
		// TODO Auto-generated method stub
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
		
		logger.debug("WorkReportServiceImpl registerWorkNoBottle  productId=" + param.getProductId() );
		logger.debug("WorkReportServiceImpl registerWorkNoBottle  productPriceSeq=" + param.getProductPriceSeq() );
		logger.debug("WorkReportServiceImpl registerWorkNoBottle  getBottleWorkCd=" + param.getBottleWorkCd() );
		
		boolean registerFlag = false;
		int result = 0;
		int workSeq=1;
		if(param.getProductId() !=null && param.getProductPriceSeq() != null) {
			
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
			workReport.setUserId(param.getCreateId());
			
			//WorkReportSeq 가져오기
			int workReportSeq = getWorkReportSeqForCustomerToday(workReport);
			
			if(workReportSeq <= 0) {
				workReportSeq = getWorkReportSeq();
				registerFlag = true;
			}else {
				workSeq = workMapper.selectWorkBottleSeq(workReportSeq);
			}
			logger.debug("WorkReportServiceImpl registerWorkNoBottle  workReportSeq=" + workReportSeq );
			logger.debug("WorkReportServiceImpl registerWorkNoBottle  workSeq=" + workSeq );
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
				boolean isGasCompleted = true;
				for(int i = 0 ; i < orderProductList.size();i++) {
					OrderProductVO tempOrderProduct = orderProductList.get(i);					
					
					for(int j = 0 ; j < registeredWorkBottleList.size() ; j++) {
						WorkBottleRegisterVO tempRegisteredBottle = registeredWorkBottleList.get(j);
						
						if(tempOrderProduct.getProductId() == tempRegisteredBottle.getProductId() && tempOrderProduct.getProductPriceSeq() == tempRegisteredBottle.getProductPriceSeq()) {
							int leftCount  = tempOrderProduct.getOrderCount()-tempRegisteredBottle.getRegisteredCount();
							logger.debug("WorkReportServiceImpl registerWorkNoBottle  leftCount=" + leftCount );
							if(leftCount <= 0) leftCount = 0;
							else {
								logger.debug("WorkReportServiceImpl registerWorkNoBottle  tempOrderProduct.getGasId()=" + tempOrderProduct.getGasId() );
								if(tempOrderProduct.getGasId() > 0) isGasCompleted = false;
							}
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
						logger.debug("WorkReportServiceImpl registerWorkNoBottle  addCount=" + addCount );
						if(addCount > 0) {	// 기존 주문과 동일 상품의 추가가 있는 경우(TB_order 업데이트, TB_order_product 업데이트)
							orderProductList.get(i).setOrderCount(tempOrderProduct.getOrderCount()+addCount);
							if(productTotal.getCustomerProductPrice() > 0)
								orderProductList.get(i).setOrderAmount(tempOrderProduct.getOrderAmount()+(param.getProductCount()* productTotal.getCustomerProductPrice()) );
							else 
								orderProductList.get(i).setOrderAmount(tempOrderProduct.getOrderAmount()+(param.getProductCount()* productTotal.getProductPrice()) );
							
							orderProductList.get(i).setUpdateId(param.getCreateId());
							
							// 주문상품 업데이트
							result = orderService.modifyOrderProductCount(orderProductList.get(i));
							
							//주문정보 업데이트
							orderTemp.setOrderTotalAmount(orderTemp.getOrderTotalAmount()+(addCount*productTotal.getProductPrice()));
							
							result = orderService.modifyOrderAdditionBottles(orderTemp);
							orderProductList.get(i).setOrderCount(0);	
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
				/*
				if(orderCompleted) {	// 주문 상태 정보 완료로 업데이트
					orderTemp.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.04"));
					orderTemp.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
					orderTemp.setSalesId(param.getCreateId());
					result = orderService.changeOrderProcessCd(orderTemp);
				}
				*/
				logger.debug("WorkReportServiceImpl registerWorkNoBottle  isNewProduct=" + isNewProduct );
				if(isNewProduct) {	// 신규 상품 주문 추가
					
					OrderProductVO newOrderProduct = new OrderProductVO();
					newOrderProduct.setOrderId(orderTemp.getOrderId());
					newOrderProduct.setOrderProductSeq(orderProductList.size()+1);
					newOrderProduct.setProductId(param.getProductId());
					newOrderProduct.setProductPriceSeq(param.getProductPriceSeq());
					newOrderProduct.setOrderCount(param.getProductCount());
					if( productTotal.getCustomerProductPrice() > 0)
						newOrderProduct.setOrderAmount(productTotal.getCustomerProductPrice()*param.getProductCount());
					else 
						newOrderProduct.setOrderAmount(productTotal.getProductPrice()*param.getProductCount());
					newOrderProduct.setOrderProductEtc("추가상품");
					newOrderProduct.setCreateId(param.getCreateId());
					newOrderProduct.setUpdateId(param.getUpdateId());					
					newOrderProduct.setBottleSaleYn("N");
				
					// 주문상품 추가
					result = orderService.registerOrderProduct(newOrderProduct);
					
					// 주문 정보 업데이트					
					orderTemp.setOrderTotalAmount(orderTemp.getOrderTotalAmount()+newOrderProduct.getOrderAmount());					
					orderTemp.setOrderProductNm(orderProductList.get(0).getProductNm()+" 외 "+orderProductList.size());
					orderTemp.setOrderProductCapa(orderProductList.get(0).getProductCapa()+" 외 "+orderProductList.size());					
					orderTemp.setUpdateId(param.getUpdateId());
					
					result = orderService.modifyOrderAdditionBottles(orderTemp);
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
					workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.0308"));
					
					result = workMapper.insertWorkReport(workReport);
				}
				
				//TODO 복수개 등록 필요
				List<WorkBottleVO> workBottleList = new ArrayList();
				
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
					if(productTotal.getCustomerProductPrice() > 0)
						addWorkBottle.setProductPrice(productTotal.getCustomerProductPrice()); 
					else
						addWorkBottle.setProductPrice(productTotal.getProductPrice());
					addWorkBottle.setProductCapa(productTotal.getProductCapa());
					addWorkBottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0308"));
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
							logger.debug("WorkReportServiceImpl ****** - registerWorkNoBottle  leftCount=" + leftCount );
							//leftOrderProduct +=leftCount;
							if(leftCount < 0) leftCount = 0;
							// 주문 상품의 카운트 셋팅(0이면 이미 주문 처리된 것임)
							orderProductList.get(i).setOrderCount(leftCount);							
						}
					}					
				}
				
				int leftOrderProduct = 0;
				for(int i = 0 ; i < orderProductList.size();i++) {
					OrderProductVO tempOrderProduct = orderProductList.get(i);	
					//logger.debug("WorkReportServiceImpl ****** - registerWorkNoBottle  tempOrderProduct.getOrderCount()=" + tempOrderProduct.getOrderCount() );
					leftOrderProduct += tempOrderProduct.getOrderCount();					
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
					//logger.debug("WorkReportServiceImpl ****** - registerWorkNoBottle  leftOrderProduct11=" + leftOrderProduct );
					orderTemp.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.04"));
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
				orderTemp.setOrderTypeCd(PropertyFactory.getProperty("common.code.order.type.01"));	//상품주문
				orderTemp.setProductCount(param.getProductCount());
				
				Calendar cal = Calendar.getInstance();
				int amPm = cal.get(Calendar.AM_PM);
				orderTemp.setDeliveryReqDt(cal.getTime());
				
				if (amPm == 1 ) orderTemp.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.PM"));
				else  orderTemp.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.AM"));		
				
				orderTemp.setOrderEtc("현장주문");
				orderTemp.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.04"));
				orderTemp.setSalesId(param.getCreateId());
				orderTemp.setOrderProductNm(productTotal.getProductNm());
				orderTemp.setOrderProductCapa(productTotal.getProductCapa());
				orderTemp.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
				if(productTotal.getCustomerProductPrice() > 0 )
					orderTemp.setOrderTotalAmount(param.getProductCount()*productTotal.getCustomerProductPrice());
				else
					orderTemp.setOrderTotalAmount(param.getProductCount()*productTotal.getProductPrice());
				orderTemp.setCreateId(param.getCreateId());
				
				//order정보 등록
				result = orderService.registerOrder(orderTemp);
				
				OrderProductVO orderProduct = new OrderProductVO();
				
				orderProduct.setOrderId(orderTemp.getOrderId());
				orderProduct.setOrderProductSeq(1);
				orderProduct.setProductId(param.getProductId());
				orderProduct.setProductPriceSeq(param.getProductPriceSeq());
				orderProduct.setProductCapa(productTotal.getProductCapa());
				orderProduct.setOrderCount(param.getProductCount());
				if(productTotal.getCustomerProductPrice() > 0)
					orderProduct.setOrderAmount(param.getProductCount()* productTotal.getCustomerProductPrice() );
				else 
					orderProduct.setOrderAmount(param.getProductCount()* productTotal.getProductPrice());
				
				//orderProduct.setOrderAmount(param.getProductCount()*productTotal.getProductPrice());
				orderProduct.setOrderProductEtc("무주문상품");
				orderProduct.setCreateId(param.getCreateId());
				orderProduct.setUpdateId(param.getCreateId());
				orderProduct.setBottleSaleYn("N");
				//order product 등록
				result = orderService.registerOrderProduct(orderProduct);
				
				//WorkReport 등록 및 업데이트
				workReport.setWorkReportSeq(workReportSeq);
				workReport.setOrderId(orderTemp.getOrderId());
				workReport.setOrderProductNm(productTotal.getProductNm());
				workReport.setOrderProductCapa(productTotal.getProductCapa());
				workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.0308"));				
				
				//WorkReport 등록
				if(registerFlag)
					result = workMapper.insertWorkReport(workReport);				
				logger.debug("WorkReportServiceImpl --registerWorkNoBottle  param.getProductCount()=" + param.getProductCount() );
				
				//WorkReportBottle 등록
				List<WorkBottleVO> workBottleList = new ArrayList();
				for(int i = 0 ; i < param.getProductCount() ; i++) {		
					WorkBottleVO addWorkBottle = new WorkBottleVO();
					addWorkBottle.setWorkReportSeq(workReportSeq);
					addWorkBottle.setCustomerId(param.getCustomerId());
					addWorkBottle.setWorkSeq(workSeq++);
					addWorkBottle.setBottleWorkCd(param.getBottleWorkCd());
					addWorkBottle.setProductId(param.getProductId());
					addWorkBottle.setProductPriceSeq(param.getProductPriceSeq());
					if(productTotal.getCustomerProductPrice() > 0)
						addWorkBottle.setProductPrice(productTotal.getCustomerProductPrice()); 
					else
						addWorkBottle.setProductPrice(productTotal.getProductPrice());
					addWorkBottle.setProductCapa(productTotal.getProductCapa());
					addWorkBottle.setBottleSaleYn("N");
					addWorkBottle.setCreateId(param.getCreateId());
					addWorkBottle.setUpdateId(param.getCreateId());
					 
					workBottleList.add(addWorkBottle);
				}
				logger.debug("WorkReportServiceImpl --registerWorkNoBottle  workBottleList.size=" + workBottleList.size() );
				result = workMapper.insertWorkBottles(workBottleList);
				
				
			}			
		}
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
	

}
