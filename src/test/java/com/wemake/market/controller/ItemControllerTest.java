package com.wemake.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.dto.ItemDto;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);

        mockMvc.perform(
                        post("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.data").value("OK"))
                .andExpect(jsonPath("$.item").hasJsonPath())
                .andDo(print());

    }

    @Test
    @DisplayName("아이템 추가 : 실패 -> 유저 요청")
    void create_실패_유저요청() throws Exception {
        ItemDto itemDto = new ItemDto("name", 1000, Role.USER);

        mockMvc.perform(
                        post("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemDto))
                ).andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.data").value("NOT_AUTH"))
                .andDo(print());

    }

    @Test
    @DisplayName("아이템 추가 : 실패 -> 중복 아이템")
    void create_실패_중복_아이템() throws Exception {
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemDto, false));

        mockMvc.perform(
                        post("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemDto))
                ).andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.data").value("ITEM_DUPL"))
                .andDo(print());

    }

    @Test
    @DisplayName("아이템 수정 : 성공 !")
    void 아이템수정_성공() throws Exception {
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemDto, false));

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("name", 2000, Role.MARKET, password);

        mockMvc.perform(
                        put("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemUpdateDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.data").value("OK"))
                .andExpect(jsonPath("$.item").hasJsonPath())
                .andDo(print());

    }

    @Test
    @DisplayName("아이템 수정 실패 : 유저 요청")
    void 아이템수정_실패_권한없음_유저요청() throws Exception {
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemDto, false));

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("name", 2000, Role.USER, password);

        mockMvc.perform(
                        put("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemUpdateDto))
                ).andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.data").value("AUTH_ERR"))
                .andDo(print());

    }
    @Test
    @DisplayName("아이템 수정 실패 : 비밀번호 틀림")
    void 아이템수정_실패_권한없음_비밀번호틀림() throws Exception {
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemDto, false));

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("name", 2000, Role.MARKET, "패스워둥");

        mockMvc.perform(
                        put("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemUpdateDto))
                ).andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.data").value("AUTH_ERR"))
                .andDo(print());

    }
    @Test
    @DisplayName("아이템 수정 실패 : 둘 다 문제")
    void 아이템수정_실패_권한없음_둘다이상함() throws Exception {
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemDto, false));

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("name", 2000, Role.USER, "패스워둥");

        mockMvc.perform(
                        put("/item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(itemUpdateDto))
                ).andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.data").value("AUTH_ERR"))
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
                .andExpect(jsonPath("$.data").value("AUTH_ERR"))
                .andDo(print());

    }

}