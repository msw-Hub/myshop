package com.example.myShop.entity;

import com.example.myShop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_order")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 한 명의 회원은 여러 번 주문을 할 수 있음. 주문 엔티티 기준 다대일 단방향 매핑

    private LocalDateTime orderDate; //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문상태

    /*
    카카오페이 결제를 위한 확장
     */
    @Column(name = "kakao_tid")
    private String kakaoTid;  // 카카오페이 결제 준비 단계에서 발급받는 TID

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; // 결제 상태

    private LocalDateTime paymentDate; // 결제 완료 시간



    // 주문 상품 엔티티와 일대다 매핑. order_id가 order_item 테이블에 있으므로 연관 관계의 주인은 OrderItem 엔티티
    // Order 엔티티가 주인이 아니므로 mappedBy 속성으로 연관관계의 주인을 설정. orderitem의 order에 의해 관리된다는 의미로 해석
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch =  FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime regTime;
    private LocalDateTime updateTime;

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this); // 양방향 연관관계 설정
    }
    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member);
        order.setOrderStatus(OrderStatus.ORDER);
        order.setPaymentStatus(PaymentStatus.READY);  // 또는 별도 초기 상태 << 카카오때매
        for(OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }


}