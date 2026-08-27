package com.kodiak.xdms.mediator.helper;

import com.kodiak.frameworks.confignotifier.watcher.KnConfigXlaConstants;
import com.kodiak.frameworks.confignotifier.watcher.KnSystemConfiguration;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.pocxlaclient.KnPoCXLATableDetailsDTO;
import com.kodiak.utilities.pocxlaclient.event.KnTableDelete;
import com.kodiak.utilities.springcontainer.KnSpringContextProvider;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class KnXlaDeleteEvent implements ApplicationListener<KnTableDelete> {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXlaDeleteEvent.class);

    private static KnXlaDeleteEvent xlaDeleteEvent;
    private static KnSystemConfiguration systemConfig;

    private KnXlaDeleteEvent() {
        knLogger.debug("KnXlaDeleteEvent: In the constructor");
        systemConfig = KnSystemConfiguration.getInstance("XDM", "Configuration");
    }

    public static synchronized KnXlaDeleteEvent getInstance() {
        KnXlaDeleteEvent xlaDeleteEvent = (KnXlaDeleteEvent) KnSpringContextProvider.getApplicationContext().getBean("knXlaDeleteEvent");
        knLogger.debug("getInstance()", "returning KnXlaDeleteEvent responder - ", xlaDeleteEvent);
        return xlaDeleteEvent;
    }

    public void onApplicationEvent(KnTableDelete event) {
        String methodName = "onApplicationEvent()";
        knLogger.info("KnXlaDeleteEvent: onApplicationEvent - " + event);
        KnPoCXLATableDetailsDTO tableData = null;
        KnTableDelete ev = (KnTableDelete) event;
        tableData = ev.getKnPoCXLATableDetailsDTO();
        if (tableData != null) {
            knLogger.debug(methodName, "ENTRY: tableData, event received for table ", tableData.getTableName());
            List<String> tableList = new ArrayList<String>();
            tableList.add(KnConfigXlaConstants.DG + tableData.getTableName());
            systemConfig.setChanged();
            systemConfig.notifyObservers(tableList);
        }
        List<String> tabelList = Arrays.asList("LOG4JINFO", "AUDITLOGINFO", "LOGINFO_" + System.getenv("PTTSERVERID"));
        if (tableData != null) {
            knLogger.debug(methodName, "ENTRY: tableData, event received for table ", tableData.getTableName());
            for (String tableName : tabelList) {
                if (tableData.getTableName().equals(tableName)) {
                    //  Re-initialize logger
                    KnLogger.initialize();
                }
            }
        }
    }
}