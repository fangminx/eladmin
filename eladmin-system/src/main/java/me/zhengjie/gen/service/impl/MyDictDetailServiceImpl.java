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

import lombok.RequiredArgsConstructor;
import me.zhengjie.gen.domain.MyDict;
import me.zhengjie.gen.domain.MyDictDetail;
import me.zhengjie.gen.repository.MyDictDetailRepository;
import me.zhengjie.gen.repository.MyDictRepository;
import me.zhengjie.gen.service.MyDictDetailService;
import me.zhengjie.gen.service.dto.MyDictDetailDto;
import me.zhengjie.gen.service.dto.MyDictDetailQueryCriteria;
import me.zhengjie.gen.service.mapstruct.MyDictDetailMapper;
import me.zhengjie.modules.system.domain.Dict;
import me.zhengjie.modules.system.domain.DictDetail;
import me.zhengjie.modules.system.repository.DictDetailRepository;
import me.zhengjie.modules.system.repository.DictRepository;
import me.zhengjie.modules.system.service.DictDetailService;
import me.zhengjie.modules.system.service.dto.DictDetailDto;
import me.zhengjie.modules.system.service.dto.DictDetailQueryCriteria;
import me.zhengjie.modules.system.service.mapstruct.DictDetailMapper;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import me.zhengjie.utils.RedisUtils;
import me.zhengjie.utils.ValidationUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "dict")
public class MyDictDetailServiceImpl implements MyDictDetailService {

    private final MyDictRepository dictRepository;
    private final MyDictDetailRepository dictDetailRepository;
    private final MyDictDetailMapper dictDetailMapper;
    private final RedisUtils redisUtils;

    @Override
    public Map<String,Object> queryAll(MyDictDetailQueryCriteria criteria, Pageable pageable) {
        Page<MyDictDetail> page = dictDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dictDetailMapper::toDto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(MyDictDetail resources) {
        dictDetailRepository.save(resources);
        // 清理缓存
        delCaches(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MyDictDetail resources) {
        MyDictDetail dictDetail = dictDetailRepository.findById(resources.getId()).orElseGet(MyDictDetail::new);
        ValidationUtil.isNull( dictDetail.getId(),"MyDictDetail","id",resources.getId());
        resources.setId(dictDetail.getId());
        dictDetailRepository.save(resources);
        // 清理缓存
        delCaches(resources);
    }

    @Override
    @Cacheable(key = "'name:' + #p0")
    public List<MyDictDetailDto> getDictByName(String name) {
        return dictDetailMapper.toDto(dictDetailRepository.findByDictName(name));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        MyDictDetail dictDetail = dictDetailRepository.findById(id).orElseGet(MyDictDetail::new);
        // 清理缓存
        delCaches(dictDetail);
        dictDetailRepository.deleteById(id);
    }

    public void delCaches(MyDictDetail dictDetail){
        MyDict dict = dictRepository.findById(dictDetail.getDict().getId()).orElseGet(MyDict::new);
        redisUtils.del("mydept::name:" + dict.getName());
    }
}