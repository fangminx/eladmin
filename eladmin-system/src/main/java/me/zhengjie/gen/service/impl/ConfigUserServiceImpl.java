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

import me.zhengjie.gen.domain.ConfigUser;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.gen.repository.ConfigUserRepository;
import me.zhengjie.gen.service.ConfigUserService;
import me.zhengjie.gen.service.dto.ConfigUserDto;
import me.zhengjie.gen.service.dto.ConfigUserQueryCriteria;
import me.zhengjie.gen.service.mapstruct.ConfigUserMapper;
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
* @date 2020-06-18
**/
@Service
@RequiredArgsConstructor
public class ConfigUserServiceImpl implements ConfigUserService {

    private final ConfigUserRepository configUserRepository;
    private final ConfigUserMapper configUserMapper;

    @Override
    public Map<String,Object> queryAll(ConfigUserQueryCriteria criteria, Pageable pageable){
        Page<ConfigUser> page = configUserRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(configUserMapper::toDto));
    }

    @Override
    public List<ConfigUserDto> queryAll(ConfigUserQueryCriteria criteria){
        return configUserMapper.toDto(configUserRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ConfigUserDto findById(Long id) {
        ConfigUser configUser = configUserRepository.findById(id).orElseGet(ConfigUser::new);
        ValidationUtil.isNull(configUser.getId(),"ConfigUser","id",id);
        return configUserMapper.toDto(configUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConfigUserDto create(ConfigUser resources) {
        return configUserMapper.toDto(configUserRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ConfigUser resources) {
        ConfigUser configUser = configUserRepository.findById(resources.getId()).orElseGet(ConfigUser::new);
        ValidationUtil.isNull( configUser.getId(),"ConfigUser","id",resources.getId());
        configUser.copy(resources);
        configUserRepository.save(configUser);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            configUserRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ConfigUserDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ConfigUserDto configUser : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户名", configUser.getUserName());
            map.put("部门名称", configUser.getDeptName());
            map.put("手机号", configUser.getUserPhone());
            map.put("条件类别", configUser.getCondition());
            map.put("条件项", configUser.getConditionItem());
            map.put("条件权重", configUser.getConditionWeight());
            map.put(" createBy",  configUser.getCreateBy());
            map.put(" updateBy",  configUser.getUpdateBy());
            map.put(" createTime",  configUser.getCreateTime());
            map.put(" updateTime",  configUser.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}