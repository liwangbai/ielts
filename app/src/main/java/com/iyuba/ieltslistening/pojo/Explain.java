package com.iyuba.ieltslistening.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

// 题目的解析
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Explain {
    private int testType;
    private int partType;
    private int quesIndex;
    private int titleNum;
    private String explain;
}
