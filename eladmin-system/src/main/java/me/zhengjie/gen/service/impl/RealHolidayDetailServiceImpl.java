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
import me.zhengjie.gen.domain.RealHoliday;
import me.zhengjie.gen.domain.RealHolidayDetail;
import me.zhengjie.gen.repository.MyDictDetailRepository;
import me.zhengjie.gen.repository.MyDictRepository;
import me.zhengjie.gen.repository.RealHolidayDetailRepository;
import me.zhengjie.gen.repository.RealHolidayRepository;
import me.zhengjie.gen.service.MyDictDetailService;
import me.zhengjie.gen.service.RealHolidayDetailService;
import me.zhengjie.gen.service.dto.MyDictDetailDto;
import me.zhengjie.gen.service.dto.MyDictDetailQueryCriteria;
import me.zhengjie.gen.service.dto.RealHolidayDetailDto;
import me.zhengjie.gen.service.dto.RealHolidayDetailQueryCriteria;
import me.zhengjie.gen.service.mapstruct.MyDictDetailMapper;
import me.zhengjie.gen.service.mapstruct.RealHolidayDetailMapper;
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
@CacheConfig(cacheNames = "realHoliday")
public class RealHolidayDetailServiceImpl implements RealHolidayDetailService {

    private final RealHolidayRepository realHolidayRepository;
    private final RealHolidayDetailRepository realHolidayDetailRepository;
    private final RealHolidayDetailMapper realHolidayDetailMapper;
    private final RedisUtils redisUtils;

    @Override
    public Map<String,Object> queryAll(RealHolidayDetailQueryCriteria criteria, Pageable pageable) {
        Page<RealHolidayDetail> page = realHolidayDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(realHolidayDetailMapper::toDto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(RealHolidayDetail resources) {
        realHolidayDetailRepository.save(resources);
        // 清理缓存
        delCaches(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RealHolidayDetail resources) {
        RealHolidayDetail realHolidayDetail = realHolidayDetailRepository.findById(resources.getId()).orElseGet(RealHolidayDetail::new);
        ValidationUtil.isNull( realHolidayDetail.getId(),"RealHolidayDetail","id",resources.getId());
        resources.setId(realHolidayDetail.getId());
        realHolidayDetailRepository.save(resources);
        // 清理缓存
        delCaches(resources);
    }

    @Override
    @Cacheable(key = "'name:' + #p0")
    public List<RealHolidayDetailDto> getRealHolidayByName(String name) {
        return realHolidayDetailMapper.toDto(realHolidayDetailRepository.findByRealHolidayName(name));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        RealHolidayDetail realHolidayDetail = realHolidayDetailRepository.findById(id).orElseGet(RealHolidayDetail::new);
        // 清理缓存
        delCaches(realHolidayDetail);
        realHolidayDetailRepository.deleteById(id);
    }

    public void delCaches(RealHolidayDetail realHolidayDetail){
        RealHoliday realHoliday = realHolidayRepository.findById(realHolidayDetail.getRealHoliday().getId()).orElseGet(RealHoliday::new);
        redisUtils.del("realholiday::name:" + realHoliday.getName());
    }
}