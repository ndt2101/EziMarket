package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.model.ImageEntity;
import com.ndt2101.ezimarket.repository.ImageRepository;
import com.ndt2101.ezimarket.utils.FileHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/api/image")
public class ImageController extends BaseController<Long> {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FileHandle fileHandle;

    @PostMapping("/addImages")
    public ResponseEntity<?> addImage(@RequestParam("image") MultipartFile image, HttpServletRequest request) {
        Map<String, String> rs = uploadImages(image);
        String imageName = rs.keySet().stream().toList().get(0);
        String imageUrl = rs.values().stream().toList().get(0);
        ImageEntity imageEntity = imageRepository.save(new ImageEntity(imageName, imageUrl, null, null));
        return successfulResponse(imageEntity.getId());
    }

    private Map<String, String> uploadImages(MultipartFile image) {
        String imageUrl = "";
        String fileName = "";
        try {
            fileName = image.getOriginalFilename();                        // to get original file name
            fileName = UUID.randomUUID().toString().concat(fileHandle.getExtension(fileName));  // to generated random string values for file name.

            File file = fileHandle.convertToFile(image, fileName);                      // to convert multipartFile to File
            String path = "images/" + fileName;
            imageUrl = fileHandle.uploadFile(file, path);                                   // to get uploaded file link
            file.delete();                                                                // to delete the copy of uploaded file stored in the project folder
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> rs = new HashMap<>();
        rs.put(fileName, imageUrl);
        return rs;
    }
}
