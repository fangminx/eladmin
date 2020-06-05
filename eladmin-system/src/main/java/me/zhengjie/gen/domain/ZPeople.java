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
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author fangmin
* @date 2020-06-05
**/
@Entity
@Data
@Table(name="z_people")
public class ZPeople implements Serializable {

    @Id
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "姓名")
    private String name;

    @Column(name = "age")
    @ApiModelProperty(value = "年龄")
    private Long age;

    @Column(name = "work_age")
    @ApiModelProperty(value = "工龄")
    private Long workAge;

    @Column(name = "holiday_total",nullable = false)
    @NotNull
    @ApiModelProperty(value = "假期总数")
    private Long holidayTotal;

    @Column(name = "remain_holiday_total",nullable = false)
    @NotNull
    @ApiModelProperty(value = "当前剩余假期天数")
    private Long remainHolidayTotal;

    @Column(name = "dept_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "所在部门名称")
    private String deptName;

    public void copy(ZPeople source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}