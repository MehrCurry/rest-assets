package com.vjoon.se.core.config;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HashingConfig {
    @Bean
    public HashFunction hashFunction() {
        return Hashing.crc32();
    }
}
