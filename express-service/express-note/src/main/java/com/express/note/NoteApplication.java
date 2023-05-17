package com.express.note;

import com.express.note.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableFeignClients
@SpringBootApplication
@EnableScheduling
public class NoteApplication {

    @Autowired
    public StringRedisTemplate redisTemplate;

    @Autowired
    public NoteService noteService;

    public static void main(String[] args) {
        SpringApplication.run(NoteApplication.class,args);
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            //TODO INIT
        };
    }
}
