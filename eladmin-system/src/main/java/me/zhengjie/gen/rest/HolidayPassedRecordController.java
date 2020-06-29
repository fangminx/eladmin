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

import io.lettuce.core.dynamic.annotation.Param;
import me.zhengjie.annotation.Log;
import me.zhengjie.gen.domain.HolidayPassedRecord;
import me.zhengjie.gen.service.HolidayPassedRecordService;
import me.zhengjie.gen.service.dto.HolidayPassedRecordQueryCriteria;
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
* @date 2020-06-28
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "被抵消记录表管理")
@RequestMapping("/api/holidayPassedRecord")
public class HolidayPassedRecordController {

    private final HolidayPassedRecordService holidayPassedRecordService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('holidayPassedRecord:list')")
    public void download(HttpServletResponse response, HolidayPassedRecordQueryCriteria criteria) throws IOException {
        holidayPassedRecordService.download(holidayPassedRecordService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询被抵消记录表")
    @ApiOperation("查询被抵消记录表")
    @PreAuthorize("@el.check('holidayPassedRecord:list')")
    public ResponseEntity<Object> query(HolidayPassedRecordQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(holidayPassedRecordService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增被抵消记录表")
    @ApiOperation("新增被抵消记录表")
    @PreAuthorize("@el.check('holidayPassedRecord:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody HolidayPassedRecord resources){
        return new ResponseEntity<>(holidayPassedRecordService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改被抵消记录表")
    @ApiOperation("修改被抵消记录表")
    @PreAuthorize("@el.check('holidayPassedRecord:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody HolidayPassedRecord resources){
        holidayPassedRecordService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除被抵消记录表")
    @ApiOperation("删除被抵消记录表")
    @PreAuthorize("@el.check('holidayPassedRecord:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        holidayPassedRecordService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/record")
    @Log("根据假期记录表id查抵消记录")
    @ApiOperation("根据假期记录表id查抵消记录")
    public ResponseEntity<Object> query(@RequestParam String id){
        return new ResponseEntity<>(holidayPassedRecordService.findByRecordId(id),HttpStatus.OK);
    }
}