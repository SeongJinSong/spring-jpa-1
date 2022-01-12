package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {
    @NotEmpty(message = "회원 이름은 필수 입니다.")
    private String name;

    //엔티티에 화면 종속적인 기능이 추가되면 유지보수가 어려워져서
    //엔티티를 최대한 순수하게 유지하도록 구현해야한다.
    //엔티티는 핵심 비즈니스 로직만 가지고 있어야 한다.
    //화면은 From객체와 Dto 객체를 사용하자!

    //서버사이드 렌더링은 Entity를 써도 되지만
    //API를 사용할때는 Entity를 사용하면 안된다.
    //API는 스펙이다!
    //Entity에 로직을 추가했는데 API 스펙이 변하게 된다.
   private String city;
    private String street;
    private String zipcode;
}
