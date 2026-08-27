package com.kodiak.common.commdto.common;

import java.util.Map;

public class KnBulkSubscribersIDMProfileDTO {
    private Map<String, KnBulkSubsProfileDTO> bulkSubsDetailsMap;
    private boolean isOIDCApplicable;

    public Map<String, KnBulkSubsProfileDTO> getBulkSubsDetailsMap() {
        return bulkSubsDetailsMap;
    }

    public void setBulkSubsDetailsMap(Map<String, KnBulkSubsProfileDTO> bulkSubsDetailsMap) {
        this.bulkSubsDetailsMap = bulkSubsDetailsMap;
    }

    public boolean isOIDCApplicable() {
        return isOIDCApplicable;
    }

    public void setOIDCApplicable(boolean OIDCApplicable) {
        isOIDCApplicable = OIDCApplicable;
    }
}
