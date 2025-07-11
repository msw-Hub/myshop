package com.example.myShop.entity;

import com.example.myShop.constant.ItemSellStatus;
import com.example.myShop.dto.ItemFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id; // 상품코드

    @Column(nullable = false, length = 50)
    private String itemName; // 상품명

    @Column(name = "price", nullable = false)
    private int price; // 가격

    @Column(nullable = false)
    private int stockNumber; // 재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail; // 상품상세설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; // 상품판매상태

    private LocalDateTime regTime;   // 등록시간
    private LocalDateTime updateTime; // 수정시간

    public void updateItem(ItemFormDto itemFormDto) {
        this.itemName = itemFormDto.getItemName();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    public void removeStock(int stockNumber) {
        int restStock = this.stockNumber - stockNumber;
        if (restStock < 0) {
            throw new IllegalArgumentException("상품의 재고가 부족합니다. ( 현재 재고: " + this.stockNumber + ", 요청 수량: " + stockNumber + " )");
        }
        this.stockNumber = restStock;
    }

}
