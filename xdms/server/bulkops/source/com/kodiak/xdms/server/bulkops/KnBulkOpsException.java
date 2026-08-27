package com.kodiak.xdms.server.bulkops;

import com.kodiak.xdms.server.common.KnXDMServerException;

public class KnBulkOpsException extends KnXDMServerException {

    public KnBulkOpsException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public KnBulkOpsException(String errorCode, STATUS_CODE statusCode, String errorMessage) {
        super(errorCode, statusCode, errorMessage);
    }

    public KnBulkOpsException(String errorCode, String errorMessage, Exception root) {
        super(errorCode, errorMessage, root);
    }

    public KnBulkOpsException(String errorCode, STATUS_CODE statusCode, String errorMessage, Exception root) {
        super(errorCode, statusCode, errorMessage, root);
    }
}
