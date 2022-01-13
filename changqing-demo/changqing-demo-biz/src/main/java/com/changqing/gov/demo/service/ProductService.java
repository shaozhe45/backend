package com.changqing.gov.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.changqing.gov.demo.entity.Product;

/**
 * <p>
 * 业务接口
 * 商品
 * </p>
 *
 * @author changqing
 * @date 2019-08-13
 */
public interface ProductService extends IService<Product> {

    boolean saveEx(Product data);
}
