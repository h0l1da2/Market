package com.wemake.market.service;

import com.wemake.market.domain.*;
import com.wemake.market.domain.dto.OrderItemDto;
import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.repository.ItemPriceHistoryRepository;
import com.wemake.market.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ItemRepository itemRepository;
    private final ItemPriceHistoryRepository itemPriceHistoryRepository;

    @Override
    public int getOrderPrice(OrderDto orderDto) throws ItemNotFoundException {

        AtomicInteger price = new AtomicInteger();

        boolean useCoupon = orderDto.isUseCoupon();
        int deliveryPrice = orderDto.getDeliveryPrice();

        // 쿠폰이 존재한다면 ?
        if (useCoupon) {
            Coupon coupon = orderDto.getCoupon();

            Where wheres = coupon.getWheres();
            How how = coupon.getHow();
            int rate = coupon.getRate();
            int amount = coupon.getAmount();

            AtomicBoolean itemNotFoundFlag = new AtomicBoolean(false);

            // 아이템 관련 쿠폰
            if (wheres.equals(Where.ITEM)) {
                orderDto.getItems().forEach(item -> {

                    Item findItem = itemRepository.findById(item.getId()).orElse(null);

                    if (findItem == null) {

                        itemNotFoundFlag.set(true);
                        return;

                    }

                    ItemPriceHistory finalItemStatus = getFinalItemStatusByItem(itemNotFoundFlag, findItem);
                    if (finalItemStatus == null) return;
                    int itemPrice = finalItemStatus.getPrice() * item.getCount();
                    int resultItemPrice = 0;

                    if (coupon.getItem().getId().equals(findItem.getId())) {

                        if (how.equals(How.FIXED)) {
                            // 고정값을 아이템 값에서 뺀 후 ...
                            resultItemPrice = itemPrice - amount;
                            price.set(price.get() + resultItemPrice);
                        }

                        if (how.equals(How.PERCENTAGE)) {
                            // 퍼센테이지를 계산 후 ...
                            double percent = (100 - rate) * 0.01;
                            double resultPrice = itemPrice * percent;
                            price.set((int) (price.get() + resultPrice));
                        }

                    } else {

                        price.set(price.get() + itemPrice);

                    }

                });

                if (itemNotFoundFlag.get() == true) {
                    throw new ItemNotFoundException();
                }

                // 배달비 계산
                price.set(price.get() + deliveryPrice);
            }

            // 주문 관련 쿠폰
            if (wheres.equals(Where.ORDER)) {
                calcItemPrice(orderDto.getItems(), price);

                if (how.equals(How.FIXED)) {
                    // 고정값을 전체 값에서 뺀 후 ...
                    price.set(price.get() + deliveryPrice);
                    price.set(price.get() - amount);
                }
                if (how.equals(How.PERCENTAGE)) {
                    // 퍼센테이지를 계산 후 ...
                    double percent = (100 - rate) * 0.01;
                    price.set((int) ((price.get() + deliveryPrice) * percent));
                }

            }

        }

        // 쿠폰이 없음
        if (!useCoupon) {
            calcItemPrice(orderDto.getItems(), price);
            price.set(price.get() + deliveryPrice);
        }

        int resultPrice = price.get();

        return resultPrice <= 0 ? 0 : resultPrice;
    }

    private void calcItemPrice(List<OrderItemDto> payDto, AtomicInteger price) throws ItemNotFoundException {

        AtomicBoolean itemNotFoundFlag = new AtomicBoolean(false);

        payDto.forEach(orderItem -> {

            Item findItem = itemRepository.findById(orderItem.getId()).orElse(null);

            if (findItem == null) {
                itemNotFoundFlag.set(true);
                return;
            }

            ItemPriceHistory finalItemStatus = getFinalItemStatusByItem(itemNotFoundFlag, findItem);
            if (finalItemStatus == null) return;

            price.set(price.get() + (finalItemStatus.getPrice() * orderItem.getCount()));

        });

        if (itemNotFoundFlag.get() == true) {
            throw new ItemNotFoundException();
        }

    }

    private ItemPriceHistory getFinalItemStatusByItem(AtomicBoolean itemNotFoundFlag, Item findItem) {
        List<ItemPriceHistory> findPriceHistories = itemPriceHistoryRepository.findByItem(findItem);
        if (findPriceHistories.size() == 0) {

            itemNotFoundFlag.set(true);
            return null;

        }
        ItemPriceHistory finalItemStatus = findPriceHistories.get(findPriceHistories.size() - 1);
        return finalItemStatus;
    }

}
