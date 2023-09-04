package com.wemake.market.repository;

import com.wemake.market.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByName(String name);
    List<Item> findByNameAndIsUpdate(String name, boolean isUpdate);
    void deleteAllByName(String name);
    @Query("select i from Item i where i.name = :name and i.date between :offset and :limit")
    List<Item> findByNameAndDate(String name, LocalDateTime offset, LocalDateTime limit);
}
