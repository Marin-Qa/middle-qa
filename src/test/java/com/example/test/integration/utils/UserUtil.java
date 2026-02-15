package com.example.test.integration.utils;

import java.util.Map;

public class UserUtil {

    public int getIdUserFromData(Map<String, Object> user){
        return ((Number) user.get("id")).intValue();
    }
}
