package com.example.myShop.kakaopay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class KakaoPayReadyResponseDto {
    private String tid;
    @JsonProperty("next_redirect_pc_url")
    private String nextRedirectPcUrl; // 결제창 URL
    private LocalDateTime createdAt;
}
