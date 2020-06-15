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
package me.zhengjie.gen.repository;

import me.zhengjie.gen.domain.HolidayReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;

/**
* @website https://el-admin.vip
* @author fangmin
* @date 2020-06-12
**/
public interface HolidayReferenceRepository extends JpaRepository<HolidayReference, Long>, JpaSpecificationExecutor<HolidayReference> {

    /**
     * 判断当天部门有多少请假记录
     */
    Long countByDeptNameAndRefHolidayDate(String name, Date date);


    /**
     * 查询当天部门所有请假记录
     */
    List<HolidayReference> findAllByDeptNameAndRefHolidayDateOrderByUpdateTimeAsc(String name, Date date);


    @Modifying
    @Query(value = "update holiday_reference set user_name = ?2 , user_phone = ?3 where id = ?1",nativeQuery = true)
    void updateReference(Long id, String userName, String userPhone);

}