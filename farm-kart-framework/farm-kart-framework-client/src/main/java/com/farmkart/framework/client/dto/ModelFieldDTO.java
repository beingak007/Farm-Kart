package com.farmkart.framework.client.dto;

import com.farmkart.framework.client.enums.FkDataType;

public record ModelFieldDTO(
        Long id,
        String fieldName,
        String displayLabel,
        FkDataType dataType,
        boolean searchable,
        int uploadOrder
) {
}
