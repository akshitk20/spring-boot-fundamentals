package com.example.springboot;

import com.example.springboot.model.Product;
import com.example.springboot.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner init(ProductRepository repository){
		return args -> {
			Flux<Product> productFlux = Flux.just(
					new Product(null,"Big Latte",2.99),
					new Product(null,"Big Decaf",2.49),
					new Product(null,"Green Tea",1.99)
			).flatMap(repository::save); //transform flux of products into flux of saved product

			productFlux
					.thenMany(repository.findAll())            //then allows the first publisher to complete and then execute
					.subscribe(System.out::println);           //the publisher it receives as argument
		};
	}
}
