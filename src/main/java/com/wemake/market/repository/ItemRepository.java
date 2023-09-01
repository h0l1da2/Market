package com.wemake.market.repository;

import com.wemake.market.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByName(String name);
    Optional<Item> findByNameAndIsUpdate(String name, boolean isUpdate);
}
