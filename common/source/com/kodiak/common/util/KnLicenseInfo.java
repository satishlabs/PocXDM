/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.util;

import java.util.Properties;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

import com.kodiak.licenseparser.beans.KnLicenseFeatureInfo;

public class KnLicenseInfo implements Serializable
{
    private static final long serialVersionUID = 4649172678128973528L;
    private static ArrayList featureList=null;
    private static ArrayList sellableFeatureList=null;

    private boolean disable = false;

    private Properties subsPerFeature = null;

    private String customerName;
    private String version;
    private String noOfSubs;
    private String pocbitsets;
    private Properties featureBitPositions = new Properties();
    private Properties subsCountEnFeaturesList = new Properties();



    //Defining the license feature types
    public static enum LicenseFeaturesTypes {
        AVS("AVS") ,CONNECTED("CONNECTED") ,GLSC("GLSC") ,COMMON("COMMON"), SC("SC"), POC("POC") ;

        String val;
        LicenseFeaturesTypes(String va)
        {
            val = va;
        }
        public String getValue() {
            return val;
        }
    }

    private static KnLicenseInfo licenseInfo = null;

    public static KnLicenseInfo getInstance()
    {
        if (licenseInfo == null)
        {
            licenseInfo = new KnLicenseInfo();
        }
        return licenseInfo;
    }

