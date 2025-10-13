package com.bd.couriertracking.store.controller;

import com.bd.couriertracking.common.api.ApiResult;
import com.bd.couriertracking.common.api.PageResult;
import com.bd.couriertracking.store.model.request.GetStoreEntranceRequest;
import com.bd.couriertracking.store.model.response.StoreEntranceResponse;
import com.bd.couriertracking.store.service.StoreEntranceQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/store-entrance")
@RequiredArgsConstructor
public class StoreEntranceReadController {

    private final StoreEntranceQueryService storeEntranceQueryService;

    @PostMapping("/search")
    public ApiResult<PageResult<StoreEntranceResponse>> search(@RequestBody @Valid GetStoreEntranceRequest req) {
        return ApiResult.ok(storeEntranceQueryService.search(req));
    }
}
