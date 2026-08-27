package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.commdto.common.KnExtGatewayInfoDTO;
import com.kodiak.common.commdto.response.KnExtGWProfileInfoDTO;
import com.kodiak.common.commdto.response.KnXDMExtGWProfileListDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnAmperGatewayDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnAmperGatewayDAO.class);
    private String pttServerId;
    private static final String SELECT_EXT_GATEWAY_INFO_QRY = "SELECT GW_ID FROM DG.EXT_GATEWAY_INFO WHERE GW_ID= ?";
    private static final String SELECT_EXT_GATEWAY_PROFILE_INFO_QRY = "SELECT GW_ID FROM DG.EXT_GATEWAY_INFO";
    private static final String EXT_GATEWAY_ID="GW_ID";
    public KnAmperGatewayDAO(String pttServerId) {
        this.pttServerId= pttServerId;
    }

    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        return null;
    }
    public KnExtGatewayInfoDTO getExtGatewayDetails(String extGatewayId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExtGatewayDetails(String)";
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnExtGatewayInfoDTO knExtGatewayInfoDTO = null;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(SELECT_EXT_GATEWAY_INFO_QRY);
            pStmt.setString(1, extGatewayId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                knExtGatewayInfoDTO = new KnExtGatewayInfoDTO();
                knExtGatewayInfoDTO.setGwId(rs.getString(EXT_GATEWAY_ID));
            }
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to select GatewayInfo - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.EXT_GATEWAY_INFO, SELECT_EXT_GATEWAY_INFO_QRY);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select GatewayInfo  - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.EXT_GATEWAY_INFO, SELECT_EXT_GATEWAY_INFO_QRY);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName, "Exit select gateway- ", knExtGatewayInfoDTO);
        return knExtGatewayInfoDTO;
    }

    public List<KnExtGWProfileInfoDTO> getExtGWProfileList() throws KnDAOException {
        String methodName = "getExtGWProfileList()";
        List<KnExtGWProfileInfoDTO> profileList = new ArrayList<>();

        try (Connection conn = KnDBManager.getDBManagerInstance().getConnection(pttServerId, true);
             PreparedStatement pStmt = conn.prepareStatement(SELECT_EXT_GATEWAY_PROFILE_INFO_QRY);
             ResultSet rs = pStmt.executeQuery()) {

            while (rs.next()) {
                KnExtGWProfileInfoDTO profileInfo = new KnExtGWProfileInfoDTO();
                profileInfo.setGwId(rs.getString(EXT_GATEWAY_ID));
                profileList.add(profileInfo);
            }

        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to select ExtGWProfileList - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.EXT_GATEWAY_INFO, SELECT_EXT_GATEWAY_PROFILE_INFO_QRY);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select ExtGWProfileList  - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.EXT_GATEWAY_INFO, SELECT_EXT_GATEWAY_PROFILE_INFO_QRY);
        }

        knLogger.debug(methodName, "Exit ExtGWProfileList- ", profileList);
        return profileList;
    }
}
