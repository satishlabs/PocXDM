/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/************************************************************************
 * File name:   IEntity.java
 * Subsystem:   COMMON DTO
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       Dec 22, 2010   7.0
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
 ***************************************************************************/
package com.kodiak.xdms.server.common.dto.intf;

import com.kodiak.common.dto.IIdentifier;

/**
* Defines the contract which identifies entities
* like input dto, persistdto, validator etc...
*/
public interface IEntity extends IProfile, IIdentifier {

   /**
    * Set the operation type to be executed.
    *
    * @param operationType the operation type
    */
   void setOperationType(String operationType);

   /**
    * Return the operation type set in the dto
    *
    * @return the operation type
    */
   String getOperationType();

   /**
    * Returns the ID of the entity.
    *
    * @return the entity id
    */
   String getEntityId();

   /**
    * Sets the ID of the entity.
    *
    * @param entityId String
    */
   void setEntityId(String entityId);
}
