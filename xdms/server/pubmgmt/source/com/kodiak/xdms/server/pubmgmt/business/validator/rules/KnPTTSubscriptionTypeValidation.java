/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;

import static com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes.Validator.OPERATION_NOT_ALLOWED;

import java.util.BitSet;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubContactInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnConstants;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

public class KnPTTSubscriptionTypeValidation extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPTTSubscriptionTypeValidation.class);

	@Override
	public void validate() throws KnValidationException {
		final String methodName = "validate()";
		IPersistenceDTO persistDTO = getDTO();
		knLogger.debug("validate",
				"ENTRY : Validating whether PTT user is allowed to create/manage its own contacts or Groups. >"
						+ persistDTO);
		try {
			if (persistDTO instanceof KnPubContactInfoPersistDTO) {
				KnPubContactInfoPersistDTO knPubContactInfoPersistDTO = (KnPubContactInfoPersistDTO) persistDTO;
				String acrtepc = knPubContactInfoPersistDTO.getAcrtepc();
				knLogger.debug("allowPublicContactManagement :", acrtepc);
				KnSubscriberPersistDTO knSubscriberPersistDTO = (KnSubscriberPersistDTO) knPubContactInfoPersistDTO
						.getPersistenceDTO();
				if (acrtepc != null) {
					boolean allowPublicContactManagement = false;
					if (knSubscriberPersistDTO.getPubSubscriptionType() == KnConstants.ONE
							&& knSubscriberPersistDTO.getCorpSubscriptionType() == KnConstants.ZERO) {
						BitSet bitSet = BitSet.valueOf(acrtepc.getBytes());
						if (bitSet.get(KnConstants.ZERO)) {
							allowPublicContactManagement = true;
						}
					} else if (knSubscriberPersistDTO.getPubSubscriptionType() == KnConstants.ZERO
							&& knSubscriberPersistDTO.getCorpSubscriptionType() == KnConstants.ONE) {
						BitSet bitSet = BitSet.valueOf(acrtepc.getBytes());
						if (bitSet.get(KnConstants.ONE)) {
							allowPublicContactManagement = true;
						}
					} else if (knSubscriberPersistDTO.getPubSubscriptionType() == KnConstants.ONE
							&& knSubscriberPersistDTO.getCorpSubscriptionType() == KnConstants.ONE) {
						BitSet bitSet = BitSet.valueOf(acrtepc.getBytes());
						if (bitSet.get(KnConstants.TWO)) {
							allowPublicContactManagement = true;
						}
					}

					if (!allowPublicContactManagement) {
						knLogger.error(methodName, "PTT user is not allowed to create/manage its own contacts");
						throw new KnPubBOValidationException(OPERATION_NOT_ALLOWED,
								"PTT user is not allowed to create/manage its own contacts - " + persistDTO.getClass(),
								getEntityId(), getOperationType(), getRuleId(), "MDN", "");
					}
				}
			} else if (persistDTO instanceof KnPubGroupInfoPersistDTO) {
				KnPubGroupInfoPersistDTO knPubGroupInfoPersistDTO = (KnPubGroupInfoPersistDTO) persistDTO;
				String acrtepg = knPubGroupInfoPersistDTO.getAcrtepg();
				knLogger.debug("allowPublicGroupManagement :", acrtepg);
				KnSubscriberPersistDTO knSubscriberPersistDTO = (KnSubscriberPersistDTO) knPubGroupInfoPersistDTO
						.getPersistenceDTO();
				if (acrtepg != null) {
					boolean allowPublicGroupManagement = false;
					if (knSubscriberPersistDTO.getPubSubscriptionType() == KnConstants.ONE
							&& knSubscriberPersistDTO.getCorpSubscriptionType() == KnConstants.ZERO) {
						BitSet bitSet = BitSet.valueOf(acrtepg.getBytes());
						if (bitSet.get(KnConstants.ZERO)) {
							allowPublicGroupManagement = true;
						}
					} else if (knSubscriberPersistDTO.getPubSubscriptionType() == KnConstants.ZERO
							&& knSubscriberPersistDTO.getCorpSubscriptionType() == KnConstants.ONE) {
						BitSet bitSet = BitSet.valueOf(acrtepg.getBytes());
						if (bitSet.get(KnConstants.ONE)) {
							allowPublicGroupManagement = true;
						}
					} else if (knSubscriberPersistDTO.getPubSubscriptionType() == KnConstants.ONE
							&& knSubscriberPersistDTO.getCorpSubscriptionType() == KnConstants.ONE) {
						BitSet bitSet = BitSet.valueOf(acrtepg.getBytes());
						if (bitSet.get(KnConstants.TWO)) {
							allowPublicGroupManagement = true;
						}
					}

					if (!allowPublicGroupManagement) {
						knLogger.error(methodName, "PTT user is not allowed to create/manage its own groups");
						throw new KnPubBOValidationException(OPERATION_NOT_ALLOWED,
								"PTT user is not allowed to create/manage its own groups - " + persistDTO.getClass(),
								getEntityId(), getOperationType(), getRuleId(), "MDN", "");
					}
				}
			} else {
				knLogger.error(methodName, "Unexpected DTO passed - ", persistDTO.getClass(),
						", Expected Dto - KnPubContactInfoPersistDTO || KnPubGroupInfoPersistDTO");
				throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
						"Unexpected instance of persistence DTO passed - " + persistDTO.getClass(), getEntityId(),
						getOperationType(), getRuleId(), "MDN", "");
			}
		} finally {
			knLogger.debug(methodName, "Exit Point");
		}
	}

}
