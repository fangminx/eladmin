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
import me.zhengjie.gen.domain.RealHoliday;
import me.zhengjie.gen.repository.MyDictRepository;
import me.zhengjie.gen.repository.RealHolidayRepository;
import me.zhengjie.gen.service.MyDictService;
import me.zhengjie.gen.service.RealHolidayService;
import me.zhengjie.gen.service.dto.*;
import me.zhengjie.gen.service.mapstruct.MyDictMapper;
import me.zhengjie.gen.service.mapstruct.RealHolidayMapper;
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
@CacheConfig(cacheNames = "realHoliday")
public class RealHolidayServiceImpl implements RealHolidayService {

    private final RealHolidayRepository realHolidayRepository;
    private final RealHolidayMapper realHolidayMapper;
    private final RedisUtils redisUtils;

    @Override
    public Map<String, Object> queryAll(RealHolidayQueryCriteria real, Pageable pageable){
        Page<RealHoliday> page = realHolidayRepository.findAll((root, query, cb) -> QueryHelp.getPredicate(root, real, cb), pageable);
        return PageUtil.toPage(page.map(realHolidayMapper::toDto));
    }

    @Override
    public List<RealHolidayDto> queryAll(RealHolidayQueryCriteria real) {
        List<RealHoliday> list = realHolidayRepository.findAll((root, query, cb) -> QueryHelp.getPredicate(root, real, cb));
        return realHolidayMapper.toDto(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(RealHoliday resources) {
        realHolidayRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RealHoliday resources) {
        // 清理缓存
        delCaches(resources);
        RealHoliday realHoliday = realHolidayRepository.findById(resources.getId()).orElseGet(RealHoliday::new);
        ValidationUtil.isNull( realHoliday.getId(),"RealHoliday","id",resources.getId());
        resources.setId(realHoliday.getId());
        realHolidayRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        // 清理缓存
        List<RealHoliday> realHolidays = realHolidayRepository.findByIdIn(ids);
        for (RealHoliday realHoliday : realHolidays) {
            delCaches(realHoliday);
        }
        realHolidayRepository.deleteByIdIn(ids);
    }

    @Override
    public void download(List<RealHolidayDto> realHolidayDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RealHolidayDto realHolidayDto : realHolidayDtos) {
            if(CollectionUtil.isNotEmpty(realHolidayDto.getRealHolidayDetails())){
                for (RealHolidayDetailDto realHolidayDetail : realHolidayDto.getRealHolidayDetails()) {
                    Map<String,Object> map = new LinkedHashMap<>();
                    map.put("部门名称", realHolidayDto.getName());
                    map.put("描述", realHolidayDto.getDescription());
                    map.put("用户名", realHolidayDetail.getUserName());
                    map.put("参考假日", realHolidayDetail.getRefHoliday());
//                    map.put("创建日期", realHolidayDetail.getCreateTime());
                    list.add(map);
                }
            } else {
                Map<String,Object> map = new LinkedHashMap<>();
                map.put("部门名称", realHolidayDto.getName());
                map.put("描述", realHolidayDto.getDescription());
                map.put("用户名", null);
                map.put("参考假日", null);
//                map.put("创建日期", realHolidayDto.getCreateTime());
                list.add(map);
            }
        }
        FileUtil.downloadExcel(list, response);
    }

    public void delCaches(RealHoliday realHoliday){
        redisUtils.del("realHoliday::name:" + realHoliday.getName());
    }
}