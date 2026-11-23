package com.consultation.medecin.service;

import com.consultation.medecin.model.Patient;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PatientQrScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientQrScheduler.class);

    private final RestClient restClient;
    private final String cdnPatientRoot;
    private final String qrBaseUrl;

    public PatientQrScheduler(
            @Value("${app.cdn.patient-root:/usr/local/cdn/patient}") String cdnPatientRoot,
            @Value("${app.patient.qr-base-url:http://localhost:8080/api/patients}") String qrBaseUrl) {
        this.cdnPatientRoot = cdnPatientRoot;
        this.qrBaseUrl = qrBaseUrl.endsWith("/") ? qrBaseUrl.substring(0, qrBaseUrl.length() - 1) : qrBaseUrl;
        this.restClient = RestClient.builder().baseUrl(this.qrBaseUrl).build();
    }

    @Scheduled(initialDelayString = "${app.qr-scheduler.initial-delay-ms:10000}",
               fixedDelayString = "${app.qr-scheduler.fixed-delay-ms:300000}")
    public void generatePatientQrcodes() {
        try {
            Patient[] patients = restClient.get()
                    .retrieve()
                    .body(Patient[].class);

            if (patients == null) {
                LOGGER.warn("The patient API returned no data");
                return;
            }

            QRCodeWriter writer = new QRCodeWriter();
            for (Patient patient : patients) {
                generateQrCode(writer, patient);
            }
            LOGGER.info("Generated QR codes for {} patients", patients.length);
        } catch (Exception exception) {
            LOGGER.error("Unable to retrieve or generate patient QR codes", exception);
        }
    }

    private void generateQrCode(QRCodeWriter writer, Patient patient) throws Exception {
        if (patient.getId() == null) {
            return;
        }

        Path directory = Paths.get(cdnPatientRoot, String.valueOf(patient.getId()), "qrcode");
        Files.createDirectories(directory);

        Path output = directory.resolve(patient.getId() + "-qrcode.jpg");
        String content = qrBaseUrl + "/" + patient.getId();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
        MatrixToImageWriter.writeToPath(matrix, "JPEG", output);
    }
}
