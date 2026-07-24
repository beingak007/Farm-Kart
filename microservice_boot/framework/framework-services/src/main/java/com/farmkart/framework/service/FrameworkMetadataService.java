package com.farmkart.framework.service;

import com.farmkart.framework.client.dto.*;
import com.farmkart.framework.client.enums.FkDataType;
import com.farmkart.framework.client.enums.FkViewType;
import com.farmkart.framework.service.view.IFarmKartViewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Facade service used by the REST layer.
 * Delegates to {@link IFarmKartViewService} (implemented by {@code GridViewService}).
 * Analogous to {@code FrameworkMetadataService} but fully expanded to expose the
 * complete model-field + grid-view API.
 */
@Service
@Transactional(readOnly = true, transactionManager = "frameworkTransactionManager")
public class FrameworkMetadataService {

    private final IFarmKartViewService viewService;

    public FrameworkMetadataService(IFarmKartViewService viewService) {
        this.viewService = viewService;
    }

    // ── Models ─────────────────────────────────────────────────────────────

    public List<ModelMetadataDTO> listModels() {
        return viewService.listModels();
    }

    public ModelMetadataDTO getModel(String modelName) {
        return viewService.getModel(modelName);
    }

    public List<ModelFieldDTO> getModelFields(String modelName) {
        return viewService.getModelFields(modelName);
    }

    public List<ModelFieldDTO> getModelFieldsByDataType(String modelName, FkDataType dataType) {
        return viewService.getModelFieldsByDataType(modelName, dataType);
    }

    public List<ModelFieldDTO> getSearchableFields(String modelName) {
        return viewService.getSearchableFields(modelName);
    }

    // ── Views ──────────────────────────────────────────────────────────────

    /** Lightweight view metadata (no deep column settings). */
    public ViewMetadataDTO getViewMetadata(String modelName, String viewName) {
        return viewService.getViewMetadata(modelName, viewName);
    }

    /** Full grid-view metadata for dynamic table rendering. */
    public GridViewMetadataDTO getGridViewMetadata(String modelName, String viewName) {
        return viewService.getGridViewMetadata(modelName, viewName);
    }

    public List<String> getViewNames(String modelName) {
        return viewService.getViewNames(modelName);
    }

    public List<ViewMetadataDTO> getViewsByType(String modelName, FkViewType viewType) {
        return viewService.getViewsByType(modelName, viewType);
    }
}
