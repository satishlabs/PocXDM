/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl.hook;

import com.kodiak.xdms.server.corpmgmt.dto.ICorpHookIPDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnActions;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12/7/11
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class KnCorpHookIPDTO implements ICorpHookIPDTO {

    private Object data;

    private KnActions.ACTIONS action;


    public KnActions.ACTIONS getAction() {
        return action;
    }

    public void setAction(KnActions.ACTIONS action) {
        this.action = action;
    }

    public void setData(Object object) {
        data =  object;
    }

    public Object getData() {
        return data;
    }

}
