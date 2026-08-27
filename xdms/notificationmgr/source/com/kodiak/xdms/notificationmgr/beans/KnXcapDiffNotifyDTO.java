/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXcapDiffNotifyDTO.java
 * Subsystem:  PoC XDMS
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Harsha             06-Jan-2011       7.0
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
package com.kodiak.xdms.notificationmgr.beans;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;

public class KnXcapDiffNotifyDTO extends KnCommonNotifyDTO {

    //stores the xcap root uri
    private String xcapRootUri;

    /**
     * directory URI
     */
    private String dirURI;
    /**
     * directory previous etag
     */
    private String dirPrevEtag;
    /**
     * directory new etag
     */
    private String dirNewEtag;
    /**
     * the doc change details
     */
    private Collection<KnXcapDiffDocDTO> docDiffObj;
    // indicating the cause for DocDiff
    private String reason;
    // indicating protocol version
    private String protocolVersion;
    /**
     * flag to indicate if deactivation notification to be sent
     */
    // private boolean deactivateNotify;
    /**
     * deactivation notification details
     */
    //  private KnDeactivateNotifyDTO deactNotifyDTO;

    //   private boolean deleteSubsNotify;

    private boolean deRegisterNotify;
    private boolean profileNotify;
    private boolean tgsModeNotify;
   // private boolean subscribeNotify;

    /**
     * This variable is to hold the tgs mode value of the subscriber used while sending tgs mode change SEH notification
     */
    private Integer tgsMode;


    //   private KnDeleteNotifyDTO deleteNotifyDTO;

    // private boolean deactivateNotify;
    /**
     * deactivation notification details
     */
    private KnDeRegisterNotifyDTO deRegisterNotifyDTO;

    private KnProfileNotifyDTO profileNotifyDTO;

    private boolean isChangeMdnNotify;

    private KnTGSModeNotifyDTO tgsModeNotifyDTO;
    //owner mdn is  0 - Base Mdn,1 - Profile Mdn
    private Integer ntfyOnAnyMDN;

    public boolean isDeRegisterNotify() {
        return deRegisterNotify;
    }

    public void setDeRegisterNotify(boolean deRegisterNotify) {
        this.deRegisterNotify = deRegisterNotify;
    }

    public KnDeRegisterNotifyDTO getDeRegisterNotifyDTO() {
        return deRegisterNotifyDTO;
    }

    public void setDeRegisterNotifyDTO(KnDeRegisterNotifyDTO deRegisterNotifyDTO) {
        this.deRegisterNotifyDTO = deRegisterNotifyDTO;
    }

    public boolean isProfileNotify() {
        return profileNotify;
    }

    public void setProfileNotify(boolean profileNotify) {
        this.profileNotify = profileNotify;
    }

    public KnProfileNotifyDTO getProfileNotifyDTO() {
        return profileNotifyDTO;
    }

    public void setProfileNotifyDTO(KnProfileNotifyDTO profileNotifyDTO) {
        this.profileNotifyDTO = profileNotifyDTO;
    }

    public String getDirURI() {
        return dirURI;
    }

    public void setDirURI(String dirURI) {
        this.dirURI = dirURI;
    }

    public String getDirPrevEtag() {
        return dirPrevEtag;
    }

    public void setDirPrevEtag(String dirPrevEtag) {
        this.dirPrevEtag = dirPrevEtag;
    }

    public String getDirNewEtag() {
        return dirNewEtag;
    }

    public void setDirNewEtag(String dirNewEtag) {
        this.dirNewEtag = dirNewEtag;
    }

    public Collection<KnXcapDiffDocDTO> getDocDiffObj() {
        return docDiffObj;
    }

    public void setDocDiffObj(Collection<KnXcapDiffDocDTO> docDiffObj) {
        this.docDiffObj = docDiffObj;
    }

   /* public boolean isSubscribeNotify() {
        return subscribeNotify;
    }

    public void setSubscribeNotify(boolean subscribeNotify) {
        this.subscribeNotify = subscribeNotify;
    }*/

