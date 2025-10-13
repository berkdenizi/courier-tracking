package com.bd.couriertracking.store.boot;

import com.bd.couriertracking.store.model.converter.StoreConverter;
import com.bd.couriertracking.store.model.dto.StoreDto;
import com.bd.couriertracking.store.model.entity.Store;
import com.bd.couriertracking.store.repository.StoreRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private static final String STORES_JSON_PATH = "/data/stores.json";
    private final StoreRepository storeRepository;
    private final ObjectMapper objectMapper;
    private final StoreConverter storeConverter;

    @Override
    public void run(String... args) {
        if (storeRepository.count() > 0) {
            log.info("Store table already contains data. Skipping data loading.");
            return;
        }

        try (InputStream is = DataLoader.class.getResourceAsStream(STORES_JSON_PATH)) {
            if (is == null) {
                log.error("Could not find stores.json at path: {}", STORES_JSON_PATH);
                return;
            }

            List<StoreDto> storeDtos = objectMapper.readValue(is, new TypeReference<>() {
            });

            if (storeDtos == null || storeDtos.isEmpty()) {
                log.info("stores.json is empty, nothing to process.");
                return;
            }

            List<Store> storesToPersist = storeConverter.dtoListToEntityList(storeDtos);

            if (storesToPersist != null && !storesToPersist.isEmpty()) {
                storeRepository.saveAll(storesToPersist);
                log.info("{} stores have been successfully loaded from stores.json.", storesToPersist.size());
            }

        } catch (Exception e) {
            log.error("Failed to load store data from stores.json", e);
        }
    }
}

