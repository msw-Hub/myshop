package com.example.myShop.repository;

import com.example.myShop.dto.ItemSearchDto;
import com.example.myShop.dto.MainItemDto;
import com.example.myShop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    /**
     * 관리자용 상품 조회 (페이징 포함)
     *
     * @param itemSearchDto 검색 조건 DTO
     * @param pageable      페이징 정보
     * @return 상품 페이지 결과
     */
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
