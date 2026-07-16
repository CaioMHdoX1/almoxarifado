package com.almoxaf.api.util;

import com.fasterxml.jackson.databind.json.JsonMapper;

public class JsonMapperFactory {
    public static JsonMapper create() {
        return JsonMapper.builder().build();
    }
}