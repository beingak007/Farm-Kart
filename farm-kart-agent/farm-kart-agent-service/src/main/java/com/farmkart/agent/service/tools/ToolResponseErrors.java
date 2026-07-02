package com.farmkart.agent.service.tools;

import com.farmkart.agent.client.enums.AgentToolErrorCodeEnum;

/**
 * Safe tool responses — never expose internal exception messages to the LLM/UI chain.
 */
public final class ToolResponseErrors {

    public static final String SERVICE_UNAVAILABLE = toJson(
            AgentToolErrorCodeEnum.SERVICE_UNAVAILABLE, "Service is temporarily unavailable");
    public static final String NO_DATA = toJson(
            AgentToolErrorCodeEnum.NO_DATA, "No data returned");
    public static final String TOOL_FAILED = toJson(
            AgentToolErrorCodeEnum.TOOL_EXECUTION_FAILED, "Tool execution failed");
    public static final String UNKNOWN_TOOL = toJson(
            AgentToolErrorCodeEnum.UNKNOWN_TOOL, "The requested tool is not available");

    private ToolResponseErrors() {}

    private static String toJson(AgentToolErrorCodeEnum code, String message) {
        return "{\"success\":false,\"error\":{\"code\":\"" + code.getValue()
                + "\",\"message\":\"" + message + "\"}}";
    }
}
