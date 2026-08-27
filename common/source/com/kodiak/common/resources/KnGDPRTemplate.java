/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.resources;


import org.springframework.util.CollectionUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.*;

public class KnGDPRTemplate {

	static String TEMPLATE_PLACEHOLDER = "#DATA#";
	static String TAG_NAME = "Tag-NAME:(#DATA#)";
	static String TAG_MDN = "Tag-MDN:(#DATA#)";
	static String TAG_EMAIL = "Tag-EMAILADDR:(#DATA#)";
	static String TAG_USERID = "Tag-USERID:(#DATA#)";
	static String TAG_IP = "Tag-IPADDR:(#DATA#)";
	static String TAG_CONTACT = "Tag-CONTACT:(#DATA#)";
	static String TAG_MCPTTID = "Tag-MCPTTID:(#DATA#)";
	static String TAG_MCVIDEOID = "Tag-MCVIDEOID:(#DATA#)";
	static String TAG_MCDATAID = "Tag-MCDATAID:(#DATA#)";
	static String TAG_MCID = "Tag-MCID:(#DATA#)";
	static String TAG_LOCINFO_LAT = "Tag-LOCINFO-LAT:(#DATA#)";
	static String Tag_LOCINFO_LON = "Tag-LOCINFO-LON:(#DATA#)";
	static String TAG_MDN_REGX = "Tag-MDN:\\($1\\)";
	static String NUMERIC_GROUP_REGX =  "([0-9]+)";
	static String TAG_MDN_PTX_ID_REGX = "$1Tag-MDN:\\($2\\)";
	static String PTX_ID_REGX =  "(s)([0-9]{7,})";
	static String TAG_JSON = "Tag-JSON:(#DATA#)";

	public static String name(String data) {
		return populate(data, TAG_NAME);
	}

	public static String mdn(String data) {
		return populate(data, TAG_MDN);
	}

	public static List<String> mdnList(List<String> mdnList) { return populateList(mdnList, TAG_MDN); }

	public static List<String> profileMdn(List<String> profileMdn) {
		return populateList(profileMdn, TAG_MDN);
	}

	public static Collection<String> mdnList(Collection<String> mdnList) { return populateList(mdnList, TAG_MDN); }

	public static LinkedList<String> mdnList(LinkedList<String> mdnList) { return populateList(mdnList, TAG_MDN); }
	
    public static Collection<List<String>> mdnLists(Collection<List<String>> mdnLists){return populateLists(mdnLists, TAG_MDN);}
		
	public static Set<String> mdnSet(Set<String> mdnSet){return populateSet(mdnSet, TAG_MDN);}

	private static Collection<List<String>> populateLists(Collection<List<String>> data, String tag) {
		// TODO Auto-generated method stub
		if (data == null || data.isEmpty()) {
			return data;
		}
		else {
			Collection<List<String>> gdprTemplateObjects = new ArrayList<List<String>>();
			for(List<String> str: data) {
				gdprTemplateObjects.add(populateList(str, TAG_MDN));
			}
			return gdprTemplateObjects;
		}
		
	}

	public static String email(String data) {
		return populate(data, TAG_EMAIL);
	}

	public static String ip(String data) {
		return populate(data, TAG_IP);
	}

	public static String userId(String data) {
		return populate(data, TAG_USERID);
	}

	public static List<String> userIdList(List<String> userIds) { return populateList(userIds,TAG_USERID ); }

	public static String contact(String data) {
		return populate(data, TAG_CONTACT);
	}

	public static String mcpttId(String data) {
		return populate(data, TAG_MCPTTID);
	}

	public static String json(String data){
		return populate(data,TAG_JSON);
	}

	public static List<String> mcPttIdList(List<String> mcpttIds) { return populateList(mcpttIds, TAG_MCPTTID); }

	public static Set<String> mcPttIdSetList(Set<String> mcPttIdSetList){return populateSet(mcPttIdSetList,TAG_MCPTTID);}

	public static String mcdataId(String data) { return populate(data, TAG_MCDATAID); }

	public static List<String> mcDataIdList(List<String> mcDataIds) { return populateList(mcDataIds, TAG_MCDATAID); }


	public static String mcvideoId(String data) {
		return populate(data, TAG_MCVIDEOID);
	}

	public static String mcId(String data) {
		return populate(data, TAG_MCID);
	}

