package com.iogogogo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by tao.zeng on 2020/6/3.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BizException extends RuntimeException {

    private int code;

    private String message;

    public BizException(String message) {
        super(message);
        this.message = message;
    }
}
