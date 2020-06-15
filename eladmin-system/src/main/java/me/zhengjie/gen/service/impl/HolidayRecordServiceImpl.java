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
package me.zhengjie.gen.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.gen.domain.HolidayRecord;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.gen.repository.HolidayRecordRepository;
import me.zhengjie.gen.service.HolidayRecordService;
import me.zhengjie.gen.service.dto.HolidayRecordDto;
import me.zhengjie.gen.service.dto.HolidayRecordQueryCriteria;
import me.zhengjie.gen.service.mapstruct.HolidayRecordMapper;
import me.zhengjie.utils.date.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;

import java.text.ParseException;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author fangmin
* @date 2020-06-12
**/
@Service
@Slf4j
@RequiredArgsConstructor
public class HolidayRecordServiceImpl implements HolidayRecordService {

    private final HolidayRecordRepository holidayRecordRepository;
    private final HolidayRecordMapper holidayRecordMapper;

    @Override
    public Map<String,Object> queryAll(HolidayRecordQueryCriteria criteria, Pageable pageable){
        Page<HolidayRecord> page = holidayRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(holidayRecordMapper::toDto));
    }

    @Override
    public List<HolidayRecordDto> queryAll(HolidayRecordQueryCriteria criteria){
        return holidayRecordMapper.toDto(holidayRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public HolidayRecordDto findById(Long id) {
        HolidayRecord holidayRecord = holidayRecordRepository.findById(id).orElseGet(HolidayRecord::new);
        ValidationUtil.isNull(holidayRecord.getId(),"HolidayRecord","id",id);
        return holidayRecordMapper.toDto(holidayRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HolidayRecordDto create(HolidayRecord resources) {
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        resources.setId(snowflake.nextId());

        //日期赋值
        java.sql.Date start = DateUtil.strToDate(resources.getRangeDate()[0]);
        java.sql.Date end = DateUtil.strToDate(resources.getRangeDate()[1]);
        resources.setStartDate(start);
        resources.setEndDate(end);
        return holidayRecordMapper.toDto(holidayRecordRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(HolidayRecord resources) {
        HolidayRecord holidayRecord = holidayRecordRepository.findById(resources.getId()).orElseGet(HolidayRecord::new);
        ValidationUtil.isNull( holidayRecord.getId(),"HolidayRecord","id",resources.getId());
        holidayRecord.copy(resources);

        //日期赋值
        java.sql.Date start = DateUtil.strToDate(resources.getRangeDate()[0]);
        java.sql.Date end = DateUtil.strToDate(resources.getRangeDate()[1]);
        holidayRecord.setStartDate(start);
        holidayRecord.setEndDate(end);
        holidayRecordRepository.save(holidayRecord);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            holidayRecordRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<HolidayRecordDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (HolidayRecordDto holidayRecord : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户名", holidayRecord.getUserName());
            map.put("部门名称", holidayRecord.getDeptName());
            map.put("请假开始时间", holidayRecord.getStartDate());
            map.put("请假结束时间", holidayRecord.getEndDate());
            map.put("总共请假天数", holidayRecord.getCount());
            map.put("数据创建时间", holidayRecord.getCreateTime());
            map.put("数据最近一次修改时间", holidayRecord.getUpdateTime());
            map.put("假期状态", holidayRecord.getStatus());
            map.put("手机号", holidayRecord.getPhone());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}