	public static String latitude(String data){return populate(data,TAG_LOCINFO_LAT); }

	public static String longitude(String data){return populate(data,Tag_LOCINFO_LON); }

	public static String mdnPart(String data){
			return tagMdnPart(data,NUMERIC_GROUP_REGX, TAG_MDN_REGX);
	}

	public static String mdnPartOfPtxId(String data){
		return tagMdnPart(data, PTX_ID_REGX, TAG_MDN_PTX_ID_REGX);
	}

	public static Collection<String> mdnPartList(Collection<String> data){
		return mdnPartList(data, NUMERIC_GROUP_REGX, TAG_MDN_REGX);
	}

	public static Collection<String> mdnPartPtxIdList(Collection<String> data){
		return mdnPartList(data, PTX_ID_REGX, TAG_MDN_PTX_ID_REGX);
	}

	public static Collection<String> mdnPartList(Collection<String> data, String matcher, String tagMdn){
		List<String> gdprTemplateObject = null;
		if (CollectionUtils.isEmpty(data)) {
			return data;
		} else {
			gdprTemplateObject = new ArrayList();
			for (String str : data) {
				if (str != null && !str.isEmpty()) {
					gdprTemplateObject.add(tagMdnPart(str, matcher, tagMdn));
				}
			}
		}
		return gdprTemplateObject;
	}

	private static <K> Map<K, Collection<String>> populateMapMdnAsValue(Map<K,Collection<String>> mapdata, String tagMdn){
		if (CollectionUtils.isEmpty(mapdata)) {
			return mapdata;
		} else {
			Map<K , Collection<String>> gdprTemplateMap =mapdata;
			for (Map.Entry<K, Collection<String>> entry : gdprTemplateMap.entrySet())
			{
				Collection<String> mdnList =entry.getValue();
				KnGDPRTemplate.mdnList(mdnList);
			}
			return gdprTemplateMap;
		}
	}
	private static <K> Map<K, List<String>> populateMapMdnAsListValue(Map<K,List<String>> mapData, String tagMdn){
		if (CollectionUtils.isEmpty(mapData)) {
			return mapData;
		} else {
			Map<K , List<String>> gdprTemplateMap =mapData;
			for (Map.Entry<K, List<String>> entry : gdprTemplateMap.entrySet())
			{
				Collection<String> mdnList =entry.getValue();
				KnGDPRTemplate.mdnList(mdnList);
			}
			return gdprTemplateMap;
		}
	}
	private static <K> Map<K, LinkedList<String>> populateMapMdnAsLinkedValue(Map<K,LinkedList<String>> mapData, String tagMdn){
		if (CollectionUtils.isEmpty(mapData)) {
			return mapData;
		} else {
			Map<K , LinkedList<String>> gdprTemplateMap =mapData;
			for (Map.Entry<K, LinkedList<String>> entry : gdprTemplateMap.entrySet())
			{
				Collection<String> mdnList =entry.getValue();
				KnGDPRTemplate.mdnList(mdnList);
			}
			return gdprTemplateMap;
		}
	}

	private static <K> Map<K ,Map<String ,Collection<String>>> populateMapMdnOfMap(Map<K , Map<String , Collection<String>>> mapData, String tagMdn){
		if (CollectionUtils.isEmpty(mapData)) {
			return mapData;
		} else {
			Map<K , Map<String , Collection<String>>> gdprTemplateMap = mapData;
			mapData.forEach((obj , var )-> {
				gdprTemplateMap.put(obj,populateMapMdnAsValue(var,tagMdn));
			});
			return gdprTemplateMap;
		}
	}

	/**
	 *
	 * @param data string having mdn in middle of string
	 *             ex: s12142142001_ptx1600905854182_meta.doc, PTX-919500120326-meta-CH, s919500120325_ptxAuditJobs.doc,s913333000001_ptx1601882387713_a1601882387728_attachment.doc
	 * @return GDPR compliant string
	 */
	private static String tagMdnPart(String data, String matcher, String tagMdn){
		if(isBlank(data)){
			return data;
		}
		return data.replaceAll(matcher, tagMdn);
	}

	/**
	 *
	 * @param mdnDetails Map having key as mdn
	 * @return GDPR compliant map
	 */
	public static Map<String , Object> mdnDetailsMap(Map<String,Object> mdnDetails){return populateMap(mdnDetails , TAG_MDN); }

