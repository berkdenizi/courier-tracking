package com.bd.couriertracking.store.service;

import com.bd.couriertracking.common.api.PageResult;
import com.bd.couriertracking.store.model.converter.StoreEntranceConverter;
import com.bd.couriertracking.store.model.entity.StoreEntrance;
import com.bd.couriertracking.store.model.entity.StoreEntrance.Fields;
import com.bd.couriertracking.store.model.request.GetStoreEntranceRequest;
import com.bd.couriertracking.store.model.response.StoreEntranceResponse;
import com.bd.couriertracking.store.repository.StoreEntranceRepository;
import com.bd.couriertracking.store.repository.spec.StoreEntranceSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreEntranceQueryService {

    private final StoreEntranceRepository repository;
    private final StoreEntranceConverter converter;

    public PageResult<StoreEntranceResponse> search(GetStoreEntranceRequest req) {
        Sort sort = Sort.by(
                "DESC".equalsIgnoreCase(req.getSortDir()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                req.getSortBy() == null ? Fields.entranceTime : req.getSortBy()
        );
        Pageable pageable = PageRequest.of(
                req.getPage() == null ? 0 : req.getPage(),
                req.getSize() == null ? 20 : req.getSize(),
                sort
        );

        Specification<StoreEntrance> spec = StoreEntranceSpecifications.buildSpec(req);

        var page = repository.findAll(spec, pageable).map(converter::entityToDto).map(converter::toResponse);
        return PageResult.from(page);
    }

}