package com.gruastremart.api.service;

import com.gruastremart.api.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.gruastremart.api.dto.EmailRequestDto;

import jakarta.mail.internet.MimeMessage;


@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender sender;

    @Value("${mailer.from}")
    private String from;

    @Value("${mailer.to}")
    private String to;

    @Value("${mailer.subject}")
    private String subject;

    /**
     * Envía un correo de contacto con los datos proporcionados en EmailRequestDto.
     *
     * @param emailRequest DTO que contiene name, email, phone y message
     */
    public boolean sendContactEmail(final EmailRequestDto emailRequest) {
        logger.info("Iniciando envío de correo de contacto...");

        boolean send = false;
        MimeMessage message = sender.createMimeMessage();

        try {
            // Configuración del correo
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);

            // Construimos el contenido HTML
            String htmlBody = "<h2>Nuevo mensaje de contacto</h2>"
                    + "<p><strong>Nombre:</strong> " + emailRequest.getName() + "</p>"
                    + "<p><strong>Email:</strong> " + emailRequest.getEmail() + "</p>"
                    + "<p><strong>Teléfono:</strong> " + emailRequest.getPhone() + "</p>"
                    + "<p><strong>Mensaje:</strong><br/>" + emailRequest.getMessage() + "</p>";

            // Adjuntamos el HTML al correo
            helper.setText(htmlBody, true);

            // Enviamos
            sender.send(message);
            logger.info("¡Correo enviado correctamente!");

            return true;
        } catch (Exception e) {
            logger.error("Error al enviar el correo de contacto: ", e);
            throw new ServiceException("Error al enviar el correo de contacto", 500);
        }
    }
}
