package org.hambomb.examples;

import org.hambomb.cache.autoconfigure.EnableHambombCache;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableHambombCache
@MapperScan(basePackages = {"org.hambomb.cache.examples.mapper"})
public class AutoConfigApplication {


	public static void main(String[] args) {
		SpringApplication.run(AutoConfigApplication.class, args);
	}

}
