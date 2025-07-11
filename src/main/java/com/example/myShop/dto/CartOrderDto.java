package com.example.myShop.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartOrderDto {
    private Long cartItemId;
    private List<CartOrderDto> cartOrderDtoList;
}
