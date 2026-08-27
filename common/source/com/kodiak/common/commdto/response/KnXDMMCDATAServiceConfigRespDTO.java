/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMMCDATAServiceConfigRespDTO.java
 * Subsystem:  XDMS
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Shashank Tewari      12/07/2019    9.1
 * <p/>
 * <p/>
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnXDMMCDATAServiceConfigRespDTO extends KnXDMRespDTO{
            private String service_configuration_domain;
            private String common_tx_and_rx_control_time_temp_data_waiting;
            private String common_tx_and_rx_control_time_periodic_announcement;
            private String onnetwork_tx_and_rx_control_max_datasize_sds_bytes;
            private String onnetwork_tx_and_rx_control_max_payload_size_sds_cplane_bytes;
            private String onnetwork_tx_and_rx_control_max_data_size_fd_bytes;
            private String onnetwork_tx_and_rx_control_max_data_size_auto_recv_bytes;
            private String onnetwork_signalling_protection_confidentiality_protection;
            private String onnetwork_signalling_protection_integrity_protection;
            private String onnetwork_file_availability_default_file_availability;
            private String onnetwork_file_max_file_availability;

            public String getService_configuration_domain() {
                return service_configuration_domain;
            }

            public void setService_configuration_domain(String service_configuration_domain) {
                this.service_configuration_domain = service_configuration_domain;
            }

            public String getCommon_tx_and_rx_control_time_temp_data_waiting() {
                return common_tx_and_rx_control_time_temp_data_waiting;
            }

            public void setCommon_tx_and_rx_control_time_temp_data_waiting(String common_tx_and_rx_control_time_temp_data_waiting) {
                this.common_tx_and_rx_control_time_temp_data_waiting = common_tx_and_rx_control_time_temp_data_waiting;
            }

            public String getCommon_tx_and_rx_control_time_periodic_announcement() {
                return common_tx_and_rx_control_time_periodic_announcement;
            }

            public void setCommon_tx_and_rx_control_time_periodic_announcement(String common_tx_and_rx_control_time_periodic_announcement) {
                this.common_tx_and_rx_control_time_periodic_announcement = common_tx_and_rx_control_time_periodic_announcement;
            }

            public String getOnnetwork_tx_and_rx_control_max_datasize_sds_bytes() {
                return onnetwork_tx_and_rx_control_max_datasize_sds_bytes;
            }

            public void setOnnetwork_tx_and_rx_control_max_datasize_sds_bytes(String onnetwork_tx_and_rx_control_max_datasize_sds_bytes) {
                this.onnetwork_tx_and_rx_control_max_datasize_sds_bytes = onnetwork_tx_and_rx_control_max_datasize_sds_bytes;
            }

            public String getOnnetwork_tx_and_rx_control_max_payload_size_sds_cplane_bytes() {
                return onnetwork_tx_and_rx_control_max_payload_size_sds_cplane_bytes;
            }

            public void setOnnetwork_tx_and_rx_control_max_payload_size_sds_cplane_bytes(String onnetwork_tx_and_rx_control_max_payload_size_sds_cplane_bytes) {
                this.onnetwork_tx_and_rx_control_max_payload_size_sds_cplane_bytes = onnetwork_tx_and_rx_control_max_payload_size_sds_cplane_bytes;
            }

            public String getOnnetwork_tx_and_rx_control_max_data_size_fd_bytes() {
                return onnetwork_tx_and_rx_control_max_data_size_fd_bytes;
            }

            public void setOnnetwork_tx_and_rx_control_max_data_size_fd_bytes(String onnetwork_tx_and_rx_control_max_data_size_fd_bytes) {
                this.onnetwork_tx_and_rx_control_max_data_size_fd_bytes = onnetwork_tx_and_rx_control_max_data_size_fd_bytes;
            }

            public String getOnnetwork_tx_and_rx_control_max_data_size_auto_recv_bytes() {
                return onnetwork_tx_and_rx_control_max_data_size_auto_recv_bytes;
            }

            public void setOnnetwork_tx_and_rx_control_max_data_size_auto_recv_bytes(String onnetwork_tx_and_rx_control_max_data_size_auto_recv_bytes) {
                this.onnetwork_tx_and_rx_control_max_data_size_auto_recv_bytes = onnetwork_tx_and_rx_control_max_data_size_auto_recv_bytes;
            }

            public String getOnnetwork_signalling_protection_confidentiality_protection() {
                return onnetwork_signalling_protection_confidentiality_protection;
            }

            public void setOnnetwork_signalling_protection_confidentiality_protection(String onnetwork_signalling_protection_confidentiality_protection) {
                this.onnetwork_signalling_protection_confidentiality_protection = onnetwork_signalling_protection_confidentiality_protection;
            }

            public String getOnnetwork_signalling_protection_integrity_protection() {
                return onnetwork_signalling_protection_integrity_protection;
            }

            public void setOnnetwork_signalling_protection_integrity_protection(String onnetwork_signalling_protection_integrity_protection) {
                this.onnetwork_signalling_protection_integrity_protection = onnetwork_signalling_protection_integrity_protection;
            }

            public String getOnnetwork_file_availability_default_file_availability() {
                return onnetwork_file_availability_default_file_availability;
            }

            public void setOnnetwork_file_availability_default_file_availability(String onnetwork_file_availability_default_file_availability) {
                this.onnetwork_file_availability_default_file_availability = onnetwork_file_availability_default_file_availability;
            }

            public String getOnnetwork_file_max_file_availability() {
                return onnetwork_file_max_file_availability;
            }

            public void setOnnetwork_file_max_file_availability(String onnetwork_file_max_file_availability) {
                this.onnetwork_file_max_file_availability = onnetwork_file_max_file_availability;
            }

    @Override
    public String toString() {
        return "KnXDMMCDATAServiceConfigRespDTO{" +
                "service_configuration_domain='" + service_configuration_domain + '\'' +
                ", common_tx_and_rx_control_time_temp_data_waiting='" + common_tx_and_rx_control_time_temp_data_waiting + '\'' +
                ", common_tx_and_rx_control_time_periodic_announcement='" + common_tx_and_rx_control_time_periodic_announcement + '\'' +
                ", onnetwork_tx_and_rx_control_max_datasize_sds_bytes='" + onnetwork_tx_and_rx_control_max_datasize_sds_bytes + '\'' +
                ", onnetwork_tx_and_rx_control_max_payload_size_sds_cplane_bytes='" + onnetwork_tx_and_rx_control_max_payload_size_sds_cplane_bytes + '\'' +
                ", onnetwork_tx_and_rx_control_max_data_size_fd_bytes='" + onnetwork_tx_and_rx_control_max_data_size_fd_bytes + '\'' +
                ", onnetwork_tx_and_rx_control_max_data_size_auto_recv_bytes='" + onnetwork_tx_and_rx_control_max_data_size_auto_recv_bytes + '\'' +
                ", onnetwork_signalling_protection_confidentiality_protection='" + onnetwork_signalling_protection_confidentiality_protection + '\'' +
                ", onnetwork_signalling_protection_integrity_protection='" + onnetwork_signalling_protection_integrity_protection + '\'' +
                ", onnetwork_file_availability_default_file_availability='" + onnetwork_file_availability_default_file_availability + '\'' +
                ", onnetwork_file_max_file_availability='" + onnetwork_file_max_file_availability + '\'' +
                '}';
    }
}
