package com.saas.member.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberVO {

    private Long id;

    private Long userId;

    private String memberNo;

    private Integer level;

    private Integer points;

    private Integer totalPoints;

    private Integer gender;

    private LocalDate birthday;
}
