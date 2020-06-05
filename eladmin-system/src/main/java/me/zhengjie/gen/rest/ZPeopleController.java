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
import me.zhengjie.gen.domain.ZPeople;
import me.zhengjie.gen.service.ZPeopleService;
import me.zhengjie.gen.service.dto.ZPeopleQueryCriteria;
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
@Api(tags = "人员信息配置管理")
@RequestMapping("/api/zPeople")
public class ZPeopleController {

    private final ZPeopleService zPeopleService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('zPeople:list')")
    public void download(HttpServletResponse response, ZPeopleQueryCriteria criteria) throws IOException {
        zPeopleService.download(zPeopleService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询人员信息配置")
    @ApiOperation("查询人员信息配置")
    @PreAuthorize("@el.check('zPeople:list')")
    public ResponseEntity<Object> query(ZPeopleQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(zPeopleService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增人员信息配置")
    @ApiOperation("新增人员信息配置")
    @PreAuthorize("@el.check('zPeople:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ZPeople resources){
        return new ResponseEntity<>(zPeopleService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改人员信息配置")
    @ApiOperation("修改人员信息配置")
    @PreAuthorize("@el.check('zPeople:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ZPeople resources){
        zPeopleService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除人员信息配置")
    @ApiOperation("删除人员信息配置")
    @PreAuthorize("@el.check('zPeople:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        zPeopleService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}