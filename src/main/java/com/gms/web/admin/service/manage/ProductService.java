package com.gms.web.admin.service.manage;

import java.util.List;

import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.ProductPriceVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.domain.manage.ProductVO;

public interface ProductService {

	public boolean registerProduct(ProductVO param, ProductPriceVO[] priceVo);
	
	public boolean modifyProduct(ProductVO param, ProductPriceVO[] priceVo);
	
	//public int registerProduct(ProductVO param);
	
	public boolean modifyProduct(ProductVO param);
		
	public boolean registerProductPrice(ProductPriceVO param);
	
	public boolean modifyProductPriceStatus(ProductPriceVO param);


	public ProductVO getProductDetails(Integer productId);
	
	public ProductTotalVO getProductTotalDetails(ProductTotalVO param);
	
	public ProductPriceVO getProductPriceDetails(ProductPriceVO param);

	public boolean deleteProduct(Integer productId);
	
	public boolean deleteProductPrice(Integer productId);
	
	public int getProductId();

	public List<ProductTotalVO> getProductTotalList();
	
	public List<ProductTotalVO> getCustomerProductTotalList(Integer customerId);
	
	public ProductTotalVO getBottleGasCapa(BottleVO param);
	
	public List<ProductPriceVO> getProductPriceList(Integer productId);
	
	public int getProductProductSeq(Integer productId) ;
	
	public List<ProductVO> getProductList();
	
	public List<ProductVO> getGasProductList();
}
