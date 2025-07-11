package com.example.myShop.kakaopay;

import com.example.myShop.entity.Order;
import com.example.myShop.entity.PaymentStatus;
import com.example.myShop.kakaopay.dto.KakaoPayApproveResponseDto;
import com.example.myShop.kakaopay.dto.KakaoPayCancelRequestDto;
import com.example.myShop.kakaopay.dto.KakaoPayCancelResponseDto;
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
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

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

                // Slack 메시지 전송 부분을 메서드로 분리하여 호출
                sendPaymentApprovedMessage(order);

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

    public void sendPaymentApprovedMessage(Order order) {
        String productList = order.getOrderItems().stream()
                .map(oi -> oi.getItem().getItemName() + " x" + oi.getCount())
                .collect(Collectors.joining(", "));

        String message = String.format(
                ":white_check_mark: 결제 승인 완료\n" +
                        "주문번호: %d\n" +
                        "결제금액: %,d원\n" +
                        "결제일시: %s\n" +
                        "결제수단: 카카오페이\n" +
                        "결제번호(TID): %s\n" +
                        "구매자: %s\n" +
                        "주문상품: %s\n"+
                        "*저희 team2 몰디브를 이용해주셔서 감사합니다.*",
                order.getId(),
                order.getTotalPrice(),
                order.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                order.getKakaoTid(),
                order.getMember().getEmail(),
                productList
        );

        slackNotifier.sendMessage(message);
    }

    @Transactional
    public KakaoPayCancelResponseDto cancelPayment(Order order) {
        try {
            KakaoPayCancelResponseDto response = kakaoPayApiClient.requestCancelPayment(
                    new KakaoPayCancelRequestDto(order.getKakaoTid(), order.getTotalPrice())
            );

            String message = String.format(
                    ":white_check_mark: 결제 취소 완료\n" +
                            "주문번호: %d\n" +
                            "결제금액: %,d원\n" +
                            "취소일시: %s\n" +
                            "결제수단: 카카오페이\n" +
                            "결제번호(TID): %s\n" +
                            "구매자: %s\n"+
                            "*저희 team2 몰디브를 이용해주셔서 감사합니다.*",
                    order.getId(),
                    order.getTotalPrice(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    order.getKakaoTid(),
                    order.getMember().getEmail()
            );

            slackNotifier.sendMessage(message);

            return response;

        } catch (Exception e) {
            String errorMessage = String.format(
                    ":x: 결제 취소 실패\n" +
                            "주문번호: %d\n" +
                            "에러: %s",
                    order.getId(),
                    e.getMessage()
            );

            slackNotifier.sendMessage(errorMessage);

            throw new RuntimeException("결제 취소 중 오류 발생", e);
        }
    }


}