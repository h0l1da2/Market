package com.wemake.market.allTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemake.market.domain.*;
import com.wemake.market.domain.dto.*;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.repository.CouponRepository;
import com.wemake.market.repository.ItemPriceHistoryRepository;
import com.wemake.market.repository.ItemRepository;
import com.wemake.market.service.CouponService;
import com.wemake.market.service.ItemService;
import com.wemake.market.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@PropertySource("classpath:application.yml")
public class FinalTest {

    @Autowired
    private ItemPriceHistoryRepository itemPriceHistoryRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Value("${market.password}")
    private String password;

    @BeforeEach
    void clean() {
        couponRepository.deleteAll();
        itemPriceHistoryRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("아이템 추가1 : 성공 !")
    void create1() throws Exception {
        LocalDateTime createDate = LocalDateTime.of(2023, 1, 1, 12, 0, 0);

        ItemCreateDto itemCreateDto = getItemCreateDto("감자", 5000, Role.MARKET, createDate);

        mockMvc.perform(
                        post("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemCreateDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("name").value(itemCreateDto.getName()))
                .andExpect(jsonPath("price").value(itemCreateDto.getPrice()))
                .andDo(print());

    }

    @Test
    @DisplayName("아이템 추가2 : 성공 !")
    void create2() throws Exception {
        LocalDateTime createDate = LocalDateTime.of(2023, 1, 1, 12, 0, 0);
        ItemCreateDto itemCreateDto = getItemCreateDto("고구마", 1500, Role.MARKET, createDate);

        mockMvc.perform(
                        post("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemCreateDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("name").value(itemCreateDto.getName()))
                .andExpect(jsonPath("price").value(itemCreateDto.getPrice()))
                .andDo(print());

    }

    @Test
    void 아이템수정() throws Exception {
        LocalDateTime createDate = LocalDateTime.of(2023, 1, 1, 12, 0, 0);

        ItemCreateDto itemCreateDto1 = getItemCreateDto("감자", 5000, Role.MARKET, createDate);
        ItemCreateDto itemSaveCreateDto1 = itemService.createItem(itemCreateDto1);

        assertThat(itemSaveCreateDto1).isNotNull();
        assertThat(itemSaveCreateDto1.getName()).isEqualTo(itemCreateDto1.getName());
        assertThat(itemSaveCreateDto1.getDate()).isEqualTo(itemCreateDto1.getDate());
        assertThat(itemSaveCreateDto1.getPrice()).isEqualTo(itemCreateDto1.getPrice());

        LocalDateTime updateDate = LocalDateTime.of(2023, 9, 1, 12, 0, 0);

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .id(itemSaveCreateDto1.getId())
                .date(updateDate)
                .price(10000)
                .role(Role.MARKET)
                .password(password)
                .build();

        ItemUpdateDto itemUpdateCompleteDto = itemService.updateItem(itemUpdateDto);

        assertThat(itemUpdateCompleteDto).isNotNull();

        ItemDto searchFirstItemPrice = itemService.searchItemByTime(itemSaveCreateDto1.getId(), "2023-01-01T12:00:00");
        ItemDto searchSecondItemPrice = itemService.searchItemByTime(itemSaveCreateDto1.getId(), "2023-09-01T12:00:00");

        assertThat(searchFirstItemPrice.getPrice()).isEqualTo(itemCreateDto1.getPrice());
        assertThat(searchSecondItemPrice.getPrice()).isEqualTo(itemUpdateCompleteDto.getPrice());

        assertThat(searchFirstItemPrice.getPrice()).isNotEqualTo(searchSecondItemPrice.getPrice());
        assertThat(searchFirstItemPrice.getName()).isEqualTo(searchSecondItemPrice.getName());

        ItemDeleteDto itemDeleteDto = ItemDeleteDto.builder()
                .id(itemSaveCreateDto1.getId())
                .password(password)
                .role(Role.MARKET)
                .build();

        itemService.deleteItem(itemDeleteDto);

        Assertions.assertThrows(ItemNotFoundException.class, () ->
                itemService.searchItemByTime(itemSaveCreateDto1.getId(), "2023-01-01T12:00:00"));

        ItemCreateDto itemCreateDto2 = getItemCreateDto("고구마", 5000, Role.MARKET, createDate);
        ItemCreateDto itemSaveCreateDto2 = itemService.createItem(itemCreateDto2);
        ItemCreateDto itemCreateDto3 = getItemCreateDto("홈런볼", 1500, Role.MARKET, createDate);
        ItemCreateDto itemSaveCreateDto3 = itemService.createItem(itemCreateDto3);

        CouponSaveDto itemFixedCouponDto = CouponSaveDto.builder()
                .itemId(itemSaveCreateDto2.getId())
                .password(password)
                .role(Role.MARKET)
                .how(How.FIXED)
                .wheres(Where.ITEM)
                .amount(2000)
                .build();

        CouponSaveDto itemPercentageCouponDto = CouponSaveDto.builder()
                .itemId(itemSaveCreateDto3.getId())
                .password(password)
                .role(Role.MARKET)
                .how(How.PERCENTAGE)
                .wheres(Where.ITEM)
                .rate(10)
                .build();

        CouponSaveDto orderFixedCouponDto = CouponSaveDto.builder()
                .itemId(0L)
                .password(password)
                .role(Role.MARKET)
                .how(How.FIXED)
                .wheres(Where.ORDER)
                .amount(5000)
                .build();

        CouponSaveDto orderPercentageCouponDto = CouponSaveDto.builder()
                .itemId(0L)
                .password(password)
                .role(Role.MARKET)
                .how(How.PERCENTAGE)
                .wheres(Where.ORDER)
                .rate(10)
                .build();

        CouponDto itemFixedCoupon = couponService.saveCoupon(itemFixedCouponDto);
        CouponDto itemPercentageCoupon = couponService.saveCoupon(itemPercentageCouponDto);

        OrderItemDto orderItemDto2 = OrderItemDto.builder()
                .itemId(itemSaveCreateDto2.getId())
                .count(3)
                .build();
        OrderItemDto orderItemDto3 = OrderItemDto.builder()
                .itemId(itemSaveCreateDto3.getId())
                .count(1)
                .build();

        List<OrderItemDto> oneOrderItem2List = new ArrayList<>();

        oneOrderItem2List.add(orderItemDto2);

        List<OrderItemDto> oneItemFixedItem2List = new ArrayList<>();

        oneItemFixedItem2List.add(orderItemDto2);

        List<OrderItemDto> oneItemFixedItem3List = new ArrayList<>();

        oneItemFixedItem3List.add(orderItemDto3);

        List<OrderItemDto> noCouponOneItemList = new ArrayList<>();
        noCouponOneItemList.add(orderItemDto2);

        // 5000 * 3 , 1500 * 1 배송 2000
        OrderDto orderDto1 = getOrderDto(null, false, noCouponOneItemList);
        int orderPrice1 = orderService.getOrderPrice(orderDto1);
        assertThat(orderPrice1).isEqualTo(17000);

        OrderDto orderDto2 = getOrderDto(null, false, noCouponOneItemList);
        int orderPrice2 = orderService.getOrderPrice(orderDto2);
        assertThat(orderPrice2).isEqualTo(17000);

        // 2 - 5000 * 3 / 2000 쿠폰
        OrderDto orderDto3 = getOrderDto(itemFixedCoupon.getCouponId(), true, oneItemFixedItem2List);
        int orderPrice3 = orderService.getOrderPrice(orderDto3);
        assertThat(orderPrice3).isEqualTo(11000);

        // 3 - 1500 * 1 / 10 퍼
        OrderDto orderDto4 = getOrderDto(itemPercentageCoupon.getCouponId(), true, oneItemFixedItem3List);
        int orderPrice4 = orderService.getOrderPrice(orderDto4);
        assertThat(orderPrice4).isEqualTo(3350);

        // 4 - 2000
        // 5 - 3000
        ItemCreateDto itemCreateDto4 = getItemCreateDto("새콤달콤", 2000, Role.MARKET, createDate);
        ItemCreateDto itemSaveCreateDto4 = itemService.createItem(itemCreateDto4);
        ItemCreateDto itemCreateDto5 = getItemCreateDto("파인애플", 3000, Role.MARKET, createDate);
        ItemCreateDto itemSaveCreateDto5 = itemService.createItem(itemCreateDto5);

        CouponSaveDto manyItemFixedCouponDtoMany = CouponSaveDto.builder()
                .itemId(itemSaveCreateDto4.getId())
                .password(password)
                .role(Role.MARKET)
                .how(How.FIXED)
                .wheres(Where.ITEM)
                .amount(2000)
                .build();

        CouponSaveDto manyItemPercentageCouponDtoFive = CouponSaveDto.builder()
                .itemId(itemSaveCreateDto5.getId())
                .password(password)
                .role(Role.MARKET)
                .how(How.PERCENTAGE)
                .wheres(Where.ITEM)
                .rate(10)
                .build();

        CouponDto manyItemFixedCoupon = couponService.saveCoupon(manyItemFixedCouponDtoMany);
        CouponDto manyItemPercentageCoupon = couponService.saveCoupon(manyItemPercentageCouponDtoFive);

        OrderItemDto orderItemDto4 = OrderItemDto.builder()
                .itemId(itemSaveCreateDto4.getId())
                .count(4)
                .build();
        OrderItemDto orderItemDto5 = OrderItemDto.builder()
                .itemId(itemSaveCreateDto5.getId())
                .count(5)
                .build();

        List<OrderItemDto> manyItemFixedItemList = new ArrayList<>();

        // 23000
        manyItemFixedItemList.add(orderItemDto4);
        manyItemFixedItemList.add(orderItemDto5);

        // 4 - 2000 * 4
        // 5 - 3000 * 5
        // 배송 2000 고정
        OrderDto orderDto5 = getOrderDto(manyItemFixedCoupon.getCouponId(), true, manyItemFixedItemList);
        int orderPrice5 = orderService.getOrderPrice(orderDto5);
        assertThat(orderPrice5).isEqualTo(17000);

        List<OrderItemDto> manyItemPercentageItemList = new ArrayList<>();
        manyItemPercentageItemList.add(orderItemDto4);
        manyItemPercentageItemList.add(orderItemDto5);

        OrderDto orderDto6 = getOrderDto(manyItemPercentageCoupon.getCouponId(), true, manyItemPercentageItemList);
        int orderPrice6 = orderService.getOrderPrice(orderDto6);
        assertThat(orderPrice6).isEqualTo(23500);

        CouponDto orderFixedCoupon = couponService.saveCoupon(orderFixedCouponDto);
        CouponDto orderPercentageCoupon = couponService.saveCoupon(orderPercentageCouponDto);

        // 고정값 2000
        OrderDto orderDto7 = getOrderDto(orderFixedCoupon.getCouponId(), true, manyItemFixedItemList);
        int orderPrice7 = orderService.getOrderPrice(orderDto7);
        assertThat(orderPrice7).isEqualTo(20000);

        // 고정값 2000
        OrderDto orderDto8 = getOrderDto(orderPercentageCoupon.getCouponId(), true, manyItemFixedItemList);
        int orderPrice8 = orderService.getOrderPrice(orderDto8);
        assertThat(orderPrice8).isEqualTo(22500);
    }

    private OrderDto getOrderDto(Long couponId, boolean useCoupon, List<OrderItemDto> orderItemDtoList1) {
        OrderDto orderDto1 = OrderDto.builder()
                .couponId(couponId)
                .useCoupon(useCoupon)
                .deliveryPrice(2000)
                .items(orderItemDtoList1)
                .build();
        return orderDto1;
    }

    private ItemCreateDto getItemCreateDto(String name, int price, Role role, LocalDateTime createDate) {
        ItemCreateDto itemCreateDto =
                ItemCreateDto.builder()
                        .name(name)
                        .price(price)
                        .role(role)
                        .date(createDate)
                        .build();
        return itemCreateDto;
    }
}
