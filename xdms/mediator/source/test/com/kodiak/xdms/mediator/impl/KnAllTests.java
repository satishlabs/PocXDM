/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package test.com.kodiak.xdms.mediator.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm.KnPOCSubscrInfoDAOTest;

@RunWith(Suite.class)
@SuiteClasses({ KnXDMMediatorTest.class,  KnPOCSubscrInfoDAOTest.class})
public class KnAllTests {

}
