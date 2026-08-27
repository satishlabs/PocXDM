package com.kodiak.xdms.server.corpmgmt.dao.persister.couchbase;

import com.kodiak.common.commdto.common.KnPTTSettingDocInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.cb.util.KnCBSRepository;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.common.cb.util.KnCouchDbManager;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpPTTSettingDTO;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutateInResult;
import com.couchbase.client.java.kv.MutateInSpec;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.client.core.error.DocumentNotFoundException;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpPTTSettingDocRespDTO;

import java.util.*;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.kodiak.xdms.server.common.resources.KnConstants.PTT_TEMPLATE_DOC_TYPE;
import static com.kodiak.xdms.server.common.resources.KnConstants.PTT_TEMPLATE_DOC_VERSION;
import static com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes.XDM_CORP_PTTSETTING;

public class KnPTTSettingDAO {

    public static final KnLogger knLogger = KnLogger.getLogger(KnPTTSettingDAO.class);

    private KnCouchDbManager knCouchDbManager;
    private String pttServerId;
    private KnCorpCommonInfoUtil commonInfoUtil;

    private static final String CB_BUCKET_NAME = "pocdata";
    private static final String META_ID = "META().id";
    private static final String PTT_SETTTING_NAME = "templateName";
    private static final String PTT_DOC_TYPE = "type";
    private static final String PTTSETTING_TEMPLATE_NAME_INDEX = "idx_pocdata_templateName";

    private static final String GET_PTTSETTING_LIST_BY_ID = "select " + META_ID + KnConstants.COMMA + PTT_SETTTING_NAME  +  " from `" + CB_BUCKET_NAME + "` where " + META_ID + " IN [ $pttIdList ] order by templateName";
    private static final String GET_ALL_PTTSETTING_LIST = "select " + META_ID + KnConstants.COMMA + PTT_SETTTING_NAME  +  " from `" + CB_BUCKET_NAME + "` where " + PTT_DOC_TYPE + "=$docType order by templateName";
    private static final String GET_PTTSETTING_DETAILS = "select * from `" + CB_BUCKET_NAME + "` where " +META_ID+"=$templateId";
    private static final String IS_PTTSETTING_EXSISTS = "select " + META_ID + KnConstants.COMMA + PTT_SETTTING_NAME + " from `" + CB_BUCKET_NAME + "` where " + PTT_SETTTING_NAME + "=$templateName";
    private static final String DELETE_PTTSETTING_DOC = "DELETE FROM `" + CB_BUCKET_NAME + "` where " +META_ID+"=$templateId ";
    private static final String CREATE_PTTSETTING_TEMPLATE_NAME_INDEX =
            "CREATE INDEX `" + PTTSETTING_TEMPLATE_NAME_INDEX + "` IF NOT EXISTS ON `" + CB_BUCKET_NAME + "`(`" + PTT_SETTTING_NAME + "`)";

    /** Ensures the CREATE INDEX statement is executed at most once per JVM. */
    private static final AtomicBoolean PTTSETTING_TEMPLATE_NAME_INDEX_CREATED = new AtomicBoolean(false);

    public KnPTTSettingDAO(String pttServerId) {
        this.knCouchDbManager = KnCouchDbManager.getInstance();
        this.pttServerId = pttServerId;
        commonInfoUtil = new KnCorpCommonInfoUtil();
        createPTTSettingTemplateNameIndex();
    }

    /**
     * Creates a secondary index on the templateName,if it does not already exist.
     * This index speeds up lookups/queries that filter on the PTT setting template
     * CREATE INDEX statement is sent to CBS at most once per JVM
     * (Couchbase still treats it as idempotent via {@code IF NOT EXISTS}).
     */
    private void createPTTSettingTemplateNameIndex() {
        final String methodName = "createPTTSettingTemplateNameIndex()";
        if (PTTSETTING_TEMPLATE_NAME_INDEX_CREATED.get()) {
            return;
        }
        try {
            Cluster pocCluster = KnCBSRepository.getInstance().getPOCCluster();
            if (pocCluster == null) {
                knLogger.warn(methodName, "POC cluster is null, skipping index creation for ", PTTSETTING_TEMPLATE_NAME_INDEX);
                return;
            }
            knLogger.info(methodName, "Creating index if not exists - ", CREATE_PTTSETTING_TEMPLATE_NAME_INDEX);
            QueryResult result = pocCluster.query(CREATE_PTTSETTING_TEMPLATE_NAME_INDEX);
            knLogger.info(methodName, "Index creation response - ", result);
            PTTSETTING_TEMPLATE_NAME_INDEX_CREATED.set(true);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception while creating index ", PTTSETTING_TEMPLATE_NAME_INDEX, " : ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
        }
    }


