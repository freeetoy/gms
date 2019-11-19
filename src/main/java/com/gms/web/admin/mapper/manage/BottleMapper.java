package com.gms.web.admin.mapper.manage;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.BottleVO;

@Mapper
public interface BottleMapper {

	public List<BottleVO> selectBottleList(Map<String, Object> map);	
	
	public int insertBottle(BottleVO param);

	public BottleVO selectBottleDetail(String BottleId) ;

	public int updateBottle(BottleVO param);
	
	public int updateBottleWorkCd(BottleVO param);

	//public int deleteBottle(String BottleId);

	public int selectBottleCount(Map<String, Object> map);	

	public int selectBottleIdCheck(BottleVO param);	

	public int selectBottleBarCdCheck(BottleVO param);
}
