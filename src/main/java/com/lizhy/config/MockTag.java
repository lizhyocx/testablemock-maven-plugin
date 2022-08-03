package com.lizhy.config;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * @author: lizhiyang03
 * @description:
 * @date: 2022/7/21 21:30
 */
@Data
public class MockTag {
    @JacksonXmlProperty(localName = "name")
    private String name;
    @JacksonXmlProperty(localName = "test-class")
    private String testClass;
    @JacksonXmlProperty(localName = "mock-class")
    private  String mockClass;
}
