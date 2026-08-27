/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpActivationInfoUtil.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Acharyya        30-11-2011      7.2
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

package com.kodiak.xdms.server.corpmgmt.business.helper;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.common.KnActivationCodeConfigDTO;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.sql.Timestamp;
import java.util.*;
import java.security.SecureRandom;

public class KnCorpActivationInfoUtil {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpActivationInfoUtil.class);

    private static final String CLASS = KnCorpActivationInfoUtil.class.getName();
    private static Map<String, Integer> corpActCodeMap = new HashMap<String, Integer>();

    public void insetActivationCode(String mdn, String activationCode, Timestamp expiryTime, String
            pttServerId, KnPersisterTxn persisterTxn, int clientType, Timestamp currentTimeInUTC) throws KnCorpBOException {
        String methodName = "insetActivationCode(mdn, activationCode, expiryTime, pttServerId, persisterTxn)";
        knLogger.debug( methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn), ", activationCode - ", activationCode);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(pttServerId);
            xdmDAO.insetActivationCode(mdn, activationCode, expiryTime, persisterTxn, clientType, currentTimeInUTC);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while insetActivationCode", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpSubscriberDTO> insertActivationCode(Collection<KnCorpSubscriberDTO> subsList, String serviceName, String
            pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "insertActivationCode(Collection, pttServerId, persisterTxn)";
        knLogger.debug( methodName, "ENTRY : subsList - ", subsList );
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(pttServerId);
            return xdmDAO.insertActivationCode(subsList,serviceName, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateActivationCode(String mdn, String activationCode, Timestamp expiryTime, String
            pttServerId, KnPersisterTxn persisterTxn, int clientType, Timestamp currentTimeInUTC) throws KnCorpBOException {
        String methodName = "updateActivationCode(mdn, activationCode, expiryTime, pttServerId, persisterTxn)";
        knLogger.debug( methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn), ", activationCode - ", activationCode);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(pttServerId);
            xdmDAO.updateActivationCode(mdn, activationCode, expiryTime, persisterTxn, clientType, currentTimeInUTC);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updateActivationCode", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpSubscriberDTO> updateActivationCode(Collection<KnCorpSubscriberDTO> subsList, String
            pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "updateActivationCode(Collection<KnCorpSubscriberDTO>, pttServerId, persisterTxn)";
        knLogger.debug( methodName, "ENTRY : subsList - ", subsList.toString());
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(pttServerId);
            return xdmDAO.updateActivationCode(subsList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updateActivationCode", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public boolean isActivationCodeExist(String mdn, String xdmsHomePttId, KnPersisterTxn persisterTxn, int clientType) throws KnCorpBOException {
        String methodName = "isActivationCodeExist(mdn, xdmsHomePttId, persisterTxn)";
        knLogger.debug( methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn));
        boolean status = false;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHomePttId);
            status = xdmDAO.isActivationCodeExist(mdn, persisterTxn, clientType);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getIdsVasKeyValidity", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug( methodName, "Return status - ", status);
        return status;
    }

    public Set<String> isActivationCodeExistInDB(Set<String> activationCode, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "isActivationCodeExistInDB(mdn, xdmsHomePttId, boolean, persisterTxn)";
        knLogger.debug( methodName, "ENTRY : activationCode - ", activationCode);

        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHomePttId);
            return xdmDAO.isActivationCodeExistInDB(activationCode, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getIdsVasKeyValidity", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpSubscriberDTO> isActivationCodeExistForMDN(Collection<KnCorpSubscriberDTO> activationList, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "isActivationCodeExistForMDN(activationList, xdmsHomePttId, boolean, persisterTxn)";
        knLogger.debug(methodName, "ENTRY : activationList - ", activationList);
        Collection<KnCorpSubscriberDTO> subsList;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHomePttId);
            subsList = xdmDAO.isActivationCodeExistForMDN(activationList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getIdsVasKeyValidity", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug( methodName, "Return subsList - ", subsList);
        return subsList;
    }

    public  Map<Integer, Timestamp> generateExpiryTime(Map<Integer, KnActivationCodeConfigDTO> activateCodeCofigMap) {
        String methodName = "generateExpiryTime(activateCodeCofigMap)";
        knLogger.debug( methodName, "ENTRY : activateCodeCofigMap - ", activateCodeCofigMap);
        Map<Integer, Timestamp> timestampMap = new HashMap<>(activateCodeCofigMap.size());
        Calendar calendar = Calendar.getInstance();
        long currentMilliSeconds = calendar.getTimeInMillis();
        for(int clientType : activateCodeCofigMap.keySet()){
            long configuedExpTime = activateCodeCofigMap.get(clientType).getActCodeValidity();
            long configuredExpTimeInMilliSec = 1000l * 60 * 60 * 24 * configuedExpTime;
            long expiryTimeInMilisec = currentMilliSeconds + configuredExpTimeInMilliSec;
            knLogger.debug( methodName, "ExpiryTime in Milisecond - ", expiryTimeInMilisec);
            Timestamp ts = new Timestamp(expiryTimeInMilisec);
            timestampMap.put(clientType, ts);
        }
        knLogger.debug( methodName, "ExpiryTime Map - ", timestampMap);
        return timestampMap;
    }

    public Timestamp getCurrentTimeInUTC() {
        String methodName = "getCurrentTimeInUTC()";
        Calendar calendar = Calendar.getInstance();
        long currentMilliSeconds = calendar.getTimeInMillis();
        knLogger.debug( methodName, "Current time in MilliSeconds - ", currentMilliSeconds);
        Timestamp ts = new Timestamp(currentMilliSeconds);
        return ts;
    }

    public void updateSubsAuthStatusToProvisioningStat(String mdn, String xdmsHomePttId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "updateSubsAuthStatusToProvisioningStat(mdn, xdmsHomePttId, persisterTxn)";
        knLogger.debug( methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn));
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHomePttId);
            xdmDAO.updateSubsAuthStatusToProvisioningStat(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating subscribers auth status", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void invalidatePocUserPassword(String mdn, String xdmsHomePttId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "invalidatePocUserPassword(mdn, xdmsHomePttId, persisterTxn)";
        knLogger.debug( methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn));
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHomePttId);
            xdmDAO.invalidatePocUserPassword(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while invalidatePocUserPassword - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public Map<String, String> generateActivationCode(Collection<KnCorpSubscriberDTO> subscrDtolist, Map<Integer, KnActivationCodeConfigDTO>
            activationCodeConfigMap) throws  KnCorpBOException{
        String methodName = "generateActivationCode(Collection<KnCorpSubscriberDTO>, Map<Integer, KnActivationCodeConfigDTO>)";
        knLogger.debug( methodName, "ENTRY : ");
        SecureRandom random = new SecureRandom();
        Map<String, String> activationCodeMap = new HashMap<>();
        for(KnCorpSubscriberDTO subscriberDTO : subscrDtolist){
            int itr = 0;
            int max_itr = 10;
            do{
                String mdn = subscriberDTO.getMdn();
                int clientType = subscriberDTO.getClientType();
                KnActivationCodeConfigDTO codeConfigDTO = activationCodeConfigMap.get(clientType);
                String code = generateRandom(codeConfigDTO.getActCodeType(), random, codeConfigDTO.getActCodeLength());
               if(!activationCodeMap.containsValue(code)){
                   activationCodeMap.put(mdn, code);
                   break;
               }
                itr ++;
            }while(itr < max_itr);
        }
        if(subscrDtolist.size() != activationCodeMap.size()){
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Duplicate activation code generated even after 10 times retry");
        }
        knLogger.debug( methodName, "Exit : activationCodeMap - ", activationCodeMap);
        return activationCodeMap;

    }

    public String generateRandom(int codeType, SecureRandom random, int codeLength) {
            String actCode;
            String START_RANGE = "1";
            String END_RANGE = "9";
            String STARTRANGE = "1";
            String ENDRANGE = "9";
            if (codeType == 2) {
                //Generating alpha numeric activation code
                actCode = getRandomStr(codeLength);
            } else {
                if (codeLength > 18) {
                    int length1 = codeLength - 18;
                    int length = codeLength - length1;
                    StringBuilder buffer1 = new StringBuilder();
                    StringBuilder buffer2 = new StringBuilder();
                    for (int j = 1; j < length; j++) {
                        buffer1.append(0);
                        buffer2.append(9);
                    }
                    STARTRANGE = STARTRANGE + buffer1.toString();
                    ENDRANGE = ENDRANGE + buffer2.toString();
                    StringBuilder buffer3 = new StringBuilder();
                    StringBuilder buffer4 = new StringBuilder();
                    for (int k = 1; k < length1; k++) {
                        buffer3.append(0);
                        buffer4.append(9);
                    }
                    START_RANGE = START_RANGE + buffer3.toString();
                    END_RANGE = END_RANGE + buffer4.toString();
                    String id1 = createRandomNumber(Long.parseLong(STARTRANGE),
                            Long.parseLong(ENDRANGE), random);
                    String id2 = createRandomNumber(Long.parseLong(START_RANGE),
                            Long.parseLong(END_RANGE), random);
                    actCode = id1 + id2;
                } else {
                    StringBuilder buffer5 = new StringBuilder();
                    StringBuilder buffer6 = new StringBuilder();
                    for (int j = 1; j < codeLength; j++) {
                        buffer5.append(0);
                        buffer6.append(9);
                    }
                    STARTRANGE = STARTRANGE + buffer5.toString();
                    ENDRANGE = ENDRANGE + buffer6.toString();
                    actCode = createRandomNumber(Long.parseLong(STARTRANGE),
                            Long.parseLong(ENDRANGE), random);
                }
            }
        return actCode;
    }

    public String createRandomNumber(long aStart, long aEnd, SecureRandom aRandom) {
        String methodName = "createRandomNumber(long, long, int,Random)";
        knLogger.debug( methodName, "ENTRY : aStart - ", aStart, "aEnd - ", aEnd);
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        //get the range, casting to long to avoid overflow problems
        long range = aEnd - (long) aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * aRandom.nextDouble());
        long randomNumber = fraction + (long) aStart;
        String finalStr = "" + randomNumber;
        knLogger.debug( methodName, "Exit : finalStr - ", finalStr);
        return finalStr;
    }


    public String getRandomStr(int len) {
        SecureRandom random = new SecureRandom();
        char ahplaCh[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        char numCh[] = {'1', '2', '3', '4', '5', '6', '7','8', '9', '0' };
        char seq[] = new char[len];
        for(int i =0 ; i< len; i++){
            seq[i] = 1;
        }
        for(int i =0;i<3;i++){
            int index = random.nextInt(len);
            seq[index] = 2;
        }
       for (int i =0 ; i< len; i++) {
            if(seq[i] == 1){
                int index = random.nextInt(51);
                seq[i] = ahplaCh[index];
            }else{
                int index = random.nextInt(9);
                seq[i] = numCh[index];
            }
        }
        return new String(seq);
    }

    /***
     * This method is to return the activation code generation and expiry time for a subscribers.
     * @param mdn
     * @param clientType
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public KnCorpSubscriberDTO getActCodeExtTime(String mdn, int clientType, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getActCodeExtTime(String, String,boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY : ");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(pttServerId);
            return xdmDAO.getActCodeExtTime(mdn, clientType, readOnly, persisterTxn);

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getIdsVasKeyValidity", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpSubscriberDTO> getDuplicateCodeMdnList(Collection<KnCorpSubscriberDTO> subscProfile, Map<String, String>
            activationCodeMap, Set<String> existingActCodeList) {
        String methodName = "getDuplicateCodeMdnList(Collection<KnCorpSubscriberDTO>, Map<String, String>, Set<String>)";
        knLogger.debug( methodName, "ENTRY : ");
        List<String> mdnList = new ArrayList<>(existingActCodeList.size());
        Collection<KnCorpSubscriberDTO> duplicateCodeMdnList = new ArrayList<>(existingActCodeList.size());
        for(Map.Entry<String, String> entry : activationCodeMap.entrySet()){
            String code = entry.getValue();
            if(existingActCodeList.contains(code)){
                mdnList.add(entry.getKey());
            }
        }
        for(KnCorpSubscriberDTO subscriberDTO : subscProfile){
           if(mdnList.contains(subscriberDTO.getMdn())){
               duplicateCodeMdnList.add(subscriberDTO);
           }
        }
        knLogger.debug( methodName, "Exit : ", duplicateCodeMdnList.toString());
        return duplicateCodeMdnList;
    }

    public Collection<String> activationCodeExistForMDNs(Collection<String> mdnList,String serviceName, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "activationCodeExistForMDNs(mdnList, xdmsHomePttId, persisterTxn)";
        knLogger.debug( methodName, "ENTRY : mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList)," serviveName - ",serviceName);
        Collection<String> presentMdnList;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHomePttId);
            presentMdnList = xdmDAO.activationCodeExistForMDNs(mdnList, serviceName, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getIdsVasKeyValidity", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug( methodName, "Return mdnList - ", KnGDPRTemplate.mdnList(presentMdnList));
        return presentMdnList;
    }

    public void deleteActivationCode(Collection<String> mdns, String serviceName, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteActivationCode(mdns,serviceName xdmsHomePttId, persisterTxn)";
        knLogger.debug( methodName, "ENTRY : mdns - ", mdns == null ? mdns : KnGDPRTemplate.mdnList(mdns));
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHomePttId);
            xdmDAO.deleteActivationCode(mdns, serviceName, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getIdsVasKeyValidity", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public String getSubscribersOTP(String mdn, String serviceName, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getSubscribersOTP(mdn,serviceName xdmsHomePttId, persisterTxn)";
        knLogger.debug( methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn));
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHomePttId);
            return xdmDAO.getSubscribersOTP(mdn, serviceName, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getIdsVasKeyValidity", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteActivationCodeForMDN(String mdn, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteActivationCodeForMDN(mdns, xdmsHomePttId, persisterTxn)";
        knLogger.debug( methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn));
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHomePttId);
            xdmDAO.deleteActivationCodeForMDN(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getIdsVasKeyValidity", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteActivationCodeForMDN(List<String> mdnList, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteActivationCodeForMDN(mdns, xdmsHomePttId, persisterTxn)";
        knLogger.debug( methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdnList(mdnList));
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHomePttId);
            xdmDAO.deleteActivationCodeForMDN(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getIdsVasKeyValidity", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
}
