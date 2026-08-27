package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnPTTSettingDocInfoDTO;
import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KnXDMPTTSettingRespDTO extends KnXDMCorpRespDTO implements IXDMResponseDTO{
    private static final long serialVersionUID = 6984479254373812511L;

    private String responseCode;

    private int responseStatus;

    private String responseMessage;

    private Collection responseDetails;

    private String destPttServerId;

    private String destQueueName;

    private String transactionId;

    private List<KnPTTSettingDocInfoDTO> pttSettingDocList;

    private Map<String, List<Integer>> pttSettingDocInfo;

    private String pttTemplateId;

    private String templateName;

    private String templateVer;

    private List<KnXDMMdnInfoDTO> mdnList;

    private String corpId;

    /**
     * getter method for clientdat code
     *
     * @return String
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * setter method for Response code
     *
     * @param responseCode String
     */
    public void setResponseCode(String responseCode) {
        if (responseCode != null) {
            responseCode = responseCode.trim();
            if (responseCode.equals("")) {
                responseCode = null;
            }
        }
        this.responseCode = responseCode;
    }

    /**
     * getter method for Response Status
     *
     * @return String
     */
    public int getResponseStatus() {
        return responseStatus;
    }

    /**
     * setter method for Response Status
     *
     * @param responseStatus String
     */
    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    /**
     * getter method for the Response Message
     *
     * @return String
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * setter method for the Response Message
     *
     * @param responseMessage String
     */
    public void setResponseMessage(String responseMessage) {
        if (responseMessage != null) {
            responseMessage = responseMessage.trim();
            if (responseMessage.equals("")) {
                responseMessage = null;
            }
        }
        this.responseMessage = responseMessage;
    }

    /**
     * getter method for the Response Details
     *
     * @return Collection
     */
    public Collection getResponseDetails() {
        return responseDetails;
    }

    /**
     * setter method for the Response Details
     *
     * @param responseDetails Collection
     */
    public void setResponseDetails(Collection responseDetails) {
        this.responseDetails = responseDetails;
    }

    public String getObjectId() {
        return transactionId;
    }

    public String getDestPttServerId() {
        return destPttServerId;
    }

    public void setDestPttServerId(String destPttServerId) {
        if (destPttServerId != null) {
            destPttServerId = destPttServerId.trim();
            if (destPttServerId.equals("")) {
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
            if (destQueueName.equals("")) {
                destQueueName = null;
            }
        }
        this.destQueueName = destQueueName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        if (transactionId != null) {
            transactionId = transactionId.trim();
            if (transactionId.equals("")) {
                transactionId = null;
            }
        }
        this.transactionId = transactionId;
    }

    public List<KnPTTSettingDocInfoDTO> getPttSettingDocList() {
        return pttSettingDocList;
    }

    public void setPttSettingDocList(List<KnPTTSettingDocInfoDTO> pttSettingDocList) {
        this.pttSettingDocList = pttSettingDocList;
    }

    public Map<String, List<Integer>> getPttSettingDocInfo() {
        return pttSettingDocInfo;
    }

    public void setPttSettingDocInfo(Map<String, List<Integer>> pttSettingDocInfo) {
        this.pttSettingDocInfo = pttSettingDocInfo;
    }

    public String getPttTemplateId() {
        return pttTemplateId;
    }

    public void setPttTemplateId(String pttTemplateId) {
        this.pttTemplateId = pttTemplateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateVer() {
        return templateVer;
    }

    public void setTemplateVer(String templateVer) {
        this.templateVer = templateVer;
    }

    public List<KnXDMMdnInfoDTO> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<KnXDMMdnInfoDTO> mdnList) {
        this.mdnList = mdnList;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    @Override
    public String toString() {
        return "KnXDMPTTSettingRespDTO{" +
                "pttSettingDocList=" + pttSettingDocList +
                ", pttSettingDocInfo=" + pttSettingDocInfo +
                ", pttTemplateId='" + pttTemplateId +
                ", templateName='" + templateName +
                ", templateVer='" + templateVer +
                ", mdnList='" + mdnList +
                '}';
    }
}
