package com.api.bank.runner;

        import com.api.bank.model.entity.QrCheck;
        import com.api.bank.service.CheckService;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.boot.ApplicationArguments;
        import org.springframework.boot.ApplicationRunner;
        import org.springframework.stereotype.Component;

/**
 * This class is used to create a default QrCheck account
 */
@Component
public class QrCheckRunner implements ApplicationRunner {

    private final CheckService qrCheckService;

    @Value("${default.individual.account.id}")
    private String applicantAccountId;

    @Value("${default.qrcheck.token}")
    private String token;

    @Autowired
    public QrCheckRunner(CheckService qrCheckService) {
        this.qrCheckService = qrCheckService;
    }

    @Override
    public void run(ApplicationArguments args) {

        QrCheck qrCheckSerach = qrCheckService.getCheckByCheckToken(token);
        if (qrCheckSerach == null) {
            QrCheck qrCheck = new QrCheck(500, token, applicantAccountId);
            qrCheckService.add(qrCheck);
        }
    }
}

