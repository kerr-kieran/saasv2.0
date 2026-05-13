package com.saas.member.service;

import com.saas.member.dto.AddressDTO;
import com.saas.member.vo.AddressVO;

import java.util.List;

public interface AddressService {

    List<AddressVO> list(Long userId);

    void add(AddressDTO addressDTO, Long userId);

    void update(Long id, AddressDTO addressDTO);

    void delete(Long id);

    void setDefault(Long userId, Long id);
}
