/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnXDMFacadeImpl.java
 * Subsystem:   XDM Facade Layer
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       1/8/11   7.0
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
 * *************************************************************************
 */
package com.kodiak.xdms.xdmintf.impl;

import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.commdto.response.*;
import com.kodiak.common.resources.KnErrorCodes;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.processinvoker.IProcessInvokerIntf;
import com.kodiak.utilities.processinvoker.KnProcessInvokerException;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.xdms.mediator.IXDMMediatorIntf;
import com.kodiak.xdms.mediator.impl.KnXDMMediator;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.xdmintf.IXDMFacadeIntf;
import com.kodiak.xdms.xdmintf.resources.KnXDMIntfConstants;

/**
 * Facade Layer Impl class for the all the API's provided by the XDM Server
 * The request is received from the call back mechanism of the Messaging FW.
 * After receiving the request it is sent to the mediation layer for
 * further processing of the Operation.
 */
public class KnXDMFacadeImpl implements IXDMFacadeIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMFacadeImpl.class);

    private IXDMMediatorIntf xdmMediator;
    private IProcessInvokerIntf processInvokerIntf;


    public KnXDMFacadeImpl() {
        xdmMediator = KnXDMMediator.getInstance();
        try {
            processInvokerIntf = KnProcessInvokerImpl.getInstance();
            knLogger.debug("Constructor - ", "processInvokerIntf - ", processInvokerIntf);
        } catch (KnProcessInvokerException e) {
            knLogger.warn("KnXDMFacadeImpl()", "Failed to get the Process Invoker instance");
        }
    }

    /**
     * method to create the subscriber profile
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO createSubscriber(IXDMRequestDTO requestDTO) {
        String methodName = "createSubscriber(KnMessage)";
        knLogger.debug(methodName, "create Subscriber with Object - ", requestDTO);
        //sending the information to the Mediation layer;
        return xdmMediator.createSubscriber(requestDTO);
    }

    /**
     * method to perform the updation of the subscriber Profile
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateSubscriber(KnMessage message) {
        String methodName = "updateSubscriber(KnMessage)";
        knLogger.debug(methodName, "update Subscriber with msg obj - ", message);
        //sending the information to the mediation Layer
        return xdmMediator.updateSubscriber(message);
    }

    /**
     * Method to retrieve the subscriber Profile Information
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getSubscriberDetails(IXDMRequestDTO requestDTO) {
        String methodName = "getSubscriberDetails(KnMessage)";
        knLogger.debug(methodName, "Get Subscriber Details with msg obj - ", requestDTO);
        //sending the information to the Mediation layer
        return xdmMediator.getSubscriberDetails(requestDTO);
    }

    /**
     * method to perform the deletion of the subscriber
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO deleteSubscriber(KnMessage message) {
        String methodName = "deleteSubscriber(KnMessage)";
        return xdmMediator.deleteSubscriber(message);

    }

    /**
     * method to perform the re-activation and the deactivation of the Subscriber
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO changeServiceAuthStatus(KnMessage message) {
        String methodName = "changeServiceAuthStatus(KnMessage)";
        knLogger.debug(methodName, "change Service Auth Status - ");
        return xdmMediator.changeServiceAuthStatus(message);
    }

    /**
     * method to perform the activation of the Subscriber
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO activateSubscriber(KnMessage message) {
        String methodName = "activateSubscriber(KnMessage)";
        knLogger.debug(methodName, "Activate subscriber ");
        return xdmMediator.activateSubscriber(message);
    }

    /**
     * method to retrieve the Subscriber Configuration Document
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getSubscriberConfigDocument(KnMessage message) {
        String methodName = "getSubscriberConfigDocument(KnMessage)";
        knLogger.debug(methodName, "Retrieve Subscriber Config Document");
        return xdmMediator.getSubscriberConfigDocument(message);
    }

    /**
     * method to perform the Change MDN Operation
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO changeMdn(KnMessage message) {
        String methodName = "changeMdn(KnMessage)";
        knLogger.debug(methodName, "Change MDN ");
        return xdmMediator.changeMdn(message);
    }

    /**
     * method to perform the Force Sync Operation
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO forceSync(IXDMRequestDTO requestDTO) {
        String methodName = "forceSync(IXDMRequestDTO, KnMessage)";
        knLogger.debug(methodName, "force Sync request ");
        return xdmMediator.forceSync(requestDTO);
    }

    /**
     * method to perform the Udpate of Subscriber name
     * <p/>
     * IXDMRequestDTO requestDTO
     *
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateSubscriberName(KnMessage message) {
        String methodName = "updateSubscriberName(KnMessage)";
        knLogger.debug(methodName, "update Subscriber Name ");
        return xdmMediator.updateSubscriberName(message);
    }

    /**
     * method to perform the Update of Camped Group
     * <p/>
     * IXDMRequestDTO requestDTO
     *
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateCampedGroup(KnMessage message) {
        String methodName = "updateCampedGroup(KnMessage)";
        knLogger.debug(methodName, "update updateCampedGroup  ");
        return xdmMediator.updateCampedGroup(message);
    }

    /**
     * method to retrieve default Subscriber Profile
     * IXDMRequestDTO requestDTO
     *
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getDefaultSubscriberProfile(IXDMRequestDTO requestDTO) {
        String methodName = "defaultSubscriberProfile(KnMessage)";
        knLogger.debug(methodName, "Default Subscriber Profile ");
        return xdmMediator.getDefaultSubscriberProfile(requestDTO);
    }

    /**
     * method to invoke custom Prov operations
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO
     */
    public IXDMResponseDTO customProvOp(IXDMRequestDTO requestDTO) {
        String methodName = "customProvOp(IXDMRequestDTO)";
        knLogger.debug(methodName, "customProvOperation - ");
        IXDMResponseDTO responseDTO = new KnXDMRespDTO();
        try {
            Object resp = processInvokerIntf.invokeHook(KnXDMIntfConstants.HOOK_NAME, requestDTO);
            responseDTO = (IXDMResponseDTO) resp;
        } catch (KnProcessInvokerException e) {
            knLogger.error(methodName, "Failed to invoke the XDMCustomMediator ");
            knLogger.error(methodName, e);
            responseDTO.setResponseStatus(1);
            responseDTO.setResponseCode(e.getErrorCode());
            responseDTO.setResponseMessage(e.getErrorMessage());
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred ");
            knLogger.error(methodName, e);
            responseDTO.setResponseStatus(1);
            responseDTO.setResponseCode(KnErrorCodes.Initializer.INTERNAL_ERROR);
            responseDTO.setResponseMessage(e.getMessage());
        }
        return responseDTO;
    }

    /**
     * method to perform the Udpate of client feature set
     * <p/>
     * IXDMRequestDTO requestDTO
     *
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateClientFS1(KnMessage message) {
        String methodName = "updateClientFS1(KnMessage)";
        knLogger.debug(methodName, "update Client FS1 ");
        return xdmMediator.updateClientFS1(message);
    }


    /**
     * method to perform the Udpate of client feature set
     * <p/>
     * IXDMRequestDTO requestDTO
     *
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateUserAgent(IXDMRequestDTO requestDTO) {
        String methodName = "updateClientFS1(KnMessage)";
        knLogger.debug(methodName, "update Client FS1 ");
        return xdmMediator.updateUserAgent(requestDTO);
    }


    /**
     * @param message
     */
    public void addContacts(KnMessage message) {

        String methodName = "addContacts(KnMessage)";
        knLogger.debug(methodName, "Add Contact to Contact List");
        xdmMediator.addContacts(message);
    }

    /**
     * @param message
     */
    public void deleteContacts(KnMessage message) {

        String methodName = "deleteContacts(KnMessage)";
        knLogger.debug(methodName, "delete Contact to Contact List");
        xdmMediator.deleteContacts(message);
    }

    /**
     * @param message
     */
    public void modifyContacts(KnMessage message) {

        String methodName = "modifyContacts(KnMessage)";
        knLogger.debug(methodName, "Modify Contact to Contact List");
        xdmMediator.modifyContacts(message);
    }

    /**
     * @param message
     */
    public void getContactList(KnMessage message) {

        String methodName = "getContactList(KnMessage)";
        knLogger.debug(methodName, "Get Contact request : ");
        xdmMediator.getContactList(message);
    }

    /**
     * @param message
     */
    public void createGroup(KnMessage message) {

        String methodName = "createGroup(KnMessage)";
        knLogger.debug(methodName, "Create Group Request : ");
        xdmMediator.createGroup(message);
    }

    /**
     * @param message
     */
    public void modifyGroupName(KnMessage message) {

        String methodName = "modifyGroupName(KnMessage)";
        knLogger.debug(methodName, "Modify GroupName Request : ");
        xdmMediator.modifyGroupName(message);
    }

    /**
     * @param message
     */
    public void modifyGroupMember(KnMessage message) {

        String methodName = "modifyGroupMember(KnMessage)";
        knLogger.debug(methodName, "Modify Group Member Request : ");
        xdmMediator.modifyGroupMember(message);
    }

    /**
     * @param message
     */
    public void deleteGroupMember(KnMessage message) {

        String methodName = "deleteGroupMember(KnMessage)";
        knLogger.debug(methodName, "Delete Group Member Request : ");
        xdmMediator.deleteGroupMember(message);
    }

    /**
     * @param message
     */
    public void addGroupMember(KnMessage message) {

        String methodName = "addGroupMember(KnMessage)";
        knLogger.debug(methodName, "Add Group Member Request : ");
        xdmMediator.addGroupMember(message);
    }

    /**
     * @param message
     */
    public void deleteGroup(KnMessage message) {

        String methodName = "deleteGroup(KnMessage)";
        knLogger.debug(methodName, "Delete Group Request : ");
        xdmMediator.deleteGroup(message);
    }

    /**
     * get group details - SOAP interface
     *
     * @param message
     */
    public void getGroupDetails(KnMessage message) {

        String methodName = "getGroupDetails(KnMessage)";
        knLogger.debug(methodName, "Get Group Details Request : ");
        xdmMediator.getGroupDetails(message);
    }

    /**
     * get group document details - XCAP interface
     *
     * @param message
     */
    public void getGroupDocDetails(KnMessage message) {

        String methodName = "getGroupDocDetails(KnMessage)";
        knLogger.debug(methodName, "Get Group Document Details Request : ");
        xdmMediator.getGroupDocDetails(message);
    }

    /**
     * get group document details - PTX interface
     *
     * @param message
     */
    public IXDMResponseDTO getPubGroupDetails(KnMessage message) {

        String methodName = "getPubGroupDetails(KnMessage)";
        knLogger.debug(methodName, "Get Group Document Details Request : ");
        return xdmMediator.getPubGroupDetails(message);
    }

    /**
     * @param message
     */
    public void getAllContactList(KnMessage message) {

        String methodName = "getAllContactList(KnMessage)";
        knLogger.debug(methodName, "Get All Contact List Request : ");
        xdmMediator.getAllContactLists(message);
    }


    /**
     * @param message
     */
    public void getDirectory(KnMessage message) {

        String methodName = "getDirectory(KnMessage)";
        knLogger.debug(methodName, "Get Directory Request : ");
        xdmMediator.getDirectory(message);
    }


    /**
     * @param message
     */
    public void getRLSDoc(KnMessage message) {
        String methodName = "getRLSDoc(KnMessage)";
        knLogger.debug(methodName, "Get Rls Doc Request : ");
        xdmMediator.getRlsDoc(message);
    }


    /**
     * @param message
     */
    public void getGroupList(KnMessage message) {
        String methodName = "getGroupList(KnMessage)";
        knLogger.debug(methodName, "Get GroupList Request : ");
        xdmMediator.getGroupList(message);
    }

    /**
     * @param message
     */
    public IXDMResponseDTO getPubGroupList(KnMessage message) {
        String methodName = "getGroupList(KnMessage)";
        knLogger.debug(methodName, "Get GroupList Request : ");
        return xdmMediator.getPubGroupList(message);
    }


    /**
     * method to verify the health check for the XDM
     *
     * @param message
     */
    public void isAlive(KnMessage message) {
        String methodName = "isAlive(KnMessage)";
        knLogger.debug(methodName, "isAlive Health Ping :");
        xdmMediator.isAlive(message);
    }


    /**
     * Method which response to the Client when found an invalid operation type is being passed
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO unKnownOperation(IXDMRequestDTO requestDTO) {
        String methodName = "unKnownOperation(KnMessage)";
        knLogger.debug(methodName, "Unknown Operation Id received in request");
        return xdmMediator.unKnownOperation(requestDTO);
    }

    //Corporate Facade APIs starts//
    public IXDMResponseDTO authenticate(IXDMRequestDTO authRequestDTO) {
        String methodName = "authenticate(authRequestDTO)";
        knLogger.debug(methodName, "Authenticate");
        IXDMResponseDTO respDto = xdmMediator.authenticate(authRequestDTO);
        return respDto;
    }

    public IXDMResponseDTO getCorpMasterList(IXDMRequestDTO contactRequestDTO) {
        String methodName = "getCorpMasterList(contactRequestDTO)";
        knLogger.debug(methodName, "Get Corp Master List.");
        IXDMResponseDTO respDto = xdmMediator.getCorpMasterList(contactRequestDTO);
        return respDto;
    }

    public IXDMResponseDTO getCorpSubscContactList(IXDMRequestDTO contactRequestDTO) {
        String methodName = "getCorpSubscContactList(contactRequestDTO)";
        knLogger.debug(methodName, "Get Corp Subscribers Contact List.");
        IXDMResponseDTO respDto = xdmMediator.getCorpSubscContactList(contactRequestDTO);
        return respDto;
    }

    public IXDMResponseDTO modifyCorpSubscContacts(KnMessage message) {
        String methodName = "modifyCorpSubscContacts(contactRequestDTO)";
        knLogger.debug(methodName, "Modify Corp Subscriber Contacts.");
        IXDMResponseDTO respDto = xdmMediator.modifyCorpSubscContacts(message);
        return respDto;
    }

    public IXDMResponseDTO pushSublists(IXDMRequestDTO sublistRequestDTO) {
        String methodName = "pushSublists(sublistRequestDTO)";
        knLogger.debug(methodName, "Push sublist to Subscribers.");
        IXDMResponseDTO respDto = xdmMediator.pushSublists(sublistRequestDTO);
        return respDto;
    }

    public IXDMResponseDTO removeSublist(KnMessage message) {
        String methodName = "removeSublist(subsRequestDTO)";
        knLogger.debug(methodName, "Remove sublist from Subscribers.");
        IXDMResponseDTO respDto = xdmMediator.removeSublist(message);
        return respDto;
    }

    public IXDMResponseDTO addCorpContacts(KnMessage message) {
        String methodName = "addCorpContacts(contactRequestDTO)";
        knLogger.debug(methodName, "Add External Contact.");
        IXDMResponseDTO respDto = xdmMediator.addCorpContacts(message);
        return respDto;
    }

    public IXDMResponseDTO modifyCorpContacts(KnMessage message) {
        String methodName = "modifyCorpContacts(contactRequestDTO)";
        knLogger.debug(methodName, "Modify External Corp Contact.");
        IXDMResponseDTO respDto = xdmMediator.modifyCorpContacts(message);
        return respDto;
    }

    public IXDMResponseDTO removeCorpContacts(KnMessage message) {
        String methodName = "removeCorpContacts(contactRequestDTO)";
        knLogger.debug(methodName, "Remove External Corp Contact.");
        IXDMResponseDTO respDto = xdmMediator.removeCorpContacts(message);
        return respDto;
    }

    public IXDMResponseDTO getCorpContactDetails(IXDMRequestDTO conactRequestDTO) {
        String methodName = "getCorpContactDetails(contactRequestDTO)";
        knLogger.debug(methodName, "Get External Corp Contact Details.");
        IXDMResponseDTO respDto = xdmMediator.getCorpContactDetails(conactRequestDTO);
        return respDto;
    }

    public IXDMResponseDTO createSublist(KnMessage message) {
        String methodName = "createSublist(sublistReqDto)";
        knLogger.debug(methodName, "Create Sublist.");
        IXDMResponseDTO respDto = xdmMediator.createSublist(message);
        return respDto;
    }

    public IXDMResponseDTO modifySublist(KnMessage message) {
        String methodName = "modifySublist(sublistReqDto)";
        knLogger.debug(methodName, "Modify Sublist.");
        IXDMResponseDTO respDto = xdmMediator.modifySublist(message);
        return respDto;
    }

    public IXDMResponseDTO deleteSublist(KnMessage message) {
        String methodName = "deleteSublist(sublistReqDto)";
        knLogger.debug(methodName, "Delete Sublist.");
        IXDMResponseDTO respDto = xdmMediator.deleteSublist(message);
        return respDto;
    }

    public IXDMResponseDTO getSublistDetails(IXDMRequestDTO sublistReqDto) {
        String methodName = "getSublistDetails(sublistReqDto)";
        knLogger.debug(methodName, "Get Sublist Details.");
        IXDMResponseDTO respDto = xdmMediator.getSublistDetails(sublistReqDto);
        return respDto;
    }

    public IXDMResponseDTO getAllSublist(IXDMRequestDTO corpInfoDto) {
        String methodName = "getAllSublist(corpInfoDto)";
        knLogger.debug(methodName, "Get All Sublist.");
        IXDMResponseDTO respDto = xdmMediator.getAllSublist(corpInfoDto);
        return respDto;
    }

    public IXDMResponseDTO getDistributionList(IXDMRequestDTO distributionInfoDto) {
        String methodName = "getAllSublist(corpInfoDto)";
        knLogger.debug(methodName, "Get Sublist Distribution list.");
        IXDMResponseDTO respDto = xdmMediator.getDistributionList(distributionInfoDto);
        return respDto;
    }

    public IXDMResponseDTO createCorpGroup(KnMessage message) {
        String methodName = "createCorpGroup(groupInfoDTO)";
        knLogger.debug(methodName, "Create Group.");
        IXDMResponseDTO respDto = xdmMediator.createCorpGroup(message, Boolean.FALSE);
        return respDto;
    }

    public IXDMResponseDTO modifyCorpGroup(KnMessage message) {
        String methodName = "modifyCorpGroup(groupInfoDTO)";
        knLogger.debug(methodName, "Modify Group.");
        IXDMResponseDTO respDto = xdmMediator.modifyCorpGroup(message);
        return respDto;
    }

    public IXDMResponseDTO deleteCorpGroup(KnMessage message) {
        String methodName = "deleteCorpGroup(groupInfoDTO)";
        knLogger.debug(methodName, "Delete Group.");
        IXDMResponseDTO respDto = xdmMediator.deleteCorpGroup(message);
        return respDto;
    }

    public IXDMResponseDTO getCorpGroupDetails(IXDMRequestDTO groupInfoDTO) {
        String methodName = "getCorpGroupDetails(groupInfoDTO)";
        knLogger.debug(methodName, "Get Corp Group Details.");
        IXDMResponseDTO respDto = xdmMediator.getCorpGroupDetails(groupInfoDTO);
        knLogger.debug(methodName, "respDto - ", respDto);
        return respDto;
    }

    public IXDMResponseDTO getCorpGroupList(IXDMRequestDTO corpInfo) {
        String methodName = "getCorpGroupList(corpInfo)";
        knLogger.debug(methodName, "Get Corp Group List.");
        IXDMResponseDTO respDto = xdmMediator.getCorpGroupList(corpInfo);
        return respDto;
    }

    public IXDMResponseDTO getCorpGroupDetailsList(IXDMRequestDTO corpGroupListInfo) {
        String methodName = "getCorpGroupDetailsList(corpGroupListInfo)";
        knLogger.debug(methodName, "Get Corp Group Details List.");
        IXDMResponseDTO respDto = xdmMediator.getCorpGroupList(corpGroupListInfo);
        return respDto;
    }

    public IXDMResponseDTO getCorpSubscriberGroupList(IXDMRequestDTO subscriberCorpInfo) {
        String methodName = "getCorpSubscriberGroupList(subscriberCorpInfo)";
        knLogger.debug(methodName, "Get Subscrier Corp Group List.");
        IXDMResponseDTO respDto = xdmMediator.getCorpSubscriberGroupList(subscriberCorpInfo);
        return respDto;
    }

    /**
     * This method is used to get the PocCorp group list for the Group MDN (client type =7)
     *
     * @return IXDMResponseDTO
     */
    public IXDMResponseDTO getPocLinkedGroupList(IXDMRequestDTO pocCorpInfo) {
        String methodName = "getPocLinkedGroupList(IXDMRequestDTO(pocCorpInfo)";
        knLogger.debug(methodName, "Get Poc Corp Group List.");
        IXDMResponseDTO respDto = xdmMediator.getPocLinkedGroupList(pocCorpInfo);
        return respDto;
    }

    public IXDMResponseDTO getSubscriberEmailId(IXDMRequestDTO activationCodeInfo) {
        String methodName = "generateActivationCode(activationCodeInfo)";
        knLogger.debug(methodName, "Calling generateActivationCode.");
        IXDMResponseDTO respDto = xdmMediator.getSubscriberEmailId(activationCodeInfo);
        return respDto;
    }

    public IXDMResponseDTO saveActivationCode(IXDMRequestDTO activationCodeInfo) {
        String methodName = "saveActivationCode(activationCodeInfo)";
        knLogger.debug(methodName, "Calling saveActivationCode.");
        IXDMResponseDTO respDto = xdmMediator.saveClientActivationMail(activationCodeInfo);
        return respDto;
    }

    public IXDMResponseDTO generateActivationCodes(KnMessage message) {
        String methodName = "generateActivationCodes(activationCodeInfo)";
        knLogger.debug(methodName, "Calling generateActivationCodes.");
        IXDMResponseDTO respDto = xdmMediator.generateActivationCodes(message);
        return respDto;
    }

    public IXDMResponseDTO getMailInfo(IXDMRequestDTO activationCodeInfo) {
        String methodName = "getMailInfo(activationCodeInfo)";
        knLogger.debug(methodName, "Calling getMailInfo.");
        IXDMResponseDTO respDto = xdmMediator.getMailInfo(activationCodeInfo);
        return respDto;
    }

    public IXDMResponseDTO sendActivationMail(IXDMRequestDTO activationCodeInfo) {
        String methodName = "sendActivationMail(activationCodeInfo)";
        knLogger.debug(methodName, "Calling sendActivationMail.");
        IXDMResponseDTO respDto = xdmMediator.sendActivationMail(activationCodeInfo);
        return respDto;
    }

    public IXDMResponseDTO sendMail(IXDMRequestDTO activationCodeInfo) {
        String methodName = "sendMail(activationCodeInfo)";
        knLogger.debug(methodName, "Calling sendMail.");
        IXDMResponseDTO respDto = xdmMediator.sendMail(activationCodeInfo);
        return respDto;
    }

    public IXDMResponseDTO removeSubscribersContacts(KnMessage message) {
        String methodName = "removeSubscribersContacts(corpSubsInfo)";
        knLogger.debug(methodName, "Remove Subscribers Contacts.");
        IXDMResponseDTO respDto = xdmMediator.removeSubscribersContacts(message);
        return respDto;
    }

    public IXDMResponseDTO removeSubscribersAllSublist(KnMessage message) {
        String methodName = "removeSubscribersAllSublist(corpSubsInfo)";
        knLogger.debug(methodName, "Remove Subscribers Shared Sublist.");
        IXDMResponseDTO respDto = xdmMediator.removeSubscribersAllSublist(message);
        return respDto;
    }

    public IXDMResponseDTO removeSubscribersAllGroups(KnMessage message) {
        String methodName = "removeSubscribersAllGroups(corpSubsInfo)";
        knLogger.debug(methodName, "Remove Subscribers All Groups.");
        IXDMResponseDTO respDto = xdmMediator.removeSubscribersAllGroups(message);
        return respDto;
    }


    /* method to create the subscriber profile
    *
    * @param requestDTO KnMessage
    * @return IXDMResponseDTO Object
    */
    public IXDMResponseDTO createLicensePack(KnMessage message) {
        String methodName = "createLicensePack(KnMessage)";
        knLogger.debug(methodName, "createLicensePack with Object - ", message);
        //sending the information to the Mediation layer;
        return xdmMediator.createLicensePack(message);
    }

    /* method to get the License Pack Profile profile
    *
    * @param requestDTO KnMessage
    * @return IXDMResponseDTO Object
    */
    public IXDMResponseDTO getLicensePackProfile(KnMessage message) {
        String methodName = "getLicensePackProfile(KnMessage)";
        knLogger.debug(methodName, "getLicensePackProfile with Object - ", message);
        //sending the information to the Mediation layer;
        return xdmMediator.getLicensePackProfile(message);
    }

    //TODO: Deprecated need to delete after testing.
    /**
     * method to perform the updation of the subscriber Profile
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    /*public IXDMResponseDTO updatePAMAccount(KnMessage message) {
        String methodName = "updatePAMAccount(KnMessage)";
        knLogger.info( methodName, "update PAM Account with msg obj - ", message);
        //sending the information to the mediation Layer
        return xdmMediator.updatePAMAccount(message);
    }*/

    /**
     * method to perform the updation of the subscriber Profile
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO deleteLicensePack(KnMessage message) {
        String methodName = "deleteLicensePack(KnMessage)";
        knLogger.debug(methodName, "deleteLicensePack with msg obj - ", message);
        //sending the information to the mediation Layer
        return xdmMediator.deleteLicensePack(message);
    }


    /**
     * method to perform the updation of the PAM Billing MDN
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO changeBillingNumber(KnMessage message) {
        String methodName = "changeBillingNumber(KnMessage)";
        knLogger.debug(methodName, "changeBillingNumber with msg obj - ", message);
        //sending the information to the mediation Layer
        return xdmMediator.changeBillingNumber(message);
    }

    /**
     * method to perform the upgrade of the PAM License pack.
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO upgradeLicensePack(KnMessage message) {
        String methodName = "upgradeLicensePack(KnMessage)";
        knLogger.debug(methodName, "upgrade LicensePack with msg obj - ", message);
        //sending the information to the mediation Layer
        return xdmMediator.upgradeLicensePack(message);
    }

    /**
     * method to perform the retrive  PAM Account MDN Details(list of Pseudomdn & its service auth status)
     * for the Billing MDN/Ext PAM Account ID.
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getPAMAccountMDNsDetails(KnMessage message) {
        String methodName = "getPAMAccountMDNsDetails(KnMessage)";
        knLogger.debug(methodName, "Retrieve PAM Account MDN Details - ", message);
        return xdmMediator.getPAMAccountMDNsDetails(message);
    }

    /**
     * method to perform the re-activation and the deactivation of the Subscriber
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO changePAMServiceAuthStatus(KnMessage message) {
        String methodName = "changePAMServiceAuthStatus(KnMessage)";
        knLogger.debug(methodName, "change PAM Service Auth Status - ", message);
        return xdmMediator.changePAMServiceAuthStatus(message);
    }

    /**
     * method for custom corpoarte operations
     *
     * @param requestDTO IXDMRequestDTO
     * @return
     */
    public IXDMResponseDTO customCorpOperation(IXDMRequestDTO requestDTO){
        String methodName = "customCorpOperation(IXDMRequestDTO)";
        knLogger.debug(methodName, "customCorpOperation - ");
        IXDMResponseDTO responseDTO = new KnXDMRespDTO();
        Object resp = null;
        try {
            // todo below getInstance added as processInvokerIntf was null need ve verify
            processInvokerIntf = KnProcessInvokerImpl.getInstance();
            resp = processInvokerIntf.invokeHook(KnXDMIntfConstants.HOOK_NAME, requestDTO);
            if ( resp != null  && resp instanceof String) {
                knLogger.debug(methodName, "the cutom hook not installed - ", responseDTO);
                responseDTO.setResponseStatus(1);
                responseDTO.setResponseCode("Validator.KNEC-CP16114");
                responseDTO.setResponseMessage("Custom is not installed");
                knLogger.debug(methodName, "the cutom hook not installed - ", responseDTO);
            } else if(resp != null){
                responseDTO = (IXDMResponseDTO) resp;
            }
            knLogger.debug(methodName, "resp - ", resp);
        } catch (KnProcessInvokerException e) {
            knLogger.error(methodName, "Failed to invoke the XDMCustomMediator ");
            knLogger.error(methodName, e);
            responseDTO.setResponseStatus(1);
            responseDTO.setResponseCode(e.getErrorCode());
            responseDTO.setResponseMessage(e.getErrorMessage());
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred ");
            knLogger.error(methodName, e);
            responseDTO.setResponseStatus(1);
        }
        knLogger.debug(methodName, "resp received from custom hook - ", responseDTO);
        return responseDTO;
    }

