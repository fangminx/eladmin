/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package me.zhengjie.gen.rest;

import me.zhengjie.annotation.Log;
import me.zhengjie.gen.domain.ConfigUser;
import me.zhengjie.gen.service.ConfigUserService;
import me.zhengjie.gen.service.dto.ConfigUserQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author fangmin
* @date 2020-06-18
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "用户荣誉条件配置管理")
@RequestMapping("/api/configUser")
public class ConfigUserController {

    private final ConfigUserService configUserService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('configUser:list')")
    public void download(HttpServletResponse response, ConfigUserQueryCriteria criteria) throws IOException {
        configUserService.download(configUserService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询用户荣誉条件配置")
    @ApiOperation("查询用户荣誉条件配置")
    @PreAuthorize("@el.check('configUser:list')")
    public ResponseEntity<Object> query(ConfigUserQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(configUserService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增用户荣誉条件配置")
    @ApiOperation("新增用户荣誉条件配置")
    @PreAuthorize("@el.check('configUser:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ConfigUser resources){
        return new ResponseEntity<>(configUserService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改用户荣誉条件配置")
    @ApiOperation("修改用户荣誉条件配置")
    @PreAuthorize("@el.check('configUser:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ConfigUser resources){
        configUserService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除用户荣誉条件配置")
    @ApiOperation("删除用户荣誉条件配置")
    @PreAuthorize("@el.check('configUser:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        configUserService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}