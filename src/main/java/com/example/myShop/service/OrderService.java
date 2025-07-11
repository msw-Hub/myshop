package com.example.myShop.service;

import com.example.myShop.dto.OrderDto;
import com.example.myShop.dto.OrderHistoryDto;
import com.example.myShop.dto.OrderItemDto;
import com.example.myShop.entity.*;
import com.example.myShop.kakaopay.dto.CreateOrderRequestDto;
import com.example.myShop.kakaopay.dto.OrderRequestDto;
import com.example.myShop.repository.ItemImgRepository;
import com.example.myShop.repository.ItemRepository;
import com.example.myShop.repository.MemberRepository;
import com.example.myShop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ItemImgRepository itemImgRepository;

//    public Long order(OrderDto orderDto, String email) {
//        // 1. 상품 조회
//        Item item = itemRepository.findById(orderDto.getItemId())
//                .orElseThrow(EntityNotFoundException::new);
//
//        // 2. 회원 조회
//        Member member = memberRepository.findByEmail(email);
//
//        // 3. 주문 상품 생성
//        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
//        List<OrderItem> orderItemList = Collections.singletonList(orderItem);
//
//        // 4. 주문 생성 및 저장
//        Order order = Order.createOrder(member, orderItemList);
//        orderRepository.save(order);
//
//        return order.getId();
//    }

    public Long order(CreateOrderRequestDto requestDto, String email) {
        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);

        OrderItem orderItem = OrderItem.createOrderItem(item, requestDto.getCount());
        Order order = Order.createOrder(member, Collections.singletonList(orderItem));

        orderRepository.save(order);
        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistoryDto> getOrderList(String email, Pageable pageable) {
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistoryDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistoryDto orderHistDto = new OrderHistoryDto(order);
            List<OrderItem> orderItems = order.getOrderItems();

            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(
                        orderItem.getItem().getId(), "Y"
                );

                OrderItemDto orderItemDto = new OrderItemDto(
                        orderItem,
                        itemImg.getImgUrl()
                );

                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto);
        }

        return new PageImpl<>(orderHistDtos, pageable, totalCount);
    }



}
