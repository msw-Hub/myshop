package com.example.myShop.kakaopay;

import com.example.myShop.kakaopay.dto.KakaoPayApproveResponseDto;
import com.example.myShop.kakaopay.dto.KakaoPayReadyResponseDto;
import com.example.myShop.kakaopay.dto.OrderRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;

    @PostMapping("/kakaoPayReady")
    @ResponseBody
    public ResponseEntity<?> kakaoPayReady(@RequestBody OrderRequestDto requestDto,
                                           Principal principal) {
        String userEmail = principal.getName();
        try {
            KakaoPayReadyResponseDto responseDto = kakaoPayService.readyPayment(requestDto.getOrderId(), userEmail);
            return ResponseEntity.ok(responseDto);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
    }

    @GetMapping("/kakaoPaySuccess")
    public String kakaoPaySuccess(@RequestParam("pg_token") String pgToken,
                                  @RequestParam("orderId") Long orderId,
                                  Principal principal,
                                  Model model) {
        String userEmail = principal.getName();

        try {
            KakaoPayApproveResponseDto response = kakaoPayService.approvePayment(orderId, userEmail, pgToken);
            model.addAttribute("approveResponse", response);

            return "kakaopay/paymentSuccess";  // 결제 성공 화면 (Thymeleaf 등)
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "kakaopay/paymentFail";  // 결제 실패 화면
        }
    }

    @GetMapping("/kakaoPayCancel")
    public String kakaoPayCancel() {
        // 결제 취소 시 메인 페이지로 이동
        return "redirect:/";
    }

    @GetMapping("/kakaoPayFail")
    public String kakaoPayFail() {
        // 결제 실패 시 메인 페이지로 이동
        return "redirect:/";
    }
}

