package com.kodiak.xdms.server.corpmgmt.business.helper;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dedicated sequential hierarchy ID generator.
 * Keeps all in-memory state and redundancy handling in one place.
 */
public final class KnHierarchyIdGenerator implements IStatusMgrNotifyIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnHierarchyIdGenerator.class);
    private static final long MAX_HIERARCHY_ID = 99_999_999L;

    private static final AtomicInteger hierarchyIdCounter = new AtomicInteger(-1);
    private static final Object hierarchyIdLock = new Object();

    private static final KnHierarchyIdGenerator INSTANCE = new KnHierarchyIdGenerator();
    private static volatile boolean registerStatus = false;

    private KnHierarchyIdGenerator() {
        // Singleton
    }

    public static KnHierarchyIdGenerator getInstance() {
        if (!registerStatus) {
            synchronized (KnHierarchyIdGenerator.class) {
                if (!registerStatus) {
                    List<IStatusMgrNotifyIntf> list = new ArrayList<IStatusMgrNotifyIntf>();
                    list.add(INSTANCE);
                    registerWithStatusManager(list);
                    registerStatus = true;
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Registers this generator for redundancy notifications if StatusManager classes are present.
     * Uses reflection to avoid hard compile-time dependency on classes unavailable in some build targets.
     */
    private static void registerWithStatusManager(List<IStatusMgrNotifyIntf> listeners) {
        final String methodName = "registerWithStatusManager";
        try {
            Class<?> clazz = Class.forName("com.kodiak.utilities.statusmgr.KnStatusManagerClient");
            clazz.getMethod("registerObjects", java.util.Collection.class).invoke(null, listeners);
            knLogger.info(methodName, "Registered hierarchy id generator for redundancy notifications");
        } catch (Throwable t) {
            // Registration is best-effort; generator still works with lazy DB initialization.
            knLogger.warn(methodName, "StatusManager registration skipped: " + t.getMessage());
        }
    }

    public String generateNextHierarchyId(String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "generateNextHierarchyId";

        if (hierarchyIdCounter.get() == -1) {
            synchronized (hierarchyIdLock) {
                if (hierarchyIdCounter.get() == -1) {
                    initializeHierarchyIdCounter(xdmsHome, persisterTxn);
                }
            }
        }

        long nextId;
        while (true) {
            int current = hierarchyIdCounter.get();
            if (current >= MAX_HIERARCHY_ID) {
                knLogger.error(methodName, "Hierarchy ID limit reached. Current max: " + current
                        + ", configured max: " + MAX_HIERARCHY_ID);
                throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        "Hierarchy ID limit reached. Maximum supported value is 8 digits (99999999)");
            }
            int candidate = current + 1;
            if (hierarchyIdCounter.compareAndSet(current, candidate)) {
                nextId = candidate;
                break;
            }
        }

        String hierarchyId = formatHierarchyIdAsString(nextId);
        knLogger.debug(methodName, "Generated hierarchy ID: " + hierarchyId);
        return hierarchyId;
    }

    private synchronized void initializeHierarchyIdCounter(String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "initializeHierarchyIdCounter";
        long maxId = fetchMaxHierarchyIdFromDb(xdmsHome, persisterTxn);
        if (maxId > MAX_HIERARCHY_ID) {
            knLogger.error(methodName, "Existing DB hierarchy MAX exceeds supported 8-digit range. DB max: "
                    + maxId + ", supported max: " + MAX_HIERARCHY_ID);
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Existing hierarchy ID exceeds supported 8-digit range");
        }
        hierarchyIdCounter.set((int) maxId);
        knLogger.info(methodName, "Hierarchy ID counter initialized to: " + maxId);
    }

    private synchronized long fetchMaxHierarchyIdFromDb(String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "fetchMaxHierarchyIdFromDb";
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            Long maxId = xdmDAO.getMaxHierarchyId(persisterTxn);
            return maxId == null ? 0L : maxId;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Failed to fetch max hierarchy ID from DB", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    private String formatHierarchyIdAsString(long id) {
        return String.format("%08d", id);
    }

    @Override
    public void notify(KnStatusMgrConstants.CARD_STATES previousState,
                       KnStatusMgrConstants.CARD_STATES currentState) {
        final String methodName = "notify";
        knLogger.info(methodName, "Redundancy Status Notify -> Prev Status: ", previousState,
                ", Curr Status: ", currentState);
        if (!KnStatusMgrConstants.CARD_STATES.ACTIVE.equals(currentState)) {
            synchronized (hierarchyIdLock) {
                hierarchyIdCounter.set(-1);
            }
            knLogger.info(methodName, "Hierarchy ID counter reset for non-active state: ", currentState);
        }
    }
}


