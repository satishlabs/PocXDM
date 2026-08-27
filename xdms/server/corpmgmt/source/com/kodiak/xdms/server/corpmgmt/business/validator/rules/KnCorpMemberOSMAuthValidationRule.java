/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT;
import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT;
import static com.kodiak.xdms.server.common.resources.KnConstants.IS_OSM_AUTHORIZE_BIT;

public class KnCorpMemberOSMAuthValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpMemberOSMAuthValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO dto=(KnCorpGroupInfoPersistDTO)persistDTO;
            Collection<KnCorpContactDTO> addedMdn = dto.getAddedMemberMdnsDTOLst();
            Collection<KnCorpSubscriberDTO> pocSubs = dto.getPocSubsInfo();
            Collection<KnCorpGroupMemberDTO> modifiedMdns = dto.getModifiedMembersList();
            boolean mcxGroupInd = dto.isMcxGroup();
            knLogger.debug( methodName,"AddedMdnDTO for validation",addedMdn);
            knLogger.debug( methodName,"ModifiedMdnDTO for validation",modifiedMdns);
            knLogger.debug( methodName,"PocSubsInfo from DB. ",pocSubs);
            if(addedMdn!=null&&!addedMdn.isEmpty()&&pocSubs!=null&&!pocSubs.isEmpty()){
                List<String> osmFBDisabledMdn=new ArrayList<>();
                for(KnCorpContactDTO reqMdn:addedMdn){
                    KnCorpSubscriberDTO dbSubsciber = pocSubs.stream().filter(mdn -> mdn.getMdn().equals(reqMdn.getMdn())).collect(Collectors.toList()).get(0);
                    boolean isOSMAuthorizeBit = KnGeneralUtil.getFeatureBitValue(dbSubsciber.getSubsActiveFS2(),IS_OSM_AUTHORIZE_BIT);
                    boolean dispatcher = dbSubsciber.getClientType() == DISPATCH_CLIENT.value() || dbSubsciber.getClientType() == THIRDPARTYDISPATCHERCLIENT.value();
                        if((!dispatcher&&!mcxGroupInd)&&reqMdn.getIsOSMAuthorize()==1&&!isOSMAuthorizeBit){
                            osmFBDisabledMdn.add(dbSubsciber.getMdn());
                        }
                }
                if(osmFBDisabledMdn.size()>0){
                    knLogger.error( methodName, "validation failed for added mdn osm auth flag,As osm bit is not enabled." , osmFBDisabledMdn.toString());
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.OSM_AUTHORIZE_FB_DISABLED,
                            "validation failed for added mdn osm auth flag,As osm bit is not enabled.", getEntityId()
                            , getOperationType(), getRuleId(), osmFBDisabledMdn.toString(), "");
                }
            }
            if(modifiedMdns!=null&&!modifiedMdns.isEmpty()&&pocSubs!=null&&!pocSubs.isEmpty()){
                List<String> osmFBDisabledMdn=new ArrayList<>();
                for(KnCorpGroupMemberDTO reqMdn:modifiedMdns){
                        KnCorpSubscriberDTO dbSubsciber = pocSubs.stream().filter(mdn -> mdn.getMdn().equals(reqMdn.getMdn())).collect(Collectors.toList()).get(0);
                        boolean isOSMAuthorizeBit = KnGeneralUtil.getFeatureBitValue(dbSubsciber.getSubsActiveFS2(),IS_OSM_AUTHORIZE_BIT);
                        boolean dispatcher = dbSubsciber.getClientType() == DISPATCH_CLIENT.value() || dbSubsciber.getClientType() == THIRDPARTYDISPATCHERCLIENT.value();
                        if((!dispatcher&&!mcxGroupInd)&&reqMdn.getIsOSMAuthorize()==1&&!isOSMAuthorizeBit){
                            osmFBDisabledMdn.add(dbSubsciber.getMdn());
                        }
                }
                if(osmFBDisabledMdn.size()>0){
                    knLogger.error( methodName, "validation failed for added mdn osm auth flag,As osm bit is not enabled." , osmFBDisabledMdn.toString());
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.OSM_AUTHORIZE_FB_DISABLED,
                            "validation failed for added mdn osm auth flag,As osm bit is not enabled.", getEntityId()
                            , getOperationType(), getRuleId(), osmFBDisabledMdn.toString(), "");
                }
            }

        }else {
            knLogger.error( "validate()", "Unexpected DTO passed - " , persistDTO.getClass() ,
                    ", Expected Dto - KnCorpOSMPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }
}
