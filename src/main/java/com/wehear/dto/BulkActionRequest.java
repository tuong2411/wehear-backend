package com.wehear.dto;

import java.util.List;

public class BulkActionRequest {
    private List<Long> ids;
    private String action;

    public BulkActionRequest() {}

    public List<Long> getIds() { return ids; }
    public void setIds(List<Long> ids) { this.ids = ids; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
