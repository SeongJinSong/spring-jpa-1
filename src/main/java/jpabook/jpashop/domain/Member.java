package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String name;

    @Embedded
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "member") //order테이블에 있는 member에 의해 매핑되었다는 의미
    private List<Order> orders = new ArrayList<>();
    //이게 best practice다.
    //하이버네이트가 persist하는 순간 collection을 하이버네이트가 제공하는 내장 컬렉션으로 변경한다.
    //이 컬렉션을 객체생성할때 만들어주고 절대 바꾸지 마라
}
