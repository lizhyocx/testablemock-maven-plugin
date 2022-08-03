package com.lizhy.generate;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.lizhy.util.FileUtil;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: lizhiyang03
 * @description:
 * @date: 2022/7/22 10:03
 */
@Data
public class MockClassGenerate {
    private String mockName;
    private String mockClassName;
    private List<String> importList;
    private List<MockMethodGenerate> methodList;

    public List<String> generateMockFile(String destBasePath) throws IOException {
        String destPathName = destBasePath + "/src/test/java";

        int index = mockName.lastIndexOf(".");
        String mockPackage = mockName.substring(0, index);
        String mockDir = Joiner.on("/").join(Arrays.stream(mockPackage.split("\\.")).collect(Collectors.toList()));

        String simpleMockName = mockName.substring(index + 1);
        // 生成Mock类
        String mockFileName = destPathName + "/" + mockDir +"/" + simpleMockName + ".java";
        File mockFile = new File(mockFileName);
        FileUtil.writeToFile(mockFile, generateMock(mockPackage, simpleMockName));

        // 生成MethodTag类
        String commonTagFileName = destPathName + "/" + mockDir +"/" + simpleMockName + "MethodTag.java";
        File commonTagFile = new File(commonTagFileName);
        FileUtil.writeToFile(commonTagFile, generateMethodTag(mockPackage, simpleMockName));

        return Lists.newArrayList(mockFileName, commonTagFileName);
    }

    private String generateMock(String packageName, String className) {
        List<String> list = Lists.newArrayList();
        list.add("package " + packageName +";");
        list.addAll(importList);
        list.add("public class " + className +" {");
        for(MockMethodGenerate methodGenerate : methodList) {
            List<String> methodList = methodGenerate.generateContent();
            methodList = methodList.stream().map(str -> str = "    " + str).collect(Collectors.toList());
            list.addAll(methodList);
        }
        list.add("}");
        return Joiner.on("\n").join(list);
    }

    private String generateMethodTag(String packageName, String className) {
        List<String> list = Lists.newArrayList();
        list.add("package " + packageName + ";");
        list.add("public class " + className + "MethodTag {");
        for (MockMethodGenerate methodGenerate : methodList) {
            list.add("    public static class " + methodGenerate.getOriginMethodName() +" {");
            list.add("        public static String methodName = \""+methodGenerate.getOriginMethodName()+"\";");
            list.add("        public static String mockMethodName = \""+methodGenerate.getMockMethodName()+"\";");
            list.add("        public static String mockMethodKey = \"" + mockClassName + "." + methodGenerate.getOriginMethodName() + "\";");
            list.add("    }");
        }
        list.add("}");

        return Joiner.on("\n").join(list);
    }
}
