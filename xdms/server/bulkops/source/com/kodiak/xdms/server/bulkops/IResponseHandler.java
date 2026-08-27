package com.kodiak.xdms.server.bulkops;

import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;

public interface IResponseHandler {

    public boolean processResponse(IXDMResponseDTO respDto, KnMessage reqMsg);

    public boolean deleteAppIntfMsg(KnMessage reqMsg);

    public boolean processJsonResponse(IXDMResponseDTO respDto,  KnMessage reqMsg);

    public boolean processCorpBanFanJsonResponse(IXDMResponseDTO respDto,  KnMessage reqMsg);

    boolean processResponse(IXDMResponseDTO respDTO, KnMessage msgObj, boolean synch_response);

}


