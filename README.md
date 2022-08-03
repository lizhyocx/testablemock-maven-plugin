# testablemock-maven-plugin

生成testablemock Mock类的maven插件
1. src/test/resource下新建testable-mock-generator.xml，内容如下：
2. 
    <mock-generator>
    <mocks>
        <mock>
            <name>com.lizhy.mock.ShareInfoMock</name>
            <test-class>com.lizhy.repository.ShareInfoRepository</test-class>
            <mock-class>com.lizhy.dao.ShareInfoMapper</mock-class>
        </mock>
        <mock>
            <name>com.lizhy.mock.HandlerMock</name>
            <test-class></test-class>
            <mock-class>com.lizhy.gateway.OneGateway, com.lizhy.gateway.TwoGateway</mock-class>
        </mock>
    </mocks>
</mock-generator>

2. 执行插件，生成Mock类和测试类，如上会再com.lizhy.mock包下生成：ShareInfoMock、ShareInfoMockMethodTag、ShareInfoRepositoryTest类，分别为：Mock类、Mock常量类、测试类。
