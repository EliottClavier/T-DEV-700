package com.api.payment.controller;

import com.api.bank.manager.IQrCheckManager;
import com.api.bank.model.entity.QrCheck;
import com.api.tools.crypting.CryptingService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
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
@RequestMapping(path = "/admin/")
public class QrCodeController {

    @Value("${default.qrcode.secret}")
    private String key;
    private final IQrCheckManager qrCheckManager;
    private final CryptingService cryptingService;

    @Autowired()
    public QrCodeController(IQrCheckManager qrCheckManager, CryptingService cryptingService) {
        this.qrCheckManager = qrCheckManager;
        this.cryptingService = cryptingService;
    }

    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public void createQrcode(@RequestBody QrCheck qrCheck) throws Exception {
        try {
            QrCheck model = new QrCheck(qrCheck.getSoldAmount(), qrCheck.getNbDayOfValidity());
            String token = model.getSoldAmount() + ":" + model.getNbDayOfValidity() + ":" + model.getCreatedAt();
            String encryptedString = cryptingService.encrypt(token, key);

            model.setCheckToken(encryptedString);
            model = qrCheckManager.buyQrCheck(model);
            if(model != null) {
                generateQrcode(encryptedString);
            }
        } catch (IOException | WriterException e) {
            throw new Exception("Something went wrong : " + e);
        }
    }

    public static void generateQrcode(String check) throws WriterException, IOException
    {
        try {
            UUID uuid = UUID.randomUUID();
            String str = "qr-code-" + uuid;
            String path = "/qr-code/" + str + ".jpg";

            Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<>();
            hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            qrcode(check, path, "UTF-8", 250, 250);

            FileWriter myWriter = new FileWriter("/qr-code/filename.txt");
            myWriter.write(str);
            myWriter.close();

        } catch (Exception e) {
            System.out.println("An error occurred : " + e);
        }
    }

    public static void qrcode(String data, String path, String charset, int h, int w) throws WriterException, IOException
    {
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, w, h);
        MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path));
    }

    @GetMapping(value = "/newCode")
    @ResponseBody
    public void getImageDynamicType() throws IOException {
        MediaType contentType = MediaType.IMAGE_JPEG;
        InputStream in;

        FileWriter myWriter = new FileWriter("../../../../qr-code/test.txt");
        myWriter.write("data.jpg");
        myWriter.close();

        try {
            in = new FileInputStream("../../../../qr-code/data.jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            new ResponseEntity<>((InputStreamResource) null, HttpStatus.INTERNAL_SERVER_ERROR);
            return;
        }
        ResponseEntity.ok().contentType(contentType).body(new InputStreamResource(in));
    }
}

