package com.example.myShop.kakaopay.dto;

import lombok.Data;

@Data
public class CreateOrderRequestDto {
    private Long itemId;
    private int count;
}
