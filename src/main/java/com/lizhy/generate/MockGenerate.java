package com.lizhy.generate;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.lizhy.config.GeneratorConfig;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: lizhiyang03
 * @description:
 * @date: 2022/7/19 16:24
 */
public class MockGenerate {
    private ClassLoader classLoader;
    private GeneratorConfig config;
    private String destBasePath;
    private Log log;

    public MockGenerate(ClassLoader classLoader, GeneratorConfig generatorConfig, String destBasePath, Log log) {
        this.classLoader = classLoader;
        this.config = generatorConfig;
        this.destBasePath = destBasePath;
        this.log = log;
    }

    public void generatorMockClass() throws MojoExecutionException, ClassNotFoundException {
        List<String> importList = Lists.newArrayList("import com.alibaba.testable.core.annotation.MockInvoke;",
                "import com.alibaba.testable.core.model.MockScope;",
                "import static com.alibaba.testable.core.tool.TestableTool.MOCK_CONTEXT;");

        MockClassGenerate mockClassGenerate = new MockClassGenerate();
        mockClassGenerate.setMockName(config.getMockName());
        mockClassGenerate.setMockClassName(config.getMockClassName());
        mockClassGenerate.setImportList(importList);


        String mockClassName = config.getMockClassName();
        List<String> mockClassList = Splitter.on(",").trimResults().splitToList(mockClassName);
        List<MockMethodGenerate> methodList = Lists.newArrayList();
        for (String className : mockClassList) {
            Class<?> srcClass = classLoader.loadClass(className);
            for (Method method : srcClass.getDeclaredMethods()) {
                if (method.getModifiers() != Modifier.PRIVATE) {
                    methodList.add(generatorMethod(config.getMockName(), srcClass, method));
                }
            }
        }
        mockClassGenerate.setMethodList(methodList);

        TestClassGenerate testClassGenerate = new TestClassGenerate();
        testClassGenerate.setTestClassName(config.getTestClassName());
        testClassGenerate.setMockClassGenerate(mockClassGenerate);

        List<String> writeFileNameList = null;
        try {
            writeFileNameList = mockClassGenerate.generateMockFile(destBasePath);
            for (String fileName : writeFileNameList) {
                log.info("生成文件:" + fileName);
            }
            writeFileNameList = testClassGenerate.generateTestClassFile(destBasePath);
            for (String fileName : writeFileNameList) {
                log.info("生成文件:" + fileName);
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }

    private MockMethodGenerate generatorMethod(String methodTagClassName, Class<?> srcClass, Method srcMethod) {
        String mockInvokeTemplate = "@MockInvoke(targetMethod = \"{0}\",scope = MockScope.GLOBAL)";
        String methodName = srcMethod.getName();
        String returnType = srcMethod.getReturnType().getName();

        String contextKey = methodTagClassName + "MethodTag." + methodName + ".mockMethodKey";
        String mockMethodName = methodName + srcClass.getSimpleName();

        MockMethodGenerate mockMethodGenerate = new MockMethodGenerate();
        mockMethodGenerate.setAnnotation(MessageFormat.format(mockInvokeTemplate, methodName));
        mockMethodGenerate.setMockMethodName(mockMethodName);
        mockMethodGenerate.setOriginMethodName(methodName);
        mockMethodGenerate.setReturnType(returnType);

        List<String> paramList = new ArrayList<>();
        paramList.add(srcClass.getName() + " self");
        for (java.lang.reflect.Parameter parameter : srcMethod.getParameters()) {
            String param = parameter.toString();
            if (param.indexOf("$") > -1) {
                param = param.replaceAll("\\$", ".");
            }
            paramList.add(param);
        }
        mockMethodGenerate.setParamList(paramList);
        // 方法内容
        String returnTypeName = srcMethod.getReturnType().getName();
        Class<?> methodReturnType = srcMethod.getReturnType();
        String returnValue = "null";
        if (methodReturnType == int.class || methodReturnType == long.class || methodReturnType == short.class) {
            returnValue = "1";
        } else if (methodReturnType == boolean.class) {
            returnValue = "true";
        } else if (methodReturnType == float.class || methodReturnType == double.class) {
            returnValue = "0";
        }

        List<String> bodyList = Lists.newArrayList("    if (MOCK_CONTEXT.containsKey(" + contextKey + ")) {",
                "        return (" + returnTypeName + ") MOCK_CONTEXT.get(" + contextKey + ");",
                "    }",
                "    return " + returnValue + ";");

        mockMethodGenerate.setBodyList(bodyList);
        return mockMethodGenerate;
    }
}
