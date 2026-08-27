/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.cb.util;

import java.util.List;

/**
 * nodePath = $ (for the field at root level of object).
 *          = <nested node object name>
 * fieldNameList = fieldName to be encrypted
 * nodeType = needs to be populated only for nested json -- array or object
 * Example: Employee{ if i want to encrypt name at root level, name in address node and name and ID in department.
 *  {"name":"XXX-2021-06-22T10:19:59.955286Z-121212","address":{"name":"name","city":"Bengaluru","street":"Hebbal"},"departments":[{"name":"IT","id":1},{"name":"FACILITY","id":2}],"id":"121212","type":"sdk3","replicant":true} ]
  }
 --encryptDecryptMetaData1.setNodePath("$");
 List<String> fieldList1 = new ArrayList<>();
 fieldList1.add("name");
 encryptDecryptMetaData1.setFieldNameList(fieldList2);
 --encryptDecryptMetaData2.setNodePath("address");
 encryptDecryptMetaData2.setNodeType("object");
 List<String> fieldList2 = new ArrayList<>();
 fieldList2.add("name");
 encryptDecryptMetaData2.setFieldNameList(fieldList3);
 --encryptDecryptMetaData3.setNodePath("departments");
 encryptDecryptMetaData3.setNodeType("array");
 List<String> fieldList3 = new ArrayList<>();
 fieldList3.add("name");
 fieldList3.add("id");
 */
public class KnFieldMetaData {
    private String nodePath;
    private List<String> fieldNameList;
    private String nodeType;

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    public List<String> getFieldNameList() {
        return fieldNameList;
    }

    public void setFieldNameList(List<String> fieldNameList) {
        this.fieldNameList = fieldNameList;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public String toString() {
        return "FieldMetaData{" +
                "nodePath='" + nodePath + '\'' +
                ", fieldNameList=" + fieldNameList +
                ", nodeType='" + nodeType + '\'' +
                '}';
    }
}
