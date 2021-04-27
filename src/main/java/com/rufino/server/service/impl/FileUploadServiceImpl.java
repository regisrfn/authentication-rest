package com.rufino.server.service.impl;

import java.util.Arrays;
import java.util.List;

import com.rufino.server.model.FileResponse;
import com.rufino.server.service.FileUploadService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class FileUploadServiceImpl implements FileUploadService{

    private RestTemplate restTemplate;
    private Dotenv dotEnv;
    private String apiUrl;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    public FileUploadServiceImpl(RestTemplate restTemplate, Dotenv dotEnv) {
        this.restTemplate = restTemplate;
        this.dotEnv = dotEnv;
        this.apiUrl = this.dotEnv.get("API_UPLOAD_URL");
    }

    @Override
    public FileResponse upload(MultipartFile file) {
        HttpEntity<MultiValueMap<String, Object>> request = createRequest(file);
        FileResponse response = restTemplate.postForObject(apiUrl + "/upload", request, FileResponse.class);
        return response;
    }

    @Override
    public void delete(String url) {
        List<String> split = Arrays.asList(url.split("/"));
        try {
            restTemplate.delete(apiUrl + "/delete/" + split.get(split.size() - 1)); 
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage());
        }
               
    }

    private HttpEntity<MultiValueMap<String, Object>> createRequest(MultipartFile file) {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        body.add("file", file.getResource());
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        return request;
    }
    
}
