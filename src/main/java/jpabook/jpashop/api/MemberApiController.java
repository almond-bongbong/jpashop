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
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

	private final MemberService memberService;

	@GetMapping("/api/v1/members")
	public List<Member> membersV1() {
		return memberService.findMembers();
	}

	@GetMapping("api/v2/members")
	public Result membersV2() {
		List<Member> findMembers = memberService.findMembers();
		List<MemberDTO> members = findMembers.stream()
				.map(m -> new MemberDTO(m.getName()))
				.collect(Collectors.toList());

		return new Result<>(members);
	}

	@Data
	@AllArgsConstructor
	private static class Result<T> {
		private T data;
	}

	@Data
	@AllArgsConstructor
	private static class MemberDTO {
		private String name;
	}

	@PostMapping("/api/v1/member")
	public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
		Long id = memberService.join(member);

		return new CreateMemberResponse(id);
	}

	@PostMapping("/api/v2/member")
	public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
		Member member = new Member();
		member.setName(request.getName());
		Long id = memberService.join(member);

		return new CreateMemberResponse(id);
	}

	@PutMapping("/api/v2/member/{id}")
	public UpdateMemberResponse updateMEmberV2(
			@PathVariable("id") Long id,
			@RequestBody @Valid UpdateMemberRequest request) {
		memberService.update(id, request.getName());
		Member findMember = memberService.findOne(id);

		return new UpdateMemberResponse(findMember.getId(), findMember.getName());
	}

	@Data
	private static class CreateMemberRequest {

		@NotEmpty
		private String name;
	}

	@Data
	private static class CreateMemberResponse {
		private Long id;

		CreateMemberResponse(Long id) {
			this.id = id;
		}
	}

	@Data
	private static class UpdateMemberRequest {
		private String name;
	}

	@Data
	@AllArgsConstructor
	private static class UpdateMemberResponse {
		private Long id;
		private String name;
	}
}
