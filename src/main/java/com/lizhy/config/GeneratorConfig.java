package com.lizhy.config;

import lombok.Data;

/**
 * @author: lizhiyang03
 * @description:
 * @date: 2022/7/19 10:27
 */
@Data
public class GeneratorConfig {
   // mock名
   private String mockName;
   // 需要mock的类，多个用逗号隔开
   private String mockClassName;
   // 被测试类，全路径名，如Repository，Gateway
   private String testClassName;
}
