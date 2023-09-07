package com.wemake.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.dto.ItemDeleteDto;
import com.wemake.market.domain.dto.ItemCreateDto;
import com.wemake.market.domain.dto.ItemSearchTimeDto;
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

import static java.time.LocalDateTime.now;
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
        ItemCreateDto itemCreateDto = new ItemCreateDto("초코송이", 1000, Role.MARKET);

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
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", 1000, Role.USER);

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

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(itemCreateDto.getName(), 2000, Role.MARKET, password);

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

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("name", 2000, Role.USER, password);

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

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("name", 2000, Role.MARKET, "패스워둥");

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

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("name", 2000, Role.USER, "패스워둥");

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

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("name", 2000, Role.MARKET, password);

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

        ItemDeleteDto itemDeleteDto = new ItemDeleteDto(itemCreateDto.getName(), itemCreateDto.getRole(), password);

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

        ItemDeleteDto itemDeleteDto = new ItemDeleteDto(itemCreateDto.getName(), itemCreateDto.getRole(), password);

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

        ItemDeleteDto itemDeleteDto = new ItemDeleteDto(itemCreateDto.getName(), Role.USER, password);

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

        ItemDeleteDto itemDeleteDto = new ItemDeleteDto(itemCreateDto.getName(), Role.MARKET, "다른패스워드");

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

        ItemDeleteDto itemDeleteDto = new ItemDeleteDto(itemCreateDto.getName(), Role.USER, "다른패스워드");

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
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", 1000, Role.MARKET);

        ItemDeleteDto itemDeleteDto = new ItemDeleteDto(itemCreateDto.getName(), itemCreateDto.getRole(), password);

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
        ItemCreateDto itemCreateDto = saveItem();

        ItemSearchTimeDto itemSearchTimeDto = new ItemSearchTimeDto(itemCreateDto.getName(), now());
        mockMvc.perform(
                        get("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemSearchTimeDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("name").value(itemSearchTimeDto.getName()))
                .andDo(print());
    }

    @Test
    @DisplayName("아이템 조회 성공 : 여러개")
    void 아이템조회_성공_여러개() throws Exception {
        ItemCreateDto itemCreateDto = saveItem();
        itemRepository.save(new Item(itemCreateDto));
        itemRepository.save(new Item(itemCreateDto));

        ItemSearchTimeDto itemSearchTimeDto = new ItemSearchTimeDto(itemCreateDto.getName(), now());
        mockMvc.perform(
                        get("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemSearchTimeDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("name").value(itemSearchTimeDto.getName()))
                .andDo(print());
    }

    @Test
    @DisplayName("아이템 조회 성공 : 특정 시간")
    void 아이템조회_성공_특정시간() throws Exception {
        LocalDateTime createDate = LocalDateTime.of(
                2023, 1, 1, 11, 11, 0, 0
        );
        ItemCreateDto itemCreateDto = saveItem();
        Item item = itemRepository.save(new Item(itemCreateDto));
        itemRepository.save(item);

        LocalDateTime searchDate = now();

        ItemSearchTimeDto itemSearchTimeDto = new ItemSearchTimeDto(item.getName(), searchDate);

        mockMvc.perform(
                        get("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemSearchTimeDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("name").value(itemSearchTimeDto.getName()))
                .andDo(print());
    }

    @Test
    @DisplayName("아이템 조회 실패 : 없는 아이템")
    void 아이템조회_실패_없음() throws Exception {
        ItemCreateDto itemCreateDto = new ItemCreateDto("딸기", 1000, Role.MARKET);

        ItemSearchTimeDto itemSearchTimeDto = new ItemSearchTimeDto(itemCreateDto.getName(), now());
        mockMvc.perform(
                        get("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemSearchTimeDto))
                ).andExpect(status().is4xxClientError())
                .andDo(print());
    }



    private ItemCreateDto saveItem() {
        ItemCreateDto itemCreateDto = new ItemCreateDto("사과", 1000, Role.MARKET);
        itemRepository.save(new Item(itemCreateDto));
        return itemCreateDto;
    }

}