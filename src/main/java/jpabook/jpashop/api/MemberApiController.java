package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController //@Controller + @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 조회 API
     * 엔티티를 리턴하면 유연성이 떨어진다.
     * @JsonIgnore를 사용할 일이 생기면 안된다.
     *
     * 객체 지향의 핵심은 클라이언트이기 때문에
     * API 스펙이 바뀌게 하면 안된다.
     *
     * 스펙 루트는 Array가 되지 않도록 해라
     * - 스펙 확장이 불가능하다.
     */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2(){
        List<Member> findMembers =  memberService.findMembers();
        List<MemberDto> collect = findMembers.stream().map(m -> new MemberDto(m.getName())).collect(toList());
        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private int count; // 유연성이 생겼다. 상황에 따라 추가도 가능하다.
        private T data;
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * V2 장점
     *  - API 스펙이 바뀌지 않는다.
     *  - 스펙에 맞게 제약조건을 설정할 수 있어 유지보수하기 쉽다.
     *  - 어떻게 변경되었는지 추적이 쉽다.
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 수정
     * Request, Response를 따로 가져간다.
     *  - 등록과 수정은 보통 많이 다른 경우가 많기 때문
     * put은 똑같은 API를 여러번 해도 결과가 똑같다. = 멱등하다
     */

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id, request.getName());
        // update함수에서 Member를 리턴하면 update를 하면서 멤버를 query해버리는 꼴이 된다.
        // 커멘드와 쿼리를 분리해야 유지보수성이 증대된다.
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    // 엔티티는 롬복을 최대한 자제하고 Getter만 쓰지만 DTO는 막써도 된다.
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
    @Data
    static class CreateMemberRequest{
        @NotEmpty
        private String name;
    }
}
