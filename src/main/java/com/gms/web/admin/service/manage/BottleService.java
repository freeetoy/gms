package com.gms.web.admin.service.manage;

import java.util.Map;

import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerVO;

public interface BottleService {

	public Map<String,Object> getBottleList(BottleVO params);	
	
	public BottleVO getBottleDetail(String BottleId) ;
	
	public int getBottleCount(Map<String, Object> map);	
	
	public int getBottleIdCheck(BottleVO param);	

	public int getBottleBarCdCheck(BottleVO param);	
	
	public int registerBottle(BottleVO param);	

	public int modifyBottle(BottleVO param);
	
	public int changeBottleWorkCd(BottleVO param);

	public int deleteBottle(String BottleId);
	
	public Map<String, Object> checkBottleIdDuplicate(BottleVO param);
}
