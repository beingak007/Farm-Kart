package com.farmkart.agent.service.tools;

/**
 * Safe tool responses — never expose internal exception messages to the LLM/UI chain.
 */
public final class ToolResponseErrors {

    public static final String SERVICE_UNAVAILABLE =
            "{\"success\":false,\"error\":{\"code\":\"SERVICE_UNAVAILABLE\","
                    + "\"message\":\"Service is temporarily unavailable\"}}";

    public static final String NO_DATA =
            "{\"success\":false,\"error\":{\"code\":\"NO_DATA\",\"message\":\"No data returned\"}}";

    public static final String TOOL_FAILED =
            "{\"success\":false,\"error\":{\"code\":\"TOOL_EXECUTION_FAILED\","
                    + "\"message\":\"Tool execution failed\"}}";

    public static final String UNKNOWN_TOOL =
            "{\"success\":false,\"error\":{\"code\":\"UNKNOWN_TOOL\","
                    + "\"message\":\"The requested tool is not available\"}}";

    private ToolResponseErrors() {}
}
