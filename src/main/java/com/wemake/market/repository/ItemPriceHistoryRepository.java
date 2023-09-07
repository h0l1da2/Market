package com.wemake.market.repository;

import com.wemake.market.domain.Item;
import com.wemake.market.domain.ItemPriceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemPriceHistoryRepository extends JpaRepository<ItemPriceHistory, Long> {
    @Query("select i from ItemPriceHistory i where i.item = :item and i.date between :offset and :limit")
    Page<ItemPriceHistory> findByItemAndDate(Item item, LocalDateTime offset, LocalDateTime limit, Pageable pageable);
    void deleteAllByItem(Item item);
    List<ItemPriceHistory> findByItem(Item item);
}
