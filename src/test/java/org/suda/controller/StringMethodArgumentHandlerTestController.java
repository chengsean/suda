package org.suda.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.suda.common.Constant;
import org.suda.common.Result;

/**
 * 字符串入参安全检查单元测试接口
 * @author chengshaozhuang
 */
@RestController
@RequestMapping(Constant.PREFIX_SERVLET_PATH)
public class StringMethodArgumentHandlerTestController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/requestParamWithStringTrimWithNoAnnotation", method = RequestMethod.GET)
    public Result<?> requestParamWithStringNoAnnotation(String name) {
        logger.info("Hello {}", name);
        logger.info("参数名：{}，参数值：{}，长度：{}", "name", name, name.length());
        return Result.OK(name);
    }
}
