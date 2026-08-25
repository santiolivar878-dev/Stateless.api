package com.stateless.stateless.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public Map upload(MultipartFile multipartFile) throws IOException {
        // Subimos a una carpeta específica llamada 'stateless_products'
        return cloudinary.uploader().upload(multipartFile.getBytes(), 
                ObjectUtils.asMap("folder", "stateless_products"));
    }

    public Map delete(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}