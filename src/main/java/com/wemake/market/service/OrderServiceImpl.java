package com.wemake.market.service;

import com.wemake.market.domain.Coupon;
import com.wemake.market.domain.How;
import com.wemake.market.domain.Item;
import com.wemake.market.domain.Where;
import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.domain.dto.OrderItemDto;
import com.wemake.market.domain.dto.PayDto;
import com.wemake.market.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ItemRepository itemRepository;

    @Override
    public int getOrderPrice(OrderDto orderDto) {

        AtomicInteger price = new AtomicInteger();

        calcItemPrice(orderDto.getItems(), price);

        price.set(price.get() + orderDto.getDeliveryPrice());

        return price.get();
    }

    @Override
    public int getPayPrice(PayDto payDto) {

        AtomicInteger price = new AtomicInteger();

        // 쿠폰이 존재한다면 ?
        if (payDto.isUseCoupon()) {
            Coupon coupon = payDto.getCoupon();

            Where wheres = coupon.getWheres();
            How how = coupon.getHow();
            int rate = coupon.getRate();
            int amount = coupon.getAmount();

            if (wheres.equals(Where.ITEM)) {
                payDto.getItems().forEach(i -> {
                    List<Item> itemList = itemRepository.findByName(i.getName());
                    Item item = itemList.get(itemList.size() - 1);

                    if (coupon.getName().equals(item.getName())) {
                        if (how.equals(How.FIXED)) {
                            // 고정값을 아이템 값에서 뺀 후 ...
                            price.set(price.get() + ((item.getPrice() - amount) * i.getCount()));
                        }
                        if (how.equals(How.PERCENTAGE)) {
                            // 퍼센테이지를 계산 후 ...
                            int value = 100 - rate;
                            value = (int) (value * 0.01);
                            price.set(price.get() + ((item.getPrice() * value) * i.getCount()));
                        }
                    } else {
                        price.set(price.get() + (item.getPrice() * i.getCount()));
                    }

                });
            }

            if (wheres.equals(Where.ORDER)) {
                calcItemPrice(payDto.getItems(), price);

                if (how.equals(How.FIXED)) {
                    // 고정값을 전체 값에서 뺀 후 ...
                    price.set(price.get() - amount);
                }
                if (how.equals(How.PERCENTAGE)) {
                    // 퍼센테이지를 계산 후 ...
                    int value = 100 - rate;
                    value = (int) (value * 0.01);
                    price.set(price.get() * value);
                }

                price.set(price.get() + payDto.getDeliveryPrice());
            }

        }

        if (!payDto.isUseCoupon()) {
            calcItemPrice(payDto.getItems(), price);
            price.set(price.get() + payDto.getDeliveryPrice());
        }

        return price.get();
    }

    private void calcItemPrice(List<OrderItemDto> payDto, AtomicInteger price) {
        payDto.forEach(i -> {
            List<Item> itemList = itemRepository.findByName(i.getName());
            Item item = itemList.get(itemList.size() - 1);

            price.set(price.get() + (item.getPrice() * i.getCount()));
        });
    }
}
