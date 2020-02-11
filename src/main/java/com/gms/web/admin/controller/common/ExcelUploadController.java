package com.gms.web.admin.controller.common;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;

import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.GasVO;
import com.gms.web.admin.service.common.ExcelService;


@Controller
public class ExcelUploadController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ExcelService excelService;
	
	@RequestMapping(value = "/uploadExcelFile", method = RequestMethod.POST)	
    public ModelAndView uploadExcelFile(MultipartHttpServletRequest request
    		, HttpServletResponse response) {
		
		MultipartFile file = null;
		int result = 0;
		try {
        
	        Iterator<String> iterator = request.getFileNames();
	        if(iterator.hasNext()) {
	            file = request.getFile(iterator.next());
	        }
	        result = excelService.uploadBottleExcelFile(request, file);	        
	      
	        //model.addAttribute("gaslist", gaslist);
	        
	        logger.debug("ExcelUploadContoller result "+ result);
		} catch (Exception e) {
            e.printStackTrace();
            String alertMessage = "엑셀 등록하는데 오류가 발생하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/bottle/list.do");
            return null;
        }
		if(result > 0){
			String alertMessage = "엑셀 등록하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/bottle/list.do");
		}
		return null;
		
    }
	
	
	@RequestMapping(value = "/gms/customer/uploadExcelFile", method = RequestMethod.POST)	
    public ModelAndView uploadExcelFileCustomer(MultipartHttpServletRequest request
    		, HttpServletResponse response
    		, Model model) {
		
		MultipartFile file = null;
		int result = 0;
		try {
        
	        Iterator<String> iterator = request.getFileNames();
	        if(iterator.hasNext()) {
	            file = request.getFile(iterator.next());
	        }
	        result = excelService.uploadCustomerExcelFile(request,file);	        
	      
	        //model.addAttribute("gaslist", gaslist);
	        
	        logger.debug("ExcelUploadContoller result "+ result);
		} catch (Exception e) {
            e.printStackTrace();
            
            return null;
        }
		
		if(result > 0){
			String alertMessage = "엑셀 등록하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/customer/list.do");
		}
		return null;
		
    }

	
	@RequestMapping(value = "/gms/price/uploadExcelFile", method = RequestMethod.POST)	
    public ModelAndView uploadExcelFileCustomerPrice(MultipartHttpServletRequest request
    		, HttpServletResponse response) {
		
		MultipartFile file = null;
		int result = 0;
		try {
        
	        Iterator<String> iterator = request.getFileNames();
	        if(iterator.hasNext()) {
	            file = request.getFile(iterator.next());
	        }
	        result = excelService.uploadCustomerPriceExcelFile(request,file);	        
	      
	        //model.addAttribute("gaslist", gaslist);
	        
	        logger.debug("ExcelUploadContoller result "+ result);
		} catch (Exception e) {
            e.printStackTrace();
            
            return null;
        }
		
		if(result > 0){
			String alertMessage = "엑셀 등록하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/price/write.do");
		}
		return null;
		
    }
}
