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

import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.gen.domain.MyDict;
import me.zhengjie.gen.repository.MyDictRepository;
import me.zhengjie.gen.service.MyDictService;
import me.zhengjie.gen.service.dto.ConditionDto;
import me.zhengjie.gen.service.dto.MyDictDetailDto;
import me.zhengjie.gen.service.dto.MyDictDto;
import me.zhengjie.gen.service.dto.MyDictQueryCriteria;
import me.zhengjie.gen.service.mapstruct.MyDictMapper;
import me.zhengjie.modules.system.domain.Dict;
import me.zhengjie.modules.system.repository.DictRepository;
import me.zhengjie.modules.system.service.DictService;
import me.zhengjie.modules.system.service.dto.DictDetailDto;
import me.zhengjie.modules.system.service.dto.DictDto;
import me.zhengjie.modules.system.service.dto.DictQueryCriteria;
import me.zhengjie.modules.system.service.mapstruct.DictMapper;
import me.zhengjie.utils.*;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "dict")
public class MyDictServiceImpl implements MyDictService {

    private final MyDictRepository dictRepository;
    private final MyDictMapper dictMapper;
    private final RedisUtils redisUtils;

    @Override
    public Map<String, Object> queryAll(MyDictQueryCriteria dict, Pageable pageable){
        Page<MyDict> page = dictRepository.findAll((root, query, cb) -> QueryHelp.getPredicate(root, dict, cb), pageable);
        return PageUtil.toPage(page.map(dictMapper::toDto));
    }

    @Override
    public List<MyDictDto> queryAll(MyDictQueryCriteria dict) {
        List<MyDict> list = dictRepository.findAll((root, query, cb) -> QueryHelp.getPredicate(root, dict, cb));
        return dictMapper.toDto(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(MyDict resources) {
        dictRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MyDict resources) {
        // 清理缓存
        delCaches(resources);
        MyDict dict = dictRepository.findById(resources.getId()).orElseGet(MyDict::new);
        ValidationUtil.isNull( dict.getId(),"MyDict","id",resources.getId());
        resources.setId(dict.getId());
        dictRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        // 清理缓存
        List<MyDict> dicts = dictRepository.findByIdIn(ids);
        for (MyDict dict : dicts) {
            delCaches(dict);
        }
        dictRepository.deleteByIdIn(ids);
    }

    @Override
    public void download(List<MyDictDto> dictDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MyDictDto dictDTO : dictDtos) {
            if(CollectionUtil.isNotEmpty(dictDTO.getDictDetails())){
                for (MyDictDetailDto dictDetail : dictDTO.getDictDetails()) {
                    Map<String,Object> map = new LinkedHashMap<>();
                    map.put("字典名称", dictDTO.getName());
                    map.put("字典描述", dictDTO.getDescription());
                    map.put("字典标签", dictDetail.getLabel());
                    map.put("字典值", dictDetail.getValue());
                    map.put("创建日期", dictDetail.getCreateTime());
                    list.add(map);
                }
            } else {
                Map<String,Object> map = new LinkedHashMap<>();
                map.put("字典名称", dictDTO.getName());
                map.put("字典描述", dictDTO.getDescription());
                map.put("字典标签", null);
                map.put("字典值", null);
                map.put("创建日期", dictDTO.getCreateTime());
                list.add(map);
            }
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<ConditionDto> findAllConditions() {
        List<MyDict> myDicts = dictRepository.findAll();
        List<ConditionDto> conditionDtos = new ArrayList<>();
        myDicts.stream().forEach(m->{
            ConditionDto conditionDto = new ConditionDto();
            List<Map<String, Object>> list = new ArrayList<>();
            conditionDto.setLabel(m.getName());
            m.getDictDetails().stream().forEach(md->{
                Map<String,Object> map = new HashMap<>();
                map.put("value",md.getLabel()); //前端要唯一
                map.put("label",md.getLabel());
                map.put("weight",md.getWeight());
                list.add(map);
                conditionDto.setChildren(list);
            });
            conditionDtos.add(conditionDto);
        });

        return conditionDtos;
    }

    public void delCaches(MyDict dict){
        redisUtils.del("mydept::name:" + dict.getName());
    }
}