package com.bd.couriertracking.courier.model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierResponse {
    private Long id;
    private String identityNumber;
    private String phoneNumber;
    private String name;
    private String surname;
}
