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

//		int _1m = 1024 * 1024;
//		byte[] data = new byte[_1m];
//		// 将data置为null即让它成为垃圾
//		data = null;
//		// 通知垃圾回收器回收垃圾
//		System.gc();
	}

}
