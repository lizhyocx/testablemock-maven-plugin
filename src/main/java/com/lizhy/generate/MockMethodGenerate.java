package com.lizhy.generate;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @author: lizhiyang03
 * @description:
 * @date: 2022/7/19 16:33
 */
@Data
public class MockMethodGenerate {
    private String annotation;
    private String mockMethodName;
    private String originMethodName;
    private String returnType;
    private List<String> paramList;
    private List<String> bodyList;

    public List<String> generateContent() {
        List<String> list = Lists.newArrayList();
        list.add(annotation);
        String paramString = Joiner.on(" ,").join(paramList);
        String methodDefine = "public " + returnType +" " + mockMethodName +" (" + paramString +")";
        list.add(methodDefine +" {");
        list.addAll(bodyList);
        list.add("}");
        return list;
    }
}
