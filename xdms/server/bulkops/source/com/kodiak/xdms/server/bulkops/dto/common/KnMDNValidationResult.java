package com.kodiak.xdms.server.bulkops.dto.common;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Result object for MDN validation operations
 * Contains lists of valid and invalid MDNs with error details
 */
public class KnMDNValidationResult {
    private final Set<String> validMdns = ConcurrentHashMap.newKeySet();
    private final List<InvalidMdnDetail> invalidMdns = new CopyOnWriteArrayList<>();

    /**
     * Add a valid MDN to the result
     */
    public void addValidMdn(String mdn) {
        validMdns.add(mdn);
    }

    /**
     * Add an invalid MDN with error details to the result
     */
    public void addInvalidMdn(String mdn, String errorCode, String errorMessage) {
        invalidMdns.add(new InvalidMdnDetail(mdn, errorCode, errorMessage));
    }

    /**
     * Get set of valid MDNs
     */
    public Set<String> getValidMdns() {
        return validMdns;
    }

    /**
     * Get list of invalid MDNs with error details
     */
    public List<InvalidMdnDetail> getInvalidMdns() {
        return invalidMdns;
    }

    /**
     * Check if all MDNs are valid
     */
    public boolean isAllValid() {
        return invalidMdns.isEmpty();
    }

    /**
     * Check if all MDNs are invalid
     */
    public boolean isAllInvalid() {
        return validMdns.isEmpty();
    }

    /**
     * Inner class to hold invalid MDN details
     */
    public static class InvalidMdnDetail {
        private final String mdn;
        private final String errorCode;
        private final String errorMessage;

        public InvalidMdnDetail(String mdn, String errorCode, String errorMessage) {
            this.mdn = mdn;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public String getMdn() {
            return mdn;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        @Override
        public String toString() {
            return "InvalidMdnDetail{" +
                    "mdn='" + mdn + '\'' +
                    ", errorCode='" + errorCode + '\'' +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "KnMDNValidationResult{" +
                "validMdns=" + validMdns.size() +
                ", invalidMdns=" + invalidMdns.size() +
                '}';
    }
}

