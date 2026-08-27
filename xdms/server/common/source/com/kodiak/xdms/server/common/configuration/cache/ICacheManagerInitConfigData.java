/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   ICacheManagerInitConfigData.java
 * Subsystem:   Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         14-03-2007 6.0
 *
 *
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 **************************************************************************/
package com.kodiak.xdms.server.common.configuration.cache;

import com.kodiak.xdms.server.common.configuration.IConfigData;


/**
 * This interface defines the basic methods which all the cache manager
 * configuration data classes should implement.he implementation specific
 * configuration data for cache manager will be done in initialization
 * xml file as given below
 *
 * <manager-config-data>
 *    loader specific tags
 * </manager-config-data>
 *
 * eg:
 * <manager-config-data>
 *    <key-generator-config>
 *      <delimiter>.</delimiter>
 *    </key-generator>
 * </manager-config-data>
 */
public interface ICacheManagerInitConfigData extends IConfigData {

}
