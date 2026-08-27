/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.resources;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: kodiak
 * Date: 21/12/13
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnPublicUtil {

    private static final String COMMA = ",";
    private static final String SINGLE_QUOTE = "'";


    public static String getComSepList(Collection<String> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        for (String mdn : collectionStr) {
            buffer = buffer.append(SINGLE_QUOTE).append(mdn).append(SINGLE_QUOTE).append(COMMA);
        }
        return buffer.substring(0, buffer.length() - 1);
    }


    public static String getIntComSepList(Collection<Integer> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        for (int id : collectionStr) {
            buffer = buffer.append(id).append(COMMA);
        }
        return buffer.substring(0, buffer.length() - 1);
    }

}
