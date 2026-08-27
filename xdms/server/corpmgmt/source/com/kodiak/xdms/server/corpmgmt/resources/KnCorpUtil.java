/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpUtil.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 29, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.resources;

import com.kodiak.common.dao.KnPersisterTxn;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSublistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.security.SecureRandom;


public class KnCorpUtil {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpUtil.class);

    private static SecureRandom randomGen = new SecureRandom();

    /**
     * This method checks if the String passed to the method is Null or empty
     *
     * @param value String
     * @return result boolean
     */
    public static boolean isObjectNull(Object value) {
        String methodName = "isObjectNull(Object)";
        boolean result = KnConstants.FALSE;
        if (null == value) {
            result = KnConstants.TRUE;
        }
        return result;
    }

    /**
     * This method checks if the Collection passed to the method is Null or empty
     *
     * @param collection Collection
     * @return result boolean
     */
    public static boolean isObjectNullOrEmpty(Collection collection) {
        String methodName = "isObjectNullOrEmpty(Collection)";
        boolean result = KnConstants.FALSE;
        if (null == collection || collection.isEmpty()) {
            result = KnConstants.TRUE;
        }
        return result;
    }

    /**
     * This method checks if the Collection passed to the method is Null or empty
     *
     * @param map Collection
     * @return result boolean
     */
    public static boolean isObjectNullOrEmpty(Map map) {
        String methodName = "isObjectNullOrEmpty(Collection)";
        boolean result = KnConstants.FALSE;
        if (null == map || map.isEmpty()) {
            result = KnConstants.TRUE;
        }
        return result;
    }

    /**
     * This method checks if the String passed to the method is Null or empty
     *
     * @param value String
     * @return result boolean
     */
    public static boolean isNullOrEmpty(String value) {
        String methodName = "isNullOrEmpty(String)";
        boolean result = KnConstants.FALSE;
        if (null == value) {
            result = KnConstants.TRUE;
        } else if ("".equalsIgnoreCase(value.trim())) {
            result = KnConstants.TRUE;
        }
        return result;
    }

    public static String formCommaSeperatedIdList(Collection<String> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        for (String mdn : collectionStr) {
            buffer = buffer.append("'").append(mdn).append("',");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }

    public static String formCommaSeperatedIdListQuotes(Collection<String> collectionStr) {
        String methodName = "formCommaSeperatedIdListQuotes";
        StringBuffer buffer = new StringBuffer(200);
        char quote = '\'';
        for (String str : collectionStr) {
            if(str.contains("'")){
                char[] ch = str.toCharArray();
                buffer.append("'");
                for (char c : ch) {
                    buffer.append(c);
                    if(c == quote){
                        buffer.append("'");
                    }
                }
                buffer.append("',");
            }else {
                buffer = buffer.append("'").append(str).append("',");
            }
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        knLogger.debug(methodName,"Query Returned :" , buffer.toString());
        return buffer.toString();
    }

    public static String formCommaSeperatedQuesMarks(Collection<String> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        for (String mdn : collectionStr) {
            buffer = buffer.append("?").append(",");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }

    public static String formCommaSeperatedIntegerQuesMarks(Collection<Integer> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        for (Integer mdn : collectionStr) {
            buffer = buffer.append("?").append(",");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }

    public static String formCommaSeperatedIdListForNVarChar(Collection<String> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        for (String mdn : collectionStr) {
            buffer = buffer.append("n'").append(mdn).append("',");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }

    public static String formIntegerCommaSeperatedIdList(Collection<Integer> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        if (collectionStr == null || collectionStr.isEmpty()) {
            return "";
        }
        for (Integer str : collectionStr) {
            buffer = buffer.append(str).append(",");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }

    public static String formIntegerCommaSeperatedIdListString(Collection<String> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        if (collectionStr == null || collectionStr.isEmpty()) {
            return "";
        }
        for (String str : collectionStr) {
            buffer = buffer.append(str).append(",");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }

    public static int getMappedSubscriptionType(int publicSubscType, int corpSubscType) {

        String methodName = "getMappedSubscriptionType(publicSubscType, corpSubscType)";
        int subscriptionType = 0;
        knLogger.debug( methodName, "PiublicSubscType - " , publicSubscType , ", CorpSubscType - " , corpSubscType);
        if (publicSubscType == 1 && corpSubscType == 0) {
            subscriptionType = 0;
        } else if (publicSubscType == 0 && corpSubscType == 1) {
            subscriptionType = 1;
        } else if (publicSubscType == 1 && corpSubscType == 1) {
            subscriptionType = 2;
        } else {
            knLogger.debug( methodName, "Unknown type, mapping to default type - 0");
        }
        return subscriptionType;
    }

    public static int getRandomNumber() {

        return randomGen.nextInt(1000);//
    }

    public static String getPstmtString(Collection<String> colectionStr) {

        StringBuffer sb = new StringBuffer(100);
        for (int i = 1; i < 10; i++) {
            sb = sb.append(i).append("','");
        }
        int count = sb.lastIndexOf("','");
        System.out.println(sb.toString());
        String str = sb.substring(0, count);
        System.out.println("Str - " + str);
        return str;
    }

    public static int[] convertStringToIntArray(String str, String delimiter) {
        int[] retArray;
        String[] values = str.split(delimiter);
        retArray = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            retArray[i] = Integer.parseInt(values[i].trim());
        }
        return retArray;
    }

    public static List<Integer> convertStringToIntList(String str, String delimiter) {
        String[] values = str.split(delimiter);
        List<Integer> list = new ArrayList<>(values.length);
        for (int i = 0; i < values.length; i++) {
            list.add(Integer.parseInt(values[i].trim()));
        }
        return list;
    }

    /**
     * Rollback the transaction
     *
     * @param txn transaction object
     */
    public static void rollback(KnPersisterTxn txn) {
        if (txn != null) {
            try {
                txn.rollback();

            } catch (Exception e) {
                knLogger.error( "rollback(txn)", "Failed to rollback the transaction - " , e);
            }
        }
    }

    public static void close(Statement stmt) {

        String methodName = "close(stmt)";
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                knLogger.error( methodName, "Failed to close stmt - " , e);
            }
        }
    }

    public static void close(PreparedStatement pstmt) {

        String methodName = "close(pstmt)";
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                knLogger.error( methodName, "Failed to close pstmt - " , e);
            }
        }
    }

    public static void close(ResultSet rs, Statement stmt) {

        String methodName = "close(rs, stmt)";
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                knLogger.error( methodName, "Failed to close ResultSet - " , e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                knLogger.error( methodName, "Failed to close stmt - " , e);
            }
        }
    }

    public static void close(ResultSet rs, PreparedStatement pstmt) {

        String methodName = "close(rs, pstmt)";
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                knLogger.error( methodName, "Failed to close ResultSet - " , e);
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                knLogger.error( methodName, "Failed to close pstmt - " , e);
            }
        }
    }

    public static Collection<Integer> formSublistIdsListFrmPersistDto(Collection<KnCorpSublistDTO> dbSublistList) {
        Collection<Integer> sublistIdList = new ArrayList<Integer>();
        for (KnCorpSublistDTO sublist : dbSublistList) {
            sublistIdList.add(sublist.getSublistId());
        }
        return sublistIdList;
    }


    public static String replaceContactWithValue(String str, String constant, String value) {
        String finalStr = "";
        if (str == null || str.trim().equals("")) {
            return finalStr;
        }
        finalStr = str.replaceAll(constant, value);
        return finalStr;
    }

    public static String replaceStringValue(String str, String constant, String value) {
        String finalStr = "";
        if (str == null || str.trim().equals("")) {
            return finalStr;
        }
        finalStr = str.replace(constant, value);
        return finalStr;
    }

    public static Map<String, KnCorpSubscriberDTO> filterOutInternalSubscribersAndSetSubscCorpId
            (Map<String, KnCorpSubscriberDTO> contacts, KnIPCorpContactListDTO contactListDTO, int corpId) {
        Collection<KnCorpSubscriberDTO> contactList = contactListDTO.getContactList();
        for (KnCorpSubscriberDTO subscriber : contactList) {
            String mdn = subscriber.getMdn();
            KnCorpSubscriberDTO external = contacts.get(mdn);
            int externalCorpId = external.getCorpId();
            if (corpId != externalCorpId) {
                //contacts.remove(mdn);
                 subscriber.setCorpId(externalCorpId);
            }
        }
        return contacts;
    }

    public static int determineMaxContactLimitFlag(int totalContacts, int maxContactsPerSubsc) {
        int flag = 0;
        if (totalContacts < maxContactsPerSubsc) {
            flag = -1;
        } else if (totalContacts == maxContactsPerSubsc) {
            flag = 0;
        } else if (totalContacts > maxContactsPerSubsc) {
            flag = 1;
        }
        return flag;
    }

    public static int mappGroupTypeToDB(int groupType) {
        int mappedGrpType = 0;
        switch (groupType){
            case 3:
                mappedGrpType = 2;
                break;
            case 2 :
                mappedGrpType = 1;
                break;
        }
        return mappedGrpType;
    }

    public static int mappGroupTypeToApp(int groupType) {
        int mappedGrpType = 1;
        switch (groupType){
            case 1:
                mappedGrpType = 2;
                break;
            case 2 :
                mappedGrpType = 3;
                break;
        }
        return mappedGrpType;
    }

    /**
     * This method return the start index based on nextToken and fetchSize values
     * @param fetchSize
     * @param nextToken
     * @return
     */
    public static int getStartIndex(int fetchSize, int nextToken) {
        return (nextToken * fetchSize) + 1;
    }

    /**
     * This method return the end index based on nextToken and fetchSize values
     * @param fetchSize
     * @param nextToken
     * @return
     */
    public static int getEndIndex(int fetchSize, int nextToken) {
        return (nextToken * fetchSize) + fetchSize;
    }
    /**
     * To Form in this format
     *select CORPGROUPID, CORPID FROM DG.CORPGROUPINFO where (CORPGROUPID, CORPID) IN ((?,?),(?,?),(?,?),(?,?),(?,?));
     * @param collectionStr
     * @return
     */
    public static String formCommaSeperatedBulkIntegerQuesMarks(Map<Integer, Integer> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        if (collectionStr == null || collectionStr.isEmpty()) {
            return  "";
        }
        for (var entry : collectionStr.entrySet()) {
            buffer = buffer.append("(").append("?").append(",").append("?").append(")").append(",");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }

    /**
     * Extracting loginHierarchyId from requestParam
     */
    @SuppressWarnings("unchecked")
    public static String getLoggedInHierarchy(Map<String, Object> customParams) {
        if (null == customParams) {
            return null;
        }
        Object idListObj = customParams.get(KnConstants.IDLIST);
        if (idListObj != null) {
            List<String> isList = (List<String>) idListObj;
            return !isList.isEmpty() ? isList.getFirst() : null;
        }
        return null;
    }
}
