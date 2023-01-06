package com.api.apk.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class ApkService {

    /**
     * Create HttpHeaders for .apk file
     *
     * @param file File instance
     * @param filename name of file
     * @return HttpHeaders with headers for .apk file
     */
    public HttpHeaders setHeadersApkRoute(File file, String filename) throws IOException {
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

    /**
     * Get .apk file
     *
     * @param path path to file
     * @return File
     */
    public File getApkFile(String path) throws FileNotFoundException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found.");
        }
        return file;
    }

}
