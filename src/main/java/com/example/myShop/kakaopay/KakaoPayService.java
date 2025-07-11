package com.example.myShop.kakaopay;

import com.example.myShop.entity.*;
import com.example.myShop.kakaopay.dto.KakaoPayApproveResponseDto;
import com.example.myShop.kakaopay.dto.KakaoPayReadyResponseDto;
import com.example.myShop.repository.OrderRepository;
import com.example.myShop.slack.SlackNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KakaoPayService {

    private final OrderRepository orderRepository;
    private final KakaoPayApiClient kakaoPayApiClient;
    private final SlackNotifier slackNotifier;

    @Transactional
    public KakaoPayReadyResponseDto readyPayment(Long orderId, String userEmail) throws AccessDeniedException {
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문이 존재하지 않음"));

        if (!order.getMember().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("해당 주문에 대한 접근 권한이 없습니다.");
        }

        System.out.println("결제 총액: " + order.getTotalPrice());  // 0이 아니어야 함

        order.setPaymentStatus(PaymentStatus.READY);

        KakaoPayReadyResponseDto response = kakaoPayApiClient.ready(order);
        order.setKakaoTid(response.getTid());

        return response;
    }

    @Transactional
    public KakaoPayApproveResponseDto approvePayment(Long orderId, String userEmail, String pgToken) throws AccessDeniedException {
        log.info("approvePayment called with orderId={}, userEmail={}, pgToken={}", orderId, userEmail, pgToken);

        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문이 존재하지 않음"));

        if (!order.getMember().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("해당 주문에 대한 접근 권한이 없습니다.");
        }

        try {
            KakaoPayApproveResponseDto response = kakaoPayApiClient.approve(order, pgToken);

            if (response != null && response.getTid() != null) {
                // 성공 처리
                order.setPaymentStatus(PaymentStatus.APPROVED);
                order.setPaymentDate(LocalDateTime.now());
                order.setKakaoTid(response.getTid());
                orderRepository.save(order);

                slackNotifier.sendMessage(
                        String.format(":white_check_mark: 결제 승인 완료\n주문ID: %d\n금액: %,d원\n사용자: %s",
                                order.getId(), order.getTotalPrice(), order.getMember().getEmail())
                );

                log.info("Order payment status updated to APPROVED for order id: {}", order.getId());
            } else {
                // 실패 처리 (TID 없을 경우)
                order.setPaymentStatus(PaymentStatus.FAILED);
                orderRepository.save(order);
                log.warn("결제 승인 실패: 응답에 TID 없음. orderId={}", order.getId());
                throw new RuntimeException("결제 승인 실패: 유효하지 않은 응답");
            }

            return response;

        } catch (Exception e) {
            // 승인 API 호출 중 예외 발생 시
            order.setPaymentStatus(PaymentStatus.FAILED);
            orderRepository.save(order);
            log.error("결제 승인 중 예외 발생 - orderId={}, error={}", order.getId(), e.getMessage(), e);
            slackNotifier.sendMessage(
                    String.format(":x: 결제 승인 실패\n주문ID: %d\n에러: %s", order.getId(), e.getMessage())
            );
            throw new RuntimeException("결제 승인 중 오류가 발생했습니다.");
        }
    }
}