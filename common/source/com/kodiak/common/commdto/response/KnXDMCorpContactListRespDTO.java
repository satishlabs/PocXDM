/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpContactListRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 17, 2011      7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnExternalSubsDetailsDTO;
import com.kodiak.common.commdto.common.KnXDMCorpContactDTO;
import com.kodiak.common.commdto.common.KnXDMCorpSublistDTO;
import com.kodiak.common.commdto.common.KnXDMCorpSublistInfoDTO;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.List;


public class KnXDMCorpContactListRespDTO extends KnXDMCorpRespDTO implements IXDMResponseDTO {

    private static final long serialVersionUID = 7526471155622776147L;

    private String corpId;
    private String ownerMdn;
    private KnXDMCorpSublistInfoDTO contactListInfo;
    private List<KnXDMCorpContactDTO> contactInfoList;
    private Collection<KnXDMCorpSublistDTO> sublistLists;
    private int totalContactsCount;
    private int maxContactsLimitFlag;
    private List<KnExternalSubsDetailsDTO> extSubsList;
    private int count;
    private int totalGroups;
    private int totalUpms;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getOwnerMdn() {
        return ownerMdn;
    }

    public void setOwnerMdn(String ownerMdn) {
        this.ownerMdn = ownerMdn;
    }

    public KnXDMCorpSublistInfoDTO getContactListInfo() {
        return contactListInfo;
    }

    public void setContactListInfo(KnXDMCorpSublistInfoDTO contactListInfo) {
        this.contactListInfo = contactListInfo;
    }

    public List<KnXDMCorpContactDTO> getContactInfoList() {
        return contactInfoList;
    }

    public void setContactInfoList(List<KnXDMCorpContactDTO> contactInfoList) {
        this.contactInfoList = contactInfoList;
    }

    public Collection<KnXDMCorpSublistDTO> getSublistLists() {
        return sublistLists;
    }

    public void setSublistLists(Collection<KnXDMCorpSublistDTO> sublistLists) {
        this.sublistLists = sublistLists;
    }

    public int getTotalContactsCount() {
        return totalContactsCount;
    }

    public void setTotalContactsCount(int totalContactsCount) {
        this.totalContactsCount = totalContactsCount;
    }

    public int getMaxContactsLimitFlag() {
        return maxContactsLimitFlag;
    }

    public void setMaxContactsLimitFlag(int maxContactsLimitFlag) {
        this.maxContactsLimitFlag = maxContactsLimitFlag;
    }

    public List<KnExternalSubsDetailsDTO> getExtSubsList() {
		return extSubsList;
	}

	public void setExtSubsList(List<KnExternalSubsDetailsDTO> extSubsList) {
		this.extSubsList = extSubsList;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

    public int getTotalGroups() {
        return totalGroups;
    }

    public void setTotalGroups(int totalGroups) {
        this.totalGroups = totalGroups;
    }

    public int getTotalUpms() {
        return totalUpms;
    }

    public void setTotalUpms(int totalUpms) {
        this.totalUpms = totalUpms;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(super.toString())
                .append(", corpId  - ").append(corpId)
                .append(", Owner - ").append(KnGDPRTemplate.mdn(ownerMdn))
                .append(", ContactListInfo - ").append(contactListInfo)
                .append(", ContactInfoList - ").append(contactInfoList)
                .append(", SublistList - ").append(sublistLists)
                .append(", ExtSubsList - ").append(extSubsList)
                .append(", MaxContactsLimitFlag - ").append(maxContactsLimitFlag)
                .append(", TotalContactsCount - ").append(totalContactsCount)
                .append(", count - ").append(count)
                .append(", totalGroups - ").append(totalGroups)
                .append(", totalUpms - ").append(totalUpms);
        return sb.toString();
    }
}
