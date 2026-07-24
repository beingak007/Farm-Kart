package com.farmkart.framework.client.dto;

import com.farmkart.framework.client.enums.FkDataType;
import com.farmkart.framework.client.enums.FkViewType;

import java.util.List;
import java.util.Map;

public record GridViewMetadataDTO(
        Long id,
        Long modelId,
        String viewName,
        FkViewType viewType,
        boolean multiSelect,
        boolean editable,
        boolean hideHeaders,
        int rowHeight,
        int pageLimit,
        String paginationType,
        Map<String, Object> config,
        List<GridColumnDTO> columns
) {
    public record GridColumnDTO(
            String fieldName,
            String displayLabel,
            FkDataType dataType,
            boolean visible,
            boolean searchable,
            int sortOrder
    ) {
    }
}
