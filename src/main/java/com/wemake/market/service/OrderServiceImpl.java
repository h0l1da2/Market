package com.wemake.market.service;

import com.wemake.market.domain.Coupon;
import com.wemake.market.domain.How;
import com.wemake.market.domain.Item;
import com.wemake.market.domain.Where;
import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.domain.dto.OrderItemDto;
import com.wemake.market.domain.dto.PayDto;
import com.wemake.market.exception.ItemDuplException;
import com.wemake.market.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ItemRepository itemRepository;

    @Override
    public int getOrderPrice(OrderDto orderDto) throws ItemDuplException {

        AtomicInteger price = new AtomicInteger();

        calcItemPrice(orderDto.getItems(), price);

        price.set(price.get() + orderDto.getDeliveryPrice());

        return price.get();
    }

    @Override
    public int getPayPrice(PayDto payDto) throws ItemDuplException {

        AtomicInteger price = new AtomicInteger();

        // 쿠폰이 존재한다면 ?
        if (payDto.isUseCoupon()) {
            Coupon coupon = payDto.getCoupon();

            Where wheres = coupon.getWheres();
            How how = coupon.getHow();
            int rate = coupon.getRate();
            int amount = coupon.getAmount();

            ConcurrentHashMap<String, Item> map = new ConcurrentHashMap<>();
            AtomicBoolean flag = new AtomicBoolean(false);

            if (wheres.equals(Where.ITEM)) {
                payDto.getItems().forEach(i -> {
                    List<Item> itemList = itemRepository.findByName(i.getName());
                    Item item = itemList.get(itemList.size() - 1);

                    // 중복 아이템 검사
                    Item findItem = map.get(item.getName());
                    if (findItem != null) {
                        flag.set(true);
                        return;
                    } else {
                        map.put(item.getName(), item);
                    }

                    if (coupon.getName().equals(item.getName())) {
                        if (how.equals(How.FIXED)) {
                            // 고정값을 아이템 값에서 뺀 후 ...
                            price.set(price.get() + (item.getPrice() * i.getCount()) - amount);
                        }
                        if (how.equals(How.PERCENTAGE)) {
                            // 퍼센테이지를 계산 후 ...
                            price.set((int) (price.get() + (((item.getPrice() * i.getCount())) * ((100 - rate) * 0.01))));
                        }
                    } else {
                        price.set(price.get() + (item.getPrice() * i.getCount()));
                    }

                });

                if (flag.get() == true) {
                    throw new ItemDuplException();
                }

                price.set(price.get() + payDto.getDeliveryPrice());
            }

            if (wheres.equals(Where.ORDER)) {
                calcItemPrice(payDto.getItems(), price);

                if (how.equals(How.FIXED)) {
                    // 고정값을 전체 값에서 뺀 후 ...
                    price.set(price.get() + payDto.getDeliveryPrice());
                    price.set(price.get() - amount);
                }
                if (how.equals(How.PERCENTAGE)) {
                    // 퍼센테이지를 계산 후 ...
                    price.set((int) ((price.get() + payDto.getDeliveryPrice()) * ((100 - rate) * 0.01)));
                }

            }

        }

        if (!payDto.isUseCoupon()) {
            calcItemPrice(payDto.getItems(), price);
            price.set(price.get() + payDto.getDeliveryPrice());
        }

        return price.get();
    }

    private void calcItemPrice(List<OrderItemDto> payDto, AtomicInteger price) throws ItemDuplException {

        ConcurrentHashMap<String, Item> map = new ConcurrentHashMap<>();
        AtomicBoolean flag = new AtomicBoolean(false);


        payDto.forEach(i -> {
            List<Item> itemList = itemRepository.findByName(i.getName());
            Item item = itemList.get(itemList.size() - 1);

            // 중복 아이템 검사
            Item findItem = map.get(item.getName());
            if (findItem != null) {
                flag.set(true);
                return;
            } else {
                map.put(item.getName(), item);
            }

            price.set(price.get() + (item.getPrice() * i.getCount()));
        });

        if (flag.get() == true) {
            throw new ItemDuplException();
        }
    }
}
