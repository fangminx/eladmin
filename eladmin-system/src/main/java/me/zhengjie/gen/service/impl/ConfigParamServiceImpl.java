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

import me.zhengjie.gen.domain.ConfigParam;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.gen.repository.ConfigParamRepository;
import me.zhengjie.gen.service.ConfigParamService;
import me.zhengjie.gen.service.dto.ConfigParamDto;
import me.zhengjie.gen.service.dto.ConfigParamQueryCriteria;
import me.zhengjie.gen.service.mapstruct.ConfigParamMapper;
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
* @date 2020-06-24
**/
@Service
@RequiredArgsConstructor
public class ConfigParamServiceImpl implements ConfigParamService {

    private final ConfigParamRepository configParamRepository;
    private final ConfigParamMapper configParamMapper;

    @Override
    public Map<String,Object> queryAll(ConfigParamQueryCriteria criteria, Pageable pageable){
        Page<ConfigParam> page = configParamRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(configParamMapper::toDto));
    }

    @Override
    public List<ConfigParamDto> queryAll(ConfigParamQueryCriteria criteria){
        return configParamMapper.toDto(configParamRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ConfigParamDto findById(Long id) {
        ConfigParam configParam = configParamRepository.findById(id).orElseGet(ConfigParam::new);
        ValidationUtil.isNull(configParam.getId(),"ConfigParam","id",id);
        return configParamMapper.toDto(configParam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConfigParamDto create(ConfigParam resources) {
        return configParamMapper.toDto(configParamRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ConfigParam resources) {
        ConfigParam configParam = configParamRepository.findById(resources.getId()).orElseGet(ConfigParam::new);
        ValidationUtil.isNull( configParam.getId(),"ConfigParam","id",resources.getId());
        configParam.copy(resources);
        configParamRepository.save(configParam);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            configParamRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ConfigParamDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ConfigParamDto configParam : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("参数名", configParam.getName());
            map.put("参数值", configParam.getValue());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}