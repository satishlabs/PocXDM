/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.tool.memorymonitor;

import com.sun.management.GarbageCollectorMXBean;
import com.sun.tools.attach.*;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.logging.*;

import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

public class KnMemoryMonitor {

    MBeanServerConnection mbServerConnection;
    JMXConnector jmxConnector;
    Timer timer;
    VirtualMachine vm;
    KnAliveTask timerTask = null;

    NotificationEmitter emitter;
    static int counter;
    static final String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";
    private static String XDMProcess = "com.kodiak.xdms.loader.KnXDMSLoader";
    private static String tomcatProcess = "org.apache.catalina.startup.Bootstrap start";
    private static String instance1Path = "/DG/activeRelease/WebApps/tomcat_instance1/tomcat.pid";

    public static final Logger logger = Logger.getLogger(KnMemoryMonitor.class
            .getName());

    static Handler handler = null;

    public enum OldGenCollectors {
        SERIAL("java.lang:type=GarbageCollector,name=MarkSweepCompact"), PARALLELOLD(
                "java.lang:type=GarbageCollector,name=PS MarkSweep"), CMS(
                "java.lang:type=GarbageCollector,name=ConcurrentMarkSweep"), G1(
                "java.lang:type=GarbageCollector,name=G1 Old Generation");

        String value;

        private OldGenCollectors(String value) {
            this.value = value;
        }
    }

    public static synchronized void initLogger(Handler handler) {
        try {
            logger.setUseParentHandlers(true);
            for (Handler handlers : Logger.getLogger("").getHandlers()) {
                Logger.getLogger("").removeHandler(handlers);
            }
            handler = new FileHandler("./memory_monitor.log");
            SimpleFormatter formatter = new SimpleFormatter();
            handler.setFormatter(formatter);
            Logger.getLogger("").addHandler(handler);
            Logger.getLogger("").setLevel(Level.INFO);
            logger.setLevel(Level.INFO);
            logger.log(Level.INFO, "Init logger");
        } catch (IOException e) {
            System.out.println("Unable to initialize logger.");
        }
    }

