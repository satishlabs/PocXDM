/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnXmlLoaderConfigData.java
 * Subsystem:   Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
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
 * you entered into with Kodiak Networks.
 **************************************************************************/
package com.kodiak.xdms.server.common.configuration.loader.xml;


import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.configuration.loader.IModuleLoaderConfigData;
import com.kodiak.xdms.server.common.configuration.init.KnInitLoaderConfigException;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This stores the loader configuration data for XML loaders.
 */
public class KnXmlLoaderConfigData implements IModuleLoaderConfigData {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXmlLoaderConfigData.class);

    private static final String CLASS = KnXmlLoaderConfigData.class.getName();

    /**
     * the below constants are used for parsing the xml.
     * the xml for xml loader will be as follows
     * <xml>
     * <xmlFile> /home/kodiak/Sample.xml </xmlFile>
     * <xsdFile> /home/kodiak/Sample.xsd </xsdFile>
     * </xml>
     */
    public static final String XML_TAG = "xml";
    public static final String XML_FILE_TAG = "xml-file";
    public static final String XSD_FILE_TAG = "xsd-file";
    public static final String UNIQUE_ID_LABEL_TAG = "unique-identifer-label";

    /**
     * stores the xmlFile
     */
    private String xmlFile[];

    /**
     * stores the xsdFile
     */
    private String xsdFile;

    /**
     * stores the name of module
     */
    private String moduleName;

    /**
     * store the uniqueId for tag name
     */
    private String idLabel;

    /**
     * This method will populateDto the fields from the dom element.
     * The element object passed to this method will be a dom
     * representation of the tag <loader-config-data>
     *
     * @param element dom object of the tag '<loader-config-data>'
     * @throws KnInitLoaderConfigException if the loader configuration data
     *                                     is missing or invalid
     */
    public void populate(Element element) throws KnInitLoaderConfigException {
        NodeList xmlNodeList = null;
        Element xmlElement = null;
        NodeList nodeList;
        Node node;
        String value;

        knLogger.debug( "populateDto",
                "populating loader config data...");
        try {
            xmlNodeList = element.getElementsByTagName(XML_TAG);
            if (xmlNodeList.getLength() == 1) {
                xmlElement = (Element) xmlNodeList.item(0);
            } else if (xmlNodeList.getLength() == 0) {
                knLogger.error( "populateDto",
                        "The <" + XML_TAG + "> tag is missing for module " + moduleName);
                throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                        "<" + XML_TAG + "> tag is missing",
                        XML_TAG, null, moduleName);
            } else {
                knLogger.error( "populateDto",
                        "The <" + XML_TAG + "> tag is repeating for module " + moduleName);
                throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                        "<" + XML_TAG + "> tag is repeating",
                        XML_TAG, null, moduleName);
            }

            nodeList = xmlElement.getElementsByTagName(XML_FILE_TAG);
            if (nodeList.getLength() == 1) {
                node = nodeList.item(0);
                value = node.getFirstChild().getNodeValue();
                if (value == null || value.trim().equals("")) {
                    knLogger.error( "populateDto",
                            "The <" + XML_FILE_TAG + "> tag under <" + XML_TAG + "> tag is empty for module " + moduleName);
                    throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                            "<" + XML_FILE_TAG + "> tag is empty",
                            XML_FILE_TAG, value, moduleName);
                } else {
                    this.xmlFile = value.split(",");
                }
            } else if (nodeList.getLength() == 0) {
                knLogger.error( "populateDto",
                        "The <" + XML_FILE_TAG + "> tag under <" + XML_TAG + "> tag is missing for module " + moduleName);
                throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                        "<" + XML_FILE_TAG + "> tag missing", XML_FILE_TAG, null, moduleName);
            } else {
                knLogger.error( "populateDto",
                        "The <" + XML_FILE_TAG + "> tag under <" + XML_TAG + "> tag is repeating for module " + moduleName);
                throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                        "More than one <" + XML_FILE_TAG + "> tag", XML_FILE_TAG, null, moduleName);
            }

            /*
            nodeList = xmlElement.getElementsByTagName(XSD_FILE_TAG);
            if (nodeList.getLength() == 1) {
                node = nodeList.item(0);
                value = node.getFirstChild().getNodeValue();
                if (value == null || value.trim().equals("")) {
                    knLogger.error( "populateDto",
                            "The <xsdFile> tag under " + XML_TAG + " tag is empty for module " + moduleName);
                    throw new KnInitLoaderConfigException("",
                            "<xsdFile> tag is empty",
                            XSD_FILE_TAG, value, moduleName);
                } else {
                    this.xsdFile = value;
                }
            } else if (nodeList.getLength() == 0) {
                knLogger.error( "populateDto",
                        "The <xsdFile> tag under " + XML_TAG + " tag is missing for module " + moduleName);
                throw new KnInitLoaderConfigException("",
                        "<xsdFile> tag is missing",
                        XSD_FILE_TAG, null, moduleName);
            } else {
                knLogger.error( "populateDto",
                        "The <xsdFile> tag under " + XML_TAG + " tag is repeating for module " + moduleName);
                throw new KnInitLoaderConfigException("",
                        "<xsdFile> tag is repeating",
                        XSD_FILE_TAG, null, moduleName);
            } */

            nodeList = xmlElement.getElementsByTagName(UNIQUE_ID_LABEL_TAG);
            if (nodeList.getLength() == 1) {
                node = nodeList.item(0);
                value = node.getFirstChild().getNodeValue();
                if (value == null || value.trim().equals("")) {
                    knLogger.error( "populateDto",
                            "The <" + UNIQUE_ID_LABEL_TAG + "> tag under <" + XML_TAG + "> tag is empty for module " + moduleName);
                    throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                            "<" + UNIQUE_ID_LABEL_TAG + "> tag is empty", UNIQUE_ID_LABEL_TAG, value, moduleName);
                } else {
                    this.idLabel = value;
                }
            } else if (nodeList.getLength() == 0) {
                knLogger.error( "populateDto",
                        "The <" + UNIQUE_ID_LABEL_TAG + "> tag under <" + XML_TAG + "> tag is missing for module " + moduleName);
                throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                        "The <" + UNIQUE_ID_LABEL_TAG + "> tag missing", UNIQUE_ID_LABEL_TAG, null, moduleName);
            } else {
                knLogger.error( "populateDto",
                        "The <" + UNIQUE_ID_LABEL_TAG + "> tag under <" + XML_TAG + "> tag is repeating for module " + moduleName);
                throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                        "More than one <" + UNIQUE_ID_LABEL_TAG + "> tag", UNIQUE_ID_LABEL_TAG, null, moduleName);
            }

            knLogger.info( "populateDto",
                    "Loader config data populated successfully. Config : " + this);
        } finally {
            xmlNodeList = null;
            xmlElement = null;
            nodeList = null;
            node = null;
            value = null;
        }
    }

    /**
     * This method will returns the xml file path
     *
     * @return the xml file path
     */
    public String[] getXmlFile() {
        return xmlFile;
    }

    /**
     * This method will set the xml file path
     *
     * @param xmlFile the path name for xml file
     */
    public void setXmlFile(String xmlFile) {
        this.xmlFile = xmlFile.split(",");
    }

    /**
     * This method will return the xsd file path.
     *
     * @return the xsd file path
     */
    public String getXsdFile() {
        return xsdFile;
    }

    /**
     * This method will set the xsd file path
     *
     * @param xsdFile the xsd file path
     */
    public void setXsdFile(String xsdFile) {
        this.xsdFile = xsdFile;
    }

    /**
     * return the Id label
     * @return the label for the attribute id
     */
    public String getIdLabel() {
        return idLabel;
    }

    /**
     * set the id attribute label
     * @param idLabel the label for id attribute
     */
    public void setIdLabel(String idLabel) {
        this.idLabel = idLabel;
    }

    /**
     * This method will set the module name
     *
     * @param moduleName
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p/>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "[KnXmlLoaderConfigData]-> Module Name : " + moduleName
                + ", xml-file : " + xmlFile + ", xsd-file : " + xsdFile
                + ", idLabel : " + idLabel;
    }
}
