package com.gruastremart.api.unit.controller;

import com.gruastremart.api.controller.EmailController;
import com.gruastremart.api.dto.EmailRequestDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class EmailControllerTest {
    @InjectMocks
    private EmailController emailController;

    @Mock
    private EmailService emailService;

    @Test
    void testSendContactEmail() {
        // Arrange
        EmailRequestDto emailRequest = new EmailRequestDto();
        emailRequest.setEmail("user@example.com");
        emailRequest.setMessage("Test message content");

        Mockito.when(emailService.sendContactEmail(any(EmailRequestDto.class))).thenReturn(true);

        // Act
        ResponseEntity<?> response = emailController.sendContactEmail(emailRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Correo enviado correctamente", response.getBody());

        Mockito.verify(emailService, Mockito.times(1)).sendContactEmail(emailRequest);
    }


    @Test
    void testSendContactEmailException() {
        // Arrange
        EmailRequestDto emailRequest = new EmailRequestDto();
        emailRequest.setName("John Doe");
        emailRequest.setEmail("johndoe@example.com");
        emailRequest.setPhone("123456789");
        emailRequest.setMessage("Test message");

        Mockito.when(emailService.sendContactEmail(any(EmailRequestDto.class)))
                .thenThrow(new ServiceException("Error al enviar el correo de contacto", 500));

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            emailController.sendContactEmail(emailRequest);
        });

        // Verificar el mensaje de la excepción
        assertEquals("Error al enviar el correo de contacto", exception.getMessage());
        assertEquals(500, exception.getCode());
    }
}
