package com.api.apk.controller;
import com.api.apk.service.ApkService;
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

    private final ApkService apkService;

    public ApkController(
            ApkService apkService
    ) {
        this.apkService = apkService;
    }

    /**
     * Route to download Shop app
     *
     * @return ResponseEntity with headers and .apk file
     */
    @RequestMapping(path = "/shop.apk", method = RequestMethod.GET, produces = "application/apk")
    public ResponseEntity<InputStreamResource> getShopApk() throws IOException {
        try {
            File file = apkService.getApkFile("/apks/shop.apk");
            InputStreamResource isResource = new InputStreamResource(new FileInputStream(file));
            HttpHeaders headers = apkService.setHeadersApkRoute(file, "shop");
            return new ResponseEntity<>(isResource, headers, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found.");
        }
    }

    /**
     * Route to download TPE app
     *
     * @return ResponseEntity with headers and .apk file
     */
    @RequestMapping(path = "/tpe.apk", method = RequestMethod.GET, produces = "application/apk")
    public ResponseEntity<InputStreamResource> getTpeApk() throws IOException {
        try {
            File file = apkService.getApkFile("/apks/tpe.apk");
            InputStreamResource isResource = new InputStreamResource(new FileInputStream(file));
            HttpHeaders headers = apkService.setHeadersApkRoute(file, "tpe");
            return new ResponseEntity<>(isResource, headers, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found.");
        }
    }

}
