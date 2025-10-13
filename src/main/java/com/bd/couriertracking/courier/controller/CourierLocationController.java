package com.bd.couriertracking.courier.controller;

import com.bd.couriertracking.courier.model.request.CourierLocationRequest;
import com.bd.couriertracking.courier.service.CourierLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courier-location")
@RequiredArgsConstructor
public class CourierLocationController {

    private final CourierLocationService locationService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishLocation(@RequestBody @Valid CourierLocationRequest req) {
        locationService.publishLocation(req);
    }

}