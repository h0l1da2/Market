package com.wemake.market.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wemake.market.config.LocalDateTimeTypeAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class WebServiceImpl implements WebService {

    @Override
    public String objToJson(Object object) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
        return gson.toJson(object);
    }

}
