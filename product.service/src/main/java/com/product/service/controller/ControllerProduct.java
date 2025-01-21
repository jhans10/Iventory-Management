package com.product.service.controller;


import com.product.service.model.Product;
import com.product.service.repository.RepositoryProduct;
import com.product.service.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api-product-controller")
public class ControllerProduct {

    @Autowired
    private IProductService iProductService;



    @PostMapping("/save-product")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> saveProduct(@RequestBody Product product){
        return ResponseEntity.ok(iProductService.save(product));
    }



    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){

        return ResponseEntity.ok(iProductService.findById(id));

    }

    @GetMapping("/find-all")
    public ResponseEntity<?> findAll(){
        return ResponseEntity.ok(iProductService.findAll());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        iProductService.deleted(id);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }






}
