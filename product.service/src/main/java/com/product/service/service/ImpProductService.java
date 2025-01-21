package com.product.service.service;

import com.product.service.model.Product;
import com.product.service.repository.RepositoryProduct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImpProductService implements IProductService{

    @Autowired
    private RepositoryProduct repositoryProduct;


    @Override
    @Transactional
    public Product save(Product product) {
        return repositoryProduct.save(product);
    }

    @Override
    public Product findById(Long id) {
        return repositoryProduct.findById(id).orElseGet(null);
    }

    @Override
    public void deleted(Long id) {
        repositoryProduct.deleteById(id);
    }

    @Override
    public List<Product> findAll() {
        return repositoryProduct.findAll();
    }
}
