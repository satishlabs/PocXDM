package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpAssignUserProfileController;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpAssignUserProfileManager;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPAuthUserPermissionInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

public class KnCorpAssignUserProfileManager implements ICorpAssignUserProfileManager {
    private ICorpAssignUserProfileController corpAssignUserProfileController;

    public KnCorpAssignUserProfileManager() {
        corpAssignUserProfileController = KnCorpBORegistry.createAssignUserprofileController();
    }

    public KnCorpResponseDTO modifyBulkCorpSubscContacts(KnIPCorpSubscContactListDTO
                                                                    contactListDTO, KnPersisterTxn persisterTxn) {
        contactListDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        contactListDTO.setOperationType(KnOperationTypes.ASSIGN_UPM_MODIFY_SUBS_CONTACT_LIST);
        contactListDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpAssignUserProfileController.modifyCorpSubscContacts(contactListDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO setBulkTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
        ipAuthUserPermissionInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipAuthUserPermissionInfoDTO.setOperationType(KnOperationTypes.SET_TARGET_PERMISSION_ASSIGNUPM);
        ipAuthUserPermissionInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpAssignUserProfileController.setTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
    }
}
