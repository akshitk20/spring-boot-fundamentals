package com.example.springboot.controller;

import com.example.springboot.model.Product;
import com.example.springboot.model.ProductEvent;
import com.example.springboot.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductRepository repository;

    @GetMapping
    public Flux<Product> getAllProducts(){
        return repository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Mono<ResponseEntity<Product>> getProductById(@PathVariable String id){
        return repository.findById(id)
                .map(product -> ResponseEntity.ok(product))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> saveProduct(@RequestBody Product product){
        return repository.save(product);
    }

    @PutMapping(value = "/{id}")
    public Mono<ResponseEntity<Product>> updateProduct(@PathVariable String id , @RequestBody Product product){
        return repository.findById(id)
                .flatMap(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setPrice(product.getPrice());
                    return repository.save(existingProduct);
                }).map(updateProduct -> ResponseEntity.ok(updateProduct))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable String id){
        return repository.deleteById(id)
                .map(product -> ResponseEntity.ok(product))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/events",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductEvent> getProductEvents(){
        return Flux.interval(Duration.ofSeconds(1))
                .map(val -> new ProductEvent(val,"Product Event"));
    }
}
