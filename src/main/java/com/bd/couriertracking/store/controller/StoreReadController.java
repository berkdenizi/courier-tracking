package com.bd.couriertracking.store.controller;

import com.bd.couriertracking.common.api.ApiResult;
import com.bd.couriertracking.common.api.PageResult;
import com.bd.couriertracking.store.model.response.StoreResponse;
import com.bd.couriertracking.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreReadController {

    private final StoreService storeService;

    @GetMapping
    public ApiResult<PageResult<StoreResponse>> list(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResult.ok(storeService.list(pageable));
    }

    @GetMapping("/{id}")
    public ApiResult<StoreResponse> get(@PathVariable Long id) {
        return ApiResult.ok(storeService.get(id));
    }
}
