package com.example.myShop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainItemDto {

    private Long id;
    private String itemName;
    private String itemDetail;
    private String imgUrl;
    private Integer price;

    @QueryProjection // QueryDSL로 결과 조회 시 MainItemDto 객체로 받기 위함
    public MainItemDto(Long id, String itemName, String itemDetail, String imgUrl, Integer price) {
        this.id = id;
        this.itemName = itemName;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
    }
}

