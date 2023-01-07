package com.api.bank.runner;

        import com.api.admin.controller.QrCodeController;
        import com.api.bank.model.entity.QrCheck;
        import com.api.bank.service.CheckService;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.boot.ApplicationArguments;
        import org.springframework.boot.ApplicationRunner;
        import org.springframework.stereotype.Component;
        import java.util.List;

/**
 * This class is used to create a default QrCheck account
 */
@Component
public class QrCheckRunner implements ApplicationRunner {

    private final CheckService qrCheckService;

    @Value("${default.qrcheck.token}")
    private String token;
    private final QrCodeController qrCodeController;
    @Autowired
    public QrCheckRunner(QrCodeController qrCodeController, CheckService qrCheckService) {
        this.qrCodeController = qrCodeController;
        this.qrCheckService = qrCheckService;
    }

    @Override
    public void run(ApplicationArguments args) {

            // Use for demonstration only
            boolean isQrCheckExist =( (List<QrCheck>)  qrCheckService.getAll().getData()).isEmpty();
            if (isQrCheckExist) {
                QrCheck qrCheck = new QrCheck(1500, token);
                qrCheckService.add(qrCheck);
            }
        }
}

