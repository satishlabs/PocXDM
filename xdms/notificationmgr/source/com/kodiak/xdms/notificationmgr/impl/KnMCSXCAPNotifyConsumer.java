/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.notificationmgr.impl;

import static com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18;
import static com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_19_X;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import com.kodiak.common.commdto.common.KnNotificationParamDTO;
import com.kodiak.common.commdto.common.KnXDMSubsProvDTO;
import com.kodiak.common.commdto.response.KnXDMSubsProfileRespDTO;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.notificationmgr.beans.KnMcsxcapMdnDTO;
import com.kodiak.xdms.mcsnotifymgr.KnMCSDocChangeNotifier;
import com.kodiak.xdms.mcsnotifymgr.beans.KnDocChangeListDto;
import com.kodiak.xdms.mcsnotifymgr.beans.KnDocumentChangeDTO;
import com.kodiak.xdms.mcsnotifymgr.beans.KnMCSNotifyDTO;
import com.kodiak.xdms.mcsnotifymgr.resources.KnMCSNotifyConstants;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;

public class KnMCSXCAPNotifyConsumer implements Runnable {

	private static final KnLogger knLogger = KnLogger.getLogger(KnMCSXCAPNotifyConsumer.class);

	private BlockingQueue<KnMcsxcapMdnDTO> blockingQueue = null;
	private BlockingQueue<String> suppressMdnBlockingQueue = null;
	private KnMCSDocChangeNotifier mcsDocChangeNotifier;
	private KnGenInfoUtil genInfoUtil;
	public static final String MCDATAUSERPROFILE_AUID = "org.3gpp.mcdata.user-profile";
	public static final String MCPTT_UE_PROFILE_AUID = "org.3gpp.mcptt.user-profile";
	public static final String MCVIDEOUSERPROFILE_AUID = "org.3gpp.mcvideo.user-profile";
	private static Map<String,String> auidMap=new HashMap<>();

	public KnMCSXCAPNotifyConsumer() {
		this.blockingQueue = KnMCSXCAPNotifier.getBlockingQueue();
		this.suppressMdnBlockingQueue = KnMCSXCAPNotifier.getSuppressMdnBlockingQueue();
		this.mcsDocChangeNotifier = KnMCSDocChangeNotifier.getInstance();
		this.genInfoUtil = KnGenInfoUtil.getInstance();
		 auidMap.put("mcvideo",MCVIDEOUSERPROFILE_AUID);
	     auidMap.put("mcdata",MCDATAUSERPROFILE_AUID);
	     auidMap.put("mcptt",MCPTT_UE_PROFILE_AUID);
	}

	@Override
	public void run() {
		knLogger.debug("Inside run Method of KnMCSXCAPNotifyConsumer");
		List<KnMcsxcapMdnDTO> mcsxcapMdnDTOs = new ArrayList<KnMcsxcapMdnDTO>();
		blockingQueue.drainTo(mcsxcapMdnDTOs, 1000);
		LinkedHashSet<String> suppressMdnList = new LinkedHashSet<String>();
		suppressMdnBlockingQueue.drainTo(suppressMdnList, 1000);
		KnXDMSubsProfileRespDTO subsProfileRespDTO = null;
		KnPersisterTxn persisterTxn = null;
		if (!mcsxcapMdnDTOs.isEmpty()) {
			try {

				subsProfileRespDTO = genInfoUtil.selectSubsProfileInfo(mcsxcapMdnDTOs.stream().map(KnMcsxcapMdnDTO::getMdn).collect(Collectors.toList()), null);
				persisterTxn = KnPersisterTxn.getPersisterTxn();
				persisterTxn.open();
				knLogger.debug("selectSubsProfileInfo- subsProfileRespDTO",subsProfileRespDTO);
				Collection<KnXDMSubsProvDTO> subsRespDTO = subsProfileRespDTO.getSubsRespDTO();
				sendMCSNotification(subsRespDTO, mcsxcapMdnDTOs, persisterTxn);
				mcsDocChangeNotifier.sendMdnsToDB(suppressMdnList, null, persisterTxn);
				persisterTxn.save();
			} catch (Throwable e) {
				knLogger.error("KnMCSXCAPNotifyConsumer:run"," Throwable :",e.getMessage());
			}
		}
	}

