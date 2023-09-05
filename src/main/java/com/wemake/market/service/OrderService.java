package com.wemake.market.service;

import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.domain.dto.PayDto;
import com.wemake.market.exception.NotFoundException;

public interface OrderService {
    int getOrderPrice(OrderDto orderDto) throws NotFoundException;
    int getPayPrice(PayDto payDto) throws NotFoundException;
}
