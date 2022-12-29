package com.api.admin.controller;

import com.api.admin.model.QrCodeModel;
import com.api.tools.crypting.CryptingService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

/**
 * Class creation of the Qr-code used on the TPE.
 */

@RestController
@RequestMapping(path = "/qr-code")
public class QrCodeController {

    @Value("${default.qrcode.secret}")
    private String key;
    private final CryptingService cryptingService;

    @Autowired
    public QrCodeController(CryptingService cryptingService) {
        this.cryptingService = cryptingService;
    }

    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public ResponseEntity createQrcode(@RequestBody QrCodeModel qrCode) {
        try {
            String token = qrCode.getSoldAmount() + ":" + qrCode.getNbDayOfValidity() + ":" + qrCode.getExpirationDate();
            String encryptedString = cryptingService.encrypt(token, key);

            //call qrCode manager
            //Change qrCode => qrCheck (a qrcode don't need a nbDaysOfValidity)

            String qrCodeUuid = generateQrcode(encryptedString);

            // Build JSON with message and qrCodeUuid
            Map<String, String> map = new HashMap<>();
            map.put("message", "QrCode created");
            map.put("uuid", qrCodeUuid);
            return ResponseEntity.ok().body(map);

        } catch (IOException | WriterException e) {
            return ResponseEntity.status(500).body(e);
        }
    }

    public String generateQrcode(String check) throws WriterException, IOException
    {
        try {
            UUID uuid = UUID.randomUUID();
            String str = "qr-code-" + uuid;
            String path = "/qr-code/" + str + ".jpg";

            Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<>();
            hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            qrcode(check, path, "UTF-8", 250, 250);
            return str;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static void qrcode(String data, String path, String charset, int h, int w) throws WriterException, IOException
    {
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, w, h);
        MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path));
    }

    //Return the qrCode in the navigator
    @RequestMapping(path = "/{qrCodeUuid}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImageWithMediaType(
            @PathVariable String qrCodeUuid
    ) throws IOException {
        InputStream in = new FileInputStream("/qr-code/" + qrCodeUuid + ".jpg");
        var image = IOUtils.toByteArray(in);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(image.length);

        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}