package com.scanlibrary;

import com.google.gson.JsonObject;

import org.json.JSONObject;

public class Response {

    private String status;
    private JsonObject fields;

    public String getStatus() {
        return status;
    }

    public JsonObject getFields() {
        return fields;
    }
}