    public static void main(String[] args) {
        initLogger(handler);
        boolean loopIt = false;
        while (!loopIt) {
            loopIt = initialize();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public static boolean initialize() {
        KnMemoryMonitor instance = new KnMemoryMonitor();
        boolean isIntialized = false;
        try {
            if (instance.AttachVirtualMachine()) {
                ObjectName collector = instance.findingCollector();
                instance.startMonitor(collector);
                isIntialized = true;
                logger.log(Level.INFO, "Memory Monitor tool started....");
            } else {
                logger.log(Level.SEVERE, "There is no processes to monitor");
            }

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "main method...", ex);
            System.out.println("Unable to run the Mbean Monitor ....." + ex.getMessage());
            System.exit(-1);
        }
        return isIntialized;
    }

    private void startMonitor(ObjectName collector)
            throws MalformedObjectNameException, NullPointerException,
            IOException, InterruptedException {
        if (timerTask != null)
            timerTask.cancel();
        timerTask = new KnAliveTask();
        if (collector == null) {
            System.out.println("Expected collector not found :" + collector);
            throw new NullPointerException();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 3600000);
        logger.log(Level.INFO, "Started keep Alive.");
        emitter = (NotificationEmitter) getGCBean(collector);
        emitter.addNotificationListener(new KnGCNotificationListener(), null,
                null);

    }

    private void shutDownMonitor() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {

                try {
                    if (KnMemoryMonitor.this.jmxConnector != null) {
                        KnMemoryMonitor.this.jmxConnector.close();
                    }
                    if (KnMemoryMonitor.this.vm != null)
                        KnMemoryMonitor.this.vm.detach();
                } catch (IOException ex) {
                    KnMemoryMonitor.logger.log(Level.SEVERE,
                            "MonitorTask...Exception", ex);
                }
                KnMemoryMonitor.logger.log(Level.INFO,
                        "Mbean Monitor stopped....");
                if (KnMemoryMonitor.handler != null) {
                    KnMemoryMonitor.handler.flush();
                    KnMemoryMonitor.handler.close();
                }
            }
        });

    }

    private boolean AttachVirtualMachine() throws AttachNotSupportedException,
            IOException, AgentLoadException, AgentInitializationException {
        // attach to the target application
        boolean flag = false;
        VirtualMachineDescriptor vmd = getProcessId();
        logger.log(Level.INFO, " AttachVirtualMachine vmd :" + vmd);
        if (vmd != null) {
            flag = true;
            this.vm = VirtualMachine.attach(vmd);
            // get the connector address
            String connectorAddress = this.vm.getAgentProperties().getProperty(
                    CONNECTOR_ADDRESS);
            // no connector address, so we start the JMX agent
            if (connectorAddress == null) {
                StringBuilder agent = new StringBuilder(this.vm
                        .getSystemProperties().getProperty("java.home"))
                        .append(File.separator).append("lib")
                        .append(File.separator).append("management-agent.jar");
                this.vm.loadAgent(agent.toString());

                // agent is started, get the connector address
                connectorAddress = this.vm.getAgentProperties().getProperty(
                        CONNECTOR_ADDRESS);
                assert connectorAddress != null;
            }
            // establish connection to connector server
            JMXServiceURL url = new JMXServiceURL(connectorAddress);
            this.jmxConnector = JMXConnectorFactory.connect(url);
            this.jmxConnector.addConnectionNotificationListener(
                    new NotificationListener() {

                        @Override
                        public void handleNotification(
                                Notification notification, Object handback) {
                            logger.log(Level.INFO, " JMX going down :" + notification);
                            //instance.shutDownMonitor();
                            KnMemoryMonitor.this.shutDownMonitor();
                            boolean noRetry = false;
                            while (!noRetry) {
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                //KnAliveTask.cancelTask();
                                noRetry = initialize();
                            }
                        }

                    }, null, new Object());

            this.mbServerConnection = this.jmxConnector
                    .getMBeanServerConnection();
        }
        return flag;
    }

    private VirtualMachineDescriptor getProcessId() {
        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        VirtualMachineDescriptor resultVMD = null;
        String vmdId = null, vmdName = null, instance1Pid = null;
        for (VirtualMachineDescriptor vmd : vms) {
            vmdId = vmd.id();
            vmdName = vmd.displayName();
            KnMemoryMonitor.logger.log(Level.INFO, "Process Name : " + vmdName);
            KnMemoryMonitor.logger.log(Level.INFO, "Process ID : " + vmdId);

            if (XDMProcess.equals(vmdName)) {
                KnMemoryMonitor.logger.log(Level.INFO, "XDM ProcessId Found: " + vmdId);
                resultVMD = vmd;

            } else if (tomcatProcess.equals(vmdName)) {
                instance1Pid = getInstance1Pid();
                KnMemoryMonitor.logger.log(Level.INFO, "instance1Pid : " + instance1Pid);
                if (instance1Pid.equals(vmdId))
                    resultVMD = vmd;

            }

        }
        logger.log(Level.INFO, " getProcessId resultVMD :" + resultVMD);
        return resultVMD;

    }


    private GarbageCollectorMXBean getGCBean(ObjectName CollectorName)
            throws IOException, MalformedObjectNameException, NullPointerException {
        GarbageCollectorMXBean gcBean = null;
        try {
            logger.log(Level.INFO,"CollectorName Object" + CollectorName.toString());
            gcBean = newPlatformMXBeanProxy(this.mbServerConnection,  CollectorName.toString(), GarbageCollectorMXBean.class);
        } catch (Exception e) {
            e.printStackTrace();
            KnMemoryMonitor.logger.log(Level.SEVERE, "getGCBean...Exception", e);
        }
        logger.log(Level.INFO, "GCBEan " + gcBean);
        return gcBean;

    }

    private ObjectName findingCollector() throws MalformedObjectNameException {
        ObjectName CollectorName = null;

        for (OldGenCollectors collectors : OldGenCollectors.values()) {
            if (checkObjectInstanceAvailable(new ObjectName(collectors.value))) {
                CollectorName = new ObjectName(collectors.value);
            }
        }

        return CollectorName;
    }

    private boolean checkObjectInstanceAvailable(ObjectName CollectorName) {
        boolean flag = false;
        try {
            newPlatformMXBeanProxy(this.mbServerConnection,
                    CollectorName.toString(), GarbageCollectorMXBean.class);
            flag = true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Retrieving object instance exception.", e);
            e.printStackTrace();
        }
        return flag;
    }

    private String getInstance1Pid() {
        String line = "";
        String resultPid = "";
        try (BufferedReader br = new BufferedReader(new FileReader(instance1Path))) {
            line = br.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (line != null && !"".equals(line)) {
            String[] splittedStr = line.split("\\[");
            resultPid = splittedStr[0];
        }
        logger.log(Level.INFO, "resultPid " + resultPid);
        return resultPid;
    }

}
