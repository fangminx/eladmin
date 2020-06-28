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

import me.zhengjie.gen.domain.HolidayPassedRecord;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.gen.repository.HolidayPassedRecordRepository;
import me.zhengjie.gen.service.HolidayPassedRecordService;
import me.zhengjie.gen.service.dto.HolidayPassedRecordDto;
import me.zhengjie.gen.service.dto.HolidayPassedRecordQueryCriteria;
import me.zhengjie.gen.service.mapstruct.HolidayPassedRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author fangmin
* @date 2020-06-28
**/
@Service
@RequiredArgsConstructor
public class HolidayPassedRecordServiceImpl implements HolidayPassedRecordService {

    private final HolidayPassedRecordRepository holidayPassedRecordRepository;
    private final HolidayPassedRecordMapper holidayPassedRecordMapper;

    @Override
    public Map<String,Object> queryAll(HolidayPassedRecordQueryCriteria criteria, Pageable pageable){
        Page<HolidayPassedRecord> page = holidayPassedRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(holidayPassedRecordMapper::toDto));
    }

    @Override
    public List<HolidayPassedRecordDto> queryAll(HolidayPassedRecordQueryCriteria criteria){
        return holidayPassedRecordMapper.toDto(holidayPassedRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public HolidayPassedRecordDto findById(Long id) {
        HolidayPassedRecord holidayPassedRecord = holidayPassedRecordRepository.findById(id).orElseGet(HolidayPassedRecord::new);
        ValidationUtil.isNull(holidayPassedRecord.getId(),"HolidayPassedRecord","id",id);
        return holidayPassedRecordMapper.toDto(holidayPassedRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HolidayPassedRecordDto create(HolidayPassedRecord resources) {
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        resources.setId(snowflake.nextId()); 
        return holidayPassedRecordMapper.toDto(holidayPassedRecordRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(HolidayPassedRecord resources) {
        HolidayPassedRecord holidayPassedRecord = holidayPassedRecordRepository.findById(resources.getId()).orElseGet(HolidayPassedRecord::new);
        ValidationUtil.isNull( holidayPassedRecord.getId(),"HolidayPassedRecord","id",resources.getId());
        holidayPassedRecord.copy(resources);
        holidayPassedRecordRepository.save(holidayPassedRecord);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            holidayPassedRecordRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<HolidayPassedRecordDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (HolidayPassedRecordDto holidayPassedRecord : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("对应请假记录表", holidayPassedRecord.getRecordId());
            map.put("被抵消者", holidayPassedRecord.getPassedUser());
            map.put("被抵消者权重", holidayPassedRecord.getPassedWeight());
            map.put("高优先级用户", holidayPassedRecord.getPriorityUser());
            map.put("高优先级用户权重", holidayPassedRecord.getPriorityWeight());
            map.put("部门名称", holidayPassedRecord.getDeptName());
            map.put("数据创建时间", holidayPassedRecord.getCreateTime());
            map.put("数据最近一次修改时间", holidayPassedRecord.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}