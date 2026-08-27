/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.helper;

import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.dao.KnConnectionException;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.jaxb.beans.KnJAXBParser;
import com.kodiak.utilities.jaxb.beans.KnJAXBProcessException;
import com.kodiak.utilities.jaxb.beans.xcap.resourcelist.EntryType;
import com.kodiak.utilities.jaxb.beans.xcap.resourcelist.ListType;
import com.kodiak.utilities.jaxb.beans.xcap.resourcelist.ResourceLists;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.business.helper.KnProfileInfoUtil;
import com.kodiak.xdms.server.common.configuration.cache.ICacheManager;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dao.persister.db.tables.KnTablesRegistry;
import com.kodiak.xdms.server.common.dao.persister.db.tables.xdm.KnXDMCorpGroupMemberListDAO;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.persistdat.KnCorpUserProfileDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.common.resources.KnCacheKeys;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.pubmgmt.business.KnPubBOException;
import com.kodiak.xdms.server.pubmgmt.dao.KnPubFactorySelector;
import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubXdmDAO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubContactDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubGroupDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOpPubResponse;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDetailsDTO;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubContactDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubContactInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubContactPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.kodiak.common.resources.KnConstants.OIDC_XCAP_ROOT;
import static com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_13;
import static com.kodiak.common.resources.KnConstants.XCAP_ROOT;
import static com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_CORPORATE_PROFILE;
import static com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_THIRD_PARTY_ACCOUNT;
import static com.kodiak.xdms.server.pubmgmt.resources.KnConstants.PUBLIC_GROUP_URI;
import static com.kodiak.xdms.server.pubmgmt.resources.KnConstants.TEL_COLON_PLUS;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubInfoUtil.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 12, 2011           7.0
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
public class KnPubInfoUtil {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPubInfoUtil.class);

    private static KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
    private KnProfileInfoUtil profileHandler;
    private String xcapDirGroupFolder = "xcap-directory/folder%5B@auid=%22org.openmobilealliance.groups%22%5D";

    public KnPubInfoUtil() {
        KnProfileInfoUtil.initialize(KnProfileTypes.PUBLIC_PROFILE, 500, 600);
        profileHandler = KnProfileInfoUtil.getInstance(KnProfileTypes.PUBLIC_PROFILE);
    }

    public String getXcapDirGroupFolder() {
        return xcapDirGroupFolder;
    }

    //private static KnXDMSServiceConfigDTO xdmsServiceConfigDTO = null;


    /**
     * This method retrieves the profile details for user. Based on the isTrue flag it either deeps into
     * DB or try to get it from cache.
     *
     * @param mdn         the user MDN
     * @param profileType the profile type of the user
     * @param isTrue      the flag whether to skip the cache cheking
     * @return profile details
     * @throws KnPubBOException exception
     */
    public KnSubsProfileDTO getProfileDetails(String mdn, String profileType, boolean isTrue, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnPubBOException {
        String methodName = "getProfileDetails(String, String, boolean, persisterTxn)";
        try {
            knLogger.debug(methodName, "Fetching profile details from DB. isTrue ->", isTrue);
            return profileHandler.getProfileDetails(mdn, profileType, isTrue, readOnly, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile!");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve user profile!", e);
        }
    }

    public KnPocSubsAddlInfoDTO getSubsAddlDetails(String mdn, KnPersisterTxn persisterTxn)
            throws KnPubBOException {
        String methodName = "getSubsAddlDetails(String, persisterTxn)";
        try {
            return profileHandler.getSubsAddlDetails(mdn,persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile addl details.");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve user profile addl details.", e);
        }
    }

    public KnEmergencyInfoDTO getEmergencySubsDestInfo(String mdn, KnPersisterTxn persisterTxn)
            throws KnPubBOException {
        String methodName = "getEmergencySubsDestinfo(String, persisterTxn)";
        try {
            return profileHandler.getEmergencySubsDestInfo(mdn,persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile addl details.");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve user profile addl details.", e);
        }
    }

    public KnSubsProfileDTO getProfileDetailsByMcpttID(String mcpttId,KnPersisterTxn persisterTxn)
            throws KnPubBOException {
        String methodName = "getProfileDetailsByMcpttID(String, persisterTxn)";
        try {
            knLogger.debug(methodName, "Fetching profile details from DB.");
            return profileHandler.getProfileDetailsByMcpttID(mcpttId, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile!");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve user profile!", e);
        }
    }

    public KnSubsProfileDTO getProfileDetailsByMcIDAndUpmIndex(String mcId,String upmIndex,KnPersisterTxn persisterTxn)
            throws KnPubBOException {
        String methodName = "getProfileDetailsByMcIDAndUpmIndex(String,String,persisterTxn)";
        try {
            knLogger.debug(methodName, "Fetching profile details from DB.");
            return profileHandler.getProfileDetailsByMcIDAndUpmIndex(mcId,upmIndex, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile!");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve user profile!", e);
        }
    }

    public List<KnSubsProfileDTO> getProfileDetailsListByMcID(String mcId,KnPersisterTxn persisterTxn)
            throws KnPubBOException {
        String methodName = "getProfileDetailsListByMcID(String,persisterTxn)";
        try {
            knLogger.debug(methodName, "Fetching profile details from DB.");
            return profileHandler.getProfileDetailsListByMcID(mcId, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile!");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve user profile!", e);
        }
    }

    public KnCorpUserProfileDTO getUserProfileNameByindex(int corpId, int upmIndex, KnPersisterTxn persisterTxn)
            throws KnPubBOException {
        String methodName = "getUserProfileNameByindex(int,int,persisterTxn)";
        try {
            knLogger.debug(methodName, "Fetching profile details from DB. corpId",corpId," upmIndex ",upmIndex);
            return profileHandler.getUserProfileNameByindex(corpId,upmIndex, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile!");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve user profile!", e);
        }
    }

    public Map<String, KnSubsProfilePersistDTO> getSubscriberDetails(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnPubBOException {
        String methodName = "getSubscriberDetails()";
        try {
            knLogger.debug(methodName, "Fetching profile details for mdnList : ->", KnGDPRTemplate.mdnList(mdnList));
            return profileHandler.getSubscriberProfileDetails(mdnList, readOnly, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile!");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve user profile!", e);
        }
    }

    /**
     * This method is used to retrieve the user profile.
     *
     * @param mdn          subscriber number
     * @param profileType  type of the profile - Public
     * @param persisterTxn
     * @return returns subscriber profile information for given subscriber number
     * @throws KnPubBOException throws bo exception
     */
    public KnSubsProfileDTO getProfileDetails(String mdn, String profileType,
                                              KnPersisterTxn persisterTxn) throws KnPubBOException {
        String methodName = "getProfileDetails(String, String, KnPersisterTxn)";
        try {
            return profileHandler.getProfileDetails(mdn, profileType, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile!");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve user profile!", e);
        }
    }

    public KnCorpProfileDTO getCorpProfileDetails(String corpId, String profileType,
                                              KnPersisterTxn persisterTxn) throws KnPubBOException {
        String methodName = "getProfileDetails(String, String, KnPersisterTxn)";
        try {
            return profileHandler.getProfileDetails(corpId, profileType, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile!");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve user profile!", e);
        }
    }

    /**
     * @return
     */
    public KnXDMSServiceConfigDTO getXdmsServiceConfig() {
        String methodName = "getXdmsServiceConfig";
        knLogger.debug(methodName, "Entry : ");
        KnXDMSServiceConfigDTO xdmsServiceConfigDTO = new KnXDMSServiceConfigDTO();
            knLogger.debug(methodName, "Fetching XDMS Service Config : ");
            try {
                xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(
                        genInfoUtil.retrieveLocalXDMPttServerId(), null);
            } catch (KnBOException boe) {
                knLogger.warn(methodName, "Exception in fetching xdm service config : ");
            }
        return xdmsServiceConfigDTO;
    }

    /**
     * @param ipContactDTO //     * @throws KnPubBOException
     */
    public void populateContactInfoPersistDTO(KnIPPubContactDTO
                                                      ipContactDTO, KnPubContactInfoPersistDTO contactPersistDTO) {

        contactPersistDTO.setOwner(ipContactDTO.getOwner());
        contactPersistDTO.setContactListName(ipContactDTO.getContactListName());
        contactPersistDTO.setInputDTO(ipContactDTO);
        contactPersistDTO.setMaxNumberOfMembers(getXdmsServiceConfig().getMaxPublicContactsPerSubs());
    }


    /**
     * @param ipContactDTO
     */
    public void populateContactPersistDTO(KnIPPubContactDTO
                                                  ipContactDTO, KnPubContactPersistDTO contactPersistDTO) {

        contactPersistDTO.setOwner(ipContactDTO.getOwner());
        contactPersistDTO.setContactListName(ipContactDTO.getContactListName());
        contactPersistDTO.setInputDTO(ipContactDTO);
    }

    /**
     * @param ipGroupDTO
     * @return
     */
    public KnPubGroupInfoPersistDTO populateGroupInfoPersistDTO(KnIPPubGroupDTO
                                                                        ipGroupDTO, KnPubGroupInfoPersistDTO groupPersistDTO) {//throws KnPubBOException {

//        KnPubGroupInfoPersistDTO groupPersistDTO = new KnPubGroupInfoPersistDTO();
        groupPersistDTO.setOwner(ipGroupDTO.getOwner());
        groupPersistDTO.setGroupName(ipGroupDTO.getGroupName());
        groupPersistDTO.setGroupDisplayName(ipGroupDTO.getGroupDisplayName());
        groupPersistDTO.setInputDTO(ipGroupDTO);
        groupPersistDTO.setMaxNumberOfMembers(getXdmsServiceConfig().getMaxMembersPerPublicPOCGrp());

        return groupPersistDTO;
    }

    /**
     * @param ipGroupDTO
     * @return
     */
    public KnPubGroupPersistDTO populateGroupPersistDTO(KnIPPubGroupDTO
                                                                ipGroupDTO, KnPubGroupPersistDTO groupPersistDTO) {//throws KnPubBOException {

//        KnPubGroupPersistDTO groupPersistDTO = new KnPubGroupPersistDTO();
        groupPersistDTO.setOwner(ipGroupDTO.getOwner());
        groupPersistDTO.setGroupName(ipGroupDTO.getGroupName());
        groupPersistDTO.setInputDTO(ipGroupDTO);

        return groupPersistDTO;
    }


    /**
     * @return
     */
    public KnOpPubResponse populateSuccessResponse() {
        KnOpPubResponse respDTO = new KnOpPubResponse();

        return respDTO;
    }

    /**
     * @param ipPubContactDTO
     * @return
     */
    public KnPubContactDTO populateContactListSuccessResponse(KnIPPubContactDTO ipPubContactDTO) {
        KnPubContactDTO contactDTO = new KnPubContactDTO();

        contactDTO.setOwner(ipPubContactDTO.getOwner());
        contactDTO.setContactListName(ipPubContactDTO.getContactListName());
        return contactDTO;
    }

    /**
     * @param docDispName
     * @return
     */
    // /resource-lists/list[@name="oma_pocbuddylist"]/display-name
    // /resource-lists/list[@name="oma_pocbuddylist"]/entry[@uri="tel:919000000001"]
    // /resource-lists/list[@name="oma_pocbuddylist"]/entry[@uri="tel:919000000001"]/display-name
    public String constructContaStringctListXPath(String docDispName, String uri, String conatactName) {

        String methodName = "constructXPath";
        StringBuffer xPath = new StringBuffer(100);
        knLogger.debug(methodName, "Doc Name : " + docDispName);
        knLogger.debug(methodName, "URI : " + KnGDPRTemplate.mdn(uri));

        if (docDispName != null) {
            xPath.append("/resource-lists/list[@name='").append(decodeSpecialChars(docDispName)).append("']");
        }

        if (uri != null) {
            xPath.append("/entry[@uri='").append(uri).append("']");

            if (conatactName != null) {
                xPath.append("/display-name/text()");
            }
        } else {
            xPath.append("/display-name");
        }

        knLogger.debug(methodName, "Constructed XPath : " + xPath.toString());
        return xPath.toString();
    }

    /**
     * constructs the group member entry URI xpath, If display name is passed, then modify member name xpath
     * is constructred
     *
     * @param memberMdn
     * @return
     */
    // /group/list-service/list/entry[@uri="tel:919000000001"]
    public String constructGroupMemberEntryXPath(String memberMdn, String memDisplayName) {

        String methodName = "constructGroupMemberEntryXPath";
        StringBuffer xPath = new StringBuffer(100);
        knLogger.debug(methodName, "memberMdn : " + KnGDPRTemplate.mdn(memberMdn) + "memDisplayName : " +
        		KnGDPRTemplate.name(memDisplayName));

        xPath.append("/group/list-service/list");

        if (memberMdn != null) {
            xPath.append("/entry[@uri=\'").append(memberMdn).append("\']");
        }

        if (memDisplayName != null) {
            xPath.append("/*['rl:display-name']/text()");
        }

        knLogger.debug(methodName, "Constructed XPath : " + xPath.toString());
        return xPath.toString();
    }

    /**
     * constructs the group display name xpath
     *
     * @return
     */
    // /group/list-service/display-name
    public String constructGroupDisplayNameXPath() {

        String methodName = "constructGroupDisplayNameXPath";
        StringBuffer xPath = new StringBuffer(100);

        xPath.append("/group/list-service/display-name/text()");

        knLogger.debug(methodName, "Constructed XPath : " + xPath.toString());
        return xPath.toString();
    }

    /**
     * @return
     */
    public String constructChangeMdnXPath(String listServiceUri) {

        String methodName = "constructChangeMdnXPath";
        StringBuffer xPath = new StringBuffer(100);

        xPath.append("/group/list-service[@uri='").append(listServiceUri).append("']");

        knLogger.debug(methodName, "Constructed XPath : " + xPath.toString());
        return xPath.toString();
    }

    /**
     * @param memberDetails
     * @return <TODO this needs to be changed to Jibx parsing
     */
    public String constructXMLStringForContactMembers(Collection<KnMemberDetailsDTO> memberDetails) {

        String methodName = "constructXMLStringForContactMembers";
        knLogger.debug(methodName, "constructing XML for DTOs : " + memberDetails);

        StringBuffer strBuff = new StringBuffer(100);
        for (KnMemberDetailsDTO memDetail : memberDetails) {
            // Space before "<entry uri" in very very important
            // parsing from Handset client will without this space.
            strBuff.append("  <entry uri=\"").append(memDetail.getUri()).append("\">");
            strBuff.append("   <display-name>").append(encodeSpecialChars(memDetail.getDisplayName())).append("</display-name>");
            strBuff.append("   </entry> ");
        }

        knLogger.debug(methodName, "strXML : " + strBuff.toString());
        return strBuff.toString();
    }


    /**
     * @param mdn
     * @return
     */
    public String constructContactListSelUri(String mdn) {

        String methodName = "constructContactListSelUri";

        knLogger.debug(methodName, "generating SEL uri of Notification for mdn - " + KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append("xcap-directory/folder%5B@auid=%22resource-lists%22%5D");
        strBuffer.append("/entry%5B@uri=%22resource-lists/users/tel:+");
        strBuffer.append(mdn);
        strBuffer.append("/index%22%5D/@etag");

        knLogger.debug(methodName, "generated SEL uri of Notification for mdn - " +
        		KnGDPRTemplate.mdn(mdn) + " is - " + KnGDPRTemplate.mdnUriTemplate(strBuffer.toString()));
        return strBuffer.toString();
    }


    /**
     * @param mdn
     * @return
     */
    public String constructGroupXcapUri(String mdn, String groupName, int pv) {

        String methodName = "constructGroupXcapUri(String, String";

        knLogger.debug(methodName, "generating Group Xcap uri for mdn - " + KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(100);
        if(pv >= PROTOCOL_VERSION_13 ) {
            strBuffer.append(OIDC_XCAP_ROOT+"/org.openmobilealliance.groups/users/tel:+");
        }else{
            strBuffer.append(XCAP_ROOT+"/org.openmobilealliance.groups/users/tel:+");

        }
        strBuffer.append(mdn);
        strBuffer.append("/");
        strBuffer.append(decodeSpecialChars(groupName));
        strBuffer.append(".xml");

        knLogger.debug(methodName, "generated Group Xcap uri of Notification for mdn - " +
        		KnGDPRTemplate.mdn(mdn) + " is - " + KnGDPRTemplate.mdnUriTemplate(strBuffer.toString()));
        return strBuffer.toString();
    }

    /**
     * @param mdn
     * @param groupName
     * @return
     */
    public String constructGroupSelUri(String mdn, String groupName) {

        String methodName = "constructGroupSelUri";

        knLogger.debug(methodName, "generating SEL uri of Notification for mdn - " +KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append(xcapDirGroupFolder);
        strBuffer.append("/entry%5B@uri=%22org.openmobilealliance.groups/users/tel:+");
        strBuffer.append(mdn);
        strBuffer.append("/").append(decodeSpecialChars(groupName)).append(".xml%22%5D/@etag");

        knLogger.debug(methodName, "generated SEL uri of Notification for mdn - " +
        		KnGDPRTemplate.mdn(mdn)+ " is - " + KnGDPRTemplate.mdnUriTemplate(strBuffer.toString()));
        return strBuffer.toString();
    }


    /**
     * @param mdn
     * @param groupName
     * @return
     */
    public String constructGroupDelUri(String mdn, String groupName) {

        String methodName = "constructGroupDelUri";

        knLogger.debug(methodName, "generating remove SEL uri of Notification for mdn - " + KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append(xcapDirGroupFolder);
        strBuffer.append("/entry%5B@uri=%22org.openmobilealliance.groups/users/tel:+");
        strBuffer.append(mdn);
        strBuffer.append("/").append(decodeSpecialChars(groupName)).append(".xml%22%5D");

        knLogger.debug(methodName, "generated remove SEL uri of Notification for mdn - " +
        		KnGDPRTemplate.mdn(mdn) + " is - " + KnGDPRTemplate.mdnUriTemplate(strBuffer.toString()));
        return strBuffer.toString();
    }


    /**
     * construct the entry uri value for the diff add (new group addition) for notification
     *
     * @param mdn
     * @param groupName
     * @return
     */
    public String constructAddGroupEntryUri(String mdn, String groupName) {
        String methodName = "constructAddGroupEntryUri";

        knLogger.debug(methodName, "generating ENTRY uri of Notification for mdn - " + KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append("org.openmobilealliance.groups/users/tel:+");
        strBuffer.append(mdn);
        strBuffer.append("/").append(decodeSpecialChars(groupName)).append(".xml");

        knLogger.debug(methodName, "generated ENTRY uri of Notification for mdn - " +
        		KnGDPRTemplate.mdn(mdn)+ " is - " + KnGDPRTemplate.mdnUriTemplate(strBuffer.toString()));
        return strBuffer.toString();
    }

    /**
     * This method is used to retrieve number of groups for user. MDN and group Type are compulsary.
     * Group name will be used if its not null.
     *
     * @param mdn
     * @param groupName
     * @return
     * @throws KnPubBOException
     */
    public int getGroupNameCount(String mdn, String groupName) throws KnPubBOException {
        String methodName = "getGroupNameCount(mdn, groupName, groupType, pttserverId)";
        knLogger.debug(methodName, "ENTRY -> Received inputs MDN : " + KnGDPRTemplate.mdn(mdn) +
                " Group name : " + groupName);
        int numberOfGroups = -1;
        KnPersisterTxn persisterTxn = null;

        try {
            IPubXdmDAO pubXdmDao = KnPubFactorySelector.getDAOFactory(KnPubFactorySelector.DB).createXdmServerDAO(
                    genInfoUtil.retrieveLocalXDMPttServerId());
            knLogger.debug(methodName, "Retrieving number of groups for subscriber  " + KnGDPRTemplate.mdn(mdn));
            numberOfGroups = pubXdmDao.countNumberOfGroupsOwnedByMDN(mdn, groupName, persisterTxn);
            knLogger.debug(methodName, "Number of groups for subscriber  " + KnGDPRTemplate.mdn(mdn) + " = " + numberOfGroups);

            return numberOfGroups;
        } catch (KnDBConnectionException e) {
            knLogger.error(methodName, "DBConnectionException - " + e.getMessage());
            throw new KnPubBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, "PTT Server not reachable..", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAOException - " + e.getMessage());
            throw new KnPubBOException(KnErrorCodes.BOEntity.NO_GROUPS_FOUND, "Error while retrieving no of groups.", e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnPubBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Error while retrieving number of groups.", e);
        } finally {
            knLogger.info(methodName, "Successfully retrieved number of groups  - " + numberOfGroups);
        }

    }


    /**
     * @param mdn
     * @return
     * @throws KnPubBOException
     */
    public int getContactMemberCount(String mdn) throws KnPubBOException {
        String methodName = "getContactMemberCount(mdn";
        knLogger.debug(methodName, "ENTRY -> Received inputs MDN : " + KnGDPRTemplate.mdn(mdn));
        int numberOfContactMems = -1;
        KnPersisterTxn persisterTxn = null;

        try {
            IPubXdmDAO pubXdmDao = KnPubFactorySelector.getDAOFactory(KnPubFactorySelector.DB).createXdmServerDAO(
                    genInfoUtil.retrieveLocalXDMPttServerId());
            knLogger.debug(methodName, "Retrieving number of Contacts members for subscriber  " + KnGDPRTemplate.mdn(mdn));
            numberOfContactMems = pubXdmDao.countNumberOfContactMembers(mdn, persisterTxn);
            knLogger.debug(methodName, "Number of contact mems for subscriber  " + KnGDPRTemplate.mdn(mdn) + " = " + numberOfContactMems);

            return numberOfContactMems;
        } catch (KnDBConnectionException e) {
            knLogger.error(methodName, "DBConnectionException - " + e.getMessage());
            throw new KnPubBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, "PTT Server not reachable..", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAOException - " + e.getMessage());
            throw new KnPubBOException(KnErrorCodes.BOEntity.CONTACT_LIST_DOES_NOT_EXISTS, "Error while retrieving no of contact mems.", e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnPubBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Error while retrieving number of contact mems.", e);
        } finally {
            knLogger.info(methodName, "Successfully retrieved number of contact mems  - " + numberOfContactMems);
        }

    }

    public String generateResourceListXML(List<KnXDMMdnInfoDTO> mdnInfoDTOList) throws KnPubBOException {
        final String methodName = "generateResourceListXML(mdnInfoDTOList)";
        knLogger.debug(methodName, "Entry point.");
        ResourceLists resourceLists = new ResourceLists();
        String xmlObject = null;

        if (null != mdnInfoDTOList) {
        	ArrayList<ListType> lists = new ArrayList<ListType>();
        	ListType list = new ListType();
        	list.setName(KnConstants.XCAP_DEFAULT_CONTACTLIST_NAME);
        	list.setDisplayName(KnConstants.XCAP_DEFAULT_CONTACTLIST_NAME);
        	ArrayList<EntryType> entries = new ArrayList<>();
        	for (KnXDMMdnInfoDTO contact : mdnInfoDTOList) {
        		EntryType entry = new EntryType();
        		entry.setUri(KnConstants.TELURI + contact.getMdn());
        		//Multilingual revert changes
        		try {
					entry.setDisplayName(new String(contact.getName().getBytes("UTF-8"),"8859_1"));//(contact.getName());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
        		entries.add(entry);
        	}
        	list.setEntries(entries);
        	lists.add(list);
        	resourceLists.setList(lists);
        	knLogger.debug(methodName, "Exit Point.Response sent is : ", resourceLists);
        }
        try {
            xmlObject = KnJAXBParser.convertBeanToXml(resourceLists, ResourceLists.class);
        } catch (KnJAXBProcessException e) {
            knLogger.error(methodName, "Failed to generate the XML object");
            throw new KnPubBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e);
        }
        knLogger.debug(methodName, "XML generated - ", xmlObject);
        try {
			xmlObject = new String(xmlObject.getBytes("8859_1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			knLogger.error(methodName, "Failed to generate the XML object");
            throw new KnPubBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e);
		}
        knLogger.debug(methodName, "XML generated - ", xmlObject);
        return xmlObject;

    }

    public String encodeSpecialChars(String str) {
        if (str != null && !str.isEmpty()) {
            return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("'", "&apos;").replaceAll("\"", "&quot;");
        }
        return str;
    }

    public String decodeSpecialChars(String str) {
        if (str != null && !str.isEmpty()) {
            return str.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&apos;", "'").replaceAll("&quot;", "\"").replaceAll("&amp;", "&");
        }
        return str;
    }

    public String constructListServiceUri(String mdn, String groupName) {
        String methodName = "constructGroupUri(String, String)";
        knLogger.debug(methodName, "generating List service uri for mdn - " + KnGDPRTemplate.mdn(mdn));
        StringBuffer stringBuffer = new StringBuffer(100);
        stringBuffer.append(TEL_COLON_PLUS);
        stringBuffer.append(mdn);
        stringBuffer.append(PUBLIC_GROUP_URI);
        stringBuffer.append(groupName);
        knLogger.debug(methodName, "generated List service uri for mdn - " ,KnGDPRTemplate.mdn(mdn) , " is - " , KnGDPRTemplate.mdnListServiceUriTemplate(stringBuffer.toString()));
        return stringBuffer.toString();
    }

    /**
     * Method to retrieve the corporate details by external corporate ID.
     * @param extCorpId
     * @param persisterTxn
     * @return
     * @throws KnPubBOException
     */
    public KnCorpProfileDTO getCorpProfileDetails(String extCorpId, KnPersisterTxn persisterTxn) throws KnPubBOException {
        String methodName = "getCorpProfileDetails(extCorpId)";
        knLogger.debug(methodName, "ENTRY -> Received inputs extCorpId : " + extCorpId);
        KnCorpProfileDTO corpProfileDTO;
        try {
            IPubXdmDAO pubXdmDao = KnPubFactorySelector.getDAOFactory(KnPubFactorySelector.DB).createXdmServerDAO(
                    genInfoUtil.retrieveLocalXDMPttServerId());
            knLogger.debug(methodName, "Fetching corp profile details from DB");
            corpProfileDTO = pubXdmDao.getCorpProfileDetails(extCorpId, persisterTxn);
            if(corpProfileDTO == null){
                knLogger.error(methodName, "Corporate profile does not exist!");
                throw new KnPubBOException(INVALID_CORPORATE_PROFILE, "Corporate profile does not exist");
            }
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve corp profile!");
            throw new KnPubBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAOException - " + e.getMessage());
            throw new KnPubBOException(e.getErrorCode(), e.getMessage(), e);
        }
        return corpProfileDTO;
    }

    public int getTPId(String vendorId, KnPersisterTxn persisterTxn) throws KnPubBOException {
        String methodName = "getTPId(String)";
        knLogger.debug(methodName, "ENTRY -> Received inputs vendorId : " + vendorId);
        int tpId;
        try {
            IPubXdmDAO pubXdmDao = KnPubFactorySelector.getDAOFactory(KnPubFactorySelector.DB).createXdmServerDAO(
                    genInfoUtil.retrieveLocalXDMPttServerId());
            tpId = pubXdmDao.getTPId(vendorId, persisterTxn);
            knLogger.debug(methodName, "tpId - ", tpId);
            if(tpId == 0){
                knLogger.error(methodName, "Third party account does not exist!");
                throw new KnPubBOException(INVALID_THIRD_PARTY_ACCOUNT, "Third party account does not exist");
            }
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to third party profile!");
            throw new KnPubBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAOException - " + e.getMessage());
            throw new KnPubBOException(e.getErrorCode(), e.getMessage(), e);
        }
        return tpId;
    }

    /**
     * method to generate the Subscriber config doc Sel Uri as per ICD
     *
     * @param mdn String
     * @return String sel uri
     */
    public String generateAuthListSelUri(String mdn) {
        String methodName = "generateAuthListSelUri";

        knLogger.debug(methodName, "generating sel uri for Notification for mdn - ", KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(150);
        strBuffer.append("xcap-directory/folder%5B@auid=%22kn-authorization-list%22%5D/entry%5B@uri=%22kn-authorization-list/users/tel:+");
        strBuffer.append(mdn);
        strBuffer.append("/index%22%5D/@etag");

        knLogger.debug(methodName, "generated sel uri for Notification for mdn - ", KnGDPRTemplate.mdn(mdn), " is - ",KnGDPRTemplate.mdnUriTemplate(strBuffer.toString()));
        return strBuffer.toString();
    }


    public String generateSubsConfigSelUri(String mdn) {
        String methodName = "generateSubsConfigSelUri";

        knLogger.debug(methodName, "generating sel uri for Notification for mdn - ", KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(150);
        strBuffer.append("xcap-directory/folder%5B@auid=%22kn-subscriber-config%22%5D/entry%5B@uri=%22kn-subscriber-config/users/tel:+");
        strBuffer.append(mdn);
        strBuffer.append("/index%22%5D/@etag");

        knLogger.debug(methodName, "generated sel uri for Notification for mdn - ", KnGDPRTemplate.mdn(mdn), " is - ", KnGDPRTemplate.mdnUriTemplate(strBuffer.toString()));
        return strBuffer.toString();
    }

    /**
     *
     * @param mdn
     * @return
     */
    public String generateTGSSListSelUri(String mdn, com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE method) {
        String methodName = "generateTGSSListSelUri";
        knLogger.debug(methodName, "generating TGSS List uri for Notification for mdn - ", KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(150);
        strBuffer.append("xcap-directory/folder%5B@auid=%22kn-tgss-list%22%5D/entry%5B@uri=%22kn-tgss-list/users/tel:+");
        strBuffer.append(mdn);
        strBuffer.append("/index%22%5D");
        //in remove doc case etag should not be part of notification
        if(null!=method && method.equals(KnConstants.DOC_CHANGE_TYPE.REPLACE))
                strBuffer.append("/@etag");
        knLogger.debug(methodName, "generated TGSS List uri for Notification for mdn - ",KnGDPRTemplate.mdn(mdn), " is - ", KnGDPRTemplate.mdnUriTemplate(strBuffer.toString()));

        return  strBuffer.toString();
    }

    /**
     * method to retrive max Simultainous Session Group Count at corporate level
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnPubBOException
     */
    public int getCorpMaxSSDDCount(int corpId, KnPersisterTxn persisterTxn) throws KnPubBOException {
        String methodName = "getCorpMaxSSDDCount(corpId)";
        knLogger.debug(methodName, "ENTRY -> Received inputs corpId : " + corpId);
        int maxSSDYCnt = 0;
        try {
            IPubXdmDAO pubXdmDao = KnPubFactorySelector.getDAOFactory(KnPubFactorySelector.DB).createXdmServerDAO(
                    genInfoUtil.retrieveLocalXDMPttServerId());
            knLogger.debug(methodName, "Fetching MaxSSDCount from DB");
            maxSSDYCnt = pubXdmDao.getCorpMaxSSDDCount(corpId,persisterTxn );

        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve MaxSSDCount!");
            throw new KnPubBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAOException - " + e.getMessage());
            throw new KnPubBOException(e.getErrorCode(), e.getMessage(), e);
        }
        return maxSSDYCnt;
    }

    public int getPocMaxSSDDCount(KnPersisterTxn persisterTxn,String pocPttServerId) throws KnPubBOException {
        String methodName = "getPocMaxSSDDCount(corpId)";
        knLogger.debug(methodName, "ENTRY ->");
        int maxSSDYCnt = 0;
        try {
            IPubXdmDAO pubXdmDao = KnPubFactorySelector.getDAOFactory(KnPubFactorySelector.DB).createXdmServerDAO(
                    genInfoUtil.retrieveLocalXDMPttServerId());
            knLogger.debug(methodName, "Fetching maxSSDYCnt from DB");
            maxSSDYCnt = pubXdmDao.getPOCMaxSSDDCount(persisterTxn,pocPttServerId);

        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve maxSSDCnt!");
            throw new KnPubBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAOException - " + e.getMessage());
            throw new KnPubBOException(e.getErrorCode(), e.getMessage(), e);
        }
        return maxSSDYCnt;
    }

    //get all fields for DG.POC_SVC_CONFIG
    public KnPOCSvcConfigDTO retrievePOCSvcConfig(String pocPttServerId,KnPersisterTxn persisterTxn) throws KnPubBOException {
        String methodName = "retrievePOCSvcConfig(String)";
        knLogger.debug(methodName, "--->ENTRY: retrieve POC Service Config for pttid - ", pocPttServerId
        );
        KnPOCSvcConfigDTO pocSvcConfigDTO = null;
        Map<String, KnPOCSvcConfigDTO> pocSvcConfigMap;
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            //pocSvcConfigDTO = (KnPOCSvcConfigDTO) cacheManager.get(KnProvCacheKeys.POC_SVC_CONFIG);
            pocSvcConfigMap = (Map<String, KnPOCSvcConfigDTO>) cacheManager.get(KnCacheKeys.POC_SVC_CONFIG);
            if (pocSvcConfigMap != null && pocSvcConfigMap.containsKey(pocPttServerId)) {
                pocSvcConfigDTO = pocSvcConfigMap.get(pocPttServerId);
            }
            knLogger.debug(methodName, "--->retrieved poc svc config DTO from cache - ", pocSvcConfigDTO
            );

            if (pocSvcConfigDTO == null) {
                knLogger.debug(methodName, "--->retrieving the poc svc config from DB");

                IPubXdmDAO pubXdmDao = KnPubFactorySelector.getDAOFactory(KnPubFactorySelector.DB).createXdmServerDAO(
                        genInfoUtil.retrieveLocalXDMPttServerId());
                knLogger.debug(methodName, "--->Fetching POCSVCCONFIG from DB");
                pocSvcConfigDTO = pubXdmDao.retrievePOCSvcConfig(pocPttServerId,persisterTxn);
                if (pocSvcConfigMap == null) {
                    pocSvcConfigMap = new HashMap<String, KnPOCSvcConfigDTO>();
                    pocSvcConfigMap.put(pocPttServerId, pocSvcConfigDTO);
                } else {
                    pocSvcConfigMap.put(pocPttServerId, pocSvcConfigDTO);
                }

                cacheManager.put(KnCacheKeys.POC_SVC_CONFIG, pocSvcConfigMap);
            }

            knLogger.debug(methodName, "--->PoC svc config DTO -  ", pocSvcConfigDTO);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnPubBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnPubBOException(KnErrorCodes.BOEntity.POC_SVC_CONFIG_NOT_FOUND,
                        "POC Svc Config not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e.getMessage());
            throw new KnPubBOException(e.getErrorCode(), e.getMessage(), e);
        } catch (KnPubBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnPubBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get POC service config ", e);
        }
        knLogger.debug(methodName, "EXIT : POC Service Config - ", pocSvcConfigDTO);

        return pocSvcConfigDTO;
    }
    
    
    /**
     *  This method returns List of real Mdns
     *
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<String> getRealMdns(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnPubBOException {
        String methodName = "getRealMdns(List<String>, String, KnPersisterTxn)";
        try {
            return profileHandler.getRealMdns(mdns, readOnly, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile!");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve user profile!", e);
        }
    }

    public KnCorpUserProfileDTO getUserProfileById( String userProfileId)
            throws KnPubBOException {
        String methodName = "getUserProfileNameByindex(int,int,persisterTxn)";
        try {
            knLogger.debug(methodName, "Fetching profile details from DB. corpId",userProfileId," upmIndex ",userProfileId);
            return profileHandler.getUserProfileById(userProfileId);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile!");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve user profile!", e);
        }
    }
    public Set<KnCorpGroupListInfoDTO> retriveGroupProfileInfoByProfileId(String userProfileId,KnPersisterTxn persisterTxn)
            throws KnPubBOException {
        String methodName = "getUserProfileNameByindex(int,int,persisterTxn)";
        try {
            knLogger.debug(methodName, "Fetching profile details from DB. corpId",userProfileId," upmIndex ",userProfileId);
            return profileHandler.retriveGroupProfileInfoByProfileId(userProfileId,persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile!");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve user profile!", e);
        }
    }

    public Map<Integer,Integer> retrivePreConfigGroupProfileInfoByProfileId(String userProfileId,KnPersisterTxn persisterTxn)
            throws KnPubBOException {
        String methodName = "retriveNonPreConfigGroupProfileInfoByProfileId(int,int,persisterTxn)";
        try {
            knLogger.debug(methodName, "Fetching profile details from DB. corpId",userProfileId," upmIndex ",userProfileId);
            return profileHandler.retrivePreConfigGroupProfileInfoByProfileId(userProfileId,persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile!");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve user profile!", e);
        }
    }

    public Map<String, List<String>> getProfileMdnListByBaseMdnsList(List<String> baseMdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnPubBOException {
        String methodName = "getProfileMdnListByBaseMdnsList()";
        try {
            return profileHandler.getProfileMdnListByBaseMdnsList(baseMdnList, readOnly, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve", e);
        }
    }

    public BitSet convertLongToBitSet(long longValue) {
        String methodName = "convertLongToBitSet(long)";
        knLogger.debug(methodName, "ENTRY: Received long value to convert bit set is - ", longValue);

        long value = longValue;

        BitSet bitSet = new BitSet(Long.SIZE);
        int index = 0;
        while (value != 0) {
            if (value % 2L != 0) {
                bitSet.set(index);
            }
            ++index;
            value = value >>> 1;
        }
        knLogger.debug(methodName, "EXIT: Generated BitSet - ", bitSet);
        return bitSet;

    }

    public long convertBitSetToLong(BitSet bitSet) {
        String methodName = "convertBitSetToLong(BitSet)";
        knLogger.debug(methodName, "ENTRY: Received bit set to convert long value is - ", bitSet);

        long longValue = 0L;
        for (int i = 0; i < bitSet.size(); ++i) {
            longValue += bitSet.get(i) ? (1L << i) : 0L;
        }

        knLogger.debug(methodName, "EXIT: Generated Long Value - ", longValue);
        return longValue;
    }
    
    public  Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagUpdate(List<String> userProfileIds, String corpId
            ,List<String> profileMdnList,String exists, String xdmsHome,KnPersisterTxn persisterTxn) throws KnPubBOException {
        String methodName = "profileMdnEtagUpdate()";
        try {
            return profileHandler.profileMdnEtagUpdate(userProfileIds,corpId,profileMdnList,exists,xdmsHome,persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve");
            throw new KnPubBOException(e.getErrorCode(), "Failed to retrieve", e);
        }
    }

    public boolean isReGroupFeatureEnable(String activeFS2) {
            String methodName = "isReGroupFeatureEnable(String)";
            boolean isRegrupFeature = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(activeFS2, com.kodiak.common.resources.KnConstants.FEATURE_SET.MCX_GROUP_REGROUP_FLAG_BIT.value());
            knLogger.debug(methodName, "isRegrupFeature = " + isRegrupFeature);
            return isRegrupFeature;
    }

    /**
     * Verify that shared PreConfigGroup is belonged to member or not
     * @param ownedCorpId
     * @param sharedCorpId
     * @param reqMdn
     * @param xdmDAO
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public boolean subscrBelongsToSharedPreConfGroup(int ownedCorpId, List<Integer> sharedCorpId, String reqMdn, IXDMServerDAO xdmDAO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "subscrBelongsToSharedPreConfGroup(int, List<Integer>, String, IXDMServerDAO, KnPersisterTxn)";
        boolean returnValue =  false;
       List<String> memberMdns = xdmDAO.getSharedGroupMemberBySharedAndOwnCorpids(ownedCorpId,sharedCorpId,persisterTxn);
       for(String mdn : memberMdns)
       {
           knLogger.debug(methodName, " mdn-" , mdn , " reqMdn ", reqMdn);
           if(mdn.trim().equalsIgnoreCase(reqMdn.trim()))
               returnValue = true;
       }

       knLogger.debug(methodName, " Subscriber is member of shared GroupId. ownedCorpId -", ownedCorpId, " sharedCorpId" ,sharedCorpId,
           " reqMdn ", reqMdn , " returnValue ", returnValue);
       return  returnValue;
    }

    public List<Integer> getUserProfileGroupIdBySharedCorpId(int sharedCorpId, IXDMServerDAO xdmDAO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUserProfileGroupIdBySharedCorpId(int, IXDMServerDAO, KnPersisterTxn)";
        boolean returnValue =  false;
        List<Integer> grpIds = xdmDAO.getUserProfileGroupIdBySharedCorpId(sharedCorpId,persisterTxn);

        knLogger.debug(methodName, " grpIds" + grpIds);
        return  grpIds;
    }

    public static int mappGroupTypeToDB(int groupType) {
        int mappedGrpType = 0;
        switch (groupType){
            case 3:
                mappedGrpType = 2;
                break;
            case 2 :
                mappedGrpType = 1;
                break;
        }
        return mappedGrpType;
    }

    /**
     *
     * @param mdn
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnPubBOException
     */
    public boolean isExternalContExist(String mdn, int corpId, KnPersisterTxn persisterTxn ) throws KnPubBOException {
        String methodName = "isExternalContExist(String, int )";
        knLogger.debug(methodName, "ENTRY -> Received inputs MDN : " + KnGDPRTemplate.contact(mdn));
        boolean isExist = false;

        try {
            IPubXdmDAO pubXdmDao = KnPubFactorySelector.getDAOFactory(KnPubFactorySelector.DB).createXdmServerDAO(
                    genInfoUtil.retrieveLocalXDMPttServerId());
            knLogger.debug(methodName, "Retrieving number of Contacts members for subscriber  " + KnGDPRTemplate.contact(mdn));
            isExist = pubXdmDao.isExternalContExist(mdn, corpId, persisterTxn);
            knLogger.debug(methodName, "isExist  " + isExist);

            return isExist;
        } catch (KnDBConnectionException e) {
            knLogger.error(methodName, "DBConnectionException - " + e.getMessage());
            throw new KnPubBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, "PTT Server not reachable..", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAOException - " + e.getMessage());
            throw new KnPubBOException(KnErrorCodes.BOEntity.CONTACT_LIST_DOES_NOT_EXISTS, "Error while retrieving isExternalContExist.", e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnPubBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Error while retrieving isExternalContExist.", e);
        } finally {
            knLogger.info(methodName, "Successfully retrieved isExternalContExist  - " + isExist);
        }

    }
}
