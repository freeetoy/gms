package com.gms.web.admin.service.manage;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.domain.manage.GasVO;
import com.gms.web.admin.domain.manage.ProductPriceVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.domain.manage.ProductVO;
import com.gms.web.admin.mapper.manage.ProductMapper;

@Service
public class ProductServiceImpl implements ProductService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ProductMapper productMapper;
/*
	
	@Override
	@Transactional
	public int registerProduct(ProductVO param) {
		boolean successFlag = false;
		int productId = 0;
		
		// 가스정보 등록
		int result = 0;
		logger.info("****** registerProductparam.getProductId()()) *****===*"+param.getProductId());
		if (param.getProductId() == null) {
			
			productId = getProductId();
			
			param.setProductId(Integer.valueOf(productId));
			logger.info("****** registerProductparam.getProductId()()) *****===*"+param.getProductId());
			
			result = productMapper.insertProduct(param);
			if (result > 0) {
				
				successFlag = true;
			}
		} 
		
		return productId;
	}
*/
	@Override
	@Transactional
	public boolean modifyProduct(ProductVO param) {
		boolean successFlag = false;

		// 가스정보 등록
		int result = 0;
		
			result = productMapper.updateProduct(param);
		if (result > 0) {
			successFlag = true;
		}
	
		
		return successFlag;
	}
	
	@Transactional
	public boolean modifyProductPrice(ProductPriceVO param) {
		boolean successFlag = false;

		// 가스정보 등록
		int result = 0;
		
			result = productMapper.updateProductPrice(param);
		if (result > 0) {
			successFlag = true;
		}
	
		
		return successFlag;
	}
	
	@Override
	public ProductVO getProductDetails(Integer prodcutId) {
		return productMapper.selectProductDetail(prodcutId);	
	}

	@Override
	@Transactional
	public boolean deleteProduct(Integer productId) {
		ProductVO product = productMapper.selectProductDetail(productId);
		
		logger.info("****** deleteProduct.productId *****===*"+productId);
		logger.info("****** product.productId()*****===*"+product.getProductId());
		
		if (product == null || "0".equals(product.getProductStatus())) {
			return false;
		}

		int result = productMapper.deleteProduct(productId);
		if (result < 1) {
			return false;
		}
		
		return true;
	}
