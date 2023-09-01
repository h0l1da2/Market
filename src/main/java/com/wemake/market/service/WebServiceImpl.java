package com.wemake.market.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebServiceImpl implements WebService {

    @Override
    public String objToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

}
