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
* @date 2020-06-09
**/
@Entity
@Data
@Table(name="holiday_record")
public class HolidayRecord implements Serializable {

    @Id
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "user_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "用户名")
    private String userName;

    @Column(name = "dept_name")
    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @Column(name = "start_date")
    @ApiModelProperty(value = "请假开始时间")
    private Timestamp startDate;

    @Column(name = "end_date")
    @ApiModelProperty(value = "请假结束时间")
    private Timestamp endDate;

    @Column(name = "count")
    @ApiModelProperty(value = "总共请假天数")
    private Long count;

    @Column(name = "create_time")
    @CreationTimestamp
    @ApiModelProperty(value = "数据创建时间")
    private Timestamp createTime;

    @Column(name = "update_time")
    @UpdateTimestamp
    @ApiModelProperty(value = "数据最近一次修改时间")
    private Timestamp updateTime;

    @Column(name = "status")
    @ApiModelProperty(value = "假期状态")
    private Long status;

    public void copy(HolidayRecord source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}