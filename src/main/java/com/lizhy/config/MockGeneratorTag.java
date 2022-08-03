package com.lizhy.config;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

/**
 * @author: lizhiyang03
 * @description:
 * @date: 2022/7/21 21:30
 */
@Data
@JacksonXmlRootElement(localName = "mock-generator")
public class MockGeneratorTag {
    @JacksonXmlElementWrapper(localName = "mocks")
    @JacksonXmlProperty(localName = "mock")
    List<MockTag> mockTagList;
}
