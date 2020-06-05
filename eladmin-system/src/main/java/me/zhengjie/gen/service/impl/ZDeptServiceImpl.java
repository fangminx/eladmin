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

import me.zhengjie.gen.domain.ZDept;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.gen.repository.ZDeptRepository;
import me.zhengjie.gen.service.ZDeptService;
import me.zhengjie.gen.service.dto.ZDeptDto;
import me.zhengjie.gen.service.dto.ZDeptQueryCriteria;
import me.zhengjie.gen.service.mapstruct.ZDeptMapper;
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
public class ZDeptServiceImpl implements ZDeptService {

    private final ZDeptRepository zDeptRepository;
    private final ZDeptMapper zDeptMapper;

    @Override
    public Map<String,Object> queryAll(ZDeptQueryCriteria criteria, Pageable pageable){
        Page<ZDept> page = zDeptRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(zDeptMapper::toDto));
    }

    @Override
    public List<ZDeptDto> queryAll(ZDeptQueryCriteria criteria){
        return zDeptMapper.toDto(zDeptRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ZDeptDto findById(Long id) {
        ZDept zDept = zDeptRepository.findById(id).orElseGet(ZDept::new);
        ValidationUtil.isNull(zDept.getId(),"ZDept","id",id);
        return zDeptMapper.toDto(zDept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ZDeptDto create(ZDept resources) {
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        resources.setId(snowflake.nextId()); 
        return zDeptMapper.toDto(zDeptRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ZDept resources) {
        ZDept zDept = zDeptRepository.findById(resources.getId()).orElseGet(ZDept::new);
        ValidationUtil.isNull( zDept.getId(),"ZDept","id",resources.getId());
        zDept.copy(resources);
        zDeptRepository.save(zDept);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            zDeptRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ZDeptDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ZDeptDto zDept : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("部门名称", zDept.getName());
            map.put("部门总人数", zDept.getCount());
            map.put("人员在位率", zDept.getRate());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}