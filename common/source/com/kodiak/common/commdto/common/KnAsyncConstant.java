/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnAsyncConstant.java
 * Subsystem:  Messaging Framework
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      22-Dec-2008  6.3
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.common.commdto.common;


public class KnAsyncConstant {

    public static final String BEAN_MAP_KEY = "beanMapKey";
    public static final String CLASS_MAP_KEY = "classMapKey";
    public static final String AGENT_DEFAULT = "defaultMsgAgent";
    public static final String TIMEOUT = "timeout";

    public static enum MESSAGE_TYPE {
        //the message (Bytes(0)/TLV(1)/Text(2)/Stream(3)/Map(4)/Object(5))
        BYTES(0), TLV(1), TEXT(2),
        STREAM(3), MAP(4), OBJECT(5), DB(6);
        private int msgType;

        MESSAGE_TYPE(int msgType) {
            this.msgType = msgType;
        }

        public int value() {
            return msgType;
        }
    }

    public static enum DELIVERY_MODE {
        NONPERSISTENT(1), PERSISTENT(2);
        private int persistent;

        DELIVERY_MODE(int persistent) {
            this.persistent = persistent;
        }

        public int value() {
            return persistent;
        }
    }

    public static enum RESULT_STATUS {
        SUCCESS(0), INPROGRESS(1), DROPPED(2);
        private int resStatus;

        RESULT_STATUS(int resStatus) {
            this.resStatus = resStatus;
        }

        public int value() {
            return resStatus;
        }
    }
}
