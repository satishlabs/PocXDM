/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnTargetMdnInfoDTO;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.List;

/**
 * Created by schandra on 18-12-2017.
 */
public class KnXDMAuthListResponseDTO extends KnXDMRespDTO {

    private String docEtag;
    private String authMdn;
    private List<KnTargetMdnInfoDTO> targetInfo;

    public String getAuthMdn() {
        return authMdn;
    }

    public void setAuthMdn(String authMdn) {
        this.authMdn = authMdn;
    }

    public List<KnTargetMdnInfoDTO> getTargetInfo() {
        return targetInfo;
    }

    public void setTargetInfo(List<KnTargetMdnInfoDTO> targetInfo) {
        this.targetInfo = targetInfo;
    }

    public String getDocEtag() {
        return docEtag;
    }

    public void setDocEtag(String docEtag) {
        this.docEtag = docEtag;
    }

    @Override
    public String toString() {
        return "KnXDMAuthListResponseDTO{" +
                "docEtag='" + docEtag + '\'' +
                ", authMdn='" + KnGDPRTemplate.mdn(authMdn) + '\'' +
                ", targetInfo=" + targetInfo +
                '}';
    }
}
