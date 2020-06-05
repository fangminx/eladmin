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
@Table(name="z_config")
public class ZConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "姓名")
    private String name;

    @Column(name = "jbtj")
    @ApiModelProperty(value = "基本条件")
    private String jbtj;

    @Column(name = "rytj")
    @ApiModelProperty(value = "荣誉条件")
    private String rytj;

    @Column(name = "yhtj")
    @ApiModelProperty(value = "优惠条件")
    private String yhtj;

    @Column(name = "tstj")
    @ApiModelProperty(value = "特殊条件")
    private String tstj;

    @Column(name = "qttj")
    @ApiModelProperty(value = "其他条件")
    private String qttj;

    @Column(name = "score")
    @ApiModelProperty(value = "优先级得分")
    private Long score;

    public void copy(ZConfig source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}