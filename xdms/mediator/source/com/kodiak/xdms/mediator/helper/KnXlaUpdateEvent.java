package com.kodiak.xdms.mediator.helper;

import com.kodiak.frameworks.confignotifier.watcher.KnConfigXlaConstants;
import com.kodiak.frameworks.confignotifier.watcher.KnSystemConfiguration;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.pocxlaclient.KnPoCXLATableDetailsDTO;
import com.kodiak.utilities.pocxlaclient.event.KnTableUpdate;
import com.kodiak.utilities.springcontainer.KnSpringContextProvider;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observer;

@Component
public class KnXlaUpdateEvent implements ApplicationListener<KnTableUpdate> {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXlaUpdateEvent.class);
    private static KnSystemConfiguration systemConfig;
    private static KnXlaUpdateEvent testXLaUpdate;

    private KnXlaUpdateEvent() {
        knLogger.debug("KnXlaUpdateEvent: In the constructor");
        systemConfig = KnSystemConfiguration.getInstance("XDM", "Configuration");
    }

    public static synchronized KnXlaUpdateEvent getInstance() {
        KnXlaUpdateEvent testXLaUpdate = (KnXlaUpdateEvent) KnSpringContextProvider.getApplicationContext().getBean("knXlaUpdateEvent");
        knLogger.debug("getInstance()", "returning xlaUpdateEvent responder - ", testXLaUpdate);
        return testXLaUpdate;
    }

    public void addObservers(List<Observer> observers) {
        final String methodName = "addObservers(List<Observer>)";
        knLogger.debug(methodName, "registering observers - ", observers);
        systemConfig.addObservers(observers);
    }

    public void onApplicationEvent(KnTableUpdate event) {
        String methodName = "onApplicationEvent()";
        knLogger.info("TestXLaUpdate: onApplicationEvent - " + event);
        KnPoCXLATableDetailsDTO tableData = null;
        KnTableUpdate ev = (KnTableUpdate) event;
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