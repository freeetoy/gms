package com.gms.web.admin.controller.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
		return "/gms/product/write";
	}
	
	@RequestMapping(value = "/gms/product/register.do", method = RequestMethod.POST)
	public String registerProduct(HttpServletRequest req) {
		
		
		logger.info("ProductContoller registerProduct");
		try {
			//임시
			
			logger.info("ProductContoller registerProductgetGasId ==="+req.getParameter("gasId"));
			logger.info("ProductContoller registerProduct getPrductNm ==="+req.getParameter("productNm"));
			logger.info("ProductContoller registerProduct productPrice_0 ==="+req.getParameter("productPrice_0"));
			logger.info("ProductContoller registerProduct priceCount ==="+req.getParameter("priceCount"));
			
			int priceCount  = Integer.parseInt(req.getParameter("priceCount"));
			ProductVO params = new ProductVO();			
			
			RequestUtils.initUserPrgmInfo(req, params);
			
			boolean result=true;
			
			params.setProductNm(req.getParameter("productNm"));			
			if(req.getParameter("gasId").equals("0")) params.setGasId(0);
			else params.setGasId(Integer.parseInt(req.getParameter("gasId")));
			params.setMemberCompSeq(1);
			//int productId = productService.registerProduct(params);
			logger.info("ProductContoller registerProduct params.getGasId()d ==="+params.getGasId());
			ProductPriceVO[] priceVo = new ProductPriceVO[priceCount] ;
			
			
			for(int i =0 ; i <priceCount ; i++ ) {
				ProductPriceVO priceVo1 = new ProductPriceVO();
				result =false;
				RequestUtils.initUserPrgmInfo(req, priceVo1);
				
				//priceVo1.setProductId(Integer.valueOf(productId));
				priceVo1.setProductPrice(Integer.parseInt(req.getParameter("productPrice_"+i)));
				priceVo1.setProductCapa(req.getParameter("productCapa_"+i));
				
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
		logger.info("ProductContoller updateProduct");
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.product"));
		
		try {

			// 가스 정보 불러오기
			List<GasVO> gasList = gasService.getGasList();
			model.addAttribute("gasList", gasList);
						
			logger.debug("******params.getProductId()()) *****===*"+params.getProductId());
			//Product 정보
			ProductVO productVo = productService.getProductDetails(params.getProductId());			
			model.addAttribute("product", productVo);
			
			//ProductPrice 정보
			List<ProductPriceVO> productPriceList = productService.getProductPriceList(params.getProductId());
			logger.debug("******getProductPriceList*****= size==*"+productPriceList.size());
			
			logger.debug("******getProductPriceList*****= getProductCapa==*"+productPriceList.get(0).getProductCapa());
			model.addAttribute("productPriceList", productPriceList);
			
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	
		return "/gms/product/update";
	}
	
	@RequestMapping(value = "/gms/product/modify.do", method = RequestMethod.POST)
	public String modifyProduct(HttpServletRequest req) {
		logger.info("ProductContoller registerProduct");
		try {
			//임시
			
			logger.info("ProductContoller registerProductgetGasId ==="+req.getParameter("gasId"));
			logger.info("ProductContoller registerProduct getPrductNm ==="+req.getParameter("productNm"));
			logger.info("ProductContoller registerProduct productPrice_0 ==="+req.getParameter("productPrice_0"));
			logger.info("ProductContoller registerProduct priceCount ==="+req.getParameter("priceCount"));
			
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
			
			logger.info("ProductContoller registerProduct params.getGasId()d ==="+params.getGasId());
			
			ProductPriceVO[] priceVo = new ProductPriceVO[priceCount] ;
						
			for(int i =0 ; i < priceCount ; i++ ) {
				ProductPriceVO priceVo1 = new ProductPriceVO();
				result = false;
			
				RequestUtils.initUserPrgmInfo(req, priceVo1);
				
				//priceVo1.setProductId(Integer.valueOf(productId));
				priceVo1.setProductId(Integer.parseInt(req.getParameter("productId")));	
				priceVo1.setProductPriceSeq(Integer.parseInt(req.getParameter("productPriceSeq_"+i)));
				priceVo1.setProductPrice(Integer.parseInt(req.getParameter("productPrice_"+i)));
				priceVo1.setProductCapa(req.getParameter("productCapa_"+i));
				
				priceVo[i] = priceVo1;			
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

		logger.info("******deleteProduct params.getProductId()()) *****===*"+productId);
		
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
	public String modifyProductPriceStatus(Integer productId, Integer productPriceSeq, String productStatus,Model model) {
		
		ProductPriceVO params = new ProductPriceVO();
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.product"));

		logger.info("******modifyProductPriceStatus params.getProductId()()) *****===*"+productId);
		logger.info("******modifyProductPriceStatus params.productPriceSeq()()) *****===*"+productPriceSeq);
		
		try { 
			//임시6
			params.setProductId(productId);
			params.setProductPriceSeq(productPriceSeq);
			params.setProductStatus(productStatus);
			
			boolean result = productService.modifyProductPriceStatus(params);
			
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
				
		if(result != null) logger.info("******result *****===*"+result.getProductId());
		else logger.info("******result is null  *****===*"); 
		
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
}
