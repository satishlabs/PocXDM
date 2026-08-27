/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.clientdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.KnXDMError;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IOutputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPopulate;

public class KnTPUserInfoDTO implements IInputDTO,IOutputDTO {

	private static final long serialVersionUID = -8241412705957814316L;
	private String operationType;
	private String entityId;
    private String profile;
    private int DTOStatus;
    private KnXDMError errorObject;
    private String responseCode;
    private int responseStatus;
    private String responseMessage;
    private String performer;
    private IAuthDTO authDTO;
    private int clientType;

    private int tpAccountId;
    private String billingMdn;
    private String tpUser;
    private String mdn;
    private String activationCode;
    private String VendorId;


	public String getVendorId() {
		return VendorId;
	}

	public void setVendorId(String vendorId) {
		VendorId = vendorId;
	}

	public int getTpAccountId() {
		return tpAccountId;
	}

	public void setTpAccountId(int tpAccountId) {
		this.tpAccountId = tpAccountId;
	}

	public String getBillingMdn() {
		return billingMdn;
	}

	public void setBillingMdn(String billingMdn) {
		this.billingMdn = billingMdn;
	}

	public String getTpUser() {
		return tpUser;
	}

	public void setTpUser(String tpUser) {
		this.tpUser = tpUser;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	@Override
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	@Override
	public String getOperationType() {
		return operationType;
	}

	@Override
	public String getEntityId() {
		return entityId;
	}

	@Override
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	@Override
	public String getProfile() {
		return profile;
	}

	@Override
	public void setProfile(String profile) {
		this.profile = profile;
	}

	@Override
	public String getObjectId() {
		return null;
	}

	@Override
	public int getDTOStatus() {
		return DTOStatus;
	}

	@Override
	public void setDTOStatus(int dtoStatus) {
		this.DTOStatus = DTOStatus;
	}

	@Override
	public KnXDMError getErrorObject() {
		return errorObject;
	}

	@Override
	public void setErrorObject(KnXDMError errorObj) {
		this.errorObject = errorObject;
	}

	@Override
	public void populate(IPopulate dtoObject) {

	}

	@Override
	public String getResponseCode() {
		  return responseCode;
	}

	@Override
	public void setResponseCode(String responseCode) {
		 this.responseCode = responseCode;
	}

	@Override
	public int getResponseStatus() {
		return responseStatus;
	}

	@Override
	public void setResponseStatus(int responseStatus) {
		this.responseStatus = responseStatus;
	}

	@Override
	public String getResponseMessage() {
		return responseMessage;
	}

	@Override
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	@Override
	public void setPerformer(String performer) {
		this.performer = performer;
	}

	@Override
	public String getPerformer() {
		return performer;
	}

	@Override
	public void setAuthDTO(IAuthDTO authDTO) {
		this.authDTO = authDTO;

	}

	@Override
	public IAuthDTO getAuthDTO() {
		return authDTO;
	}

	@Override
	public void setClientType(int clientType) {
		this.clientType = clientType;
	}

	@Override
	public int getClientType() {
		return clientType;
	}

	@Override
    public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnTPUserInfoDTO [")
		.append("operationType-").append(operationType)
		.append("entityId-").append(entityId)
		.append("profile-").append(profile)
		.append("DTOStatus-").append(DTOStatus)
		.append("errorObject-").append(errorObject)
		.append("responseCode-").append(responseCode)
		.append("responseStatus-").append(responseStatus)
		.append("responseMessage-").append(responseMessage)
		.append("performer-").append(performer)
		.append("authDTO-").append(authDTO)
		.append("clientType-").append(clientType)
		.append("tpAccountId-").append(tpAccountId)
		.append("billingMdn-").append(billingMdn)
		.append("tpUser-").append(tpUser)
		.append("mdn-").append(KnGDPRTemplate.mdn(mdn))
		.append("activationCode-").append(activationCode)
		.append("VendorId-").append(VendorId);

		return builder.toString();
    }

}
