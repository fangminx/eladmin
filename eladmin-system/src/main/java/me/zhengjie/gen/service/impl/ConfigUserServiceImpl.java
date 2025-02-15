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
import me.zhengjie.gen.service.HolidayRecordService;
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
            if(items.contains("未婚探望父母") | items.contains("已婚探望父母") | items.contains("探望配偶") | items.contains("父母、配偶均异地（优待）")){
                if("未婚探望父母".equals(thisItem) | "已婚探望父母".equals(thisItem) | "探望配偶".equals(thisItem)  | "父母、配偶均异地（优待）".equals(thisItem)){
                    throw new RuntimeException("探亲假已配置，请勿重新配置");
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

        List<HolidayRecord> holidayRecordsSuccess = holidayRecordRepository.findByUserNameAndStatus(userName,"成功")
                              .stream().filter(h->!"未休假".equals(h.getResult())).collect(Collectors.toList());

        List<HolidayRecord> holidayRecordsResult = holidayRecordRepository.findByUserNameAndResult(userName,"已休完")
                              .stream().filter(h->!"成功".equals(h.getStatus())).collect(Collectors.toList());
        Long usedDay = 0L;
        for (HolidayRecord h : holidayRecordsSuccess){
            usedDay += h.getCount();
        }
        for (HolidayRecord h : holidayRecordsResult){
            usedDay += h.getCount();
        }
        int[] resArray = new int[2];
        resArray[0] = baseDay + optionalDay;
        resArray[1] = usedDay.intValue();

        return resArray;
    }

    @Override
    public List<Map<String,Object>> findAllUserHolidayForShowVChar() {
        List<String> users = userRepository.findAll().stream().map(u->u.getUsername()).collect(Collectors.toList());
        List<Map<String,Object>> list = new ArrayList<>();
        for(String u: users){
            Map<String,Object> map = new HashMap<>();
            int[] days = findAllHolidayAndUsedHolidayByUserName(u);
            map.put("用户名",u);
            map.put("假期总数",days[0]);
            map.put("剩余假期",days[0] - days[1]);
            map.put("优先级",calculateUserWeight(u));
            list.add(map);
        }

        list = list.stream().sorted((o1, o2) -> {
            Long groupScore1 = Long.parseLong(o1.get("优先级").toString());
            Long groupScore2 = Long.parseLong(o2.get("优先级").toString());
            return Long.compare(groupScore2, groupScore1);
        }).collect(Collectors.toList());
        return list;
    }

    //一个方法，计算用户当前的优先级（权重）
    private Long calculateUserWeight(String userName){
        List<ConfigUser> configUsers = configUserRepository.findByUserName(userName);
        Long userWeight = 0L;
        for(ConfigUser c: configUsers){
            if(!ObjectUtils.isEmpty(c.getConditionWeight())){
                userWeight += c.getConditionWeight();
            }
        }
        return userWeight;
    }

    private Integer optionalStep(List<ConfigUser> configUsers, Map<String, Integer> params) {

        if(CollectionUtils.isEmpty(configUsers)){
            return 0;
        }

        Integer option1 = params.get("特殊假累加-晚婚假");
        Integer option2 = params.get("特殊假累加-正常婚假");
        Integer option3 = params.get("特殊假累加-晚育产假");
        Integer option4 = params.get("特殊假累加-正常产假");
        Integer option5 = params.get("特殊假累加-陪产假");
        Integer option6 = params.get("特殊假累加-子女中高考假");
        Integer option7 = params.get("特殊假累加-直系亲属重病");
        Integer option8 = params.get("丧假");

        Integer optionDay = 0;

        List<String> conditionItems = configUsers.stream().map(c->c.getConditionItem()).collect(Collectors.toList());

        for (String item: conditionItems){

            if("晚婚假".equals(item)){
                optionDay += option1;
            }else if("正常婚假".equals(item)){
                optionDay += option2;
            }else if("晚育产假".equals(item)){
                optionDay += option3;
            }else if("正常产假".equals(item)){
                optionDay += option4;
            }else if("陪产假".equals(item)){
                optionDay += option5;
            }else if("子女中高考假".equals(item)){
                optionDay += option6;
            }else if("直属亲属重病".equals(item)){
                optionDay += option7;
            }else if("丧假".equals(item)){
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

        if(CollectionUtils.isEmpty(configUsers)){
            return base5;
        }
        List<String> conditionItems = configUsers.stream().map(c->c.getConditionItem()).collect(Collectors.toList());

        if (conditionItems.contains("父母、配偶均异地（优待）")){
            return base1;
        }
        if (conditionItems.contains("探望配偶")){
            return base2;
        }
        if (conditionItems.contains("参加工作满20年以上") || conditionItems.contains("未婚探望父母")){
            return base3;
        }
        if (conditionItems.contains("参加工作不满20年") || conditionItems.contains("已婚探望父母")){
            return base4;
        }
        return base5;
    }
}