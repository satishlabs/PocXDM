/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/****************************************************************************
 *
 * File name:   KnGeneralUtil.java
 * Subsystem:   Common
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         26-03-2007 6.0
 *
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 ***************************************************************************/
package com.kodiak.xdms.server.common.util;

import com.kodiak.common.commdto.common.KnUserAgentDTO;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.ggcache.dto.KnDefaultMCSClientInfo;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.xdms.server.common.KnXDMError;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.configuration.cache.KnCacheClient;
import com.kodiak.xdms.server.common.configuration.cache.ICacheManager;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheElement;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

import static com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue;
import static com.kodiak.xdms.server.common.resources.KnConstants.MCPTT_COMPLAIANCE_BIT;
import static com.kodiak.xdms.server.common.resources.KnConstants.VERY_LARGE_GROUP;

/**
 * This class provides some general utility methods.
 */
public class KnGeneralUtil {
	private static final KnLogger knLogger = KnLogger.getLogger(KnGeneralUtil.class);

    public static final String CLASS = KnGeneralUtil.class.getName();

    private static Map<String,String> auidMap=new HashMap<>();
    private  KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
    public KnGeneralUtil()
    {
    	 auidMap.put("mcvideo","MCVIDEOUSERPROFILE_AUID");
         auidMap.put("mcdata","MCDATAUSERPROFILE_AUID");
         auidMap.put("mcptt","MCPTT_UE_PROFILE_AUID");
    }


    /**
     * Return the object info.
     * The object info will be in the format <code>classname@hashcode</code>.
     * The class name will not include the package
     *
     * @param obj
     * @return Object info string
     */
    public static String getObjectInfo(Object obj) {
        if (obj == null) return "null";
        String className = obj.getClass().getName();
        int i = className.lastIndexOf('.') + 1;
        if (i >= 0 && i < className.length()) {
            className = className.substring(i);
        }
        return className + "@" + obj.hashCode();
    }

    /**
     * Convert a string array to string
     *
     * @param strArray
     * @return string
     */
    public static String convertArrayToString(String[] strArray) {
        if (strArray == null || strArray.length == 0) {
            return null;
        }
        StringBuffer buffer = new StringBuffer(500);
        buffer.append("{");
        for (int i = 0; i < strArray.length; i++) {
            buffer.append(strArray[i]);
            buffer.append(", ");
        }
        int len = buffer.length();
        buffer.delete(len - 2, len);
        buffer.append("}");
        return buffer.toString();
    }

    /**
     * Convert an int array to String
     *
     * @param intArray
     * @return String representation for int array
     */
    public static String convertArrayToString(int[] intArray) {
        if (intArray == null || intArray.length == 0) {
            return null;
        }
        StringBuffer buffer = new StringBuffer(500);
        buffer.append("{");
        for (int i = 0; i < intArray.length; i++) {
            buffer.append(intArray[i]);
            buffer.append(", ");
        }
        int len = buffer.length();
        buffer.delete(len - 2, len);
        buffer.append("}");
        return buffer.toString();
    }

    /**
     * Convert a boolean array to String
     *
     * @param boolArray
     * @return the String form of boolean array
     */
    public static String convertArrayToString(boolean[] boolArray) {
        if (boolArray == null || boolArray.length == 0) {
            return null;
        }
        StringBuffer buffer = new StringBuffer(500);
        buffer.append("{");
        for (int i = 0; i < boolArray.length; i++) {
            buffer.append(i).append(":").append(boolArray[i]);
            buffer.append(", ");
        }
        int len = buffer.length();
        buffer.delete(len - 2, len);
        buffer.append("}");
        return buffer.toString();
    }

