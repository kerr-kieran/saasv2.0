package com.saas.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.saas.common.ResultCode;
import com.saas.common.exception.BusinessException;
import com.saas.member.entity.Member;
import com.saas.member.mapper.MemberMapper;
import com.saas.member.service.MemberService;
import com.saas.member.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;

    @Override
    public MemberVO getProfile(Long userId) {
        Member member = memberMapper.selectOne(
                new LambdaQueryWrapper<Member>().eq(Member::getUserId, userId));
        if (member == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "会员信息不存在");
        }
        MemberVO memberVO = new MemberVO();
        BeanUtil.copyProperties(member, memberVO);
        return memberVO;
    }

    @Override
    public void updateProfile(Member member) {
        Member existing = memberMapper.selectOne(
                new LambdaQueryWrapper<Member>().eq(Member::getUserId, member.getUserId()));
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "会员信息不存在");
        }
        member.setId(existing.getId());
        memberMapper.updateById(member);
        log.info("会员信息更新成功: userId={}", member.getUserId());
    }
}
