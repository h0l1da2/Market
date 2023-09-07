package com.wemake.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemake.market.domain.How;
import com.wemake.market.domain.Item;
import com.wemake.market.domain.Where;
import com.wemake.market.domain.dto.CouponSaveDto;
import com.wemake.market.repository.CouponRepository;
import com.wemake.market.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static java.time.LocalDateTime.now;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CouponControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private ItemRepository itemRepository;
    @BeforeEach
    void clean() {
        couponRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("쿠폰 추가 성공 : 아이템 / 고정")
    void 쿠폰추가_성공_아이템_고정() throws Exception {

        Item item = Item.builder()
                .name("아이템")
                .date(now())
                .build();

        Item saveItem = itemRepository.save(item);

        CouponSaveDto couponSaveDto = CouponSaveDto.builder()
                .itemId(saveItem.getId())
                .how(How.FIXED)
                .wheres(Where.ITEM)
                .amount(1000)
                .build();

        mockMvc.perform(
                        post("/coupon")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(couponSaveDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("itemId").value(couponSaveDto.getItemId()))
                .andExpect(jsonPath("amount").value(couponSaveDto.getAmount()))
                .andExpect(jsonPath("couponId").isNotEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("쿠폰 추가 성공 : 아이템 / 비율")
    void 쿠폰추가_성공_아이템_비율() throws Exception {

        Item item = Item.builder()
                .name("아이템")
                .date(now())
                .build();

        Item saveItem = itemRepository.save(item);

        CouponSaveDto couponSaveDto = CouponSaveDto.builder()
                .itemId(saveItem.getId())
                .how(How.PERCENTAGE)
                .wheres(Where.ITEM)
                .rate(10)
                .build();

        mockMvc.perform(
                        post("/coupon")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(couponSaveDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("itemId").value(couponSaveDto.getItemId()))
                .andExpect(jsonPath("rate").value(couponSaveDto.getRate()))
                .andExpect(jsonPath("couponId").isNotEmpty())
                .andDo(print());
    }
    @Test
    @DisplayName("쿠폰 추가 성공 : 주문 / 비율")
    void 쿠폰추가_성공_주문_비율() throws Exception {

        CouponSaveDto couponSaveDto = CouponSaveDto.builder()
                .how(How.PERCENTAGE)
                .wheres(Where.ORDER)
                .rate(10)
                .build();

        mockMvc.perform(
                        post("/coupon")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(couponSaveDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("itemId").isEmpty())
                .andExpect(jsonPath("rate").value(couponSaveDto.getRate()))
                .andExpect(jsonPath("couponId").isNotEmpty())
                .andDo(print());
    }
    @Test
    @DisplayName("쿠폰 추가 성공 : 주문 / 비율")
    void 쿠폰추가_성공_주문_고정() throws Exception {

        CouponSaveDto couponSaveDto = CouponSaveDto.builder()
                .how(How.FIXED)
                .wheres(Where.ORDER)
                .amount(1000)
                .build();

        mockMvc.perform(
                        post("/coupon")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(couponSaveDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("itemId").isEmpty())
                .andExpect(jsonPath("amount").value(couponSaveDto.getAmount()))
                .andExpect(jsonPath("couponId").isNotEmpty())
                .andDo(print());
    }

}
