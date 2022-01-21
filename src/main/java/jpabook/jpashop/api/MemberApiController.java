package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController //@Controller + @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

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
