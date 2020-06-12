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
import me.zhengjie.gen.domain.MyDictDetail;
import me.zhengjie.gen.domain.RealHoliday;
import me.zhengjie.gen.domain.RealHolidayDetail;
import me.zhengjie.gen.service.MyDictDetailService;
import me.zhengjie.gen.service.RealHolidayDetailService;
import me.zhengjie.gen.service.dto.MyDictDetailDto;
import me.zhengjie.gen.service.dto.MyDictDetailQueryCriteria;
import me.zhengjie.gen.service.dto.RealHolidayDetailDto;
import me.zhengjie.gen.service.dto.RealHolidayDetailQueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@RestController
@RequiredArgsConstructor
@Api(tags = "实时部门人员假日数据")
@RequestMapping("/api/realDetail")
public class RealHolidayDetailController {

    private final RealHolidayDetailService realHolidayDetailService;
    private static final String ENTITY_NAME = "realHolidayDetail";

    @Log("查询实时部门人员假日数据")
    @ApiOperation("查询实时部门人员假日数据")
    @GetMapping
    public ResponseEntity<Object> query(RealHolidayDetailQueryCriteria criteria,
                                        @PageableDefault(sort = {"realHolidaySort"}, direction = Sort.Direction.ASC) Pageable pageable){
        return new ResponseEntity<>(realHolidayDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("查询多个实时部门人员假日数据详情")
    @ApiOperation("查询多个实时部门人员假日数据详情")
    @GetMapping(value = "/map")
    public ResponseEntity<Object> getDictDetailMaps(@RequestParam String realName){
        String[] names = realName.split("[,，]");
        Map<String, List<RealHolidayDetailDto>> realHolidayMap = new HashMap<>(16);
        for (String name : names) {
            realHolidayMap.put(name, realHolidayDetailService.getRealHolidayByName(name));
        }
        return new ResponseEntity<>(realHolidayMap, HttpStatus.OK);
    }

    @Log("新增实时部门人员假日数据详情")
    @ApiOperation("新增实时部门人员假日数据详情")
    @PostMapping
    @PreAuthorize("@el.check('realholiday:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody RealHolidayDetail resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        realHolidayDetailService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改实时部门人员假日数据详情")
    @ApiOperation("修改实时部门人员假日数据详情")
    @PutMapping
    @PreAuthorize("@el.check('realholiday:edit')")
    public ResponseEntity<Object> update(@Validated(RealHolidayDetail.Update.class) @RequestBody RealHolidayDetail resources){
        realHolidayDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除实时部门人员假日数据详情")
    @ApiOperation("删除实时部门人员假日数据详情")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("@el.check('realholiday:del')")
    public ResponseEntity<Object> delete(@PathVariable Long id){
        realHolidayDetailService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}