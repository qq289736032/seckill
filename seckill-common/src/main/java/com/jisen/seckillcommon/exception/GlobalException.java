package com.jisen.seckillcommon.exception;

import com.jisen.seckillcommon.result.CodeMsg;

/**
 * @author jisen
 * @date 2019/6/12 19:58
 */
public class GlobalException extends RuntimeException {
    private CodeMsg codeMsg;

    /**
     * 使用构造器接收CodeMsg
     *
     * @param codeMsg
     */
    public GlobalException(CodeMsg codeMsg) {
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
