package org.hambomb.examples;

import org.hambomb.cache.EnableHambombCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@SpringBootApplication
@ComponentScan(basePackages = {"org.hambomb.cache"})
@EnableHambombCache
public class AutoConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutoConfigApplication.class, args);
	}

}
