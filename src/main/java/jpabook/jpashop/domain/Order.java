package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//코드를 제약하는 스타일로 짜는게 좋은 설계와 유지보수로 끌어갈 수 있다.
public class Order {
    @Id
    @GeneratedValue
    @Column(name="order_id")
    private Long id;

    //연관관계 주인은 FK가 가까운 곳으로 선택한다.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    //JPQL select o From order o; -> SQL select * from order;
    // -> n + 1 문제 -> 첫번째 날라온 쿼리가 1번 날라가서 결과가 100개인데 그만큼 멤버를 가져오기 위해 단건 쿼리를 날린다.

    //ManyToOne은 기본이 EAGER다. 하나를 가져오는 거는 무겁지 않으니까 그렇게 설계했을 것이다.
    //ManyToOne은 전부다 찾아서 LAZY로 변경시켜야 한다.
    @OneToMany(mappedBy = "order", fetch = LAZY, cascade = CascadeType.ALL)
    // oneToMany 시리즈는 전부 기본이 LaZy다
    private List<OrderItem> orderItems = new ArrayList<>();

    /*
    persist(orderItemA)
    persist(orderItemB)
    persist(orderItemC)
    persist(order)
    */


    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문 상태 [ORDER, CANCEL]

    //==연관관계 편의 메서드==// 양방향일때 세팅하기 좋다
    public void setMember(Member member){
        this.member=member;
        member.getOrders().add(this);
    }
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery){
        this.delivery=delivery;
        delivery.setOrder(this);
    }

//    public static void main(String[] args) {
//        Member member = new Member();
//        Order order = new Order();
//        member.getOrders().add(order);
//        order.setMember(member);
//    }
    //== 생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //== 비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel(){
        if(delivery.getStatus()==DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }
    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice(){
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }
}
