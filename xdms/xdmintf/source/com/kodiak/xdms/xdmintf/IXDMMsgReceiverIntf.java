/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.xdmintf;

import com.kodiak.frameworks.messaging.common.KnMessageException;

import java.util.Collection;

public interface IXDMMsgReceiverIntf {

    public boolean initialize();

    public void registerForCallbacks(Collection<Integer> features) throws KnMessageException;

}
