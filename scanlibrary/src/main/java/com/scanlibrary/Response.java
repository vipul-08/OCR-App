package com.scanlibrary;

import com.google.gson.JsonObject;

import org.json.JSONObject;

public class Response {

    private boolean status;
    private String name;
    private JsonObject fields;

    public boolean getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public JsonObject getFields() {
        return fields;
    }
}