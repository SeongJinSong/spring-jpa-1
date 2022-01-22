package jpabook.jpashop.repository.springdatarepo;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SDMemberRepository extends JpaRepository<Member, Long> {
    // 구현체를 StringDataJPA가 다 구현해서 Injection해준다.

    //없는 것은 만들어준다.
    //select m from Member where m.name = ?
    List<Member> findByName(String name);
}
