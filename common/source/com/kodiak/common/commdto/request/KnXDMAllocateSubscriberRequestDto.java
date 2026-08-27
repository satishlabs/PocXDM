/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;


import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;

import java.io.Serializable;
import java.util.List;

public class KnXDMAllocateSubscriberRequestDto extends KnXDMCorpInfo implements IXDMRequestDTO {
    private static final long serialVersionUID = 2654525373432268389L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private List<KnMdnDetailsDTO> mdnList;
    private String hierarchyId;

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    public String getDestPttServerId() {
        return destPttServerId;
    }

    public void setDestPttServerId(String destPttServerId) {
        this.destPttServerId = destPttServerId;
    }

    public String getDestQueueName() {
        return destQueueName;
    }

    public void setDestQueueName(String destQueueName) {
        this.destQueueName = destQueueName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }


    public List<KnMdnDetailsDTO> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<KnMdnDetailsDTO> mdnList) {
        this.mdnList = mdnList;
    }

    public static class KnMdnDetailsDTO implements Serializable {
        private static final long serialVersionUID = 2654525373432268388L;

        private String geoCode;

        private List<String> mdn;

        public String getGeoCode() {
            return geoCode;
        }

        public void setGeoCode(String geoCode) {
            this.geoCode = geoCode;
        }

        public List<String> getMdn() {
            return mdn;
        }

        public void setMdn(List<String> mdn) {
            this.mdn = mdn;
        }

        @Override
        public String toString() {
            return "KnMdnDetailsDTO{" +
                    "geoCode='" + geoCode + '\'' +
                    ", mdn=" + mdn +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "KnXDMAllocateSubscriberRequestDto{" +
                "operationType='" + operationType + '\'' +
                ", clientType=" + clientType +
                ", authDTO=" + authDTO +
                ", destPttServerId='" + destPttServerId + '\'' +
                ", destQueueName='" + destQueueName + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", mdnList=" + mdnList +
                '}';
    }
}