    public void createPTTSettingDoc(String pttSettingId, KnCorpPTTSettingDTO pttSettingDoc) throws KnDAOException {
        final String methodName = "createPTTSettingDoc()";
        knLogger.info(methodName, "pttSettingId - ", pttSettingId, " pttSettingDoc ", pttSettingDoc);
        try {
            pttSettingDoc.setCreateTimeStamp(System.currentTimeMillis());
            pttSettingDoc.setUpdateTimeStamp(System.currentTimeMillis());
            pttSettingDoc.setVer(PTT_TEMPLATE_DOC_VERSION);
            pttSettingDoc.setType(PTT_TEMPLATE_DOC_TYPE);
            MutationResult result =  KnCBSRepository.getInstance().saveDocument(pttSettingId,pttSettingDoc);
            knLogger.debug(methodName, "Done creating pttSettingDoc with Id:", pttSettingId);
            knLogger.info(methodName,"Response from CBSDB"+result.toString());
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException =", e);
            throw KnDbUtil.processException(e, "Failed to create User PTTSetting Doc", pttServerId, XDM_CORP_PTTSETTING, "CREATE_PTT_SETTING");
        } catch (Exception e) {
            knLogger.error(methodName, "InterruptedException =", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to Create User PTTSetting Doc", pttServerId, XDM_CORP_PTTSETTING, "CREATE_PTT_SETTING");
        }
    }

        public List<KnPTTSettingDocInfoDTO> getPTTSettingById(List<KnCorpPTTSettingDocRespDTO> docRespDTOList) throws KnDAOException {
            final String methodName = "getPTTSettingById(String)";
            knLogger.info(methodName, "Doc list -", docRespDTOList);
            List<KnPTTSettingDocInfoDTO> pttSettingList = new ArrayList<>();
            try {
                for(KnCorpPTTSettingDocRespDTO settingDocRespDTO: docRespDTOList) {
                    KnCorpPTTSettingDTO pttSettingDoc = getPTTSettingDoc(settingDocRespDTO.getPttSettingId());
                    KnPTTSettingDocInfoDTO docInfoDTO = new KnPTTSettingDocInfoDTO();
                    docInfoDTO.setDocId(pttSettingDoc.get_id());
                    docInfoDTO.setDocName(pttSettingDoc.getTemplateName());
                    docInfoDTO.setIsDefault(settingDocRespDTO.getIsDefault());
                    pttSettingList.add(docInfoDTO);
                }

                knLogger.debug(methodName, "queryResult-", pttSettingList);
            } catch (NoSuchElementException ex) {
                knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
                throw KnDbUtil.processException(ex, "Failed to get PTTSetting List", pttServerId, XDM_CORP_PTTSETTING, GET_PTTSETTING_LIST_BY_ID);
            }catch (RejectedExecutionException e) {
                knLogger.error(methodName, "RejectedExecutionException ", e);
                throw KnDbUtil.processException(e, "Failed to get PTTSetting List", pttServerId, XDM_CORP_PTTSETTING, GET_PTTSETTING_LIST_BY_ID);
            } catch (Exception e) {
                knLogger.error(methodName, "Exception ", e);
                KnCBSRepository.generateCBSTimeoutAlarm(e);
                throw KnDbUtil.processException(e, "Failed to get User PTTSetting List", pttServerId, XDM_CORP_PTTSETTING, GET_PTTSETTING_LIST_BY_ID);
            }
            return pttSettingList;


    }

    public KnCorpPTTSettingDTO getPTTSettingDoc(String docId) throws KnDAOException {
        final String methodName = "getPTTSettingDoc(String)";

        knLogger.info(methodName, "pttSettingId -", docId);
        KnCorpPTTSettingDTO pttSettingDTO = null;
        try {

            JsonObject values = JsonObject.create().put("templateId", docId);
            knLogger.debug(methodName, "values-", values);
            QueryResult result=KnCBSRepository.getInstance().getQueryResult(GET_PTTSETTING_DETAILS,values);
            if (!result.rowsAsObject().isEmpty()) {
                pttSettingDTO = commonInfoUtil.jsonToObject(result.rowsAsObject().get(0).get(CB_BUCKET_NAME).toString(), KnCorpPTTSettingDTO.class);
                knLogger.debug(methodName, "queryResult-", result.rowsAsObject().get(0).toString());
            } else {
                knLogger.debug(methodName, "No PTT setting doc found for id:", docId);
            }

            knLogger.debug(methodName, "queryResult-", pttSettingDTO);
        } catch (DocumentNotFoundException ex) {
            knLogger.error(methodName, "DocumentNotFoundException :", ex);
            throw ex;
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get PTTSetting doc", pttServerId, XDM_CORP_PTTSETTING,GET_PTTSETTING_DETAILS);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User PTTSetting doc", pttServerId, XDM_CORP_PTTSETTING, GET_PTTSETTING_DETAILS);
        }
        return pttSettingDTO;
    }
    public String getPTTSettingDocName(String docName) throws KnDAOException {
        final String methodName = "getPTTSettingDoc(String)";

        knLogger.info(methodName, "pttSettingName -", docName);
        String result=null;
        try {

            JsonObject values = JsonObject.create().put("templateName", docName);
            knLogger.debug(methodName, "values-", values);
            QueryResult queryResult=KnCBSRepository.getInstance().getQueryResult(IS_PTTSETTING_EXSISTS,values);
            Map<String, Object> queryResultMap=new HashMap<String, Object>();
            for (JsonObject row : queryResult.rowsAsObject()) {
                queryResultMap = row.toMap();
            }
            result=(String) queryResultMap.get(PTT_SETTTING_NAME);

            knLogger.debug(methodName, "queryResult-", result);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get PTTSetting name", pttServerId, XDM_CORP_PTTSETTING, IS_PTTSETTING_EXSISTS);
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get PTTSetting name", pttServerId, XDM_CORP_PTTSETTING, IS_PTTSETTING_EXSISTS);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User PTTSetting name", pttServerId, XDM_CORP_PTTSETTING, IS_PTTSETTING_EXSISTS);
        }
        return result;
    }

    public void deletePTTSettingDoc(String docId) throws KnDAOException {
        final String methodName = "deletePTTSettingDoc(String)";

        knLogger.info(methodName, "pttSettingId -", docId);
        KnCorpPTTSettingDTO pttSettingDTO;
        try {
            JsonObject values = JsonObject.create().put("$templateId", docId);
            QueryResult queryResult=KnCBSRepository.getInstance().getQueryResult(DELETE_PTTSETTING_DOC,values);
            knLogger.debug(methodName, "queryResult-", queryResult);
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to Delete User Profile", pttServerId, XDM_CORP_PTTSETTING, DELETE_PTTSETTING_DOC);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to Delete User Profile", pttServerId, XDM_CORP_PTTSETTING, DELETE_PTTSETTING_DOC);
        }

    }

    public List<KnPTTSettingDocInfoDTO> getAllPTTSettingDocs(String docType) throws KnDAOException {

        final String methodName = "getAllPTTSettingDocs(String)";

        knLogger.info(methodName, "docType -", docType);
        KnCorpPTTSettingDTO pttSettingDTO;
        List<KnPTTSettingDocInfoDTO> pttSettingList = new ArrayList<>();
        try {

            JsonObject values = JsonObject.create().put("docType", docType);
            knLogger.debug(methodName, "values-", values);
            QueryResult queryResult=KnCBSRepository.getInstance().getQueryResult(GET_ALL_PTTSETTING_LIST,values);

            for(JsonObject object : queryResult.rowsAsObject()){
                pttSettingDTO = commonInfoUtil.jsonToObject(object.toString(), KnCorpPTTSettingDTO.class);
                KnPTTSettingDocInfoDTO docInfoDTO = new KnPTTSettingDocInfoDTO();
                docInfoDTO.setDocId(pttSettingDTO.get_id());
                docInfoDTO.setDocName(pttSettingDTO.getTemplateName());
                pttSettingList.add(docInfoDTO);
            }
            knLogger.debug(methodName, "queryResult-", pttSettingList);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get all PTTSetting List", pttServerId, XDM_CORP_PTTSETTING, GET_ALL_PTTSETTING_LIST);
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get all PTTSetting List", pttServerId, XDM_CORP_PTTSETTING, GET_ALL_PTTSETTING_LIST);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get all PTTSetting List", pttServerId, XDM_CORP_PTTSETTING, GET_ALL_PTTSETTING_LIST);
        }
        return pttSettingList;


    }

    public void modifyPTTSettingTemplate(String pttSettingId, KnCorpPTTSettingDTO pttSettingDoc) throws KnDAOException {
        final String methodName = "modifyPTTSettingTemplate()";
        knLogger.info(methodName, "pttSettingId - ", pttSettingId, " pttSettingDoc ", pttSettingDoc);
        try {
            long updateTimestamp = System.currentTimeMillis();

            List<MutateInSpec> specs = Arrays.asList(
                MutateInSpec.upsert("updateTimeStamp", updateTimestamp),
                MutateInSpec.upsert("pttSettingDocDetail", pttSettingDoc.getPttSettingDocDetail())
            );

            MutateInResult result = KnCBSRepository.getInstance().updateDocument(pttSettingId, specs);
            knLogger.debug(methodName, "Modified pttSettingDoc with Id:", pttSettingId);
            knLogger.info(methodName, "Response from CBSDB " + result.toString());
        } catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException =", e);
            throw KnDbUtil.processException(e, "Failed to modify PTTSetting Doc", pttServerId, XDM_CORP_PTTSETTING, "MODIFY_PTT_SETTING");
        } catch (Exception e) {
            knLogger.error(methodName, "Exception =", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to modify PTTSetting Doc", pttServerId, XDM_CORP_PTTSETTING, "MODIFY_PTT_SETTING");
        }
    }

}