//Corporate Facade APIs ends//

    /**
     * method to perform the updation of the subscriber Profile
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateLicensePack(KnMessage message) {
        String methodName = "updateLicensePack(KnMessage)";
        knLogger.debug(methodName, "updateLicensePack with msg obj - ", message);
        //sending the information to the mediation Layer
        return xdmMediator.updateLicensePack(message);
    }

    /**
     * method to perform the updation of the subscriber Profile
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO downgradeLicensePack(KnMessage message) {
        String methodName = "downgradeLicensePack(KnMessage)";
        knLogger.debug(methodName, "downgradeRatePlanPamAcc with msg obj - ", message);
        //sending the information to the mediation Layer
        return xdmMediator.downgradeLicensePack(message);
    }

    /**
     * method to perform the updation of the subscriber Profile
     *
     * @return IXDMResponseDTO Object
     */
    @Override
    public IXDMResponseDTO modifySubscCorpFeature(IXDMRequestDTO requestDTO) {
        String methodName = "modifySubscCorpFeature(KnMessage)";
        knLogger.debug(methodName, "modifySubscCorpFeature with msg obj - ", requestDTO);
        //sending the information to the mediation Layer
        return xdmMediator.modifySubscCorpFeature(requestDTO);
    }

    /**
     * method to perform the updation of the subscriber Profile
     *
     * @return IXDMResponseDTO Object
     */
    @Override
    public IXDMResponseDTO getSubscriberScanList(IXDMRequestDTO requestDTO) {
        String methodName = "getSubscriberScanList(KnMessage)";
        knLogger.debug(methodName, "getSubscriberScanList with msg obj - ", requestDTO);
        return xdmMediator.getSubscriberScanList(requestDTO);
    }

    /**
     * method to perform the updation of the subscriber Profile
     *
     * @return IXDMResponseDTO Object
     */
    @Override
    public IXDMResponseDTO modifySubscriberScanList(KnMessage message) {
        String methodName = "modifySubscriberScanList(KnMessage)";
        knLogger.debug(methodName, "Entry ");
        return xdmMediator.modifySubscriberScanList(message);
    }

    public IXDMResponseDTO deleteExternalSubscriber(KnMessage message) {
        String methodName = "deleteExternalSubscriber(KnMessage)";
        knLogger.debug(methodName, "delete External subscriber - ");
        return xdmMediator.deleteExternalSubscriber(message);

    }

    public IXDMResponseDTO updateScanList(IXDMRequestDTO requestDTO) {
        String methodName = "updateScanList(IXDMRequestDTO)";
        knLogger.debug(methodName, "Update Scan List - ", requestDTO);
        return xdmMediator.updateScanList(requestDTO);

    }

    public IXDMResponseDTO getScanList(IXDMRequestDTO requestDTO) {
        String methodName = "getScanList(IXDMRequestDTO)";
        knLogger.debug(methodName, "Get Scan List - ", requestDTO);
        return xdmMediator.getSubscriberScanList(requestDTO);

    }

    public IXDMResponseDTO deleteScanList(IXDMRequestDTO requestDTO) {
        String methodName = "deleteScanList(IXDMRequestDTO)";
        knLogger.debug(methodName, "Delete Scan List - ", requestDTO);
        return xdmMediator.deleteScanList(requestDTO);

    }

    public IXDMResponseDTO getAllBillingMdns(IXDMRequestDTO licenseRequestDTO) {
        String methodName = "getAllBillingMdns(IXDMRequestDTO)";
        knLogger.debug(methodName, "Get All Billing Mdns.");
        IXDMResponseDTO respDto = xdmMediator.getAllBillingMdns(licenseRequestDTO);
        return respDto;
    }

    public IXDMResponseDTO getLicenseSubs(IXDMRequestDTO licenseRequestDTO) {
        String methodName = "getLicenseSubs(licenseRequestDTO)";
        knLogger.debug(methodName, "Get License Subscribers.");
        IXDMResponseDTO respDto = xdmMediator.getLicenseSubs(licenseRequestDTO);
        return respDto;
    }
    
    public IXDMResponseDTO updateBillingName(IXDMRequestDTO licenseRequestDTO) {
        String methodName = "updateBillingName(licenseRequestDTO)";
        knLogger.debug(methodName, "update Billing Name");
        IXDMResponseDTO respDto = xdmMediator.updateBillingName(licenseRequestDTO);
        return respDto;
    }

    public IXDMResponseDTO markSubsForDeletion(IXDMRequestDTO licenseRequestDTO) {
        String methodName = "markSubsForDeletion(licenseRequestDTO)";
        knLogger.debug(methodName, "Mark & Unmark Subscriber For Deletion");
        IXDMResponseDTO respDto = xdmMediator.markSubsForDeletion(licenseRequestDTO);
        return respDto;
    }

    public IXDMResponseDTO licenseAuthenticate(IXDMRequestDTO licenseRequestDTO) {
        String methodName = "licenseAuthenticate(licenseRequestDTO)";
        knLogger.debug(methodName, "Pam license Authenticate");
        IXDMResponseDTO respDto = xdmMediator.licenseAuthenticate(licenseRequestDTO);
        return respDto;

    }

    /**
     * This method is to retrieve the list of subscribers where request mdn exist as contact in there private contact list.
     *
     * @param requestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getSubscrReverseContacts(IXDMRequestDTO requestDTO) {
        String methodName = "getSubscrReverseContacts()";
        knLogger.debug(methodName, "Get Reverse Contacts - ", requestDTO);
        return xdmMediator.getSubscrReverseContacts(requestDTO);
    }

    /**
     * This method is to retrieve the list of sublist where the request MDN exist as member.
     *
     * @param requestDTO
     * @return
     */
    public IXDMResponseDTO getSubscrSublists(IXDMRequestDTO requestDTO) {
        String methodName = "getSubscrSublists()";
        knLogger.debug(methodName, "Get Subscr Sublists - ", requestDTO);
        return xdmMediator.getSubscrSublists(requestDTO);
    }

    @Override
	public IXDMResponseDTO updateAutoPairing(IXDMRequestDTO requestDTO) {
		String methodName = "updateAutoPairing(KnMessage)";
	    knLogger.debug(methodName, "update Auto Pairing with Object - ", requestDTO);
	    //sending the information to the Mediation layer;
	    return xdmMediator.updateAutoPairing(requestDTO);
	}

    /**
     * This method is to create TPUser
     *
     * @param requestDTO
     * @return IXDMResponseDTO Object
     */
	@Override
	public IXDMResponseDTO createTPUser(IXDMRequestDTO requestDTO) {
		return xdmMediator.createTPUser(requestDTO);
	}

	 /**
     * This method is to update TPUser
     *
     * @param requestDTO
     * @return IXDMResponseDTO Object
     */
	@Override
	public IXDMResponseDTO updateTPUser(IXDMRequestDTO requestDTO) {
		return xdmMediator.updateTPUser(requestDTO);
	}

	 /**
     * This method is to delete TPUser
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
	@Override
	public IXDMResponseDTO deleteTPUser(KnMessage message) {
		return xdmMediator.deleteTPUser(message);
	}


	 /**
    * This method is to generate activation code for TPUser
    *
    * @param requestDTO
    * @return IXDMResponseDTO Object
    */
	@Override
	public IXDMResponseDTO generateTPActivationCode(IXDMRequestDTO requestDTO) {
		return xdmMediator.generateTPActivationCode(requestDTO);
	}


    @Override
    public IXDMResponseDTO updateCorpAdminFS(KnMessage message) {
        return xdmMediator.updateCorpAdminFS(message);
    }

    @Override
    public IXDMResponseDTO getAllCorpSubscrFeatureSets(IXDMRequestDTO requestDTO) {
        return xdmMediator.getAllCorpSubscrFeatureSets(requestDTO);
    }

    @Override
    public IXDMResponseDTO getActivationCode(IXDMRequestDTO requestDTO) {
        return xdmMediator.getActivationCode(requestDTO);
    }

    @Override
    public IXDMResponseDTO getCorpSubscriberDetails(IXDMRequestDTO requestDTO) {
        return xdmMediator.getCorpSubscriberDetails(requestDTO);
    }

    @Override
    public IXDMResponseDTO getCorpExtContactDetails(IXDMRequestDTO requestDTO) {
        return xdmMediator.getCorpExtContactDetails(requestDTO);
    }

    @Override
    public IXDMResponseDTO getSubscrActivationCode(IXDMRequestDTO requestDTO) {
        String methodName = "getSubscrActivationCode(KnMessage)";
        knLogger.debug(methodName, "update Auto Pairing with Object - ", requestDTO);
        //sending the information to the Mediation layer;
        return xdmMediator.getSubscrActivationCode(requestDTO);
    }

    @Override
    public IXDMResponseDTO generateOTP(IXDMRequestDTO requestDTO) {
        String methodName = "generateOTP(KnMessage)";
        knLogger.debug(methodName, "generate the OTP for the subscriber - ", requestDTO);
        //sending the information to the Mediation layer;
        return xdmMediator.generateOTP(requestDTO);
    }

    @Override
    public IXDMResponseDTO validateOTP(IXDMRequestDTO requestDTO) {
        String methodName = "validateOTP(KnMessage)";
        knLogger.debug(methodName, "validate the OTP for the subscriber - ", requestDTO);
        //sending the information to the Mediation layer;
        return xdmMediator.validateOTP(requestDTO);
    }

    @Override
    public IXDMResponseDTO getCorpBanFanList(IXDMRequestDTO requestDTO){
        String methodName = "getCorpBanFanList(KnMessage)";
        knLogger.debug(methodName, "get ban fan list for the corporate - ", requestDTO);
        //sending the information to the Mediation layer;
        return xdmMediator.getCorpBanFanList(requestDTO);
    }

    @Override
    public IXDMResponseDTO getCorporateProfile(IXDMRequestDTO requestDTO) {
        return xdmMediator.getCorporateProfile(requestDTO);
    }

    @Override
    public IXDMResponseDTO switchConvergedClient(KnMessage message) {
        return xdmMediator.switchConvergedClient(message);
    }

    /**
     * Method to retrieve the system level configuration
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getSysConfig(IXDMRequestDTO requestDTO) {
        String methodName = "getSysConfig(KnMessage)";
        knLogger.debug(methodName, "Get System configuration ", requestDTO);
        //sending the information to the Mediation layer
        return xdmMediator.getSysConfig(requestDTO);
    }

    /**
     * @param message
     */
    @Override
    public void modifyDynamicContacts(KnMessage message) {
        xdmMediator.modifyDynamicContacts(message);
    }

    /**
     * Interface to delete all dynamic contact list
     * @param message
     */
    @Override
    public void deleteDynamicContacts(KnMessage message) {
        xdmMediator.deleteDynamicContacts(message);
    }

    /**
     * Interface to retrieve the dynamic contact list.
     *
     * @param message
     */
    @Override
    public void getDynamicContacts(KnMessage message) {
        xdmMediator.getDynamicContacts(message);
    }

    /**
     * Interface to retrieve the dynamic group details
     *
     * @param message
     */
    @Override
    public void getDynamicNonSharedGrpDetails(KnMessage message) {
        xdmMediator.getDynamicNonSharedGrpDetails(message);
    }

    /**
     * Interface to retrieve the list of groups owned by MDN.
     *
     * @param msgObj
     */
    @Override
    public void getDynamicNonSharedGrpList(KnMessage msgObj) {
        xdmMediator.getDynamicNonSharedGrpList(msgObj);
    }

    /**
     * Interface to delete dynamic non-shared group
     *
     * @param message
     */
    @Override
    public void deleteDynamicNonSharedGrp(KnMessage message) {
        xdmMediator.deleteDynamicNonSharedGrp(message);
    }

    /**
     * Interface to create non-sharded dynamic group
     *
     * @param msgObj
     */
    @Override
    public void createNonSharedGroup(KnMessage msgObj) {
        xdmMediator.createNonSharedGroup(msgObj);
    }

    /**
     * Interface to modify the non-shared dynamic group
     *
     * @param msgObj
     */
    @Override
    public void modifyNonSharedGroup(KnMessage msgObj) {
        xdmMediator.modifyNonSharedGroup(msgObj);
    }

    @Override
    public IXDMResponseDTO updateCorpSubscriber(KnMessage message) {
        return xdmMediator.updateCorpSubscriber(message);
    }

    @Override
    public IXDMResponseDTO generateActivationCodeIDMIntf(IXDMRequestDTO requestDTO) {
        return xdmMediator.generateActivationCodeIDMIntf(requestDTO);
    }

    @Override
    public IXDMResponseDTO getLITargetInfo(IXDMRequestDTO requestDTO){
        return xdmMediator.getLITargetInfo(requestDTO);
    }

    @Override
    public IXDMResponseDTO getCorpSubsUserProfile(IXDMRequestDTO requestDTO) {
        return xdmMediator.getCorpSubsUserProfile(requestDTO);
    }

    @Override
    public IXDMResponseDTO resetCorpSubsUserPassword(IXDMRequestDTO requestDTO) {
        return xdmMediator.resetCorpSubsUserPassword(requestDTO);
    }

    @Override
    public IXDMResponseDTO resendCorpSubsVerificationEmail(IXDMRequestDTO requestDTO) {
        return xdmMediator.resendCorpSubsVerificationEmail(requestDTO);
    }

    @Override
    public IXDMResponseDTO setTargetPermissions(KnMessage message) {
        return xdmMediator.setTargetPermissions(message);
    }

    @Override
    public IXDMResponseDTO getTargetPermissions(KnMessage message) {
        return xdmMediator.getTargetPermissions(message);
    }

    public IXDMResponseDTO getAuthorizationList(IXDMRequestDTO requestDTO) {
        String methodName = "getAuthorizationList(IXDMRequestDTO)";
        knLogger.debug(methodName, "Get Scan AuthList - ", requestDTO);
        return xdmMediator.getAuthorizationList(requestDTO);

    }

    public IXDMResponseDTO updateAuthorizationList(KnMessage message) {
        String methodName = "updateAuthorizationList(IXDMRequestDTO)";
        knLogger.debug(methodName, "Get  AuthList - ");
        return xdmMediator.updateAuthorizationList(message);

    }

    public IXDMResponseDTO getEmergencyConfigDoc(IXDMRequestDTO requestDTO) {
        String methodName = "getEmergencyConfigDoc(IXDMRequestDTO)";
        knLogger.debug(methodName, "Get emergency List - ", requestDTO);
        return xdmMediator.getEmergencyConfigDoc(requestDTO);

    }

    @Override
    public IXDMResponseDTO getAuthorizedMdnList(IXDMRequestDTO requestDTO) {
        return xdmMediator.getAuthorizedMdnList(requestDTO);
    }

    @Override
    public IXDMResponseDTO setSubsEmergencyAttributes(KnMessage message) {
        return xdmMediator.setSubsEmergencyAttributes(message);
    }

    @Override
    public IXDMResponseDTO getSubsEmergencyAttributes(IXDMRequestDTO requestDTO) {
        return xdmMediator.getSubsEmergencyAttributes(requestDTO);
    }

    @Override
    public IXDMResponseDTO getPubContactList(KnMessage message){
        return xdmMediator.getPubContactList(message);
    }

    @Override
    public IXDMResponseDTO updateSubsAliasEntities(KnMessage message) {
        return xdmMediator.updateSubsAliasEntities(message);
    }

    @Override
    public IXDMResponseDTO generateTempPassword(KnMessage message) {
        return xdmMediator.generateTempPassword(message);
    }

    @Override
    public IXDMResponseDTO getUserEmergDest(IXDMRequestDTO requestDTO) {
        return xdmMediator.getUserEmergDest(requestDTO);
    }

    /**
     * Interface for device activation API
     *
     * @param inputDTO
     * @return
     */
    @Override
    public IXDMResponseDTO deviceActivation(IXDMRequestDTO inputDTO) {
        return xdmMediator.deviceActivation(inputDTO);
    }

    public IXDMResponseDTO radioDeviceActivation(IXDMRequestDTO inputDTO){
        return xdmMediator.radioDeviceActivation(inputDTO);
    }

    /**
     * Interface for user login API
     *
     * @param message KnMessage
     * @return
     */
    @Override
    public IXDMResponseDTO userLogin(KnMessage message) {
        return xdmMediator.userLogin(message);
    }

    /**
     * Interface for modify Additional TG List
     *
     * @param message KnMessage
     * @return
     */
    @Override
    public IXDMResponseDTO modifySubscriberTGList(KnMessage message) {
        return xdmMediator.modifySubscriberTGList(message);
    }

    /**
     * Interface to Get Additional TG List
     *
     * @param requestDTO
     * @return
     */
    @Override
    public IXDMResponseDTO getSubscriberTGList(IXDMRequestDTO requestDTO) {
        return xdmMediator.getSubscriberTGList(requestDTO);
    }

    public IXDMResponseDTO getGroupUsageListDoc(IXDMRequestDTO requestDTO) {
        String methodName = "getGroupUsageListDoc(IXDMRequestDTO)";
        knLogger.debug(methodName, "Get getGroupUsageListDoc  - ", requestDTO);
        return xdmMediator.getGroupUsageListDoc(requestDTO);

    }

    /**
     * Interface to Delete Additional TG List
     *
     * @param message KnMessage
     * @return
     */
    @Override
    public IXDMResponseDTO deleteTGList(KnMessage message) {
        return xdmMediator.deleteTGList(message);
    }

    @Override
    public IXDMResponseDTO getUserProfile(IXDMRequestDTO requestDTO) {
        return xdmMediator.getUserProfile(requestDTO);
    }
    
    @Override
    public IXDMResponseDTO upgradeOrDowngradLicensePack(KnMessage message) {
        String methodName = "upgradeOrDowngradLicensePack(KnMessage)";
        knLogger.debug(methodName, "upgrade or downgrad LicensePack with msg obj - ", message);
        return xdmMediator.upgradeOrDowngradLicensePack(message);
    }

    @Override
    public IXDMResponseDTO getSubsEmergencyDetails(IXDMRequestDTO inputDTO) {
        return xdmMediator.getSubsEmergencyDetails(inputDTO);
    }

    @Override
    public IXDMResponseDTO sendSMS(IXDMRequestDTO requestDTO) {
        return xdmMediator.sendSMS(requestDTO);
    }

    @Override
    public IXDMResponseDTO getCorpProfileByEntities(IXDMRequestDTO requestDTO) {
        return xdmMediator.getCorpProfileByEntities(requestDTO);
    }
    
    @Override
    public IXDMResponseDTO sendTempPassword(IXDMRequestDTO requestDTO) {
        return xdmMediator.sendTempPassword(requestDTO);
    }

    @Override
	public IXDMResponseDTO getSubscriberCorpGroupList(IXDMRequestDTO subscriberCorpInfo) {
        String methodName = "getSubscriberCorpGroupList(subscriberCorpInfo)";
        knLogger.debug(methodName, "Get Subscrier Group List.");
       IXDMResponseDTO respDto = xdmMediator.getSubscriberCorpGroupList(subscriberCorpInfo);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getTGSSList(IXDMRequestDTO requestDTO) {
        String methodName = "getTGSSList(IXDMRequestDTO)";
        knLogger.debug(methodName, "Get TGSSList - ", requestDTO);
        return xdmMediator.getTGSSList(requestDTO);
    }

    @Override
    public IXDMResponseDTO updateTGSSList(IXDMRequestDTO requestDTO) {
        String methodName = "updateTGSSList(IXDMRequestDTO)";
        knLogger.debug(methodName, "update TGSSList - ", requestDTO);
        return xdmMediator.updateTGSSList(requestDTO);
    }

    @Override
    public IXDMResponseDTO deleteTGSSList(IXDMRequestDTO requestDTO) {
        String methodName = "deleteTGSSList(IXDMRequestDTO)";
        knLogger.debug(methodName, "delete  TGSSList - ", requestDTO);
        return xdmMediator.deleteTGSSList(requestDTO);
    }

    @Override
    public IXDMResponseDTO addBulkGroupsToSubscriber(KnMessage message){
        String methodName = "addBulkGroupsToSubscriber(IXDMRequestDTO)";
        return xdmMediator.addBulkGroupsToSubscriber(message);
    }

    @Override
    public IXDMResponseDTO createSubsATGScanList(KnMessage message){
        String methodName = "createSubsATGScanList(KnMessage)";
        return xdmMediator.createSubsATGScanList(message);
    }

    @Override
    public IXDMResponseDTO createOSMList(IXDMRequestDTO requestDTO){
        String methodName = "createOSMList(IXDMRequestDTO)";
        knLogger.debug(methodName, "requestDTO - ", requestDTO);
        return xdmMediator.createOSMList(requestDTO);
    }

    @Override
    public IXDMResponseDTO updateOSMList(IXDMRequestDTO requestDTO){
        String methodName = "updateOSMList(IXDMRequestDTO)";
        knLogger.debug(methodName, "requestDTO - ", requestDTO);
        return xdmMediator.updateOSMList(requestDTO);
    }

    @Override
    public IXDMResponseDTO deleteOSMList(KnMessage message){
        String methodName = "deleteOSMList(IXDMRequestDTO)";
        return xdmMediator.deleteOSMList(message);
    }

    @Override
    public IXDMResponseDTO getOSMList(IXDMRequestDTO requestDTO){
        String methodName = "getOSMList(IXDMRequestDTO)";
        knLogger.debug(methodName, "requestDTO - ", requestDTO);
        return xdmMediator.getOSMList(requestDTO);
    }

    @Override
    public IXDMResponseDTO getOSMListDetails(IXDMRequestDTO requestDTO){
        String methodName = "getOSMListDetails(IXDMRequestDTO)";
        knLogger.debug(methodName, "requestDTO - ", requestDTO);
        return xdmMediator.getOSMListDetails(requestDTO);
    }

    @Override
    public IXDMResponseDTO assignOSMIdToGroup(KnMessage message) {
        String methodName = "assignOSMIdToGroup(IXDMRequestDTO)";
        return xdmMediator.assignOSMIdToGroup(message);
    }

    @Override
    public IXDMResponseDTO getOSMGroupList(IXDMRequestDTO requestDTO) {
        String methodName = "assignOSMIdToGroup(IXDMRequestDTO)";
        knLogger.debug(methodName, "requestDTO - ", requestDTO);
        return xdmMediator.getOSMGroupList(requestDTO);
    }
	@Override
   	public IXDMResponseDTO searchCorpAddressBook(IXDMRequestDTO subscriberInfo) {
           String methodName = "searchCorpAddressBook(subscriberInfo)";
           knLogger.debug(methodName, "Entry");
          IXDMResponseDTO respDto = xdmMediator.searchCorpAddressBook(subscriberInfo);
           return respDto;
       }

    @Override
    public IXDMResponseDTO updateCorpSubscriberMCSIds(KnMessage message) {
        return xdmMediator.updateCorpSubscriberMCSIds(message);
    }
    

	@Override
	public IXDMResponseDTO updateMCSIds(IXDMRequestDTO subscriberInfo) {
		String methodName = "updateMCSIds(subscriberInfo)";
        knLogger.debug(methodName, "Entry");
       IXDMResponseDTO respDto = xdmMediator.updateMCSIds(subscriberInfo);
        return respDto;
	}

	@Override
	public IXDMResponseDTO updateUserId(IXDMRequestDTO subscriberInfo) {
		String methodName = "updateUserId(subscriberInfo)";
        knLogger.debug(methodName, "Entry");
       IXDMResponseDTO respDto = xdmMediator.updateUserId(subscriberInfo);
        return respDto;
	}

	@Override
	public IXDMResponseDTO updateLicensePackSubsMCSIds(IXDMRequestDTO subscriberInfo) {
		String methodName = "updateLicensePackSubsMCSIds(subscriberInfo)";
        knLogger.debug(methodName, "Entry");
       IXDMResponseDTO respDto = xdmMediator.updateLicensePackSubsMCSIds(subscriberInfo);
        return respDto;
	}

	@Override
	public IXDMResponseDTO updateLicensePackSubsUserId(IXDMRequestDTO subscriberInfo) {
		String methodName = "updateLicensePackSubsUserId(subscriberInfo)";
        knLogger.debug(methodName, "Entry");
       IXDMResponseDTO respDto = xdmMediator.updateLicensePackSubsUserId(subscriberInfo);
        return respDto;
	}

    @Override
    public IXDMResponseDTO getPoCConfig(IXDMRequestDTO requestDTO){
        return xdmMediator.getPoCConfig(requestDTO);
    }

    @Override
    public IXDMResponseDTO getMobileSyncLocSupervisors(IXDMRequestDTO requestDTO){
        return xdmMediator.getMobileSyncLocSupervisors(requestDTO);
    }

    @Override
    public IXDMResponseDTO updatePrivacyOptStatus(KnMessage message) {
        String methodName = "updatePrivacyOptStatus(KnMessage)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.updatePrivacyOptStatus(message);
        knLogger.debug( methodName, "respDto::"+respDto);
        return respDto;
    }
	
	@Override
    public IXDMResponseDTO getMCPTTUEConfig(IXDMRequestDTO requestDTO) {
        String methodName = "getMCPTTUEConfig(requestDTO)";
        knLogger.debug(methodName, "--->Entry");
        knLogger.debug( methodName, "--->requestDTO::"+requestDTO);
        IXDMResponseDTO respDto = xdmMediator.getMCPTTUEConfig(requestDTO);
        knLogger.debug( methodName, "--->respDto::"+respDto);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getMCPTTUserProfile(KnMessage message) {
        String methodName = "getMCPTTUserProfile(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getMCPTTUserProfile(message);
        knLogger.debug( methodName, "respDto::"+respDto);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getMCPTTServiceConfig(IXDMRequestDTO requestDTO) {
        String methodName = "getMCPTTServiceConfig(requestDTO)";
        knLogger.debug(methodName, "Entry");
        knLogger.debug( methodName, "requestDTO::"+requestDTO);
        IXDMResponseDTO respDto = xdmMediator.getMCPTTServiceConfig(requestDTO);
        knLogger.debug( methodName, "respDto::"+respDto);
        return respDto;
    }
	
    @Override
    public IXDMResponseDTO getMCDATAUEConfig(IXDMRequestDTO requestDTO) {
        String methodName = "getMCDATAUEConfig(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getMCDATAUEConfig(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getMCDATAUserProfile(KnMessage message) {
        String methodName = "getMCDATAUserProfile(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getMCDATAUserProfile(message);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getMCDATAServiceConfig(IXDMRequestDTO requestDTO) {
        String methodName = "getMCDATAServiceConfig(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getMCDATAServiceConfig(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getMCVideoUEConfig(IXDMRequestDTO requestDTO) {
        String methodName = "getMCVideoUEConfig(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getMCVideoUEConfig(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getMCVideoUserProfile(KnMessage message) {
        String methodName = "getMCVideoUserProfile(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getMCVideoUserProfile(message);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getMCVideoServiceConfig(IXDMRequestDTO requestDTO) {
        String methodName = "getMCVideoServiceConfig(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getMCVideoServiceConfig(requestDTO);
        return respDto;
    }


    @Override
    public IXDMResponseDTO getMCSGroupDoc(IXDMRequestDTO requestDTO) {
        String methodName = "getMCSGroupDoc(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getMCSGroupDoc(requestDTO);
        return respDto;
    }

    public IXDMResponseDTO getMdnProfileIdsForMcPttIds(IXDMRequestDTO requestDTO) {
        String methodName = "getMdnProfileIdsForMcPttIds(KnMessage)";
        knLogger.debug(methodName, "getMdnProfileIdsForMcPttIds");
        return xdmMediator.getMdnProfileIdsForMcPttIds(requestDTO);
    }

    @Override
    public IXDMResponseDTO getMCSUserDir(IXDMRequestDTO requestDTO) {
        String methodName = "getMCSUserDir(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getMCSUserDir(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO selectProfileMdn(IXDMRequestDTO inputDTO) {
        return xdmMediator.selectProfileMdn(inputDTO);
    }

    @Override
    public IXDMResponseDTO createUserProfile(IXDMRequestDTO requestDTO){
        return xdmMediator.createUserProfile(requestDTO);
    }

    @Override
    public IXDMResponseDTO updateUserProfile(IXDMRequestDTO requestDTO){
        return xdmMediator.updateUserProfile(requestDTO);
    }

    @Override
    public IXDMResponseDTO deleteUserProfile(IXDMRequestDTO requestDTO){
        return xdmMediator.deleteUserProfile(requestDTO);
    }

    @Override
    public IXDMResponseDTO getUserProfileDetails(IXDMRequestDTO requestDTO){
        return xdmMediator.getUserProfileDetails(requestDTO);
    }

    @Override
    public IXDMResponseDTO getUserProfileList(IXDMRequestDTO requestDTO){
        return xdmMediator.getUserProfileList(requestDTO);
    }

    @Override
    public IXDMResponseDTO getUserProfileListByName(IXDMRequestDTO requestDTO){
        return xdmMediator.getUserProfileListByName(requestDTO);
    }

    @Override
    public IXDMResponseDTO getSubscriberUserProfileList(IXDMRequestDTO requestDTO){
        return xdmMediator.getSubscriberUserProfileList(requestDTO);
    }

    @Override
    public IXDMResponseDTO assignUserProfile(IXDMRequestDTO requestDTO){
        return xdmMediator.assignUserProfile(requestDTO);
    }

    @Override
    public IXDMResponseDTO unassignUserProfile(KnMessage message){
        return xdmMediator.unassignUserProfile(message);
    }

    @Override
    public IXDMResponseDTO getUserProfileSubscriberList(IXDMRequestDTO requestDTO) {
        return xdmMediator.getUserProfileSubscriberList(requestDTO);
    }

    @Override
    public IXDMResponseDTO updateDefaultprofile(IXDMRequestDTO requestDTO) {
        return xdmMediator.updateDefaultprofile(requestDTO);
    }
	
	@Override
	public IXDMResponseDTO createDevice(IXDMRequestDTO requestDTO) {
		String methodName = "createDevice(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.createDevice(requestDTO);
        return respDto;
	}

	@Override
	public IXDMResponseDTO getDeviceInfo(IXDMRequestDTO requestDTO) {
		String methodName = "getDeviceInfo(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getDeviceInfo(requestDTO);
        return respDto;
	}

	@Override
	public IXDMResponseDTO deleteDeviceInfo(IXDMRequestDTO requestDTO) {
		String methodName = "deleteDeviceInfo(requestDTO)";
        knLogger.debug(methodName, "Entry"); 
        IXDMResponseDTO respDto = xdmMediator.deleteDeviceInfo(requestDTO);		   
        return respDto;
	}

    @Override
    public IXDMResponseDTO modifyDevice(IXDMRequestDTO requestDTO) {
        String methodName = "modifyDevice(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.modifyDevice(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO loginNotifyEvent(IXDMRequestDTO requestDTO) {
        return xdmMediator.loginNotifyEvent(requestDTO);
    }

    @Override
    public IXDMResponseDTO getSubsGroupMembershipDetails(IXDMRequestDTO requestDTO) {
        String methodName = "getSubsGroupMembershipDetails(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getSubsGroupMembershipDetails(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO createBulkCorpGroup(IXDMRequestDTO requestDTO) {
        String methodName = "createBulkCorpGroup(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.createBulkCorpGroup(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getProfileGroupList(IXDMRequestDTO requestDTO) {
        String methodName = "getProfileGroupList(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getProfileGroupList(requestDTO);
        return respDto;
    }

	@Override
	public IXDMResponseDTO createGroupProfile(IXDMRequestDTO requestDTO) {
		String methodName = "createGroupProfile(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.createGroupProfile(requestDTO);
        return respDto;
	}

	@Override
	public IXDMResponseDTO getGroupProfileList(IXDMRequestDTO requestDTO) {
		String methodName = "getGroupProfileList(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getGroupProfileList(requestDTO);
        return respDto;
	}

	@Override
	public IXDMResponseDTO getGroupProfileDetails(IXDMRequestDTO requestDTO) {
		String methodName = "getGroupProfileDetails(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getGroupProfileDetails(requestDTO);
        return respDto;
	}

    @Override
    public IXDMResponseDTO searchGroupProfile(IXDMRequestDTO requestDTO) {
        String methodName = "searchGroupProfile(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.searchGroupProfile(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO createCorpGroupWithProfile(KnMessage message) {
        String methodName = "createCorpGroupWithProfile(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.createCorpGroupWithProfile(message);
        return respDto;
    }

    @Override
    public IXDMResponseDTO modifyGroupProfile(KnMessage message) {
        String methodName = "modifyGroupProfile(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.modifyGroupProfile(message);
        return respDto;
    }

    @Override
    public IXDMResponseDTO deleteBulkCorpGroup(KnMessage message) {
        String methodName = "deleteBulkCorpGroup(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.deleteBulkCorpGroup(message);
        return respDto;
    }

    @Override
    public IXDMResponseDTO deleteGroupProfile(IXDMRequestDTO requestDTO) {
        String methodName = "deleteGroupProfile(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.deleteGroupProfile(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getAuthorizedUserList(IXDMRequestDTO inputDTO) {
        return xdmMediator.getAuthorizedUserList(inputDTO);
    }

    @Override
    public IXDMResponseDTO getSharedCorpTrustMatrix(IXDMRequestDTO requestDTO) {
        String methodName="getSharedCorpTrustMatrix(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getSharedCorpTrustMatrix(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO updateCorpTrustMatrix(IXDMRequestDTO requestDTO) {
        String methodName = "updateCorpTrustMatrix(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.updateCorpTrustMatrix(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO deleteCorpTrustMatrix(IXDMRequestDTO requestDTO) {
        String methodName = "deleteCorpTrustMatrix(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.deleteCorpTrustMatrix(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO sendTrkMaterial(IXDMRequestDTO requestDTO) {
        String methodName="sendTrkMaterial(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.sendTrkMaterial(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getAsyncOpStatus(IXDMRequestDTO requestDTO) {
        String methodName="getAsyncOpStatus(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getAsyncOpStatus(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getUserprofileidsByProfileMdns(IXDMRequestDTO requestDTO) {
        String methodName="getUserprofileidsByProfileMdns(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getUserprofileidsByProfileMdns(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getSubscrClientSettings(IXDMRequestDTO requestDTO) {
        String methodName="getSubscrClientSettings(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getSubscrClientSettings(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO setSubscrClientSettings(KnMessage message) {
        String methodName="setSubscrClientSettings(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.setSubscrClientSettings(message);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getMdnAuthorizationForGroupId(IXDMRequestDTO requestDTO) {
        String methodName="getMdnAuthorizationForGroupId(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getMdnAuthorizationForGroupId(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO modifyGroupsUGWConfig(KnMessage message) {
        String methodName="modifyGroupsUGWConfig(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.modifyGroupsUGWConfig(message);
        return respDto;
    }
    @Override
    public IXDMResponseDTO getGroupsUGWConfig(IXDMRequestDTO requestDTO) {
        String methodName="modifyGroupsUGWConfig(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getGroupsUGWConfig(requestDTO);
        return respDto;
    }
    @Override
    public IXDMResponseDTO assignCommonContactList(KnMessage message) {
        String methodName="assignCommonContactList(KnMessage)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.assignCommonContactList(message);
        return respDto;
    }
    @Override
    public IXDMResponseDTO unAssignCommonContactList(KnMessage message) {
        String methodName="unAssignCommonContactList(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.unAssignCommonContactList(message);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getDeviceDetails(IXDMRequestDTO requestDTO) {
        String methodName="getDeviceDetails(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getDeviceDetails(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getSubscriberStats(IXDMRequestDTO requestDTO) {
        String methodName="getSubscriberStats(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getSubscriberStats(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getDeviceList(IXDMRequestDTO requestDTO) {
        String methodName="getDeviceList(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getDeviceList(requestDTO);
        return respDto;
    }
    @Override
    public IXDMResponseDTO getGroupStats(IXDMRequestDTO requestDTO) {
        String methodName="getGroupStats(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getGroupStats(requestDTO);
        return respDto;
    }
    @Override
    public IXDMResponseDTO getDeviceStats(IXDMRequestDTO requestDTO) {
        String methodName="getDeviceStats(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getDeviceStats(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO getCorporateFS(IXDMRequestDTO requestDTO) {
        String methodName = "getCorporateFS(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getCorporateFS(requestDTO);
        return respDto;
    }

    @Override
    public IXDMResponseDTO updateCorporateFS(IXDMRequestDTO requestDTO) {
        String methodName = "updateCorporateFS(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.updateCorporateFS(requestDTO);
        return respDto;
    }

    public IXDMResponseDTO modifyBulkGroupProperties(IXDMRequestDTO groupInfoDTO) {
        String methodName = "modifyBulkGroupProperties(groupInfoDTO)";
        knLogger.debug(methodName, "Modify Bulk Group Properties.");
        IXDMResponseDTO respDto = xdmMediator.modifyBulkGroupProperties(groupInfoDTO);
        return respDto;
    }

    public IXDMResponseDTO createCorpAccount(IXDMRequestDTO requestDTO) {
        IXDMResponseDTO respDto = xdmMediator.createCorpAccount(requestDTO);
        return respDto;
    }
    @Override
    public IXDMResponseDTO updateCorpAccount(IXDMRequestDTO requestDTO) {
        IXDMResponseDTO respDto = xdmMediator.updateCorpAccount(requestDTO);
        return respDto;
    }
    @Override
    public IXDMResponseDTO deleteCorpAccount(IXDMRequestDTO requestDTO) {
        IXDMResponseDTO respDto = xdmMediator.deleteCorpAccount(requestDTO);
        return respDto;
    }
    @Override
    public IXDMResponseDTO getCorporateAccountDetails(IXDMRequestDTO requestDTO) {
        String methodName = "getCorporateAccountDetails(requestDTO)";
        knLogger.debug(methodName, "Entry");
        IXDMResponseDTO respDto = xdmMediator.getCorporateAccountDetails(requestDTO);
        return respDto;
    }
    @Override
    public IXDMResponseDTO getCorporateAccountsList(IXDMRequestDTO requestDTO) {
        String methodName = "getCorporateAccountsList";
        knLogger.debug(methodName, "getCorporateAccountsList");
        IXDMResponseDTO respDto = xdmMediator.getCorporateAccountsList(requestDTO);
        return respDto;
    }


    @Override
    public IXDMResponseDTO getGroupsDetailsWithoutMembers(IXDMRequestDTO requestDTO) {
        String methodName="getGroupsDetailsWithoutMembers(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.getGroupsDetailsWithoutMembers(requestDTO);
    }

    @Override
    public IXDMResponseDTO cloneContactsGroupsAndFeatures(KnMessage message) {
        String methodName = "cloneContactsGroupsAndFeatures(IXDMRequestDTO)";
        return xdmMediator.cloneContactsGroupsAndFeatures(message);
    }

    public IXDMResponseDTO setCATAccessPermission(IXDMRequestDTO requestDTO) {
        String methodName="setCATAccessPermission(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.setCATAccessPermission(requestDTO);
    }
    @Override
    public IXDMResponseDTO getExtGWProfileList(IXDMRequestDTO requestDTO) {
        String methodName="getExtGWProfileList(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.getExtGWProfileList(requestDTO);
    }

    @Override
    public IXDMResponseDTO deleteHierarchy(IXDMRequestDTO requestDTO) {
        String methodName="deleteHierarchy(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.deleteHierarchy(requestDTO);
    }

    @Override
    public IXDMResponseDTO createHierarchy(IXDMRequestDTO requestDTO) {
        String methodName="createHierarchy(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.createHierarchy(requestDTO);
    }

    @Override
    public IXDMResponseDTO modifyHierarchy(IXDMRequestDTO requestDTO) {
        String methodName="modifyHierarchy(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.modifyHierarchy(requestDTO);
    }

    @Override
    public IXDMResponseDTO allocateSubs(IXDMRequestDTO requestDTO) {
        String methodName = "allocateSubs(authRequestDTO)";
        knLogger.debug(methodName, "allocateSubs");
        return xdmMediator.allocateSubs(requestDTO);
    }

    @Override
    public IXDMResponseDTO unAllocateSubs(IXDMRequestDTO requestDTO) {
        String methodName = "UnAllocateSubs(authRequestDTO)";
        knLogger.debug(methodName, "UnAllocateSubs");
        return xdmMediator.unAllocateSubs(requestDTO);
    }


    @Override
    public IXDMResponseDTO createPTTSettingDoc(IXDMRequestDTO requestDTO) {
        String methodName="createPTTSettingDoc(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.createPTTSettingDoc(requestDTO);
    }
    @Override
    public IXDMResponseDTO getPTTSettingDocList(IXDMRequestDTO requestDTO) {
        String methodName="getPTTSettingDocList(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.getPTTSettingDocList(requestDTO);
    }
    @Override
    public IXDMResponseDTO getPTTSettingDoc(IXDMRequestDTO requestDTO) {
        String methodName="getPTTSettingDoc(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.getPTTSettingDoc(requestDTO);
    }
    @Override
    public IXDMResponseDTO setDefaultPttSettingDoc(IXDMRequestDTO requestDTO) {
        String methodName="setDefaultPttSettingDoc(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.setDefaultPttSettingDoc(requestDTO);
    }
    @Override
    public IXDMResponseDTO deletePTTSettingDoc(IXDMRequestDTO requestDTO) {
        String methodName="deletePTTSettingDoc(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.deletePTTSettingDoc(requestDTO);
    }
    @Override
    public IXDMResponseDTO assignPttSettingToHierarchy(IXDMRequestDTO requestDTO) {
        String methodName="assignPttSettingToHierarchy(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.assignPttSettingToHierarchy(requestDTO);
    }
    @Override
    public IXDMResponseDTO unassignPttSettingToHierarchy(IXDMRequestDTO requestDTO) {
        String methodName="unassignPttSettingToHierarchy(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.unassignPttSettingToHierarchy(requestDTO);
    }

    @Override
    public IXDMResponseDTO assignPttSettingDocToMdns(IXDMRequestDTO requestDTO) {
        String methodName="assignPttSettingDocToMdns(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.assignPttSettingDocToMdns(requestDTO);
    }

    @Override
    public IXDMResponseDTO unassignPttSettingDocToMdns(IXDMRequestDTO requestDTO) {
        String methodName="unassignPttSettingDocToMdns(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.unassignPttSettingDocToMdns(requestDTO);
    }

    @Override
    public IXDMResponseDTO getPttSettingDocMdnList(IXDMRequestDTO requestDTO) {
        String methodName="getPttSettingDocMdnList(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.getPttSettingDocMdnList(requestDTO);
    }

    @Override
    public IXDMResponseDTO getMDNCountForPttSettingDocID(IXDMRequestDTO requestDTO) {
        String methodName="getMDNCountForPttSettingDocID(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.getMDNCountForPttSettingDocID(requestDTO);
    }
    @Override
    public IXDMResponseDTO assignPttSettingToCorp(IXDMRequestDTO requestDTO) {
        String methodName="assignPttSettingToCorp(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.assignPttSettingToCorp(requestDTO);
    }
    @Override
    public IXDMResponseDTO unassignPttSettingToCorp(IXDMRequestDTO requestDTO) {
        String methodName="unassignPttSettingToCorp(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.unassignPttSettingToCorp(requestDTO);
    }
    @Override
    public IXDMResponseDTO modifyPTTSettingTemplate(IXDMRequestDTO requestDTO) {
        String methodName="modifyPTTSettingTemplate(requestDTO)";
        knLogger.debug(methodName, "Entry");
        return xdmMediator.modifyPTTSettingTemplate(requestDTO);
    }
    @Override
    public IXDMResponseDTO groupRehome(KnMessage message) {
        String methodName = "groupRehome(message)";
        knLogger.debug(methodName, "Group Rehome.");
        IXDMResponseDTO respDto = xdmMediator.groupRehome(message);
        return respDto;
    }

    /**
     * Method to retrieve regions (geocodes) for a hierarchy within a corporation.
     * Delegates to mediator layer for transaction management and business logic.
     *
     * @param requestDTO IXDMRequestDTO containing corpId and hierarchyId
     * @return IXDMResponseDTO containing list of geocodes
     */
    public IXDMResponseDTO getRegions(IXDMRequestDTO requestDTO) {
        String methodName = "getRegions(IXDMRequestDTO)";
        knLogger.debug(methodName, "Get Regions with request object - ", requestDTO);
        // Delegate to mediator layer
        return xdmMediator.getRegions(requestDTO);
    }

}
