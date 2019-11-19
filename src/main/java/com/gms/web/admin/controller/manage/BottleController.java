package com.gms.web.admin.controller.manage;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.GasVO;
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
	
	
	@RequestMapping(value = "/gms/bottle/list.do")
	public String getBottleList(BottleVO params, Model model) {

		logger.info("BottleContoller getBottleList");
		logger.info("BottleContoller searchChargeDt "+ params.getSearchChargeDt());
		logger.info("BottleContoller searchGasId "+ params.getSearchGasId());
		logger.info("BottleContoller searchBottleId "+ params.getSearchBottleId());
		
		String searchChargetDt = params.getSearchChargeDt();
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
				
		if(searchChargetDt != null) {
			
			logger.info("BottleContoller searchChargeDt "+ searchChargetDt.length());
			searchChargeDtFrom = searchChargetDt.substring(0, 10) ;
			
			searchChargeDtEnd = searchChargetDt.substring(13, searchChargetDt.length()) ;
			
			params.setSearchChargeDtFrom(searchChargeDtFrom);
			params.setSearchChargeDtEnd(searchChargeDtEnd);
			
		}
		
		Map<String, Object> map = bottleService.getBottleList(params);
		
		model.addAttribute("bottleList", map.get("list"));
		
		// 가스 정보 불러오기
		List<GasVO> gasList = gasService.getGasList();
		model.addAttribute("gasList", gasList);
		//model.addAttribute("bottleList", map.get("list"));
		
		model.addAttribute("searchBottleId", params.getSearchBottleId());
		model.addAttribute("searchGasId", params.getSearchGasId());
		model.addAttribute("searchChargeDt", params.getSearchChargeDt());
		
		model.addAttribute("currentPage", map.get("currentPage"));
		model.addAttribute("lastPage", map.get("lastPage"));
		model.addAttribute("startPageNum", map.get("startPageNum"));
		model.addAttribute("lastPageNum", map.get("lastPageNum"));
		model.addAttribute("totalCount", map.get("totalCount"));
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.bottle"));	 
		
		return "gms/bottle/list";
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
	
	
}
