package com.socia1ca3t.timepillbackup.util;

import jakarta.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.Map;

public class HashMapGeneratorUtil {

    public static @NotNull HashMap<String, String> generate(String... keyValuePairs) {

        HashMap<String, String> hashMap = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {

            String key = keyValuePairs[i];
            String value = i + 1 < keyValuePairs.length ? keyValuePairs[i + 1] : "";
            hashMap.put(key, value);
        }
        return hashMap;
    }



    public static @NotNull <K, V> Map<K, V> generate(K key, V value) {

        Map<K, V> hashMap = new HashMap<>();
        hashMap.put(key, value);
        return hashMap;
    }
}
