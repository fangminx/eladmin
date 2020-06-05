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
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author fangmin
* @date 2020-06-05
**/
@Data
public class ZConfigDto implements Serializable {

    private Long id;

    /** 姓名 */
    private String name;

    /** 基本条件 */
    private String jbtj;

    /** 荣誉条件 */
    private String rytj;

    /** 优惠条件 */
    private String yhtj;

    /** 特殊条件 */
    private String tstj;

    /** 其他条件 */
    private String qttj;

    /** 优先级得分 */
    private Long score;
}