package com.kodiak.xdms.mediator.helper;

import com.kodiak.frameworks.confignotifier.watcher.KnConfigXlaConstants;
import com.kodiak.frameworks.confignotifier.watcher.KnSystemConfiguration;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.pocxlaclient.KnPoCXLATableDetailsDTO;
import com.kodiak.utilities.pocxlaclient.event.KnTableInsert;
import com.kodiak.utilities.springcontainer.KnSpringContextProvider;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class KnXlaInsertEvent implements ApplicationListener<KnTableInsert> {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXlaInsertEvent.class);
    private static KnXlaInsertEvent xlaInsertEvent;
    private static KnSystemConfiguration systemConfig;

    private KnXlaInsertEvent() {
        knLogger.debug("KnXlaInsertEvent: In the constructor");
        systemConfig = KnSystemConfiguration.getInstance("XDM", "Configuration");
    }

    public static synchronized KnXlaInsertEvent getInstance() {
        KnXlaInsertEvent xlaInsertEvent = (KnXlaInsertEvent) KnSpringContextProvider.getApplicationContext().getBean("knXlaInsertEvent");
        knLogger.debug("getInstance()", "returning KnXlaInsertEvent responder - ", xlaInsertEvent);
        return xlaInsertEvent;
    }

    public void onApplicationEvent(KnTableInsert event) {
        String methodName = "onApplicationEvent()";
        knLogger.info("KnXlaInsertEvent: onApplicationEvent - " + event);
        KnPoCXLATableDetailsDTO tableData = null;
        KnTableInsert ev = (KnTableInsert) event;
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