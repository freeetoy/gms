package com.gms.web.admin.controller.manage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.common.CodeVO;
import com.gms.web.admin.domain.manage.BottleHistoryVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.GasVO;
import com.gms.web.admin.service.common.CodeService;
import com.gms.web.admin.service.manage.BottleService;
import com.gms.web.admin.service.manage.GasService;

@Controller
public class BottleController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private GasService gasService;
	
	@Autowired
	private CodeService codeService;
	
	
	@RequestMapping(value = "/gms/bottle/list.do")
	public String getBottleList(BottleVO params, Model model) {

		logger.info("BottleContoller getBottleList");
		logger.info("BottleContoller currentPage "+ params.getCurrentPage());
		logger.info("BottleContoller searchChargeDt "+ params.getSearchChargeDt());
		logger.info("BottleContoller searchGasId "+ params.getSearchGasId());
		logger.info("BottleContoller searchBottleId "+ params.getSearchBottleId());
		
		String searchChargeDt = params.getSearchChargeDt();	
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
				
		if(searchChargeDt != null && searchChargeDt.length() > 20) {
			
			logger.info("BottleContoller searchChargeDt "+ searchChargeDt.length());
			searchChargeDtFrom = searchChargeDt.substring(0, 10) ;
			
			searchChargeDtEnd = searchChargeDt.substring(13, searchChargeDt.length()) ;
			
			params.setSearchChargeDtFrom(searchChargeDtFrom);
			params.setSearchChargeDtEnd(searchChargeDtEnd);
			
		}
		
		//params.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0301"));
		
		Map<String, Object> map = bottleService.getBottleList(params);
		
		model.addAttribute("bottleList", map.get("list"));
		
		String searchGasId = "";
		if(params.getSearchGasId() != null && params.getSearchGasId().length() > 20 ) {
			searchGasId = params.getSearchGasId();
			model.addAttribute("searchGasId", Integer.parseInt(searchGasId));
		}
		
		// 가스 정보 불러오기
		List<GasVO> gasList = gasService.getGasList();
		model.addAttribute("gasList", gasList);
		
		//BOTTLE_WORK_CD 코드정보 불러오기 
		List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.bottle.status"));
		model.addAttribute("codeList", codeList);
		
		//검색어 셋팅
		model.addAttribute("searchBottleId", params.getSearchBottleId());		
		//model.addAttribute("searchGasId", Integer.parseInt(searchGasId));
		model.addAttribute("searchChargeDt", params.getSearchChargeDt());	
		
		model.addAttribute("currentPage", map.get("currentPage"));
		model.addAttribute("lastPage", map.get("lastPage"));
		model.addAttribute("startPageNum", map.get("startPageNum"));
		model.addAttribute("lastPageNum", map.get("lastPageNum"));
		model.addAttribute("totalCount", map.get("totalCount"));
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.bottle"));	 
		
		return "gms/bottle/list";
	}
	
	
	@RequestMapping(value = "/gms/bottle/charge.do")
	public String getBottleTestList(BottleVO params, Model model) {

		logger.info("BottleContoller getBottleList");
		logger.info("BottleContoller currentPage "+ params.getCurrentPage());
		logger.info("BottleContoller searchChargeDt "+ params.getSearchChargeDt());
		logger.info("BottleContoller searchGasId "+ params.getSearchGasId());
		logger.info("BottleContoller searchBottleId "+ params.getSearchBottleId());
		
		String searchChargeDt = params.getSearchChargeDt();	
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
				
		if(searchChargeDt != null && searchChargeDt.length() > 20) {
			
			logger.info("BottleContoller searchChargeDt "+ searchChargeDt.length());
			searchChargeDtFrom = searchChargeDt.substring(0, 10) ;
			
			searchChargeDtEnd = searchChargeDt.substring(13, searchChargeDt.length()) ;
			
			params.setSearchChargeDtFrom(searchChargeDtFrom);
			params.setSearchChargeDtEnd(searchChargeDtEnd);
			
		}else {		
			
			// Date 로 구하기
		    SimpleDateFormat fm1 = new SimpleDateFormat("yyyy/MM/dd");
		    String fromDate = fm1.format(new Date());
		    logger.info("현재시간 년월일 = " + fromDate);

		    Calendar cal = Calendar.getInstance();;
		    cal.setTime(new Date());
		    cal.add(Calendar.DAY_OF_YEAR, 7); // 하루를 더한다.
		    	    
		    String endDate = fm1.format(cal.getTime());
		    logger.info("현재시간 년월일 = " + endDate);
		    
		    searchChargeDt = fromDate+" - "+endDate;
		    
		    params.setSearchChargeDt(searchChargeDt);
			
			params.setSearchChargeDtFrom(fromDate);
			params.setSearchChargeDtEnd(endDate); 
			
		}
		logger.info("BottleContoller searchChargeDt "+ searchChargeDt);
		//params.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0301"));
		
		Map<String, Object> map = bottleService.getBottleList(params);
		
		model.addAttribute("bottleList", map.get("list"));
		
		String searchGasId = "";
		if(params.getSearchGasId() != null && params.getSearchGasId().length() > 20 ) {
			searchGasId = params.getSearchGasId();
			model.addAttribute("searchGasId", Integer.parseInt(searchGasId));
		}
		
		// 가스 정보 불러오기
		List<GasVO> gasList = gasService.getGasList();
		model.addAttribute("gasList", gasList);
		
		//BOTTLE_WORK_CD 코드정보 불러오기 
		List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.bottle.status"));
		model.addAttribute("codeList", codeList);
		
		//검색어 셋팅
		model.addAttribute("searchBottleId", params.getSearchBottleId());	
		model.addAttribute("searchChargeDt", searchChargeDt);	
		
		model.addAttribute("currentPage", map.get("currentPage"));
		model.addAttribute("lastPage", map.get("lastPage"));
		model.addAttribute("startPageNum", map.get("startPageNum"));
		model.addAttribute("lastPageNum", map.get("lastPageNum"));
		model.addAttribute("totalCount", map.get("totalCount"));
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.boottle.test"));	 
		
		return "gms/bottle/charge";
	}
	
	@RequestMapping(value = "/gms/bottle/sales.do")
	public String getBottleSalesList(BottleVO params, Model model) {

		logger.info("BottleContoller getBottleList");
		logger.info("BottleContoller currentPage "+ params.getCurrentPage());
		logger.info("BottleContoller searchChargeDt "+ params.getSearchChargeDt());
		logger.info("BottleContoller searchGasId "+ params.getSearchGasId());
		logger.info("BottleContoller searchBottleId "+ params.getSearchBottleId());
		
		String searchChargeDt = params.getSearchChargeDt();	
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
				
		if(searchChargeDt != null && searchChargeDt.length() > 20) {
			
			logger.info("BottleContoller searchChargeDt "+ searchChargeDt.length());
			searchChargeDtFrom = searchChargeDt.substring(0, 10) ;
			
			searchChargeDtEnd = searchChargeDt.substring(13, searchChargeDt.length()) ;
			
			params.setSearchChargeDtFrom(searchChargeDtFrom);
			params.setSearchChargeDtEnd(searchChargeDtEnd);
			
		}
		
		logger.info("BottleContoller searchChargeDt "+ searchChargeDt);
		//params.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0301"));
		
		params.setSearchSalesYn("Y");
		logger.info("BottleContoller searchSalesYn "+ params.getSearchSalesYn());
		
		Map<String, Object> map = bottleService.getBottleList(params);
		
		model.addAttribute("bottleList", map.get("list"));
		
		String searchGasId = "";
		if(params.getSearchGasId() != null && params.getSearchGasId().length() > 20 ) {
			searchGasId = params.getSearchGasId();
			model.addAttribute("searchGasId", Integer.parseInt(searchGasId));
		}
		
		// 가스 정보 불러오기
		List<GasVO> gasList = gasService.getGasList();
		model.addAttribute("gasList", gasList);
		
		//BOTTLE_WORK_CD 코드정보 불러오기 
		List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.bottle.status"));
		model.addAttribute("codeList", codeList);
		
		//검색어 셋팅
		model.addAttribute("searchBottleId", params.getSearchBottleId());	
		model.addAttribute("searchChargeDt", searchChargeDt);	
		
		model.addAttribute("currentPage", map.get("currentPage"));
		model.addAttribute("lastPage", map.get("lastPage"));
		model.addAttribute("startPageNum", map.get("startPageNum"));
		model.addAttribute("lastPageNum", map.get("lastPageNum"));
		model.addAttribute("totalCount", map.get("totalCount"));
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.boottle.sales"));	 
		
		return "gms/bottle/sales";
	}
	
	@RequestMapping(value = "/gms/bottle/write.do")
	public String openBottleWrite(Model model) {
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.bottle"));
	
		// 가스 정보 불러오기
		List<GasVO> gasList = gasService.getGasList();
		model.addAttribute("gasList", gasList);		
					
		return "/gms/bottle/write";
	}
	
	@RequestMapping(value = "/gms/bottle/CheckBottleId.do")
	@ResponseBody
	public Object checkBottleId(BottleVO param){
		
		Map<String, Object> result = bottleService.checkBottleIdDuplicate(param);
		  
		return result;
	}
	
	
	@RequestMapping(value = "/gms/bottle/register.do", method = RequestMethod.POST)
	public String registerBottle(HttpServletRequest request
			, HttpServletResponse response
			, Model model
			, BottleVO params) {
		logger.debug("BottleContoller registerBottle");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.bottle"));
		
		try {
			//임시
			params.setMemberCompSeq(1);
			
			//ID 중복체크
			
			int result = bottleService.registerBottle(params);
			if (result < 0) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
				logger.debug("BottleContoller registerBottle error");
			}
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	
		return "redirect:/gms/bottle/list.do";
	}
	
	@RequestMapping(value = "/gms/bottle/update.do")
	public String openBottleUpdate(BottleVO params, Model model) {
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.bottle"));
		logger.info("BottleContoller openBottleUpdate bottleId "+ params.getBottleId());
		
		if (params.getBottleId() == null) {
			return "redirect:/gms/bottle/list.do";
		} else {
			
			BottleVO bottle = bottleService.getBottleDetail(params.getBottleId());
			
			if (bottle == null) {
				return "redirect:/gms/bottle/list.do";
			}
			
			// 가스 정보 불러오기
			List<GasVO> gasList = gasService.getGasList();
			model.addAttribute("gasList", gasList);		
			
			// 용기 이력 정보 불러오기
			List<BottleHistoryVO> historyList = bottleService.selectBottleHistoryList(params.getBottleId());
			model.addAttribute("historyList", historyList);	
			
			model.addAttribute("bottle", bottle);
			model.addAttribute("currentPage", params.getCurrentPage());
			model.addAttribute("searchGasId", params.getSearchGasId());
			model.addAttribute("searchBottleId", params.getSearchBottleId());
			model.addAttribute("searchChargeDt", params.getSearchChargeDt());
		}
		
		return "/gms/bottle/update";
	}
	
	@RequestMapping(value = "/gms/bottle/modify.do", method = RequestMethod.POST)
	public String modifyBottle(HttpServletRequest request
			, HttpServletResponse response
			, Model model
			, BottleVO params) {
		logger.info("BottleContoller modifyBottle");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		logger.info("BottleContoller searchChargeDt "+ params.getSearchChargeDt());
		logger.info("BottleContoller searchGasId "+ params.getSearchGasId());
		logger.info("BottleContoller searchBottleId "+ params.getSearchBottleId());
		
		String searchChargetDt = params.getSearchChargeDt();	
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
				
		if(searchChargetDt != null && searchChargetDt.length() > 20) {
			
			logger.info("BottleContoller searchChargeDt "+ searchChargetDt.length());
			searchChargeDtFrom = searchChargetDt.substring(0, 10) ;
			
			searchChargeDtEnd = searchChargetDt.substring(13, searchChargetDt.length()) ;
			
			params.setSearchChargeDtFrom(searchChargeDtFrom);
			params.setSearchChargeDtEnd(searchChargeDtEnd);
			
		}
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.bottle"));
		
		String searchGasId = "";
		if(params.getSearchGasId() != null ) searchGasId = params.getSearchGasId();
		
		
		try {
						
			logger.debug("******params.getBottleId()()) *****===*"+params.getBottleId());
			
			int  result = bottleService.modifyBottle(params);
			
			model.addAttribute("searchBottleId", params.getSearchBottleId());
			model.addAttribute("searchGasId", Integer.parseInt(searchGasId));
			model.addAttribute("searchChargeDt", params.getSearchChargeDt());			
			model.addAttribute("currentPage", params.getCurrentPage());
			
			if (result < 0) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
				logger.debug("BottleContoller registerBottle error");
			}
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	
		return "redirect:/gms/bottle/list.do?currentPage="+params.getCurrentPage()+"&searchBottleId="+params.getSearchBottleId()+"&searchChargeDt="+params.getSearchChargeDt()+"&searchGasId="+params.getSearchGasId();
	}
	
	
	@RequestMapping(value = "/gms/bottle/detail.do")
	@ResponseBody
	public BottleVO getBottleDetail(@RequestParam(value = "bottleId", required = false) String bottleId, Model model)	{
		
		BottleVO result = bottleService.getBottleDetail(bottleId);		
		
		if(result != null) logger.info("******result *****===*"+result.getGasId());
		else logger.info("******result is null  *****===*"); 
		
		return result;
	}
	
	
	@RequestMapping(value = "/gms/bottle/changeWorkCd.do", method = RequestMethod.POST)
	public String modifyBottleWorkCd(HttpServletRequest request
			, HttpServletResponse response
			, Model model
			, BottleVO params) {
		logger.info("BottleContoller modifyBottleWorkCd");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		//검색조건 셋팅
		logger.info("BottleContoller searchChargeDt "+ params.getSearchChargeDt());
		logger.info("BottleContoller searchGasId "+ params.getSearchGasId());
		logger.info("BottleContoller searchBottleId "+ params.getSearchBottleId());
		logger.info("BottleContoller chBottleId "+ request.getParameter("chBottleId"));
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
		String searchGasId = "";
		
		
		try {		
			logger.debug("******params.getBottleId()()) *****===*"+params.getChBottleId());
			
			String searchChargetDt = params.getSearchChargeDt();	
					
			if(searchChargetDt != null && searchChargetDt.length() > 20) {
				
				logger.info("BottleContoller searchChargeDt "+ searchChargetDt.length());
				searchChargeDtFrom = searchChargetDt.substring(0, 10) ;
				
				searchChargeDtEnd = searchChargetDt.substring(13, searchChargetDt.length()) ;
				
				params.setSearchChargeDtFrom(searchChargeDtFrom);
				params.setSearchChargeDtEnd(searchChargeDtEnd);
				
			}			
			
			if(params.getSearchGasId() != null && params.getSearchGasId().length() > 0 ) {
				searchGasId = params.getSearchGasId();
				model.addAttribute("searchGasId", Integer.parseInt(searchGasId));
			}
			
			params.setBottleWorkId(params.getUpdateId());
			
			int  result = bottleService.changeBottleWorkCd(params);
			
			model.addAttribute("searchBottleId", params.getSearchBottleId());
			//model.addAttribute("searchGasId", Integer.parseInt(searchGasId));
			model.addAttribute("searchChargeDt", params.getSearchChargeDt());			
			model.addAttribute("currentPage", params.getCurrentPage());
			
			if (result < 0) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
				logger.debug("BottleContoller registerBottle error");
			}
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	
		return "redirect:/gms/bottle/list.do?currentPage="+params.getCurrentPage()+"&searchBottleId="+params.getSearchBottleId()+"&searchChargeDt="+params.getSearchChargeDt()+"&searchGasId="+params.getSearchGasId();
	}
		
	@RequestMapping(value = "/gms/bottle/delete.do", method = RequestMethod.POST)
	public String deleteBottle(HttpServletRequest request
			, HttpServletResponse response
			, Model model
			, BottleVO params) {
		logger.info("BottleContoller modifyBottleWorkCd");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		//검색조건 셋팅
		logger.info("BottleContoller searchChargeDt "+ params.getSearchChargeDt());
		logger.info("BottleContoller searchGasId "+ params.getSearchGasId());
		logger.info("BottleContoller searchBottleId "+ params.getSearchBottleId());
		
		String searchChargetDt = params.getSearchChargeDt();	
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;			
		String searchGasId = "";	
		
		try {
			
			if(searchChargetDt != null && searchChargetDt.length() > 20) {
				
				logger.info("BottleContoller searchChargeDt "+ searchChargetDt.length());
				searchChargeDtFrom = searchChargetDt.substring(0, 10) ;
				
				searchChargeDtEnd = searchChargetDt.substring(13, searchChargetDt.length()) ;
				
				params.setSearchChargeDtFrom(searchChargeDtFrom);
				params.setSearchChargeDtEnd(searchChargeDtEnd);
				
			}			
			
			if(params.getSearchGasId() != null && params.getSearchGasId().length() > 0 ) {
				searchGasId = params.getSearchGasId();
				model.addAttribute("searchGasId", Integer.parseInt(searchGasId));
			}			
						
			logger.debug("******params.getBottleId()()) *****===*"+params.getBottleId());
			
			int  result = bottleService.deleteBottle(params);
			
			model.addAttribute("searchBottleId", params.getSearchBottleId());
			model.addAttribute("searchChargeDt", params.getSearchChargeDt());			
			model.addAttribute("currentPage", params.getCurrentPage());
			
			if (result < 0) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
				logger.debug("BottleContoller registerBottle error");
			}
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	
		return "redirect:/gms/bottle/list.do?currentPage="+params.getCurrentPage()+"&searchBottleId="+params.getSearchBottleId()+"&searchChargeDt="+params.getSearchChargeDt()+"&searchGasId="+params.getSearchGasId();
	}
	
	
	@RequestMapping(value = "/gms/bottle/deleteChecked.do", method = RequestMethod.POST)
	public String deleteBottles(HttpServletRequest request
			, HttpServletResponse response
			, Model model
			, BottleVO params) {
		logger.info("BottleContoller modifyBottleWorkCd");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		//검색조건 셋팅
		logger.info("BottleContoller searchChargeDt "+ params.getSearchChargeDt());
		logger.info("BottleContoller searchGasId "+ params.getSearchGasId());
		logger.info("BottleContoller searchBottleId "+ params.getSearchBottleId());
		
		logger.info("BottleContoller datastring "+ request.getParameter("bottleIds"));
		
		String bottleIds = null;
		
		
		String searchChargetDt = params.getSearchChargeDt();	
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;			
		String searchGasId = "";	
		
		try {
			
			if(searchChargetDt != null && searchChargetDt.length() > 20) {
				
				logger.info("BottleContoller searchChargeDt "+ searchChargetDt.length());
				searchChargeDtFrom = searchChargetDt.substring(0, 10) ;
				
				searchChargeDtEnd = searchChargetDt.substring(13, searchChargetDt.length()) ;
				
				params.setSearchChargeDtFrom(searchChargeDtFrom);
				params.setSearchChargeDtEnd(searchChargeDtEnd);
				
			}			
			
			if(params.getSearchGasId() != null && params.getSearchGasId().length() > 0 ) {
				searchGasId = params.getSearchGasId();
				model.addAttribute("searchGasId", Integer.parseInt(searchGasId));
			}			
						
			logger.debug("******params.getBottleId()()) *****===*"+params.getBottleId());
			
			if(request.getParameter("bottleIds")!=null && request.getParameter("bottleIds").length() > 0) {
				bottleIds= request.getParameter("bottleIds");
				List<String> list = StringUtils.makeForeach(bottleIds, ","); 		
				params.setBottList(list);
			}					
			
			int  result = bottleService.deleteBottles(params);
			
			
			model.addAttribute("searchBottleId", params.getSearchBottleId());
			model.addAttribute("searchChargeDt", params.getSearchChargeDt());			
			model.addAttribute("currentPage", params.getCurrentPage());
			
			if (result < 0) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
				logger.debug("BottleContoller registerBottle error");
			}
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
		
		return "redirect:/gms/bottle/list.do?currentPage="+params.getCurrentPage()+"&searchBottleId="+params.getSearchBottleId()+"&searchChargeDt="+params.getSearchChargeDt()+"&searchGasId="+params.getSearchGasId();
	}
}
