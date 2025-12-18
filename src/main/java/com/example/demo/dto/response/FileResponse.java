package com.example.demo.dto.response;

import org.springframework.http.HttpHeaders;

public record FileResponse(
        byte[] data,
        HttpHeaders headers
) {

}
