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
import org.springframework.ui.Model;

import com.gms.web.admin.domain.manage.GasVO;
import com.gms.web.admin.service.common.ExcelService;


@Controller
public class ExcelUploadController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ExcelService excelService;
	
	@RequestMapping(value = "/uploadExcelFile", method = RequestMethod.POST)
	@ResponseBody
    public List<GasVO> uploadExcelFile(MultipartHttpServletRequest request, Model model) {
		
		MultipartFile file = null;
		List<GasVO> gaslist = null;
		try {
        
	        Iterator<String> iterator = request.getFileNames();
	        if(iterator.hasNext()) {
	            file = request.getFile(iterator.next());
	        }
	        gaslist = excelService.uploadExcelFile(file);	        
	      
	        model.addAttribute("gaslist", gaslist);
		} catch (Exception e) {
            e.printStackTrace();
            
            return null;
        }
        return gaslist;
    }

}
