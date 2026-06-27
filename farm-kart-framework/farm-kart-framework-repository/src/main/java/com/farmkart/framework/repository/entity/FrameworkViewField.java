package com.farmkart.framework.repository.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fk_view_fields")
public class FrameworkViewField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "view_id", nullable = false)
    private FrameworkView view;

    @Column(name = "field_name", nullable = false)
    private String fieldName;

    @Column(name = "display_label", nullable = false)
    private String displayLabel;

    @Column(name = "data_type", nullable = false)
    private String dataType = "STRING";

    private boolean visible = true;
    private boolean searchable;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public Long getId() { return id; }
    public FrameworkView getView() { return view; }
    public void setView(FrameworkView view) { this.view = view; }
    public String getFieldName() { return fieldName; }
    public String getDisplayLabel() { return displayLabel; }
    public String getDataType() { return dataType; }
    public boolean isVisible() { return visible; }
    public boolean isSearchable() { return searchable; }
    public int getSortOrder() { return sortOrder; }
}
