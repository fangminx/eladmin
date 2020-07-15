package me.zhengjie.modules.system.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "批量加用户")
@RestController
@RequestMapping("/api/users/batch")
@RequiredArgsConstructor
public class BatchAddUsers {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

//    @Log("批量新增用户")
//    @ApiOperation("批量新增用户")
//    @GetMapping
//    public ResponseEntity<Object> create(){
//        User resources = new User();
//
//        // 默认密码 123456
//        resources.setPassword(passwordEncoder.encode("123456"));
//        userService.create(resources);
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }
}
