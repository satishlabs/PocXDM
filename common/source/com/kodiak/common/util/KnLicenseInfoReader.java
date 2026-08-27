/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Properties;

import com.kodiak.licenseparser.api.KnLicenseParser;
import com.kodiak.licenseparser.beans.KnLicenseFeatureInfo;


public class KnLicenseInfoReader
{
    private static KnLicenseInfoReader infoReader = null;
    private KnLicenseParser licParser = null;

    private KnLicenseInfo licenseInfo = null;
    public static final int rtxCard = KnLicenseParser.rtxCard;
    public static final int emsCard = KnLicenseParser.emsCard;
    public static final int otherCard = KnLicenseParser.otherCard;

    private KnLicenseInfoReader()
    {
        licParser = KnLicenseParser.getInstance();
        licenseInfo = KnLicenseInfo.getInstance();
    }

    public static KnLicenseInfoReader getInstance()
    {
        if (infoReader == null)
        {
            infoReader = new KnLicenseInfoReader();
        }
        return infoReader;
    }

    public boolean parserLicense(int type, String dbFilePath)
    {
        try
        {
            if (licParser == null)
            {
                licParser = KnLicenseParser.getInstance();
            }
            licParser.setDbPropsFile(dbFilePath);
            licParser.init(type);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        if (licenseInfo == null)
        {
            licenseInfo = KnLicenseInfo.getInstance();
        }
        try
        {

            licenseInfo.setCustomerName(licParser.getCustomerName());
            licenseInfo.setDisable(licParser.isDisable());
            licenseInfo.setFeatureBitPositions(licParser.getFeatureBitPositions());
            licenseInfo.setNoOfSubs(licParser.getNoOfSubs());
            licenseInfo.setPocbitsets(licParser.getPocbitsets());
            licenseInfo.setSubsPerFeature(licParser.getSubsPerFeature());
            licenseInfo.setVersion(licParser.getVersion());

            ArrayList featureList = new ArrayList(64);
            ArrayList sellableFeatureList = new ArrayList(64);
            Properties subsCountEnabledList = new Properties();
            KnLicenseFeatureInfo features[]=licParser.getFeatures();
            for(int j=0;j<features.length;j++){
                featureList.add(features[j].getFeatureName());
                if(features[j].getFeaureBitSubType().equalsIgnoreCase("Sellable")){
                    sellableFeatureList.add(features[j].getFeatureName());
                }
                if(features[j].isSubsCountEnabled()) {
                    subsCountEnabledList.put(features[j].getFeaureBitType(), features[j].getFeatureName());
                }

            }
            licenseInfo.setFeatureList(featureList);
            licenseInfo.setSellableFeatureList(sellableFeatureList);
            licenseInfo.setSubsCountEnFeaturesList(subsCountEnabledList);
            //System.out.println("========featureList=================="+featureList);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * generic method to accept db connection params as properties for license init from Webapps
     * and for redirection from EMS license init.
     * @param type
     * @param dbProps
     * @param licenseParserFilePath
     * @return
     */
    public boolean parserLicense(int type, Properties dbProps, String licenseParserFilePath) {
        try
        {
            if (licParser == null)
            {
                licParser = KnLicenseParser.getInstance();
            }
            licParser.setLicenseParserFilePath(licenseParserFilePath);
            licParser.setDbProps(dbProps);
            if (type == 1)
                licParser.setDbPropsFile(dbProps.getProperty("DBFILEPATH"));
            licParser.init(type);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        if (licenseInfo == null)
        {
            licenseInfo = KnLicenseInfo.getInstance();
        }
        try
        {

            licenseInfo.setCustomerName(licParser.getCustomerName());
            licenseInfo.setDisable(licParser.isDisable());
            licenseInfo.setFeatureBitPositions(licParser.getFeatureBitPositions());
            licenseInfo.setNoOfSubs(licParser.getNoOfSubs());
            licenseInfo.setPocbitsets(licParser.getPocbitsets());
            licenseInfo.setSubsPerFeature(licParser.getSubsPerFeature());
            licenseInfo.setVersion(licParser.getVersion());
            ArrayList sellableFeatureList = new ArrayList(64);
            ArrayList featureList = new ArrayList(64);
            KnLicenseFeatureInfo features[]=licParser.getFeatures();
            for(int j=0;j<features.length;j++){
                featureList.add(features[j].getFeatureName());
                if(features[j].getFeaureBitSubType().equalsIgnoreCase("Sellable")){
                    sellableFeatureList.add(features[j].getFeatureName());
                }
            }
            licenseInfo.setFeatureList(featureList);
            licenseInfo.setSellableFeatureList(sellableFeatureList);
//            System.out.println("========featureList=================="+featureList);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * overloaded method to accept dbmgr.props as file path to read the db connection properties
     * for license init from EMS.
     * @param type
     * @param dbFilePath
     * @param licenseParserFilePath
     * @return
     */
    public boolean parserLicense(int type, String dbFilePath, String licenseParserFilePath)
    {
        boolean status = false;
        Properties dbProps = null;
        FileInputStream fStrteam = null;
        try {
            try {
                dbProps = new Properties();
                fStrteam = new FileInputStream(dbFilePath);
                dbProps.load(fStrteam);
                dbProps.setProperty("DBFILEPATH", dbFilePath);
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fStrteam.close();
            }
            if (type == 1)
                status = parserLicense(type, dbProps, licenseParserFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public KnLicenseInfo getLicenseInfo()
    {
        return licenseInfo;
    }

}
