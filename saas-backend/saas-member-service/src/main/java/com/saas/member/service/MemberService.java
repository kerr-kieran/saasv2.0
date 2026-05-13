package com.saas.member.service;

import com.saas.member.entity.Member;
import com.saas.member.vo.MemberVO;

public interface MemberService {

    MemberVO getProfile(Long userId);

    void updateProfile(Member member);
}
