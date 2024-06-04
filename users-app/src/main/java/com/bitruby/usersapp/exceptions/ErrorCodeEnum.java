package com.bitruby.usersapp.exceptions;


public enum ErrorCodeEnum {
    UNSPECIFIED_ERROR("10"),
    MAPPING_ERROR("11"),
    GET_BY_ID_ERROR("12"),
    IDEMPOTENCE_ERROR("13"),

    CAMUNDA_CONNECTION_ERROR("14");

    private final String code;

    ErrorCodeEnum(String code) {
        this.code = code;
    }

    public String getEnumCode() {
        return code;
    }

    public static ErrorCodeEnum getErrorCodeValueByCode(int code) {
        for (ErrorCodeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new BitrubyRuntimeExpection("Unknown code of error " + code);
    }
}
