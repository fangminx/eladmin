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
import me.zhengjie.gen.domain.ConfigParam;
import me.zhengjie.gen.service.ConfigParamService;
import me.zhengjie.gen.service.dto.ConfigParamQueryCriteria;
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
* @date 2020-06-24
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "参数配置管理")
@RequestMapping("/api/configParam")
public class ConfigParamController {

    private final ConfigParamService configParamService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('configParam:list')")
    public void download(HttpServletResponse response, ConfigParamQueryCriteria criteria) throws IOException {
        configParamService.download(configParamService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询参数配置")
    @ApiOperation("查询参数配置")
    @PreAuthorize("@el.check('configParam:list')")
    public ResponseEntity<Object> query(ConfigParamQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(configParamService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增参数配置")
    @ApiOperation("新增参数配置")
    @PreAuthorize("@el.check('configParam:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ConfigParam resources){
        return new ResponseEntity<>(configParamService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改参数配置")
    @ApiOperation("修改参数配置")
    @PreAuthorize("@el.check('configParam:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ConfigParam resources){
        configParamService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除参数配置")
    @ApiOperation("删除参数配置")
    @PreAuthorize("@el.check('configParam:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        configParamService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}