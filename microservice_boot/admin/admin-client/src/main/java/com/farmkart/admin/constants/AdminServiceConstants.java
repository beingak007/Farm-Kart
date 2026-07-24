package com.farmkart.admin.constants;

import com.farmkart.starter.common.enums.FkServiceNameEnum;

public final class AdminServiceConstants {

    public static final FkServiceNameEnum SERVICE_NAME = FkServiceNameEnum.ADMIN;
    public static final String SERVICE_ORIGIN = SERVICE_NAME.getValue();
    public static final String KAFKA_GROUP_AUDIT = "admin-audit";

    private AdminServiceConstants() {}
}
