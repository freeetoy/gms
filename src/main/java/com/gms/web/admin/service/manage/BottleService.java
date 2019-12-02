package com.gms.web.admin.service.manage;

import java.util.List;
import java.util.Map;

import com.gms.web.admin.domain.manage.BottleHistoryVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerVO;

public interface BottleService {

	public Map<String,Object> getBottleList(BottleVO params);	
	
	public List<BottleVO> getBottleListToExcel(BottleVO params);
	
	public List<BottleVO> getCustomerBottleList(Integer customerId);
	
	public BottleVO getBottleDetail(String bottleId) ;
	
	public int getBottleCount(Map<String, Object> map);	
	
	public int getBottleIdCheck(BottleVO param);	

	public int getBottleBarCdCheck(BottleVO param);	
	
	public int registerBottle(BottleVO param);	

	public int modifyBottle(BottleVO param);
	
	public int modifyBottleOrder(BottleVO param);
	
	public int changeBottleWorkCd(BottleVO param);

	public int deleteBottle(BottleVO param);
	
	public int deleteBottles(BottleVO param);
	
	public Map<String, Object> checkBottleIdDuplicate(BottleVO param);
	
	public List<BottleHistoryVO> selectBottleHistoryList(String bottleId);
	
	
}
