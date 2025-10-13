package com.bd.couriertracking.courier.controller;

import com.bd.couriertracking.common.api.ApiResult;
import com.bd.couriertracking.common.api.PageResult;
import com.bd.couriertracking.courier.model.response.CourierDistanceResponse;
import com.bd.couriertracking.courier.model.response.CourierResponse;
import com.bd.couriertracking.courier.service.CourierQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/couriers")
@RequiredArgsConstructor
public class CourierReadController {
    private final CourierQueryService queryService;

    @GetMapping
    public ApiResult<PageResult<CourierResponse>> list(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResult.ok(queryService.listPage(pageable));
    }

    @GetMapping("/{id}")
    public ApiResult<CourierResponse> get(@PathVariable Long id) {
        return ApiResult.ok(queryService.get(id));
    }

    @GetMapping("/{id}/distance")
    public ApiResult<CourierDistanceResponse> getTotalDistance(@PathVariable("id") long courierId) {
        return ApiResult.ok(queryService.getTotalDistanceMeters(courierId));
    }
}
