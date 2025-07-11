package com.example.myShop.service;

import com.example.myShop.dto.ItemFormDto;
import com.example.myShop.dto.ItemImgDto;
import com.example.myShop.dto.ItemSearchDto;
import com.example.myShop.dto.MainItemDto;
import com.example.myShop.entity.Item;
import com.example.myShop.entity.ItemImg;
import com.example.myShop.repository.ItemImgRepository;
import com.example.myShop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 상품 등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        // 이미지 등록
        for (int i = 0; i < itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if (i == 0) {
                itemImg.setRepimgYn("Y"); // 첫 번째 이미지를 대표 이미지로 설정
            } else {
                itemImg.setRepimgYn("N");
            }

            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        return item.getId();
    }

    @Transactional(readOnly = true) // 읽기 전용 트랜잭션: 더티 체킹 생략 → 성능 향상
    public ItemFormDto getItemDtl(Long itemId) {

        // 1. 해당 상품의 이미지 목록 조회
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        // 2. 이미지 DTO 리스트 생성
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        // 3. 상품 엔티티 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        // 4. 상품 DTO 생성 및 이미지 DTO 주입
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);

        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())
                // 전달받은 상품아이디로 상품 조회
                .orElseThrow(EntityNotFoundException::new);

        // 상품 엔티티 필드 업데이트
        item.updateItem(itemFormDto);

        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        // 이미지 수정 처리
        for (int i = 0; i < itemImgFileList.size(); i++) {
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
            // 상품 이미지 업데이트를 위해 이미지 id와 파일 정보 전달
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }


}
