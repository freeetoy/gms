package com.gms.web.admin.mapper.manage;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.BottleHistoryVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.SimpleBottleVO;

@Mapper
public interface BottleMapper {

	public List<BottleVO> selectBottleList(Map<String, Object> map);	
	
	public List<BottleVO> selectBottleListAll();	
	
	public List<BottleVO> selectBottleListToExcel(Map<String, Object> map);
	
	public List<BottleVO> selectCustomerBottleList(Integer customerId);	
	
	public List<BottleVO> selectCustomerBottleListDate(BottleVO param);	
	
	public BottleVO selectCustomerBottleRecent(Integer customerId);	
	
	public List<SimpleBottleVO> selectSimpleCustomerBottleList(Integer customerId);	
	
	public BottleVO selectBottleDetail(String bottleId) ;
	
	public BottleVO selectBottleDetailForBarCd(String bottleBarCd) ;
	
	public List<BottleVO> selectBottleDetails(BottleVO param) ;
	
	public List<BottleVO> selectBottleListByBarCds(BottleVO param) ;
	
	public int insertBottle(BottleVO param);	
	
	public int insertBottles(List<BottleVO> param);	

	public int updateBottle(BottleVO param);
	
	public int updateBottleWorkCd(BottleVO param);
	
	public int updateBottlesWorkCd(BottleVO param);
	
	public int updateBottlesWorkCdOnly(BottleVO param);
	
	public int updateBottleOrderId(BottleVO param);

	public int deleteBottle(BottleVO param);
	
	public int deleteBottles(BottleVO param);

	public int selectBottleCount(Map<String, Object> map);	

	public int selectBottleIdCheck(BottleVO param);	

	public int selectBottleBarCdCheck(BottleVO param);
	
	public List<BottleHistoryVO> selectBottleHistoryList(String bottleId);	
	
	public int insertBottleHistory(BottleVO param);
	
	public int insertBottleHistorys(List<BottleVO> param);
	
	public List<BottleVO> selectDummyBottleList();	
}
