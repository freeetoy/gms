package com.gms.web.admin.mapper.manage;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.domain.manage.WorkReportViewVO;

@Mapper	
public interface WorkReportMapper {
	
	public List<WorkReportVO> selectWorkReportList(WorkReportVO param);	
	
	public List<WorkReportVO> selectWorkReportOnlyList(WorkReportVO param);	
	
	public List<WorkReportVO> selectWorkReportOnlyListAll(WorkReportVO param);	
	
	public List<WorkBottleVO> selectWorkReportList1(WorkReportVO param);	
	
	public List<WorkBottleVO> selectWorkReportListAll(WorkReportVO param);	
	
	public List<WorkBottleVO> selectWorkBottleList(Integer workReportSeq);	
	
	public List<BottleVO> selectWorBottleListTotal(Map<String, Object> map);	
	
	public List<BottleVO> selectWorBottleListToday();	
	
	public int selectWorBottleCountTotal(Map<String, Object> map);	
	
	public int selectWorkReportSeq() ;
	
	public int selectWorkReportSeqForCustomerToday(WorkReportVO param) ;
	
	public int insertWorkReport(WorkReportVO param);
	
	public int insertWorkBottle(WorkBottleVO param);
	
	public int insertWorkBottles(List<WorkBottleVO> param);	
	

}
