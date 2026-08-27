/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;


import com.kodiak.common.commdto.common.KnXDMGroupDTO;
import com.kodiak.common.commdto.common.KnXDMMCSGroupInfoDTO;
import com.kodiak.common.commdto.common.KnXDMMemberDTO;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;

/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMMCDATAUserProfileRespDTO.java
 * Subsystem:  MCSXCAP
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Shashank Tewari            July 09, 2019                9.1.1
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
public class KnXDMMCDATAUserProfileRespDTO extends KnXDMRespDTO {
    private static final long serialVersionUID = -1540924884058819709L;
    private String xui_uri;
    private String name;
    private boolean status;
    private String profileName;
    private String pre_selected_indication;
    private String common_useralias_name;
    private KnXDMMemberDTO common_mcdata_userdto;
    private String common_mco_name;
    private KnXDMMemberDTO common_fd_userdto;
    private String common_trxcontrol_maxdata1to1;
    private String common_trxcontrol_maxtime1to1;
    private KnXDMGroupDTO common_grpemergalert_userdto;
    private Collection<KnXDMMemberDTO> common_one2one_userdto;
    private String common_one2one_proentry_discoverygroupid;
    private String common_one2one_proentry_userinfoid;
    private KnXDMMemberDTO common_one2one_kmsuri_userdto;
    private Collection<KnXDMMemberDTO> common_one2one_anyext_userdto;
    private String common_one2one_anyext_proentry_discoverygroupid;
    private String common_one2one_anyext_proentry_userinfoid;
    private KnXDMMemberDTO common_one2one_anyext_kmsuri_userdto;
    private Collection<KnXDMMCSGroupInfoDTO> mcsgrpInfo;
    private String onnetwork_relpresentpriority;
    private String onnetwork_maxaffiliationsN2;
    private KnXDMMemberDTO onnetwork_implicitaffiliations_usertdto;
    private KnXDMMemberDTO onnetwork_one2oneemergencyalert_dto;
    private String activeFS;
    private String aliasMdn;
    private String oldActiveFS;
    private int corpId;
    private Integer userProfileIndex;

    public String getXui_uri() {
        return xui_uri;
    }

