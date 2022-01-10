package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    //SpringDataJPA가 있으면 @Autowired로 PersistContext인젝션이 가능하다.
    //@PersistenceContext
    private final EntityManager em;

//    @PersistenceUnit
//    private EntityManagerFactory emf;

    public void save(Member member){
        em.persist(member);
    }
    public Member findOne(Long id){
        return em.find(Member.class, id);
    }
    public List<Member> findAll(){
        //entity 객체를 대상으로 쿼리를 한다.
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
