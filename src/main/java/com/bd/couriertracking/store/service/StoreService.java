package com.bd.couriertracking.store.service;

import com.bd.couriertracking.common.api.PageResult;
import com.bd.couriertracking.common.geo.GeoService;
import com.bd.couriertracking.store.model.converter.StoreConverter;
import com.bd.couriertracking.store.model.entity.Store;
import com.bd.couriertracking.store.model.response.StoreResponse;
import com.bd.couriertracking.store.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreConverter storeConverter;
    private final GeoService geoService;

    @Value("${app.geo.radius-meters:100}")
    private double defaultRadiusMeters;

    public StoreResponse get(Long id) {
        var store = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Store not found: " + id));
        return storeConverter.toResponse(storeConverter.entityToDto(store));
    }

    public PageResult<StoreResponse> list(Pageable pageable) {
        Page<StoreResponse> page = storeRepository.findAll(pageable)
                .map(storeConverter::entityToDto)
                .map(storeConverter::toResponse);
        return PageResult.from(page);
    }

    public Store findNearestWithinRadius(double lat, double lng, Double radiusMeters) {
        double r = (radiusMeters != null ? radiusMeters : defaultRadiusMeters);

        double degLat = r / 111_000d;
        double degLng = r / (111_000d * Math.cos(Math.toRadians(lat)));

        List<Store> candidates = storeRepository.findByLatitudeBetweenAndLongitudeBetween(
                lat - degLat, lat + degLat, lng - degLng, lng + degLng);

        return candidates.stream()
                .map(s -> new Object[]{s, geoService.distanceMeters(lat, lng, s.getLatitude(), s.getLongitude())})
                .filter(arr -> (double) arr[1] <= r)
                .min(Comparator.comparingDouble(arr -> (double) arr[1]))
                .map(arr -> (Store) arr[0])
                .orElse(null);
    }
}
