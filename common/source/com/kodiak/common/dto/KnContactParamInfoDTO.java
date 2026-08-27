/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */
package com.kodiak.common.dto;

public class KnContactParamInfoDTO {

    public KnContactParamInfoDTO() {
    }
    public KnContactParamInfoDTO(String mdn, String name, int activefs, String tag) {
        Mdn = mdn;
        this.name = name;
        this.activefs = activefs;
        this.tag = tag;
    }

    private String Mdn;
        private String name;
        private int activefs;
        private String tag;

        // Getters and setters
        public String getMdn() {
            return Mdn;
        }

        public void setMdn(String mdn) {
            Mdn = mdn;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getActivefs() {
            return activefs;
        }

        public void setActivefs(int activefs) {
            this.activefs = activefs;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

}
