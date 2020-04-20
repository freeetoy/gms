package com.gms.web.admin.controller.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.dao.DataAccessException;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.GasVO;
import com.gms.web.admin.domain.manage.ProductPriceSimpleVO;
import com.gms.web.admin.domain.manage.ProductPriceVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.domain.manage.ProductVO;
import com.gms.web.admin.service.manage.GasService;
import com.gms.web.admin.service.manage.ProductService;

@Controller
public class ProductController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * BoardService 빈(Bean) 선언
	 */
	@Autowired
	private ProductService productService;
	
	@Autowired
	private GasService gasService;
	
	@RequestMapping(value = "/gms/product/list.do")
	public String openProductList(Model model) {

		List<ProductTotalVO> productList = productService.getProductTotalList();
		model.addAttribute("productList", productList);
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.product"));

		return null;
	}
	
	@RequestMapping(value = "/gms/product/write.do")
	public String openProductWrite(@RequestParam(value = "productId", required = false) Integer productId, Model model) {

		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.product"));
		
		if (productId == null) {
			model.addAttribute("product", new ProductVO());
			
		} else {
			if (productId < 1) {
				return "redirect:/gms/product/list.do";
			}

			ProductVO product = productService.getProductDetails(productId);
			if (product == null) {
				return "redirect:/gms/product/list.do";
			}
			model.addAttribute("product", product);
		}

		// 가스 정보 불러오기
		List<GasVO> gasList = gasService.getGasList();
		model.addAttribute("gasList", gasList);
		return "gms/product/write";
	}
	
	@RequestMapping(value = "/gms/product/register.do", method = RequestMethod.POST)
	public String registerProduct(HttpServletRequest req) {
		
		
		logger.debug("ProductContoller registerProduct");
		try {
	/*			
			logger.debug("ProductContoller registerProductgetGasId ==="+req.getParameter("gasId"));
			logger.debug("ProductContoller registerProduct getPrductNm ==="+req.getParameter("productNm"));
			logger.debug("ProductContoller registerProduct productPrice_0 ==="+req.getParameter("productPrice_0"));
			logger.debug("ProductContoller registerProduct priceCount ==="+req.getParameter("priceCount"));
	*/		
			int priceCount  = Integer.parseInt(req.getParameter("priceCount"));
			ProductVO params = new ProductVO();			
			
			RequestUtils.initUserPrgmInfo(req, params);
			
			boolean result=true;
			
			params.setProductNm(req.getParameter("productNm"));			
			if(req.getParameter("gasId").equals("0")) params.setGasId(0);
			else params.setGasId(Integer.parseInt(req.getParameter("gasId")));
			params.setMemberCompSeq(1);
			//int productId = productService.registerProduct(params);			
			
			ProductPriceVO[] priceVo = new ProductPriceVO[priceCount] ;			
			
			for(int i =0 ; i <priceCount ; i++ ) {
				ProductPriceVO priceVo1 = new ProductPriceVO();
				result =false;
				RequestUtils.initUserPrgmInfo(req, priceVo1);
				
				//priceVo1.setProductId(Integer.valueOf(productId));
				priceVo1.setProductPrice(Integer.parseInt(req.getParameter("productPrice_"+i)));
				priceVo1.setProductCapa(req.getParameter("productCapa_"+i));
				priceVo1.setECountCd(req.getParameter("eCountCd_"+i));
				
				priceVo[i] = priceVo1;			
			}
			
			result = productService.registerProduct(params, priceVo);
			
			if (result == false) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달				
			}	
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
			
		return "redirect:/gms/product/list.do";
	}
	
	@RequestMapping(value = "/gms/product/update.do")
	public String updateProductForm(ProductVO params, Model model) {
		logger.debug("ProductContoller updateProduct");
		
		try {

			// 가스 정보 불러오기
			List<GasVO> gasList = gasService.getGasList();
			model.addAttribute("gasList", gasList);
			//Product 정보
			ProductVO productVo = productService.getProductDetails(params.getProductId());			
			model.addAttribute("product", productVo);
			
			//ProductPrice 정보
			List<ProductPriceVO> productPriceList = productService.getProductPriceList(params.getProductId());
			
			StringBuffer productPriceSeqs = new StringBuffer();
			
			for(int i=0 ; i< productPriceList.size() ; i++) {
				productPriceSeqs .append(productPriceList.get(i).getProductPriceSeq().toString());
				productPriceSeqs .append(",");
				
			}
			model.addAttribute("productPriceSeqs", productPriceSeqs.toString());	
			model.addAttribute("productPriceList", productPriceList);			
			
			model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.product"));
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	
		return "gms/product/update";
	}
	
	@RequestMapping(value = "/gms/product/modify.do", method = RequestMethod.POST)
	public String modifyProduct(HttpServletRequest req) {
		logger.debug("ProductContoller modifyProduct");
		try {
	/*		
			logger.debug("ProductContoller modifyProduct getGasId ==="+req.getParameter("gasId"));
			logger.debug("ProductContoller modifyProduct getPrductNm ==="+req.getParameter("productNm"));
			logger.debug("ProductContoller modifyProduct productPrice_0 ==="+req.getParameter("productPrice_0"));
			logger.debug("ProductContoller modifyProduct priceCount ==="+req.getParameter("priceCount"));
	*/		
			int priceCount  = Integer.parseInt(req.getParameter("priceCount"));
			ProductVO params = new ProductVO();		
			
			RequestUtils.initUserPrgmInfo(req, params);
			
			boolean result=true;
			
			params.setProductId(Integer.parseInt(req.getParameter("productId")));	
			params.setProductNm(req.getParameter("productNm"));			
			if(req.getParameter("gasId").equals("0")) params.setGasId(0);
			else params.setGasId(Integer.parseInt(req.getParameter("gasId")));
			params.setMemberCompSeq(1);
			//int productId = productService.registerProduct(params);
			
			//ProductPrice 정보
			List<ProductPriceVO> productPriceList = productService.getProductPriceList(params.getProductId());
			Integer lastPriceSeqInt = productPriceList.get(productPriceList.size()-1).getProductPriceSeq();
			int lastPriceSeq = lastPriceSeqInt.intValue();			
			
			ProductPriceVO[] priceVo = new ProductPriceVO[priceCount] ;
						
			int listIndex=0;
			int Max_Product_Count = 11 ;
			//int lastPriceSeq=0;
			for(int i =0 ; i < Max_Product_Count ; i++ ) {
				ProductPriceVO priceVo1 = new ProductPriceVO();
				result = false;
				
				RequestUtils.initUserPrgmInfo(req, priceVo1);
				
				if(req.getParameter("productPriceSeq_"+i) != null) {
					//logger.debug("ProductContoller registerProduct req.getParameter(\"productPriceSeq_\"+i)==="+req.getParameter("productPriceSeq_"+i));
				//priceVo1.setProductId(Integer.valueOf(productId));
					priceVo1.setProductId(Integer.parseInt(req.getParameter("productId")));					
					priceVo1.setProductPriceSeq(Integer.parseInt(req.getParameter("productPriceSeq_"+i)));				
					priceVo1.setProductPrice(Integer.parseInt(req.getParameter("productPrice_"+i)));
					priceVo1.setProductCapa(req.getParameter("productCapa_"+i));
					priceVo1.setECountCd(req.getParameter("eCountCd_"+i));
					
					priceVo[listIndex++] = priceVo1;		
					
					//lastPriceSeq = Integer.parseInt(req.getParameter("productPriceSeq_"+i));
				}else {
					if(req.getParameter("productPrice_"+i) !=null) {
						priceVo1.setProductId(Integer.parseInt(req.getParameter("productId")));			
						priceVo1.setProductPriceSeq(++lastPriceSeq);	
						priceVo1.setProductPrice(Integer.parseInt(req.getParameter("productPrice_"+i)));
						priceVo1.setProductCapa(req.getParameter("productCapa_"+i));
						priceVo1.setECountCd(req.getParameter("eCountCd"+i));
						priceVo1.setCreateId(params.getCreateId());
						
						priceVo[listIndex++] = priceVo1;
					}
				}
			}
			
			result = productService.modifyProduct(params, priceVo);
			
			if (result == false) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
				
			}	
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
			
		return "redirect:/gms/product/list.do";
	}
	
	@RequestMapping(value = "/gms/product/delete.do")
	public String deleteProduct(@RequestParam(value = "productId", required = false) Integer productId, Model model) {

		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.product"));
		
		try { 
			//임시
			
			boolean result = productService.deleteProduct(productId);
			if (result == false) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			}
			
			List<ProductTotalVO> productList = productService.getProductTotalList();
			model.addAttribute("productList", productList);
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}		
		
		return "redirect:/gms/product/list.do";
	}
	
	
	@RequestMapping(value = "/gms/product/modifyPrice.do")
	public String modifyProductPriceStatus(HttpServletRequest request
			, HttpServletResponse response,
			ProductPriceVO params, Model model) {
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		try { 			
			
			boolean result = productService.modifyProductPriceStatus(params);
			
			if (result == false) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			}
			model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.product"));
			
			List<ProductTotalVO> productList = productService.getProductTotalList();
			model.addAttribute("productList", productList);
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}		
		
		return "redirect:/gms/product/list.do";
	}
	
	/*
	@RequestMapping(value = "/gms/product/detail.do")
	public String getProductDetails(@RequestParam(value = "productId", required = false) Integer productId, Model model) {

		if (productId == null || productId < 1) {
			// TODO => 올바르지 않은 접근이라는 메시지를 전달
			return "redirect:/gms/product/list.do";
		}

		ProductVO product = productService.getProductDetails(productId);
		if (product == null || "Y".equals(product.getDeleteYn())) {
			// TODO => 존재하지 않는 게시글이거나 이미 삭제된 게시글이라는 메시지를 전달
			return "redirect:/gms/product/list.do";
		}

		model.addAttribute("product", product);

		//return "gms/product/list";
		return null;
	}
	*/
	
	@RequestMapping(value = "/gms/product/detail.do")
	@ResponseBody
	public ProductVO getProductDetails(@RequestParam(value = "productId", required = false) Integer productId, Model model)	{
		
		ProductVO result = productService.getProductDetails(productId);
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.product"));
				
		if(result != null) logger.debug("******result *****===*"+result.getProductId());
		else logger.debug("******result is null  *****===*"); 
		
		return result;
	}
	
	@RequestMapping(value = "/gms/product/priceList.do")
	@ResponseBody
	public List<ProductPriceVO> getProductPriceList(@RequestParam(value = "productId", required = false) Integer productId, Model model)	{		
		
		List<ProductPriceVO> productPriceList =  productService.getProductPriceList(productId);
		
		model.addAttribute("productPriceList", productPriceList);		
		return productPriceList;
		//return null;
	}
	
	@RequestMapping(value = "/gms/product/nlist.do")
	@ResponseBody
	public List<ProductVO> getProductList(Model model)	{	
		
		List<ProductVO> productList = productService.getProductList();
		model.addAttribute("productList", productList);
		
		return productList;
		//return null;
	}
	
	@RequestMapping(value = "/gms/common/ngasProducts.do")
	@ResponseBody
	public List<ProductVO> getProductListOfNoGas(Model model)	{	
		
		List<ProductVO> productList = productService.getNoGasProductList();
		model.addAttribute("productList", productList);
		
		return productList;
		//return null;
	}
	
	@RequestMapping(value = "/api/ngasProductPriceList.do")
	@ResponseBody
	public List<ProductPriceSimpleVO> getProductPriceListOfNoGas(Model model)	{	
		
		List<ProductPriceSimpleVO> productList = productService.getNoGasProductPriceList();
		model.addAttribute("productList", productList);
		
		return productList;
		//return null;
	}
}
