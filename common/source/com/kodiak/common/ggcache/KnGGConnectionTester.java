package com.kodiak.common.ggcache;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import com.kodiak.common.ggcache.KnGGCacheConstants.GG_ERROR_CODES;
import com.kodiak.logger.KnLogger;
import com.mchange.v2.c3p0.AbstractConnectionTester;
// This class for only GG thin client
public class KnGGConnectionTester extends AbstractConnectionTester {
    private static final KnLogger knLogger = KnLogger.getLogger(KnGGConnectionTester.class);
    @Override
    public int activeCheckConnection(Connection c, String preferredTestQuery, Throwable[] rootCauseOutParamHolder) {
        final String methodName  = "activeCheckConnection()";
        knLogger.info(methodName, "Entered activeCheckConnection for the connection - " + c);
        return 0;
    }
    @Override
    public int statusOnException(Connection c, Throwable throwable, String preferredTestQuery,
                                 Throwable[] rootCauseOutParamHolder) {
        final String methodName = "statusOnException()";
        try {
            //knLogger.error(methodName, "Entered statusOnException for the connection - " + c);
            knLogger.error(methodName, " preferredTestQuery - " + preferredTestQuery, "Throwable - " + throwable);
            if (throwable != null && (throwable instanceof SQLException)) {
                SQLException sqlException = (SQLException) throwable;
                knLogger.info(methodName, "sqlException.getMessage() - " + sqlException.getMessage(),
                        " sqlException.getCause() - " + sqlException.getCause(),
                        "  sqlException.getErrorCode() - " + sqlException.getErrorCode(),
                        "sqlException.getSQLState() - " + sqlException.getSQLState());
                String erroCode = sqlException.getSQLState();
                if (erroCode != null) {
                    boolean matched = Arrays.stream(GG_ERROR_CODES.values())
                            .anyMatch(e -> e.getGgErrroCode().equals(erroCode.trim()));
                    if (matched) {
                        knLogger.info(methodName, "refreshing datasource connection pool");
                        KnGGConnection.resetDdataSource();
                    }
                }
            } else {
                knLogger.error(methodName, "UnExpected Exception - ", throwable);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "UnExpected Exception - ", e);
        }
        knLogger.info(methodName, "Exit");
        return 0;
    }
}