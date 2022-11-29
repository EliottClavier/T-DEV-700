package com.api.api.controller;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping(path = "/download")
public class ApkController {

    private HttpHeaders setHeadersApkRoute(File file, String filename) throws IOException {
        FileSystemResource fileSystemResource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.setContentLength(fileSystemResource.contentLength());
        headers.setContentDispositionFormData("attachment", filename + ".apk");
        return headers;
    }

    public File getApkFile(String path) throws FileNotFoundException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found.");
        }
        return file;
    }

    @RequestMapping(path = "/shop.apk", method = RequestMethod.GET, produces = "application/apk")
    public ResponseEntity<InputStreamResource> getShopApk() throws IOException {
        File file = getApkFile("/apks/shop.apk");
        InputStreamResource isResource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = setHeadersApkRoute(file, "shop");
        return new ResponseEntity<>(isResource, headers, HttpStatus.OK);
    }

    @RequestMapping(path = "/tpe.apk", method = RequestMethod.GET, produces = "application/apk")
    public ResponseEntity<InputStreamResource> getTpeApk() throws IOException {
        File file = getApkFile("/apks/tpe.apk");
        InputStreamResource isResource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = setHeadersApkRoute(file, "tpe");
        return new ResponseEntity<>(isResource, headers, HttpStatus.OK);
    }

}
