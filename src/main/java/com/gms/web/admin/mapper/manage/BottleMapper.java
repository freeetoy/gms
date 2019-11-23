package com.gms.web.admin.mapper.manage;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.BottleHistoryVO;
import com.gms.web.admin.domain.manage.BottleVO;

@Mapper
public interface BottleMapper {

	public List<BottleVO> selectBottleList(Map<String, Object> map);	
	
	public int insertBottle(BottleVO param);

	public BottleVO selectBottleDetail(String bottleId) ;

	public int updateBottle(BottleVO param);
	
	public int updateBottleWorkCd(BottleVO param);

	public int deleteBottle(BottleVO param);
	
	public int deleteBottles(BottleVO param);

	public int selectBottleCount(Map<String, Object> map);	

	public int selectBottleIdCheck(BottleVO param);	

	public int selectBottleBarCdCheck(BottleVO param);
	
	public List<BottleHistoryVO> selectBottleHistoryList(String bottleId);	
	
	public int insertBottleHistory(String bottleId);
}
