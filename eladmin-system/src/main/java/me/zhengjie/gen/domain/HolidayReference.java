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

import java.sql.Date;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author fangmin
* @date 2020-06-12
**/
@Entity
@Data
@Table(name="holiday_reference")
public class HolidayReference implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "dept_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @Column(name = "user_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "用户名")
    private String userName;

    @Column(name = "ref_holiday_date",nullable = false)
    @NotNull
    @ApiModelProperty(value = "休息日")
    private Date refHolidayDate;

    @Column(name = "create_by")
    @ApiModelProperty(value = "createBy")
    private String createBy;

    @Column(name = "update_by")
    @ApiModelProperty(value = "updateBy")
    private String updateBy;

    @Column(name = "create_time")
    @CreationTimestamp
    @ApiModelProperty(value = "createTime")
    private Timestamp createTime;

    @Column(name = "update_time")
    @UpdateTimestamp
    @ApiModelProperty(value = "updateTime")
    private Timestamp updateTime;

    @Column(name = "user_phone",nullable = false)
    @NotNull
    @ApiModelProperty(value = "手机号")
    private Long userPhone;

    public void copy(HolidayReference source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}