package com.example.myShop.kakaopay.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoPayCancelResponseDto {
    private String aid;
    private String tid;
    private String cid;
    private String status; // 취소 결과 상태
    private String canceledAt;
    private int canceledAmount;

}
