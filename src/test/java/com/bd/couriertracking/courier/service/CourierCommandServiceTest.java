package com.bd.couriertracking.courier.service;

import com.bd.couriertracking.common.exception.DuplicateResourceException;
import com.bd.couriertracking.courier.model.converter.CourierMapper;
import com.bd.couriertracking.courier.model.dto.CourierDto;
import com.bd.couriertracking.courier.model.entity.Courier;
import com.bd.couriertracking.courier.model.request.CreateCourierRequest;
import com.bd.couriertracking.courier.model.response.CourierResponse;
import com.bd.couriertracking.courier.repository.CourierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourierCommandServiceTest {

    @Mock CourierRepository courierRepository;
    @Mock CourierMapper mapper;

    @InjectMocks CourierCommandService service;

    @Test
    void create_ok_saves_and_returns_response() {
        var req = CreateCourierRequest.builder()
                .identityNumber("12345678901")
                .phoneNumber("+905554443322")
                .name("Ada").surname("Lovelace").build();

        var dto = new CourierDto();
        dto.setIdentityNumber("12345678901");
        dto.setPhoneNumber("+905554443322");
        dto.setName("Ada");
        dto.setSurname("Lovelace");
        when(mapper.toDto(req)).thenReturn(dto);

        when(courierRepository.existsByIdentityNumber("12345678901")).thenReturn(false);
        when(courierRepository.existsByPhoneNumber("+905554443322")).thenReturn(false);

        var entityToSave = new Courier();
        when(mapper.dtoToEntity(dto)).thenReturn(entityToSave);

        var saved = new Courier();
        saved.setId(1L);
        when(courierRepository.save(entityToSave)).thenReturn(saved);

        var savedDto = new CourierDto();
        savedDto.setId(1L);
        savedDto.setIdentityNumber("12345678901");
        savedDto.setPhoneNumber("+905554443322");
        savedDto.setName("Ada");
        savedDto.setSurname("Lovelace");
        when(mapper.entityToDto(saved)).thenReturn(savedDto);

        var resp = CourierResponse.builder()
                .id(1L).identityNumber("12345678901").phoneNumber("+905554443322")
                .name("Ada").surname("Lovelace").build();
        when(mapper.toResponse(savedDto)).thenReturn(resp);

        var out = service.create(req);

        assertThat(out.getId()).isEqualTo(1L);
        assertThat(out.getIdentityNumber()).isEqualTo("12345678901");
        verify(courierRepository).save(entityToSave);
    }

    @Test
    void create_duplicateIdentity_throws() {
        var req = CreateCourierRequest.builder()
                .identityNumber("DUPLICATE")
                .phoneNumber("+905554443322")
                .name("X").surname("Y").build();

        var dto = new CourierDto();
        dto.setIdentityNumber("DUPLICATE");
        dto.setPhoneNumber("+905554443322");
        when(mapper.toDto(req)).thenReturn(dto);

        when(courierRepository.existsByIdentityNumber("DUPLICATE")).thenReturn(true);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(DuplicateResourceException.class);
        verify(courierRepository, never()).save(any());
    }

    @Test
    void create_duplicatePhone_throws() {
        var req = CreateCourierRequest.builder()
                .identityNumber("12345678901")
                .phoneNumber("+90DUP")
                .name("X").surname("Y").build();

        var dto = new CourierDto();
        dto.setIdentityNumber("12345678901");
        dto.setPhoneNumber("+90DUP");
        when(mapper.toDto(req)).thenReturn(dto);

        when(courierRepository.existsByIdentityNumber("12345678901")).thenReturn(false);
        when(courierRepository.existsByPhoneNumber("+90DUP")).thenReturn(true);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(DuplicateResourceException.class);
        verify(courierRepository, never()).save(any());
    }


}
