package com.saas.member.controller;

import com.saas.common.Result;
import com.saas.member.dto.AddressDTO;
import com.saas.member.service.AddressService;
import com.saas.member.vo.AddressVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/member/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public Result<List<AddressVO>> list(@RequestHeader("X-User-Id") Long userId) {
        List<AddressVO> list = addressService.list(userId);
        return Result.ok(list);
    }

    @PostMapping
    public Result<Void> add(@RequestHeader("X-User-Id") Long userId,
                            @Valid @RequestBody AddressDTO addressDTO) {
        addressService.add(addressDTO, userId);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @Valid @RequestBody AddressDTO addressDTO) {
        addressService.update(id, addressDTO);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@RequestHeader("X-User-Id") Long userId,
                                   @PathVariable Long id) {
        addressService.setDefault(userId, id);
        return Result.ok();
    }
}
