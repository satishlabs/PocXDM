package com.kodiak.xdms.server.bulkops.dto.common;


import com.kodiak.common.commdto.response.KnBulkOpsErrorDetail;


import java.util.HashMap;
import java.util.Map;

public class KnBulkUpdateStatusDTO {
    // Successful updates: MDN -> persisted DTO
    private final Map<String, KnSubsProfilePersistDTO> success = new HashMap<>();
    // Failures: MDN -> bulk exception info
    private final Map<String, KnBulkOpsErrorDetail> exceptions = new HashMap<>();

    public KnBulkUpdateStatusDTO(Map <String,KnSubsProfilePersistDTO>  success,
                                 Map<String, KnBulkOpsErrorDetail> exceptions) {
        if (success != null) {
            this.success.putAll(success);
        }
        if (exceptions != null) {
            this.exceptions.putAll(exceptions);
        }
    }

    public Map<String, KnSubsProfilePersistDTO> getSuccess() {
        return success;
    }

    public Map<String, KnBulkOpsErrorDetail> getExceptions() {
        return exceptions;
    }
}

