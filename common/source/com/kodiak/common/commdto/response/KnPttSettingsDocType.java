package com.kodiak.common.commdto.response;

import com.kodiak.common.dto.IIdentifier;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
public class KnPttSettingsDocType implements Serializable {

    private static final long serialVersionUID = 7526471151122676146L;

    @XmlAttribute(name = "url")
    private String url;
    @XmlAttribute(name = "etag")
    private long etag;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getEtag() {
        return etag;
    }

    public void setEtag(long etag) {
        this.etag = etag;
    }

    @Override
    public String toString() {
        return "KnPttSettingsDocType{" +
                "url='" + url + '\'' +
                ", etag='" + etag + '\'' +
                '}';
    }

}
