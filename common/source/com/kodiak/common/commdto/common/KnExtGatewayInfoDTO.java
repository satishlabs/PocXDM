package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

public class KnExtGatewayInfoDTO implements IIdentifier {
    private static final long serialVersionUID = 7533061164832163337L;
    private String gwId;
    private String gwName;
    private int gwType;
    private int gwSipType;
    private String uri;
    private String gwFqdnPrimary;
    private String gwFqdnGeo;
    private String gwPrimSipIp;
    private String gwGeoSipIp;
    private int gwSipConnType;
    private int gwInterfaceType;
    private int gwStatus;


    /**
     * getter method for the Gateway Id
     *
     * @return String
     */
    public String getGwId() {
        return gwId;
    }

    /**
     * setter method for the Corporation Id
     *
     * @param gwId int
     */
    public void setGwId(String gwId) {
        this.gwId = gwId;
    }
    /**
     * getter method for the Gateway Name
     *
     * @return String
     */
    public String getGwName() {
        return gwName;
    }
    /**
     * setter method for the Gateway Name
     *
     * @param gwName
     */
    public void setGwName(String gwName) {
        this.gwName = gwName;
    }
    /**
     * getter method for the Gateway Type
     *
     * @return int
     */
    public int getGwType() {
        return gwType;
    }
    /**
     * setter method for the Gateway Type
     *
     * @param gwType
     */
    public void setGwType(int gwType) {
        this.gwType = gwType;
    }
    /**
     * getter method for the Gateway SIP Type
     *
     * @return int
     */
    public int getGwSipType() {
        return gwSipType;
    }
    /**
     * setter method for the Gateway SIP Type
     *
     * @param gwSipType
     */
    public void setGwSipType(int gwSipType) {
        this.gwSipType = gwSipType;
    }
    /**
     * getter method for the Gateway uri
     *
     * @return String
     */
    public String getUri() {
        return uri;
    }
    /**
     * setter method for the Gateway uri
     *
     * @param uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }
    /**
     * getter method for the Gateway fqdn primary
     *
     * @return String
     */
    public String getGwFqdnPrimary() {
        return gwFqdnPrimary;
    }
    /**
     * setter method for the Gateway fqdn primary
     *
     * @param gwFqdnPrimary
     */
    public void setGwFqdnPrimary(String gwFqdnPrimary) {
        this.gwFqdnPrimary = gwFqdnPrimary;
    }
    /**
     * getter method for the Gateway fqdn Geo
     *
     * @return String
     */
    public String getGwFqdnGeo() {
        return gwFqdnGeo;
    }
    /**
     * setter method for the Gateway fqdn Geo
     *
     * @param gwFqdnGeo
     */
    public void setGwFqdnGeo(String gwFqdnGeo) {
        this.gwFqdnGeo = gwFqdnGeo;
    }
    /**
     * getter method for the Gateway Primary SIP IP
     *
     * @return String
     */
    public String getGwPrimSipIp() {
        return gwPrimSipIp;
    }
    /**
     * setter method for the Gateway Primary SIP IP
     *
     * @param gwPrimSipIp
     */
    public void setGwPrimSipIp(String gwPrimSipIp) {
        this.gwPrimSipIp = gwPrimSipIp;
    }
    /**
     * getter method for the Gateway GEO SIP IP
     *
     * @return String
     */
    public String getGwGeoSipIp() {
        return gwGeoSipIp;
    }
    /**
     * setter method for the Gateway GEO SIP IP
     *
     * @param gwGeoSipIp
     */
    public void setGwGeoSipIp(String gwGeoSipIp) {
        this.gwGeoSipIp = gwGeoSipIp;
    }
    /**
     * getter method for the Gateway SIP Conn Type
     *
     * @return int
     */
    public int getGwSipConnType() {
        return gwSipConnType;
    }
    /**
     * setter method for the Gateway GEO SIP IP
     *
     * @param gwSipConnType
     */
    public void setGwSipConnType(int gwSipConnType) {
        this.gwSipConnType = gwSipConnType;
    }
    /**
     * getter method for the Gateway Interface Type
     *
     * @return int
     */
    public int getGwInterfaceType() {
        return gwInterfaceType;
    }
    /**
     * setter method for the Gateway GEO SIP IP
     *
     * @param gwInterfaceType
     */
    public void setGwInterfaceType(int gwInterfaceType) {
        this.gwInterfaceType = gwInterfaceType;
    }
    /**
     * getter method for the Gateway status
     *
     * @return int
     */
    public int getGwStatus() {
        return gwStatus;
    }
    /**
     * setter method for the Gateway status
     *
     * @param gwStatus
     */
    public void setGwStatus(int gwStatus) {
        this.gwStatus = gwStatus;
    }

    @Override
    public String toString() {
        return "KnExtGatewayInfoDTO{" +
                "gwId='" + gwId + '\'' +
                ", gwName='" + gwName + '\'' +
                ", gwType=" + gwType +
                ", gwSipType=" + gwSipType +
                ", uri='" + uri + '\'' +
                ", gwFqdnPrimary='" + gwFqdnPrimary + '\'' +
                ", gwFqdnGeo='" + gwFqdnGeo + '\'' +
                ", gwPrimSipIp='" + gwPrimSipIp + '\'' +
                ", gwGeoSipIp='" + gwGeoSipIp + '\'' +
                ", gwSipConnType=" + gwSipConnType +
                ", gwInterfaceType=" + gwInterfaceType +
                ", gwStatus=" + gwStatus +
                '}';
    }

    @Override
    public String getObjectId() {
        return gwId;
    }
}
