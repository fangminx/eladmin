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
import me.zhengjie.gen.domain.HolidayReference;
import me.zhengjie.gen.service.HolidayReferenceService;
import me.zhengjie.gen.service.dto.HolidayReferenceQueryCriteria;
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
* @date 2020-06-12
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "实时参考假日，根据部门在位率算管理")
@RequestMapping("/api/holidayReference")
public class HolidayReferenceController {

    private final HolidayReferenceService holidayReferenceService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('holidayReference:list')")
    public void download(HttpServletResponse response, HolidayReferenceQueryCriteria criteria) throws IOException {
        holidayReferenceService.download(holidayReferenceService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询实时参考假日，根据部门在位率算")
    @ApiOperation("查询实时参考假日，根据部门在位率算")
    @PreAuthorize("@el.check('holidayReference:list')")
    public ResponseEntity<Object> query(HolidayReferenceQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(holidayReferenceService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增实时参考假日，根据部门在位率算")
    @ApiOperation("新增实时参考假日，根据部门在位率算")
    @PreAuthorize("@el.check('holidayReference:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody HolidayReference resources){
        return new ResponseEntity<>(holidayReferenceService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改实时参考假日，根据部门在位率算")
    @ApiOperation("修改实时参考假日，根据部门在位率算")
    @PreAuthorize("@el.check('holidayReference:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody HolidayReference resources){
        holidayReferenceService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除实时参考假日，根据部门在位率算")
    @ApiOperation("删除实时参考假日，根据部门在位率算")
    @PreAuthorize("@el.check('holidayReference:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        holidayReferenceService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}