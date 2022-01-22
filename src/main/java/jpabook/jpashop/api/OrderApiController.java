package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o->o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 페치 조인
     * - 페이징 처리가 안된다는 단점이 있다.
     * - 전체를 DB에서 가져와서 어플리케이션에서 페이징처리한다. -> 메모리 터진다.
     *
     * - 하이버네이트가 이런 선택을 한 이유
     *   > 일대다 조인을 하는 순간 Order의 기준 자체가 다 틀어져버린다.
     *   > 페이징 처리를 하기엔 사이즈가 이상해진다.
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        return orderRepository.findAllWithItem().stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    //@Data //toString 등 여러가지를 제공한다.
    @Getter
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
//        private List<OrderItem> orderItems;
        private List<OrderItemDto> orderItems;

        /**
         * DTO안에 엔티티가 있으면 안된다!!
         * OrderItem 조차도 Dto로 변경해야한다! > 이걸 캐치하지 못하는 경우가 실제로 많다.
         */

        public OrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
//            order.getOrderItems().stream().forEach(o->o.getItem().getName()); // 초기화 필요
//            orderItems = order.getOrderItems();// 위 초기화 없으면 null로 나온다.
            orderItems = order.getOrderItems().stream().map(OrderItemDto::new).collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto{
        private String itemName; //상품 명
        private int orderPrice; //주문 가격
        private int count; //주문 수량
        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}

