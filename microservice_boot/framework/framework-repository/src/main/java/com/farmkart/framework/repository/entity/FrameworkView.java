package com.farmkart.framework.repository.entity;

import com.farmkart.framework.client.enums.FkViewType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "fk_views",
       uniqueConstraints = @UniqueConstraint(columnNames = {"model_id", "view_name"}))
public class FrameworkView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private FrameworkModel model;

    @Column(name = "view_name", nullable = false)
    private String viewName;

    @Enumerated(EnumType.STRING)
    @Column(name = "view_type", nullable = false)
    private FkViewType viewType = FkViewType.GRID;

    // ── Grid-level behaviour ───────────────────────────────────────────────
    @Column(name = "multi_select")
    private boolean multiSelect;

    @Column(name = "is_editable")
    private boolean editable;

    @Column(name = "hide_headers")
    private boolean hideHeaders;

    @Column(name = "row_height")
    private int rowHeight = 40;

    @Column(name = "page_limit")
    private int pageLimit = 25;

    /** INFINITE_SCROLL | SERVER_SIDE | CLIENT_SIDE */
    @Column(name = "pagination_type")
    private String paginationType = "SERVER_SIDE";

    /** Free-form JSON blob for extra config (filters presets, etc.). */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> config = new HashMap<>();

    /** View-field definitions used for grid column metadata. */
    @OneToMany(mappedBy = "view", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<FrameworkViewField> fields = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Long getId() { return id; }

    public FrameworkModel getModel() { return model; }
    public void setModel(FrameworkModel model) { this.model = model; }

    public String getViewName() { return viewName; }
    public void setViewName(String viewName) { this.viewName = viewName; }

    public FkViewType getViewType() { return viewType; }
    public void setViewType(FkViewType viewType) { this.viewType = viewType; }

    public boolean isMultiSelect() { return multiSelect; }
    public void setMultiSelect(boolean multiSelect) { this.multiSelect = multiSelect; }

    public boolean isEditable() { return editable; }
    public void setEditable(boolean editable) { this.editable = editable; }

    public boolean isHideHeaders() { return hideHeaders; }
    public void setHideHeaders(boolean hideHeaders) { this.hideHeaders = hideHeaders; }

    public int getRowHeight() { return rowHeight; }
    public void setRowHeight(int rowHeight) { this.rowHeight = rowHeight; }

    public int getPageLimit() { return pageLimit; }
    public void setPageLimit(int pageLimit) { this.pageLimit = pageLimit; }

    public String getPaginationType() { return paginationType; }
    public void setPaginationType(String paginationType) { this.paginationType = paginationType; }

    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }

    public List<FrameworkViewField> getFields() { return fields; }
}
