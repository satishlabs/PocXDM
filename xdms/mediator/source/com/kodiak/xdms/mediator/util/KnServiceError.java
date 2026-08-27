/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnServiceError.java
 * Subsystem:  IDS
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ajit KUmar          21-Dec-2011      7.2
 *  * <p/>
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
package com.kodiak.xdms.mediator.util;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.KnMediatorConstants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KnServiceError {
	private static final KnLogger knLogger = KnLogger.getLogger(KnServiceError.class);

    private static final String CLASS = KnServiceError.class.getName();
    //single instance of this map, that shall contain
    //key: mapperFileName, value: instance of this class
    private static Map instanceMap = new HashMap();


    //stores the configuration file name that is loaded in this instance
    private String configFile = null;
    
  //stores the PAM configuration file name that is loaded in this instance
    private String PamConfigFile = null;
    private String SoapPamConfigFile = null;

    //data structures to store the information to be used for mapping
    //global error mapping information
    private Map errorConfigMap = null;
    //operation specific error mapping information
    //   private Map operationErrorConfigMap = null;
    //instance of the mapping xml loader
    private KnServiceErrorLoader loader = null;
    // instance of error code mappings properties file
    private Properties errorCodeMappings = null;
    //global PAM error mapping information
    private Map PamErrorConfigMap = null;

 // instance of PAM code mappings properties file
    private Properties PamErrorCodeMappings = null;
    
    private Map SoapPamErrorConfigMap = null;
    private Properties SoapPamErrorCodeMappings = null;

    private KnServiceError(String configFile, String PamConfigFile, String SoapPamConfigFile) {
        this.errorConfigMap = new HashMap();
        this.errorCodeMappings = new Properties();
        this.PamErrorCodeMappings = new Properties();
        this.PamErrorConfigMap = new HashMap();
        this.SoapPamErrorCodeMappings=new Properties();
        this.SoapPamErrorConfigMap=new HashMap();
        this.configFile = configFile;
        this.PamConfigFile = PamConfigFile;
        this.SoapPamConfigFile=SoapPamConfigFile;
        loadErrorCodeMappings();
        loadConfigFile();
        loadPamConfigFile();
        loadSoapPamConfigFile();
    }

    /**
     * This static method should be used to get an instance of this class
     * The instance retrieved would depend on the configuration file passed
     * to the method. i.e. There would be ONE instance for EACH config file
     *
     * @param errorConfigFile
     * @return
     */
    public static synchronized KnServiceError getInstance(String errorConfigFile, String PamErrorConfigFile , String SoapPamConfigFile) {

        if (!instanceMap.containsKey(errorConfigFile)) {
            knLogger.info( "getInstance(errorConfigFile)", "getting first instance of KnServiceError.");
            KnServiceError instance = new KnServiceError(errorConfigFile, PamErrorConfigFile,SoapPamConfigFile );
            instanceMap.put(errorConfigFile, instance);
        }
        return (KnServiceError) instanceMap.get(errorConfigFile);
    }


    /**
     * Private method to load the configuration file required
     * for this class to perform the mapping functionality.
     */
    private void loadConfigFile() {
        String methodName = "loadConfigFile()";
        knLogger.info( methodName, "Initializing Errors with config file: " , configFile);

        if (loader == null) {
            loader = new KnServiceErrorLoader(configFile, errorConfigMap);
        }
        try {
            loader.load();
            knLogger.debug( methodName, "Initialized Errors with global config: " , errorConfigMap);
            // knLogger.info( methodName, "Initialized Errors with operation config: "
            //          , operationErrorConfigMap);
        } catch (Exception e) {
            knLogger.error( methodName, "Exception occured: " , e);
        }
    }

    /**
     * Private method to load the configuration file required
     * for this class to perform the mapping functionality.
     */
    private void loadPamConfigFile() {
        String methodName = "loadPamConfigFile()";
        knLogger.info( methodName, "Initializing Errors with config file: " , PamConfigFile);

        	KnServiceErrorLoader loader = new KnServiceErrorLoader(PamConfigFile, PamErrorConfigMap);
        try {
            loader.load();
            knLogger.debug( methodName, "Initialized Errors with PAM global config: "
                    , PamErrorConfigMap);
        } catch (Exception e) {
            knLogger.error( methodName, "Exception occured: " , e);
        }
    }

    private void loadSoapPamConfigFile() {
        String methodName = "loadSoapPamConfigFile()";
        knLogger.info( methodName, "Initializing Errors with config file: " , SoapPamConfigFile);

        	KnServiceErrorLoader loader = new KnServiceErrorLoader(SoapPamConfigFile, SoapPamErrorConfigMap);
        try {
            loader.load();
            knLogger.info( methodName, "Initialized Errors with Soap PAM global config: "
                    , SoapPamErrorConfigMap);
        } catch (Exception e) {
            knLogger.error( methodName, "Exception occured: " , e);
        }
    }
    
    private void loadErrorCodeMappings() {
        String methodName = "loadErrorCodeMappings";
        try {

            //TODO
            String errorCodeMappingFile = KnMediatorConstants.ERROR_CODE_MAPPINGS_PROPS;
            knLogger.info( methodName, "Initializing Error code mappings with config file: " , KnMediatorConstants.ERROR_CODE_MAPPINGS_PROPS);
            //load the error mappings properties file
            // Loading Config file.
            InputStream input = getClass().getResourceAsStream(errorCodeMappingFile);
            //InputStream input = new FileInputStream( errorCodeMappingFile);
            errorCodeMappings.load(input);
            knLogger.debug( methodName, "Initialized base prov Errors mappings: " , errorCodeMappings);
            
            knLogger.info( methodName, "Initializing PAM Error code mappings with config file: " , KnMediatorConstants.PAM_ERROR_CODE_MAPPINGS_PROPS);
            //load the error mappings properties file
            errorCodeMappingFile = KnMediatorConstants.PAM_ERROR_CODE_MAPPINGS_PROPS;
         // Loading Config file.
            input = getClass().getResourceAsStream(errorCodeMappingFile);
            PamErrorCodeMappings.load(input);
            knLogger.debug( methodName, "Initialized base PAM Errors mappings: " , PamErrorCodeMappings);
            
            knLogger.info( methodName, "Initializing SOAP PAM Error code mappings with config file: " , KnMediatorConstants.SOAP_PAM_ERROR_CODE_MAPPINGS_PROPS);
            //load the error mappings properties file
            errorCodeMappingFile = KnMediatorConstants.SOAP_PAM_ERROR_CODE_MAPPINGS_PROPS;
         // Loading Config file.
            input = getClass().getResourceAsStream(errorCodeMappingFile);
            SoapPamErrorCodeMappings.load(input);
            knLogger.debug( methodName, "Initialized base SOAP PAM Errors mappings: " , SoapPamErrorCodeMappings);
            
        } catch (FileNotFoundException fne) {
            knLogger.error( methodName, "File not found --> " , fne);
        } catch (IOException ie) {
            knLogger.error( methodName, "IO Exception occured -->" , ie);
        }
    }

    /**
     * @param errorCode errorcode
     * @return KnErrorBean
     */
    public KnErrorBean getMappedError(String errorCode) {
        return (KnErrorBean) errorConfigMap.get(errorCode);
    }

    /**
     * @param errorCode errorcode
     * @return KnErrorBean
     */
    public KnErrorBean getPamMappedError(String errorCode) {
        return (KnErrorBean) PamErrorConfigMap.get(errorCode);
    }

    public KnErrorBean getSoapPamMappedError(String errorCode) {
    	String methodName = "getSoapPamMappedError(String)";
    	// knLogger.debug( methodName, "Entry " , SoapPamErrorConfigMap);
        return (KnErrorBean) SoapPamErrorConfigMap.get(errorCode);
    }
    /**
     * @param serverErrorCode
     * @return KnErrorBean
     */
    public KnErrorBean getMappedServerError(String serverErrorCode) {

        String methodName = "getMappedServerError(String)";
        String serviceErrorCode = KnMediatorConstants.ERROR_CODE_INTERNAL_SERVER_ERROR;
        knLogger.debug( methodName, "Server Error Code to be mapped to " ,
                "error code : " , serverErrorCode);

        if (serverErrorCode != null && !serverErrorCode.trim().equals("")) {
            String libError = serverErrorCode.substring(serverErrorCode.lastIndexOf("-") + 1);
            knLogger.debug( methodName, "Server Error Code :"
                    , libError);
            String mappedLibError = KnMediatorConstants.ERROR_CODES_MAP.get(libError);
            knLogger.debug( methodName, "Mapped Server Error Code :"
                    , mappedLibError);
            if (mappedLibError != null) serviceErrorCode = mappedLibError;
        }
        return getMappedError(serviceErrorCode);
    }

    /**
     * @param libErrorCode
     * @return
     */
    public String getServiceErrorCode(String libErrorCode) {
        String methodName = "getServiceErrorCode(String)";
        knLogger.debug( methodName, "Error Code to be converted to IDS major code " , libErrorCode
        );
        String libError = null;
        String actualErrCode = null;
        if (libErrorCode.startsWith(KnMediatorConstants.ERROR_CODE_PREFIX)) {
            libError = libErrorCode.substring(KnMediatorConstants.ERROR_CODE_PREFIX.length(), libErrorCode.length());
        } else if (libErrorCode.length() > 5) {
            libError = libErrorCode.substring(libErrorCode.length() - 5, libErrorCode.length());
        } else {
            libError = libErrorCode;
        }

        // get the value for the libError key in the errorcode mappings properties
        if (errorCodeMappings.containsKey(libError)) {
            actualErrCode = (String) errorCodeMappings.get(libError);
            libError = actualErrCode;
        }
        //knLogger.debug( methodName, "LibError Code:" , libError);
        //String serviceErrorCode = KnIDSProvisionConstants.ERROR_CODE_PREFIX + libError;
        return libError;
    }

    /**
     * @param libErrorCode
     * @return
     */
    public String getPamServiceErrorCode(String libErrorCode) {
        String methodName = "getPamServiceErrorCode(String)";
        knLogger.debug( methodName, "Error Code to be converted to PAM major code " , libErrorCode
        );
        String libError = null;
        String actualErrCode = null;
        if (libErrorCode.startsWith(KnMediatorConstants.ERROR_CODE_PREFIX)) {
            libError = libErrorCode.substring(KnMediatorConstants.ERROR_CODE_PREFIX.length(), libErrorCode.length());
        } else if (libErrorCode.length() > 5) {
            libError = libErrorCode.substring(libErrorCode.length() - 5, libErrorCode.length());
        } else {
            libError = libErrorCode;
        }

        // get the value for the libError key in the errorcode mappings properties
        if (PamErrorCodeMappings.containsKey(libError)) {
            actualErrCode = (String) PamErrorCodeMappings.get(libError);
            libError = actualErrCode;
        }
        //knLogger.debug( methodName, "LibError Code:" , libError);
        //String serviceErrorCode = KnIDSProvisionConstants.ERROR_CODE_PREFIX + libError;
        knLogger.exit( methodName, "LibError Code:" , libError);
        return libError;
    }
    
    public String getSoapPamServiceErrorCode(String libErrorCode) {
        String methodName = "getSoapPamServiceErrorCode(String)";
        knLogger.debug( methodName, "Error Code to be converted to PAM major code " , libErrorCode
        );
        String libError = null;
        String actualErrCode = null;
        if (libErrorCode.startsWith(KnMediatorConstants.ERROR_CODE_PREFIX)) {
            libError = libErrorCode.substring(KnMediatorConstants.ERROR_CODE_PREFIX.length(), libErrorCode.length());
        } else if (libErrorCode.length() > 5) {
            libError = libErrorCode.substring(libErrorCode.length() - 5, libErrorCode.length());
        } else {
            libError = libErrorCode;
        }

        // get the value for the libError key in the errorcode mappings properties
        if (SoapPamErrorCodeMappings.containsKey(libError)) {
            actualErrCode = (String) SoapPamErrorCodeMappings.get(libError);
            libError = actualErrCode;
        }
        //knLogger.debug( methodName, "LibError Code:" , libError);
        //String serviceErrorCode = KnIDSProvisionConstants.ERROR_CODE_PREFIX + libError;
        knLogger.exit( methodName, "LibError Code:" , libError);
        return libError;
    }

    /**
     * @param libErrorMsg
     * @param errorCode
     * @return
     */
    public String getServiceErrorMsg(String libErrorMsg, String errorCode) {
        KnErrorBean errorBean = null;
        errorBean = (KnErrorBean) errorConfigMap.get(errorCode);
        if (errorBean != null) {
            return errorBean.getErrMsg();
        }
        return libErrorMsg;
    }
}
