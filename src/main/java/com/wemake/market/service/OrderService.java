package com.wemake.market.service;

import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.exception.NotFoundException;

public interface OrderService {
    int getOrderPrice(OrderDto orderDto) throws NotFoundException;
}
