package com.mark.framework.exception;

import com.mark.framework.exception.base.MyBaseException;

/**
 *
 * @author mark
 * @date 2017-12-06
 */
public class OptimisticLockException extends MyBaseException {
    public OptimisticLockException(String msg) {
        super(msg);
    }
}
