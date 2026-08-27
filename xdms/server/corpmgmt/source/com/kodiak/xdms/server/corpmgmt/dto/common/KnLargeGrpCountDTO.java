/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

public class KnLargeGrpCountDTO {

    private int lrgGrpCountSystem;
    private int lrgGrpCountCorp;
    private int largeBCGrpCountCorp;


    public int getLrgGrpCountSystem() {
        return lrgGrpCountSystem;
    }

    public void setLrgGrpCountSystem(int lrgGrpCountSystem) {
        this.lrgGrpCountSystem = lrgGrpCountSystem;
    }

    public int getLrgGrpCountCorp() {
        return lrgGrpCountCorp;
    }

    public void setLrgGrpCountCorp(int lrgGrpCountCorp) {
        this.lrgGrpCountCorp = lrgGrpCountCorp;
    }

    public int getLargeBCGrpCountCorp() {
        return largeBCGrpCountCorp;
    }

    public void setLargeBCGrpCountCorp(int largeBCGrpCountCorp) {
        this.largeBCGrpCountCorp = largeBCGrpCountCorp;
    }

    @Override
    public String toString() {
        return "KnLargeGrpCountDTO{" +
                "lrgGrpCountSystem=" + lrgGrpCountSystem +
                ", lrgGrpCountCorp=" + lrgGrpCountCorp +
                ", largeBCGrpCountCorp=" + largeBCGrpCountCorp +
                '}';
    }
}
