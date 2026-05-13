package com.saas.member.controller;

import com.saas.common.Result;
import com.saas.member.entity.Member;
import com.saas.member.service.MemberService;
import com.saas.member.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/profile")
    public Result<MemberVO> getProfile(@RequestHeader("X-User-Id") Long userId) {
        MemberVO memberVO = memberService.getProfile(userId);
        return Result.ok(memberVO);
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestHeader("X-User-Id") Long userId,
                                      @RequestBody Member member) {
        member.setUserId(userId);
        memberService.updateProfile(member);
        return Result.ok();
    }
}