	public static <V> Map<String , V> mapKeyMdn(Map<String, V> mdnDetails ){return tagMdnMapKey(mdnDetails , TAG_MDN); }
	
	public static <K> Map< K ,Collection<String>> mapMdnAsValue(Map<K,Collection<String>> mdnMap){return populateMapMdnAsValue(mdnMap,TAG_MDN);}

	public static <K> Map< K , List<String>> mapMdnAsListValue(Map<K ,List<String>> mdnMap){return populateMapMdnAsListValue(mdnMap,TAG_MDN);}

	public static <K> Map<K , LinkedList<String>> mapMdnAsLinkedValue(Map<K , LinkedList<String>> mdnMap) {return populateMapMdnAsLinkedValue(mdnMap,TAG_MDN);}

	public static <K> Map<K,Map<String , Collection<String>>> mapMdnOfMap(Map<K,Map <String ,Collection<String>>> mdnMap){return populateMapMdnOfMap(mdnMap,TAG_MDN);}

	private static <V> Map<String , V> tagMdnMapKey(Map<String , V> data, String tag) {
		if (CollectionUtils.isEmpty(data)) {
			return data;
		} else {
			Map<String , V> gdprTemplateObject = new HashMap<>();
			data.forEach((str , obj)-> {
				gdprTemplateObject.put(str != null ? tag.replace(TEMPLATE_PLACEHOLDER , str) : null , obj);
			});
			return gdprTemplateObject;
		}
	}

	private static String populate(String data, String tag) {
		if (isNull(data)) {
			return tag.replace(TEMPLATE_PLACEHOLDER, String.valueOf(data));
		} else {
			return tag.replace(TEMPLATE_PLACEHOLDER, data);
		}
	}

	private static List<String> populateList(List<String> data, String tag) {
		if (data == null || data.isEmpty()) {
			return data;
		} else {
			List<String> gdprTemplateObject = new ArrayList();
			for (String str : data) {
			    if(str != null && !str.isEmpty()) {
                    gdprTemplateObject.add(tag.replace(TEMPLATE_PLACEHOLDER, str));
                }
			}
			return gdprTemplateObject;
		}
	}

	private static Collection<String> populateList(Collection<String> data, String tag) {
		if (data == null || data.isEmpty()) {
			return data;
		} else {
			Collection<String> gdprTemplateObject = new ArrayList();
			for (String str : data) {
				gdprTemplateObject.add(tag.replace(TEMPLATE_PLACEHOLDER, str));
			}
			return gdprTemplateObject;
		}
	}


	private static LinkedList<String> populateList(LinkedList<String> data, String tag) {
		if (data == null || data.isEmpty()) {
			return data;
		} else {
			LinkedList<String> gdprTemplateObject = new LinkedList<>();
			for (String str : data) {
				gdprTemplateObject.add(tag.replace(TEMPLATE_PLACEHOLDER, str));
			}
			return gdprTemplateObject;
		}
	}

	private static Map<String,Object> populateMap(Map<String , Object> data, String tag) {
		if (CollectionUtils.isEmpty(data)) {
			return data;
		} else {
			Map<String , Object> gdprTemplateObject = new HashMap<>();
			data.forEach((str , obj)-> {
				gdprTemplateObject.put(str != null ? tag.replace(TEMPLATE_PLACEHOLDER , str) : null , obj);
			});
			return gdprTemplateObject;
		}
	}
	
	private static Set<String> populateSet(Set<String> data,String tag){
		if(CollectionUtils.isEmpty(data)){
			return data;
		}else {
			Set<String> gdprTemplateObject = new HashSet<>();
			data.forEach(str -> {
				gdprTemplateObject.add(str != null ? tag.replace(TEMPLATE_PLACEHOLDER, str) : null);
			});
			return gdprTemplateObject;
		}
	}


	private static boolean  isNull(String data) {
		return data == null;
	}


	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 *
	 * @param  Map having key and value as mdn
	 * @return GDPR compliant map
	 */
	public static Map<String , String> mdnMap(Map<String,String> mdnMap){return populateMdnMap(mdnMap , TAG_MDN); }
	
