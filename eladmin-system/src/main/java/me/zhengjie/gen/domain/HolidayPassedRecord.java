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
package me.zhengjie.gen.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author fangmin
* @date 2020-06-28
**/
@Entity
@Data
@Table(name="holiday_passed_record")
public class HolidayPassedRecord implements Serializable {

    @Id
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "record_id")
    @ApiModelProperty(value = "对应请假记录表")
    private Long recordId;

    @Column(name = "passed_user")
    @ApiModelProperty(value = "被抵消者")
    private String passedUser;

    @Column(name = "passed_weight")
    @ApiModelProperty(value = "被抵消者权重")
    private String passedWeight;

    @Column(name = "priority_user")
    @ApiModelProperty(value = "高优先级用户")
    private String priorityUser;

    @Column(name = "priority_weight")
    @ApiModelProperty(value = "高优先级用户权重")
    private String priorityWeight;

    @Column(name = "dept_name")
    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @Column(name = "create_time")
    @CreationTimestamp
    @ApiModelProperty(value = "数据创建时间")
    private Timestamp createTime;

    @Column(name = "update_time")
    @UpdateTimestamp
    @ApiModelProperty(value = "数据最近一次修改时间")
    private Timestamp updateTime;

    public void copy(HolidayPassedRecord source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}