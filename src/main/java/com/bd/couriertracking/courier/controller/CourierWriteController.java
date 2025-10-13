package com.bd.couriertracking.courier.controller;

import com.bd.couriertracking.common.api.ApiResult;
import com.bd.couriertracking.courier.model.request.CreateCourierRequest;
import com.bd.couriertracking.courier.model.response.CourierResponse;
import com.bd.couriertracking.courier.service.CourierCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/couriers")
@RequiredArgsConstructor
public class CourierWriteController {
    private final CourierCommandService commandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<CourierResponse> create(@RequestBody @Valid CreateCourierRequest req) {
        return ApiResult.ok(commandService.create(req));
    }
}

