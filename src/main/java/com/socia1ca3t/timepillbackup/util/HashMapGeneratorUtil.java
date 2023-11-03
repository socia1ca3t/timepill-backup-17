package com.socia1ca3t.timepillbackup.util;

import java.util.HashMap;

public class HashMapGeneratorUtil {

    public static HashMap<String, String> generate(String... keyValuePairs) {

        HashMap<String, String> hashMap = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {

            String key = keyValuePairs[i];
            String value = i + 1 < keyValuePairs.length ? keyValuePairs[i + 1] : "";
            hashMap.put(key, value);
        }
        return hashMap;
    }
}
