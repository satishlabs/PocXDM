package com.kodiak.xdms.server.bulkops.utils;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.bulkops.KnBulkOpsException;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsErrorCodes;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KnBulkOpsDBUtil {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkOpsDBUtil.class);
    private static KnBulkOpsDBUtil instance = null;
    private static String xdmPttServerId;

    public KnBulkOpsDBUtil() throws KnBulkOpsException {
        xdmPttServerId = retrieveLocalXDMPttServerId();
    }

    public static synchronized KnBulkOpsDBUtil getInstance() throws KnBulkOpsException {
        if (instance == null) {
            instance = new KnBulkOpsDBUtil();
        }
        return instance;
    }

    /**
     * Retrieve the local XDM PTT Server ID
     *
     * @return Local XDM PTT Server ID
     * @throws KnBulkOpsException if unable to retrieve the ID
     */
    public static String retrieveLocalXDMPttServerId()  throws KnBulkOpsException {
        KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
        } catch (KnBOException e) {
            knLogger.error( "constructor", "failed to retrieve xdm Ptt Sever Id");
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
        }
        return xdmPttServerId;
    }

    /**
     * Get the local XDM PTT Server ID
     *
     * @return Local XDM PTT Server ID
     */
    public static String getXdmPttServerId() {
        return xdmPttServerId;
    }

    /**
     * Get DB Connection for local XDM PTT Server
     *
     * @param persisterTxn Database transaction
     * @param dualDSNIndex The dataStore to use
     * @return Connection to the database
     * @throws KnDAOException if unable to get connection
     */
    public Connection getDBConnection(KnPersisterTxn persisterTxn, KnDBConst.DataStores dualDSNIndex) throws KnDAOException {
        String methodName = " getDBConnection(KnPersisterTxn, KnDBConst.DataStores)";
        String pttId;
        Connection conn;

        pttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
        if (dualDSNIndex == null) {
            conn = persisterTxn.getDBConnection(pttId, false);
        }else {
            conn = persisterTxn.getDBConnection(pttId, dualDSNIndex, false);
        }
        knLogger.debug(methodName, "local  pttId ", pttId);

        return conn;
    }

    /**
     * Functional interface for processing ResultSet
     */
    @FunctionalInterface
    public interface ResultSetProcessor<T> {
        T process(ResultSet rs) throws Exception;
    }

    /**
     * Common method to execute a query with IN clause for a list of values
     *
     * @param queryPrefix    SQL query prefix (e.g., "SELECT COLUMN FROM TABLE WHERE COLUMN IN ")
     * @param valueList      List of values to query
     * @param processor      Callback function to process the ResultSet and extract desired data
     * @param persisterTxn   Database transaction (can be null)
     * @param dualDSNIndex   The dataStore to use
     * @return Result from processing the ResultSet
     * @throws KnDAOException if database operation fails
     */
    public <T> T executeInClauseQuery(String queryPrefix,
                                      List<String> valueList,
                                      ResultSetProcessor<T> processor,
                                      KnPersisterTxn persisterTxn,
                                      KnDBConst.DataStores dualDSNIndex) throws KnDAOException {
        String methodName = "executeInClauseQuery(List<String>, KnPersisterTxn, String, ResultSetProcessor, KnDBConst.DataStores)";
        knLogger.debug(methodName, "Executing IN clause query with prefix: ", queryPrefix);
        boolean ownedTxn = false;
        int size = valueList.size();
        String placeholders = String.join(",", java.util.Collections.nCopies(size, "?"));
        final String query = queryPrefix + "(" + placeholders + ")";
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        try{
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            conn = getDBConnection(persisterTxn, dualDSNIndex);
            pStmt = conn.prepareStatement(query);

            for (int i = 0; i < size; i++) {
                pStmt.setString(i + 1, valueList.get(i));
            }

            rs = pStmt.executeQuery();
            T result = processor.process(rs);

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }
            return result;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        }  catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while executing query - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to execute query - " + e.getMessage(),
                    null, null, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            // Connection is managed by transaction, don't close it
        }
    }

    /**
     * Common method to execute DELETE/UPDATE with IN clause
     * Builds the IN clause placeholders automatically based on valueList size
     *
     * @param queryPrefix    SQL query prefix ending with "IN " (e.g., "DELETE FROM table WHERE col IN ")
     * @param valueList      List of values for the IN clause
     * @param persisterTxn   Database transaction (can be null)
     * @param dualDSNIndex   The dataStore to use
     * @return Number of rows affected
     * @throws KnDAOException if database operation fails
     */
    public int executeInClauseUpdate(String queryPrefix,
                                     List<String> valueList,
                                     KnPersisterTxn persisterTxn,
                                     KnDBConst.DataStores dualDSNIndex) throws KnDAOException {
        String methodName = "executeInClauseUpdate(String, List<String>, KnPersisterTxn, KnDBConst.DataStores)";
        knLogger.debug(methodName, "Executing IN clause update with prefix: ", queryPrefix);

        if (valueList == null || valueList.isEmpty()) {
            knLogger.debug(methodName, "Empty value list, returning 0");
            return 0;
        }

        boolean ownedTxn = false;
        int size = valueList.size();
        String placeholders = String.join(",", java.util.Collections.nCopies(size, "?"));
        final String query = queryPrefix + "(" + placeholders + ")";
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            conn = getDBConnection(persisterTxn, dualDSNIndex);
            pStmt = conn.prepareStatement(query);

            for (int i = 0; i < size; i++) {
                pStmt.setString(i + 1, valueList.get(i));
            }

            int rowsAffected = pStmt.executeUpdate();
            knLogger.debug(methodName, "Rows affected: ", rowsAffected);

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }
            return rowsAffected;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while executing update - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to execute update - " + e.getMessage(),
                    null, null, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            // Connection is managed by transaction, don't close it
        }
    }

    /**
     * Common method to execute a query with parameters
     *
     * @param query          SQL query to execute
     * @param params         Parameters for the query
     * @param processor      Callback function to process the ResultSet and extract desired data
     * @param persisterTxn   Database transaction (can be null)
     * @param dualDSNIndex   The dataStore to use
     * @return Result from processing the ResultSet
     * @throws KnDAOException if database operation fails
     */
    public <T> T executeQuery(String query, Object[] params, ResultSetProcessor<T> processor, KnPersisterTxn persisterTxn, KnDBConst.DataStores dualDSNIndex) throws KnDAOException{
        String methodName = "executeQuery(String, Object[], ResultSetProcessor, KnPersisterTxn, KnDBConst.DataStores)";
        knLogger.debug(methodName, "Executing query: ", query);
        boolean ownedTxn = false;
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            conn = getDBConnection(persisterTxn, dualDSNIndex);
            pStmt = conn.prepareStatement(query);

            // Set parameters if provided
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pStmt.setObject(i + 1, params[i]);
                }
            }

            rs = pStmt.executeQuery();
            T result = processor.process(rs);

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }
            return result;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while executing query - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to execute query - " + e.getMessage(),
                    null, null, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            // Connection is managed by transaction, don't close it
        }
    }

    /**
     * Functional interface for processing batch updates
     */
    @FunctionalInterface
    public interface BatchProcessor<T> {
        void process(PreparedStatement pStmt, T item) throws Exception;
    }

    /**
     * Execute batch update operation for bulk inserts/updates
     *
     * @param query          SQL query with placeholders
     * @param items          List of items to process
     * @param processor      Callback function to set parameters for each item
     * @param persisterTxn   Database transaction (can be null)
     * @param dualDSNIndex   The dataStore to use
     * @return Array of update counts for each batch execution
     * @throws KnDAOException if database operation fails
     */
    public <T> int[] executeBatchUpdate(String query, List<T> items, BatchProcessor<T> processor, KnPersisterTxn persisterTxn, KnDBConst.DataStores dualDSNIndex) throws KnDAOException {
        String methodName = "executeBatchUpdate(String, List, BatchProcessor, KnPersisterTxn)";
        knLogger.debug(methodName, "Executing batch update with ", items.size(), " items");
        boolean ownedTxn = false;
        Connection conn = null;
        PreparedStatement pStmt = null;

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            conn = getDBConnection(persisterTxn,dualDSNIndex);
            pStmt = conn.prepareStatement(query);

            for (T item : items) {
                processor.process(pStmt, item);
                pStmt.addBatch();
            }

            knLogger.debug(methodName, "Executing batch update for ", items.size(), " items");
            knLogger.debug(methodName, "query being executed  ", pStmt.toString());
            int[] updateCounts = pStmt.executeBatch();

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }

            knLogger.debug(methodName, "Batch update completed with counts: ", java.util.Arrays.toString(updateCounts));
            return updateCounts;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while executing batch update - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to execute batch update - " + e.getMessage(),
                    null, null, query);
        }
    }
    
    /**
     * Execute batch update operation for bulk inserts/updates
     *
     * @param query          SQL query with placeholders
     * @param batchParams    List of parameter arrays for batch execution
     * @param persisterTxn   Database transaction (can be null)
     * @param dualDSNIndex   The dataStore to use
     * @throws KnDAOException if database operation fails
     */
    public void executeBatchUpdate(String query, List<Object[]> batchParams, KnPersisterTxn persisterTxn, KnDBConst.DataStores dualDSNIndex) throws KnDAOException {
        String methodName = "executeBatchUpdate(String, List<Object[]>, KnPersisterTxn, KnDBConst.DataStores)";
        knLogger.debug(methodName, "Executing batch update with ", batchParams.size(), " batches");
        boolean ownedTxn = false;
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            conn = getDBConnection(persisterTxn, dualDSNIndex);
            pStmt = conn.prepareStatement(query);

            for (Object[] params : batchParams) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    // TimesTen requires explicit setBytes() for VARBINARY columns
                    if (param instanceof byte[]) {
                        pStmt.setBytes(i + 1, (byte[]) param);
                    } else {
                        // setObject() works fine for String, Integer, Long, etc.
                        pStmt.setObject(i + 1, param);
                    }
                }
                pStmt.addBatch();
            }

            int[] results = pStmt.executeBatch();
            knLogger.debug(methodName, "Batch executed successfully, affected rows: ", results.length);

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while executing batch - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to execute batch update - " + e.getMessage(),
                    null, null, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            // Connection is managed by transaction, don't close it
        }
    }

    /**
     * Builder class for constructing MERGE (upsert) queries for TimesTen database.
     * This builder provides a fluent API for creating MERGE statements that will:
     * - INSERT a new record if the key doesn't exist
     * - UPDATE the existing record if the key already exists
     *
     * Thread-safe: Each instance should be used for a single query construction.
     *
     * Example usage:
     * <pre>
     * MergeQueryBuilder builder = new MergeQueryBuilder("POCSUBSCRINFO")
     *     .withKeyColumns(Arrays.asList("MDN"))
     *     .withValueColumns(queryFields);
     * String mergeQuery = builder.build();
     * </pre>
     */
    public static class MergeQueryBuilder {
        private final String tableName;
        private List<String> keyColumns;
        private List<String> allColumns;
        private List<String> updateColumns;

        /**
         * Creates a new MergeQueryBuilder for the specified table
         *
         * @param tableName The target table name for the MERGE operation
         */
        public MergeQueryBuilder(String tableName) {
            if (tableName == null || tableName.trim().isEmpty()) {
                throw new IllegalArgumentException("Table name cannot be null or empty");
            }
            this.tableName = tableName;
            this.keyColumns = new ArrayList<>();
            this.allColumns = new ArrayList<>();
            this.updateColumns = null;
        }

        /**
         * Sets the key columns used for matching records (typically primary key columns)
         * These columns will be used in the ON clause of the MERGE statement
         *
         * @param keyColumns List of column names that form the key for matching
         * @return this builder instance for method chaining
         */
        public MergeQueryBuilder withKeyColumns(List<String> keyColumns) {
            if (keyColumns == null || keyColumns.isEmpty()) {
                throw new IllegalArgumentException("Key columns cannot be null or empty");
            }
            this.keyColumns = new ArrayList<>(keyColumns);
            return this;
        }

        /**
         * Sets all columns to be included in the MERGE operation.
         * These columns will be used for both INSERT and UPDATE operations.
         *
         * @param allColumns List of all column names including key columns
         * @return this builder instance for method chaining
         */
        public MergeQueryBuilder withAllColumns(List<String> allColumns) {
            if (allColumns == null || allColumns.isEmpty()) {
                throw new IllegalArgumentException("All columns list cannot be null or empty");
            }
            this.allColumns = new ArrayList<>(allColumns);
            return this;
        }

        /**
         * Sets specific columns to be updated when a matching record is found.
         * If not specified, all non-key columns will be updated.
         *
         * @param updateColumns List of column names to update (excluding key columns)
         * @return this builder instance for method chaining
         */
        public MergeQueryBuilder withUpdateColumns(List<String> updateColumns) {
            if (updateColumns != null) {
                this.updateColumns = new ArrayList<>(updateColumns);
            }
            return this;
        }

        /**
         * Builds the MERGE SQL query string.
         *
         * TimesTen MERGE syntax:
         * MERGE INTO target_table USING (SELECT ? AS col1, ? AS col2, ...) src
         * ON (target_table.key_col = src.key_col)
         * WHEN MATCHED THEN UPDATE SET col1 = src.col1, col2 = src.col2, ...
         * WHEN NOT MATCHED THEN INSERT (col1, col2, ...) VALUES (src.col1, src.col2, ...)
         *
         * @return The constructed MERGE SQL query string with placeholders
         * @throws IllegalStateException if required fields are not set
         */
        public String build() {
            validateState();

            StringBuilder query = new StringBuilder(512);

            // Build the USING clause with a SELECT of parameters
            query.append("MERGE INTO ").append(tableName).append(" tgt USING (SELECT ");

            // Create source values with placeholders
            for (int i = 0; i < allColumns.size(); i++) {
                if (i > 0) {
                    query.append(", ");
                }
                query.append("? AS ").append(allColumns.get(i));
            }
            query.append(" FROM DUAL) src ON (");

            // Build ON clause with key columns
            for (int i = 0; i < keyColumns.size(); i++) {
                if (i > 0) {
                    query.append(" AND ");
                }
                query.append("tgt.").append(keyColumns.get(i))
                     .append(" = src.").append(keyColumns.get(i));
            }
            query.append(") ");

            // Build WHEN MATCHED (UPDATE) clause
            List<String> columnsToUpdate = getColumnsToUpdate();
            if (!columnsToUpdate.isEmpty()) {
                query.append("WHEN MATCHED THEN UPDATE SET ");
                for (int i = 0; i < columnsToUpdate.size(); i++) {
                    if (i > 0) {
                        query.append(", ");
                    }
                    String col = columnsToUpdate.get(i);
                    query.append("tgt.").append(col).append(" = src.").append(col);
                }
            }

            // Build WHEN NOT MATCHED (INSERT) clause
            query.append(" WHEN NOT MATCHED THEN INSERT (");
            for (int i = 0; i < allColumns.size(); i++) {
                if (i > 0) {
                    query.append(", ");
                }
                query.append(allColumns.get(i));
            }
            query.append(") VALUES (");
            for (int i = 0; i < allColumns.size(); i++) {
                if (i > 0) {
                    query.append(", ");
                }
                query.append("src.").append(allColumns.get(i));
            }
            query.append(")");

            return query.toString();
        }

        /**
         * Validates the builder state before building the query
         */
        private void validateState() {
            if (keyColumns.isEmpty()) {
                throw new IllegalStateException("Key columns must be specified");
            }
            if (allColumns.isEmpty()) {
                throw new IllegalStateException("All columns must be specified");
            }
            // Ensure all key columns are present in allColumns
            for (String keyCol : keyColumns) {
                if (!allColumns.contains(keyCol)) {
                    throw new IllegalStateException("Key column '" + keyCol + "' must be present in all columns list");
                }
            }
        }

        /**
         * Gets the list of columns to update (excludes key columns)
         */
        private List<String> getColumnsToUpdate() {
            List<String> result = new ArrayList<>();
            List<String> sourceColumns = (updateColumns != null) ? updateColumns : allColumns;

            for (String col : sourceColumns) {
                // Exclude key columns from update
                if (!keyColumns.contains(col)) {
                    result.add(col);
                }
            }
            return result;
        }

        /**
         * Returns an unmodifiable view of the configured key columns
         */
        public List<String> getKeyColumns() {
            return Collections.unmodifiableList(keyColumns);
        }

        /**
         * Returns an unmodifiable view of all configured columns
         */
        public List<String> getAllColumns() {
            return Collections.unmodifiableList(allColumns);
        }
    }

    /**
     * Generates a MERGE query for upsert operations (INSERT if not exists, UPDATE if exists).
     * This is a convenience method that uses MergeQueryBuilder internally.
     *
     * @param tableName    The target table name
     * @param keyColumns   List of key column names used for matching (typically primary key)
     * @param allColumns   List of all column names to be included in the operation
     * @return The constructed MERGE SQL query string with placeholders
     */
    public static String getMergeQuery(String tableName, List<String> keyColumns, List<String> allColumns) {
        return new MergeQueryBuilder(tableName)
                .withKeyColumns(keyColumns)
                .withAllColumns(allColumns)
                .build();
    }

    /**
     * Generates a MERGE query with specific update columns for upsert operations.
     *
     * @param tableName      The target table name
     * @param keyColumns     List of key column names used for matching
     * @param allColumns     List of all column names for INSERT operation
     * @param updateColumns  List of columns to update when record exists (excluding keys)
     * @return The constructed MERGE SQL query string with placeholders
     */
    public static String getMergeQuery(String tableName, List<String> keyColumns,
                                       List<String> allColumns, List<String> updateColumns) {
        return new MergeQueryBuilder(tableName)
                .withKeyColumns(keyColumns)
                .withAllColumns(allColumns)
                .withUpdateColumns(updateColumns)
                .build();
    }

    /**
     * Execute batch MERGE (upsert) operation for bulk inserts/updates.
     * This method performs INSERT if record doesn't exist, or UPDATE if it does.
     *
     * @param tableName      The target table name
     * @param keyColumns     List of key column names for matching records
     * @param allColumns     List of all column names
     * @param batchParams    List of parameter arrays for batch execution
     * @param persisterTxn   Database transaction (can be null)
     * @param dualDSNIndex   The dataStore to use
     * @throws KnDAOException if database operation fails
     */
    public void executeBatchMerge(String tableName, List<String> keyColumns, List<String> allColumns,
                                  List<Object[]> batchParams, KnPersisterTxn persisterTxn,
                                  KnDBConst.DataStores dualDSNIndex) throws KnDAOException {
        String methodName = "executeBatchMerge(String, List, List, List<Object[]>, KnPersisterTxn, KnDBConst.DataStores)";
        knLogger.debug(methodName, "ENTRY: Executing batch MERGE for table ", tableName, " with ", batchParams.size(), " records");

        String mergeQuery = getMergeQuery(tableName, keyColumns, allColumns);
        knLogger.debug(methodName, "Generated MERGE query: ", mergeQuery);

        executeBatchUpdate(mergeQuery, batchParams, persisterTxn, dualDSNIndex);

        knLogger.debug(methodName, "EXIT: Batch MERGE completed successfully");
    }

    /**
     * Execute batch MERGE (upsert) operation with specific update columns.
     * This method allows specifying which columns to update when a matching record is found.
     *
     * @param tableName      The target table name
     * @param keyColumns     List of key column names for matching records
     * @param allColumns     List of all column names
     * @param updateColumns  List of columns to update when record exists
     * @param batchParams    List of parameter arrays for batch execution
     * @param persisterTxn   Database transaction (can be null)
     * @param dualDSNIndex   The dataStore to use
     * @throws KnDAOException if database operation fails
     */
    public void executeBatchMerge(String tableName, List<String> keyColumns, List<String> allColumns,
                                  List<String> updateColumns, List<Object[]> batchParams,
                                  KnPersisterTxn persisterTxn, KnDBConst.DataStores dualDSNIndex) throws KnDAOException {
        String methodName = "executeBatchMerge(String, List, List, List, List<Object[]>, KnPersisterTxn, KnDBConst.DataStores)";
        knLogger.debug(methodName, "ENTRY: Executing batch MERGE for table ", tableName,
                      " with ", batchParams.size(), " records and specific update columns");

        String mergeQuery = getMergeQuery(tableName, keyColumns, allColumns, updateColumns);
        knLogger.debug(methodName, "Generated MERGE query: ", mergeQuery);

        executeBatchUpdate(mergeQuery, batchParams, persisterTxn, dualDSNIndex);

        knLogger.debug(methodName, "EXIT: Batch MERGE with specific update columns completed successfully");
    }

    /**
     * Execute batch MERGE operation with a custom processor for setting parameters.
     * This provides maximum flexibility for complex parameter binding scenarios.
     *
     * @param tableName      The target table name
     * @param keyColumns     List of key column names for matching records
     * @param allColumns     List of all column names
     * @param items          List of items to process
     * @param processor      Callback function to set parameters for each item
     * @param persisterTxn   Database transaction (can be null)
     * @param dualDSNIndex   The dataStore to use
     * @return Array of update counts for each batch execution
     * @throws KnDAOException if database operation fails
     */
    public <T> int[] executeBatchMerge(String tableName, List<String> keyColumns, List<String> allColumns,
                                       List<T> items, BatchProcessor<T> processor,
                                       KnPersisterTxn persisterTxn, KnDBConst.DataStores dualDSNIndex) throws KnDAOException {
        String methodName = "executeBatchMerge(String, List, List, List<T>, BatchProcessor, KnPersisterTxn, KnDBConst.DataStores)";
        knLogger.debug(methodName, "ENTRY: Executing batch MERGE with processor for table ", tableName,
                      " with ", items.size(), " items");

        String mergeQuery = getMergeQuery(tableName, keyColumns, allColumns);
        knLogger.debug(methodName, "Generated MERGE query: ", mergeQuery);

        int[] results = executeBatchUpdate(mergeQuery, items, processor, persisterTxn, dualDSNIndex);

        knLogger.debug(methodName, "EXIT: Batch MERGE with processor completed successfully");
        return results;
    }

    /**
     * Get BASE_PKGCODE from system configuration.
     * This is used in FeatureSet calculation to get the base package default features.
     *
     * @param persisterTxn Database transaction
     * @return BASE_PKGCODE value, or null if not configured
     */
    public String getBasePkgCode(KnPersisterTxn persisterTxn) {
        String methodName = "getBasePkgCode";
        try {
            KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
            java.util.Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            if (paramNameValueMap != null) {
                return paramNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.BASE_PKGCODE);
            }
        } catch (Exception e) {
            knLogger.warn(methodName, "Failed to get BASE_PKGCODE from config: ", e);
        }
        return null;
    }

}
