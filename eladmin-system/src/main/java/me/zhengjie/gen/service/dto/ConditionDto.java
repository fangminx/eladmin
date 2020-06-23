package me.zhengjie.gen.service.dto;

import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseDTO;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
//前端要的的级联结果
public class ConditionDto implements Serializable {

//    private Long id;

    private String label;

    private List<Map<String,Object>> children;


}
