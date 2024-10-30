package org.suda.sample.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.suda.sample.common.Account;
import org.suda.sample.common.Constant;
import org.suda.sample.common.Result;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author chengshaozhuang
 * @dateTime 2024-10-27 22:14
 */
@RestController
@RequestMapping(Constant.STRING_PREFIX_SERVLET_PATH)
public class StringArgCheckSampleController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/simpleStringCheckWithRequestParamAnnotation", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<?> simpleStringCheckWithRequestParamAnnotation(@RequestParam(name = "sn") Long sn,
                                                                 @RequestParam(name = "id") String id,
                                                                 @RequestParam(name = "name") String name,
                                                                 @RequestParam(name = "email") String email) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("sn", sn);
        map.put("id", id);
        map.put("name", name);
        map.put("email", email);
        printLog(map);
        return Result.OK(map);
    }

    @RequestMapping(value = "/mapStringCheckWithRequestParamAnnotation", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<?> mapStringCheckWithRequestParamAnnotation(@RequestParam Map<String, String> map) {
        printLog(map);
        return Result.OK(map);
    }

    @RequestMapping(value = "/accountStringCheckWithoutModelAttributeAnnotation", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<?> accountStringCheckWithoutAnnotation(Account account) {
        printLog(account);
        return Result.OK(account);
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private void printLog(Object obj) {
        if (obj == null) {
            logger.info("param: null");
        } else if (obj instanceof String) {
            logger.info("param: '{}'，String length after trim: {}", obj, obj.toString().length());
        } else if (obj instanceof Map) {
            Map<String, Object> map = (Map) obj;
            logger.info("Map name: '{}', Map value length  after trim: {}",
                    map.get(Constant.NAME_KEY), Objects.toString(map.get(Constant.NAME_KEY), "").length());
        } else if (obj instanceof Account) {
            Account account = (Account) obj;
            logger.info("Account name: '{}', Account name length  after trim: {}",
                    account.getName(), account.getName().length());
        } else {
            logger.info("param: {}", obj);
        }
    }
}
