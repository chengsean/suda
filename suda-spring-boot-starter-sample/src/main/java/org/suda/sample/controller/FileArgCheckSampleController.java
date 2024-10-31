package org.suda.sample.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.suda.sample.common.Constant;
import org.suda.sample.common.Result;

import javax.servlet.http.Part;

/**
 *
 * @author chengshaozhuang
 * @dateTime 2024-10-30 22:14
 */
@RestController
@RequestMapping(Constant.FILE_PREFIX_SERVLET_PATH)
public class FileArgCheckSampleController {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @RequestMapping(value = "/multipartFileCheckWithoutRequestParamAnnotation")
    public Result<?> multipartFileCheckWithoutRequestParamAnnotation(MultipartFile file) {
        printLog(file);
        return Result.OK(file.getOriginalFilename());
    }

    @RequestMapping(value = "/partCheckWithoutRequestParamAnnotation")
    public Result<?> partCheckWithoutRequestParamAnnotation(Part part) {
        printLog(part);
        return Result.OK(part.getSubmittedFileName());
    }

    private void printLog(Object obj) {
        if (obj == null) {
            logger.info("param: null");
        } else if (obj instanceof MultipartFile) {
            MultipartFile file = (MultipartFile) obj;
            logger.info("file name: '{}'，file length: {}", file.getOriginalFilename(), file.getSize());
        } else if (obj instanceof Part) {
            Part part = (Part) obj;
            logger.info("file name: '{}'，file length: {}", part.getSubmittedFileName(), part.getSize());
        } else {
            logger.info("param: {}", obj);
        }
    }
}
