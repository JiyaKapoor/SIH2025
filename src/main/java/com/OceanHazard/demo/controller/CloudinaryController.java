package com.OceanHazard.demo.controller;

import com.OceanHazard.demo.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/cloudinary")
public class CloudinaryController {
    private final CloudinaryService cloudinaryService;

    public CloudinaryController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }
    @GetMapping("/images")
    public Map listAllImages() throws Exception {
        return cloudinaryService.getAllImages();
    }

    @PostMapping("/upload")
    public Map<String,Object> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return cloudinaryService.uploadFile(file);
    }
}
