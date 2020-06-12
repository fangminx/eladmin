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

import java.sql.Date;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author fangmin
* @date 2020-06-12
**/
@Data
public class HolidayReferenceDto implements Serializable {

    private Long id;

    /** 部门名称 */
    private String deptName;

    /** 用户名 */
    private String userName;

    /** 休息日 */
    private Date refHolidayDate;

    private String createBy;

    private String updateBy;

    private Timestamp createTime;

    private Timestamp updateTime;

    /** 手机号 */
    private Long userPhone;
}