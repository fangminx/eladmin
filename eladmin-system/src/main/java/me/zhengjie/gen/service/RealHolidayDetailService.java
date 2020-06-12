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
package me.zhengjie.gen.service;


import me.zhengjie.gen.domain.MyDictDetail;
import me.zhengjie.gen.domain.RealHoliday;
import me.zhengjie.gen.domain.RealHolidayDetail;
import me.zhengjie.gen.service.dto.MyDictDetailDto;
import me.zhengjie.gen.service.dto.MyDictDetailQueryCriteria;
import me.zhengjie.gen.service.dto.RealHolidayDetailDto;
import me.zhengjie.gen.service.dto.RealHolidayDetailQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
public interface RealHolidayDetailService {

    /**
     * 创建
     * @param resources /
     */
    void create(RealHolidayDetail resources);

    /**
     * 编辑
     * @param resources /
     */
    void update(RealHolidayDetail resources);

    /**
     * 删除
     * @param id /
     */
    void delete(Long id);

    /**
     * 分页查询
     * @param criteria 条件
     * @param pageable 分页参数
     * @return /
     */
    Map<String,Object> queryAll(RealHolidayDetailQueryCriteria criteria, Pageable pageable);

    /**
     * 根据字典名称获取字典详情
     * @param name 字典名称
     * @return /
     */
    List<RealHolidayDetailDto> getRealHolidayByName(String name);
}