/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 * File name:   KnInitializerConfigData.java
 * Subsystem:   Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- -------
 * Rama Krishna         15-03-2007 6.0
 *
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
 * you entered into with Kodiak Networks..
 **************************************************************************/
package com.kodiak.xdms.server.common.configuration.loader;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.configuration.init.KnInitConfigException;
import com.kodiak.xdms.server.common.configuration.init.KnInitLoaderConfigException;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * This class shall be invoked from KnConfigInitializer.
 * It shall load the information (required for initialization),
 * from the initialization xml file.
 */
public class KnInitializerConfigData implements IModuleLoaderConfigData {
	private static final KnLogger knLogger = KnLogger.getLogger(KnInitializerConfigData.class);

    private static final String CLASS = KnInitializerConfigData.class.getName();

    /**
     * the below constants are used for parsing the xml.
     * the xml for initialization will be as follows
     * <db-init>
     * ..<db-file-path>emsdb.props</db-file-path>
     * ..<license-parser-path>licenseparser.properties</license-parser-path>
     * ..<active-release-dir>/home/kodiak</active-release-dir>
     * ..<client-id>WGPLibrary</client-id>
     * ..<rmi-port>1099</rmi-port>
     * </db-init>
     */
    public static final String DB_INIT_TAG = "db-init";
    public static final String DB_FILE_PATH_TAG = "db-file-path";
    public static final String CARD_TYPE_TAG = "card-type";
    public static final String CONF_FILE_TAG = "config-path";
    public static final String LIC_PARSER_PATH_TAG = "license-parser-path";
    public static final String ACTIVE_RELEASE_DIR_TAG = "active-release-dir";
    public static final String RMI_PORT_TAG = "rmi-port";
    public static final String INT_MON_FILE_PATH_TAG = "intf-mon-file-path";

    /**
     * stores the name of module
     */
    private String moduleName;

    /**
     * stores the db file path
     */
    private String dbFilePath;

    /**
     * stores the card type
     */
    private int cardType;

    /**
     * stores the license parser path
     */
    private String licParserPath;

    /**
     * stores the active release dir
     */
    private String activeRelDir;

    /**
     * stores the rmi port
     */
    private int rmiPort;

    /**
     * stores the interface monitor file path
     */
    private String monitorFilePath;

    /**
     * stores the path for the configuration file
     */
    private String configPath;


    /**
     * This method shall populate the value of the variables
     * with the values set in the xml file for initialization
     * @param element
     * @throws KnInitConfigException
     */
    public void populate(Element element) throws KnInitConfigException {
        NodeList xmlNodeList = null;
        Element xmlElement = null;

        knLogger.debug( "populate",
                "populating loader config data...");
        try {
            xmlNodeList = element.getElementsByTagName(DB_INIT_TAG);
            if (xmlNodeList.getLength() == 1) {
                xmlElement = (Element) xmlNodeList.item(0);
            } else if (xmlNodeList.getLength() == 0) {
                knLogger.error( "populate",
                        "The <" + DB_INIT_TAG + "> tag is missing for module " + moduleName);
                throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                        "The <" + DB_INIT_TAG + "> tag is missing",
                        DB_INIT_TAG, null, moduleName);
            } else {
                knLogger.error( "populate",
                        "The <" + DB_INIT_TAG + "> tag is repeating for module " + moduleName);
                throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                        "The <" + DB_INIT_TAG + "> tag is repeating",
                        DB_INIT_TAG, null, moduleName);
            }

            //Now, set the value for the attributes from the xml
            this.dbFilePath = getValueForTag(xmlElement, DB_FILE_PATH_TAG);
            this.cardType = Integer.parseInt(getValueForTag(xmlElement, CARD_TYPE_TAG));
            this.configPath = getValueForTag(xmlElement, CONF_FILE_TAG);
            this.licParserPath = getValueForTag(xmlElement, LIC_PARSER_PATH_TAG);
            this.activeRelDir = getValueForTag(xmlElement, ACTIVE_RELEASE_DIR_TAG);
            this.rmiPort = Integer.parseInt(getValueForTag(xmlElement, RMI_PORT_TAG));
            this.monitorFilePath = getValueForTag(xmlElement, INT_MON_FILE_PATH_TAG);

