package com.wemake.market.service;

import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.domain.dto.PayDto;

public interface OrderService {
    int getOrderPrice(OrderDto orderDto);
    int getPayPrice(PayDto payDto);
}
