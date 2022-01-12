package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }
    public List<Item> findItems(){
        return itemRepository.findAll();
    }
    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }

    //변경 방지 방법
    @Transactional
    //이렇게 변경감지를 통해서 하는 방법이 더 나은 방법이다.
    public void updateItem(Long itemId, Book param){
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(param.getPrice());
        findItem.setName(param.getName());
        findItem.setStockQuantity(param.getStockQuantity());
        //영속 상태이므로 Repo.save(item)을 할 필요가 없다.
        //* set함수가 아닌 비즈니스적으로 의미있는 함수로 바꿔야 한다.
        //findItem.changeBookInfo 등
    }
    //병합 방법
    @Transactional
    //머지에 반환된 아이템은 영속성 컨텍스트로 바뀌지 않는다.
    //병합시에는 모든 필드를 교체한다.
    //조금 귀찮더라도 머지를 쓰면 안된다.
    public Item updateItemMerge(Long itemId, Book param){
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(param.getPrice());
        findItem.setName(param.getName());
        findItem.setStockQuantity(param.getStockQuantity());
        return findItem;
    }

    //코드 유지보수를 위한 주의사항
    @Transactional
    //setter를 쓰지말고 의미 있는 함수로 업데이트함수를 만들자
    public void updateItem3(Long itemId, String name, int price, int stockQuantity){
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);
        //영속 상태이므로 Repo.save(item)을 할 필요가 없다.
        //* set함수가 아닌 비즈니스적으로 의미있는 함수로 바꿔야 한다.
        //이래야 변경 지점이 Entity로 간다.
        //findItem.changeBookInfo 등
    }
    @Transactional
    //필드가 많으면 Dto를 사용하자
    //dto에 id가 들어가도 된다.
    public void updateItem4(Long itemId, UpdateItemDto item){
        Item findItem = itemRepository.findOne(itemId);
//        findItem.setDto(item);
    }
}
