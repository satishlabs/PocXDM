/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnBulkFwUtil.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ravi Shanker P       Oct 1, 2012   7.4
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
 * ************************************************************************/
package com.kodiak.xdms.bulkfw.resources;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.idgenerator.KnIdGeneratorImpl;
import com.kodiak.utilities.idgenerator.dao.KnTableInfoBean;

import java.sql.*;
import java.util.*;

public final class KnBulkFwUtil {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkFwUtil.class);

    private static KnBulkFwUtil instance = null;
    private String pttServerId;

    private KnBulkFwUtil() {
        this.pttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
    }

    public static synchronized KnBulkFwUtil getInstance() {
        if (instance == null) {
            instance = new KnBulkFwUtil();
        }
        return instance;
    }

    public int retrieveIdForTable(String tableName, final String pttServerId, final String columnName, final boolean retry) {
        String methodName = "registerTableToIdGen(String)";
        KnIdGeneratorImpl idGenerator = KnIdGeneratorImpl.getInstance();
        tableName = tableName.toUpperCase();
        knLogger.debug(methodName, "registering table ", tableName, " to ID Gen");
        ArrayList<KnTableInfoBean> registerTable = new ArrayList<>();
        KnTableInfoBean tableInfoBean = new KnTableInfoBean();
        tableInfoBean.setTableName(tableName);
        tableInfoBean.setColumnName(columnName);
        tableInfoBean.setPttServerId(pttServerId);
        registerTable.add(tableInfoBean);
        idGenerator.registerTables(registerTable);

        if (retry) {
            ArrayList<KnTableInfoBean> registerTableList = new ArrayList<KnTableInfoBean>();
            registerTableList.add(tableInfoBean);
            idGenerator.reInitialiseIds(registerTableList);
        }
        return idGenerator.getNextId(tableName);
    }

    /**
     * Updates the Spring Batch Running jobs to failed as Spring batch Running
     * jobs will be in same state in case of process crash and Spring batch fw
     * cannot restart the running jobs hence it is required those jobs to set to
     * Failed so that those jobs can be restarted.
     */
    public void updateRunningJobsToFailed() {
        String methodName = "updateRunningJobsToFailed()";
        knLogger.debug(methodName, "ENTRY: updating the Running jobs if any to failed");
        KnPersisterTxn persisterTxn;

        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "Opening the transaction");
            persisterTxn.open();

            List<Integer> runningJobList = getExecutingJobId(persisterTxn);

            knLogger.debug(methodName, "Running Job List ", runningJobList);

            if (runningJobList != null && !runningJobList.isEmpty()) {
                Map<Integer, List<Integer>> runningJobStepList = getExecutingJobStepId(runningJobList, persisterTxn);

                if (runningJobStepList != null) {
                    for (Map.Entry<Integer, List<Integer>> mapSet : runningJobStepList.entrySet()) {
                        try {
                            updateJobStepToFailed(mapSet.getValue(), persisterTxn);
                        } catch (KnDAOException e) {
                            knLogger.error(methodName, "DAO Exception", e);
                            knLogger.error(methodName, e);
                        }
                    }
                }

                updateJobToFailed(runningJobList, persisterTxn);
            }

            knLogger.debug(methodName, "Saving the transaction");
            persisterTxn.save();
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - Failed to update running jobs", e);
            knLogger.error(methodName, e);
        }

    }

    /**
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    private List<Integer> getExecutingJobId(final KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExecutingJobIds(KnPersisterTxn)";
        knLogger.debug(methodName, "Get Executing Job Ids");
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        List<Integer> runningJobIdList = new ArrayList<Integer>();
        final String selectJobQry = "SELECT JOB_EXECUTION_ID FROM DG.BTCH_JOB_EXECUTION WHERE STATUS IN ('EXECUTING', 'UNKNOWN', 'STARTED')";
        try {
            conn = persisterTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(selectJobQry);

            knLogger.debug(methodName, "QUERY: Executing ", selectJobQry);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed");

            while (rs.next()) {
                runningJobIdList.add(rs.getInt("JOB_EXECUTION_ID"));
            }

            knLogger.debug(methodName, "Running Job List - " + runningJobIdList);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception", e);
            throw e;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception", e);
            throw KnDbUtil.processException(e, "Failed to get Etag for mdn - " + e.getMessage(), pttServerId, "DG.BTCH_JOB_EXECUTION", selectJobQry);
        } catch (Exception e) {
            knLogger.error(methodName, "Un Expected Exception", e);
            throw KnDbUtil.processException(e, "Failed to get Etag for mdn - " + e.getMessage(), pttServerId, "DG.BTCH_JOB_EXECUTION", selectJobQry);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }

        return runningJobIdList;
    }

    /**
     * @param runningJobIdList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    private Map<Integer, List<Integer>> getExecutingJobStepId(List<Integer> runningJobIdList, final KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getExecutingJobStepId(List<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Getting the Step Execution Ids for the running job id List");

        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        Map<Integer, List<Integer>> runningJobIdStepList = new HashMap<Integer, List<Integer>>();
        final String selectJobQry = "SELECT STEP_EXECUTION_ID FROM DG.BTCH_STEP_EXECUTION WHERE JOB_EXECUTION_ID=? AND STATUS IN ('EXECUTING', 'UNKNOWN', 'STARTED')";
        try {
            conn = persisterTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(selectJobQry);
            // pStmt.setString(1, "EXECUTING");

            for (Integer jobId : runningJobIdList) {
                pStmt.setInt(1, jobId);

                knLogger.debug(methodName, "QUERY: Executing ", selectJobQry, jobId);
                rs = pStmt.executeQuery();
                knLogger.debug(methodName, "QUERY: Executed");

                List<Integer> runningStepIdList = new ArrayList<Integer>();
                while (rs.next()) {
                    runningStepIdList.add(rs.getInt("STEP_EXECUTION_ID"));
                }

                if (!runningStepIdList.isEmpty()) {
                    runningJobIdStepList.put(jobId, runningStepIdList);
                }

            }

            knLogger.debug(methodName, "Running Step details List - ", runningJobIdStepList);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception", e);
            throw e;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception", e);
            throw KnDbUtil.processException(e, "Failed to step ID - " + e.getMessage(), pttServerId, "DG.BTCH_STEP_EXECUTION", selectJobQry);
        } catch (Exception e) {
            knLogger.error(methodName, "Un Expected Exception", e);
            throw KnDbUtil.processException(e, "Failed to get stepId - " + e.getMessage(), pttServerId, "DG.BTCH_STEP_EXECUTION", selectJobQry);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT: Return Step details List - ");

        return runningJobIdStepList;
    }

    /**
     * @param runningJobStepList
     * @param persisterTxn
     * @throws KnDAOException
     */
    private void updateJobStepToFailed(List<Integer> runningJobStepList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateJobStepToFailed(List<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Updating jobs to failed");
        Connection conn;
        PreparedStatement pStmt = null;
        final String tableName = "DG.BTCH_STEP_EXECUTION";
        final String updateStepQry = "UPDATE " + tableName + " SET STATUS='FAILED' WHERE STEP_EXECUTION_ID=?";
        List<Integer> failedJobStepIdList = new ArrayList<Integer>();
        List<Integer> processStepList = new LinkedList<Integer>(runningJobStepList);

        try {
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(updateStepQry);

            while (processStepList.size() > 0) {
                for (Integer stepId : runningJobStepList) {
                    pStmt.setInt(1, stepId);
                    pStmt.addBatch();
                }

                try {
                    knLogger.debug(methodName, "QUERY: Executing", updateStepQry, " with ", runningJobStepList);
                    int[] updateCount = pStmt.executeBatch();
                    if (updateCount.length == processStepList.size()) {
                        processStepList.clear();
                    }
                } catch (BatchUpdateException e) {
                    knLogger.error(methodName, "DAO Exception", e);
                    int[] updateCount = e.getUpdateCounts();

                    for (int failedRow : updateCount) {
                        failedJobStepIdList.add(processStepList.get(failedRow));
                        processStepList = processStepList.subList(0, failedRow + 1);
                    }

                }
            }
            knLogger.debug(methodName, "QUERY: Executed with failed Step Id List ", failedJobStepIdList);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception", e);
            throw e;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception", e);
            throw KnDbUtil.processException(e, "Failed to step ID - " + e.getMessage(), pttServerId, tableName, updateStepQry);
        } catch (Exception e) {
            knLogger.error(methodName, "Un Expected Exception", e);
            throw KnDbUtil.processException(e, "Failed to get stepId - " + e.getMessage(), pttServerId, tableName, updateStepQry);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }

    }

    /**
     * @param runningJobList
     * @param persisterTxn
     * @throws KnDAOException
     */
    private void updateJobToFailed(List<Integer> runningJobList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateJobToFailed(List<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Updating jobs to failed");
        Connection conn;
        PreparedStatement pStmt = null;
        String tableName = "DG.BTCH_JOB_EXECUTION";
        final String updateStepQry = "UPDATE " + tableName + " SET STATUS='FAILED' WHERE JOB_EXECUTION_ID=?";
        List<Integer> failedJobIdList = new ArrayList<Integer>();
        List<Integer> processJobList = new LinkedList<Integer>(runningJobList);

        try {
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(updateStepQry);

            while (processJobList.size() > 0) {
                for (Integer jobId : processJobList) {
                    pStmt.setInt(1, jobId);
                    pStmt.addBatch();
                }

                try {
                    knLogger.debug(methodName, "QUERY: Executing", updateStepQry, " with ", runningJobList);
                    int[] updateCount = pStmt.executeBatch();
                    if (updateCount.length == processJobList.size()) {
                        processJobList.clear();
                    }
                } catch (BatchUpdateException e) {
                    knLogger.error(methodName, "DAO Exception", e);
                    int[] updateCount = e.getUpdateCounts();

                    for (int failedRow : updateCount) {
                        failedJobIdList.add(processJobList.get(failedRow));
                        processJobList = processJobList.subList(0, failedRow + 1);
                    }

                }
            }
            knLogger.debug(methodName, "QUERY: batch completed");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception", e);
            throw e;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception", e);
            throw KnDbUtil.processException(e, "Failed to job ID - " + e.getMessage(), pttServerId, tableName, updateStepQry);
        } catch (Exception e) {
            knLogger.error(methodName, "Un Expected Exception", e);
            throw KnDbUtil.processException(e, "Failed to get jobId - " + e.getMessage(), pttServerId, tableName, updateStepQry);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }

        knLogger.debug(methodName, "EXIT : failedJobIdList ->", failedJobIdList);

    }
}