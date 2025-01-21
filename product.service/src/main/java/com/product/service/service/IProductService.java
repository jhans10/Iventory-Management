package com.product.service.service;

import com.product.service.model.Product;

import java.util.List;

public interface IProductService {


    public Product save(Product product);

    public Product findById(Long id);

    public void deleted(Long id);

    public List<Product> findAll();




}
