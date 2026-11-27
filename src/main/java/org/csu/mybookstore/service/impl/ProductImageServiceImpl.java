package org.csu.mybookstore.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.csu.mybookstore.entity.ProductImage;
import org.csu.mybookstore.mapper.ProductImageMapper;
import org.csu.mybookstore.service.ProductImageService;
import org.springframework.stereotype.Service;

@Service
public class ProductImageServiceImpl extends ServiceImpl<ProductImageMapper, ProductImage> implements ProductImageService {
}
