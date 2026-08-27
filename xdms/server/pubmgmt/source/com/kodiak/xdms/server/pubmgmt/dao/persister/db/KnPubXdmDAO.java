/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnPOCSvcConfigDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubXdmDAO;
import com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.KnPubDBTablesRegistry;
import com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm.*;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnGroupUsageDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.*;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubGroupDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupPersistDTO;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubXdmDAO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 10, 2011           7.0
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
public class KnPubXdmDAO implements IPubXdmDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPubXdmDAO.class);

    //stores the pttServerId
    private String pttServerId;
    KnGenInfoUtil genInfoUtil = null;

    /**
     * @param pttServerId
     */
    public KnPubXdmDAO(String pttServerId) {
        this.pttServerId = pttServerId;
        genInfoUtil = KnGenInfoUtil.getInstance();
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateEtagForDirDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateEtagForDirDoc(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        boolean ownedTxn = false;

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                ownedTxn = true;
                persisterTxn.open();
            }

            KnDirectoryDAO directoryDAO = KnPubDBTablesRegistry.
                    getDBXdmTableRegistry().getDirectoryDAO(pttServerId);
            directoryDAO.updateEtagForDirDoc(mdn, persisterTxn);
            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (KnDAOException ex) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "exception - ", ex);
            throw ex;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "exception occured - ", e);
            throw KnDbUtil.processException(e, "Failed to Update Etag info." + ", Ex: " +
                    e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_RESOURCELISTINDEXDOC, null);
        } finally {
            if (ownedTxn) {
                persisterTxn = null;
            }
            knLogger.debug(methodName, "EXIT : Mdn:", KnGDPRTemplate.mdn(mdn));
        }

    }


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getCurrentEtagForDirDoc(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCurrentEtagForDirDoc(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        int etag = 0;
        try {
            KnDirectoryDAO directoryDAO = KnPubDBTablesRegistry.
                    getDBXdmTableRegistry().getDirectoryDAO(pttServerId);
            etag = directoryDAO.getCurrentEtagForDirDoc(mdn, readOnly, persisterTxn);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "exception - ", ex);
            throw ex;
        } catch (Exception e) {

            knLogger.error(methodName, "exception occured - ", e);
            throw KnDbUtil.processException(e, "Failed to get etag info." + ", Ex: " +
                    e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_RESOURCELISTINDEXDOC, null);
        }
        knLogger.debug(methodName, "EXIT : etag info:", etag);

        return etag;
    }


    public int getContactListIdForMDN(String mdn, boolean readonly,
                                      KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getContactListIdForMDN(int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        int contactListId = 0;

        KnContactListDocMapDAO contactDocMapDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getContactListDocMapDAO(pttServerId);
        contactListId = contactDocMapDAO.getContactListIdForMDN(mdn, readonly, persisterTxn);

        knLogger.debug(methodName, "EXIT : contactListId info:", contactListId);
        return contactListId;
    }

    /**
     * @param contactListId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void addContacts(int contactListId, Collection<KnMemberDTO> members,
                            KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "addContacts(int, Collection<KnMemberDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnContactListMemberDAO contactListMemberDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getContactListMemberDAO(pttServerId);
        contactListMemberDAO.addContacts(contactListId, members, persisterTxn);
        knLogger.debug(methodName, "EXIT : contactlist Id:", contactListId);

    }


    /**
     * @param contactListId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void modifyContacts(int contactListId, Collection<KnMemberDTO> members,
                               KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyContacts(int, Collection<KnMemberDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnContactListMemberDAO contactListMemberDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getContactListMemberDAO(pttServerId);
        contactListMemberDAO.modifyContacts(contactListId, members, persisterTxn);

        knLogger.debug(methodName, "EXIT : contactlist Id:", contactListId);
    }


    /**
     * @param contactListId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteContacts(int contactListId, Collection<String> members,
                               KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteContacts(int, Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnContactListMemberDAO contactListMemberDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getContactListMemberDAO(pttServerId);
        contactListMemberDAO.deleteContacts(contactListId, members, persisterTxn);

        knLogger.debug(methodName, "EXIT : contactlist Id:", contactListId);
    }

    /**
     * @param contactListId
     * @param memberMdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public boolean checkContactDetails(int contactListId, String memberMdn, boolean readonly,
                                       KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "checkContactDetails(int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnContactListMemberDAO contactListMemberDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getContactListMemberDAO(pttServerId);
        boolean result = contactListMemberDAO.checkContactDetails(contactListId, memberMdn, readonly, persisterTxn);
        knLogger.debug(methodName, "EXIT : result :", result);

        return result;

    }

    /**
     * check if the member already exists in the group
     *
     * @param pocGroupId
     * @param memberMdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public boolean checkGroupMemberExists(int pocGroupId, String memberMdn, boolean readonly,
                                          KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "checkGroupMemberExists(int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnPOCGroupMemberDAO pocGroupMemberDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupMemberDAO(pttServerId);
        boolean result = pocGroupMemberDAO.checkGroupMemberExists(pocGroupId, memberMdn, readonly, persisterTxn);
        knLogger.debug(methodName, "EXIT : result :", result);

        return result;

    }


    /**
     * @param pocGroupId
     * @param readonly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getGroupMemberCount(int pocGroupId, boolean readonly,
                                   KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupMemberCount(int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : PocGroupId: ", pocGroupId);
        int memCount = 0;


        KnPOCGroupMemberDAO pocGroupMemberDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupMemberDAO(pttServerId);
        memCount = pocGroupMemberDAO.getPocGroupMembersCount(pocGroupId, readonly, persisterTxn);

        knLogger.debug(methodName, "EXIT : MemCount :", memCount);

        return memCount;
    }


    public void updateContactListDocMapEtagsForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateContactListDocMapEtagsForMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        KnContactListDocMapDAO contactListDocMapDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getContactListDocMapDAO(pttServerId);
        contactListDocMapDAO.updateContactListDocMapEtagsForMdn(mdn, persisterTxn);

        knLogger.debug(methodName, "EXIT : mdn:", KnGDPRTemplate.mdn(mdn));

    }


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void syncAllGroupDocs(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "syncAllGroupDocs(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        omaGroupDocDAO.updateGroupDocEtagsForMdn(mdn, persisterTxn);

        KnPOCGroupDocMapDAO groupDocMapDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupDocMapDAO(pttServerId);
        groupDocMapDAO.updatePOCGroupDocMapEtagsForMdn(mdn, persisterTxn);

        knLogger.debug(methodName, "EXIT : Mdn:", KnGDPRTemplate.mdn(mdn));

    }


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void syncAllContactDocs(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "syncAllContactDocs(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnContactListDocMapDAO contactListDocMapDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getContactListDocMapDAO(pttServerId);
        contactListDocMapDAO.updateContactListDocMapEtagsForMdn(mdn, persisterTxn);


        knLogger.debug(methodName, "EXIT : Mdn:", KnGDPRTemplate.mdn(mdn));

    }


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllContacts(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteAllContacts(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnContactListDAO contactListDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getContactListDAO(pttServerId);
        Collection<Integer> contactListIds = contactListDAO.getContactListIdsForMdn(mdn, false, persisterTxn);

        if (contactListIds != null && !contactListIds.isEmpty()) {
            KnContactListMemberDAO contactMemDAO = KnPubDBTablesRegistry.
                    getDBXdmTableRegistry().getContactListMemberDAO(pttServerId);
            contactMemDAO.deleteAllContacts(contactListIds, persisterTxn);
            contactMemDAO.getMembersForContactIds(contactListIds,false,persisterTxn);
        }

        //deleting contacts from poc contact list
        KnPOCContactListDAO poccontactListDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPOCContactListDAO(pttServerId);
        poccontactListDAO.deleteAllContacts(mdn, persisterTxn);


        knLogger.debug(methodName, "EXIT : Mdn:", KnGDPRTemplate.mdn(mdn));
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllGroups(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteAllGroups(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnPOCGroupDAO groupDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupDAO(pttServerId);
        Collection<Integer> pocGroupIds = groupDAO.getPocGroupIdsForMdn(mdn, false, persisterTxn);

        if (pocGroupIds != null && !pocGroupIds.isEmpty()) {
            // delete all members form all groups
            KnPOCGroupMemberDAO groupMemDAO = KnPubDBTablesRegistry.
                    getDBXdmTableRegistry().getPocGroupMemberDAO(pttServerId);
            groupMemDAO.deleteMembersFromAllGroups(pocGroupIds, persisterTxn);

            // delete all groups
            groupDAO.deleteAllPocGroups(pocGroupIds, persisterTxn);

            // delete all group doc maps
            KnPOCGroupDocMapDAO groupDocMapDAO = KnPubDBTablesRegistry.
                    getDBXdmTableRegistry().getPocGroupDocMapDAO(pttServerId);
            groupDocMapDAO.deleteAllPocGroupDocMaps(pocGroupIds, persisterTxn);

            // delete all oma group docs
            KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.
                    getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
            omaGroupDocDAO.deleteAllGroupsForMdn(mdn, persisterTxn);
        }


        knLogger.debug(methodName, "EXIT : Mdn:", KnGDPRTemplate.mdn(mdn));
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateAllContactDocs(String mdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateAllContactDocs(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnContactListDocMapDAO contactListDocMapDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getContactListDocMapDAO(pttServerId);
        contactListDocMapDAO.updateMdn(mdn, newMdn, persisterTxn);

        KnContactListDAO contactListDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getContactListDAO(pttServerId);
        contactListDAO.updateMdn(mdn, newMdn, persisterTxn);

        KnPOCContactListDAO poccontactListDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPOCContactListDAO(pttServerId);
        poccontactListDAO.updateMdn(mdn, newMdn, persisterTxn);


        knLogger.debug(methodName, "EXIT : Mdn:", KnGDPRTemplate.mdn(mdn));
    }


    /**
     * @param mdn
     * @param newMdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateAllGroupDocs(String mdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateAllGroupDocs(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        // Update Group data
        KnPOCGroupDocMapDAO groupDocMapDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupDocMapDAO(pttServerId);
        groupDocMapDAO.updateMdn(mdn, newMdn, persisterTxn);

        KnPOCGroupDAO pocGroupDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getPocGroupDAO(pttServerId);
        Collection<KnPubGroupPersistDTO> pocGroups = pocGroupDAO.getAllPocGroupsForMdn(mdn, false, persisterTxn);
        for (KnPubGroupPersistDTO pocGroup : pocGroups) {
            String updatedListServiceUri = pocGroup.getListServiceURI().replaceAll(mdn, newMdn);

            pocGroup.setOwner(newMdn);
            pocGroup.setListServiceURI(updatedListServiceUri);
        }
        pocGroupDAO.updateAllPocGroupsForMdn(pocGroups, persisterTxn);


        knLogger.debug(methodName, "EXIT : Mdn:", KnGDPRTemplate.mdn(mdn));
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */

    public int getResourceListEtagForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        return getResourceListEtagForMdn(mdn, false, persisterTxn);
    }

    public int getResourceListEtagForMdn(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getResourceListEtagForMdn(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        int etag = 0;


        KnContactListDocMapDAO contactListDocMapDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getContactListDocMapDAO(pttServerId);
        etag = contactListDocMapDAO.selectResourceListEtagForMdn(mdn, readOnly, persisterTxn);

        knLogger.debug(methodName, "EXIT : etag :", etag);


        return etag;

    }


    /**
     * @param resourceListIds
     * @param readOnly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Integer> getContactListIdsForResourceDocIds(Collection<Integer> resourceListIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getContactListIdsForResourceDocIds(Collection<Integer>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        Map<Integer, Integer> resourceVsContactListIdMap = null;


        KnContactListDocMapDAO contactDocMapDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getContactListDocMapDAO(pttServerId);
        resourceVsContactListIdMap = contactDocMapDAO.getContactListIdsForResourceListIds(resourceListIds, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT : GroupDoc Id Vs Group Id Map :", resourceVsContactListIdMap);

        return resourceVsContactListIdMap;

    }


    /**
     * @param contactListIds
     * @param readOnly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Collection<KnMemberDTO>> getContactListMemberInfos(Collection<Integer> contactListIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getContactListMemberInfos(Collection<Integer>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        Map<Integer, Collection<KnMemberDTO>> contactVsMemberMap = null;


        KnContactListMemberDAO contactMemDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getContactListMemberDAO(pttServerId);
        contactVsMemberMap = contactMemDAO.getMembersForContactIds(contactListIds, readOnly, persisterTxn);

        knLogger.debug(methodName, "EXIT : ContactListId Vs Members Map :", contactVsMemberMap);

        return contactVsMemberMap;

    }


    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void createOMAGroupDoc(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "createOMAGroupDoc(KnPubGroupInfoPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        omaGroupDocDAO.insertGroupDoc(groupInfoPersistDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT : Mdn:", groupInfoPersistDTO.getOwner());

    }

    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateOMAGroupDocXmlDoc(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateOMAGroupDocXmlDoc(KnPubGroupInfoPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        omaGroupDocDAO.updateGroupDocXmlDoc(groupInfoPersistDTO, persisterTxn);

        knLogger.debug(methodName, "EXIT : Mdn:", groupInfoPersistDTO.getOwner());

    }


    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */

    public void replaceOMAGroupDoc(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn
            persisterTxn) throws KnDAOException {

        String methodName = "replaceOMAGroupDoc(KnPubGroupInfoPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        omaGroupDocDAO.replaceGroupDoc(groupInfoPersistDTO, persisterTxn);

        knLogger.debug(methodName, "EXIT : Mdn:", groupInfoPersistDTO.getOwner());

    }


    /**
     * modify group name - display name
     *
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateGroupDisplayName(KnPubGroupInfoPersistDTO groupInfoPersistDTO,
                                       KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateGroupDisplayName(KnPubGroupInfoPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        omaGroupDocDAO.updateGroupDisplayName(groupInfoPersistDTO, persisterTxn);

        knLogger.debug(methodName, "EXIT : Mdn:", groupInfoPersistDTO.getOwner());

    }

    /**
     * Gets the group details XML for the owner MDN
     *
     * @param mdn
     * @param xcapDocUri
     * @param etag
     * @param readOnly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public InputStream getCurrentOMAGroupDocXmlDoc(String mdn, String xcapDocUri, int etag, boolean readOnly,
                                                   KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getCurrentOMAGroupDocXmlDoc(String, String, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        InputStream result = null;
        String xmlDoc = null;


        KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        xmlDoc = omaGroupDocDAO.getCurrentGroupDocXmlDoc(mdn, xcapDocUri, etag, readOnly, persisterTxn);
        result = new ByteArrayInputStream(xmlDoc.getBytes());

        knLogger.debug(methodName, "EXIT : result :", result);

        return result;

    }


    /**
     * @param grpInfoPersistDTO
     * @param xcapDocUri
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void getCurrentOMAGroupDoc(KnPubGroupInfoPersistDTO grpInfoPersistDTO, String xcapDocUri,
                                      KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCurrentOMAGroupDocXmlDoc(String, String, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        omaGroupDocDAO.getCurrentGroupDoc(grpInfoPersistDTO, xcapDocUri, persisterTxn);
        knLogger.debug(methodName, "EXIT : Group Info :", grpInfoPersistDTO);
    }


    /**
     * @param mdn
     * @param groupDispName
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnPubGroupInfoPersistDTO getOMAGroupDocForGroupName(String mdn, String listServiceUri, String groupDispName,
                                                               KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getOMAGroupDocForGroupName(String, String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        KnPubGroupInfoPersistDTO grpInfoPersistDTO = null;


        KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        grpInfoPersistDTO = omaGroupDocDAO.getOMAGroupDocForGroupName(mdn, listServiceUri, groupDispName, persisterTxn);

        knLogger.debug(methodName, "EXIT : Group Info :", grpInfoPersistDTO);

        return grpInfoPersistDTO;
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnPubGroupDTO> getAllGroupDocsForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getAllGroupDocsForMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        Collection<KnPubGroupDTO> groupList = null;


        KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        groupList = omaGroupDocDAO.getAllGroupDocsForMdn(mdn, persisterTxn);

        knLogger.debug(methodName, "EXIT : groupList :", groupList);

        return groupList;
    }

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnPubGroupDTO> getAllGroupsForMdn(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAllGroupsForMdn(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        Collection<KnPubGroupDTO> groupList = null;

        KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        groupList = omaGroupDocDAO.getAllGroupsForMdn(mdn, readOnly, persisterTxn);

        knLogger.debug(methodName, "EXIT : groupList :", groupList);
        return groupList;
    }

    /**
     * @param groupDocs
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateAllOMAGroupDocs(Collection<KnPubGroupDTO> groupDocs, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateAllOMAGroupDocs(Collection<KnPubGroupDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnOMAGroupDocDAO groupDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        groupDocDAO.updateAllGroupDocs(groupDocs, persisterTxn);

        knLogger.debug(methodName, "EXIT : GroupDocs:", groupDocs);
    }


    /**
     * @param pocGroupId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnPubGroupInfoDTO getGroupDetails(int pocGroupId, String ownerMdn,
                                             KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getGroupDetails(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : PocGroupId:", pocGroupId, ", OwnerMdn:",KnGDPRTemplate.mdn(ownerMdn));
        KnPubGroupInfoDTO groupDetails = null;
        Collection<KnGroupMemberDTO> groupMemberList = null;
        int groupDocId = 0;
        KnPOCGroupMemberDAO pocGroupMemberDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupMemberDAO(pttServerId);
        //fetch the group members
        groupMemberList = pocGroupMemberDAO.getPocGroupMembers(pocGroupId, persisterTxn);
        groupDetails = new KnPubGroupInfoDTO();

        if (null != groupMemberList && !groupMemberList.isEmpty()) {
            groupDetails.setGroupMembers(groupMemberList);
            groupDetails.setGroupMemberCount(groupMemberList.size());
        }

        // fetch the group members count
//            grpMembersCount = pocGroupMemberDAO.getPocGroupMembersCount(pocGroupId, persisterTxn);

        // fetch the xml doc tables group Id for further processing
        KnPOCGroupDocMapDAO groupDocMapDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupDocMapDAO(pttServerId);
        groupDocId = groupDocMapDAO.getGroupDocIdForPocGroupId(pocGroupId, ownerMdn, persisterTxn);

        // fetch the group info like name, etag etc
        KnOMAGroupDocDAO groupDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        KnPubGroupDTO pubGroupDTO = groupDocDAO.getGroupInfoForGroupDocId(groupDocId, ownerMdn, persisterTxn);

        // populate the group details response
        groupDetails.setGroupDisplayName(pubGroupDTO.getGroupDisplayName());
        groupDetails.setGroupType(pubGroupDTO.getGroupType());
        groupDetails.setGroupDocEtag(pubGroupDTO.getGroupDocEtag());
        groupDetails.setGroupId(pocGroupId);
        groupDetails.setOwner(pubGroupDTO.getOwner());

        knLogger.debug(methodName, "EXIT : groupDetails :", groupDetails);

        return groupDetails;
    }

    /**
     * @param groupDocId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnPubGroupInfoDTO getPubGroupDetails(int groupDocId, String ownerMdn,
                                                KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getGroupDetails(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : groupDocId:", groupDocId, ", OwnerMdn:", KnGDPRTemplate.mdn(ownerMdn));
        KnPubGroupInfoDTO groupDetails = null;
        Collection<KnGroupMemberDTO> groupMemberList = null;
        int pubGroupId = 0;

        KnPOCGroupDocMapDAO groupDocMapDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupDocMapDAO(pttServerId);
        pubGroupId = groupDocMapDAO.getGroupIdForGroupDocId(groupDocId, ownerMdn, persisterTxn);

        KnPOCGroupMemberDAO pocGroupMemberDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupMemberDAO(pttServerId);
        //fetch the group members
        groupMemberList = pocGroupMemberDAO.getPocGroupMembers(pubGroupId, persisterTxn);
        groupDetails = new KnPubGroupInfoDTO();

        if (null != groupMemberList && !groupMemberList.isEmpty()) {
            groupDetails.setGroupMembers(groupMemberList);
            groupDetails.setGroupMemberCount(groupMemberList.size());
        }
        // fetch the group info like name, etag etc
        KnOMAGroupDocDAO groupDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        KnPubGroupDTO pubGroupDTO = groupDocDAO.getGroupInfoForGroupDocId(groupDocId, ownerMdn, persisterTxn);
        // populate the group details response
        groupDetails.setGroupDisplayName(pubGroupDTO.getGroupDisplayName());
        groupDetails.setGroupType(pubGroupDTO.getGroupType());
        groupDetails.setGroupDocEtag(pubGroupDTO.getGroupDocEtag());
        groupDetails.setGroupId(pubGroupId);
        groupDetails.setOwner(pubGroupDTO.getOwner());
        knLogger.debug(methodName, "EXIT : groupDetails :", groupDetails);
        return groupDetails;
    }


    /**
     * @param xcapDocUri
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getGroupDocId(String xcapDocUri, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getGroupDocId(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        int groupDocId = 0;

        KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        groupDocId = omaGroupDocDAO.getGroupDocId(xcapDocUri, persisterTxn);

        knLogger.debug(methodName, "EXIT : groupDocId :", groupDocId);
        return groupDocId;

    }

    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */

    public int getCurrentOMAGroupDocEtag(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        return getCurrentOMAGroupDocEtag(groupInfoPersistDTO, false, persisterTxn);
    }

    public int getCurrentOMAGroupDocEtag(KnPubGroupInfoPersistDTO groupInfoPersistDTO,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getCurrentOMAGroupDocEtag(KnPubGroupInfoPersistDTO,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        int etag = 0;


        KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        etag = omaGroupDocDAO.getCurrentGroupDocEtag(groupInfoPersistDTO, readOnly, persisterTxn);

        knLogger.debug(methodName, "EXIT : etag :", etag);

        return etag;
    }

    /**
     * @param groupDocId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteOMAGroupDoc(int groupDocId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteOMAGroupDoc(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        omaGroupDocDAO.deleteGroupDoc(groupDocId, persisterTxn);


        knLogger.debug(methodName, "EXIT : groupDocId:", groupDocId);

    }

    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void createPocGroupDocMap(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "createPocGroupDocMap(KnPubGroupInfoPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnPOCGroupDocMapDAO groupDocMapDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupDocMapDAO(pttServerId);
        groupDocMapDAO.insertGroupDocMap(groupInfoPersistDTO, persisterTxn);

        knLogger.debug(methodName, "EXIT : Mdn:", groupInfoPersistDTO.getOwner());

    }

    /**
     * @param pocGroupId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deletePocGroupDocMap(int pocGroupId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deletePocGroupDocMap(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnPOCGroupDocMapDAO groupDocMapDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupDocMapDAO(pttServerId);
        groupDocMapDAO.deletePocGroupDocMap(pocGroupId, persisterTxn);

        knLogger.debug(methodName, "EXIT : GroupId:", pocGroupId);

    }

    /**
     * @param groupDocId
     * @param etag
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateEtagForPOCGroupDocMap(int groupDocId, int etag,
                                            KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateEtagForPOCGroupDocMap(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnPOCGroupDocMapDAO groupDocMapDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupDocMapDAO(pttServerId);
        groupDocMapDAO.updateEtagForPOCGroupDocMap(groupDocId, etag, persisterTxn);

        knLogger.debug(methodName, "EXIT : GroupDocId:", groupDocId);

    }

    /**
     * @param groupDocId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getPocGroupIdForGroupDocId(int groupDocId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getPocGroupIdForGroupDocId(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        int pocGroupId = 0;

        KnPOCGroupDocMapDAO groupDocMapDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupDocMapDAO(pttServerId);
        pocGroupId = groupDocMapDAO.getPocGroupIdForGroupDocId(groupDocId, persisterTxn);

        knLogger.debug(methodName, "EXIT : pocGroupId :", pocGroupId);

        return pocGroupId;
    }


    /**
     * @param groupDocIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Integer> getPocGroupIdsForGroupDocIds(Collection<Integer> groupDocIds, boolean
            readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getPocGroupIdsForGroupDocIds(Collection<Integer>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        Map<Integer, Integer> grpDocVsGrpIdMap = null;

        KnPOCGroupDocMapDAO groupDocMapDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupDocMapDAO(pttServerId);
        grpDocVsGrpIdMap = groupDocMapDAO.getPocGroupIdsForGroupDocIds(groupDocIds, readOnly, persisterTxn);

        knLogger.debug(methodName, "EXIT : GroupDoc Id Vs Group Id Map :", grpDocVsGrpIdMap);

        return grpDocVsGrpIdMap;

    }

    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void createPOCGroup(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "createPOCGroup(KnPubGroupInfoPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnPOCGroupDAO pocGroupDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupDAO(pttServerId);
        pocGroupDAO.insertPocGroup(groupInfoPersistDTO, persisterTxn);

        knLogger.debug(methodName, "EXIT : Mdn:", groupInfoPersistDTO.getOwner());

    }

    /**
     * @param pocGroupId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deletePocGroup(int pocGroupId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deletePocGroup(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnPOCGroupDAO pocGroupDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupDAO(pttServerId);
        pocGroupDAO.deletePocGroup(pocGroupId, persisterTxn);

        knLogger.debug(methodName, "EXIT : PocGroupId:", pocGroupId);

    }

    /**
     * @param mdn
     * @param listServiceURI
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deletePocGroupForMdn(String mdn, String listServiceURI,
                                     KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deletePocGroupForMdn(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnPOCGroupDAO pocGroupDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupDAO(pttServerId);
        pocGroupDAO.deletePocGroupForMdn(mdn, listServiceURI, persisterTxn);

        knLogger.debug(methodName, "EXIT : Mdn:", KnGDPRTemplate.mdn(mdn));
    }

    /**
     * @param pocGroupId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void addPocGroupMembers(int pocGroupId, Collection<KnGroupMemberDTO> members,
                                   KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "addPocGroupMembers(int, Collection<KnGroupMemberDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnPOCGroupMemberDAO pocGroupMemberDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupMemberDAO(pttServerId);
        pocGroupMemberDAO.addPocGroupMembers(pocGroupId, members, persisterTxn);

        knLogger.debug(methodName, "EXIT : POCGroupId:", pocGroupId);

    }


    /**
     * @param pocGroupId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void modifyPocGroupMembers(int pocGroupId, Collection<KnGroupMemberDTO> members,
                                      KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "modifyPocGroupMembers(int, Collection<KnGroupMemberDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnPOCGroupMemberDAO pocGroupMemberDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupMemberDAO(pttServerId);
        pocGroupMemberDAO.modifyPocGroupMembers(pocGroupId, members, persisterTxn);

        knLogger.debug(methodName, "EXIT : POCGroupId:", pocGroupId);
    }

    /**
     * @param pocGroupId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deletePocGroupMembers(int pocGroupId, Collection<String> members,
                                      KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deletePocGroupMembers(int, Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnPOCGroupMemberDAO pocGroupMemberDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupMemberDAO(pttServerId);
        pocGroupMemberDAO.deletePocGroupMembers(pocGroupId, members, persisterTxn);

        knLogger.debug(methodName, "EXIT : POCGroupId:", pocGroupId);
    }

    /**
     * @param pocGroupId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllPocGroupMembers(int pocGroupId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteAllPocGroupMembers(int, Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnPOCGroupMemberDAO pocGroupMemberDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupMemberDAO(pttServerId);
        pocGroupMemberDAO.deleteAllPocGroupMembers(pocGroupId, persisterTxn);

        knLogger.debug(methodName, "EXIT : POCGroupId:", pocGroupId);
    }


    /**
     * @param mdn
     * @param groupName
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int countNumberOfGroupsOwnedByMDN(String mdn, String groupName,
                                             KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "countNumberOfGroupsOwnedByMDN(String,String,KnPersisterTxn)";

        boolean ownedTxn = false;
        int numberOfGroups = -1;

        knLogger.debug(methodName, "ENTRY -> Got MDN ", KnGDPRTemplate.mdn(mdn) , " to retrieve number of groups for. Group ->",
                groupName);

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                ownedTxn = true;
                persisterTxn.open();
            }
            KnOMAGroupDocDAO grpRetrievalDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
            if (groupName != null)
                groupName = new String(groupName.getBytes("UTF-8"), "8859_1");

            numberOfGroups = grpRetrievalDAO.countGroupsOwnedByMDN(mdn, groupName, persisterTxn);

            knLogger.debug(methodName, "Got number of groups : ", numberOfGroups);

            //save the transaction if it is owned
            if (ownedTxn) {
                persisterTxn.save();
            }

            return numberOfGroups;
        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "exception - ", e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "exception occured - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve number of groups for  " + mdn +
                    ", Ex: " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, null);
        } finally {
            if (ownedTxn) {
                persisterTxn = null;
            }
            knLogger.debug(methodName, "EXIT : Number of Groups - ", numberOfGroups);
        }
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int countNumberOfContactMembers(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "countNumberOfContactMembers(String, KnPersisterTxn)";

        boolean ownedTxn = false;
        int numberOfContactMems = -1;

        knLogger.debug(methodName, "ENTRY -> Got MDN ", KnGDPRTemplate.mdn(mdn), " to retrieve number ",
                "of contact member.");

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                ownedTxn = true;
                persisterTxn.open();
            }

            KnContactListDAO contactListDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getContactListDAO(pttServerId);
            Collection<Integer> contactListIds = contactListDAO.getContactListIdsForMdn(mdn, true, persisterTxn);
            KnContactListMemberDAO contactMemlDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().
                    getContactListMemberDAO(pttServerId);

            // gets the first contact list id for mdn, asuming only one contact list is allowed.
            int contactListId = contactListIds.iterator().next();
            numberOfContactMems = contactMemlDAO.countContactMembers(contactListId, persisterTxn);
            knLogger.debug(methodName, "Got number of Contact Mems : ", numberOfContactMems);

            //save the transaction if it is owned
            if (ownedTxn) {
                persisterTxn.save();
            }

            return numberOfContactMems;
        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "exception - ", e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "exception occured - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve number of contacts for  " + mdn +
                    ", Ex: " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, null);
        } finally {
            if (ownedTxn) {
                persisterTxn = null;
            }
            knLogger.debug(methodName, "EXIT : Number of Contacts - ", numberOfContactMems);
        }

    }

    /**
     * @param mdn
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void addContactsToPOCContactList(String mdn, Collection<KnMemberDTO> members,
                                            KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "addContactsToPOCContactList(String, Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnPOCContactListDAO poccontactListDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPOCContactListDAO(pttServerId);
        poccontactListDAO.addContacts(mdn, members, persisterTxn);

        knLogger.debug(methodName, "EXIT : mdn :", KnGDPRTemplate.mdn(mdn));

    }


    /**
     * @param mdn
     * @param contactMDNs
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteContactsFromPOCContactList(String mdn, Collection<String> contactMDNs,
                                                 KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteContactsFromPOCContactList(String, Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnPOCContactListDAO poccontactListDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPOCContactListDAO(pttServerId);
        poccontactListDAO.deleteContacts(mdn, contactMDNs, persisterTxn);


        knLogger.debug(methodName, "EXIT : mdn :", KnGDPRTemplate.mdn(mdn));

    }

    //req == rqHistoryBasedPresence_23 : Populating the DG.publicContactCount table

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getPublicContactCount(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getPublicContactCount(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        int count = 0;

        KnPubContactCountDAO contactCountDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPubContactCountDAO(pttServerId);
        count = contactCountDAO.getContactCount(mdn, persisterTxn);

        knLogger.debug(methodName, "EXIT : public contact count  info:", count);

        return count;
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public void updateContactCount(String mdn, int contactCount, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateContactCount(String,int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        int count = 0;
        KnPubContactCountDAO contactCountDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPubContactCountDAO(pttServerId);
        contactCountDAO.updateContactCount(mdn, contactCount, persisterTxn);

        knLogger.debug(methodName, "EXIT : increment public contact count  info:", count);

    }


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public void addContactCount(String mdn, int contactCount, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "addContactCount(String,int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        int count = 0;
        KnPubContactCountDAO contactCountDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPubContactCountDAO(pttServerId);
        contactCountDAO.insertContactCount(mdn, contactCount, persisterTxn);

        knLogger.debug(methodName, "EXIT : add public contact count  info:", count);

    }


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public void deleteContactCount(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteContactCount(String,int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        KnPubContactCountDAO contactCountDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPubContactCountDAO(pttServerId);
        contactCountDAO.deleteContactCount(mdn, persisterTxn);

        knLogger.debug(methodName, "EXIT : delete public contact MDN  info:", KnGDPRTemplate.mdn(mdn));

    }


    /**
     * @param newMdn
     * @param oldMDN
     * @param contactCount
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public void updateContactMDN(String oldMDN, String newMdn, int contactCount, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateContactMDN(String,String,int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnPubContactCountDAO contactCountDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPubContactCountDAO(pttServerId);
        contactCountDAO.UpdateContactMDN(oldMDN, newMdn, contactCount, persisterTxn);

        knLogger.debug(methodName, "EXIT : updateContactMDN   info:", KnGDPRTemplate.mdn(newMdn));

    }

    /**
     * @param mdns
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllContacts(List mdns, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteAllContacts(List, KnPersisterTxn)";
        knLogger.debug(methodName,KnGDPRTemplate.mdnList(mdns));

        KnContactListDAO contactListDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getContactListDAO(pttServerId);
        Collection<Integer> contactListIds = contactListDAO.getContactListIdsForMdn(mdns, false, persisterTxn);

        if (contactListIds != null && !contactListIds.isEmpty()) {
            KnContactListMemberDAO contactMemDAO = KnPubDBTablesRegistry.
                    getDBXdmTableRegistry().getContactListMemberDAO(pttServerId);
            contactMemDAO.deleteAllContacts(contactListIds, persisterTxn);
        }

        //deleting contacts from poc contact list
        KnPOCContactListDAO poccontactListDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPOCContactListDAO(pttServerId);
        poccontactListDAO.deleteAllContacts(mdns, persisterTxn);

    }


    /**
     * @param mdns
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllGroups(List mdns, KnPersisterTxn persisterTxn) throws KnDAOException {

        final String methodName = "deleteAllGroups(String, KnPersisterTxn)";
        knLogger.debug(methodName);


        KnPOCGroupDAO groupDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPocGroupDAO(pttServerId);
        Collection<Integer> pocGroupIds = groupDAO.getPocGroupIdsForMdn(mdns, false, persisterTxn);

        if (pocGroupIds != null && !pocGroupIds.isEmpty()) {
            // delete all members form all groups
            KnPOCGroupMemberDAO groupMemDAO = KnPubDBTablesRegistry.
                    getDBXdmTableRegistry().getPocGroupMemberDAO(pttServerId);
            groupMemDAO.deleteMembersFromAllGroups(pocGroupIds, persisterTxn);

            // delete all groups
            groupDAO.deleteAllPocGroups(pocGroupIds, persisterTxn);

            // delete all group doc maps
            KnPOCGroupDocMapDAO groupDocMapDAO = KnPubDBTablesRegistry.
                    getDBXdmTableRegistry().getPocGroupDocMapDAO(pttServerId);
            groupDocMapDAO.deleteAllPocGroupDocMaps(pocGroupIds, persisterTxn);

            // delete all oma group docs
            KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.
                    getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
            omaGroupDocDAO.deleteAllGroupsForMdn(mdns, persisterTxn);
        }

    }

    /**
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public void deleteContactCount(List mdns, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteContactCount(String,int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnPubContactCountDAO contactCountDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPubContactCountDAO(pttServerId);
        contactCountDAO.deleteContactCount(mdns, persisterTxn);

        knLogger.debug(methodName, "EXIT : delete public contact MDN  info:", KnGDPRTemplate.mdnList(mdns));

    }

    public Collection<Integer> getContactListIdForMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnContactListDAO contactListDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getContactListDAO(pttServerId);
        Collection<Integer> contactListIdsForMdn = contactListDAO.getContactListIdsForMdn(mdn, true, persisterTxn);
        return contactListIdsForMdn;
    }

    public Integer getCorpListIdForMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnContactListDAO contactListDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getContactListDAO(pttServerId);
        Integer corpListIdForMdn = contactListDAO.getCorpListIdForMDN(mdn, true, persisterTxn);
        return corpListIdForMdn;
    }

    public Collection<String> getMemberMdnsForCorpListId(Collection<Integer> corpListId,Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnContactListDAO contactListDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getContactListDAO(pttServerId);
        Collection<String> memberMdnsForCorpListId = contactListDAO.getMemberMdnsForCorpListId(corpListId,corpId,true,persisterTxn);
        return memberMdnsForCorpListId;
    }

    public Collection<String> getPrivateContactsForMDN(String mdn,Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnContactListDAO contactListDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getContactListDAO(pttServerId);
        Collection<String> privateContactList = contactListDAO.getPrivateContactsForMDN(mdn,corpId,persisterTxn);
        return privateContactList;
    }
    
	public String getRealMdnForProfileMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
		final String methodName = "getRealMdnForProfileMdn(String, KnPersisterTxn)";
		knLogger.debug(methodName, "ENTRY : mdn", KnGDPRTemplate.mdn(mdn));
		KnPOCSubscriberDAO pocSubscriberDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry()
				.getPOCSubscriberDAO(pttServerId);
		String realMdn = pocSubscriberDAO.getRealMdnForProfileMdn(mdn, persisterTxn);
		knLogger.debug(methodName, "EXIT :", KnGDPRTemplate.mdn(realMdn));
		return realMdn;
	}

    public Collection<String> getAllListServiceUrisForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllGroups(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        boolean ownedTxn = false;
        Collection<String> listServiceUris = null;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                ownedTxn = true;
                persisterTxn.open();
            }
            KnPOCGroupDAO groupDAO = KnPubDBTablesRegistry.
                    getDBXdmTableRegistry().getPocGroupDAO(pttServerId);
            listServiceUris = groupDAO.getAllListServiceUrisForMdn(mdn, false, persisterTxn);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "exception - ", ex);
            throw ex;
        } catch (Exception e) {
            knLogger.error(methodName, "exception occured - ", e);
            throw KnDbUtil.processException(e, "Failed to delete public contact count MDN  info." + ", Ex: " +
                    e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, null);
        }
        knLogger.debug(methodName, "EXIT : delete public contact MDN  info:", listServiceUris);

        return listServiceUris;
    }

    /**
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */

    public List<KnMemberDTO> getMembersClientType(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscriberDAO pocSubscriberDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getPOCSubscriberDAO(pttServerId);
        List<KnMemberDTO> listIdsForMdn = pocSubscriberDAO.getMembersClientType(mdns, true, persisterTxn);
        return listIdsForMdn;
    }

    /**
     * @param pocGroupId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public Collection<KnGroupMemberDTO> getPocGroupMembers(int pocGroupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPocGroupMembers(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        KnPOCGroupMemberDAO pocGroupMemberDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getPocGroupMemberDAO(pttServerId);
        Collection<KnGroupMemberDTO> grpMember = pocGroupMemberDAO.getPocGroupMembers(pocGroupId, persisterTxn);
        knLogger.debug(methodName, "EXIT : POCGroupId:", grpMember);
        return grpMember;
    }

    /**
     * Method to get the corporate detaisl from dg.poccorpinfo tables by external corporate ID.
     *
     * @param extCorpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    @Override
    public KnCorpProfileDTO getCorpProfileDetails(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpProfileDetails(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        KnCorporateInfoDAO corporateInfoDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getCorporateInfoDAO(pttServerId);
        return corporateInfoDAO.getCorpProfileDetails(extCorpId, persisterTxn);
    }

    public KnPOCSvcConfigDTO retrievePOCSvcConfig(String pocPttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrievePOCSvcConfig(String, KnPersisterTxn)";
        KnPOCSvcConfigDTO pocSvcConfigDTO = null;
        knLogger.debug(methodName, "--->ENTRY: Retrieve POC service config");
        try {

            // getting doc config info from POC Service Config
            KnPOCSvcConfigDAO pocSvcConfigDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().createPOCSvcConfigDAO(pttServerId);
            pocSvcConfigDTO = pocSvcConfigDAO.selectPOCSvcConfig(pocPttServerId, persisterTxn);

        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving  POC service config ", pttServerId,
                    KnDAOSourceTypes.POC_SVC_CONFIG, null);
        }
        knLogger.debug(methodName, "--->EXIT:Retrieved  POC service config - ", pocSvcConfigDTO);

        return pocSvcConfigDTO;
    }


    /**
     * Method to retrieve the subscriber details.
     *
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    @Override
    public List<KnMemberDTO> getMembersDetails(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpProfileDetails(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        KnPOCSubscriberDAO subscriberDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getPOCSubscriberDAO(pttServerId);
        return subscriberDAO.getMembersDetails(mdns, persisterTxn);
    }

    /**
     * Method to get the third party ID.
     *
     * @param vendorId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    @Override
    public int getTPId(String vendorId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnTPAccountInfoDAO tpAccountInfoDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getTPAccountInfoDAO(pttServerId);
        return tpAccountInfoDAO.getTPId(vendorId, persisterTxn);
    }

    /**
     * Method to return a Map of MDN and respective third party ID.
     *
     * @param tpMdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    @Override
    public Map<String, Integer> getMdnTPidMap(List<String> tpMdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnTPUserMdnMapDAO tpUserMdnMapDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getTPUserMdnMapDAO(pttServerId);
        return tpUserMdnMapDAO.getMdnTPidMap(tpMdnList, persisterTxn);
    }

    /**
     * Method to get the external contacts in the corporation
     *
     * @param extContactList
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    @Override
    public List<String> getExtContactList(List<String> extContactList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnExtCorpContactDAO extCorpContactDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getExtCorpContactDAO(pttServerId);
        return extCorpContactDAO.getExtContactList(extContactList, corpId, persisterTxn);
    }

    /**
     * Interface to update the OMA Group, listserviceuri, groupdocuri and displayname for a groupdocid
     *
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateOMAGroupDetail(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnOMAGroupDocDAO omaGroupDocDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getOmaGroupDocDAO(pttServerId);
        omaGroupDocDAO.updateOMAGroupDetail(groupInfoPersistDTO, persisterTxn);

    }

    public void updatePocGroupsListSrvUri(List<KnPubGroupPersistDTO> pocGroups, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCGroupDAO pocGroupDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getPocGroupDAO(pttServerId);
        pocGroupDAO.updateAllPocGroupsForMdn(pocGroups, persisterTxn);

    }

    public KnAuthDocDTO getAuthorizationDocDetails(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAuthorizationDocDetails(String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "AuthEntry : ", KnGDPRTemplate.mdn(mdn));
        KnAuthorizationDocDAO authorizationDocDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getAuthorizationDocDAO(pttServerId);
        KnAuthDocDTO knAuthDocDTO = authorizationDocDAO.getAuthorizationDocDetails(mdn, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT :", knAuthDocDTO);
        return knAuthDocDTO;
    }

    public List<KnMCPTTPermInfoDTO> getMCPTTPermInfo(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMCPTTPermInfo(String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "AuthEntry : ", KnGDPRTemplate.mdn(mdn));
        KnMCPTTPermInfoDAO mcpttPermInfoDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getMCPTTPermDAO(pttServerId);
        List<KnMCPTTPermInfoDTO> permInfoDTOS = mcpttPermInfoDAO.getMCPTTPermInfo(mdn, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT :", permInfoDTOS);
        return permInfoDTOS;
    }

    public List<KnMCPTTPermInfoDTO> getMCPTTPermInfoOnTargetMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMCPTTPermInfoOnTargetMDN(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry of TargetMDN : ", KnGDPRTemplate.mdn(mdn));
        KnMCPTTPermInfoDAO mcpttPermInfoDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getMCPTTPermDAO(pttServerId);
        List<KnMCPTTPermInfoDTO> permInfoDTOS = mcpttPermInfoDAO.getMCPTTPermInfoOnTargetMDN(mdn, persisterTxn);
        knLogger.debug(methodName, "EXIT :", permInfoDTOS);
        return permInfoDTOS;
    }


    public Map<String, Integer> getUserServiceStatus(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUserServiceStatus(AuthList<String>,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "AuthEntry : ", KnGDPRTemplate.mdnList(mdns));
        KnPOCSubscriberDAO pocSubscriberDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getPOCSubscriberDAO(pttServerId);
        Map<String, Integer> mdnVsUserStatusMap = pocSubscriberDAO.getUserServiceStatus(mdns, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT :", mdnVsUserStatusMap);
        return mdnVsUserStatusMap;
    }

    public Map<String, Integer> getTargetMdnsCorpid(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getTargetMdnsCorpid(AuthList<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "AuthEntry : ", KnGDPRTemplate.mdnList(mdns));
        KnPOCSubscriberDAO pocSubscriberDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getPOCSubscriberDAO(pttServerId);
        Map<String, Integer> mdnCorpIdMap = pocSubscriberDAO.getTargetMdnsCorpid(mdns, persisterTxn);
        return mdnCorpIdMap;
    }

    public KnMCPTTPermInfoDTO getMCPTTPermInfoOnTarget(String mdn, String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMCPTTPermInfoOnTarget(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "AuthEntry : ", KnGDPRTemplate.mdn(mdn)," targetMdn -", KnGDPRTemplate.mdn(targetMdn));
        KnMCPTTPermInfoDAO mcpttPermInfoDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getMCPTTPermDAO(pttServerId);
        KnMCPTTPermInfoDTO permInfoDTO = mcpttPermInfoDAO.getMCPTTPermInfoOnTarget(mdn, targetMdn, persisterTxn);
        knLogger.debug(methodName, "EXIT :", permInfoDTO);
        return permInfoDTO;
    }

    public KnMCPTTPermInfoDTO getMCPTTPermInfoByMdns(String mdn, String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMCPTTPermInfoOnTarget(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "AuthEntry : ", KnGDPRTemplate.mdn(mdn)," targetMdn -",KnGDPRTemplate.mdn(targetMdn));
        KnMCPTTPermInfoDAO mcpttPermInfoDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getMCPTTPermDAO(pttServerId);
        KnMCPTTPermInfoDTO permInfoDTO = mcpttPermInfoDAO.getMCPTTPermInfoByMdns(mdn, targetMdn, persisterTxn);
        knLogger.debug(methodName, "EXIT :", permInfoDTO);
        return permInfoDTO;
    }

    public void updateAuthorizationDocDetails(String mdn, long etag, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateAuthorizationDocDetails(String, KnPersisterTxn)";
        knLogger.debug(methodName, "AuthEntry : ", KnGDPRTemplate.mdn(mdn));
        KnAuthorizationDocDAO authorizationDocDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getAuthorizationDocDAO(pttServerId);
        authorizationDocDAO.updateAuthorizationDocDetails(mdn, etag, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
    }

    public void updateMCPTTDiscreetEnabled(String authMdn, List<String> targetMdns, int discreetListenerStatus, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateMCPTTDiscreetEnabled(String, String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "AuthEntry : ",KnGDPRTemplate.mdn(authMdn), KnGDPRTemplate.mdnList(targetMdns), discreetListenerStatus);
        KnMCPTTPermInfoDAO mcpttPermInfoDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getMCPTTPermDAO(pttServerId);
        mcpttPermInfoDAO.updateMCPTTDiscreetEnabled(authMdn, targetMdns, discreetListenerStatus, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
    }

    public void updateDiscreetEnabledForTarget(List<String> targetMdns, int discreetListenerStatus, long lastProfileUpdateTime, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateDiscreetEnabledForTarget(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ", KnGDPRTemplate.mdnList(targetMdns), discreetListenerStatus);
        KnPOCSubscriberDAO pocSubscriberDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getPOCSubscriberDAO(pttServerId);
        pocSubscriberDAO.updateDiscreetEnabledForTarget(targetMdns, discreetListenerStatus, lastProfileUpdateTime, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
    }

    public void updateServiceAuthStatusForTarget(List<String> targetMdns, int userServStatus, int finalEerviceAuthStatus, long lastProfileUpdateTime, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateServiceAuthStatusForTarget(String, int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ", KnGDPRTemplate.mdnList(targetMdns), userServStatus, finalEerviceAuthStatus);
        KnPOCSubscriberDAO pocSubscriberDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getPOCSubscriberDAO(pttServerId);
        pocSubscriberDAO.updateServiceAuthStatusForTarget(targetMdns, userServStatus, finalEerviceAuthStatus, lastProfileUpdateTime, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
    }

    public List<String> getAllAuthMdsForTarget(String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAllAuthMdsForTarget(String, KnPersisterTxn)";
        knLogger.debug(methodName, "AuthEntry : ", KnGDPRTemplate.mdn(targetMdn));
        KnMCPTTPermInfoDAO mcpttPermInfoDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getMCPTTPermDAO(pttServerId);
        List<String> authMdsList = mcpttPermInfoDAO.getAllAuthMdsForTarget(targetMdn, persisterTxn);
        knLogger.debug(methodName, "authMdsList :",KnGDPRTemplate.mdnList(authMdsList));
        return authMdsList;
    }

    public void updateMdnsEtagForAuthDoc(List<String> authMdnsList, long etag, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateMdnsEtagForAuthDoc(List<String>, long, KnPersisterTxn)";
        knLogger.debug(methodName, "AuthEntry : ", KnGDPRTemplate.mdnList(authMdnsList));
        KnAuthorizationDocDAO authorizationDocDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getAuthorizationDocDAO(pttServerId);
        authorizationDocDAO.updateMdnsEtagForAuthDoc(authMdnsList, etag, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
    }

    public KnEmergencyDocDTO getEmergencyDocDetails(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getEmergencyDocDetails(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ", KnGDPRTemplate.mdn(mdn));
        KnEmergencyDocDAO emergencyDocDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getEmergencyDocDAO(pttServerId);
        KnEmergencyDocDTO emergencyDocDTO = emergencyDocDAO.getEmergencyDocDetails(mdn, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT :", emergencyDocDTO);
        return emergencyDocDTO;
    }

    public KnGroupUsageListDocDTO getGroupUsageListDocDetails(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupUsageListDocDetails(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ", KnGDPRTemplate.mdn(mdn));
        KnSubscrPttRadioGroupListDAO subscrPttRadioGroupListDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getSubscrPttRadioGroupListDAO(pttServerId);
        KnGroupUsageListDocDTO usageListDocDTO = subscrPttRadioGroupListDAO.getSubscrPTTRadioGroupListDoc(mdn, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT :", usageListDocDTO);
        return usageListDocDTO;
    }


    public List<KnGroupUsageDTO> getSubscrPTTRadioTGList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscrPTTRadioTGList(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ", KnGDPRTemplate.mdn(mdn));
        KnSubscrPTTRadioTGListDAO pttRadioGroupListDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getSubscrPTTRadioTGList(pttServerId);
        List<KnGroupUsageDTO> usageListDocDTOs = pttRadioGroupListDAO.getSubscrPTTRadioTGList(mdn, persisterTxn);
        knLogger.debug(methodName, "EXIT :", usageListDocDTOs);
        return usageListDocDTOs;
    }

    public KnSubsAddEmgrConfigDTO getSubAddEmergencyConfig(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubAddEmergencyConfig(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ", KnGDPRTemplate.mdn(mdn));
        KnPocSubscrAddInfoDAO pocSubscrAddInfo = KnPubDBTablesRegistry.getDBXdmTableRegistry().getPocSubscrAddInfoDAO(pttServerId);
        KnSubsAddEmgrConfigDTO subsAddEmgrConfig = pocSubscrAddInfo.getEmgrConfig(mdn, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT :", pocSubscrAddInfo);
        return subsAddEmgrConfig;
    }

    public List<KnEmgrDestinationInfoDTO> getEmergencyDestinationInfo(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getEmergencyDestinationInfo(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ", KnGDPRTemplate.mdn(mdn));
        KnEmergencySubscrDestinfoDAO emergencySubscrDestinfoDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getEmergencySubscrDestinfoDAO(pttServerId);
        List<KnEmgrDestinationInfoDTO> destinationInfoDTOS = emergencySubscrDestinfoDAO.getEmergencySubscrDestinfo(mdn, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT :", destinationInfoDTOS);
        return destinationInfoDTOS;
    }

    public Map<String, String> getUfmiForContactMDNs(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUfmiForContactMDNs(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        KnPOCContactListDAO pocContactListDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getPOCContactListDAO(pttServerId);
        Map<String, String> ufmiForContactMDNs = pocContactListDAO.getUfmiForContactMDNs(mdns, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT : ufmiForContactMDNs:", ufmiForContactMDNs);
        return ufmiForContactMDNs;
    }

    public void updateContactUfmi(String mdn, String contactMDN, String ufmi, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "addContactsToPOCContactList(String, String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");


        KnPOCContactListDAO poccontactListDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPOCContactListDAO(pttServerId);
        poccontactListDAO.updateContactUfmi(mdn, contactMDN, ufmi, persisterTxn);

        knLogger.debug(methodName, "EXIT : mdn :", KnGDPRTemplate.mdn(mdn));

    }

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnTGSSDocDTO getTGSSDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        return getTGSSDoc(mdn, false, persisterTxn);
    }

    public KnTGSSDocDTO getTGSSDoc(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getTGSSDoc(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdn:", KnGDPRTemplate.mdn(mdn));

        KnTGSSDocDAO tgssDocDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getTGSSDocDAO(pttServerId);
        KnTGSSDocDTO tgssDocDTO = tgssDocDAO.getTGSSSDoc(mdn, readOnly, persisterTxn);

        knLogger.debug(methodName, "EXIT : mdn :", KnGDPRTemplate.mdn(mdn), "tgssDocDTO : ", tgssDocDTO);

        return tgssDocDTO;
    }

    /**
     * @param mdn
     * @param etag
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateEtagTGSSDoc(String mdn, long etag, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateEtagTGSSDoc(String,long,KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnTGSSDocDAO tgssDocDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getTGSSDocDAO(pttServerId);
        tgssDocDAO.updateEtagTGSSDoc(mdn, etag, persisterTxn);
        knLogger.debug(methodName, "Exit : " + KnGDPRTemplate.mdn(mdn));
    }

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<Integer> getSSGroupIds(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSSGroupIds(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        KnSSChannelGroupInfoDAO ssChannelGroupInfoDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getSSChannelGroupInfoDAO(pttServerId);

        List<Integer> groupIds = ssChannelGroupInfoDAO.getSSGroupIds(mdn, readOnly, persisterTxn);

        knLogger.debug(methodName, "Exit : " + KnGDPRTemplate.mdn(mdn), " groupIds :" + groupIds);

        return groupIds;
    }

    /**
     * @param mdn
     * @param groupId
     * @param persisterTxn
     * @throws KnDAOException
     */

    public void addSSChannelGrpInfo(String mdn, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "addSSChannelGrpInfo(String,int,KnPersisterTxn)";

        knLogger.debug(methodName, "Entry : mdn :", KnGDPRTemplate.mdn(mdn), " groupId", groupId);

        KnSSChannelGroupInfoDAO ssChannelGroupInfoDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getSSChannelGroupInfoDAO(pttServerId);

        ssChannelGroupInfoDAO.createSSGroupId(mdn, groupId, persisterTxn);

        knLogger.debug(methodName, "Exit : mdn :", KnGDPRTemplate.mdn(mdn), " groupId", groupId);

    }

    /**
     * @param mdn
     * @param groupId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteSSChannelGrpInfo(String mdn, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteSSChannelGrpInfo(String,int,KnPersisterTxn)";

        knLogger.debug(methodName, "Entry : mdn :", KnGDPRTemplate.mdn(mdn), " groupId", groupId);

        KnSSChannelGroupInfoDAO ssChannelGroupInfoDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getSSChannelGroupInfoDAO(pttServerId);

        ssChannelGroupInfoDAO.deleteSSGroupId(mdn, groupId, persisterTxn);

        knLogger.debug(methodName, "Exit : mdn :", KnGDPRTemplate.mdn(mdn), " groupId", groupId);

    }

    @Override
    public int getCorpMaxSSDDCount(int corpid, KnPersisterTxn persisterTxn) throws KnDAOException {

        KnCorporateInfoDAO corporateInfoDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getCorporateInfoDAO(pttServerId);

        return corporateInfoDAO.getMaxSSDDSessionCnt(corpid,persisterTxn );
    }

    @Override
    public int getPOCMaxSSDDCount(KnPersisterTxn persisterTxn, String pocPttServerId) throws KnDAOException {

        KnPOCSvcConfigDAO pocSvcConfigDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getPOCSvcConfigDAO(pttServerId);

        return pocSvcConfigDAO.getMaxSSDDSessionCnt(persisterTxn, pocPttServerId);
    }


    /**
     * Method to get the external contacts details in the corporation
     *
     * @param extContactList
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    @Override
    public Map<String,KnMemberDTO> getExtContactListByContactMDN(List<String> extContactList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnExtCorpContactDAO extCorpContactDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getExtCorpContactDAO(pttServerId);
        return extCorpContactDAO.getExtContactListByContactMDN(extContactList, corpId, persisterTxn);
    }
    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public boolean isExternalContExist(String mdn, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnExtCorpContactDAO extCorpContactDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getExtCorpContactDAO(pttServerId);
        return extCorpContactDAO.isExternalContExist(mdn, corpId, persisterTxn);
    }

    public List<Integer> getCommonCorpListIdForMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnContactListDAO contactListDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getContactListDAO(pttServerId);
        var corpListIdForMdn = contactListDAO.getCommonCorpListIdForMDN(mdn, true, persisterTxn);
        return corpListIdForMdn;
    }

    @Override
    public List<String> getCommonContactListForMdns(String mdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCommonContactListForMdns(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnContactListDAO contactListDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getContactListDAO(pttServerId);
        List<String> result = contactListDAO.getCommonContactListForMdns(mdn, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return result;
    }

    @Override
    public List<String> getNonCommonContactListForMdns(String mdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException{
        final String methodName = "getNonCommonContactListForMdns(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnContactListDAO contactListDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getContactListDAO(pttServerId);
        List<String> result = contactListDAO.getNonCommonContactListForMdns(mdn, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return result;
    }

    public Map<Integer, Integer> getSsGroupIdsCorpInfo(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        Map<Integer, Integer> groupIdCorpIdInfo = new HashMap<>();
        KnSSChannelGroupInfoDAO ssChannelGroupInfoDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().getSSChannelGroupInfoDAO(pttServerId);
        groupIdCorpIdInfo = ssChannelGroupInfoDAO.getSsGroupIdsCorpInfo(mdn, readOnly, persisterTxn);
        return groupIdCorpIdInfo;
    }

    @Override
    public Map<Integer, Integer> getCorpGrpMemCount(Collection<Integer> grpIdList, String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupMemberCountDAO memCountDAO = KnPubDBTablesRegistry.
                getDBXdmTableRegistry().createCorpGrpMemCountDAO(pttServerId);
        return memCountDAO.getGroupMemCount(grpIdList, persisterTxn);
    }
}
