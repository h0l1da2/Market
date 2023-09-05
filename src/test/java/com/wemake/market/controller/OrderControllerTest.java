package com.wemake.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemake.market.domain.*;
import com.wemake.market.domain.dto.ItemCreateDto;
import com.wemake.market.domain.dto.OrderItemDto;
import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void clean() {
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("결제가격 성공 : 아이템 한개")
    void 결제가격_성공_아이템한개() throws Exception {

        saveItem("감자", 3000);
        saveItem("고구마", 1000);
        saveItem("사과", 2000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 2);

        list.add(orderItemDto1);

        OrderDto orderDto = new OrderDto(list, 1000, false);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.data").value(Code.OK.name()))
                .andExpect(jsonPath("$.price").value(7000))
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템 여러개")
    void 결제가격_성공_아이템여러개() throws Exception {

        saveItem("감자", 3000);
        saveItem("고구마", 1000);
        saveItem("사과", 2000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 2);
        OrderItemDto orderItemDto2 = new OrderItemDto("고구마", 1);
        OrderItemDto orderItemDto3 = new OrderItemDto("사과", 3);

        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);

        OrderDto orderDto = new OrderDto(list, 1000, false);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.data").value(Code.OK.name()))
                .andExpect(jsonPath("$.price").value(14000))
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템한개,아이템쿠폰(고정값)")
    void 결제가격_성공_아이템한개_쿠폰_고정값() throws Exception {

        saveItem("감자", 3000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 2);

        list.add(orderItemDto1);

        Coupon coupon = new Coupon("감자", How.FIXED, Where.ITEM);
        coupon.setAmount(2000);

        OrderDto orderDto = new OrderDto(list, 1000, true, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.data").value(Code.OK.name()))
                .andExpect(jsonPath("$.price").value(5000))
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템여러개,아이템쿠폰(고정값)")
    void 결제가격_성공_아이템여러개_쿠폰_고정값() throws Exception {

        saveItem("감자", 3000);
        saveItem("고구마", 1000);
        saveItem("사과", 2000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 2);
        OrderItemDto orderItemDto2 = new OrderItemDto("고구마", 1);
        OrderItemDto orderItemDto3 = new OrderItemDto("사과", 3);

        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);

        Coupon coupon = new Coupon("감자", How.FIXED, Where.ITEM);
        coupon.setAmount(2000);

        OrderDto orderDto = new OrderDto(list, 1000, true, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.data").value(Code.OK.name()))
                .andExpect(jsonPath("$.price").value(12000))
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템한개,아이템쿠폰(비율)")
    void 결제가격_성공_아이템한개_쿠폰_비율() throws Exception {

        saveItem("감자", 3000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 2);

        list.add(orderItemDto1);

        Coupon coupon = new Coupon("감자", How.PERCENTAGE, Where.ITEM);
        coupon.setRate(10);

        OrderDto orderDto = new OrderDto(list, 1000, true, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.data").value(Code.OK.name()))
                .andExpect(jsonPath("$.price").value(6400))
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템여러개,아이템쿠폰(비율)")
    void 결제가격_성공_아이템여러개_쿠폰_비율() throws Exception {

        saveItem("감자", 3000);
        saveItem("고구마", 1000);
        saveItem("사과", 2000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 2);
        OrderItemDto orderItemDto2 = new OrderItemDto("고구마", 1);
        OrderItemDto orderItemDto3 = new OrderItemDto("사과", 3);

        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);

        Coupon coupon = new Coupon("감자", How.PERCENTAGE, Where.ITEM);
        coupon.setRate(10);

        OrderDto orderDto = new OrderDto(list, 1000, true, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.data").value(Code.OK.name()))
                .andExpect(jsonPath("$.price").value(13400))
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템한개,주문쿠폰(고정값)")
    void 결제가격_성공_아이템한개_주문쿠폰_고정값() throws Exception {

        saveItem("감자", 3000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 2);

        list.add(orderItemDto1);

        Coupon coupon = new Coupon(How.FIXED, Where.ORDER);
        coupon.setAmount(1000);

        OrderDto orderDto = new OrderDto(list, 1000, true, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.data").value(Code.OK.name()))
                .andExpect(jsonPath("$.price").value(6000))
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템여러개,주문쿠폰(고정값)")
    void 결제가격_성공_아이템여러개_주문쿠폰_고정값() throws Exception {

        saveItem("감자", 3000);
        saveItem("고구마", 1000);
        saveItem("사과", 2000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 2);
        OrderItemDto orderItemDto2 = new OrderItemDto("고구마", 1);
        OrderItemDto orderItemDto3 = new OrderItemDto("사과", 3);

        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);

        Coupon coupon = new Coupon(How.FIXED, Where.ORDER);
        coupon.setAmount(1000);

        OrderDto orderDto = new OrderDto(list, 1000, true, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.data").value(Code.OK.name()))
                .andExpect(jsonPath("$.price").value(13000))
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템한개,주문쿠폰(비율)")
    void 결제가격_성공_아이템한개_주문쿠폰_비율() throws Exception {

        saveItem("감자", 3000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 2);

        list.add(orderItemDto1);

        Coupon coupon = new Coupon(How.PERCENTAGE, Where.ORDER);
        coupon.setRate(10);

        OrderDto orderDto = new OrderDto(list, 1000, true, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.data").value(Code.OK.name()))
                .andExpect(jsonPath("$.price").value(6300))
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템여러개,주문쿠폰(비율)")
    void 결제가격_성공_아이템여러개_주문쿠폰_비율() throws Exception {

        saveItem("감자", 3000);
        saveItem("고구마", 1000);
        saveItem("사과", 2000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 2);
        OrderItemDto orderItemDto2 = new OrderItemDto("고구마", 1);
        OrderItemDto orderItemDto3 = new OrderItemDto("사과", 3);

        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);


        Coupon coupon = new Coupon(How.PERCENTAGE, Where.ORDER);
        coupon.setRate(10);

        OrderDto orderDto = new OrderDto(list, 1000, true, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.data").value(Code.OK.name()))
                .andExpect(jsonPath("$.price").value(12600))
                .andDo(print());

    }

    private Item saveItem(String name, int price) {
        ItemCreateDto itemCreateDto = new ItemCreateDto(name, price, Role.MARKET);
        Item item = new Item(itemCreateDto);
        itemRepository.save(item);

        return item;
    }
}