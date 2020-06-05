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
import me.zhengjie.gen.domain.ZConfig;
import me.zhengjie.gen.service.ZConfigService;
import me.zhengjie.gen.service.dto.ZConfigQueryCriteria;
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
* @date 2020-06-05
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "荣誉条件配置管理")
@RequestMapping("/api/zConfig")
public class ZConfigController {

    private final ZConfigService zConfigService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('zConfig:list')")
    public void download(HttpServletResponse response, ZConfigQueryCriteria criteria) throws IOException {
        zConfigService.download(zConfigService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询荣誉条件配置")
    @ApiOperation("查询荣誉条件配置")
    @PreAuthorize("@el.check('zConfig:list')")
    public ResponseEntity<Object> query(ZConfigQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(zConfigService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增荣誉条件配置")
    @ApiOperation("新增荣誉条件配置")
    @PreAuthorize("@el.check('zConfig:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ZConfig resources){
        return new ResponseEntity<>(zConfigService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改荣誉条件配置")
    @ApiOperation("修改荣誉条件配置")
    @PreAuthorize("@el.check('zConfig:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ZConfig resources){
        zConfigService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除荣誉条件配置")
    @ApiOperation("删除荣誉条件配置")
    @PreAuthorize("@el.check('zConfig:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        zConfigService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}