package com.lizhy.generate;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.lizhy.util.FileUtil;
import lombok.Data;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: lizhiyang03
 * @description:
 * @date: 2022/7/22 10:05
 */
@Data
public class TestClassGenerate {
    private String testClassName;
    private MockClassGenerate mockClassGenerate;

    public List<String> generateTestClassFile(String destBasePath) throws IOException {
        List<String> fileList = Lists.newArrayList();
        if (StringUtils.isNotBlank(testClassName)) {
            String destPathName = destBasePath + "/src/test/java";
            int index = testClassName.lastIndexOf(".");
            String testPackage = testClassName.substring(0, index);
            String testDir = Joiner.on("/").join(Arrays.stream(testPackage.split("\\.")).collect(Collectors.toList()));

            String simpleTestName = testClassName.substring(index + 1);

            // 生成Test类
            String mockFileName = destPathName + "/" + testDir +"/" + simpleTestName + "Test.java";
            File mockFile = new File(mockFileName);
            FileUtil.writeToFile(mockFile, generateTest(testPackage, simpleTestName + "Test"));
            fileList.add(mockFileName);
        }
        return fileList;
    }

    private String generateTest(String packageName, String className) {
        List<String> list = Lists.newArrayList();
        list.add("package " + packageName + ";");
        list.add("public class " + className + " {");
        list.add("    public static class Mock extends " + mockClassGenerate.getMockName() +" { ");
        list.add("    }");
        list.add("}");
        return Joiner.on("\n").join(list);
    }
}
