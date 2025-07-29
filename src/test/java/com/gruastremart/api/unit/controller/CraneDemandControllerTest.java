package com.gruastremart.api.unit.controller;

import com.gruastremart.api.controller.CraneDemandController;
import com.gruastremart.api.dto.AssignCraneDemandDto;
import com.gruastremart.api.dto.CraneDemandCreateRequestDto;
import com.gruastremart.api.dto.CraneDemandResponseDto;
import com.gruastremart.api.dto.LocationDto;
import com.gruastremart.api.dto.RequestMetadataDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.service.CraneDemandService;
import com.gruastremart.api.utils.tools.RequestMetadataExtractorUtil;
import com.gruastremart.api.utils.enums.WeightCategoryEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class CraneDemandControllerTest {

    @InjectMocks
    private CraneDemandController craneDemandController;

    @Mock
    private CraneDemandService craneDemandService;

    @Mock
    private RequestMetadataExtractorUtil requestMetadataExtractorUtil;

    @Test
    void testFindWithFilters() {
        // Arrange
        CraneDemandResponseDto craneDemandResponseDto = new CraneDemandResponseDto();
        craneDemandResponseDto.setId("1");
        craneDemandResponseDto.setCurrentLocation(LocationDto.builder().latitude(10.0).longitude(20.0).build());
        craneDemandResponseDto.setDestinationLocation(LocationDto.builder().latitude(30.0).longitude(40.0).build());

        Page<CraneDemandResponseDto> page = new PageImpl<>(List.of(craneDemandResponseDto));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Mockito.when(craneDemandService.findWithFilters(params)).thenReturn(page);

        // Act
        ResponseEntity<Page<CraneDemandResponseDto>> result = craneDemandController.findWithFilters(params);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().getTotalElements());
        assertEquals(craneDemandResponseDto.getId(), result.getBody().getContent().getFirst().getId());
    }

    @Test
    void testFindById() {
        // Arrange
        CraneDemandResponseDto craneDemandResponseDto = new CraneDemandResponseDto();
        craneDemandResponseDto.setId("1");
        craneDemandResponseDto.setCurrentLocation(LocationDto.builder().latitude(10.0).longitude(20.0).build());
        craneDemandResponseDto.setDestinationLocation(LocationDto.builder().latitude(30.0).longitude(40.0).build());

        Mockito.when(craneDemandService.getCraneDemandById(eq("1"))).thenReturn(craneDemandResponseDto);

        // Act
        ResponseEntity<CraneDemandResponseDto> result = craneDemandController.findById("1");

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(craneDemandResponseDto.getId(), result.getBody().getId());
    }

    @Test
    void testFindByIdNotFound() {
        // Arrange
        Mockito.when(craneDemandService.getCraneDemandById(anyString())).thenThrow(new ServiceException("Crane request not found", 404));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            ResponseEntity<CraneDemandResponseDto> result = craneDemandController.findById("invalid-id");
        });

        // Verificar el mensaje de la excepción
        assertEquals("Crane request not found", exception.getMessage());

    }

    @Test
    void testCreateCraneDemand() {
        // Arrange
        HttpServletRequest mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
        RequestMetadataDto mockMetadata = RequestMetadataDto.builder()
                .userId("user123")
                .email("user@example.com")
                .role("admin")
                .ip("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .timestamp(LocalDateTime.now())
                .build();
        try (MockedStatic<RequestMetadataExtractorUtil> mockedStatic = Mockito.mockStatic(RequestMetadataExtractorUtil.class)) {
            mockedStatic.when(() -> RequestMetadataExtractorUtil.extract(any(HttpServletRequest.class)))
                    .thenReturn(mockMetadata);

            CraneDemandCreateRequestDto craneDemandRequest = new CraneDemandCreateRequestDto();
            craneDemandRequest.setCurrentLocation(LocationDto.builder().latitude(10.0).longitude(20.0).build());
            craneDemandRequest.setDestinationLocation(LocationDto.builder().latitude(30.0).longitude(40.0).build());
            // Vehicle information
            craneDemandRequest.setVehicleBrand("Ford");
            craneDemandRequest.setVehicleModel("Ecosport");
            craneDemandRequest.setVehicleYear(2006);
            craneDemandRequest.setVehiclePlate("A00A49G");
            craneDemandRequest.setVehicleColor("Gris");

            CraneDemandResponseDto createdResponse = new CraneDemandResponseDto();
            createdResponse.setId("1");
            createdResponse.setCurrentLocation(LocationDto.builder().latitude(10.0).longitude(20.0).build());
            createdResponse.setDestinationLocation(LocationDto.builder().latitude(30.0).longitude(40.0).build());
            // Vehicle information in response
            createdResponse.setVehicleBrand("Ford");
            createdResponse.setVehicleModel("Ecosport");
            createdResponse.setVehicleYear(2006);
            createdResponse.setVehiclePlate("A00A49G");
            createdResponse.setVehicleColor("Gris");

            Mockito.when(craneDemandService.createCraneDemand(any(), any())).thenReturn(createdResponse);

            // Act
            ResponseEntity<CraneDemandResponseDto> result = craneDemandController.createCraneDemand(craneDemandRequest, mockHttpServletRequest);

            // Assert
            assertEquals(HttpStatus.CREATED, result.getStatusCode());
            assertNotNull(result.getBody());
            assertEquals(craneDemandRequest.getCurrentLocation(), result.getBody().getCurrentLocation());
            assertEquals("Ford", result.getBody().getVehicleBrand());
            assertEquals("Ecosport", result.getBody().getVehicleModel());
            assertEquals(2006, result.getBody().getVehicleYear());
            assertEquals("A00A49G", result.getBody().getVehiclePlate());
            assertEquals("Gris", result.getBody().getVehicleColor());
        }
    }

    @Test
    void testAssignCraneDemand() {
        // Arrange
        AssignCraneDemandDto assignCraneDemandDto = AssignCraneDemandDto.builder()
                .userId("user123")
                .weightCategory(WeightCategoryEnum.PESO_1)
                .latitude(10.0)
                .longitude(20.0)
                .build();

        CraneDemandResponseDto updatedResponse = new CraneDemandResponseDto();
        updatedResponse.setId("1");
        updatedResponse.setCurrentLocation(LocationDto.builder()
                .latitude(10.0)
                .longitude(20.0)
                .build());

        Mockito.when(craneDemandService.assignCraneDemand(eq("1"), any(AssignCraneDemandDto.class))).thenReturn(Optional.of(updatedResponse));

        // Act
        ResponseEntity<CraneDemandResponseDto> result = craneDemandController.assignCraneDemand("1", assignCraneDemandDto);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(updatedResponse.getCurrentLocation(), result.getBody().getCurrentLocation());
    }

    @Test
    void testAssignCraneDemandNotFound() {
        // Arrange
        AssignCraneDemandDto assignCraneDemandDto = AssignCraneDemandDto.builder()
                .userId("user123")
                .weightCategory(WeightCategoryEnum.PESO_1)
                .latitude(10.0)
                .longitude(20.0)
                .build();

        Mockito.when(craneDemandService.assignCraneDemand(anyString(), any(AssignCraneDemandDto.class)))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<CraneDemandResponseDto> result = craneDemandController.assignCraneDemand("invalid-id", assignCraneDemandDto);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void testCancelCraneDemand() {
        // Arrange
        Mockito.doNothing().when(craneDemandService).cancelCraneDemand(eq("1"));

        // Act
        ResponseEntity<Void> result = craneDemandController.cancelCraneDemand("1");

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
