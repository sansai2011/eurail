package com.eurail.core.services;

import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;

public interface VideoLibraryService {
    JsonObject getVideoLibraryJsonObject(String keyword , String limit) throws UnsupportedEncodingException;
}
