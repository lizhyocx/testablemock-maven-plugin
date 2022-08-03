package com.lizhy;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.lizhy.config.GeneratorConfig;
import com.lizhy.config.MockGeneratorTag;
import com.lizhy.config.MockTag;
import com.lizhy.generate.MockGenerate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: lizhiyang03
 * @description:
 * @date: 2022/7/19 10:18
 */
@Mojo(name = "generate", requiresDependencyResolution = ResolutionScope.COMPILE)
public class MockGeneratePlugin extends AbstractMojo {
    @Parameter(property = "configurationFile",
            defaultValue = "${project.basedir}/src/test/resources/testable-mock-generator.xml", required = true)
    protected File configurationFile;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter( defaultValue = "${project.compileClasspathElements}", readonly = true, required = true )
    private List<String> compilePath;

    private ClassLoader classLoader;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        classLoader = getClassLoader(project);
        List<GeneratorConfig> configList = parseGeneratorConfig();
        if (CollectionUtils.isEmpty(configList)) {
            throw new MojoExecutionException("configurationFile 配置不能为空");
        }
        for (GeneratorConfig config : configList) {
            MockGenerate mockGenerate = new MockGenerate(classLoader, config, project.getBasedir().getAbsolutePath(), getLog());
            try {
                mockGenerate.generatorMockClass();
            } catch (ClassNotFoundException e) {
                throw new MojoExecutionException("生成Mock失败", e);
            }
        }
    }

    private List<GeneratorConfig> parseGeneratorConfig() throws MojoExecutionException {
        List<GeneratorConfig> configList = new ArrayList<>();
        if (configurationFile == null) {
            throw new MojoExecutionException("configurationFile 配置不能为空");
        }

        if (!configurationFile.exists()) {
            throw new MojoExecutionException("configurationFile 不是一个文件");
        }
        try {
            XmlMapper xmlMapper = new XmlMapper();
            MockGeneratorTag mockGeneratorTag = xmlMapper.readValue(configurationFile, MockGeneratorTag.class);
            if (mockGeneratorTag != null) {
                for(MockTag mockTag : mockGeneratorTag.getMockTagList()) {
                    GeneratorConfig config = new GeneratorConfig();
                    config.setMockName(mockTag.getName());
                    config.setMockClassName(mockTag.getMockClass());
                    config.setTestClassName(mockTag.getTestClass());
                    configList.add(config);
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("configurationFile 文件有问题", e);
        }
        return configList;
    }

    private ClassLoader getClassLoader(MavenProject project) {
        try {
            // 所有的类路径环境，也可以直接用 compilePath
            List classpathElements = project.getCompileClasspathElements();

            classpathElements.add(project.getBuild().getOutputDirectory());
            classpathElements.add(project.getBuild().getTestOutputDirectory());
            // 转为 URL 数组
            URL urls[] = new URL[classpathElements.size()];
            for (int i = 0; i < classpathElements.size(); ++i) {
                urls[i] = new File((String) classpathElements.get(i)).toURL();
            }
            // 自定义类加载器
            return new URLClassLoader(urls, this.getClass().getClassLoader());
        } catch (Exception e) {
            getLog().debug("Couldn't get the classloader.");
            return this.getClass().getClassLoader();
        }
    }

}
