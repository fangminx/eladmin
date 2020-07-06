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

import me.zhengjie.base.BaseEntity;
import me.zhengjie.gen.domain.HolidayRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
* @website https://el-admin.vip
* @author fangmin
* @date 2020-06-12
**/
public interface HolidayRecordRepository extends JpaRepository<HolidayRecord, Long>, JpaSpecificationExecutor<HolidayRecord> {

    List<HolidayRecord> findByDeptName(String deptName);

    @Modifying
    @Query(value = "update holiday_record set status = ?2 where id = ?1",nativeQuery = true)
    void updateStatusById(Long id, String status);

    List<HolidayRecord> findByUserNameAndStatus(String userName,String status);

    List<HolidayRecord> findByUserNameAndResult(String userName,String result);
}