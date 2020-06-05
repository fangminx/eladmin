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

import me.zhengjie.gen.domain.ZPeople;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.gen.repository.ZPeopleRepository;
import me.zhengjie.gen.service.ZPeopleService;
import me.zhengjie.gen.service.dto.ZPeopleDto;
import me.zhengjie.gen.service.dto.ZPeopleQueryCriteria;
import me.zhengjie.gen.service.mapstruct.ZPeopleMapper;
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
* @date 2020-06-05
**/
@Service
@RequiredArgsConstructor
public class ZPeopleServiceImpl implements ZPeopleService {

    private final ZPeopleRepository zPeopleRepository;
    private final ZPeopleMapper zPeopleMapper;

    @Override
    public Map<String,Object> queryAll(ZPeopleQueryCriteria criteria, Pageable pageable){
        Page<ZPeople> page = zPeopleRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(zPeopleMapper::toDto));
    }

    @Override
    public List<ZPeopleDto> queryAll(ZPeopleQueryCriteria criteria){
        return zPeopleMapper.toDto(zPeopleRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ZPeopleDto findById(Long id) {
        ZPeople zPeople = zPeopleRepository.findById(id).orElseGet(ZPeople::new);
        ValidationUtil.isNull(zPeople.getId(),"ZPeople","id",id);
        return zPeopleMapper.toDto(zPeople);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ZPeopleDto create(ZPeople resources) {
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        resources.setId(snowflake.nextId()); 
        return zPeopleMapper.toDto(zPeopleRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ZPeople resources) {
        ZPeople zPeople = zPeopleRepository.findById(resources.getId()).orElseGet(ZPeople::new);
        ValidationUtil.isNull( zPeople.getId(),"ZPeople","id",resources.getId());
        zPeople.copy(resources);
        zPeopleRepository.save(zPeople);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            zPeopleRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ZPeopleDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ZPeopleDto zPeople : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("姓名", zPeople.getName());
            map.put("年龄", zPeople.getAge());
            map.put("工龄", zPeople.getWorkAge());
            map.put("假期总数", zPeople.getHolidayTotal());
            map.put("当前剩余假期天数", zPeople.getRemainHolidayTotal());
            map.put("所在部门名称", zPeople.getDeptName());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}