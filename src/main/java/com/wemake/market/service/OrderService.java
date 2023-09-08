package com.wemake.market.service;

import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.exception.CouponErrorException;
import com.wemake.market.exception.ItemNotFoundException;

public interface OrderService {
    int getOrderPrice(OrderDto orderDto) throws ItemNotFoundException, CouponErrorException;
}
