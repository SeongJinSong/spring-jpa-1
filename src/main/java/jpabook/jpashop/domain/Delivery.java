package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class Delivery {
    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @JsonIgnore
    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    //ORDINAL은 숫자로 들어간다. >> 중간에 다른 상태가 생기면 망한다. >> DB에 값의 일관성이 깨진다.
    //반드시 스트링으로 넣엉 ㅑ한다.
    private DeliveryStatus status; //READY, COMP
}
