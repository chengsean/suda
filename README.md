# suda

一款基于SpringMVC的轻量级IO安全插件（spring-boot-starter）。它具有稳定性、易用性、灵活性等特点，能够有效的防范XSS攻击、 
SQL注入以及识别出潜在的恶意文件（篡改文件后缀名）

### System Requirements
suda需要java8或以上，SpringBoot2.x，建议按官方的版本生命周期升级至2.7.x，servlet-api建议使用4.0版本，
具体可参考[SpringBoot2官方文档](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/getting-started.html#getting-started.system-requirements)

### Getting Started
1、添加maven依赖
```xml
 <dependency>
    <groupId>io.github.chengsean</groupId>
    <artifactId>suda-spring-boot-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```
2、修改默认配置
```
suda:
  io:
    chars: 
      trim-enabled: true # 字符串去空格是否启用，默认值: true
    xss-attack: 
      check-enabled: false # XSS攻击安全检查是否启用，默认值: false
      servlet-path-whitelist: '' # XSS攻击安全检查接口白名单，默认空
      xss-regex-list: ''  # XSS攻击检测正则列表
    sql-inject:
      check-enabled: false # SQL注入安全检查是否启用，默认值: false
      servlet-path-whitelist: '' # SQL注入安全检查接口白名单，默认空
      sql-keyword-list: '' # SQL注入检测关键词列表
    files:
      check-enabled: false  # 上传文件安全检查是否启用，默认值: false
      servlet-path-whitelist: '' # 文件安全检查接口白名单，默认空
      extension-blacklist: '' # 文件扩展名黑名单
```
3、完整示例可参考'suda-spring-boot-starter-sample'，详情请到 [gitee](https://gitee.com/chengsean/suda) 或 [github](https://github.com/chengsean/suda)