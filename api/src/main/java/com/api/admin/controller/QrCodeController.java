package com.api.admin.controller;

import com.api.admin.model.QrCodeModel;
import com.api.bank.manager.IBankManager;
import com.api.bank.manager.IQrCheckManager;
import com.api.bank.model.enums.PaymentMethod;
import com.api.bank.model.transaction.QrCheckTransactionModel;
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
    private final IBankManager bankManager;

    @Autowired
    public QrCodeController(
            CryptingService cryptingService,
            IBankManager bankManager
    ) {
        this.cryptingService = cryptingService;
        this.bankManager = bankManager;
    }

    /**
     * Route to generate a Qr-code (image and database entry)
     *
     * @param qrCode data to generate the Qr-code
     * @return ResponseEntity with headers and .png file, or error message in case of exception
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public ResponseEntity createQrcode(@RequestBody QrCodeModel qrCode) {
        try {
            // Create the QR code (the image)
            String token = qrCode.getAmount() + ":" + qrCode.getClientId() + ":" + new Date().getTime();
            String encryptedString = cryptingService.encrypt(token, key);
            String qrCodeUuid = generateQrcode(encryptedString);

            if (qrCodeUuid == null) {
                throw new Exception("Error while generating the QR code");
            } else {
                // Execute the transaction between Bank and the client asking for the QR code
                QrCheckTransactionModel qrCheckTransactionModel = new QrCheckTransactionModel(
                        null, encryptedString, qrCode.getAmount(), qrCode.getClientId(), PaymentMethod.TRANSFER
                );
                bankManager.buyCheckTransaction(qrCheckTransactionModel);

                // Build JSON Response with message and qrCodeUuid
                Map<String, String> map = new HashMap<>();
                map.put("message", "QrCode created");
                map.put("uuid", qrCodeUuid);
                return ResponseEntity.ok().body(map);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e);
        }
    }

    /**
     * Method which calls QR Code generator and prepare it to be placed in the file system
     *
     * @param check
     * @return
     */
    public String generateQrcode(String check) {
        try {
            UUID uuid = UUID.randomUUID();
            String str = "qr-code-" + uuid;
            String path = "/qr-code/" + str + ".jpg";

            Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<>();
            hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            qrcode(check, path, "UTF-8", 250, 250);
            return str;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Method which generates the QR Code
     *
     * @param data the text to be encoded
     * @param path the path where the QR Code will be saved
     * @param charset the charset to be used
     * @param h the height of the QR Code
     * @param w the width of the QR Code
     * @throws WriterException
     * @throws IOException
     */
    public static void qrcode(String data, String path, String charset, int h, int w) throws WriterException, IOException
    {
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, w, h);
        MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path));
    }

    /**
     * Return the Qr-code image
     *
     * @param qrCodeUuid the uuid of the Qr-code
     * @return ResponseEntity with headers and .png file, or error message in case of exception
     * @throws IOException
     */
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