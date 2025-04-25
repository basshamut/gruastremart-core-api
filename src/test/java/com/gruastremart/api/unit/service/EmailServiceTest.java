package com.gruastremart.api.unit.service;

import com.gruastremart.api.dto.EmailRequestDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.service.EmailService;
import com.gruastremart.api.unit.utils.TestUtils;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Captor
    ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        emailService = new EmailService(mailSender);
        TestUtils.setField(emailService, "from", "test@gruastremart.com");
        TestUtils.setField(emailService, "to", "destino@gruastremart.com");
        TestUtils.setField(emailService, "subject", "Solicitud recibida");
    }

    @Test
    public void testSendContactEmail_shouldSendEmailSuccessfully() throws Exception {
        EmailRequestDto request = new EmailRequestDto();
        request.setName("Juan Pérez");
        request.setEmail("juan@example.com");
        request.setPhone("666777888");
        request.setMessage("Necesito una grúa");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        boolean result = emailService.sendContactEmail(request);

        assertTrue(result);
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    public void testSendResponseOfCraneDemandEmail_shouldSendEmailSuccessfully() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        boolean result = emailService.sendResponseOfCraneDemandEmail("Lucía", "lucia@example.com");

        assertTrue(result);
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    public void testSendEmail_shouldThrowServiceExceptionOnFailure() {
        MimeMessage mockMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        doThrow(new RuntimeException("Fallo interno")).when(mailSender).send(any(MimeMessage.class));

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            emailService.sendResponseOfCraneDemandEmail("Pedro", "pedro@example.com");
        });

        assertEquals("Error al enviar el correo", exception.getMessage());
    }

}
