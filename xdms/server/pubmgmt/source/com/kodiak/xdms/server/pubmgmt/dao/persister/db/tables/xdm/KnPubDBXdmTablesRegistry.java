/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.xdms.server.common.dao.persister.db.tables.xdm.KnXDMSubscriberInfoDAO;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubDBXdmTablesRegistry.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 10, 2011           7.0
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
public final class KnPubDBXdmTablesRegistry {

    public KnDirectoryDAO getDirectoryDAO(String pttServerId) {
        return new KnDirectoryDAO(pttServerId);
    }

    public KnContactListDocMapDAO getContactListDocMapDAO(String pttServerId) { return new KnContactListDocMapDAO(pttServerId); }

    public KnContactListDAO getContactListDAO(String pttServerId) { return new KnContactListDAO(pttServerId); }

    public KnContactListMemberDAO getContactListMemberDAO(String pttServerId) { return new KnContactListMemberDAO(pttServerId); }

    public KnPOCContactListDAO getPOCContactListDAO(String pttServerId) { return new KnPOCContactListDAO(pttServerId); }

    public KnXDMSubscriberInfoDAO getSubscriberInfoDAO(String pttServerId) { return new KnXDMSubscriberInfoDAO(pttServerId); }

    public KnOMAGroupDocDAO getOmaGroupDocDAO(String pttServerId) { return new KnOMAGroupDocDAO(pttServerId); }

    public KnPOCGroupDAO getPocGroupDAO(String pttServerId) { return new KnPOCGroupDAO(pttServerId); }

    public KnPOCGroupDocMapDAO getPocGroupDocMapDAO(String pttServerId) { return new KnPOCGroupDocMapDAO(pttServerId); }

    public KnPOCGroupMemberDAO getPocGroupMemberDAO(String pttServerId) { return new KnPOCGroupMemberDAO(pttServerId); }

    public KnPubContactCountDAO getPubContactCountDAO(String pttServerId) { return new KnPubContactCountDAO(pttServerId); }

    public KnPOCSubscriberDAO getPOCSubscriberDAO(String pttServerId) { return new KnPOCSubscriberDAO(pttServerId); }

    public KnCorporateInfoDAO getCorporateInfoDAO(String pttServerId) { return new KnCorporateInfoDAO(pttServerId); }

    public KnTPAccountInfoDAO getTPAccountInfoDAO(String pttServerId) { return new KnTPAccountInfoDAO(pttServerId); }

    public KnTPUserMdnMapDAO getTPUserMdnMapDAO(String pttServerId) { return new KnTPUserMdnMapDAO(pttServerId); }

    public KnExtCorpContactDAO getExtCorpContactDAO(String pttServerId) {
        return new KnExtCorpContactDAO(pttServerId);
    }

    public KnAuthorizationDocDAO getAuthorizationDocDAO(String pttServerId){ return new KnAuthorizationDocDAO(pttServerId);}

    public KnMCPTTPermInfoDAO getMCPTTPermDAO(String pttServerId){ return new KnMCPTTPermInfoDAO(pttServerId);}

    public KnEmergencyDocDAO getEmergencyDocDAO(String pttServerId){ return new KnEmergencyDocDAO(pttServerId);}

    public KnPocSubscrAddInfoDAO getPocSubscrAddInfoDAO(String pttServerId){ return new KnPocSubscrAddInfoDAO(pttServerId);}

    public KnEmergencySubscrDestinfoDAO getEmergencySubscrDestinfoDAO(String pttServerId){ return new KnEmergencySubscrDestinfoDAO(pttServerId);}

    public KnSubscrPttRadioGroupListDAO getSubscrPttRadioGroupListDAO(String pttServerId){ return new KnSubscrPttRadioGroupListDAO(pttServerId);}

    public KnSubscrPTTRadioTGListDAO getSubscrPTTRadioTGList(String pttServerId){ return new KnSubscrPTTRadioTGListDAO(pttServerId);}

    public KnTGSSDocDAO getTGSSDocDAO(String pttServerId){ return new KnTGSSDocDAO(pttServerId); }

    public KnSSChannelGroupInfoDAO getSSChannelGroupInfoDAO(String pttServerId){ return new KnSSChannelGroupInfoDAO(pttServerId);}

    public KnPOCSvcConfigDAO getPOCSvcConfigDAO(String pttServerId){ return  new KnPOCSvcConfigDAO(pttServerId);}

    public KnPOCSvcConfigDAO createPOCSvcConfigDAO(String pttServerId) {
        return new KnPOCSvcConfigDAO(pttServerId);
    }

    public KnCorpGroupMemberCountDAO createCorpGrpMemCountDAO(String pttServerId) {
        return new KnCorpGroupMemberCountDAO(pttServerId);
    }
}
