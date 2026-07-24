package com.farmkart.framework.service.view;

import com.farmkart.framework.client.dto.*;
import com.farmkart.framework.client.enums.FkDataType;
import com.farmkart.framework.client.enums.FkViewType;

import java.util.List;

public interface IFarmKartViewService {

    List<ModelMetadataDTO> listModels();

    ModelMetadataDTO getModel(String modelName);

    List<ModelFieldDTO> getModelFields(String modelName);

    List<ModelFieldDTO> getModelFieldsByDataType(String modelName, FkDataType dataType);

    List<ModelFieldDTO> getSearchableFields(String modelName);

    ViewMetadataDTO getViewMetadata(String modelName, String viewName);

    GridViewMetadataDTO getGridViewMetadata(String modelName, String viewName);

    List<String> getViewNames(String modelName);

    List<ViewMetadataDTO> getViewsByType(String modelName, FkViewType viewType);
}
