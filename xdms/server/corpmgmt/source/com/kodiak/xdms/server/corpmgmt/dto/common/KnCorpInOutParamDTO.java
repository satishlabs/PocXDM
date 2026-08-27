/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import java.util.HashMap;
import java.util.Map;

import com.kodiak.xdms.server.common.dto.clientdat.KnTGSModeChgDTO;

public class KnCorpInOutParamDTO {
	private Map<String, KnTGSModeChgDTO> tgsModeChgMap = new HashMap<String, KnTGSModeChgDTO>();
    private Map<String,Integer> mdnTgscEtag = new HashMap<String, Integer>();

	private Map<String,Integer> mdnTgssEtag = new HashMap<String, Integer>();
	public Map<String, KnTGSModeChgDTO> getTgsModeChgMap() {
		return tgsModeChgMap;
	}

	public void putTGSModeChg(Map<String, KnTGSModeChgDTO> tgsModeChgMap) {
		if (tgsModeChgMap != null && !tgsModeChgMap.isEmpty()) {
			this.tgsModeChgMap.putAll(tgsModeChgMap);
		}
	}

    public Map<String, Integer> getMdnTgscEtag() {
        return mdnTgscEtag;
    }

    public void setMdnTgscEtag(Map<String, Integer> mdnTgscEtag) {
        this.mdnTgscEtag = mdnTgscEtag;
    }

	public Map<String, Integer> getMdnTgssEtag() { return mdnTgssEtag; 	}

	public void setMdnTgssEtag(Map<String, Integer> mdnTgssEtag) { this.mdnTgssEtag = mdnTgssEtag;	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnCorpInOutParamDTO [tgsModeChgMap=").append(tgsModeChgMap).append("]")
                .append("[mdnTgscEtag=").append(mdnTgscEtag).append("],")
			    .append("[mdnTgssEtag=").append(mdnTgssEtag).append("]}");
		return builder.toString();
	}

}
