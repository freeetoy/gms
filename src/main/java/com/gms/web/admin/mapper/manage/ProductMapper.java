package com.gms.web.admin.mapper.manage;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.ProductPriceVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.domain.manage.ProductVO;

@Mapper
public interface ProductMapper {

	public int insertProduct(ProductVO param);
	
	public int insertProductPrice(ProductPriceVO param);

	public ProductVO selectProductDetail(Integer productId) ;
	
	public List<ProductPriceVO> selectProductPriceList(Integer productId);

	public int updateProduct(ProductVO param);
	
	public int updateProductPrice(ProductPriceVO param);
	
	public int updateProductPriceStatus(ProductPriceVO param);

	public int deleteProduct(Integer productId);
	
	public int deleteProductPrice(Integer productId);
	
	public int statusChangeProduct(ProductVO param);
	
	public int selectProductCount();

	public List<ProductTotalVO> selectProductTotalList();
	
	public int selectProductPriceSeq(Integer productId);
	
	public List<ProductVO> selectProductList();
}
