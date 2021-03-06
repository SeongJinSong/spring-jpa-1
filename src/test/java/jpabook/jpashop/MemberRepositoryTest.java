package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {
//    @Autowired MemberRepository memberRepository;

    @Test
    @Transactional //EntityManger를 통한 모든 데이터 변경은 항상 트랜잭션 안에서 이루어져야 한다.
    //test에 있으면 Rollback한다.
    @Rollback(false)
    public void testMember() throws Exception{
//        //given
//        Member member = new Member();
//        member.setUserName("memberA");
//
//        //when
//        Long saveId = memberRepository.save(member);
//        Member findMember = memberRepository.find(saveId);
//
//        //then
//        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
//        Assertions.assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
//        Assertions.assertThat(findMember).isEqualTo(member);
//        System.out.println("findMember == member :" + (findMember == member));
    }
}