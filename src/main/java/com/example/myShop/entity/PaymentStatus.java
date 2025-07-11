package com.example.myShop.entity;

public enum PaymentStatus {
    READY,      // 결제 준비 완료
    APPROVED,   // 결제 승인 완료
    CANCELLED,  // 결제 취소됨
    FAILED      // 결제 실패
}
