#
# Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.
# All Rights Reserved
# Motorola Solutions Confidential Restricted
#
 . /etc/kodiakDG.conf
 $JAVA_HOME/bin/java  -Djava.class.path=$LD_LIBRARY_PATH/ttjdbc11.jar:$JAVA_HOME/lib/tools.jar:./kodiakmemmonitor.jar com.kodiak.tool.memorymonitor.KnMemoryMonitor &