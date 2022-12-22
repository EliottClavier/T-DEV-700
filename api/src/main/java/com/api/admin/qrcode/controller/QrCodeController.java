package com.api.admin.qrcode.controller;

import com.api.admin.qrcode.model.QrCodeModel;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

/**
 * Class creation of the Qr-code used on the TPE.
 */

@RestController
@RequestMapping(path = "/admin/")
public class QrCodeController {

    @Value("${default.qrcode.secret}")
    private String key;
    private final CryptingService cryptingService;

    @Autowired()
    public QrCodeController(CryptingService cryptingService) {
        this.cryptingService = cryptingService;
    }

    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public void createQrcode(@RequestBody QrCodeModel qrCode) {
        try {
            String token = qrCode.getSoldAmount() + ":" + qrCode.getNbDayOfValidity() + ":" + qrCode.getExpirationDate();
            String encryptedString = cryptingService.encrypt(token, key);

            //call qrCode manager
            //Change qrCode => qrCheck (a qrcode don't need a nbDaysOfValidity)

            generateQrcode(encryptedString);

        } catch (IOException | WriterException e) {
            System.out.println("An error occurred : " + e);
        }
    }

    public void generateQrcode(String check) throws WriterException, IOException
    {
        try {
            UUID uuid = UUID.randomUUID();
            String str = "qr-code-" + uuid;
            String path = "/qr-code/" + str + ".jpg";

            Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<>();
            hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            qrcode(check, path, "UTF-8", 250, 250);
            getImageWithMediaType(str);

        } catch (Exception e) {
            System.out.println("An error occurred : " + e);
        }
    }

    public static void qrcode(String data, String path, String charset, int h, int w) throws WriterException, IOException
    {
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, w, h);
        MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path));
    }

    //Return the qrCode in the navigator
    @GetMapping(value = "/newCode", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImageWithMediaType(String filename) throws IOException {
        InputStream in = new FileInputStream("../../../../qr-code/" + filename + ".jpg");
        return IOUtils.toByteArray(in);
    }
}