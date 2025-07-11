package com.example.myShop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="t_cartitem")
public class CartItem {
    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cart_id")
    private Cart cart; // 하나의 장바구니에 여러 개의 상품을 담을 수 있음

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // 장바구니에 담을 상품 정보를 알아야 해서 매핑. 하나의 상품은 여러 장바구니에 담길 수 있음

    private int count;
}