package com.example.ttcs_jobwebsite.config;

import io.github.cdimascio.dotenv.Dotenv;

public class envConfig {
    private static final Dotenv dotenv = Dotenv.load();

    public static String get(String key) {
        return dotenv.get(key);
    }
}
