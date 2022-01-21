package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    /**
     * V1 :
     * 1. 양방향 연관관계가 있으면 무한루프에 빠진다.
     *  - 한쪽을 @JsonIgnore를 해야한다.
     * 2. Lazy 로딩이기 때문에 실제 엔티티 대신 proxy가 들어있어서 오류가 난다.
     *  - json lib가 proxy를 변환 못한다.
     *      > Lazy 강제 초기화 해야한다.
     *      > Hibernate5Module 사용으로 해결 가능
     *
     *  ★★★ 어쨌든 이 방법은 쓰면 안된다.
     *   - 엔티티가 바뀌면 API 쓰펙이 바뀐다.
     *   - API 스펙이 추가노출이 되는 문제가 있다.
     *   - 사용하지 않을 가능성이 있는데, 불필요한 조회가 들어간다.
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        //하이버네이트 모듈을 안쓰고 강제로 lazy loading이 가능하다.
        for(Order order : all){
            order.getMember().getName();
            order.getDelivery().getAddress();
        }
        return all;
    }

    /**
     * V2: 엔티티를 DTO로 변환
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        //ORDER 2개
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> collect = orders
                .stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }
}
