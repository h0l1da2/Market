package com.wemake.market.repository;

import com.wemake.market.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByName(String name);
    List<Item> findByNameAndIsUpdate(String name, boolean isUpdate);
    void deleteAllByName(String name);
    Optional<Item> findByNameAndDate(String name, LocalDateTime date);
}
