package com.wemake.market.service;

import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.domain.dto.PayDto;
import com.wemake.market.exception.ItemDuplException;

public interface OrderService {
    int getOrderPrice(OrderDto orderDto) throws ItemDuplException;
    int getPayPrice(PayDto payDto) throws ItemDuplException;
}
