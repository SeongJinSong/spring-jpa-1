package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 화면이나 API에 의존관계가 있는 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;
    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders(); // query 1번

        result.forEach(o->{ //query N번
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
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