/*
	@Override
	public List<ProductVO> getProductList() {
		logger.info("****** getProductList *****===*");
		return productMapper.selectProductList();
	}
*/
	@Override
	@Transactional
	public boolean registerProductPrice(ProductPriceVO param) {
		boolean successFlag = false;
		// 가스정보 등록
		int result = 0;
		logger.info("****** registerProductPrice.getProductId()()) *****===*"+param.getProductId());
		if (param.getProductId() != null) {					
			
			logger.info("****** registerProductparam.getProductId()()) *****===*"+param.getProductId());
			
			result = productMapper.insertProductPrice(param);
			if (result > 0) {				
				successFlag = true;
			}
		} 
		
		return successFlag;
	}

	@Override
	public List<ProductPriceVO> getProductPriceList(Integer productId) {
		logger.info("****** getProductPriceList *****===*");
		return productMapper.selectProductPriceList(productId);
	}

	@Override
	public List<ProductTotalVO> getProductTotalList() {
		logger.info("****** getProductTotalList *****===*");
		return productMapper.selectProductTotalList();
	}

	@Override
	public List<ProductTotalVO> getCustomerProductTotalList(Integer customerId) {
		return productMapper.selectCustomerProductTotalList(customerId);
	}
	
	@Override
	public int getProductId() {
		// TODO Auto-generated method stub
		
		int result = productMapper.selectProductCount()+1;
		
		return result;
	}

	@Override
	public int getProductProductSeq(Integer productId) {
		// TODO Auto-generated method stub
		
		int result = productMapper.selectProductPriceSeq(productId)+1;
		
		return result;
	}

	@Override
	public boolean registerProduct(ProductVO param, ProductPriceVO[] param1) {
		boolean successFlag = false;
		int productId = 0;
		
		// 가스정보 등록
		int result = 0;
		
		logger.info("****** Start registerProduct param.getProductId()()) *****===*"+param.getProductId());
		if (param.getProductId() == null) {
			
			productId = getProductId();
			
			param.setProductId(Integer.valueOf(productId));
			param.setMemberCompSeq(1);		
			
			logger.info("****** after set ProductId registerProduct param.getProductId()()) *****===*"+param.getProductId());
			logger.info("****** registerProduct param.getGasId *****===*"+param.getGasId());
			logger.info("****** registerProduct param.param1.length *****===*"+param1.length);
			
			
			logger.info("****** before registerProduct param. result *****===*"+result);
			result = productMapper.insertProduct(param);
			
			logger.info("****** after registerProductparam. result *****===*"+result);
			
			
			if (result > 0) {
				ProductPriceVO priceVo = null;
				
				//int productPriceSeq = getProductProductSeq(Integer.valueOf(productId));
				
				//logger.info("****** registerProductparam.getProductPriceSeq()()) *****===*"+productPriceSeq);
				
				boolean pResult = false;
				for(int i =0 ; i < param1.length ; i++ ) {
					pResult = false;
					priceVo = param1[i];
					priceVo.setProductId(productId);
					priceVo.setProductPriceSeq(i+1);
					
					pResult = registerProductPrice(priceVo);
					
					if (pResult == false) {
						// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
						successFlag = false;
					}				
				}				
				successFlag = true;
			}
		} 
		
		return successFlag;
	}

	@Override
	public boolean modifyProduct(ProductVO param, ProductPriceVO[] param1) {
		boolean successFlag = false;
		int productId = 0;
		
		// 가스정보 등록
		int result = 0;
		
		logger.info("****** Start modifyProduct param.getProductId()()) *****===*"+param.getProductId());
		if (param.getProductId() != null) {
			
			productId = param.getProductId();
			
			logger.info("****** after set ProductId modifyProduct param.getProductId()()) *****===*"+param.getProductId());
			logger.info("****** modifyProduct param.getGasId *****===*"+param.getGasId());
			logger.info("****** modifyrProduct param.param1.length *****===*"+param1.length);
			
			
			logger.info("****** before modifyProduct param. result *****===*"+result);
			result = productMapper.updateProduct(param);
			
			logger.info("****** after modifyProductparam. result *****===*"+result);
			
			
			if (result > 0) {
				
				int delProductPrice = productMapper.deleteProductPrice(param.getProductId());
				
				if(delProductPrice > 0) {
					ProductPriceVO priceVo = null;
					
					//int productPriceSeq = getProductProductSeq(Integer.valueOf(productId));
					
					//logger.info("****** modify Productparam.getProductPriceSeq()()) *****===*"+productPriceSeq);
					
					logger.info("****** modify Product param1.length) *****===*"+param1.length);
					
					boolean pResult = false;
					for(int i =0 ; i < param1.length ; i++ ) {
						pResult = false;
						priceVo = param1[i];
												
						//pResult = modifyProductPrice(priceVo);
						pResult = registerProductPrice(priceVo);
						
						if (pResult == false) {
							// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
							successFlag = false;
						}				
					}		
					successFlag = true;
				}
				
			}
		} 
		
		return successFlag;
	}

	@Override
	public boolean deleteProductPrice(Integer productId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean modifyProductPriceStatus(ProductPriceVO param) {
		boolean successFlag = false;

		// 정보 등록
		int result = 0;
		logger.info("****** modifyProductPriceStatus.getProductId()()) *****===*"+param.getProductId());
		logger.info("****** modifyProductPriceStatus.getProductPriceSeq()()) *****===*"+param.getProductPriceSeq());
		
		result = productMapper.updateProductPriceStatus(param);
		if (result > 0) {
			successFlag = true;
		}
		
		return successFlag;
	}

	@Override
	public List<ProductVO> getProductList() {
		logger.info("****** getProductTotalList *****===*");
		return productMapper.selectProductList();
	}

	
}
