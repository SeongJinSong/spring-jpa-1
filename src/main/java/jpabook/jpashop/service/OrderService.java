package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count){
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 누군가가 아래와 같이 만든다면 문제가 된다.
        //OrderItem orderItem1 = new OrderItem();
        //orderItem.setCount();

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        //cascade 때문에 전부 저장된다.
        //cascade 범위선정 : delivery나 orderItem은 order에서만 유일하게 관리한다. 이런경우만 사용한다.
        return order.getId();
    }
    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId){
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
    }
    /*
        JPA에 진짜 강점이 여기에서 나온다!
          변경내역 감지가 일어나며 데이터베이스에 업데이트 쿼리가 날라간다.
        MyBatis의 경우
          1. 주문 취소로 상태 변경 -> 배송 상태로 확인 -> 재고를 +하는 쿼리 실행
     */

    /**
     * 주문 검색
     */
    public List<Order> findOrder(OrderSearch orderSearch){
//        return orderRepository.findAllByString(orderSearch);
        return orderRepository.findAll_Querydsl(orderSearch);
    }
}
