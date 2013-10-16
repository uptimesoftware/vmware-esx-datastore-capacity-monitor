ESX Datastore Capacity 

This monitor allows users to threshold ESX datastore target capacities
- datastore capacity by percentage worst offender


INSTALL

-Copy the extracted “MonitorESXDatastoreCapacity.jar” file into the “<uptime_dir>/core” directory:
$ cp “scripts/VMWARE ESX Datastore Capacity/ MonitorESXDatastoreCapacity.jar” core/.

(Linux/Solaris monitoring stations only)
-Edit the “uptime.lax” file and add the text below to the end of the line that begins with “lax.class.path=…”:
:core/MonitorESXDatastoreCapacity.jar

-Restart the up.time core
$ /etc/init.d/uptime_core stop
$ /etc/init.d/uptime_core start
