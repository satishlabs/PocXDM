package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnBulkSubscriberProvDTO;
import com.kodiak.common.commdto.common.KnUserAgentDTO;
import com.kodiak.common.resources.KnConstants;

import java.io.Serial;
import java.util.Map;

public class KnXDMBulkSubsProvInfoDTO extends KnBulkSubscriberProvDTO implements IXDMRequestDTO {
    @Serial
    private static final long serialVersionUID = 7526471155622676301L;
    private String operationType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String srcRoutingKey;
    private String srcQueueName;
    private String srcIPAddress;
    private String correlationId;
    private String countryCode;
    private String version;
    private boolean compensationRequest;

    // Can be removed later if not needed
    private KnUserAgentDTO userAgentDTO;
    private Map<Integer,Integer> vocoderIdMap;
    private KnConstants.BUSINESS_CLIENT_TYPE businessClientType;

    public void setOperationType(String operationType) {
        if (operationType != null) {
            operationType = operationType.trim();
            if (operationType.isEmpty()) {
                operationType = null;
            }
        }
        this.operationType = operationType;
    }
    public String getOperationType() {
        return operationType;
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
        if (destPttServerId != null) {
            destPttServerId = destPttServerId.trim();
            if (destPttServerId.isEmpty()) {
                destPttServerId = null;
            }
        }
        this.destPttServerId = destPttServerId;
    }

    public String getDestQueueName() {
        return destQueueName;
    }
    public void setDestQueueName(String destQueueName) {
        if (destQueueName != null) {
            destQueueName = destQueueName.trim();
            if (destQueueName.isEmpty()) {
                destQueueName = null;
            }
        }
        this.destQueueName = destQueueName;
    }

    public String getSrcRoutingKey() {
        return srcRoutingKey;
    }
    public void setSrcRoutingKey(String srcRoutingKey) {
        this.srcRoutingKey = srcRoutingKey;
    }

    public String getSrcQueueName() {
        return srcQueueName;
    }
    public void setSrcQueueName(String srcQueueName) {
        this.srcQueueName = srcQueueName;
    }

    public String getSrcIPAddress() {
        return srcIPAddress;
    }
    public void setSrcIPAddress(String srcIPAddress) {
        this.srcIPAddress = srcIPAddress;
    }

    public String getCorrelationId() {
        return correlationId;
    }
    public void setCorrelationId(String correlationId) {
        if (correlationId != null) {
            correlationId = correlationId.trim();
            if (correlationId.isEmpty()) {
                correlationId = null;
            }
        }
        this.correlationId = correlationId;
    }

    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isCompensationRequest() {
        return compensationRequest;
    }

    public void setCompensationRequest(boolean compensationRequest) {
        this.compensationRequest = compensationRequest;
    }

    // Can be removed later if not needed
    public KnUserAgentDTO getUserAgentDTO() {
        return userAgentDTO;
    }
    public void setUserAgentDTO(KnUserAgentDTO userAgentDTO) {
        this.userAgentDTO = userAgentDTO;
    }

    public Map<Integer, Integer> getVocoderIdMap() {
        return vocoderIdMap;
    }
    public void setVocoderIdMap(Map<Integer, Integer> vocoderIdMap) {
        this.vocoderIdMap = vocoderIdMap;
    }

    public KnConstants.BUSINESS_CLIENT_TYPE getBusinessClientType() {
        return businessClientType;
    }
    public void setBusinessClientType(KnConstants.BUSINESS_CLIENT_TYPE businessClientType) {
        this.businessClientType = businessClientType;
    }

    @Override
    public String toString() {
        StringBuilder strBuffer = new StringBuilder(100);
        strBuffer.append(super.toString());
        strBuffer.append(", Operation_Type - ").append(this.operationType)
                .append(", Auth_DTO - ").append(this.authDTO)
                .append(", Dest_PTT_Server_ID - ").append(this.destPttServerId)
                .append(", Dest_Queue_Name - ").append(this.destQueueName)
                .append(", srcRoutingKey - ").append(this.srcRoutingKey)
                .append(", srcQueueName - ").append(this.srcQueueName)
                .append(", srcIPAddress - ").append(this.srcIPAddress)
                .append(", Correlation_Id - ").append(this.correlationId)
                .append(", countryCode - ").append(this.countryCode)
                .append(", version - ").append(this.version)
                .append(", compensationRequest - ").append(this.compensationRequest)
                .append(", hierarchyType - ").append(this.hierarchyType);
        return strBuffer.toString();
    }


//    private int clientType;
//    private String transactionId;
//    private long ifMatch;
//    private long ifNoneMatch;
//    private KnConstants.SERVICE_AUTH_STATUS serviceAuthStatus;
//    private int subsDefPttRadio;
//    private Map<Integer,Integer> provFSMap;
//
//    @Override
//    public int getClientType() {
//        return clientType;
//    }
//
//    @Override
//    public void setClientType(int clientType) {
//        this.clientType = clientType;
//    }
//
//    @Override
//    public String getTransactionId() {
//        return transactionId;
//    }
//
//    @Override
//    public void setTransactionId(String transactionId) {
//        this.transactionId = transactionId;
//    }
//
//    @Override
//    public long getIfMatch() {
//        return ifMatch;
//    }
//
//    @Override
//    public void setIfMatch(long ifMatch) {
//        this.ifMatch = ifMatch;
//    }
//
//    @Override
//    public long getIfNoneMatch() {
//        return ifNoneMatch;
//    }
//
//    @Override
//    public void setIfNoneMatch(long ifNoneMatch) {
//        this.ifNoneMatch = ifNoneMatch;
//    }
//
//    @Override
//    public KnConstants.SERVICE_AUTH_STATUS getServiceAuthStatus() {
//        return serviceAuthStatus;
//    }
//
//    @Override
//    public void setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS serviceAuthStatus) {
//        this.serviceAuthStatus = serviceAuthStatus;
//    }
//
//    @Override
//    public int getSubsDefPttRadio() {
//        return subsDefPttRadio;
//    }
//
//    @Override
//    public void setSubsDefPttRadio(int subsDefPttRadio) {
//        this.subsDefPttRadio = subsDefPttRadio;
//    }
//
//    public Map<Integer, Integer> getProvFSMap() {
//        return provFSMap;
//    }
//
//    public void setProvFSMap(Map<Integer, Integer> provFSMap) {
//        this.provFSMap = provFSMap;
//    }
//
}
