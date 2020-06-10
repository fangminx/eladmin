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
import me.zhengjie.gen.domain.HolidayRecord;
import me.zhengjie.gen.service.HolidayRecordService;
import me.zhengjie.gen.service.dto.HolidayRecordQueryCriteria;
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
* @date 2020-06-09
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "请假记录管理")
@RequestMapping("/api/holidayRecord")
public class HolidayRecordController {

    private final HolidayRecordService holidayRecordService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('holidayRecord:list')")
    public void download(HttpServletResponse response, HolidayRecordQueryCriteria criteria) throws IOException {
        holidayRecordService.download(holidayRecordService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询请假记录")
    @ApiOperation("查询请假记录")
    @PreAuthorize("@el.check('holidayRecord:list')")
    public ResponseEntity<Object> query(HolidayRecordQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(holidayRecordService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增请假记录")
    @ApiOperation("新增请假记录")
    @PreAuthorize("@el.check('holidayRecord:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody HolidayRecord resources){
        return new ResponseEntity<>(holidayRecordService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改请假记录")
    @ApiOperation("修改请假记录")
    @PreAuthorize("@el.check('holidayRecord:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody HolidayRecord resources){
        holidayRecordService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除请假记录")
    @ApiOperation("删除请假记录")
    @PreAuthorize("@el.check('holidayRecord:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        holidayRecordService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}