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

import me.zhengjie.gen.domain.HolidayReference;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.gen.repository.HolidayReferenceRepository;
import me.zhengjie.gen.service.HolidayReferenceService;
import me.zhengjie.gen.service.dto.HolidayReferenceDto;
import me.zhengjie.gen.service.dto.HolidayReferenceQueryCriteria;
import me.zhengjie.gen.service.mapstruct.HolidayReferenceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
* @date 2020-06-12
**/
@Service
@RequiredArgsConstructor
public class HolidayReferenceServiceImpl implements HolidayReferenceService {

    private final HolidayReferenceRepository holidayReferenceRepository;
    private final HolidayReferenceMapper holidayReferenceMapper;

    @Override
    public Map<String,Object> queryAll(HolidayReferenceQueryCriteria criteria, Pageable pageable){
        Page<HolidayReference> page = holidayReferenceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(holidayReferenceMapper::toDto));
    }

    @Override
    public List<HolidayReferenceDto> queryAll(HolidayReferenceQueryCriteria criteria){
        return holidayReferenceMapper.toDto(holidayReferenceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public HolidayReferenceDto findById(Long id) {
        HolidayReference holidayReference = holidayReferenceRepository.findById(id).orElseGet(HolidayReference::new);
        ValidationUtil.isNull(holidayReference.getId(),"HolidayReference","id",id);
        return holidayReferenceMapper.toDto(holidayReference);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HolidayReferenceDto create(HolidayReference resources) {
        return holidayReferenceMapper.toDto(holidayReferenceRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(HolidayReference resources) {
        HolidayReference holidayReference = holidayReferenceRepository.findById(resources.getId()).orElseGet(HolidayReference::new);
        ValidationUtil.isNull( holidayReference.getId(),"HolidayReference","id",resources.getId());
        holidayReference.copy(resources);
        holidayReferenceRepository.save(holidayReference);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            holidayReferenceRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<HolidayReferenceDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (HolidayReferenceDto holidayReference : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("部门名称", holidayReference.getDeptName());
            map.put("用户名", holidayReference.getUserName());
            map.put("休息日", holidayReference.getRefHolidayDate());
            map.put(" createBy",  holidayReference.getCreateBy());
            map.put(" updateBy",  holidayReference.getUpdateBy());
            map.put(" createTime",  holidayReference.getCreateTime());
            map.put(" updateTime",  holidayReference.getUpdateTime());
            map.put("手机号", holidayReference.getUserPhone());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}