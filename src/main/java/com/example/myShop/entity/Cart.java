package com.example.myShop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "t_cart")
@Getter
@Setter
@ToString
public class Cart {
    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // 회원 엔티티와 일대일로 매핑
    @JoinColumn(name="member_id") // 매핑할 외래키 지정. 지정하지 않으면 JPA가 알아서 ID를 찾지만, 컬럼명이 원하는 대로 생성되지 않을 수 있음.
    private Member member;

    public static Cart createCart(Member member) {
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }
}
