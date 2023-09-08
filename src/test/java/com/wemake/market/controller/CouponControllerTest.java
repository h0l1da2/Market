package com.wemake.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemake.market.domain.How;
import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.Where;
import com.wemake.market.domain.dto.CouponSaveDto;
import com.wemake.market.repository.CouponRepository;
import com.wemake.market.repository.ItemPriceHistoryRepository;
import com.wemake.market.repository.ItemRepository;
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

import static java.time.LocalDateTime.now;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@PropertySource("classpath:application.yml")
public class CouponControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemPriceHistoryRepository itemPriceHistoryRepository;
    @Value("${market.password}")
    private String password;


    @BeforeEach
    void clean() {
        couponRepository.deleteAll();
        itemPriceHistoryRepository.deleteAll();
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
                .role(Role.MARKET)
                .password(password)
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
                .role(Role.MARKET)
                .password(password)
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
                .role(Role.MARKET)
                .password(password)
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
                .role(Role.MARKET)
                .password(password)
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

    @Test
    @DisplayName("쿠폰 추가 실패 : 비율 / 고정값")
    void 쿠폰추가_실패_비율_고정값() throws Exception {

        CouponSaveDto couponSaveDto = CouponSaveDto.builder()
                .how(How.PERCENTAGE)
                .wheres(Where.ORDER)
                .amount(1000)
                .role(Role.MARKET)
                .password(password)
                .build();

        mockMvc.perform(
                        post("/coupon")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(couponSaveDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());

    }

    @Test
    @DisplayName("쿠폰 추가 실패 : 고정 / 비율값")
    void 쿠폰추가_실패_고정_비율값() throws Exception {

        CouponSaveDto couponSaveDto = CouponSaveDto.builder()
                .how(How.FIXED)
                .wheres(Where.ORDER)
                .rate(10)
                .build();

        mockMvc.perform(
                        post("/coupon")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(couponSaveDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());

    }
    @Test
    @DisplayName("쿠폰 추가 실패 : 값이 없음")
    void 쿠폰추가_실패_값이없음() throws Exception {

        CouponSaveDto couponSaveDto = CouponSaveDto.builder()
                .how(How.FIXED)
                .wheres(Where.ORDER)
                .role(Role.MARKET)
                .password(password)
                .build();

        mockMvc.perform(
                        post("/coupon")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(couponSaveDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());

    }
    @Test
    @DisplayName("쿠폰 추가 실패 : 유저권한")
    void 쿠폰추가_실패_유저권한() throws Exception {

        CouponSaveDto couponSaveDto = CouponSaveDto.builder()
                .how(How.FIXED)
                .wheres(Where.ORDER)
                .role(Role.USER)
                .password(password)
                .build();

        mockMvc.perform(
                        post("/coupon")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(couponSaveDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());

    }
    @Test
    @DisplayName("쿠폰 추가 실패 : 패스워드 오류")
    void 쿠폰추가_실패_비밀번호오류() throws Exception {

        CouponSaveDto couponSaveDto = CouponSaveDto.builder()
                .how(How.FIXED)
                .wheres(Where.ORDER)
                .role(Role.MARKET)
                .password("password")
                .build();

        mockMvc.perform(
                        post("/coupon")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(couponSaveDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());

    }
    @Test
    @DisplayName("쿠폰 추가 실패 : 둘다 오류")
    void 쿠폰추가_실패_둘다오류() throws Exception {

        CouponSaveDto couponSaveDto = CouponSaveDto.builder()
                .how(How.FIXED)
                .wheres(Where.ORDER)
                .role(Role.USER)
                .password("password")
                .build();

        mockMvc.perform(
                        post("/coupon")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(couponSaveDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());

    }

}
