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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseEntity;
import me.zhengjie.modules.system.domain.Dict;
import me.zhengjie.modules.system.domain.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@Entity
@Getter
@Setter
@Table(name="my_dict_detail")
public class MyDictDetail extends BaseEntity implements Serializable {

    @Id
    @Column(name = "id")
    @NotNull(groups = Update.class)
    @ApiModelProperty(value = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "dict_id")
    @ManyToOne(fetch=FetchType.LAZY)
    @ApiModelProperty(value = "字典", hidden = true)
    private MyDict dict;

    @ApiModelProperty(value = "字典标签")
    private String label;

    @ApiModelProperty(value = "字典值")
    private String value;

    @ApiModelProperty(value = "权重")
    private String weight;

    @ApiModelProperty(value = "排序")
    private Integer dictSort = 999;

//    @JsonIgnore
//    @ManyToMany(mappedBy = "rytjs")
//    @ApiModelProperty(value = "用户", hidden = true)
//    private Set<User> users;
//
//    @JsonIgnore
//    @ManyToMany(mappedBy = "yhtjs")
//    @ApiModelProperty(value = "用户", hidden = true)
//    private Set<User> users2;
}