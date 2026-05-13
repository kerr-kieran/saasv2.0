package com.saas.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.saas.common.ResultCode;
import com.saas.common.exception.BusinessException;
import com.saas.member.dto.AddressDTO;
import com.saas.member.entity.Address;
import com.saas.member.mapper.AddressMapper;
import com.saas.member.service.AddressService;
import com.saas.member.vo.AddressVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    public List<AddressVO> list(Long userId) {
        List<Address> addresses = addressMapper.selectList(
                new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId));
        return addresses.stream()
                .map(address -> {
                    AddressVO vo = new AddressVO();
                    BeanUtil.copyProperties(address, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void add(AddressDTO addressDTO, Long userId) {
        Address address = new Address();
        BeanUtil.copyProperties(addressDTO, address);
        address.setUserId(userId);
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        addressMapper.insert(address);
        log.info("地址添加成功: userId={}, id={}", userId, address.getId());
    }

    @Override
    public void update(Long id, AddressDTO addressDTO) {
        Address existing = addressMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "地址不存在");
        }
        Address address = new Address();
        BeanUtil.copyProperties(addressDTO, address);
        address.setId(id);
        addressMapper.updateById(address);
        log.info("地址更新成功: id={}", id);
    }

    @Override
    public void delete(Long id) {
        Address existing = addressMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "地址不存在");
        }
        // 软删除
        addressMapper.deleteById(id);
        log.info("地址删除成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long userId, Long id) {
        Address existing = addressMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "地址不存在");
        }
        // 清除用户其他默认地址
        addressMapper.update(null,
                new LambdaUpdateWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .set(Address::getIsDefault, 0));
        // 设置新默认地址
        existing.setIsDefault(1);
        addressMapper.updateById(existing);
        log.info("默认地址设置成功: userId={}, id={}", userId, id);
    }
}