    /*   public boolean isDeactivateNotify() {
     return deactivateNotify;
 }


 public void setDeactivateNotify(boolean deactivateNotify) {
     this.deactivateNotify = deactivateNotify;
 }

 public KnDeactivateNotifyDTO getDeactNotifyDTO() {
     return deactNotifyDTO;
 }

 public void setDeactNotifyDTO(KnDeactivateNotifyDTO deactNotifyDTO) {
     this.deactNotifyDTO = deactNotifyDTO;
 }   */

    public String getXcapRootUri() {
        return xcapRootUri;
    }

    public void setXcapRootUri(String xcapRootUri) {
        this.xcapRootUri = xcapRootUri;
    }

/*    public boolean isDeleteSubsNotify() {
        return deleteSubsNotify;
    }

    public void setDeleteSubsNotify(boolean deleteSubsNotify) {
        this.deleteSubsNotify = deleteSubsNotify;
    }

    public KnDeleteNotifyDTO getDeleteNotifyDTO() {
        return deleteNotifyDTO;
    }

    public void setDeleteNotifyDTO(KnDeleteNotifyDTO deleteNotifyDTO) {
        this.deleteNotifyDTO = deleteNotifyDTO;
    }     */

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public boolean isChangeMdnNotify() {
        return isChangeMdnNotify;
    }

    public void setChangeMdnNotify(boolean isChangeMdnNotify) {
        this.isChangeMdnNotify = isChangeMdnNotify;
    }

    public boolean isTgsModeNotify() {
        return tgsModeNotify;
    }

    public void setTgsModeNotify(boolean tgsModeNotify) {
        this.tgsModeNotify = tgsModeNotify;
    }

    public KnTGSModeNotifyDTO getTgsModeNotifyDTO() {
        return tgsModeNotifyDTO;
    }

    public void setTgsModeNotifyDTO(KnTGSModeNotifyDTO tgsModeNotifyDTO) {
        this.tgsModeNotifyDTO = tgsModeNotifyDTO;
    }

    public Integer getTgsMode() {
        return tgsMode;
    }

    public void setTgsMode(Integer tgsMode) {
        this.tgsMode = tgsMode;
    }

    public Integer getNtfyOnAnyMDN() {
        return ntfyOnAnyMDN;
    }

    public void setNtfyOnAnyMDN(Integer ntfyOnAnyMDN) {
        this.ntfyOnAnyMDN = ntfyOnAnyMDN;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(" [KnXCAPDiffNotifyDTO - ")
                .append(" XCAP_ROOT_URI - ").append(xcapRootUri)
                .append(", Directory_URI - ").append(KnGDPRTemplate.mdnUriTemplate(dirURI))
                .append(", Reason - ").append(reason)
                .append(", Directory_Prev_Etag - ").append(dirPrevEtag)
                .append(", Directory_New_Etag - ").append(dirNewEtag)
                .append(", Doc_Diff_Object - ").append(docDiffObj)
                //  .append(", Deactivate_Notify - ").append(deactivateNotify)
                //     .append(", Deactivate_Notfy_DTO - ").append(deactNotifyDTO)
                //    .append(", Delete_Subs_Notify - ").append(deleteSubsNotify)
                //    .append(", Delete_Notify_DTO - ").append(deleteNotifyDTO)
                .append(", DeRegister_Notify - ").append(deRegisterNotify)
                .append(", DeRegister_Notify_DTO - ").append(deRegisterNotifyDTO)
                .append(", Profile_Notify - ").append(profileNotify)
                .append(", Profile_Notify_DTO - ").append(profileNotifyDTO)
                .append(", CHANGE_MDN_NOTIFY - ").append(isChangeMdnNotify)
                .append(", TGS_Notify_DTO - ").append(tgsModeNotifyDTO)
                .append(", tgsModeNotify - ").append(tgsModeNotify)
                .append(", tgsMode - ").append(tgsMode)
                .append(", ntfyOnAnyMDN - ").append(ntfyOnAnyMDN)
                .append("]");

        return strBuffer.toString();
    }
}
