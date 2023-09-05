package com.wemake.market.repository;

import com.wemake.market.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByName(String name);
    void deleteAllByName(String name);
    @Query("select i from Item i where i.name = :name and i.date between :offset and :limit")
    Page<Item> findByNameAndDate(String name, LocalDateTime offset, LocalDateTime limit, Pageable pageable);
}
