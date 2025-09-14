package com.OceanHazard.demo.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dbo6dahb6",
                "api_key", "153228629533719",
                "api_secret", "JEhHmTOtC5U6OV9LbrW8XlRJaLg"
        ));
    }
}
