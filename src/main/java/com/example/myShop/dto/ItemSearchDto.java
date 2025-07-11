package com.example.myShop.dto;

import com.example.myShop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {

    // 현재 시간과 상품 등록일을 비교해서 상품 데이터를 조회
    private String searchDateType;

    // 판매 상태 기준으로 조회
    private ItemSellStatus searchSellStatus;

    // 상품을 어떤 유형으로 조회할지 선택 (상품명, 등록자 아이디 등)
    private String searchBy;

    // 조회할 검색어 저장할 변수 (기본값: 빈 문자열)
    private String searchQuery = "";

    /*
     * all: 상품 등록일 전체1
     * d: 최근 하루 동안 등록된 상품
     * 1w: 최근일주일 동안 등록된 상품
     * 1m: 최근 한달 동안 등록된 상품
     * 6m: 최근 6개월 동안 등록된 상품
     */
}

