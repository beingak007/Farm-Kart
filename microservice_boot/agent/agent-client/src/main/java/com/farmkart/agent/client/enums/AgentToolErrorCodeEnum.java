package com.farmkart.agent.client.enums;

import com.farmkart.starter.common.enums.StringValuedEnum;
import com.farmkart.starter.common.enums.StringValuedEnumSupport;

public enum AgentToolErrorCodeEnum implements StringValuedEnum {

    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE"),
    NO_DATA("NO_DATA"),
    TOOL_EXECUTION_FAILED("TOOL_EXECUTION_FAILED"),
    UNKNOWN_TOOL("UNKNOWN_TOOL");

    private final String value;

    AgentToolErrorCodeEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static AgentToolErrorCodeEnum getAgentToolErrorCodeEnum(String value) {
        return StringValuedEnumSupport.fromValue(AgentToolErrorCodeEnum.class, value);
    }
}
