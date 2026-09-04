/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.util.KnGeneralUtil;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubGroupDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;

import java.sql.*;
import java.util.Collection;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnOMAGroupDocDAO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 13, 2011           7.0
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
public class KnOMAGroupDocDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnOMAGroupDocDAO.class);

    public static final String CLASSNAME = KnOMAGroupDocDAO.class.getName();
    public static final String TABLENAME = "DG.XDM_OMA_GROUPDOC";

    public static final String GROUP_DOC_ID     = "GROUPDOCID";
    public static final String MDN              = "MDN";
    public static final String LIST_SERVICE_URI = "LISTSERVICEURI";
    public static final String DOC_URI          = "DOCURI";
    public static final String DISPLAY_NAME     = "DISPLAYNAME";
    public static final String XML_DOC          = "XMLDOC";
    public static final String GROUP_TYPE       = "GROUPTYPE";
    public static final String ETAG             = "ETAG";
    public String pttServerId = null;
    KnGenInfoUtil genInfoUtil = null;

    public KnOMAGroupDocDAO(String pttServerId) {
        this.pttServerId = pttServerId;
        genInfoUtil = KnGenInfoUtil.getInstance();
    }


    public static final String QRY_SELECT_DOC_FOR_UPDATE = "SELECT " + ETAG + ", " + XML_DOC + " FROM "
            + TABLENAME + " WHERE " + MDN + " = ? AND " + DOC_URI + " = ? FOR UPDATE";
    public static final String QRY_SELECT_XML_DOC1 = "SELECT " + XML_DOC + ", " + ETAG + " FROM " + TABLENAME
            + " WHERE " + MDN + " = ? AND " + DOC_URI + " = ?";
    public static final String QRY_SELECT_XML_DOC2 = "SELECT " + XML_DOC + ", " + ETAG + " FROM " + TABLENAME
            + " WHERE " + MDN + " = ? AND " + DOC_URI + " = ? AND " + ETAG + " = ?";
    public static final String QRY_SELECT_GROUP_DOC_FOR_GROUP_NAME = "SELECT " + GROUP_DOC_ID + ", " +
            LIST_SERVICE_URI + ", " + DOC_URI + ", " + DISPLAY_NAME  + ", " + XML_DOC + ", " + GROUP_TYPE + ", " + ETAG +" FROM " +
            TABLENAME + " WHERE " + MDN + "=? AND " + LIST_SERVICE_URI + "=?";
    public static final String QRY_SELECT_ALL_XML_DOCS = "SELECT " + GROUP_DOC_ID + ", " + MDN + ", "
            + LIST_SERVICE_URI + ", " + DOC_URI + ", " + DISPLAY_NAME  + ", " + XML_DOC + ", " + GROUP_TYPE
            + ", " + ETAG + " FROM " + TABLENAME + " WHERE " + MDN + " = ?";
    public static final String QRY_SELECT_ALL_DOCS = "SELECT " +
             DOC_URI + ", " + DISPLAY_NAME
            + ", " + ETAG +" FROM " + TABLENAME + " WHERE " + MDN + " = ?";

    public static final String QRY_SELECT_ETAG = "SELECT " + ETAG + " FROM " + TABLENAME
            + " WHERE " + MDN + " = ? AND " + DOC_URI + " = ?";
    public static final String QRY_UPDATE_XML_DOC = "UPDATE " + TABLENAME + " SET " + XML_DOC + " = ? "
            + ", " + ETAG + " = " + (ETAG + "+ 1") + " WHERE " + MDN + " = ? AND " + DOC_URI + " = ?";
    public static final String QRY_UPDATE_GROUP_DOC = "UPDATE " + TABLENAME + " SET " + XML_DOC + " = ? "
    		+ ", " + DISPLAY_NAME + " = ? , " + ETAG + " = " + (ETAG + "+ 1") + " WHERE " + MDN
            + " = ? AND " + LIST_SERVICE_URI + " = ?";
    public static final String QRY_INSERT = "INSERT INTO " + TABLENAME + " VALUES (?,?,?,?,?,?,?,?)";
    public static final String QRY_DELETE = "DELETE FROM " + TABLENAME + " WHERE " + GROUP_DOC_ID + " = ?";
    public static final String QRY_SELECT_GROUP_DOC_ID = "SELECT " + GROUP_DOC_ID + " FROM " + TABLENAME
                + " WHERE " + DOC_URI + " = ?";
    public static final String COUNT_GRPS_FOR_MDN = "SELECT COUNT(*) FROM " + TABLENAME + " WHERE " + MDN + " = ?";
    public static final String QRY_SELECT_GROUP_INFO = "SELECT " + DISPLAY_NAME  + ", " + ETAG + ", " + GROUP_TYPE +
             " FROM " + TABLENAME + " WHERE " + GROUP_DOC_ID + " = ?" + " AND " + MDN + " = ?";
    public static final String QRY_UPDATE_GRP_NAME = "UPDATE " + TABLENAME + " SET " + DISPLAY_NAME + " = ? "+
            " WHERE " + MDN + " = ? AND " + DOC_URI + " = ?";
    public static final String QRY_UPDATE_ETAGS_FOR_MDN = "UPDATE " + TABLENAME + " SET " +
            ETAG + " = " + (ETAG + "+ 1") + " WHERE " + MDN + " = ?";
    public static final String QRY_DELETE_ALL_GROUPS_FOR_MDN = "DELETE FROM " + TABLENAME + " WHERE " + MDN + " = ?";
    public static final String QRY_UPDATE_ALL_DOCS = "UPDATE " + TABLENAME + " SET " + MDN  + " = ? " + ", " +
            LIST_SERVICE_URI + " = ? " + ", " + DOC_URI + " = ? " + ", " + XML_DOC + " = ? "
            + ", " + ETAG + " = " + (ETAG + "+ 1") + " WHERE " + GROUP_DOC_ID + " = ?";
    public static final String QRY_DELETE_ALL_GROUPS_FOR_MDNS = "DELETE FROM " + TABLENAME + " WHERE " + MDN + " = ?";

    public static final String QRY_UPDATE_GRUP_DETAIL = "UPDATE " + TABLENAME + " SET " + LIST_SERVICE_URI + " = ? " +
            ", " + DOC_URI + " = ? " + ", " + DISPLAY_NAME + " = ? " + " WHERE " + GROUP_DOC_ID + " = ?";


    /**
     * This method is used for insertion of data in SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "insert", "Not Implemented");
    }

    /**
     * This method is used for updation of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "update", "Not Implemented");
    }

    /**
     * This method is used for deletion of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "delete", "Not Implemented");
    }

    /**
     * This method is used for retrieving of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     * @return Collection
     */
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "select", "Not Implemented");
        return null;
    }


    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void insertGroupDoc(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "insertGroupDoc(KnPubGroupInfoPersistDTO, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : groupInfoPersistDTO: " + groupInfoPersistDTO);

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        String mdn = groupInfoPersistDTO.getOwner();
        int groupDocId;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_INSERT;

            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStatement = conn.prepareStatement(query);

            groupDocId = genInfoUtil.retrieveIdForTable(TABLENAME, pttServerId, GROUP_DOC_ID, false);

            pStatement.setInt(1, groupDocId);
            pStatement.setString(2, mdn);
            pStatement.setString(3, groupInfoPersistDTO.getListServiceURI());
            pStatement.setString(4, groupInfoPersistDTO.getGroupDocURI());
            //multilingual revert change
            if(null != groupInfoPersistDTO.getGroupDisplayName())
            {
	            pStatement.setString(5, new String(groupInfoPersistDTO.getGroupDisplayName().getBytes("UTF-8"),"8859_1"));
            }
            String strXml = groupInfoPersistDTO.getStrXml();
            if (strXml.length() != strXml.getBytes().length) {
                knLogger.warn(methodName, " strXml.length():", (strXml.length()), " strXml.getBytes()length:", (strXml.getBytes().length));
                knLogger.warn(methodName, "invalid (UTF-8) bytes found " + KnGDPRTemplate.mdn(mdn));
            }
            pStatement.setBinaryStream(6, new ByteArrayInputStream(strXml.getBytes()), strXml.getBytes().length);
            pStatement.setInt(7, groupInfoPersistDTO.getGroupType());
            pStatement.setInt(8, KnConstants.INITIAL_ETAG);   // ETAG is set to 1 in case of create Group
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            groupInfoPersistDTO.setGroupDocId(groupDocId);
            knLogger.info( methodName, "Updated xmlDoc for MDN:  " + KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to insert record for Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to inset record for List - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : MDN ->" + KnGDPRTemplate.mdn(mdn));
        }

    }


    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateGroupDocXmlDoc(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateGroupDocXmlDoc(KnPubGroupInfoPersistDTO, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        String mdn = groupInfoPersistDTO.getOwner();

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_UPDATE_XML_DOC;

            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStatement = conn.prepareStatement(query);
            String strXml = KnGeneralUtil.convertStreamToStringEnc(groupInfoPersistDTO.getXmlDoc());
            knLogger.debug( methodName, "STR XML : " + strXml);
            if (strXml.length() != strXml.getBytes().length) {
                knLogger.warn(methodName, " strXml.length():", (strXml.length()), " strXml.getBytes()length:", (strXml.getBytes().length));
                knLogger.warn(methodName, "invalid (UTF-8) bytes found " + KnGDPRTemplate.mdn(mdn));
            }
            pStatement.setBinaryStream(1, new ByteArrayInputStream(strXml.getBytes()), strXml.getBytes().length);
            pStatement.setString(2, mdn);
            pStatement.setString(3, groupInfoPersistDTO.getGroupDocURI());
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Updated xmlDoc for MDN:  " + KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update xmldoc for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update xmldoc for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : MDN ->" + KnGDPRTemplate.mdn(mdn));
        }

    }


    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void replaceGroupDoc(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "replaceGroupDoc(KnPubGroupInfoPersistDTO, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : " + groupInfoPersistDTO);

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        String mdn = groupInfoPersistDTO.getOwner();

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_UPDATE_GROUP_DOC;

            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStatement = conn.prepareStatement(query);
            String strXml = groupInfoPersistDTO.getStrXml();
            if (strXml.length() != strXml.getBytes().length) {
                knLogger.warn(methodName, " strXml.length():", (strXml.length()), " strXml.getBytes()length:", (strXml.getBytes().length));
                knLogger.warn(methodName, "invalid (UTF-8) bytes found " + KnGDPRTemplate.mdn(mdn));
            }
            pStatement.setBinaryStream(1, new ByteArrayInputStream(strXml.getBytes()), strXml.getBytes().length);
            //multilingual revert change
            if(null != groupInfoPersistDTO.getGroupDisplayName())
            {
            	pStatement.setString(2, new String(groupInfoPersistDTO.getGroupDisplayName().getBytes("UTF-8"),"8859_1"));
            }
            pStatement.setString(3, mdn);
            pStatement.setString(4, groupInfoPersistDTO.getListServiceURI());
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "replace Grp Doc for MDN:  " + KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to replace grp doc for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to replace grp doc for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : MDN ->" + KnGDPRTemplate.mdn(mdn));
        }

    }


    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateGroupDisplayName(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateGroupDisplayName(KnPubGroupInfoPersistDTO, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        String mdn = groupInfoPersistDTO.getOwner();

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_UPDATE_GRP_NAME;

            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStatement = conn.prepareStatement(query);
            //multilingual revert change
            if(null != groupInfoPersistDTO.getGroupDisplayName())
            {
            	pStatement.setString(1, new String(groupInfoPersistDTO.getGroupDisplayName().getBytes("UTF-8"),"8859_1"));
            }
            pStatement.setString(2, mdn);
            pStatement.setString(3, groupInfoPersistDTO.getGroupDocURI());
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Updated group display name for MDN:  " + KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update group display name for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update group display name for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : MDN ->" + KnGDPRTemplate.mdn(mdn));
        }

    }


    /**
     * Get the current XML document from the OMA group Doc table (XCAP interface)
     *
     * @param mdn
     * @param xcapDocUri
     * @param etag
     * @param readonly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public String getCurrentGroupDocXmlDoc(String mdn, String xcapDocUri, int etag, boolean readonly,
                                           KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCurrentGroupDocXmlDoc(String, String, int, boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        InputStream is = null;
        String xmlDoc = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            conn = persisterTxn.getDBConnection(pttServerId, true);

            if (etag > 0) {
                query = QRY_SELECT_XML_DOC2;
                pStatement = conn.prepareStatement(query);
                pStatement.setString(1, mdn);
                pStatement.setString(2, xcapDocUri);
                pStatement.setInt(3, etag);
            } else {
                query = QRY_SELECT_XML_DOC1;
                pStatement = conn.prepareStatement(query);
                pStatement.setString(1, mdn);
                pStatement.setString(2, xcapDocUri);
            }

            knLogger.debug( methodName, "QUERY : Executing " + query + ", mdn : " + KnGDPRTemplate.mdn(mdn) +
                    "xcapDocUri : " + xcapDocUri + "etag : " + etag + "persisterTxn : " +
                    persisterTxn);


            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");
            if (rs.next()) {
                is = rs.getBinaryStream(1);
             //   etag = rs.getInt(2);
            } else {
                // Throw back Exception
                knLogger.error( methodName, "No Group Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found. Query ->" + query);
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
            xmlDoc = KnGeneralUtil.convertStreamToString(is);
            return xmlDoc;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve xmldoc for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve xmldoc for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : xml doc retrieved ->" + xmlDoc);
        }

    }


    /**
     * @param grpInfoPersistDTO
     * @param xcapDocUri
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void getCurrentGroupDoc(KnPubGroupInfoPersistDTO grpInfoPersistDTO, String xcapDocUri,
                                           KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCurrentGroupDoc(KnPubGroupInfoPersistDTO, String, int, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : GrpInfo: " + grpInfoPersistDTO);

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            conn = persisterTxn.getDBConnection(pttServerId, true);

            query = QRY_SELECT_DOC_FOR_UPDATE;
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, grpInfoPersistDTO.getOwner());
            pStatement.setString(2, xcapDocUri);

            knLogger.debug( methodName, "QUERY : Executing " + query + "persisterTxn : " +
                    persisterTxn);


            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");
            if (rs.next()) {
                grpInfoPersistDTO.setGroupDocEtag(rs.getInt(1));
//                String strXml = KnGeneralUtil.convertStreamToStringEnc(rs.getBinaryStream(2));
//                knLogger.info( methodName, "XML1 :" + strXml);
//                grpInfoPersistDTO.setXmlDoc(new ByteArrayInputStream(strXml.getBytes("UTF-8")));
                grpInfoPersistDTO.setXmlDoc(new ByteArrayInputStream(KnGeneralUtil.convertStreamToString(
                        rs.getBinaryStream(2)).getBytes()));
            } else {
                // Throw back Exception
                knLogger.error( methodName, "No Group Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found. Query ->" + query);
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve Group Info for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve Group Info for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : Group Info retrieved ->" + grpInfoPersistDTO);
        }

    }


    /**
     * @param mdn
     * @param listServiceUri
     * @param groupDispName
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnPubGroupInfoPersistDTO getOMAGroupDocForGroupName(String mdn, String listServiceUri, String groupDispName,
                                              KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getOMAGroupDocForGroupName(String, String, String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : Mdn: " + KnGDPRTemplate.mdn(mdn) + ", ListServiceUri : " + listServiceUri
                + ", GrpDispName: " + groupDispName);

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        KnPubGroupInfoPersistDTO grpInfoPersistDTO = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            conn = persisterTxn.getDBConnection(pttServerId, true);

            query = QRY_SELECT_GROUP_DOC_FOR_GROUP_NAME;
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            pStatement.setString(2, listServiceUri);

            knLogger.debug( methodName, "QUERY : Executing " + query + "persisterTxn : " +
                    persisterTxn);


            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");
            if (rs.next()) {
                grpInfoPersistDTO = new KnPubGroupInfoPersistDTO();
                grpInfoPersistDTO.setOwner(mdn);

                grpInfoPersistDTO.setGroupDocId(rs.getInt(1));
                grpInfoPersistDTO.setListServiceURI(rs.getString(2));
                grpInfoPersistDTO.setGroupDocURI(rs.getString(3));
                //multilingual revert change
                if(null != rs.getString(4))
                {
                    grpInfoPersistDTO.setGroupDisplayName(new String(rs.getString(4).trim().getBytes("8859_1"),"UTF-8"));
                }
                grpInfoPersistDTO.setXmlDoc(new ByteArrayInputStream(KnGeneralUtil.convertStreamToString(rs.getBinaryStream(5)).getBytes()));
                grpInfoPersistDTO.setGroupType(rs.getInt(6));
                grpInfoPersistDTO.setGroupDocEtag(rs.getInt(7));
            } else {
                // No need to Throw back Exception as if the group does not exist, a new group will be created
                knLogger.error( methodName, "No Group Info found");
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve Group Info for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve Group Info for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : Group Info retrieved ->" + grpInfoPersistDTO);
        }
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
        knLogger.debug( methodName, "Entry : ");

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnPubGroupDTO> groupList = null;

        try {

            query = QRY_SELECT_ALL_XML_DOCS;

            conn = persisterTxn.getDBConnection(pttServerId, true);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " , query , ", persisterTxn : " , persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                groupList = new ArrayList<KnPubGroupDTO>();
                do {
                    KnPubGroupInfoPersistDTO groupInfo = new KnPubGroupInfoPersistDTO();
                    groupInfo.setGroupDocId(rs.getInt(1));
                    groupInfo.setOwner(rs.getString(2));
                    groupInfo.setListServiceURI(rs.getString(3));
                    groupInfo.setGroupDocURI(rs.getString(4));
                    //multilingual revert change
                    if(null != rs.getString(5))
                    {
                       groupInfo.setGroupName(new String(rs.getString(5).trim().getBytes("8859_1"),"UTF-8"));
                    }
//                  groupInfo.setStrXml(KnGeneralUtil.convertStreamToString(rs.getBinaryStream(6)));
                    groupInfo.setXmlDoc(new ByteArrayInputStream(KnGeneralUtil.convertStreamToString(rs.getBinaryStream(6)).getBytes()));
                    groupInfo.setGroupType(rs.getInt(7));
                    groupInfo.setGroupDocEtag(rs.getInt(8));
                    groupList.add(groupInfo);
                } while (rs.next());
            } else {
                //throw exception
                knLogger.debug( methodName, "No Group Info found");
//                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found. Query ->" + query);
            }


            knLogger.debug( methodName, "Returning GroupList - " , groupList);
            return groupList;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " , e);

            throw KnDbUtil.processException(e, "Failed to retrieve GroupList for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " , e);

            throw e;
        } catch (Exception e) {
            knLogger.error( methodName,  "Unexpected Exception - " , e);

            throw KnDbUtil.processException(e, "Failed to retrieve GroupList for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnPubGroupDTO> getAllGroupsForMdn(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAllGroupsForMdn(String, boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");
        knLogger.info( methodName, "Entry : ",mdn,readOnly,persisterTxn);

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnPubGroupDTO> groupList = null;

        try {

            query = QRY_SELECT_ALL_DOCS;

            conn = persisterTxn.getDBConnection(pttServerId, readOnly);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " , query , ", persisterTxn : " , persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                groupList = new ArrayList<KnPubGroupDTO>();
                do {
                    KnPubGroupInfoPersistDTO groupInfo = new KnPubGroupInfoPersistDTO();
//                    groupInfo.setGroupDocId(rs.getInt(1));
//                    groupInfo.setOwner(rs.getString(2));
//                    groupInfo.setListServiceURI(rs.getString(3));
                    groupInfo.setGroupDocURI(rs.getString(1));
                    //multilingual revert change
                    if(null != rs.getString(2))
                    {
                       groupInfo.setGroupName(new String(rs.getString(2).trim().getBytes("8859_1"),"UTF-8"));
                    }
//                  groupInfo.setGroupType(rs.getInt(6));
                    groupInfo.setGroupDocEtag(rs.getInt(3));
                    groupList.add(groupInfo);
                } while (rs.next());
            } else {
                knLogger.debug( methodName, "No Group Info found");
            }


            knLogger.debug( methodName, "Returning GroupList - " , groupList);
            return groupList;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " , e);
             throw KnDbUtil.processException(e, "Failed to retrieve GroupList for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName,  "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to retrieve GroupList for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }


    /**
     * @param xcapDocUri
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getGroupDocId(String xcapDocUri, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupDocId(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int groupDocId = 0;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_SELECT_GROUP_DOC_ID;

            conn = persisterTxn.getDBConnection(pttServerId, true);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, xcapDocUri);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                groupDocId = rs.getInt(1);
            } else {
                // throw back exception
                knLogger.error( methodName, "No Group Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found. Query ->" + query);
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Returning groupDocId - " + groupDocId);
            return groupDocId;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve groupDocId for Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve groupDocId for Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : groupDocId ->" + groupDocId);
        }

    }


    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getCurrentGroupDocEtag(KnPubGroupInfoPersistDTO groupInfoPersistDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCurrentGroupDocEtag(KnPubGroupInfoPersistDTO,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : groupInfoPersistDTO: " + groupInfoPersistDTO);

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        String mdn = groupInfoPersistDTO.getOwner();
        int etag = 0;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_SELECT_ETAG;

            conn = persisterTxn.getDBConnection(pttServerId, readOnly);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            pStatement.setString(2, groupInfoPersistDTO.getGroupDocURI());
            knLogger.debug( methodName, "QUERY : Executing " + query + ", GroupDocUri:" +
                    groupInfoPersistDTO.getGroupDocURI() + ", persisterTxn : " + persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                etag = rs.getInt(1);
            } else {
                // Throw back exception
                knLogger.error( methodName, "No Group Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found. Query ->" + query);
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Returning etag - " + etag);
            return etag;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve etag for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve etag for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : etag ->" + etag);
        }
    }


    /**
     * @param groupDocId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnPubGroupDTO getGroupInfoForGroupDocId(int groupDocId, String ownerMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupInfoForGroupDocId(int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : GroupDocId:" + groupDocId + ", OwnerMdn:"+ KnGDPRTemplate.mdn(ownerMdn));

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int etag = 0;
        String displayName = null;
        int groupType = 0;
        String mdn = null;
        KnPubGroupDTO pubGroupDTO = null;

        try {
            //open a txn if its not already opened

            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_SELECT_GROUP_INFO;

            conn = persisterTxn.getDBConnection(pttServerId, true);
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1, groupDocId);
            pStatement.setString(2, ownerMdn);
            knLogger.debug( methodName, "QUERY : Executing " + query + "Group Doc Id" + groupDocId +
                    ", persisterTxn : " + persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
            	//multilingual revert change
            	if(null != rs.getString(1))
            	{
                    displayName = new String(rs.getString(1).trim().getBytes("8859_1"),"UTF-8");
            	}
                etag = rs.getInt(2);
                groupType = rs.getInt(3);
//                mdn = rs.getString(4);
            } else {
                // Throw back exception
                knLogger.error( methodName, "No Group Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found. Query ->" + query);
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
            pubGroupDTO = new KnPubGroupDTO();
            pubGroupDTO.setGroupDisplayName(displayName);
            pubGroupDTO.setOwner(mdn);
            pubGroupDTO.setGroupDocEtag(etag);
            pubGroupDTO.setGroupType(groupType);
            knLogger.info( methodName, "Returning group info - " + pubGroupDTO);
            return pubGroupDTO;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve group info for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve group info for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : group info ->" + pubGroupDTO);
        }
    }
    
    
    /**
     * @param groupDocId
     * @param persisterTxn
     * @throws KnDAOException
     */ 
    public void deleteGroupDoc(int groupDocId,
                              KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteGroupDoc(int, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_DELETE;

            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1, groupDocId);

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Delete Group for groupDocId:  " + groupDocId);
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Delete record for Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete record for List - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : groupDocId ->" + groupDocId);
        }
    }

    public int countGroupsOwnedByMDN(String mdn, String groupName,
                                     KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "countGroupsOwnedByMDN(String, String, KnPersisterTxn)";
        boolean ownedTxn = false;
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        StringBuffer sqlBuffer = new StringBuffer(100);
        int noOfGroups = -1;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            sqlBuffer.append(COUNT_GRPS_FOR_MDN);
            if (groupName != null && groupName.length() > 0) {
            	//multilingual revert change
                sqlBuffer.append(" AND ").append(LIST_SERVICE_URI).append(" = '").append(new String(groupName.getBytes("UTF-8"),"8859_1")).append("'");
            }
            query = sqlBuffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", mdnNumber : " + KnGDPRTemplate.mdn(mdn) +
                    ", persisterTxn : " + persisterTxn + ", listServiceUri: " + groupName);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");
            if (rs.next()) {
                noOfGroups = rs.getInt(1);
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Returning owned numebr of groups - " + noOfGroups);
            return noOfGroups;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve number of groups - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve number of groups - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStatement);
            knLogger.debug( methodName, "EXIT : Number of groups -> " + noOfGroups);
        }

    }


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateGroupDocEtagsForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateGroupDocEtagsForMdn(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : " + KnGDPRTemplate.mdn(mdn));

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_UPDATE_ETAGS_FOR_MDN;

            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Updated Etags for MDN:  " + KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Etags for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Etags for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : MDN ->" + KnGDPRTemplate.mdn(mdn));
        }

    }


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllGroupsForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteAllGroupsForMdn(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : " + KnGDPRTemplate.mdn(mdn));

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_DELETE_ALL_GROUPS_FOR_MDN;

            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            pStatement.execute();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Updated Etags for MDN:  " + KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete all groups for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete all groups for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : MDN ->" + KnGDPRTemplate.mdn(mdn));
        }
    }


    /**
     * @param groupDocs
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateAllGroupDocs(Collection<KnPubGroupDTO> groupDocs, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateAllGroupDocs(Collection<KnPubGroupDTO>, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_UPDATE_ALL_DOCS;

            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStatement = conn.prepareStatement(query);
            for (KnPubGroupDTO groupDoc : groupDocs) {
                String strXml = KnGeneralUtil.convertStreamToStringEnc(groupDoc.getXmlDoc());
                knLogger.debug( methodName, "STR XML : " + strXml);
                pStatement.setString(1, groupDoc.getOwner());
                pStatement.setString(2, groupDoc.getListServiceURI());
                pStatement.setString(3, groupDoc.getGroupDocURI());
                 if (strXml.length() != strXml.getBytes().length) {
                    knLogger.warn(methodName, " strXml.length():", (strXml.length()), " strXml.getBytes()length:", (strXml.getBytes().length));
                }
                pStatement.setBinaryStream(4, new ByteArrayInputStream(strXml.getBytes()), strXml.getBytes().length);
                pStatement.setInt(5, groupDoc.getGroupDocId());

                pStatement.addBatch();
            }
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            pStatement.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed." );

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Updated All Group Docs:");
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update all group docs - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update all group docs - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : Update Group docs :");
        }

    }

    /**
     * @param mdns
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllGroupsForMdn(List mdns, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteAllGroupsForMdn(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : " + KnGDPRTemplate.mdnList(mdns));

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }


            //StringBuffer buffer = new StringBuffer(200);
            //buffer.append(QRY_DELETE_ALL_GROUPS_FOR_MDNS).append(KnDbUtil.convertListToStringBuffer(mdns));
            //query = buffer.toString();
            query = QRY_DELETE_ALL_GROUPS_FOR_MDNS;
            conn = persisterTxn.getDBConnection(pttServerId, false);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            pstmt = conn.prepareStatement(query);
            for (Object mdn : mdns) {
                pstmt.setString(1, mdn.toString());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Updated Etags for MDN:  " + KnGDPRTemplate.mdnList(mdns));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete all groups for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete all groups for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT : MDN ->" + KnGDPRTemplate.mdnList(mdns));
        }
    }


    public void updateOMAGroupDetail(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateOMAGroupDetail(KnPubGroupInfoPersistDTO, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        String mdn = groupInfoPersistDTO.getOwner();

        try {
            query = QRY_UPDATE_GRUP_DETAIL;

            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStatement = conn.prepareStatement(query);
            //multilingual revert change
            pStatement.setString(1, groupInfoPersistDTO.getListServiceURI());
            pStatement.setString(2, groupInfoPersistDTO.getGroupDocURI());
            if(null != groupInfoPersistDTO.getGroupDisplayName())
            {
                pStatement.setString(3, new String(groupInfoPersistDTO.getGroupDisplayName().getBytes("UTF-8"),"8859_1"));
            }
            pStatement.setInt(4, groupInfoPersistDTO.getGroupDocId());
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);

            throw KnDbUtil.processException(e, "Failed to update group display name for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);

            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);

            throw KnDbUtil.processException(e, "Failed to update group display name for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_OMAGROUPDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }

    }

}
