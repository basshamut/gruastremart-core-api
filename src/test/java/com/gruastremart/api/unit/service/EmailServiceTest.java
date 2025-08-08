package com.gruastremart.api.unit.service;

import com.gruastremart.api.dto.EmailRequestDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.service.EmailService;
import com.gruastremart.api.unit.utils.TestUtils;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailServiceTest {

    private JavaMailSender createMailSenderMock() {
        return mock(JavaMailSender.class);
    }

    private EmailService createEmailService(JavaMailSender mailSender) {
        EmailService service = new EmailService(mailSender);
        TestUtils.setField(service, "from", "test@gruastremart.com");
        TestUtils.setField(service, "to", "destino@gruastremart.com");
        TestUtils.setField(service, "contactSubject", "Solicitud de contacto");
        TestUtils.setField(service, "demandSubject", "Solicitud recibida");
        return service;
    }

    @Test
    public void testSendContactEmail_shouldSendEmailSuccessfully() throws Exception {
        // Arrange
        JavaMailSender mailSender = createMailSenderMock();
        EmailService emailService = createEmailService(mailSender);

        EmailRequestDto request = new EmailRequestDto();
        request.setName("Juan Pérez");
        request.setEmail("juan@example.com");
        request.setPhone("666777888");
        request.setMessage("Necesito una grúa");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        boolean result = emailService.sendContactEmail(request);

        // Assert
        assertTrue(result);
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    public void testSendResponseOfCraneDemandEmail_shouldSendEmailSuccessfully() throws Exception {
        // Arrange
        JavaMailSender mailSender = createMailSenderMock();
        EmailService emailService = createEmailService(mailSender);

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        boolean result = emailService.sendResponseOfCraneDemandEmail("Lucía", "lucia@example.com");

        // Assert
        assertTrue(result);
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    public void testSendEmail_shouldThrowServiceExceptionOnFailure() {
        // Arrange
        JavaMailSender mailSender = createMailSenderMock();
        EmailService emailService = createEmailService(mailSender);

        MimeMessage mockMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);
        doThrow(new RuntimeException("Fallo interno")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            emailService.sendResponseOfCraneDemandEmail("Pedro", "pedro@example.com");
        });

        assertEquals("Error al enviar el correo", exception.getMessage());
    }

}
