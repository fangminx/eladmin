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

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@Entity
@Getter
@Setter
@Table(name="holiday_real_detail")
public class RealHolidayDetail extends BaseEntity implements Serializable {

    @Id
    @Column(name = "id")
    @NotNull(groups = Update.class)
    @ApiModelProperty(value = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "real_id")
    @ManyToOne(fetch=FetchType.LAZY)
    @ApiModelProperty(value = "所在部门", hidden = true)
    private RealHoliday realHoliday;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "参考假日")
    private Timestamp refHoliday;

}