package com.example.myShop.kakaopay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoPayApproveResponseDto {

    private String aid;                 // 요청 고유 번호
    private String tid;                 // 결제 고유 번호
    private String cid;                 // 가맹점 코드
    private String status;              // 결제 상태 (예: APPROVED)

    @JsonProperty("partner_order_id")
    private String partnerOrderId;     // 가맹점 주문 번호

    @JsonProperty("partner_user_id")
    private String partnerUserId;      // 가맹점 회원 ID

    @JsonProperty("payment_method_type")
    private String paymentMethodType;  // 결제 수단 종류 (예: CARD)

    private Amount amount;             // 결제 금액 정보
    private CardInfo card_info;        // 카드 결제 정보 (카드 결제인 경우)

    @JsonProperty("created_at")
    private String createdAt;          // 결제 요청 시간

    @JsonProperty("approved_at")
    private String approvedAt;         // 결제 승인 시간

    @Data
    public static class Amount {
        private int total;             // 전체 결제 금액
        private int tax_free;          // 비과세 금액
        private int vat;               // 부가세 금액
        private int point;             // 사용한 포인트 금액
        private int discount;          // 할인 금액
    }

    @Data
    public static class CardInfo {
        private String purchase_corp;          // 매입 카드사
        private String purchase_corp_code;     // 매입 카드사 코드
        private String issuer_corp;            // 카드 발급사
        private String issuer_corp_code;       // 카드 발급사 코드
        private String kakaopay_purchase_corp;      // 카카오페이 매입사
        private String kakaopay_purchase_corp_code; // 카카오페이 매입사 코드
        private String kakaopay_issuer_corp;         // 카카오페이 발급사
        private String kakaopay_issuer_corp_code;    // 카카오페이 발급사 코드
        private String bin;                   // 카드 BIN
        private String card_type;             // 카드 종류 (예: 신용카드)
        private String install_month;         // 할부 개월 수
        private String approved_id;           // 승인번호
        private String card_mid;              // 카드 가맹점 번호
        private String interest_free_install; // 무이자 할부 여부
        private String card_item_code;        // 카드 상품 코드
    }
}
