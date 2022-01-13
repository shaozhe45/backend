package com.changqing.gov.demo.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.changqing.gov.demo.dao.ProductMapper;
import com.changqing.gov.demo.entity.Product;
import com.changqing.gov.demo.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 业务实现类
 * 商品
 * </p>
 *
 * @author changqing
 * @date 2019-08-13
 */
@Slf4j
@Service
@DS("#thread.tenant")
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Product entity) {
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveEx(Product data) {
        boolean bool = super.save(data);
        int a = 1 / 0;
        return bool;
    }
}