    private KnLicenseInfo()
    {

    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public void setFeatureBitPositions(Properties featureBitPositions) {
        this.featureBitPositions = featureBitPositions;
    }

    public static void setLicenseInfo(KnLicenseInfo licenseInfo) {
        KnLicenseInfo.licenseInfo = licenseInfo;
    }

    public void setNoOfSubs(String noOfSubs) {
        this.noOfSubs = noOfSubs;
    }

    public void setSubsPerFeature(Properties subsPerFeature) {
        this.subsPerFeature = subsPerFeature;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCustomerName() {
        return customerName;
    }

    public boolean isDisable() {
        return disable;
    }

    public String getNoOfSubs() {
        return noOfSubs;
    }

    public String getPocbitsets() {
        return pocbitsets;
    }

    public void setPocbitsets(String pocbitsets) {
        this.pocbitsets = pocbitsets;
    }

    public Properties getSubsPerFeature() {
        return subsPerFeature;
    }

    public String getVersion() {
        return version;
    }

    public ArrayList getSellableFeatureList() {
        return sellableFeatureList;
    }

    public void setSellableFeatureList(ArrayList sellableFeatureList) {
        KnLicenseInfo.sellableFeatureList = sellableFeatureList;
    }


    /**
    * This method returns the array of all the feature names of the given type - AVS/CONNECTED/GLSC.
    * If feature type is SC, then whole list is returned
    * @param licFeatureType KnLicenseInfo.LicenseFeaturesTypes
    * @return ArrayList
    */
   public ArrayList getFeaturesOfSubType(KnLicenseInfo.LicenseFeaturesTypes licFeatureType){

         ArrayList tmpFeaturesList = new ArrayList();
         KnLicenseFeatureInfo featureInfo = null;
         String featureName=null;
         boolean pocFeatureType = false;

         if (licFeatureType != null) {
             if (licFeatureType.getValue().equalsIgnoreCase(KnLicenseInfo.LicenseFeaturesTypes.POC.getValue())) {
                pocFeatureType = true;
             }
         }

         for(Iterator ite=featureList.iterator();ite.hasNext();)
         {
             featureName=(String)ite.next();
             featureInfo = (KnLicenseFeatureInfo) featureBitPositions.get(featureName);
             /*
                if AVS, get all AVS features and common features. Same with CONNECTED
                if GLSC, get all GLSC features
             */
             if (pocFeatureType) {
                 if (featureInfo.getFeaureBitType().equalsIgnoreCase(
                         KnLicenseInfo.LicenseFeaturesTypes.POC.getValue())) {
                    tmpFeaturesList.add(featureName);
                 }
             }
         }
         return tmpFeaturesList;
   }

    public boolean isLicensedFeature(String featureName)
    {
        KnLicenseFeatureInfo featureInfo = null;
        try
        {
            featureInfo = (KnLicenseFeatureInfo) featureBitPositions.get(featureName);
            if (featureInfo ==  null)
            {
                return false;
            }
        }
        catch(Exception e)
        {
            return false;
        }
        finally
        {
            featureInfo = null;
        }
        return true;
    }

    public boolean isFeatureEnabled(String featureName)
    {
        KnLicenseFeatureInfo featureInfo = null;
        try
        {
            featureInfo = (KnLicenseFeatureInfo) featureBitPositions.get(featureName);
            return featureInfo.isFeatureEnabled();
        }
        catch(Exception e)
        {
            return false;
        }
        finally
        {
            featureInfo = null;
        }
    }

     public String getFeatureDisplayString(String featureName)
    {
        KnLicenseFeatureInfo featureInfo = null;
        try
        {
            featureInfo = (KnLicenseFeatureInfo) featureBitPositions.get(featureName);
            return featureInfo.getFeatureDisplayString();
        }
        catch(Exception e)
        {
            return null;
        }
        finally
        {
            featureInfo = null;
        }
    }

    public boolean isDependentFeature(String featureName)
    {
        KnLicenseFeatureInfo featureInfo = null;
        try
        {
            featureInfo = (KnLicenseFeatureInfo) featureBitPositions.get(featureName);
            return featureInfo.getDependsOnFeature()!=null;
        }
        catch(Exception e)
        {
            return false;
        }
        finally
        {
            featureInfo = null;
        }
    }

    public long getSubsCountPerFeature(String featureName)
    {
        KnLicenseFeatureInfo featureInfo = null;
        try
        {
            featureInfo = (KnLicenseFeatureInfo) featureBitPositions.get(featureName);
            return featureInfo.getSubsCount();
        }
        catch(Exception e)
        {
            return -1;
        }
        finally
        {
            featureInfo = null;
        }
    }

    public int getFeatureBitPosition(String featureName)
    {
        KnLicenseFeatureInfo featureInfo = null;
        try
        {
            featureInfo = (KnLicenseFeatureInfo) featureBitPositions.get(featureName);
            return featureInfo.getFeaureBitPosition();
        }
        catch(Exception e)
        {
            return -1;
        }
        finally
        {
            featureInfo = null;
        }
    }

    public String getFeatureBitType(String featureName)
    {
        KnLicenseFeatureInfo featureInfo = null;
        try
        {
            featureInfo = (KnLicenseFeatureInfo) featureBitPositions.get(featureName);
            return featureInfo.getFeaureBitType();
        }
        catch(Exception e)
        {
            return null;
        }
        finally
        {
            featureInfo = null;
        }
    }
    public ArrayList getFeatureList()
    {
        return featureList;
    }

    public ArrayList setFeatureList(ArrayList list)
    {
        return featureList=list;
    }

    /**
     * get enabled subscriber count for features based on the required feature type (AVS/CONNECTED)
     * if nothing is passed as param value/NULL, then returns all the values
     * @param featureType
     * @return Properties containing feature type as key and feature name as values
     */
    public Properties getSubsCountEnFeaturesList(KnLicenseInfo.LicenseFeaturesTypes featureType) {
        Properties tempProps = new Properties();

        if (featureType != null) {
            if (featureType.getValue().equalsIgnoreCase(
                    KnLicenseInfo.LicenseFeaturesTypes.POC.getValue()))
            {
                tempProps.put(KnLicenseInfo.LicenseFeaturesTypes.POC.getValue(), (String) subsCountEnFeaturesList.get(
                        KnLicenseInfo.LicenseFeaturesTypes.POC.getValue()));
            }
        } else {
            tempProps = subsCountEnFeaturesList;
        }
        return tempProps;
    }

    public void setSubsCountEnFeaturesList(Properties subsCountEnFeaturesList) {
        this.subsCountEnFeaturesList = subsCountEnFeaturesList;
    }


   /**
    *  dump the whole data that is collected and decrypted from the license
    * @return string format of the data
    */
   public String toString()
   {
      StringBuffer sb = new StringBuffer();

      sb.append("\n");
      sb.append("ISDISABLED:");
      sb.append(disable);
      sb.append("\n");

      sb.append("customerName:");
      sb.append(customerName);
      sb.append("\n");

      sb.append("version:");
      sb.append(version);
      sb.append("\n");

      sb.append("noOfSubs:");
      sb.append(noOfSubs);
      sb.append("\n");

      sb.append("pocbitsets:");
      sb.append(pocbitsets);
      sb.append("\n");

      sb.append("subsPerFeature:");
      sb.append(subsPerFeature.toString());
      sb.append("\n");

      sb.append("featureBitPositions:");
      sb.append(featureBitPositions);
      sb.append("\n");

      return sb.toString();
   }
}
