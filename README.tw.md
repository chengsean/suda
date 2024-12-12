### Language
- [簡體中文](README.md)
- [English](README.en.md)

# suda

一款基於SpringMVC的羽量型的IO安全外掛程式（spring-boot-starter）。 它具有穩定性、易用性、靈活性等特點，能夠有效的防範XSS攻擊、
SQL注入以及識別出潛在的惡意檔（篡改文件後綴名）

### System Requirements
suda需要java8或以上，SpringBoot2.x，建議按官方的版本生命周期升級至2.7.x，servlet-api建議使用4.0版本，
具體可參考[SpringBoot2官方文件](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/getting-started.html#getting-started.system-requirements)

### Getting Started
1、添加maven依賴
```xml
 <dependency>
    <groupId>io.github.chengsean</groupId>
    <artifactId>suda-spring-boot-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```
2、修改預設配置
```
suda:
  io:
    chars: 
      trim-enabled: true # 字串去空格是否啟用，預設值： true
    xss-attack: 
      check-enabled: false # XSS攻擊安全檢查是否啟用，預設值： false
      servlet-path-whitelist: '' # XSS攻擊安全檢查路徑白名單，預設空
      xss-regex-list: ''  # XSS攻擊檢測正則清單
    sql-inject:
      check-enabled: false # SQL注入安全檢查是否啟用，預設值： false
      servlet-path-whitelist: '' # SQL注入安全檢查路徑白名單，預設空
      sql-keyword-list: '' # SQL注入檢測關鍵詞清單
    files:
      check-enabled: false  # 上傳檔安全檢查是否啟用，預設值： false
      servlet-path-whitelist: '' # 檔安全檢查路徑白名單，預設空
      extension-blacklist: '' # 檔擴展名黑名單
```
3、完整示例可參考『suda-spring-boot-starter-sample』模組，詳情請到 [gitee](https://gitee.com/chengsean/suda) 或 [github](https://github.com/chengsean/suda)