package com.farmkart.rest.controller;

import com.farmkart.framework.client.dto.*;
import com.farmkart.framework.client.enums.FkDataType;
import com.farmkart.framework.client.enums.FkViewType;
import com.farmkart.framework.service.FrameworkMetadataService;
import com.farmkart.starter.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Framework metadata API — lets the frontend dynamically render grid views
 * and upload-mapping screens without any hard-coded column lists.
 *
 * Analogous to the FrameworkController pattern in the Farm Kart view-framework.
 */
@RestController
@RequestMapping("/api/v1/framework")
@Tag(name = "Framework Metadata", description = "Dynamic view and model metadata used by the frontend")
public class FrameworkController {

    private final FrameworkMetadataService frameworkMetadataService;

    public FrameworkController(FrameworkMetadataService frameworkMetadataService) {
        this.frameworkMetadataService = frameworkMetadataService;
    }

    // ── Model endpoints ────────────────────────────────────────────────────

    @GetMapping("/models")
    @Operation(summary = "List all registered models (summary)")
    public ApiResponse<List<ModelMetadataDTO>> listModels() {
        return ApiResponse.ok(frameworkMetadataService.listModels());
    }

    @GetMapping("/models/{modelName}")
    @Operation(summary = "Get model metadata with all fields")
    public ApiResponse<ModelMetadataDTO> getModel(@PathVariable String modelName) {
        return ApiResponse.ok(frameworkMetadataService.getModel(modelName));
    }

    @GetMapping("/models/{modelName}/fields")
    @Operation(summary = "Get all fields for a model")
    public ApiResponse<List<ModelFieldDTO>> getModelFields(@PathVariable String modelName) {
        return ApiResponse.ok(frameworkMetadataService.getModelFields(modelName));
    }

    @GetMapping("/models/{modelName}/fields/searchable")
    @Operation(summary = "Get searchable fields (used by search/filter UI)")
    public ApiResponse<List<ModelFieldDTO>> getSearchableFields(@PathVariable String modelName) {
        return ApiResponse.ok(frameworkMetadataService.getSearchableFields(modelName));
    }

    @GetMapping("/models/{modelName}/fields/by-type/{dataType}")
    @Operation(summary = "Get model fields filtered by data type (e.g. DATE, DECIMAL)")
    public ApiResponse<List<ModelFieldDTO>> getFieldsByDataType(
            @PathVariable String modelName,
            @PathVariable FkDataType dataType) {
        return ApiResponse.ok(frameworkMetadataService.getModelFieldsByDataType(modelName, dataType));
    }

    // ── View endpoints ─────────────────────────────────────────────────────

    @GetMapping("/models/{modelName}/views")
    @Operation(summary = "List view names registered for a model")
    public ApiResponse<List<String>> getViewNames(@PathVariable String modelName) {
        return ApiResponse.ok(frameworkMetadataService.getViewNames(modelName));
    }

    @GetMapping("/models/{modelName}/views/by-type/{viewType}")
    @Operation(summary = "List views for a model filtered by view type")
    public ApiResponse<List<ViewMetadataDTO>> getViewsByType(
            @PathVariable String modelName,
            @PathVariable FkViewType viewType) {
        return ApiResponse.ok(frameworkMetadataService.getViewsByType(modelName, viewType));
    }

    @GetMapping("/models/{modelName}/views/{viewName}")
    @Operation(summary = "Get lightweight view metadata (column list only)")
    public ApiResponse<ViewMetadataDTO> getView(
            @PathVariable String modelName,
            @PathVariable String viewName) {
        return ApiResponse.ok(frameworkMetadataService.getViewMetadata(modelName, viewName));
    }

    @GetMapping("/models/{modelName}/views/{viewName}/grid")
    @Operation(summary = "Get full grid-view metadata (columns + actions + config) for dynamic rendering")
    public ApiResponse<GridViewMetadataDTO> getGridView(
            @PathVariable String modelName,
            @PathVariable String viewName) {
        return ApiResponse.ok(frameworkMetadataService.getGridViewMetadata(modelName, viewName));
    }
}
