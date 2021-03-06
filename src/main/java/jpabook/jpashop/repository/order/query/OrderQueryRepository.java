package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 화면이나 API에 의존관계가 있는 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    /**
     * V4: JPA에서 DTO 직접 조회
     */
    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders(); // query 1번

        result.forEach(o->{ //query N번
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    /**
     * V5: 컬렉션 조회 최적화
     */
    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders(); // query 1번

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(result); // query 1번

        //모자랐던 컬렉션 데이터 채워주기
        result.forEach(o->o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    /**
     * V6: 플랫폼 데이터 최적화
     */
    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                "select new "+
                        "jpabook.jpashop.repository.order.query.OrderFlatDto"+
                        "(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)"  +
                            " from Order o" +
                            " join o.member m"+
                            " join o.delivery d"+
                            " join o.orderItems oi "+
                            " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<OrderQueryDto> result) {
        List<Long> orderIds = toOrderIds(result);
        List<OrderItemQueryDto> orderItems = em.createQuery( // query 1번
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto" +
                        "(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
        return orderItemMap;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto" +
                                    "(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                    " from OrderItem oi" +
                                    " join oi.item i" +
                                    " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findOrders() {
        // 컬렉션을 프로젝션하는 것은 불가능하다.
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto" +
                        "(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o"+
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }
}
