package com.gms.web.admin.service.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.domain.manage.CustomerPriceExtVO;
import com.gms.web.admin.domain.manage.CustomerPriceVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.mapper.manage.CustomerMapper;

@Service
public class CustomerServiceImpl implements CustomerService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CustomerMapper customerMapper;

	@Override
	public Map<String, Object> getCustomerList(CustomerVO param) {
		
		logger.info("****** getCustomerList *****start===*");		
		logger.info("****** getCustomerList *****param.getSearchCustomerNm===*" + param.getSearchCustomerNm());
		logger.info("****** getCustomerList *****param.getRowPerPage===*" + param.getRowPerPage());
		
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
		map.put("searchCustomerNm", param.getSearchCustomerNm());				
		
		int customerCount = customerMapper.selectCustomerCount(map);
		
		int lastPage = (int)((double)customerCount/ROW_PER_PAGE+0.95);
		if(lastPage == 0) lastPage = 1;

		logger.info("****** getCustomerList *****currentPage===*"+currentPage);	
		logger.info("****** getCustomerList.customerCount *****===*"+customerCount);		
		logger.info("****** getCustomerList.lastPage *****===*"+lastPage);
		
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<CustomerVO> customerList = customerMapper.selectCustomerList(map);
		
		logger.info("****** getCustomerList.customerList *****===*"+customerList.size());
		
		resutlMap.put("list",  customerList);
		resutlMap.put("searchCustomerNm", param.getSearchCustomerNm());
		resutlMap.put("currentPage", currentPage);
		resutlMap.put("lastPage", lastPage);
		resutlMap.put("startPageNum", starPageNum);
		resutlMap.put("lastPageNum", lastPageNum);
		resutlMap.put("totalCount", customerCount);
		
		return resutlMap;
	}

	@Override
	public CustomerVO getCustomerDetails(Integer customerId) {
		return customerMapper.selectCustomerDetail(customerId);	
	}

	@Override
	@Transactional
	public boolean registerCustomer(CustomerVO param) {
		boolean successFlag = false;

		// 정보 등록
		int result = 0;
		logger.info("****** registerCustomer.getCustomerId()()) *****===*"+param.getCustomerId());
		
		result = customerMapper.insertCustomer(param);
		if (result > 0) {
			successFlag = true;
		}
		
		return successFlag;
	}

	@Override
	@Transactional
	public boolean modifyCustomer(CustomerVO param) {
		boolean successFlag = false;

		// 가스정보 등록
		int result = 0;
		logger.info("****** modifyCustomer()()) *****===*"+param.getCustomerId());
		
			result = customerMapper.updateCustomer(param);
			if (result > 0) {
				successFlag = true;
			}	
		
		return successFlag;
	}

	@Override
	@Transactional
	public boolean modifyCustomerStatus(CustomerVO param) {
		boolean successFlag = false;

		// 정보 등록
		int result = 0;
		logger.info("****** modifyCustomerStatus.getCustomerId()()) *****===*"+param.getCustomerId());
		
		result = customerMapper.updateCustomerStatus(param);
		if (result > 0) {
			successFlag = true;
		}
		
		return successFlag;
	}

	@Override
	@Transactional
	public boolean deleteCustomer(Integer customerId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, Object> checkBusinessRegIdDuplicate(CustomerVO param) {
		// 중복체크
		int count = customerMapper.selectBusinessRegId(param);
		// 결과 변수
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(count > 0){
			result.put("result", "fail");
			result.put("message", "아이디가 존재 합니다. 확인 후 입력해 주세요.");
			return result;
		}
		
		result.put("result", "success");
		
		return result;
	}

	@Override
	public Map<String, Object> searchCustomerList(String param) {
		
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<CustomerVO> customerList = customerMapper.searchCustomerList(param);
		
		logger.info("****** getCustomerList.customerList *****===*"+customerList.size());
		
		resutlMap.put("list",  customerList);
		resutlMap.put("searchCustomerNm", param);
		
		return resutlMap;	
	}

	@Override
	@Transactional
	public boolean registerCustomerPrice(CustomerPriceVO[] param) {
		boolean successFlag = false;

		// 정보 등록
		int result = 0;
		logger.info("****** registerCustomer.getCustomerId()()) *****===*");
		
		boolean deleteResult = false;
		for(int i = 0 ; i < param.length ; i++ ) {
			
			if( i == 0 ) deleteResult = deleteCustomerPrice(param[i].getCustomerId());
			
			result = customerMapper.insertCustomerPrice(param[i]);
			
			if (deleteResult != false && result > 0) {
				successFlag = true;
			}
		}
				
		
		return successFlag;
	}

	@Override
	@Transactional
	public boolean deleteCustomerPrice(Integer customerId) {
		boolean successFlag = false;

		// 정보 등록
		int result = 0;
		logger.info("****** registerCustomer.getCustomerId()()) *****===*"+customerId);
		
		result = customerMapper.deleteCustomerPrice(customerId);
		if (result > 0) {
			successFlag = true;
		}
		
		return successFlag;
	}

	@Override
	public List<CustomerPriceExtVO> getCustomerPreiceList(Integer customerId) {
		return customerMapper.selectCustomerPriceList(customerId);
	}

}
