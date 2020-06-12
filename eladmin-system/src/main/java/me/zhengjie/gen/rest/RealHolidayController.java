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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.base.BaseEntity;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.gen.domain.MyDict;
import me.zhengjie.gen.domain.RealHoliday;
import me.zhengjie.gen.service.MyDictService;
import me.zhengjie.gen.service.RealHolidayService;
import me.zhengjie.gen.service.dto.MyDictQueryCriteria;
import me.zhengjie.gen.service.dto.RealHolidayQueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@RestController
@RequiredArgsConstructor
@Api(tags = "实时假日")
@RequestMapping("/api/real")
public class RealHolidayController {

    private final RealHolidayService realHolidayService;
    private static final String ENTITY_NAME = "realholiday";

    @Log("导出实时假日数据")
    @ApiOperation("实时假日数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('realholiday:list')")
    public void download(HttpServletResponse response, RealHolidayQueryCriteria criteria) throws IOException {
        realHolidayService.download(realHolidayService.queryAll(criteria), response);
    }

    @Log("查询实时部门假日数据")
    @ApiOperation("查询实时部门假日数据")
    @GetMapping(value = "/all")
    @PreAuthorize("@el.check('realholiday:list')")
    public ResponseEntity<Object> queryAll(){
        return new ResponseEntity<>(realHolidayService.queryAll(new RealHolidayQueryCriteria()),HttpStatus.OK);
    }

    @Log("查询实时部门假日数据")
    @ApiOperation("查询实时部门假日数据")
    @GetMapping
    @PreAuthorize("@el.check('realholiday:list')")
    public ResponseEntity<Object> query(RealHolidayQueryCriteria resources, Pageable pageable){
        return new ResponseEntity<>(realHolidayService.queryAll(resources,pageable),HttpStatus.OK);
    }

    @Log("新增实时部门假日数据")
    @ApiOperation("新增实时部门假日数据")
    @PostMapping
    @PreAuthorize("@el.check('realholiday:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody RealHoliday resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        realHolidayService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改实时部门假日数据")
    @ApiOperation("修改实时部门假日数据")
    @PutMapping
    @PreAuthorize("@el.check('realholiday:edit')")
    public ResponseEntity<Object> update(@Validated(RealHoliday.Update.class) @RequestBody RealHoliday resources){
        realHolidayService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除实时部门假日数据")
    @ApiOperation("删除实时部门假日数据")
    @DeleteMapping
    @PreAuthorize("@el.check('realholiday:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        realHolidayService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}