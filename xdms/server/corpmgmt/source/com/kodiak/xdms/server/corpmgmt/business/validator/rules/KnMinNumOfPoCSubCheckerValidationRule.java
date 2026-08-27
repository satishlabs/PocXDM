/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

/**
 * Created by IntelliJ IDEA.
 * User: kodiak
 * Date: 28/5/13
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGenActivationPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnMinNumOfPoCSubCheckerValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnMinNumOfPoCSubCheckerValidationRule.class);
    private static final long serialVersionUID = 7526471155622676328L;
    private final String CLASS = KnMinNumOfPoCSubCheckerValidationRule.class.getName();

    /**
     * This method implements the actual logic for validation
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        IPersistenceDTO persistDTO = getDTO();
        String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY : Validating if total members exceeds thresh count exceeds limit");
        KnCorpGenActivationPersistDTO corpActPersistDTO;
        if (persistDTO instanceof KnCorpGenActivationPersistDTO) {
            corpActPersistDTO = (KnCorpGenActivationPersistDTO) persistDTO;
            if (corpActPersistDTO.getSubsCount() == corpActPersistDTO.getExtMdnList().size()) {
            	Collection<KnCorpSubscriberDTO> subscriberList= corpActPersistDTO.getExtMdnList();
            	List<String> otherCorpMdnList=new ArrayList<String>();
            	for(KnCorpSubscriberDTO knCorpSubscriberDTO : subscriberList){
            		otherCorpMdnList.add(knCorpSubscriberDTO.getMdn());
            	}
            	knLogger.error("Atleast one subscriber should be part of the corporation. ",otherCorpMdnList);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.ATLEAST_ONE_SHOULD_BE_PART_OF_CORP,
                        "Atleast one subscriber should be part of the corporation.", getEntityId(), getOperationType(), getRuleId(),
                        otherCorpMdnList.toString(), "");
            }
        } else {
            knLogger.error( "validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGenActivationPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug( "validate", "KnMinNumOfPoCSubCheckerValidationRule Validated successfully.");

    }
}
