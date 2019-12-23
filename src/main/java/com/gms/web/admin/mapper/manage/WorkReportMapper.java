package com.gms.web.admin.mapper.manage;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;

@Mapper	
public interface WorkReportMapper {
	
	public List<WorkReportVO> selectWorkReportList(WorkReportVO param);	
	
	public List<WorkBottleVO> selectWorkBottleList(Integer workReportSeq);	
	
	public List<BottleVO> selectWorBottleListTotal(Map<String, Object> map);	
	
	public List<BottleVO> selectWorBottleListToday();	
	
	public int selectWorBottleCountTotal(Map<String, Object> map);	
	
	public int selectWorkReportSeq() ;
	
	public int insertWorkReport(WorkReportVO param);
	
	public int insertWorkBottle(WorkBottleVO param);
	
	public int insertWorkBottles(List<WorkBottleVO> param);	
	

}
