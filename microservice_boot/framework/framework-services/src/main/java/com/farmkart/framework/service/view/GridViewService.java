package com.farmkart.framework.service.view;

import com.farmkart.framework.client.dto.*;
import com.farmkart.framework.client.enums.FkDataType;
import com.farmkart.framework.client.enums.FkViewType;
import com.farmkart.framework.repository.FrameworkModelRepository;
import com.farmkart.framework.repository.FrameworkViewRepository;
import com.farmkart.framework.repository.entity.FrameworkModel;
import com.farmkart.framework.repository.entity.FrameworkView;
import com.farmkart.framework.repository.entity.FrameworkViewField;
import com.farmkart.starter.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true, transactionManager = "frameworkTransactionManager")
public class GridViewService implements IFarmKartViewService {

    private final FrameworkModelRepository modelRepository;
    private final FrameworkViewRepository viewRepository;

    public GridViewService(FrameworkModelRepository modelRepository, FrameworkViewRepository viewRepository) {
        this.modelRepository = modelRepository;
        this.viewRepository = viewRepository;
    }

    @Override
    public List<ModelMetadataDTO> listModels() {
        return modelRepository.findAll().stream()
                .map(model -> new ModelMetadataDTO(model.getId(), model.getModelName(), model.getDisplayName(), model.getDescription()))
                .toList();
    }

    @Override
    public ModelMetadataDTO getModel(String modelName) {
        FrameworkModel model = requireModel(modelName);
        return new ModelMetadataDTO(
                model.getId(),
                model.getModelName(),
                model.getDisplayName(),
                model.getDescription(),
                getModelFields(modelName));
    }

    @Override
    public List<ModelFieldDTO> getModelFields(String modelName) {
        requireModel(modelName);
        return collectUniqueFields(viewRepository.findByModel_ModelName(modelName));
    }

    @Override
    public List<ModelFieldDTO> getModelFieldsByDataType(String modelName, FkDataType dataType) {
        return getModelFields(modelName).stream()
                .filter(field -> field.dataType() == dataType)
                .toList();
    }

    @Override
    public List<ModelFieldDTO> getSearchableFields(String modelName) {
        return getModelFields(modelName).stream()
                .filter(ModelFieldDTO::searchable)
                .toList();
    }

    @Override
    public ViewMetadataDTO getViewMetadata(String modelName, String viewName) {
        FrameworkView view = requireView(modelName, viewName);
        return toViewMetadata(view);
    }

    @Override
    public GridViewMetadataDTO getGridViewMetadata(String modelName, String viewName) {
        FrameworkView view = requireView(modelName, viewName);
        return toGridViewMetadata(view);
    }

    @Override
    public List<String> getViewNames(String modelName) {
        requireModel(modelName);
        return viewRepository.findByModel_ModelName(modelName).stream()
                .map(FrameworkView::getViewName)
                .toList();
    }

    @Override
    public List<ViewMetadataDTO> getViewsByType(String modelName, FkViewType viewType) {
        requireModel(modelName);
        return viewRepository.findByModel_ModelNameAndViewType(modelName, viewType).stream()
                .map(this::toViewMetadata)
                .toList();
    }

    private FrameworkModel requireModel(String modelName) {
        return modelRepository.findByModelName(modelName)
                .orElseThrow(() -> new BusinessException(404, "Model not found: " + modelName));
    }

    private FrameworkView requireView(String modelName, String viewName) {
        return viewRepository.findByModel_ModelNameAndViewName(modelName, viewName)
                .orElseThrow(() -> new BusinessException(404, "View not found: " + modelName + "/" + viewName));
    }

    private List<ModelFieldDTO> collectUniqueFields(List<FrameworkView> views) {
        Map<String, ModelFieldDTO> fields = new LinkedHashMap<>();
        for (FrameworkView view : views) {
            for (FrameworkViewField field : view.getFields()) {
                fields.putIfAbsent(field.getFieldName(), toModelField(field));
            }
        }
        return new ArrayList<>(fields.values());
    }

    private ModelFieldDTO toModelField(FrameworkViewField field) {
        return new ModelFieldDTO(
                field.getId(),
                field.getFieldName(),
                field.getDisplayLabel(),
                parseDataType(field.getDataType()),
                field.isSearchable(),
                field.getSortOrder());
    }

    private ViewMetadataDTO toViewMetadata(FrameworkView view) {
        List<ViewMetadataDTO.ViewFieldDTO> fields = view.getFields().stream()
                .sorted(Comparator.comparingInt(FrameworkViewField::getSortOrder))
                .map(field -> new ViewMetadataDTO.ViewFieldDTO(
                        field.getFieldName(),
                        field.getDisplayLabel(),
                        parseDataType(field.getDataType()),
                        field.isVisible(),
                        field.isSearchable()))
                .toList();

        return new ViewMetadataDTO(
                view.getId(),
                view.getModel().getId(),
                view.getViewName(),
                view.getViewType(),
                fields);
    }

    private GridViewMetadataDTO toGridViewMetadata(FrameworkView view) {
        List<GridViewMetadataDTO.GridColumnDTO> columns = view.getFields().stream()
                .sorted(Comparator.comparingInt(FrameworkViewField::getSortOrder))
                .map(field -> new GridViewMetadataDTO.GridColumnDTO(
                        field.getFieldName(),
                        field.getDisplayLabel(),
                        parseDataType(field.getDataType()),
                        field.isVisible(),
                        field.isSearchable(),
                        field.getSortOrder()))
                .toList();

        return new GridViewMetadataDTO(
                view.getId(),
                view.getModel().getId(),
                view.getViewName(),
                view.getViewType(),
                view.isMultiSelect(),
                view.isEditable(),
                view.isHideHeaders(),
                view.getRowHeight(),
                view.getPageLimit(),
                view.getPaginationType(),
                view.getConfig(),
                columns);
    }

    private FkDataType parseDataType(String raw) {
        if (raw == null || raw.isBlank()) {
            return FkDataType.STRING;
        }
        try {
            return FkDataType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return FkDataType.STRING;
        }
    }
}