    public void setXui_uri(String xui_uri) {
        this.xui_uri = xui_uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getPre_selected_indication() {
        return pre_selected_indication;
    }

    public void setPre_selected_indication(String pre_selected_indication) {
        this.pre_selected_indication = pre_selected_indication;
    }

    public String getCommon_useralias_name() {
        return common_useralias_name;
    }

    public void setCommon_useralias_name(String common_useralias_name) {
        this.common_useralias_name = common_useralias_name;
    }

    public KnXDMMemberDTO getCommon_mcdata_userdto() {
        return common_mcdata_userdto;
    }

    public void setCommon_mcdata_userdto(KnXDMMemberDTO common_mcdata_userdto) {
        this.common_mcdata_userdto = common_mcdata_userdto;
    }

    public String getCommon_mco_name() {
        return common_mco_name;
    }

    public void setCommon_mco_name(String common_mco_name) {
        this.common_mco_name = common_mco_name;
    }

    public KnXDMMemberDTO getCommon_fd_userdto() {
        return common_fd_userdto;
    }

    public void setCommon_fd_userdto(KnXDMMemberDTO common_fd_userdto) {
        this.common_fd_userdto = common_fd_userdto;
    }

    public String getCommon_trxcontrol_maxdata1to1() {
        return common_trxcontrol_maxdata1to1;
    }

    public void setCommon_trxcontrol_maxdata1to1(String common_trxcontrol_maxdata1to1) {
        this.common_trxcontrol_maxdata1to1 = common_trxcontrol_maxdata1to1;
    }

    public String getCommon_trxcontrol_maxtime1to1() {
        return common_trxcontrol_maxtime1to1;
    }

    public void setCommon_trxcontrol_maxtime1to1(String common_trxcontrol_maxtime1to1) {
        this.common_trxcontrol_maxtime1to1 = common_trxcontrol_maxtime1to1;
    }


    public KnXDMGroupDTO getCommon_grpemergalert_userdto() {
        return common_grpemergalert_userdto;
    }

    public void setCommon_grpemergalert_userdto(KnXDMGroupDTO common_grpemergalert_userdto) {
        this.common_grpemergalert_userdto = common_grpemergalert_userdto;
    }

    public Collection<KnXDMMemberDTO> getCommon_one2one_userdto() {
        return common_one2one_userdto;
    }

    public void setCommon_one2one_userdto(Collection<KnXDMMemberDTO> common_one2one_userdto) {
        this.common_one2one_userdto = common_one2one_userdto;
    }

    public String getCommon_one2one_proentry_discoverygroupid() {
        return common_one2one_proentry_discoverygroupid;
    }

    public void setCommon_one2one_proentry_discoverygroupid(String common_one2one_proentry_discoverygroupid) {
        this.common_one2one_proentry_discoverygroupid = common_one2one_proentry_discoverygroupid;
    }

    public String getCommon_one2one_proentry_userinfoid() {
        return common_one2one_proentry_userinfoid;
    }

    public void setCommon_one2one_proentry_userinfoid(String common_one2one_proentry_userinfoid) {
        this.common_one2one_proentry_userinfoid = common_one2one_proentry_userinfoid;
    }

    public KnXDMMemberDTO getCommon_one2one_kmsuri_userdto() {
        return common_one2one_kmsuri_userdto;
    }

    public void setCommon_one2one_kmsuri_userdto(KnXDMMemberDTO common_one2one_kmsuri_userdto) {
        this.common_one2one_kmsuri_userdto = common_one2one_kmsuri_userdto;
    }

    public Collection<KnXDMMemberDTO> getCommon_one2one_anyext_userdto() {
        return common_one2one_anyext_userdto;
    }

    public void setCommon_one2one_anyext_userdto(Collection<KnXDMMemberDTO> common_one2one_anyext_userdto) {
        this.common_one2one_anyext_userdto = common_one2one_anyext_userdto;
    }

    public String getCommon_one2one_anyext_proentry_discoverygroupid() {
        return common_one2one_anyext_proentry_discoverygroupid;
    }

    public void setCommon_one2one_anyext_proentry_discoverygroupid(String common_one2one_anyext_proentry_discoverygroupid) {
        this.common_one2one_anyext_proentry_discoverygroupid = common_one2one_anyext_proentry_discoverygroupid;
    }

    public String getCommon_one2one_anyext_proentry_userinfoid() {
        return common_one2one_anyext_proentry_userinfoid;
    }

    public void setCommon_one2one_anyext_proentry_userinfoid(String common_one2one_anyext_proentry_userinfoid) {
        this.common_one2one_anyext_proentry_userinfoid = common_one2one_anyext_proentry_userinfoid;
    }

    public KnXDMMemberDTO getCommon_one2one_anyext_kmsuri_userdto() {
        return common_one2one_anyext_kmsuri_userdto;
    }

    public void setCommon_one2one_anyext_kmsuri_userdto(KnXDMMemberDTO common_one2one_anyext_kmsuri_userdto) {
        this.common_one2one_anyext_kmsuri_userdto = common_one2one_anyext_kmsuri_userdto;
    }

    public Collection<KnXDMMCSGroupInfoDTO> getMcsgrpInfo() {return mcsgrpInfo; }

    public void setMcsgrpInfo(Collection<KnXDMMCSGroupInfoDTO> mcsgrpInfo) {
        this.mcsgrpInfo = mcsgrpInfo;
    }

    public String getOnnetwork_relpresentpriority() {
        return onnetwork_relpresentpriority;
    }

    public void setOnnetwork_relpresentpriority(String onnetwork_relpresentpriority) {
        this.onnetwork_relpresentpriority = onnetwork_relpresentpriority;
    }

    public String getOnnetwork_maxaffiliationsN2() {
        return onnetwork_maxaffiliationsN2;
    }

    public void setOnnetwork_maxaffiliationsN2(String onnetwork_maxaffiliationsN2) {
        this.onnetwork_maxaffiliationsN2 = onnetwork_maxaffiliationsN2;
    }

    public KnXDMMemberDTO getOnnetwork_implicitaffiliations_usertdto() {
        return onnetwork_implicitaffiliations_usertdto;
    }

    public void setOnnetwork_implicitaffiliations_usertdto(KnXDMMemberDTO onnetwork_implicitaffiliations_usertdto) {
        this.onnetwork_implicitaffiliations_usertdto = onnetwork_implicitaffiliations_usertdto;
    }

    public KnXDMMemberDTO getOnnetwork_one2oneemergencyalert_dto() {
        return onnetwork_one2oneemergencyalert_dto;
    }

    public void setOnnetwork_one2oneemergencyalert_dto(KnXDMMemberDTO onnetwork_one2oneemergencyalert_dto) {
        this.onnetwork_one2oneemergencyalert_dto = onnetwork_one2oneemergencyalert_dto;
    }

    public String getActiveFS() {
        return activeFS;
    }

    public void setActiveFS(String activeFS) {
        this.activeFS = activeFS;
    }

    public String getAliasMdn() {
        return aliasMdn;
    }

    public void setAliasMdn(String aliasMdn) {
        this.aliasMdn = aliasMdn;
    }

    public String getOldActiveFS() {
        return oldActiveFS;
    }

    public void setOldActiveFS(String oldActiveFS) {
        this.oldActiveFS = oldActiveFS;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public Integer getUserProfileIndex() { return userProfileIndex; }

    public void setUserProfileIndex(Integer userProfileIndex) { this.userProfileIndex = userProfileIndex; }

    @Override
    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(super.toString());
        strBuffer.append(
                "xui_uri='" + xui_uri + '\'' +
                ", name='" + KnGDPRTemplate.name(name) + '\'' +
                ", status=" + status +
                ", profileName='" + profileName + '\'' +
                ", activeFS='" + activeFS + '\'' +
                ", aliasMdn='" + KnGDPRTemplate.mdn(aliasMdn) + '\'' +
                ", pre_selected_indication='" + pre_selected_indication + '\'' +
                ", common_useralias_name='" + common_useralias_name + '\'' +
                ", common_mcdata_userdto=" + common_mcdata_userdto +
                ", common_mco_name='" + common_mco_name + '\'' +
                ", common_fd_userdto=" + common_fd_userdto +
                ", common_trxcontrol_maxdata1to1='" + common_trxcontrol_maxdata1to1 + '\'' +
                ", common_trxcontrol_maxtime1to1='" + common_trxcontrol_maxtime1to1 + '\'' +
                ", common_grpemergalert_userdto=" + common_grpemergalert_userdto +
                ", common_one2one_userdto=" + common_one2one_userdto +
                ", common_one2one_proentry_discoverygroupid='" + common_one2one_proentry_discoverygroupid + '\'' +
                ", common_one2one_proentry_userinfoid='" + common_one2one_proentry_userinfoid + '\'' +
                ", common_one2one_kmsuri_userdto=" + common_one2one_kmsuri_userdto +
                ", common_one2one_anyext_userdto=" + common_one2one_anyext_userdto +
                ", common_one2one_anyext_proentry_discoverygroupid='" + common_one2one_anyext_proentry_discoverygroupid + '\'' +
                ", common_one2one_anyext_proentry_userinfoid='" + common_one2one_anyext_proentry_userinfoid + '\'' +
                ", common_one2one_anyext_kmsuri_userdto=" + common_one2one_anyext_kmsuri_userdto +
                ", KnXDMMCSGroupInfoDTO_mcsgrpInfo=" + mcsgrpInfo +
                ", onnetwork_relpresentpriority='" + onnetwork_relpresentpriority + '\'' +
                ", onnetwork_maxaffiliationsN2='" + onnetwork_maxaffiliationsN2 + '\'' +
                ", onnetwork_implicitaffiliations_usertdto=" + onnetwork_implicitaffiliations_usertdto +
                        ", oldActiveFS=" + oldActiveFS +
                        ", corpId=" + corpId +
                        ", onnetwork_one2oneemergencyalert_dto=" + onnetwork_one2oneemergencyalert_dto +
                        ", userProfileIndex=" + userProfileIndex);
        return strBuffer.toString();
    }
}
