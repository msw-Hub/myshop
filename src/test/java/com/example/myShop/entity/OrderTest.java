package com.example.myShop.entity;

import com.example.myShop.constant.ItemSellStatus;
import com.example.myShop.repository.ItemRepository;
import com.example.myShop.repository.MemberRepository;
import com.example.myShop.repository.OrderItemRepository;
import com.example.myShop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
@Transactional
class OrderTest {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    public Item createItem() {
        Item item = new Item();
        item.setItemName("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }
    public Order createOrder() {
        Order order = new Order();
        for (int i = 0; i < 3; i++) {
            Item item = createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }
        Member member = new Member();
        memberRepository.save(member);
        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest() {
        Order order = new Order();
        for(int i=0;i<3;i++){
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }
        orderRepository.saveAndFlush(order); //order 엔티티를 저장하면서 flush 호출하여 영속성 컨텍스트에 있는 객체들을 디비에 반영
        em.clear(); // 영속성컨텍스트 상태 초기화
        Order savedOrder = orderRepository.findById(order.getId()) // 디비에서 주문엔티티 조회.
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size()); // 저장된 3개의 itemorder 확인
    }

    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order = this.createOrder();
        order.getOrderItems().remove(0); // orderitem리스트의 0번째 인덱스 요소 제거
        em.flush();
    }

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest() {
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush(); // 영속성 컨텍스트에 있는 엔티티를 디비에 반영
        em.clear(); // 영속성 컨텍스트 초기화
        OrderItem orderItem = orderItemRepository.findById(orderItemId) // orderitem 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        System.out.println("orderItem.getOrder().getId() = " + orderItem.getOrder().getId());
    }

}