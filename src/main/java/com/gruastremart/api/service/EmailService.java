package com.gruastremart.api.service;

import com.gruastremart.api.dto.EmailRequestDto;
import com.gruastremart.api.exception.ServiceException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    public static final String CORREO_ENVIADO_CORRECTAMENTE = "¡Correo enviado correctamente!";

    private final JavaMailSender sender;

    @Value("${mailer.from}")
    private String from;

    @Value("${mailer.to}")
    private String to;

    @Value("${mailer.contact-subject}")
    private String contactSubject;

    @Value("${mailer.demand-subject}")
    private String demandSubject;

    public boolean sendContactEmail(final EmailRequestDto emailRequest) {
        logger.info("Iniciando envío de correo de contacto...");

        String htmlBody = buildContactEmailBody(emailRequest);

        return sendEmail(contactSubject, htmlBody, to);
    }

    public boolean sendResponseOfCraneDemandEmail(final String nombre, final String email) {
        logger.info("Iniciando envío de correo de agradecimiento...");

        String htmlBody = buildRequestAcknowledgementEmailBody(nombre);

        return sendEmail(demandSubject, htmlBody, email);
    }

    public boolean sendPasswordChangeNotification(final String email, final String subject, final String htmlBody) {
        logger.info("Iniciando envío de notificación de cambio de contraseña...");
        return sendEmail(subject, htmlBody, email);
    }

    public boolean sendPasswordChangeNotification(final String email) {
        logger.info("Iniciando envío de notificación de cambio de contraseña...");
        String subject = "Contraseña cambiada exitosamente";
        String htmlBody = buildPasswordChangeEmailBody();
        return sendEmail(subject, htmlBody, email);
    }

    private boolean sendEmail(String subject, String body, String to) {
        logger.info("Iniciando envío de correo...");

        MimeMessage message = sender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            sender.send(message);
            logger.info(CORREO_ENVIADO_CORRECTAMENTE);
            return true;
        } catch (Exception e) {
            logger.error("Error al enviar el correo: ", e);
            throw new ServiceException("Error al enviar el correo", 500);
        }
    }

    private String buildContactEmailBody(EmailRequestDto emailRequest) {
        return "<h2>Nuevo mensaje de contacto</h2>"
                + "<p><strong>Nombre:</strong> " + emailRequest.getName() + "</p>"
                + "<p><strong>Email:</strong> " + emailRequest.getEmail() + "</p>"
                + "<p><strong>Teléfono:</strong> " + emailRequest.getPhone() + "</p>"
                + "<p><strong>Mensaje:</strong><br/>" + emailRequest.getMessage() + "</p>";
    }

    private String buildRequestAcknowledgementEmailBody(String nombre) {
        return "<h2>¡Gracias por tu mensaje, " + nombre + "!</h2>"
                + "<p>Hemos recibido tu solicitud correctamente y estamos trabajando en ella.</p>"
                + "<p>En breve uno de nuestros agentes se pondrá en contacto contigo si es necesario.</p>"
                + "<p>Puedes hacer seguiemiento de tu solicitud a través de nuestra aplicación web usando el mapa.</p>"
                + "<br/>"
                + "<p>Un saludo,<br/>"
                + "El equipo de Grúas Tre-Mart</p>";
    }

    private String buildPasswordChangeEmailBody() {
        return "<h2>Contraseña cambiada exitosamente</h2>"
                + "<p>Te informamos que tu contraseña ha sido cambiada exitosamente.</p>"
                + "<p>Si no realizaste este cambio, por favor contacta con nuestro soporte inmediatamente.</p>"
                + "<p>Fecha y hora: " + java.time.LocalDateTime.now().toString() + "</p>"
                + "<br/>"
                + "<p>Un saludo,<br/>"
                + "El equipo de Grúas Tre-Mart</p>"
                + "<br/>"
                + "<p><small>Este es un mensaje automático, por favor no respondas a este correo.</small></p>";
    }
}
