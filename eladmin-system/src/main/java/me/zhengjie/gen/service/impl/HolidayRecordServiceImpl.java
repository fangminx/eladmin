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
import me.zhengjie.gen.domain.HolidayRecord;
import me.zhengjie.gen.domain.HolidayReference;
import me.zhengjie.gen.repository.HolidayReferenceRepository;
import me.zhengjie.gen.service.HolidayReferenceService;
import me.zhengjie.modules.mnt.websocket.MsgType;
import me.zhengjie.modules.mnt.websocket.SocketMsg;
import me.zhengjie.modules.mnt.websocket.WebSocketServer;
import me.zhengjie.modules.system.service.DeptService;
import me.zhengjie.modules.system.service.dto.DeptSimpleDto;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.gen.repository.HolidayRecordRepository;
import me.zhengjie.gen.service.HolidayRecordService;
import me.zhengjie.gen.service.dto.HolidayRecordDto;
import me.zhengjie.gen.service.dto.HolidayRecordQueryCriteria;
import me.zhengjie.gen.service.mapstruct.HolidayRecordMapper;
import me.zhengjie.utils.calc.CalculationUtil;
import me.zhengjie.utils.date.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;

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
    private final HolidayReferenceService holidayReferenceService;
    private final HolidayRecordMapper holidayRecordMapper;
    private final DeptService deptService;

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


        //遍历开始时间和结束时间
        DateUtil.collectLocalDates(DateUtil.date2Str(start),DateUtil.date2Str(end)).forEach(now -> {
            //根据在位率来判断是新增还是修改
            HolidayReference holidayReference = new HolidayReference();
            holidayReference.setUserName(userName);
            holidayReference.setDeptName(deptName);
            holidayReference.setUserPhone(userPhone);
            holidayReference.setRefHolidayDate(DateUtil.strToDate(now));

            Long nowCount = holidayReferenceRepository.countByDeptNameAndRefHolidayDate(deptName, DateUtil.strToDate(now));
            if(nowCount < maxCount){
                holidayReferenceService.create(holidayReference);
                sendMsg("您在" + now + "的请假申请已进入竞选状态",MsgType.success,userPhone.toString());
            }else {
                List<HolidayReference> holidayReferences = holidayReferenceRepository.findAllByDeptNameAndRefHolidayDateOrderByUpdateTimeAsc(deptName,DateUtil.strToDate(now));
                //被淘汰的请假用户手机号
                Long passedPhone = holidayReferences.get(0).getUserPhone();
                //获得要被淘汰用户的参考假日
                java.sql.Date refDate = holidayReferences.get(0).getRefHolidayDate();
                holidayReference.setId(holidayReferences.get(0).getId());
                if(userPhone.equals(passedPhone) && now.equals(refDate.toString())){
                    sendMsg("您在" + now + "已提交过假日申请,请重新申请" , MsgType.error,userPhone.toString());
                    return;
                }
                if(!holidayReferences.stream().map(h -> h.getUserPhone()).collect(Collectors.toList()).contains(userPhone)){
                    holidayReferenceService.update(holidayReference);
                    sendMsg("您在" + now + "的请假申请已进入竞选状态,淘汰了用户：" + passedPhone, MsgType.success,userPhone.toString());
                    sendMsg("您在" + now + "的请假被高优先级用户：" + userPhone + "抵消，请重新申请", MsgType.error,passedPhone.toString());
                }else {
                    sendMsg("您在" + now + "已提交过假日申请,请重新申请" , MsgType.error,userPhone.toString());
                }
            }

        });
        return holidayRecordMapper.toDto(holidayRecordRepository.save(resources));
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