            knLogger.info( "populate",
                    "Loader config data populated successfully. Config : " + this);
        } finally {
            xmlNodeList = null;
            xmlElement = null;
        }
    }


    /**
     * Utility method to get the value for a given 'tag'
     * from the xml element
     * @param xmlElement
     * @param tagName
     * @return String value of the tag in the xml
     * @throws KnInitLoaderConfigException
     */
    private String getValueForTag (Element xmlElement, String tagName) throws KnInitLoaderConfigException {
        NodeList nodeList;
        Node node;
        String value;
        try {
            nodeList = xmlElement.getElementsByTagName(tagName);
            if (nodeList.getLength() == 1) {
                node = nodeList.item(0);
                value = node.getFirstChild().getNodeValue();
                if (value == null || value.trim().equals("")) {
                    knLogger.error( "populate",
                            "The <" + tagName + "> tag under <" + DB_INIT_TAG + "> tag is empty for module " + moduleName);
                    throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                            "the <" + tagName + "> tag is empty",
                            tagName, value, moduleName);
                } else {
                    return value;
                }
            } else if (nodeList.getLength() == 0) {
                knLogger.error( "populate",
                        "The <" + tagName + "> tag under <" + DB_INIT_TAG + "> tag is missing for module " + moduleName);
                throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                        "The <" + tagName + "> tag missing", tagName, null, moduleName);
            } else {
                knLogger.error( "populate",
                        "The <" + tagName + "> tag under <" + DB_INIT_TAG + "> tag is repeating for module " + moduleName);
                throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                        "More than one <" + tagName + "> tag", tagName, null, moduleName);
            }
        } finally {
            nodeList = null;
            node = null;
            value = null;
        }
    }


    /**
     * This method will set the module name
     * @param moduleName
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * This method will return the db file path
     * @return dbFilePath
     */
    public String getDbFilePath() {
        return dbFilePath;
    }

    /**
     * This method will set the db file path
     * @param dbFilePath
     */
    public void setDbFilePath(String dbFilePath) {
        this.dbFilePath = dbFilePath;
    }

    /**
     * This method will return the license parser path
     * @return licParserPath
     */
    public String getLicParserPath() {
        return licParserPath;
    }

    /**
     * This method will set the license parser path
     * @param licParserPath
     */
    public void setLicParserPath(String licParserPath) {
        this.licParserPath = licParserPath;
    }

    /**
     * This method will return the configuration file path
     *
     * @return configuration file path
     */
    public String getConfigPath() {
        return configPath;
    }

    /**
     * This method will set the configuration file path
     *
     * @param configPath
     */
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    /**
     * retrieve cardType
     * @return cardType
     */
    public int getCardType() {
        return cardType;
    }

    /**
     * sets cardType
     * @param cardType
     */
    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    /**
     * This method will return the active release dir
     * @return activeRelDir
     */
    public String getActiveRelDir() {
        return activeRelDir;
    }

    /**
     * This method will set the active release dir
     * @param activeRelDir
     */
    public void setActiveRelDir(String activeRelDir) {
        this.activeRelDir = activeRelDir;
    }

    /**
     * This method will return the rmi port
     * @return rmiPort
     */
    public int getRmiPort() {
        return rmiPort;
    }

    /**
     * This method will set the rmi port
     * @param rmiPort
     */
    public void setRmiPort(int rmiPort) {
        this.rmiPort = rmiPort;
    }

    /**
     * This method will return the file path for the monitor properties file
     * @return monitorFilePath
     */
    public String getMonitorFilePath() {
        return monitorFilePath;
    }

    /**
     * This method will set the file path for the monitor properties file
     * @param monitorFilePath file path for the monitor properties file
     */
    public void setMonitorFilePath(String monitorFilePath) {
        this.monitorFilePath = monitorFilePath;
    }


    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuffer ret = new StringBuffer("[KnInitializerConfigData]-> Module Name : ");
        ret.append(moduleName).append(", ");
        ret.append(DB_FILE_PATH_TAG).append(": ").append(dbFilePath).append(", ");
        ret.append(CONF_FILE_TAG).append(": ").append(configPath);
        ret.append(LIC_PARSER_PATH_TAG).append(": ").append(licParserPath).append(", ");
        ret.append(ACTIVE_RELEASE_DIR_TAG).append(": ").append(activeRelDir).append(", ");
        ret.append(RMI_PORT_TAG).append(": ").append(rmiPort);
        return ret.toString();
    }
}
