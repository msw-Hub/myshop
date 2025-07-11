package com.example.myShop.kakaopay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoPayCancelRequestDto {
    private String tid;
    private int cancelAmount;
}
