package com.wemake.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.dto.ItemDto;
import com.wemake.market.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;

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
}