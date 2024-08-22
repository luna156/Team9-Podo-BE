package com.softeer.podo.log.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class Info {
    private Map<String, String> headers;
    private byte[] body;
}