package com.farmkart.framework.client.dto;

import java.util.List;

/**
 * Metadata for a framework model (analogous to {@code FarmKartModelDTO}).
 * Used by the frontend to understand the shape of a data entity and
 * map upload columns, form fields, etc.
 */
public record ModelMetadataDTO(
        Long id,
        String modelName,
        String displayName,
        String description,
        List<ModelFieldDTO> fields   // null when fetched as a list summary
) {
    /** Convenience constructor without fields (for list responses). */
    public ModelMetadataDTO(Long id, String modelName, String displayName, String description) {
        this(id, modelName, displayName, description, null);
    }
}
