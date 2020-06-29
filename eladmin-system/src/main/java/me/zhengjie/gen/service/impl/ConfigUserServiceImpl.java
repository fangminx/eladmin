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
import me.zhengjie.gen.domain.ConfigUser;
import me.zhengjie.gen.domain.HolidayRecord;
import me.zhengjie.gen.repository.ConfigParamRepository;
import me.zhengjie.gen.repository.HolidayRecordRepository;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.repository.UserRepository;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

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
    private final ConfigParamRepository configParamRepository;
    private final HolidayRecordRepository holidayRecordRepository;
    private final UserRepository userRepository;

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
        String userName = resources.getUserName();
        User user = userRepository.findByUsername(userName);
        if(ObjectUtils.isEmpty(user)){
            throw new RuntimeException("用户条件关系配置总输入的用户名不存在");
        }
        //判断校验
        List<ConfigUser> configUsers = configUserRepository.findByUserName(userName);
        if(!CollectionUtils.isEmpty(configUsers)){
            List<String> items = configUsers.stream().map(c->c.getConditionItem()).collect(Collectors.toList());
            String thisItem = resources.getConditionItem();
            if(items.contains("参加工作不满20年") | items.contains("参加工作满20年以上")){
                if("参加工作不满20年".equals(thisItem) | "参加工作满20年以上".equals(thisItem) ){
                    throw new RuntimeException("工作年限已配置，请勿重新配置");
                }
            }
            if(items.contains("正常婚假") | items.contains("晚婚假")){
                if("正常婚假".equals(thisItem) | "晚婚假".equals(thisItem) ){
                    throw new RuntimeException("婚假已配置，请勿重新配置");
                }
            }
            if(items.contains("正常产假") | items.contains("晚育产假")){
                if("正常产假".equals(thisItem) | "晚育产假".equals(thisItem) ){
                    throw new RuntimeException("产假已配置，请勿重新配置");
                }
            }

        }
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
            map.put("条件类别", configUser.getConditions());
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

    @Override
    public int[] findAllHolidayAndUsedHolidayByUserName(String userName) {
        List<ConfigParam> configParams = configParamRepository.findAll();
        Map<String,Integer> params = new HashMap<>();
        configParams.stream().forEach(c->{
            params.put(c.getName(),c.getValue());
        });
        List<ConfigUser> configUsers = configUserRepository.findByUserName(userName);
        Integer baseDay = baseStep(configUsers,params);
        Integer optionalDay = optionalStep(configUsers,params);

        List<HolidayRecord> holidayRecords = holidayRecordRepository.findByUserNameAndStatus(userName,"成功");
        Long usedDay = 0L;
        for (HolidayRecord h : holidayRecords){
            usedDay += h.getCount();
        }
        int[] resArray = new int[2];
        resArray[0] = baseDay + optionalDay;
        resArray[1] = usedDay.intValue();

        return resArray;
    }

    private Integer optionalStep(List<ConfigUser> configUsers, Map<String, Integer> params) {
        Integer option1 = params.get("特殊假累加-晚婚假");
        Integer option2 = params.get("特殊假累加-正常婚假");
        Integer option3 = params.get("特殊假累加-晚育产假");
        Integer option4 = params.get("特殊假累加-正常产假");
        Integer option5 = params.get("特殊假累加-陪产假");
        Integer option6 = params.get("特殊假累加-子女中高考假");
        Integer option7 = params.get("特殊假累加-直系亲属重病");
        Integer option8 = params.get("丧假");

        Integer optionDay = 0;

        for (ConfigUser c: configUsers){
            String conditionItem = c.getConditionItem();
            if("晚婚假".equals(conditionItem)){
                optionDay += option1;
            }else if("正常婚假".equals(conditionItem)){
                optionDay += option2;
            }else if("晚育产假".equals(conditionItem)){
                optionDay += option3;
            }else if("正常产假".equals(conditionItem)){
                optionDay += option4;
            }else if("陪产假".equals(conditionItem)){
                optionDay += option5;
            }else if("子女中高考假".equals(conditionItem)){
                optionDay += option6;
            }else if("直属亲属重病".equals(conditionItem)){
                optionDay += option7;
            }else if("丧假".equals(conditionItem)){
                optionDay += option8;
            }

        }
        return optionDay;
    }

    private Integer baseStep(List<ConfigUser> configUsers, Map<String,Integer> params) {

        Integer base1 = params.get("基本条件判断步骤1-包含同时探望父母和配偶");
        Integer base2 = params.get("基本条件判断步骤2-包含仅探望配偶");
        Integer base3 = params.get("基本条件判断步骤3-工作20年以上或未婚探望父母");
        Integer base4 = params.get("基本条件判断步骤4-工作不满20年或已婚探望父母");
        Integer base5 = params.get("基本条件判断步骤5-无任何基本条件配置");

        for (ConfigUser c: configUsers){
            String conditionItem = c.getConditionItem();
            if("父母、配偶均异地（优待）".equals(conditionItem)){
                return base1;
            }else if("探望配偶".equals(conditionItem)){
                return base2;
            }else if("参加工作满20年以上".equals(conditionItem) || "未婚探望父母".equals(conditionItem)){
                return base3;
            }else if("参加工作不满20年".equals(conditionItem) || "已婚探望父母".equals(conditionItem)){
                return base4;
            }
        }
        return base5;
    }
}