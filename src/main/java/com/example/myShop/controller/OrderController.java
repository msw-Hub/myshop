package com.example.myShop.controller;

import com.example.myShop.dto.OrderDto;
import com.example.myShop.dto.OrderHistoryDto;
import com.example.myShop.kakaopay.dto.CreateOrderRequestDto;
import com.example.myShop.kakaopay.dto.OrderRequestDto;
import com.example.myShop.repository.OrderRepository;
import com.example.myShop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private  final OrderService orderService;

//    @PostMapping(value = "/order")
//    public @ResponseBody ResponseEntity<?> order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult, Principal principal) {
//        if (bindingResult.hasErrors()) {
//            StringBuilder sb = new StringBuilder();
//            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
//
//            for (FieldError fieldError : fieldErrors) {
//                sb.append(fieldError.getDefaultMessage());
//            }
//            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
//        }
//        String email = principal.getName();
//
//        Long orderId;
//
//        try {
//            orderId = orderService.order(orderDto, email);
//        } catch (Exception e) {
//            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//
//        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
//    }

    @PostMapping("/order")
    public ResponseEntity<?> order(@RequestBody @Valid CreateOrderRequestDto requestDto,
                                   BindingResult bindingResult,
                                   Principal principal) {
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                sb.append(error.getDefaultMessage());
            }
            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();
        Long orderId;

        try {
            orderId = orderService.order(requestDto, email);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHistory(
            @PathVariable("page") Optional<Integer> page,
            Principal principal,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page.orElse(0), 4);

        Page<OrderHistoryDto> ordersHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        model.addAttribute("orders", ordersHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHistory";
    }

}
