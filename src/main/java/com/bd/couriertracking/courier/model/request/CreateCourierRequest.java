package com.bd.couriertracking.courier.model.request;

import com.bd.couriertracking.common.validation.MaxLength;
import com.bd.couriertracking.common.validation.Required;
import com.bd.couriertracking.common.validation.Tckn;
import com.bd.couriertracking.common.validation.TrPhone;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCourierRequest {

    @Tckn
    private String identityNumber;

    @TrPhone
    private String phoneNumber;

    @Required
    @MaxLength(120)
    private String name;

    @Required
    @MaxLength(120)
    private String surname;
}
