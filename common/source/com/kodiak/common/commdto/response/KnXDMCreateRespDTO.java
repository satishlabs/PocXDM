package com.kodiak.common.commdto.response;

/**
 * Extended XDM response DTO that includes corporate and network identifiers.
 */
public class KnXDMCreateRespDTO extends KnXDMRespDTO implements IXDMResponseDTO {

    private int corpId;

    private String corpName;

    private String networkName;

    private boolean isLastSubscriber;

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public boolean isLastSubscriber() {
        return isLastSubscriber;
    }

    public void setLastSubscriber(boolean lastSubscriber) {
        isLastSubscriber = lastSubscriber;
    }

    @Override
    public String toString() {
        return super.toString() + ", corpId - " + corpId +
                ", corpName - " + corpName +
                ", networkName - " + networkName +
                ", isLastSubscriber - " + isLastSubscriber;
    }
}


