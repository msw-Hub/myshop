package com.example.myShop.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderDto {
    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long itemId; // 상품 아이디

    @Min(value=1, message = "최소 1개 이상 주문해야 합니다.")
    @Max(value = 999, message = "최대 999개까지 주문할 수 있습니다.")
    private int count; // 주문 수량
}
