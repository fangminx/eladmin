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

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.gen.domain.ConfigUser;
import me.zhengjie.gen.domain.HolidayPassedRecord;
import me.zhengjie.gen.domain.HolidayRecord;
import me.zhengjie.gen.domain.HolidayReference;
import me.zhengjie.gen.repository.*;
import me.zhengjie.gen.service.ConfigUserService;
import me.zhengjie.gen.service.HolidayReferenceService;
import me.zhengjie.modules.mnt.websocket.MsgType;
import me.zhengjie.modules.mnt.websocket.SocketMsg;
import me.zhengjie.modules.mnt.websocket.WebSocketServer;
import me.zhengjie.modules.system.service.DeptService;
import me.zhengjie.modules.system.service.dto.DeptSimpleDto;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.gen.service.HolidayRecordService;
import me.zhengjie.gen.service.dto.HolidayRecordDto;
import me.zhengjie.gen.service.dto.HolidayRecordQueryCriteria;
import me.zhengjie.gen.service.mapstruct.HolidayRecordMapper;
import me.zhengjie.utils.calc.CalculationUtil;
import me.zhengjie.utils.date.DateUtil;
import me.zhengjie.utils.sms.SmTool;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author fangmin
* @date 2020-06-12
**/
@Service
@Slf4j
@RequiredArgsConstructor
public class HolidayRecordServiceImpl implements HolidayRecordService {

    private final HolidayRecordRepository holidayRecordRepository;
    private final HolidayReferenceRepository holidayReferenceRepository;
    private final ConfigUserRepository configUserRepository;
    private final HolidayReferenceService holidayReferenceService;
    private final HolidayRecordMapper holidayRecordMapper;
    private final DeptService deptService;
    private final ConfigUserService configUserService;
    private final ConfigParamRepository configParamRepository;
    private final HolidayPassedRecordRepository holidayPassedRecordRepository;

