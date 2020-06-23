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

import me.zhengjie.gen.domain.MyDict;
import me.zhengjie.gen.service.dto.ConditionDto;
import me.zhengjie.gen.service.dto.MyDictDto;
import me.zhengjie.gen.service.dto.MyDictQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
public interface MyDictService {

    /**
     * 分页查询
     * @param criteria 条件
     * @param pageable 分页参数
     * @return /
     */
    Map<String,Object> queryAll(MyDictQueryCriteria criteria, Pageable pageable);

    /**
     * 查询全部数据
     * @param dict /
     * @return /
     */
    List<MyDictDto> queryAll(MyDictQueryCriteria dict);

    /**
     * 创建
     * @param resources /
     * @return /
     */
    void create(MyDict resources);

    /**
     * 编辑
     * @param resources /
     */
    void update(MyDict resources);

    /**
     * 删除
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * 导出数据
     * @param queryAll 待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<MyDictDto> queryAll, HttpServletResponse response) throws IOException;

    /**
     * 查询全部级联要用到的条件字段
     * @return
     */
    List<ConditionDto> findAllConditions();


}