	private static Map<String,String> populateMdnMap(Map<String , String> data, String tag) {
		if (CollectionUtils.isEmpty(data)) {
			return data;
		} else {
			Map<String , String> gdprTemplateObject = new HashMap<>();
			data.forEach((key , value)-> {
				gdprTemplateObject.put( key != null? tag.replace(TEMPLATE_PLACEHOLDER , key) : null ,value != null? tag.replace(TEMPLATE_PLACEHOLDER , value) : null);
			});
			return gdprTemplateObject;
		}
	}
	
	public static Map<String, Map<Integer, String>> mcpttIdAndProfileMdnMap(Map<String, Map<Integer, String>> mdnAndProfileMdnMap)
	{
		return populateMcpttIdAndProfileMdnMap(mdnAndProfileMdnMap, TAG_MCPTTID);
	}
	
	private static Map<String, Map<Integer, String>> populateMcpttIdAndProfileMdnMap(
			Map<String, Map<Integer, String>> data, String tag) {
		if (CollectionUtils.isEmpty(data)) {
			return data;
		} else {
			Map<String, Map<Integer, String>> gdprTemplateObject = new HashMap<>();
			data.forEach((str, map) -> {
				gdprTemplateObject.put(str != null ? tag.replace(TEMPLATE_PLACEHOLDER, str) : null, populateProfileMdnMap(map, TAG_MDN));
			});
			return gdprTemplateObject;
		}
	}
	private static Map<Integer, String> populateProfileMdnMap(Map<Integer, String> data, String tag) {
		if (CollectionUtils.isEmpty(data)) {
			return data;
		} else {
			Map<Integer, String> gdprTemplateObject = new HashMap<>();
			data.forEach((key, value) -> {
				gdprTemplateObject.put(key, value != null ? tag.replace(TEMPLATE_PLACEHOLDER, value) : null);
			});
			return gdprTemplateObject;
		}
	}
	
	public static Map<String , Collection<String>> mapKeyValueListMdn(Map<String, Collection<String>> mdnDetails ){return tagMapKeyValueListMdn(mdnDetails , TAG_MDN); }


	private static Map<String , Collection<String>> tagMapKeyValueListMdn(Map<String , Collection<String>> data, String tag) {
		if (CollectionUtils.isEmpty(data)) {
			return data;
		} else {
			Map<String , Collection<String>> gdprTemplateObject = new HashMap<>();
			data.forEach((str , obj)-> {
				gdprTemplateObject.put(str != null ? tag.replace(TEMPLATE_PLACEHOLDER , str) : null , populateList(obj,TEMPLATE_PLACEHOLDER));
			});
			return gdprTemplateObject;
		}
	}
	
	public static String mdnUriTemplate(String uri) {

		if (uri == null || uri.isEmpty()) {
			return uri;
		} else {
			StringTokenizer st = new StringTokenizer(uri, "/");
			StringBuilder appends = new StringBuilder();
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (token.contains("tel")) {
					token = TAG_MDN.replace(TEMPLATE_PLACEHOLDER, token);
				}
				if (appends.length() != 0) {
					appends.append("/");
				}
				appends.append(token);

			}
			return appends.toString();
		}
	}

	public static String mdnListServiceUriTemplate(String uri) {

		if (uri == null || uri.isEmpty()) {
			return uri;
		} else {
			StringTokenizer st = new StringTokenizer(uri, ";");
			StringBuilder appends = new StringBuilder();
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (token.contains("tel")) {
					token = TAG_MDN.replace(TEMPLATE_PLACEHOLDER, token);
				}
				if (appends.length() != 0) {
					appends.append(";");
				}
				appends.append(token);

			}
			return appends.toString();
		}
	}
	public static String  mdnURLTemplate(String URL)
	{
		if (URL == null || URL.isEmpty()) {
			return URL;
		} else {
			Pattern pattern = Pattern.compile("\\d{10,15}");
			Matcher matcher =null;
			int found =0;
			StringTokenizer st = new StringTokenizer(URL, "/");
			StringBuilder appends = new StringBuilder();
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				matcher =  pattern.matcher(token);
				if(matcher.find()) {
					token = TAG_MDN.replace(TEMPLATE_PLACEHOLDER, token);
				}if(found == 1){
					appends.append("/");
					found=0;
				}if (appends.length() != 0) {
					appends.append("/");
				}if(token.equals("http:")){
					found=1;
				}
				appends.append(token);
			}
			return appends.toString();
		}
	}

}
