package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.entity.Image;
import com.enigma.wmb_api.repositry.ImageRepository;
import com.enigma.wmb_api.service.ImageService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    private final Path directoryPath;
    private final ImageRepository imageRepository;

    // Dependency Inject path from application.properties
    @Autowired
    public ImageServiceImpl(
           @Value("${wmb_api.multipart.path-location}") String directoryPath,
            ImageRepository imageRepository
    ) {
        this.directoryPath = Paths.get(directoryPath);
        this.imageRepository = imageRepository;
    }

    // Create directory path if not exist
    @PostConstruct
    public void initDirectory(){
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectory(directoryPath);
            } catch (IOException e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }

    // Create Image Service
    @Override
    public Image create(MultipartFile multipartFile) {
        try {
            // Validate only image format
            if (!List.of("image/jpeg", "image/png", "image/jpg", "image/svg+xml").contains(multipartFile.getContentType())
            )     throw new ConstraintViolationException(ResponseMessage.INVALID_CONTENT_TYPE, null);

            // Create unique file name
            String uniqueFilename = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();

            // Create Path (Folder/xxz.png)
            Path filePath = directoryPath.resolve(uniqueFilename);

            // Save file to path (1-binary, 2-location)
            Files.copy(multipartFile.getInputStream(), filePath);

            // Create Image
            Image image = Image.builder()
                    .name(uniqueFilename)
                    .contentType(multipartFile.getContentType())
                    .size(multipartFile.getSize())
                    .path(filePath.toString())
                    .build();

            // Save to Repository
            imageRepository.saveAndFlush(image);

            return image;
        } catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Get Image By Id Service (return Resource)
    @Override
    public Resource getById(String id) {
        try {
            // Find by Id or Throw Error
            Image image = imageRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.ERROR_NOT_FOUND));

            // Get Image Path
            Path filePath = Paths.get(image.getPath());
            // Conditional file path exists
            if (!Files.exists(filePath)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.ERROR_NOT_FOUND);

            // Return Url Resource
            return new UrlResource(filePath.toUri());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Delete Image Service
    @Override
    public void deleteById(String id) {
        try {
            // Find by Id or Throw Error
            Image image = imageRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.ERROR_NOT_FOUND));

            // Get Image Path
            Path filePath = Paths.get(image.getPath());
            // Conditional file path exists
            if (!Files.exists(filePath)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.ERROR_NOT_FOUND);

            // Delete in file path (directory)
            Files.delete(filePath);

            // Delete to Repository
            imageRepository.delete(image);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
