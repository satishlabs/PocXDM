package com.kodiak.common.commdto.common;

import java.io.Serializable;
import java.util.List;

public class KnPTTSettingDocInfoDTO implements Serializable {

    private static final long serialVersionUID = 752647115715067688L;

    private String docId;
    private String docName;
    private int isDefault;
    private int mdnCount;
    private int isSysDefault;

    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }

    public String getDocName() { return docName; }
    public void setDocName(String docName) { this.docName = docName; }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public int getMdnCount() {
        return mdnCount;
    }

    public void setMdnCount(int mdnCount) {
        this.mdnCount = mdnCount;
    }

    public int getIsSystemDefault() { return isSysDefault; }

    public void setIsSystemDefault(int isSystemDefault) { this.isSysDefault = isSystemDefault; }

    @Override
    public String toString() {
        return "KnPTTSettingDocInfoDTO{" +
                "docId='" + docId + '\'' +
                ", docName='" + docName + '\'' +
                ", isDefault=" + isDefault +
                ", mdnCount=" + mdnCount +
                ", isSystemDefault=" + isSysDefault +
                '}';
    }
}
