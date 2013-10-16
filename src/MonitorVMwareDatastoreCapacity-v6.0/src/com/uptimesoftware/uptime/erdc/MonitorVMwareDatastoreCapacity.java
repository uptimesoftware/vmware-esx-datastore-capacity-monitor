/*
 * MonitorVMwareDatastoreCapacity.java
 *
 * Created on Sep 29, 2008
 *
 * The purpose of this extension is to allow users to threshold datastore capacities on ESX hosts.
 * 
 */

package com.uptimesoftware.uptime.erdc;
import com.uptimesoftware.uptime.erdc.baseclass.*;
import com.uptimesoftware.uptime.erdc.perfcheck.MonitorPerformanceCheckDataRetriever;
import com.uptimesoftware.uptime.erdc.performance.statistics.*;
import com.uptimesoftware.uptime.base.entity.UptimeHostIdByHostnameLoader;
import com.uptimesoftware.uptime.base.entity.configuration.MemorySizeByHostLoader;
import com.uptimesoftware.uptime.base.util.Parameters;
import java.util.*;


/**
 *
 * @author Kenneth Cheung [ken.cheung@uptimesoftware.com]
 */
public class MonitorVMwareDatastoreCapacity extends MonitorWithMonitorVariables {
    
    /*
    private String hostname         = "";
    private Double freeMem          = null;
    private Double freeSwap         = null;
    private Double freeMemPercent     = null;
    private Long physicalMem        = null;
    */
    
    /** Creates a new instance of MonitorPerformance */
    public MonitorVMwareDatastoreCapacity() {
    }
    
    @Override
    public void setParameters(Parameters params, Long instanceId) {
        super.setParameters(params, instanceId);
    }
    
    @Override
    protected void monitor(){
        try {
            MemorySizeByHostLoader memoryLoader = new MemorySizeByHostLoader();
            UptimeHostIdByHostnameLoader idLoader = new UptimeHostIdByHostnameLoader();            
            MonitorPerformanceCheckDataRetriever retriever = new MonitorPerformanceCheckDataRetriever();
            
            idLoader.setHostname(getHostname());                        
            Long hostId = idLoader.execute();
            memoryLoader.setHostId(hostId);
            // Author: Joel Pereira
            // May 1, 2012
            // - updating with changes from v5 -> v6+
            // v5
            //DataSample  dataSample      = retriever.getMostRecentDataSample(getHostname());
            // v6
            retriever.setHostname(getHostname());
            // get all the values
            ArrayList arr = (ArrayList)retriever.getDataSampleListInMinuteRange(15);
            // we only care about the first one (or any one really)
            DataSample dataSample = (DataSample)arr.get(0);
            
            
            //Aggregate   dataAggregate   = dataSample.getAggregate();                        
            //List myDiskList = dataSample.getDisks();
            List<FilesystemCapacity> myDiskList2 = dataSample.getFilesystems();
         
            /* Debug code leave in for testing
            BufferedWriter logger = new BufferedWriter(new FileWriter("c:\\MonitorPerformance.txt"));
            logger.write("Running! ------   ");
            logger.write("The collection has " + myDiskList2.size() + " objects");
            */ 

            ListIterator li = myDiskList2.listIterator();
            
            //declare some variables to get this show started
            double worstFsCapacity = 0;
            double thisFsCap = 0;
            String worstFileSystemName = null;
            
            //Forward direction traversal of all elemints in the list
             while( li.hasNext() ) {
                 FilesystemCapacity myFSCAP = (FilesystemCapacity) li.next();
                 /* Debug code 
                 logger.write(myFSCAP.getFilesystem()+ " " +myFSCAP.getPercentUsed().toString() + "% ");
                  */  
                 thisFsCap = Double.parseDouble(myFSCAP.getPercentUsed().toString());
                 //myFSCAP.getFilesystem().toString()
                 if (worstFsCapacity < thisFsCap){ 
                     worstFsCapacity = myFSCAP.getPercentUsed();
                     worstFileSystemName = myFSCAP.getFilesystem();
                 }
             }
            
             addVariable("worstOffenderFSCapacity", worstFsCapacity);
             setMessage("The most filled ESX datastore filesystem is: " + worstFileSystemName + " with a capacity of " + worstFsCapacity + "%" );
             setState(ErdcTransientState.OK);
             
             /* Test Output For Debugging
             logger.write("\nWorst FS:" + worstFileSystemName + "Worst FS Capacity: " + worstFsCapacity );
             logger.close();
             */
        } catch(Exception e) {
            setMessage("An error occured while executing the monitor\n" + e.getMessage());
             setState(ErdcTransientState.CRIT);
        }
    }
}