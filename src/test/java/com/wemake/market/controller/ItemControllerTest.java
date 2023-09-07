package com.wemake.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.dto.ItemDeleteDto;
import com.wemake.market.domain.dto.ItemCreateDto;
import com.wemake.market.domain.dto.ItemUpdateDto;
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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@PropertySource("classpath:application.yml")
class ItemControllerTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Value("${market.password}")
    private String password;

    @BeforeEach
    void clean() {
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("아이템 추가 : 성공 !")
    void create() throws Exception {
        ItemCreateDto itemCreateDto =
                ItemCreateDto.builder()
                        .name("초코송이")
                        .price(3000)
                        .role(Role.MARKET)
                        .build();

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
    @DisplayName("아이템 추가 : 실패 -> 유저 요청")
    void create_실패_유저요청() throws Exception {
        ItemCreateDto itemCreateDto =
                ItemCreateDto.builder()
                        .name("사과")
                        .price(1000)
                        .role(Role.USER)
                        .build();

        mockMvc.perform(
                        post("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemCreateDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());

    }

    @Test
    @DisplayName("아이템 추가 : 실패 -> 중복 아이템")
    void create_실패_중복_아이템() throws Exception {
        ItemCreateDto itemCreateDto = saveItem();

        mockMvc.perform(
                        post("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemCreateDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());

    }

    @Test
    @DisplayName("아이템 수정 : 성공 !")
    void 아이템수정_성공() throws Exception {
        ItemCreateDto itemCreateDto = saveItem();

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name(itemCreateDto.getName())
                .price(2000)
                .role(Role.MARKET)
                .password(password)
                .build();

        mockMvc.perform(
                        put("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemUpdateDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("price").value(2000))
                .andDo(print());

    }

    @Test
    @DisplayName("아이템 수정 실패 : 유저 요청")
    void 아이템수정_실패_권한없음_유저요청() throws Exception {
        saveItem();

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("칸쵸")
                .price(2000)
                .role(Role.USER)
                .password(password)
                .build();
        mockMvc.perform(
                        put("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemUpdateDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());

    }
    @Test
    @DisplayName("아이템 수정 실패 : 비밀번호 틀림")
    void 아이템수정_실패_권한없음_비밀번호틀림() throws Exception {
        saveItem();

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("도로롱")
                .price(2000)
                .role(Role.MARKET)
                .password("메롱")
                .build();

        mockMvc.perform(
                        put("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemUpdateDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());

    }
    @Test
    @DisplayName("아이템 수정 실패 : 둘 다 문제")
    void 아이템수정_실패_권한없음_둘다이상함() throws Exception {
        saveItem();

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("문제")
                .price(2000)
                .role(Role.USER)
                .password("문제")
                .build();

        mockMvc.perform(
                        put("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemUpdateDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());

    }

    @Test
    @DisplayName("아이템 수정 실패 : 없는 아이템")
    void 아이템수정_실패_아이템없음() throws Exception {

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("샴푸")
                .price(2000)
                .role(Role.MARKET)
                .password(password)
                .build();

        mockMvc.perform(
                        put("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemUpdateDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());

    }

    @Test
    @DisplayName("아이템 삭제 성공 : 한 개")
    void 아이템삭제_성공_한개() throws Exception {
        ItemCreateDto itemCreateDto = saveItem();

        ItemDeleteDto itemDeleteDto = ItemDeleteDto.builder()
                .name(itemCreateDto.getName())
                .role(itemCreateDto.getRole())
                .password(password)
                .build();

        mockMvc.perform(
                        delete("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemDeleteDto))
                ).andExpect(status().is2xxSuccessful())
                .andDo(print());

    }
    @Test
    @DisplayName("아이템 삭제 성공 : 여러 개")
    void 아이템삭제_성공_여러개() throws Exception {
        ItemCreateDto itemCreateDto = saveItem();
        itemRepository.save(new Item(itemCreateDto));
        itemRepository.save(new Item(itemCreateDto));

        ItemDeleteDto itemDeleteDto = ItemDeleteDto.builder()
                .name(itemCreateDto.getName())
                .role(itemCreateDto.getRole())
                .password(password)
                .build();

        mockMvc.perform(
                        delete("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemDeleteDto))
                ).andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    @DisplayName("아이템 삭제 실패 : 유저권한")
    void 아이템삭제_실패_권한없음_유저() throws Exception {
        ItemCreateDto itemCreateDto = saveItem();

        ItemDeleteDto itemDeleteDto = ItemDeleteDto.builder()
                .name(itemCreateDto.getName())
                .role(Role.USER)
                .password(password)
                .build();

        mockMvc.perform(
                        delete("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemDeleteDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());
    }
    @Test
    @DisplayName("아이템 삭제 실패 : 비밀번호틀림")
    void 아이템삭제_실패_권한없음_비밀번호() throws Exception {
        ItemCreateDto itemCreateDto = saveItem();

        ItemDeleteDto itemDeleteDto = ItemDeleteDto.builder()
                .name(itemCreateDto.getName())
                .role(itemCreateDto.getRole())
                .password("ㅋㅋ")
                .build();

        mockMvc.perform(
                        delete("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemDeleteDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @DisplayName("아이템 삭제 실패 : 둘다틀림")
    void 아이템삭제_실패_권한없음_둘다틀림() throws Exception {
        ItemCreateDto itemCreateDto = saveItem();

        ItemDeleteDto itemDeleteDto = ItemDeleteDto.builder()
                .name(itemCreateDto.getName())
                .role(Role.USER)
                .password("zz")
                .build();

        mockMvc.perform(
                        delete("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemDeleteDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @DisplayName("아이템 삭제 실패 : 없는 아이템")
    void 아이템삭제_실패_권한없음_없는아이템() throws Exception {

        ItemCreateDto itemCreateDto =
                ItemCreateDto.builder()
                        .name("빼빼로")
                        .price(3000)
                        .role(Role.MARKET)
                        .build();

        ItemDeleteDto itemDeleteDto = ItemDeleteDto.builder()
                .name(itemCreateDto.getName())
                .role(itemCreateDto.getRole())
                .password(password)
                .build();

        mockMvc.perform(
                        delete("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemDeleteDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @DisplayName("아이템 조회 성공 : 하나")
    void 아이템조회_성공_한개() throws Exception {
        LocalDateTime createDate = LocalDateTime.of(
                2023, 1, 1, 12, 20, 0
        );

        Item item = itemRepository.save(new Item("사과", 2000, createDate));

        mockMvc.perform(
                        get("/item?name="+item.getName()+"&date=2023-03-05T12:00:00")
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("name").value(item.getName()))
                .andDo(print());
    }

    @Test
    @DisplayName("아이템 조회 성공 : 여러개")
    void 아이템조회_성공_여러개() throws Exception {
        ItemCreateDto itemCreateDto = saveItem();
        itemRepository.save(new Item(itemCreateDto));
        itemRepository.save(new Item(itemCreateDto));

        mockMvc.perform(
                        get("/item?name="+itemCreateDto.getName()+"&date=2023-03-05T12:00:00")
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("name").value(itemCreateDto.getName()))
                .andDo(print());
    }

    @Test
    @DisplayName("아이템 조회 성공 : 특정 시간")
    void 아이템조회_성공_특정시간() throws Exception {
        ItemCreateDto itemCreateDto = saveItem();
        Item item = itemRepository.save(new Item(itemCreateDto));
        itemRepository.save(item);

        mockMvc.perform(
                        get("/item?name="+item.getName()+"&date=2023-03-05T12:00:00")
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("name").value(item.getName()))
                .andDo(print());
    }

    @Test
    @DisplayName("아이템 조회 실패 : 없는 아이템")
    void 아이템조회_실패_없음() throws Exception {
        ItemCreateDto itemCreateDto =
                ItemCreateDto.builder()
                .name("딸기")
                .price(1000)
                .role(Role.MARKET)
                .build();

        mockMvc.perform(
                        get("/item?name="+itemCreateDto.getName()+"&date=2023-03-05T12:00:00")
                ).andExpect(status().is4xxClientError())
                .andDo(print());
    }



    private ItemCreateDto saveItem() {
        ItemCreateDto itemCreateDto =
                ItemCreateDto.builder()
                        .name("사과")
                        .price(1000)
                        .role(Role.MARKET)
                        .build();

        itemRepository.save(new Item("사과", 2000, LocalDateTime.of(2023,1,1,12,0,0)));

        return itemCreateDto;
    }

}