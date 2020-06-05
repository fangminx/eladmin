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
import me.zhengjie.gen.domain.ZDept;
import me.zhengjie.gen.service.ZDeptService;
import me.zhengjie.gen.service.dto.ZDeptQueryCriteria;
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
@Api(tags = "部门配置管理")
@RequestMapping("/api/zDept")
public class ZDeptController {

    private final ZDeptService zDeptService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('zDept:list')")
    public void download(HttpServletResponse response, ZDeptQueryCriteria criteria) throws IOException {
        zDeptService.download(zDeptService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询部门配置")
    @ApiOperation("查询部门配置")
    @PreAuthorize("@el.check('zDept:list')")
    public ResponseEntity<Object> query(ZDeptQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(zDeptService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增部门配置")
    @ApiOperation("新增部门配置")
    @PreAuthorize("@el.check('zDept:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ZDept resources){
        return new ResponseEntity<>(zDeptService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改部门配置")
    @ApiOperation("修改部门配置")
    @PreAuthorize("@el.check('zDept:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ZDept resources){
        zDeptService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除部门配置")
    @ApiOperation("删除部门配置")
    @PreAuthorize("@el.check('zDept:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        zDeptService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}