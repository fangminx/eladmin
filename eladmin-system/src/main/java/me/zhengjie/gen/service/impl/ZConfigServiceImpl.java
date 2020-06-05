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

import me.zhengjie.gen.domain.ZConfig;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.gen.repository.ZConfigRepository;
import me.zhengjie.gen.service.ZConfigService;
import me.zhengjie.gen.service.dto.ZConfigDto;
import me.zhengjie.gen.service.dto.ZConfigQueryCriteria;
import me.zhengjie.gen.service.mapstruct.ZConfigMapper;
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
* @date 2020-06-05
**/
@Service
@RequiredArgsConstructor
public class ZConfigServiceImpl implements ZConfigService {

    private final ZConfigRepository zConfigRepository;
    private final ZConfigMapper zConfigMapper;

    @Override
    public Map<String,Object> queryAll(ZConfigQueryCriteria criteria, Pageable pageable){
        Page<ZConfig> page = zConfigRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(zConfigMapper::toDto));
    }

    @Override
    public List<ZConfigDto> queryAll(ZConfigQueryCriteria criteria){
        return zConfigMapper.toDto(zConfigRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ZConfigDto findById(Long id) {
        ZConfig zConfig = zConfigRepository.findById(id).orElseGet(ZConfig::new);
        ValidationUtil.isNull(zConfig.getId(),"ZConfig","id",id);
        return zConfigMapper.toDto(zConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ZConfigDto create(ZConfig resources) {
        return zConfigMapper.toDto(zConfigRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ZConfig resources) {
        ZConfig zConfig = zConfigRepository.findById(resources.getId()).orElseGet(ZConfig::new);
        ValidationUtil.isNull( zConfig.getId(),"ZConfig","id",resources.getId());
        zConfig.copy(resources);
        zConfigRepository.save(zConfig);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            zConfigRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ZConfigDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ZConfigDto zConfig : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("姓名", zConfig.getName());
            map.put("基本条件", zConfig.getJbtj());
            map.put("荣誉条件", zConfig.getRytj());
            map.put("优惠条件", zConfig.getYhtj());
            map.put("特殊条件", zConfig.getTstj());
            map.put("其他条件", zConfig.getQttj());
            map.put("优先级得分", zConfig.getScore());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}