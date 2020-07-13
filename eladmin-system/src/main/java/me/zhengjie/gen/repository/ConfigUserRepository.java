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

import me.zhengjie.gen.domain.ConfigUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
* @website https://el-admin.vip
* @author fangmin
* @date 2020-06-18
**/
public interface ConfigUserRepository extends JpaRepository<ConfigUser, Long>, JpaSpecificationExecutor<ConfigUser> {
    List<ConfigUser> findByUserName(String userName);

    @Query(value = "SELECT c.* FROM config_user c WHERE" +
            " c.user_name = ?1 AND c.condition_weight <> 0 AND c.condition_weight is not NULL", nativeQuery = true)
    List<ConfigUser> findForShowWeightDetail(String userName);
}