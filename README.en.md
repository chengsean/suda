### Language
- [简体中文](README.md)
- [繁体中文](README.tw.md)

# suda

A lightweight I/O security plug-in (spring-boot-starter) based on SpringMVC. It has the characteristics of stability, ease of use, flexibility, etc., and can effectively prevent XSS attacks,
SQL injection and identification of potentially malicious files (tampering with file suffixes)

### System Requirements
suda requires java8 or above, SpringBoot 2.x, it is recommended to upgrade to 2.7.x according to the official version lifecycle, and servlet-api is recommended to use version 4.0,
For details, please refer to it [SpringBoot2 documentation
](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/getting-started.html#getting-started.system-requirements)

### Getting Started
1、Add Maven dependencies to the pom
```xml
 <dependency>
    <groupId>io.github.chengsean</groupId>
    <artifactId>suda-spring-boot-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```
2、Modify the default configuration
```
suda:
  io:
    chars: 
      trim-enabled: true # Whether to enable blank space for strings, default value: true
    xss-attack: 
      check-enabled: false # Whether the XSS attack security check is enabled, default: false
      servlet-path-whitelist: '' # The whitelist of XSS attack security check servletPath is empty by default
      xss-regex-list: ''  # Regular list of XSS attacks
    sql-inject:
      check-enabled: false # Whether the SQL injection security check is enabled, default value: false
      servlet-path-whitelist: '' # The whitelist of SQL injection security check servletPath is empty by default
      sql-keyword-list: '' # SQL injection keyword list
    files:
      check-enabled: false  # Whether to enable security check for uploaded files, default value: false
      servlet-path-whitelist: '' # The whitelist of file security check servletPath is empty by default
      extension-blacklist: '' # File extension blacklist
```
3、For a complete example, please refer to the 'suda-spring-boot-starter-sample' module