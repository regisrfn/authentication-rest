package com.rufino.server.service;

import com.rufino.server.model.FileResponse;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    FileResponse upload(MultipartFile file);
    
    public void delete(String url);
    
}
