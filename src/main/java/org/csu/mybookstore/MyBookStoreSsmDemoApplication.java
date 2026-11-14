package org.csu.mybookstore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.csu.mybookstore.mapper")
public class MyBookStoreSsmDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyBookStoreSsmDemoApplication.class, args);
    }

}
