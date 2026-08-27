/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.bulkops.dto.common;

/**
 * Consolidated DTO for POC Server and Partition Configuration
 * Contains all POC-related configuration data in a single object for optimal memory usage
 * Thread-safe - immutable after construction
 */
public class KnBulkOpsPocConfigDTO {

    // ========== Partition Configuration Fields ==========
    private String partitionPttServerId;
    private Integer mdnPartitionTypePOC;
    private Integer enableCorpAccAnch;
    private Integer maxSubsPerXDMS;
    private Integer maxSubsPerPoC;

    // ========== POC Capacity Information Fields ==========
    private String pocPttServerId;
    private Integer subscriberCount;
    private Integer maxSubsLimit;
    private Integer allowProv;
    private boolean hasCapacityConfig;

    // ========== Constructors ==========

    public KnBulkOpsPocConfigDTO() {
        this.hasCapacityConfig = false;
    }

    // ========== Partition Config Getters/Setters ==========

    public String getPartitionPttServerId() {
        return partitionPttServerId;
    }

    public void setPartitionPttServerId(String partitionPttServerId) {
        this.partitionPttServerId = partitionPttServerId;
    }

    public Integer getMdnPartitionTypePOC() {
        return mdnPartitionTypePOC;
    }

    public void setMdnPartitionTypePOC(Integer mdnPartitionTypePOC) {
        this.mdnPartitionTypePOC = mdnPartitionTypePOC;
    }

    public Integer getEnableCorpAccAnch() {
        return enableCorpAccAnch;
    }

    public void setEnableCorpAccAnch(Integer enableCorpAccAnch) {
        this.enableCorpAccAnch = enableCorpAccAnch;
    }

    public Integer getMaxSubsPerXDMS() {
        return maxSubsPerXDMS;
    }

    public void setMaxSubsPerXDMS(Integer maxSubsPerXDMS) {
        this.maxSubsPerXDMS = maxSubsPerXDMS;
    }

    public Integer getMaxSubsPerPoC() {
        return maxSubsPerPoC;
    }

    public void setMaxSubsPerPoC(Integer maxSubsPerPoC) {
        this.maxSubsPerPoC = maxSubsPerPoC;
    }

    // ========== POC Capacity Getters/Setters ==========

    public String getPocPttServerId() {
        return pocPttServerId;
    }

    public void setPocPttServerId(String pocPttServerId) {
        this.pocPttServerId = pocPttServerId;
    }

    public Integer getSubscriberCount() {
        return subscriberCount != null ? subscriberCount : 0;
    }

    public void setSubscriberCount(Integer subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public Integer getMaxSubsLimit() {
        return maxSubsLimit;
    }

    public void setMaxSubsLimit(Integer maxSubsLimit) {
        this.maxSubsLimit = maxSubsLimit;
    }

    public Integer getAllowProv() {
        return allowProv;
    }

    public void setAllowProv(Integer allowProv) {
        this.allowProv = allowProv;
    }

    public boolean hasCapacityConfig() {
        return hasCapacityConfig;
    }

    public void setHasCapacityConfig(boolean hasCapacityConfig) {
        this.hasCapacityConfig = hasCapacityConfig;
    }

    // ========== Helper Methods ==========

    /**
     * Create partition config only instance
     */
    public static KnBulkOpsPocConfigDTO forPartitionConfig(String pttServerId, int mdnPartitionType,
                                                           int enableCorpAnch, Integer maxSubsXDMS, Integer maxSubsPoC) {
        KnBulkOpsPocConfigDTO dto = new KnBulkOpsPocConfigDTO();
        dto.setPartitionPttServerId(pttServerId);
        dto.setMdnPartitionTypePOC(mdnPartitionType);
        dto.setEnableCorpAccAnch(enableCorpAnch);
        dto.setMaxSubsPerXDMS(maxSubsXDMS);
        dto.setMaxSubsPerPoC(maxSubsPoC);
        return dto;
    }

    /**
     * Create POC capacity only instance
     */
    public static KnBulkOpsPocConfigDTO forPocCapacity(String pttServerId, int subsCount,
                                                        Integer maxLimit, Integer allow, boolean hasConfig) {
        KnBulkOpsPocConfigDTO dto = new KnBulkOpsPocConfigDTO();
        dto.setPocPttServerId(pttServerId);
        dto.setSubscriberCount(subsCount);
        dto.setMaxSubsLimit(maxLimit);
        dto.setAllowProv(allow);
        dto.setHasCapacityConfig(hasConfig);
        return dto;
    }

    @Override
    public String toString() {
        return "KnBulkOpsPocConfigDTO[" +
                "partitionPttServerId=" + partitionPttServerId +
                ", mdnPartitionTypePOC=" + mdnPartitionTypePOC +
                ", enableCorpAccAnch=" + enableCorpAccAnch +
                ", pocPttServerId=" + pocPttServerId +
                ", subscriberCount=" + subscriberCount +
                ", maxSubsLimit=" + maxSubsLimit +
                ", allowProv=" + allowProv +
                ", hasCapacityConfig=" + hasCapacityConfig +
                ']';
    }

    /**
     * Inner class to store MDN prefix to POC server mapping
     * Used for MDN_BASED partitioning prefix matching
     */
    public static class PrefixMapping {
        public final String prefix;
        public final String pttServerId;

        public PrefixMapping(String prefix, String pttServerId) {
            this.prefix = prefix;
            this.pttServerId = pttServerId;
        }
    }

    /**
     * Inner class to track server capacity information for allocation algorithms
     * Used in load-based greedy capacity-filling algorithm
     */
    public static class ServerCapacityInfo {
        public final String serverId;
        public final int availableCapacity;
        public final int currentCount;
        public final int maxLimit;

        public ServerCapacityInfo(String serverId, int availableCapacity, int currentCount, int maxLimit) {
            this.serverId = serverId;
            this.availableCapacity = availableCapacity;
            this.currentCount = currentCount;
            this.maxLimit = maxLimit;
        }

        @Override
        public String toString() {
            return "ServerCapacity{id='" + serverId + "', available=" + availableCapacity +
                   ", current=" + currentCount + ", max=" + maxLimit + "}";
        }
    }
}