    /**
     * This will convert a string of numbers separated by <code>delimiter</code> to integer array.
     * For eg,  "1,2,3" will be converted to {1, 2, 3}. The delimiter can be passed to the
     * method
     *
     * @param str       the string
     * @param delimiter the delimiter
     * @return integer array
     */
    public static int[] convertStringToIntArray(String str, String delimiter) {
        int[] retArray;
        String[] values = str.split(delimiter);
        retArray = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            retArray[i] = Integer.parseInt(values[i].trim());
        }
        return retArray;
    }

    /**
     * Parse the Feature bits and convert to a boolean array.
     *
     * @param featureStr the featureset as an integer
     * @return the boolean array
     */
    public static boolean[] parseFeatureset(String featureStr) {
        String methodName = "parseFeatureset";
        knLogger.debug( methodName, "Feature String : " + featureStr);
        boolean featureSetArr[] = new boolean[32];
        if (featureStr == null) return featureSetArr;
        int k = 0;
        try {
            for (int i = 0; i < featureStr.length(); i++) {
                if (featureStr.charAt(i) == '0') {
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = false;
                } else if (featureStr.charAt(i) == '1') {
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = true;
                } else if (featureStr.charAt(i) == '2') {
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = false;
                } else if (featureStr.charAt(i) == '3') {
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = true;
                } else if (featureStr.charAt(i) == '4') {
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = false;
                } else if (featureStr.charAt(i) == '5') {
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = true;
                } else if (featureStr.charAt(i) == '6') {
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = false;
                } else if (featureStr.charAt(i) == '7') {
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = true;
                } else if (featureStr.charAt(i) == '8') {
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = false;
                } else if (featureStr.charAt(i) == '9') {
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = true;
                } else if (featureStr.charAt(i) == 'A') {
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = false;
                } else if (featureStr.charAt(i) == 'B') {
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = true;
                } else if (featureStr.charAt(i) == 'C') {
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = false;
                } else if (featureStr.charAt(i) == 'D') {
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = false;
                    featureSetArr[k++] = true;
                } else if (featureStr.charAt(i) == 'E') {
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = false;
                } else if (featureStr.charAt(i) == 'F') {
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = true;
                    featureSetArr[k++] = true;
                }
                //System.out.print(Integer.toBinaryString(Character.digit(str_featureset.charAt(i),16)));
            }
        } catch (Exception exp) {
            knLogger.error( methodName, exp);
            return null;
        }
        boolean booleanArr[] = new boolean[featureSetArr.length];
        int i = 0;
        booleanArr[i++] = featureSetArr[7];
        booleanArr[i++] = featureSetArr[6];
        booleanArr[i++] = featureSetArr[5];
        booleanArr[i++] = featureSetArr[4];
        booleanArr[i++] = featureSetArr[3];
        booleanArr[i++] = featureSetArr[2];
        booleanArr[i++] = featureSetArr[1];
        booleanArr[i++] = featureSetArr[0];
        booleanArr[i++] = featureSetArr[15];
        booleanArr[i++] = featureSetArr[14];
        booleanArr[i++] = featureSetArr[13];
        booleanArr[i++] = featureSetArr[12];
        booleanArr[i++] = featureSetArr[11];
        booleanArr[i++] = featureSetArr[10];
        booleanArr[i++] = featureSetArr[9];
        booleanArr[i++] = featureSetArr[8];
        booleanArr[i++] = featureSetArr[23];
        booleanArr[i++] = featureSetArr[22];
        booleanArr[i++] = featureSetArr[21];
        booleanArr[i++] = featureSetArr[20];
        booleanArr[i++] = featureSetArr[19];
        booleanArr[i++] = featureSetArr[18];
        booleanArr[i++] = featureSetArr[17];
        booleanArr[i++] = featureSetArr[16];
        booleanArr[i++] = featureSetArr[31];
        booleanArr[i++] = featureSetArr[30];
        booleanArr[i++] = featureSetArr[29];
        booleanArr[i++] = featureSetArr[28];
        booleanArr[i++] = featureSetArr[27];
        booleanArr[i++] = featureSetArr[26];
        booleanArr[i++] = featureSetArr[25];
        booleanArr[i] = featureSetArr[24];
        return booleanArr;
    }

    /**
     * create the error object.
     *
     * @param errorCode the error code
     */
    public static KnXDMError createErrorObject(String errorCode) {
        knLogger.debug( "createErrorObject", "Error Code - " + errorCode);
        KnXDMError errorObj = null;
        String libraryName = null;
        try {
            String[] ids = errorCode.split("[.]");
            String id = ids[1];
            String orignators = ids[0];
            if (ids.length == 3) {
                // first element in the error-code will be the library name
                libraryName = ids[0];
                orignators = ids[1];
                id = ids[2];
            }
            KnCacheClient cacheClient = new KnCacheClient(KnErrorCodes.MODULE_NAME,
                    new String[]{"error-codes"}, id);
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance(libraryName);
            ICacheManager cacheManager = configManager.getCacheManager();
            KnCacheElement cacheElement = (KnCacheElement) cacheManager.get(cacheClient);
            if (cacheElement != null) {
                errorObj = new KnXDMError(id);
                errorObj.setErrorMessage(cacheElement.getAttribute("msg"));
                errorObj.setOriginator(orignators);
                errorObj.setErrorContext(cacheElement.getAttribute("error-context"));
                errorObj.setLevel(cacheElement.getAttribute("level"));
                errorObj.setCause(cacheElement.getAttribute("cause"));
            }
        } catch (Exception e) {
            knLogger.fatal( "createErrorObject", "Cant create Error object - " , e);
        }
        return errorObj;
    }


    public static String getFeatureSet(boolean[] biArray) {
        String methodName = "getFeatureSet(boolean[] biArray)";
        BitSet featureSet = null;
        try {
            featureSet = new BitSet(biArray.length);
            for (int i = 0; i < biArray.length; i++) {
                if (biArray[i]) {
                    knLogger.debug( methodName, "biArray[" + i + "]=" + biArray[i]);
                    featureSet.set(i);
                }
            }
            knLogger.debug( methodName, "Feature set" + featureSet);
        } catch (Exception exp) {
            knLogger.error( methodName, "Exception=" , exp);
        }
        return getString(featureSet, biArray); //featureSet.toString();
    }//getClientFeatureSet

    public static String getFeatureSet(boolean[] biArray, int bitPos, boolean value) {
        String methodName = "getFeatureSet(boolean[] biArray,int bitPos,boolean value)";
        BitSet featureSet = null;
        try {
            featureSet = new BitSet(biArray.length);
            for (int i = 0; i < biArray.length; i++) {
                if (biArray[i]) {
                    knLogger.debug( methodName, "biArray[" + i + "]=" + biArray[i]);
                    featureSet.set(i);
                }
            }
            knLogger.debug( methodName, "Before Feature set" + featureSet);
            featureSet.set(bitPos, value);
            knLogger.debug( methodName, "Feature set" + featureSet);
        } catch (Exception exp) {
            knLogger.error( methodName, "Exception=" , exp);
        }
        //return featureSet.toString();
        return getString(featureSet, biArray);
    }//getClientFeatureSet


    public static String getString(BitSet featureSet, boolean[] biArray) {
        StringBuffer buffer = new StringBuffer(200);
        buffer.append("");
        for (int i = 0; i < biArray.length; i += 8) {
            //take 4bits at a time and convert them
            //into a corresponding char.
            boolean bit1 = featureSet.get(i);
            boolean bit2 = featureSet.get(i + 1);
            boolean bit3 = featureSet.get(i + 2);
            boolean bit4 = featureSet.get(i + 3);
            boolean bit5 = featureSet.get(i + 4);
            boolean bit6 = featureSet.get(i + 5);
            boolean bit7 = featureSet.get(i + 6);
            boolean bit8 = featureSet.get(i + 7);
            buffer.append(getChar(bit8, bit7, bit6, bit5));
            buffer.append(getChar(bit4, bit3, bit2, bit1));
        }
        return buffer.toString();
    }

    private static String getChar(boolean bit4, boolean bit3, boolean bit2, boolean bit1) {

        //System.out.println(bit4+":"+bit3+":"+bit2+":"+bit1);
        int i = 0;
        if (bit4) {
            i = i + 8;
        }

        if (bit3) {
            i = i + 4;
        }

        if (bit2) {
            i = i + 2;
        }

        if (bit1) {
            i = i + 1;
        }
        //System.out.println(Integer.toHexString(i));
        return Integer.toHexString(i);

    }

    public static boolean isBitSet(byte[] arrayOfBytes, int bitPosition) {
        boolean setOn = false;
      //  int arrayLength = arrayOfBytes.length;
        int byteToSearch = -1;
        int positionInByte = -1;

        int divider = bitPosition / 8;
        int remainder = bitPosition % 8;

        if (remainder == 0) {
         //   byteToSearch = divider - 1;
            positionInByte = 1;
        } else {
          //  byteToSearch = divider;
            positionInByte = remainder;
        }

        byte b = arrayOfBytes[divider];
        int result = 0;

        if (positionInByte == 1) {
            result = b & 1;
        } else if (positionInByte == 2) {
            result = b & 2;
        } else if (positionInByte == 3) {
            result = b & 4;
        } else if (positionInByte == 4) {
            result = b & 8;
        } else if (positionInByte == 5) {
            result = b & 16;
        } else if (positionInByte == 6) {
            result = b & 32;
        } else if (positionInByte == 7) {
            result = b & 64;
        } else if (positionInByte == 8) {
            result = b & 128;
        }

        if (result != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method is used to check special chars in objToValidate
     *
     * @param objToValidate
     * @param splChars
     * @return boolean returns true if it doesn't contains spl. chars in "objToValidate" otherwise it returns false.
     */
    public static boolean chk4SplChars(Object objToValidate, String splChars) {
        char charArray[] = splChars.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (((String) objToValidate).indexOf(charArray[i]) != -1) {
                return false;
            }
        }
        return true;
    }

    public static String getErrorCode(String errorCode) {
        String serverErrorCode;
        if (errorCode.startsWith("ID")) {
            //Identity FW error codes
            int errorCodeValue = Integer.parseInt(errorCode.substring(2));
            switch (errorCodeValue) {
                case 1003:
                    serverErrorCode = KnErrorCodes.Authenticator.WRONG_PASSWORD;
                    break;
                case 1004:
                    serverErrorCode = KnErrorCodes.Authenticator.SESSION_TIMEOUT;
                    break;
                case 1002:
                case 1005:
                case 1009:
                    serverErrorCode = KnErrorCodes.Authenticator.AUTHENTICATION_FAILED;
                    break;
                default:
                    serverErrorCode = KnErrorCodes.Authenticator.INTERNAL_ERROR;
            }
            return serverErrorCode;
        }

        if(errorCode.startsWith("MSG")) {
            int errorCodeValue = Integer.parseInt(errorCode.substring(3));
            switch (errorCodeValue) {
                case 1008:
                    serverErrorCode = KnErrorCodes.BOEntity.INTERNAL_ERROR; //mapped for DB connection error
                    break;
                default:
                    serverErrorCode = KnErrorCodes.BOEntity.MESSAGE_PROCESSING_FAILED;
            }
            return serverErrorCode;
        }


        //Security FW error codes
        int error = Integer.parseInt(errorCode);
        String mappedErrorCode;
        switch (error) {
            case 9004://Invalid User
            case 9652://Invalid Subscriber
                mappedErrorCode = KnErrorCodes.BOEntity.INVALID_USER_ID;
                break;
            case 9006://Invalid Password
                mappedErrorCode = KnErrorCodes.BOEntity.WRONG_PASSWORD;
                break;
            case 9007://Password is locked by Administrator
                mappedErrorCode = KnErrorCodes.BOEntity.PASSWORD_LOCKED_BY_ADMIN;
                break;
            case 9012://Invalid Old Password
                mappedErrorCode = KnErrorCodes.BOEntity.INVALID_OLD_PASSWORD;
                break;
            case 9013://Invalid New Passwprd
                mappedErrorCode = KnErrorCodes.BOEntity.INVALID_NEW_PASSWORD;
                break;
            case 9656://Invalid User type
                mappedErrorCode = KnErrorCodes.BOEntity.INVALID_PROFILE;
                break;
            default://Server Busy - DB Error
                mappedErrorCode = KnErrorCodes.BOEntity.INTERNAL_ERROR;
        }
        return mappedErrorCode;
    }


   /* public static String getSyncGWErrorCode(String errorCode) {
        String syncGWMappedErrorCode;
            //Identity FW error codes
            int errorCodeValue = Integer.parseInt(errorCode);
            switch (errorCodeValue) {
                case 10525://Invalid Subscriber
                    syncGWMappedErrorCode = KnErrorCodes.BOEntity.XDMS_PTX_BUCKET_URLS_NOT_FOUND;
                    break;
                default://Server Busy - DB Error
                    syncGWMappedErrorCode = KnErrorCodes.BOEntity.INTERNAL_ERROR;
            }
            return syncGWMappedErrorCode;
        }*/
    /**
     * Simple alphanumeric random password generator
     *
     * @param length the length of the pwd required
     * @return newRandomPwd the random alphanumeric password
     */
    public static String getRandomPassword(int length) {
        StringBuffer result = new StringBuffer(200);
        SecureRandom rand = new SecureRandom();
        String chars = "0123456789";
        int poolSize = chars.length();
        char[] letters = chars.toCharArray();
        for (int i = 0; i < length; i++) {
            result.append(letters[rand.nextInt(poolSize)]);
        }
        return result.toString();
    }

    /**
     * This method is meant for parsing the SubscriberDefualtValues.xml and putting in to a Properties
     *
     * @param filePath
     * @param nodeName
     * @return
     * @throws Exception
     */
    public static Properties getSubscDefaultValues(String filePath, String nodeName) throws Exception {
        String methodName = "getSubscDefaultValues()";
        Properties defaultValHash ;
        BufferedReader bufferedreader = null;
        try {
            bufferedreader = new BufferedReader(new FileReader(filePath));
            String s;
            StringBuffer stringbuffer;
            for (stringbuffer = new StringBuffer(""); (s = bufferedreader.readLine()) != null; stringbuffer = stringbuffer.append(s))
                ;
            bufferedreader.close();
            InputSource inputsource = new InputSource(new StringReader(stringbuffer.toString()));
            DocumentBuilderFactory documentbuilderfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentbuilder = documentbuilderfactory.newDocumentBuilder();
            Document document = documentbuilder.parse(inputsource);

            Element element = document.getDocumentElement();
            if (nodeName != null) {
                NodeList nodeList = element.getElementsByTagName(nodeName);
                if (nodeList.getLength() > 0) {
                    element = (Element) nodeList.item(0);
                }
            }
            defaultValHash = getDefaultValues(element);
            return defaultValHash;
        } catch (Exception e) {
            knLogger.error( methodName, "Exception occured while parsing xml. Exception ->" + e);
            throw e;
        } finally {
            try {
                bufferedreader.close();
            } catch (Exception e) {
                throw e;
            }
        }
    }

    /**
     * Private method for reading and populating the name-attributes from elements
     *
     * @param element
     * @return
     * @throws Exception
     */
    private static Properties getDefaultValues(Element element) throws Exception {

        Properties defaultValHash = new Properties();

        if (element != null) {
            NodeList nodeList = element.getChildNodes();
            int numNodes = nodeList.getLength();
            for (int i = 0; i < numNodes; i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String name = ((Element) node).getAttribute("name");
                    if (name != null && !name.equals("")) {
                        String value = ((Element) node).getAttribute("value");
                        defaultValHash.put(name, value);
                    }
                    String staticList = ((Element) node).getAttribute("StaticList");

                    if (staticList != null && !staticList.equals("")) {
                        defaultValHash.put(name + "_StaticList", staticList);
                    }
                }
            }
        } //end of element//
        return defaultValHash;
    }


    /**
     * utility to convert InputStream to String
     * @param is
     * @return
     * @throws com.kodiak.xdms.server.common.KnXDMServerException
     */
    public static String convertStreamToString(InputStream is)
            throws KnXDMServerException {

        String methodName = "convertStreamToString";
        if (is != null) {
            Writer writer = new StringWriter();
            try {
                char[] buffer = new char[1024];
                try {
                    Reader reader = new BufferedReader(
                            new InputStreamReader(is));
                    int n;
                    while ((n = reader.read(buffer)) != -1) {
                        writer.write(buffer, 0, n);
                    }
                } finally {
                    is.close();
                }

            } catch (Exception e) {
                //
                knLogger.error( methodName, "Exception "+ e);
            }
            return writer.toString();
        } else {
            return "";
        }
    }


    /**
     *
     * @param is
     * @return
     * @throws KnXDMServerException
     */
    public static String convertStreamToStringEnc(InputStream is)
            throws KnXDMServerException {

        String methodName = "convertStreamToString";
        if (is != null) {
            Writer writer = new StringWriter();
            try {
                char[] buffer = new char[1024];
                try {
                    Reader reader = new BufferedReader(
                            new InputStreamReader(is, "UTF-8"));
                    int n;
                    while ((n = reader.read(buffer)) != -1) {
                        writer.write(buffer, 0, n);
                    }
                } finally {
                    is.close();
                }

            } catch (Exception e) {
                //
                knLogger.error( methodName, "Exception "+ e);
            }
            return writer.toString();
        } else {
            return "";
        }
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

    public static String replaceContactWithValue(String str, String constant, String value) {
        String finalStr = "";
        if (str == null || str.trim().equals("")) {
            return finalStr;
        }
        finalStr = str.replaceAll(constant, value);
        return finalStr;
    }

    public String generateMD5(String text) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        byte[] md5hash = new byte[32];
        md.update(text.getBytes("UTF-8"), 0, text.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    }


    private String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer(50);
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String validateAndFetchDetailsFromUA(String userAgent) {
    	/**
    	 * <3rd Party Client Identifier><SingleSpace><VendorID><SingleSpace>
    	 * <3GPP ReleaseVersion><Single Space><PVVersion><Single space>
    	 * <UEType><SingleSpace><AdditionalInformation>
		 *3GPP_MCX 01 3GPP_Rel_15.0.0 19 CR
    	 */
        String methodName = "fetchDetailsFromUserAgent(String userAgent)";
        knLogger.debug(methodName,"UserAgent from Request : ",userAgent);
        String prefix = "3GPP_MCX ";
        String kodClient = "MCPTT-client/MCPTT2.0";
        String protocolVersion=null;
        if (userAgent!=null && !userAgent.isEmpty() && userAgent.startsWith(prefix)) {
            userAgent = userAgent.substring(prefix.length());
            String[] arr = userAgent.split("\\s+");
            if (arr.length >=4) {
            	if (arr[2].matches("\\d+")) {
                    knLogger.debug(methodName," Extracted PV : ",arr[2]);
                    protocolVersion = arr[2];
                }
            }
            if (arr.length == 3) {
                if (arr[1].matches("\\d+")) {
                    knLogger.debug(methodName," Extracted PV : ",arr[1]);
                    protocolVersion = arr[1];
                }
            }
        }else if(userAgent!=null && !userAgent.isEmpty() && userAgent.startsWith(kodClient)) {
            KnUserAgentDTO userAgentInfo = getDetailsFromUserAgent(userAgent);
            //converting 18.0 to 18
            String[] pv = userAgentInfo.getProtocolVersion().split("\\.");
            if (pv != null && pv[0] != null) {
                    int newMajorPV = Integer.parseInt(pv[0]);
                    knLogger.debug(methodName,"newMajorPV ",newMajorPV);
                    protocolVersion = String.valueOf(newMajorPV);
            }
        }

        knLogger.debug(methodName,"protocolVersion : ",protocolVersion);
        return protocolVersion;
    }

    public static KnUserAgentDTO getDetailsFromUserAgent(String userAgent) {
        String methodName = "getDetailsFromUserAgent";
        knLogger.debug(methodName, "ENTRY: getDetailsFromUserAgent ", userAgent);
        KnUserAgentDTO userAgentDTO = new KnUserAgentDTO();

            /*
            sample
            User-Agent: MCPTT-client/MCPTT2.0 Win32_UA/kn/Kodiak/KodiakWin32Test Win32/1.0 knpoc-08_001_01_01_25V/9.0 Android_POC_07_007_01_43 I747UCDJL3
            User-Agent: PoC-Client/OMA2.0 Motorola/Droid Android/2.0 knpoc-7.0/1.0
            User-Agent: PoC-Client/OMA2.0 TEST/kn/Motorola/Droid Android/2.0 knpoc-7.0/2.0 yyy uuuu(with device UA)
            mn  manufacturer name
            dn  device name
            osn  os/framework name
            osv  os/framework version
            an  app name  knpoc
            av  app version
            pv  protocol version
            uv  ui version
            */
        try {
            String preFix1 = "PoC-client/OMA2.0";
            String preFix2 = "PoC-Client/OMA2.0";
            String preFix3 = "MCPTT-client/MCPTT2.0";
            if (userAgent.contains(preFix1))
                userAgent = userAgent.substring(userAgent.indexOf(preFix1) + 17);
            else if (userAgent.contains(preFix2)) {
                userAgent = userAgent.substring(userAgent.indexOf(preFix2) + 17);

            }else if (userAgent.contains(preFix3)){
                userAgent = userAgent.substring(userAgent.indexOf(preFix3) + 21);
            }
            userAgent = userAgent.trim();
            String deviceUA = null;
            String del = "/kn/";
            if (userAgent.contains(del)) {
                deviceUA = userAgent.substring(0, userAgent.indexOf(del));
                userAgent = userAgent.substring(userAgent.indexOf(del) + 4);
            }

            userAgent = userAgent.trim();
            String[] temp = userAgent.split("\\s+");
            String[] temp1;

            userAgentDTO.setDeviceUA(deviceUA);
            if (temp.length > 0) {
                temp1 = temp[0].split("/");
                userAgentDTO.setManufactName(temp1[0]);
                userAgentDTO.setDeviceName(temp1[1]);
            }
            if (temp.length > 1) {
                temp1 = temp[1].split("/");
                userAgentDTO.setOsName(temp1[0]);
                userAgentDTO.setOsVersion(temp1[1]);
            }

            if (temp.length > 2) {
                temp1 = temp[2].split("/");
                userAgentDTO.setProtocolVersion(temp1[1]);
                String []appNameVer = temp1[0].split("-");
                userAgentDTO.setAppName(appNameVer[0]);
                if (appNameVer.length > 1) {
                    userAgentDTO.setAppVersion(appNameVer[1]);
                }
            }

            if (temp.length > 3) {
                userAgentDTO.setUiVersion(temp[3]);
            }

            if (temp.length > 4) {
                userAgentDTO.setHsBaseband(temp[4]);
            }

        } catch (Exception e) {
            knLogger.warn(methodName, "invalid userAgent - ", userAgent, e);
            userAgentDTO = null;
        }
        knLogger.debug(methodName, "Exit: getDetailsFromUserAgent  userAgentDTO:", userAgentDTO);
        return userAgentDTO;
    }

    public static boolean isInteger(Object object) {
        if(object == null){
            return false;
        }
        if(object instanceof Integer) {
            return true;
        } else {
            try {
                String string = object.toString();
                Integer.parseInt(string);
            } catch(Exception e) {
                return false;
            }
        }
        return true;
    }

    public static List<KnDocChangeListDTO> buildMCSDOC(String mcId
            ,String mdn,String upmIndex,String newEtag,String previousEtag,int mcsCompliance,String exists) {
        List<KnDocChangeListDTO> docList = new ArrayList<>();
        if (mcsCompliance == 0) {
            //for kodiak clients
            for (Map.Entry auid : auidMap.entrySet()) {
                StringBuilder mcsDocUri = new StringBuilder();
                mcsDocUri.append(auid.getValue())
                        .append("/users/")
                        .append(mcId)
                        .append("/")
                        .append(auid.getKey())
                        .append("-user-profile-")
                        .append(upmIndex)
                        .append(".xml");
                if(exists != null && exists.equals("0")){
                    docList.add(new KnDocChangeListDTO(mdn, mcsDocUri.toString(), null, null,"0"));
                }else{
                    docList.add(new KnDocChangeListDTO(mdn, mcsDocUri.toString(), newEtag, previousEtag,null));
                }
            }
        } else {
            for (Map.Entry auid : auidMap.entrySet()) {
                StringBuilder mcsDocUri = new StringBuilder();
                mcsDocUri.append(auid.getValue())
                        .append("/users/")
                        .append(mcId)
                        .append("/user-profile-")
                        .append(upmIndex)
                        .append(".xml");
                if(exists != null && exists.equals("0")){
                    docList.add(new KnDocChangeListDTO(mdn, mcsDocUri.toString(), null, null,"0"));
                }else{
                    docList.add(new KnDocChangeListDTO(mdn, mcsDocUri.toString(), newEtag, previousEtag,null));
                }
            }
        }
        return docList;
    }

    public KnSubsProfileDTO prepareUserProfileChanges(KnSubsProfileDTO subsProfile,String corpFS2,String userAgent,int protocolVersion,String clientFS2CRI) throws Exception {
        String methodName = "prepareUserProfileChanges(KnSubsProfileDTO,String,String,String)";
        knLogger.info(methodName, "Entry :");
        Map<Integer, KnDefaultMCSClientInfo> mcsClientInfoMap = null;
        KnSubsProfileDTO subsProfileDTO = new KnSubsProfileDTO();;
        KnDefaultMCSClientInfo mcsClientInfo = null;
        boolean userAgentUpdated = false;
        String clientFS2 = subsProfile.getClientFS2();
        String oldActiveFS2 = subsProfile.getActiveFS2();
        String newActiveFS2 = null;
        String xdmServerId = subsProfile.getXdmsHome();
        String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(xdmServerId, protocolVersion);
        if (subsProfile.getUserAgent() == null || !subsProfile.getUserAgent().equalsIgnoreCase(userAgent) || clientFS2CRI != null) {
            if(subsProfile.getUserAgent() == null || !subsProfile.getUserAgent().equalsIgnoreCase(userAgent)){
                userAgentUpdated = true;
            }
            String newClientFS2 = null;
            if (KnConstants.MCSCOMPLIANCE == subsProfile.getMcpttCompliance()) {
                knLogger.info(methodName,"MCS COMPLIANCE client");
                if (clientFS2CRI != null) {
                    BitSet clientFS2BitSet = featureSetUtil.convertHexStringToBitSet(clientFS2CRI);
                    String clientFS2BITMASK = featureSetUtil.getClientFS2BitMask();
                    BitSet clientFS2MaskBitSet = featureSetUtil.convertHexStringToBitSet(clientFS2BITMASK);
                    BitSet finalClientFSBitSet = new BitSet(Long.SIZE);
                    finalClientFSBitSet.or(clientFS2BitSet);
                    finalClientFSBitSet.or(clientFS2MaskBitSet);
                    newClientFS2 = featureSetUtil.convertBitSetToHexString(finalClientFSBitSet);
                } else {
                    mcsClientInfoMap = KnGeneralCacheUtil.getInstance().retrieveMCSClientInfo();
                    if (mcsClientInfoMap != null) {
                        mcsClientInfo = mcsClientInfoMap.get(protocolVersion);
                        if (mcsClientInfo != null)
                            newClientFS2 = mcsClientInfo.getClientFS2();
                    }
                }
            } else {
                if (subsProfile.getClientFS2() != null) {
                    newClientFS2 = featureSetUtil.generateClientFeatureSet(subsProfile.getClientFS2(), clientCapOverrideBitMask);
                }
            }
            clientFS2 = newClientFS2;
    }
        String subsFS2 = subsProfile.getSubscriberFS2();
        String opsFS2 = subsProfile.getOpsFS2();
        String xdmsFs2 = subsProfile.getXdmsFS2();
        String corpAdminFS2 = subsProfile.getCorpAdminFS2();
        String userProfileFS2 = subsProfile.getUserProfileFS2();
        if(userProfileFS2 == null)
        {
            userProfileFS2=featureSetUtil.getDefFinalUserProfileFS();
        }
        String pocPttId = subsProfile.getPocHome();
        String presencePttId = subsProfile.getPresenceHome();
        if (subsProfile.getCorpSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
            newActiveFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmServerId,
                    clientFS2, subsFS2, opsFS2, clientCapOverrideBitMask,xdmsFs2,userProfileFS2);
        } else {
            newActiveFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmServerId,
                    clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2, clientCapOverrideBitMask,xdmsFs2,userProfileFS2);
        }
        knLogger.info(methodName,"userAgentUpdated = ",userAgentUpdated," oldActiveFS2.equals(newActiveFS2) = ",oldActiveFS2.equals(newActiveFS2));
        if(userAgentUpdated || !oldActiveFS2.equals(newActiveFS2)){
            subsProfileDTO.setUserAgent(userAgent);
            subsProfileDTO.setClientMajorVersion(protocolVersion);
            subsProfileDTO.setClientFS2(clientFS2);
            subsProfileDTO.setActiveFS2(newActiveFS2);
            subsProfileDTO.setMdn(subsProfile.getMdn());
            subsProfileDTO.setLastProfileUpdateTime(Calendar.getInstance().getTimeInMillis());
            subsProfileDTO.setActiveFSUpdated(Boolean.TRUE);
            subsProfileDTO.setUserAgentUpdated(Boolean.TRUE);
            //setting new values in existing subscriber Profile
            subsProfile.setActiveFSUpdated(Boolean.TRUE);
            subsProfile.setUserAgent(userAgent);
            subsProfile.setClientMajorVersion(protocolVersion);
            subsProfile.setClientFS2(clientFS2);
            subsProfile.setOldActiveFS2(oldActiveFS2);
            subsProfile.setActiveFS2(newActiveFS2);
            subsProfile.setUserAgentUpdated(Boolean.TRUE);
            subsProfile.setLastProfileUpdateTime( subsProfileDTO.getLastProfileUpdateTime());
          //  boolean cleanUpTGSData = cleanUpTGSData(newActiveFS2, oldActiveFS2);
           // subsProfileDTO.setCleanUpTGSData(cleanUpTGSData);

        }
        knLogger.info(methodName,"Exit :");
        return subsProfileDTO;
    }

    private Boolean cleanUpTGSData(String activeFS, String existingActiveFS) {
        String methodName = "cleanUpTGSData(String,String)";
        boolean cleanUpTGSData = false;
        //get the 20th and 31st bit from the existing DB activefs
        boolean talkGrpSelServerExstActFS = featureSetUtil.getFeatureBitValue(existingActiveFS, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value());
        boolean talkGrpSelClientExstActFS = featureSetUtil.getFeatureBitValue(existingActiveFS, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELCLIENT.value());

        //get the 20th,28th and 31st bit from the recalculated activefs
        boolean talkGrpScanServerActFS = featureSetUtil.getFeatureBitValue(activeFS, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value());
        boolean talkGrpScanClientActFS = featureSetUtil.getFeatureBitValue(activeFS, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSCANCLIENT.value());
        boolean talkGrpSelClientActFS = featureSetUtil.getFeatureBitValue(activeFS, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELCLIENT.value());

        if (!talkGrpScanServerActFS && !talkGrpScanClientActFS) {
            if (talkGrpSelClientActFS) {
                if ((talkGrpSelServerExstActFS && !talkGrpSelClientExstActFS) || (!talkGrpSelServerExstActFS && !talkGrpSelClientExstActFS)) {
                    cleanUpTGSData = true;
                }
            } else {
                if ((talkGrpSelServerExstActFS && !talkGrpSelClientExstActFS) || (!talkGrpSelServerExstActFS && talkGrpSelClientExstActFS)) {
                    cleanUpTGSData = true;
                }
            }
        }
        knLogger.debug(methodName," cleanUpTGSData=== ",cleanUpTGSData);
        return cleanUpTGSData;
    }

    public static void veryLargeGroupBitChanged(String oldActiveFS2, String newActiveFS2
            , String mdn, String corpId, boolean isProfileMdn) {
        final String methodName = "veryLargeGroupBitChanged()";
        knLogger.debug(methodName, " oldActiveFS2 :"
                , oldActiveFS2, " newActiveFS2 :", newActiveFS2
                , " mdn :", mdn," corpId :",corpId
                ," isProfileMdn :",isProfileMdn);
        boolean veryLargeGroupBitDisabled=false;
        int resourceType = KnConstants.UPM_RESOURCE_TYPE.MDN.Value();
        try {
            if (oldActiveFS2 == null) {
                veryLargeGroupBitDisabled = getFeatureBitValue(newActiveFS2, VERY_LARGE_GROUP);
            } else {
                boolean newVeryLargeGroupBit = getFeatureBitValue(newActiveFS2, VERY_LARGE_GROUP);
                boolean oldVeryLargeGroupBit = getFeatureBitValue(oldActiveFS2, VERY_LARGE_GROUP);
                //old =true, new=false
                if(oldVeryLargeGroupBit&&!newVeryLargeGroupBit) {
                    veryLargeGroupBitDisabled=true;
                }
            }
            knLogger.debug(methodName, " veryLargeGroupBitDisabled :", veryLargeGroupBitDisabled);
            if(veryLargeGroupBitDisabled){
                KnAsyncJobDTO JobReqDTO = new KnAsyncJobDTO();
                JobReqDTO.setTxnId(String.valueOf(System.nanoTime()));
                JobReqDTO.setCreationTime(String.valueOf(Instant.now().toEpochMilli()));
                JobReqDTO.setOpType(KnConstants.UPM_OPERATION_TYPE.VERY_LARGE_GROUP_BIT_DISABLED.Value());
                JobReqDTO.setOpStatus(KnConstants.UPM_JOB_STATUS.VERY_LARGE_GRP_CLEAN_NEW.Value());
                JobReqDTO.setResourceEntity(null);
                if(isProfileMdn){
                    resourceType = KnConstants.UPM_RESOURCE_TYPE.PROFILEMDN.Value();
                }
                JobReqDTO.setResourceType(resourceType);
                JobReqDTO.setCorpId(Integer.parseInt(corpId));
                JobReqDTO.setUpdationTime(String.valueOf(Instant.now().toEpochMilli()));
                JobReqDTO.setPayLoad(mdn);
                knLogger.info(methodName, " mcpttBitDisabled JobReqDTO:", JobReqDTO);
                KnGeneralCacheUtil.getInstance().createAsyncJob(JobReqDTO);
            }
        } catch (Exception e) {
            knLogger.error(methodName,"Exception :",e.getMessage());
        }
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

    public static boolean isTargetInfoOptimizationEnabled(String targetInfoOptimization) {
        return KnConstants.TARGET_INFO_OPTIMIZATION_ENABLED.equals(targetInfoOptimization);
    }

}

