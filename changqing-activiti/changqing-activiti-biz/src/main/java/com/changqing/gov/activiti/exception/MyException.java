package com.changqing.gov.activiti.exception;

import com.changqing.gov.exception.BizException;
import com.changqing.gov.exception.code.BaseExceptionCode;

/**
 * 异常抛出
 *
 * @author wz
 * @date 2020-08-21 13:25
 */
public class MyException {
    /**
     * 抛出异常
     * @param ex 异常类型
     * @param <T>
     */
    public static <T extends BaseExceptionCode> void exception(T ex) {
        throw new BizException(ex.getCode(), ex.getMsg());
    }
}
