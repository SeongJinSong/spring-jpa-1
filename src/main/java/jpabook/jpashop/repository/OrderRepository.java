package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;
    public void save(Order order){
        em.persist(order);
    }
    public Order findOne(Long id){
        return em.find(Order.class, id);
    }
    public List<Order> findAll(OrderSearch orderSearch){
        return em.createQuery("select o from Order o join o.member m where o.status = :status and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setFirstResult(100)
                .setMaxResults(1000) //최대 1000건
                .getResultList();
    }
    /*
        myBatis는 동적 쿼리생성하는게 편하다는 장점이 있다.
     */
    /**
     * JPA Criteria -- JPA 표준 스펙으로 제공한다.
     * 하지만 권장하는 방법이 아니다. -> 실무에서 안쓴다.
     */
    /**
     * 실무에서는 QueryDSL을 사용한다.
     */
}