	private void sendMCSNotification(Collection<KnXDMSubsProvDTO> subsRespDTO, List<KnMcsxcapMdnDTO> mcsxcapMdnDTOs, KnPersisterTxn persisterTxn) throws KnBOException {
		List<KnMCSNotifyDTO> mcsNotifyDTOs0 = new ArrayList<>();
		List<KnMCSNotifyDTO> mcsNotifyDTOs5 = new ArrayList<>();
		if (subsRespDTO != null && !subsRespDTO.isEmpty()) {
			for (KnXDMSubsProvDTO subsProfileDTO : subsRespDTO) {
				if (subsProfileDTO.getClientPvMajorVersion() >= PROTOCOL_VERSION_18
						|| (subsProfileDTO.getMcsCompliance() == 1
						&& subsProfileDTO.getClientPvMajorVersion() >= PROTOCOL_VERSION_19_X)) {
					Iterator<KnMcsxcapMdnDTO> iterator = mcsxcapMdnDTOs.iterator();
					while (iterator.hasNext()) {
						KnMcsxcapMdnDTO mcsxcapMdnDTO = iterator.next();
						if (mcsxcapMdnDTO.getMdn().trim().equals(subsProfileDTO.getMdn().trim())) {
							KnMCSNotifyDTO mcsNotifyDTO = prepareMCSNotification(subsProfileDTO, persisterTxn);
							if(mcsxcapMdnDTO.isEmergencyFlag() == true)
							{
								mcsNotifyDTOs0.add(mcsNotifyDTO);
							}else{
								mcsNotifyDTOs5.add(mcsNotifyDTO);
							}
							iterator.remove();
						}
					}
				}
			}
			//High/0 priority notification
			KnNotificationParamDTO notificationParamDTO0 = new KnNotificationParamDTO();
			notificationParamDTO0.setPriority(0);
			//Default/5 priority notification
			KnNotificationParamDTO notificationParamDTO5 = new KnNotificationParamDTO();
			mcsDocChangeNotifier.generateMCSNotification(mcsNotifyDTOs0, notificationParamDTO0, persisterTxn);
			mcsDocChangeNotifier.generateMCSNotification(mcsNotifyDTOs5, notificationParamDTO5, persisterTxn);
		}
	}

	public KnMCSNotifyDTO prepareMCSNotification(KnXDMSubsProvDTO subsProfileDTO, KnPersisterTxn persisterTxn)
			throws KnBOException {

		String methodName = "prepareMCSNotification(KnOPDirChgDTO,String)";
		knLogger.info(methodName, "inside prepareMCSNotification");
		knLogger.debug(methodName, "subsProfileDTO", subsProfileDTO);

		KnMCSNotifyDTO mcsNotifyDTO = new KnMCSNotifyDTO();
		if (subsProfileDTO != null) {
			List<KnDocumentChangeDTO> mcsDocumentChangeDTOS = new ArrayList<>();
			String mcsXcapRootUri = genInfoUtil.getMCSXCAPRootURI(subsProfileDTO.getMdn(), persisterTxn);
			knLogger.info(methodName, "mcsXcapRootUri-->" + KnGDPRTemplate.mdnUriTemplate(mcsXcapRootUri));
			mcsNotifyDTO.setXcapRootUri(mcsXcapRootUri);
			mcsNotifyDTO.setNotifyType(KnMCSNotifyConstants.NOTIFYTYPE.DOCUMENT_CHANGE.value());
			KnDocumentChangeDTO mcsDocumentChangeDTO = new KnDocumentChangeDTO();
			mcsDocumentChangeDTO.setMdn(subsProfileDTO.getMdn().trim());
			mcsDocumentChangeDTO.setDocType(KnMCSNotifyConstants.DOCTYPE.MDN.value());
			mcsDocumentChangeDTO.setXcapRootUri(mcsXcapRootUri);
			mcsDocumentChangeDTO.setDocChangeList(buildMCSDOC(subsProfileDTO.getMcId(),
					subsProfileDTO.getUserProfileIndex(), String.valueOf(System.currentTimeMillis()),
					String.valueOf(subsProfileDTO.getLastUpdateProfileTime()), subsProfileDTO.getMcsCompliance()));
			mcsDocumentChangeDTOS.add(mcsDocumentChangeDTO);
			mcsNotifyDTO.setDocumentChange(mcsDocumentChangeDTOS);
		}
		knLogger.info(methodName, "Before sending the MCSNotification:  mcsNotifyDTO - ", mcsNotifyDTO);
		return mcsNotifyDTO;
	}

	public static List<KnDocChangeListDto> buildMCSDOC(String mcId
            ,Integer upmIndex,String newEtag,String previousEtag,int mcsCompliance) {
        List<KnDocChangeListDto> docList = new ArrayList<>();
        if (mcsCompliance == 0) {
            //for kodiak clients
            for (Map.Entry auid : auidMap.entrySet()) {
                StringBuilder mcsDocUri = new StringBuilder();
                mcsDocUri.append(auid.getValue())
                        .append("/users/")
                        .append(mcId)
                        .append("/")
                        .append(auid.getKey())
                        .append("-user-profile-")
                        .append(upmIndex)
                        .append(".xml");
                docList.add(new KnDocChangeListDto(mcsDocUri.toString(), newEtag, previousEtag,null));
            }
        } else {
            for (Map.Entry auid : auidMap.entrySet()) {
                StringBuilder mcsDocUri = new StringBuilder();
                mcsDocUri.append(auid.getValue())
                        .append("/users/")
                        .append(mcId)
                        .append("/user-profile")
                        .append(".xml");
                docList.add(new KnDocChangeListDto(mcsDocUri.toString(), newEtag, previousEtag,null));
            }
        }
        return docList;
    }

}
