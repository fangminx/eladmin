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
package me.zhengjie.gen.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
* @website https://el-admin.vip
* @description /
* @author fangmin
* @date 2020-06-28
**/
@Data
public class HolidayPassedRecordDto implements Serializable {

    /** 防止精度丢失 */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /** 对应请假记录表 */
    private Long recordId;

    /** 被抵消者 */
    private String passedUser;

    /** 被抵消者权重 */
    private String passedWeight;

    /** 高优先级用户 */
    private String priorityUser;

    /** 高优先级用户权重 */
    private String priorityWeight;

    /** 部门名称 */
    private String deptName;

    /** 数据创建时间 */
    private Timestamp createTime;

    /** 数据最近一次修改时间 */
    private Timestamp updateTime;
}