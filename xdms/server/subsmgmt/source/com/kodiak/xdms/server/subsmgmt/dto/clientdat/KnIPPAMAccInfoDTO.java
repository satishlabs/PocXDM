/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.clientdat;


import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnPAMAccInfoDTO;


/**
 * Created by IntelliJ IDEA.
 * User: kodiak
 * Date: 9/2/13
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnIPPAMAccInfoDTO extends KnPAMAccInfoDTO implements IInputDTO {
    private static final long serialVersionUID = 7526471155622678005L;

      private String performer;
      private IAuthDTO authDTO;
      private int clientType;
      private String operationType;
      private String entityId;

      private String profile;
      private String method;

      /**
       * setter method for the Performer
       *
       * @param performer sets performer to subscriber
       */
      public void setPerformer(String performer) {
          if (performer != null) {
              performer = performer.trim();
              if (performer.equals("")) {
                  performer = null;
              }
          }
          this.performer = performer;
      }

      /**
       * getter method for Performer
       *
       * @return String
       */
      public String getPerformer() {
          return performer;
      }

      /**
       * getter method for the Auth DTO
       *
       * @return IAuthDTO
       */
      public IAuthDTO getAuthDTO() {
          return authDTO;
      }

      /**
       * setter method for the AuthDTO
       *
       * @param authDTO IAuth DTO
       */
      public void setAuthDTO(IAuthDTO authDTO) {
          this.authDTO = authDTO;
      }

      /**
       * setter method for the clientType
       *
       * @param clientType int
       */
      public void setClientType(int clientType) {
          this.clientType = clientType;
      }

      /**
       * getter method for the client Type
       *
       * @return int
       */
      public int getClientType() {
          return clientType;
      }

      /**
       * setter method for the Operation Type
       *
       * @param operationType the operation type
       */
      public void setOperationType(String operationType) {
          if (operationType != null) {
              operationType = operationType.trim();
              if (operationType.equals("")) {
                  operationType = null;
              }
          }
          this.operationType = operationType;
      }

      /**
       * getter method for operation Type
       *
       * @return String
       */
      public String getOperationType() {
          return operationType;
      }

      /**
       * getter method for the Entity Id
       *
       * @return String
       */
      public String getEntityId() {
          return entityId;
      }

      /**
       * setter method for the entity Id
       *
       * @param entityId String
       */
      public void setEntityId(String entityId) {
          if (entityId != null) {
              entityId = entityId.trim();
              if (entityId.equals("")) {
                  entityId = null;
              }
          }
          this.entityId = entityId;
      }

      public String getObjectId() {
          return super.getExtPamAccId();
      }

      public String getMethod() {
          return method;
      }

      public void setMethod(String method) {
          this.method = method;
      }

      public String toString() {
          StringBuffer strBuffer = new StringBuffer();
          strBuffer.append(super.toString());
          strBuffer.append("[KnIPSubsProvInfoDTO --> ");
          strBuffer.append(", Operation_TYpe - ").append(operationType)
                  .append(", Client_Type - ").append(clientType)
                  .append(", Entity_ID").append(entityId)
                 .append(", Method -").append(method)
                  .append("]");

          return strBuffer.toString();
      }

      public String getProfile() {
          return profile;
      }

      public void setProfile(String profile) {
          this.profile = profile;
      }

    //  public void validate() throws KnValidationException {
          //To change body of implemented methods use File | Settings | File Templates.
    //  }
  }

