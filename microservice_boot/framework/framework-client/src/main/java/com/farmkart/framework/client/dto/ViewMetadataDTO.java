package com.farmkart.framework.client.dto;

import com.farmkart.framework.client.enums.FkDataType;
import com.farmkart.framework.client.enums.FkViewType;

import java.util.List;

/**
 * Lightweight view metadata — use {@link GridViewMetadataDTO} when you need
 * the full column-level detail for rendering a grid.
 *
 * Kept as a simple record for backward-compat with the existing FrameworkController.
 */
public record ViewMetadataDTO(
        Long id,
        Long modelId,
        String viewName,
        FkViewType viewType,
        List<ViewFieldDTO> fields
) {
    /**
     * One column descriptor inside a lightweight view.
     */
    public record ViewFieldDTO(
            String fieldName,
            String displayLabel,
            FkDataType dataType,
            boolean visible,
            boolean searchable
    ) {}
}