    private void sendMsg(String msg, MsgType msgType, String phone) {
        try {
            WebSocketServer.sendInfo(new SocketMsg(msg, msgType), phone);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String,Object> queryAll(HolidayRecordQueryCriteria criteria, Pageable pageable){
        Page<HolidayRecord> page = holidayRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(holidayRecordMapper::toDto));
    }

    @Override
    public List<HolidayRecordDto> queryAll(HolidayRecordQueryCriteria criteria){
        return holidayRecordMapper.toDto(holidayRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public HolidayRecordDto findById(Long id) {
        HolidayRecord holidayRecord = holidayRecordRepository.findById(id).orElseGet(HolidayRecord::new);
        ValidationUtil.isNull(holidayRecord.getId(),"HolidayRecord","id",id);
        return holidayRecordMapper.toDto(holidayRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HolidayRecordDto create(HolidayRecord resources) {
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        resources.setId(snowflake.nextId());

        //日期赋值
        java.sql.Date start = DateUtil.strToDate(resources.getRangeDate()[0]);
        java.sql.Date end = DateUtil.strToDate(resources.getRangeDate()[1]);
        resources.setStartDate(start);
        resources.setEndDate(end);

        //基本信息获取
        String deptName = resources.getDeptName();
        String userName = resources.getUserName();
        Long userPhone = resources.getPhone();
        DeptSimpleDto deptSimpleDto = deptService.findByName(deptName);
        Long allCount = deptSimpleDto.getCount();
        Float preRate = deptSimpleDto.getPreRate();
        Long maxCount = Long.valueOf(CalculationUtil.multiply(allCount.toString(), preRate.toString(),0));
        Integer moveDay = configParamRepository.findByName("最大优先时间（天）").getValue();
        Long afterTime = DateUtil.getDateAfter(new Date(),moveDay).getTime();

        //判断当前用户是否还有假期
        int[] array = configUserService.findAllHolidayAndUsedHolidayByUserName(userName);
        int submitDay = DateUtil.getDiff(start,end,Calendar.DATE)+1;
        if(array[0]<array[1]){
            sendMsg("您当前假期已超过年假总天数，请及时处理，年假总数："+array[0]+"天， 已申请假期："+array[1]+"天", MsgType.error, userPhone.toString());
            throw new RuntimeException("您当前假期已超过年假总天数，请及时处理，年假总数："+array[0]+"天， 已申请假期："+array[1]+"天");
        }else if(array[0]==array[1]) {
            sendMsg("您当前假期刚好用完，共："+array[0]+"天", MsgType.error, userPhone.toString());
            throw new RuntimeException("您当前假期刚好用完，共："+array[0]+"天");
        } else if((array[0]-array[1])<submitDay){
            sendMsg("您当前假期还剩："+(array[0]-array[1])+"天, 请减少日期范围", MsgType.error, userPhone.toString());
            throw new RuntimeException("您当前假期还剩："+(array[0]-array[1])+"天， 请减少日期范围");
        } else if ((array[0]-array[1])>=submitDay){
//            sendMsg("您当前假期还剩："+(array[0]-array[1])+"天， 当前提交天数：" +submitDay+ "， 完成休假后，您将还剩下："+((array[0]-array[1])-submitDay)+"天", MsgType.info, userPhone.toString());
        }

        //记录可以被抵消的人员请假记录信息
        List<HolidayRecord> passedRecords = new ArrayList<>();

        //遍历开始时间和结束时间
        DateUtil.collectLocalDates(DateUtil.date2Str(start),DateUtil.date2Str(end)).forEach(now -> {
            //判断自己的提交记录，不能有日期重复提交
            List<HolidayRecord> hitRecords = checkIfSatisfiedPreRate(deptName,now);
            if(hitRecords.stream().filter(h->h.getUserName().equals(userName)).count() > 0){
                sendMsg("您提交的时间范围和之前成功提交的有冲突，请重新提交", MsgType.error, userPhone.toString());
                throw new RuntimeException("您提交的时间范围和之前成功提交的有冲突，请重新提交");
            }

            //根据在位率来判断
            //当前遍历的日期已经达到最大部门在位人数
            if(hitRecords.size() >= maxCount){
                //判断优先级，看是否能抵消其他人，要遍历完，找到当前天优先级最低的
                Long thisUserWeight = calculateUserWeight(userName);
//                Long minWeight = hitRecords.stream().mapToLong(h->calculateUserWeight(h.getUserName())).min().getAsLong();
                //当天优先级最低的记录
                HolidayRecord minRecord = hitRecords.get(0);
                Long minWeight = calculateUserWeight(minRecord.getUserName());
                for(HolidayRecord hit : hitRecords){
                    Long hitWeight = calculateUserWeight(hit.getUserName());
                    if(hitWeight < minWeight){
                        minWeight = hitWeight;
                        minRecord = hit;
                    }
                }


                //做了个最大优先时间的判断
                if(thisUserWeight > minWeight && minRecord.getStartDate().getTime() > afterTime){
                    //当前日期可以抵消对方,记录对方信息
                    passedRecords.add(minRecord);

                }else {
                    //必然失败
                    sendMsg(now + "已达到最大申请人数，且您的优先级不足，无法抵消其他人", MsgType.error, userPhone.toString());
                    throw new RuntimeException(now + "已达到最大申请人数，且您的优先级不足，无法抵消其他人");
                }

            }else {
                //当前日期空闲

            }
        });

        //遍历结束，当前用户优先级高，准备抵消别人的请假记录
        if(passedRecords.size()>0) {
            HolidayRecord passed = passedRecords.get(0);
            Long minPassedWeight = calculateUserWeight(passed.getUserName());
            for (HolidayRecord pass : passedRecords) {
                Long hitPassedWeight = calculateUserWeight(passed.getUserName());
                if (hitPassedWeight < minPassedWeight) {
                    minPassedWeight = hitPassedWeight;
                    passed = pass;
                }
            }
            holidayRecordRepository.updateStatusById(passed.getId(),"被抵消");
            //被抵消记录表做相关记录
            HolidayPassedRecord holidayPassedRecord = new HolidayPassedRecord();
            holidayPassedRecord.setRecordId(passed.getId());
            holidayPassedRecord.setDeptName(passed.getDeptName());
            holidayPassedRecord.setPassedUser(passed.getUserName());
            holidayPassedRecord.setPassedWeight(minPassedWeight.toString());
            holidayPassedRecord.setPriorityUser(userName);
            holidayPassedRecord.setPriorityWeight(calculateUserWeight(userName).toString());
            holidayPassedRecordRepository.save(holidayPassedRecord);
            sendMsg("您有一条请假记录被高优先级用户："+ userName + "抵消，请注意查看", MsgType.error, passed.getPhone().toString());
            SmTool.sendSmMsg("您好，您有一条请假记录被抵消，请注意查看", passed.getPhone().toString());


        }

        //遍历结束，无论是否抵消别人，当前用户都保存为成功
        resources.setStatus("成功");
        HolidayRecord holidayRecord = holidayRecordRepository.save(resources);
        sendMsg("您当前假期还剩："+(array[0]-array[1])+"天， 当前提交天数：" +submitDay+ "， 完成休假后，您将还剩下："+((array[0]-array[1])-submitDay)+"天", MsgType.info, userPhone.toString());
        return holidayRecordMapper.toDto(holidayRecord);
    }

    //一个方法，判断包含这个日期的（成功）假期记录
    private List<HolidayRecord> checkIfSatisfiedPreRate(String deptName, String now) {
        List<HolidayRecord> holidayRecords = holidayRecordRepository.findByDeptName(deptName);
        //记录now在这些日期范围的记录
        Long nowDateTime = DateUtil.strToDate(now).getTime();
        List<HolidayRecord> hitRecords = holidayRecords.stream().filter(h->"成功".equals(h.getStatus()) && !"未休假".equals(h.getResult()))
                .filter(h->nowDateTime >= h.getStartDate().getTime() && nowDateTime <= h.getEndDate().getTime())
                .collect(Collectors.toList());
         return hitRecords;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(HolidayRecord resources) {
        HolidayRecord holidayRecord = holidayRecordRepository.findById(resources.getId()).orElseGet(HolidayRecord::new);
        ValidationUtil.isNull( holidayRecord.getId(),"HolidayRecord","id",resources.getId());
        holidayRecord.copy(resources);

        //日期赋值
        java.sql.Date start = DateUtil.strToDate(resources.getRangeDate()[0]);
        java.sql.Date end = DateUtil.strToDate(resources.getRangeDate()[1]);
        holidayRecord.setStartDate(start);
        holidayRecord.setEndDate(end);
        holidayRecordRepository.save(holidayRecord);
        //管理员修改后发送短信
        SmTool.sendSmMsg("您好，您有一条请假记录已被管理员变更，请注意查看",holidayRecord.getPhone().toString());
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            holidayRecordRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<HolidayRecordDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (HolidayRecordDto holidayRecord : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户名", holidayRecord.getUserName());
            map.put("部门名称", holidayRecord.getDeptName());
            map.put("请假开始时间", holidayRecord.getStartDate());
            map.put("请假结束时间", holidayRecord.getEndDate());
            map.put("总共请假天数", holidayRecord.getCount());
            map.put("数据创建时间", holidayRecord.getCreateTime());
            map.put("数据最近一次修改时间", holidayRecord.getUpdateTime());
            map.put("假期状态", holidayRecord.getStatus());
            map.put("手机号", holidayRecord.getPhone());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}