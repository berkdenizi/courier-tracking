package com.bd.couriertracking.courier.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierDto {
    private Long id;
    private String identityNumber;
    private String phoneNumber;
    private String name;
    private String surname;
}
