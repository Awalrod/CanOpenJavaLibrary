package com.gcdc.canopen;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;


public class DefaultOD
{
	public static ObjectDictionary create( int nodeId )
	{
		ObjectDictionary objDict = new ObjectDictionary();

		OdEntry od;
		SubEntry v1;

		// mandatory
		od = new OdEntry(0x1000, "Device Type");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.RO, "", 0x20192);
		od.appendSub(v1);

		// mandatory
		od = new OdEntry(0x1001, "Error Register");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.RO, "", (byte)0x0);
		od.appendSub(v1);

		// mandatory
		od = new OdEntry(0x1005, "SYNC COB ID");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.RW, "", (int)0x00000080);
		od.appendSub(v1);

		// mandatory if Emergency objects are supported by this device
		od = new OdEntry(0x1014, "Emergency COB ID");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.RW, "", (int)(0x00000080 | nodeId));
		od.appendSub(v1);

		// mandatory if Heartbeats are used
		od = new OdEntry(0x1017, "Producer Heartbeat Time");
		v1 = new SubEntry(SubEntry.AccessType.RW, "", (short)5000);
		od.appendSub(v1);
		objDict.insert(od);

		// mandatory
		od = new OdEntry(0x1018, "Identity Object");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.CONST, "number of Entries", (byte)0x4);
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RO, "Vendor-ID", 0x0000029C);
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RO, "Product code", 0x19);
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RO, "Revision number", 0x116);
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RO, "Serial number", 0x1);
		od.appendSub(v1);

		// mandatory, but not really used since they can't be changed
		od = new OdEntry(0x1200, "SDO server parameters");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.CONST, "number of Entries", (byte)0x2);
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RO, "COB-ID client to server", 0x00000600 | nodeId);
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RO, "COB-ID server to client", 0x00000580 | nodeId);
		od.appendSub(v1);


		// mandatory for earch RPDO COB_ID (1-4), four by default
System.out.println("WARNING: hardcoding default rpdo 1&2 node ids to 0x66");
		{
			int cobid = 0x180 | 0x66;
			//					    index,  string desc, cobId, trans type, inhibit time, event timer
			OdEntry commParamEntry = OdEntry.pdoFactory(0x1400, "RPDO 1", cobid, 0xff, 0, 0);
			objDict.insert(commParamEntry);
			OdEntry mappingEntry = OdEntry.pdoMappingFactory(0x1600, "RPDO 1 mapping parameter", 0x62000008);  // index 0x6200 subindex: 01 len 0x08
			objDict.insert(mappingEntry);
		}
		{
			int cobid = 0x280 | 0x66;
			OdEntry commParamEntry = OdEntry.pdoFactory(0x1401, "RPDO 2", cobid, 0xff, 0, 0);
			objDict.insert(commParamEntry);
			OdEntry mappingEntry = OdEntry.pdoMappingFactory(0x1601, "RPDO 2 mapping parameter", 0x64110110, 0x64110210, 0x64110310);  // solenoid volage, current and node cpu teemperature degc*10
			objDict.insert(mappingEntry);
		}
System.out.println("WARNING: hardcoding default rpdo 3&4 node ids to 0x33");
		{
			int cobid = 0x180 | 0x33;
			OdEntry commParamEntry = OdEntry.pdoFactory(0x1402, "RPDO 3", cobid, 0xff, 0, 0);
			objDict.insert(commParamEntry);
			OdEntry mappingEntry = OdEntry.pdoMappingFactory(0x1602, "RPDO 3 mapping parameter", 0x62000008);
			objDict.insert(mappingEntry);
		}
		{
			int cobid = 0x280 | 0x33;
			OdEntry commParamEntry = OdEntry.pdoFactory(0x1403, "RPDO 4", cobid, 0xff, 0, 0);
			objDict.insert(commParamEntry);
			OdEntry mappingEntry = OdEntry.pdoMappingFactory(0x1603, "RPDO 4 mapping parameter", 0x64110110, 0x64110210, 0x64110310);
			objDict.insert(mappingEntry);
		}


		// mandatory for each TPDO COB_ID (1-4) four by default
		for(int i=0; i<4; i++)
		{
			OdEntry commParamEntry  = OdEntry.pdoFactory(0x1800+i, "TPDO "+(i+1), ((i+2)*0x100)|nodeId, 0xff, 0, 0);
			objDict.insert(commParamEntry);
			OdEntry mappingEntry = OdEntry.pdoMappingFactory(0x1A00+i, "TPDO "+(i+1)+ "mapping parameter", 0x64010010+0x0100*i);
			objDict.insert(mappingEntry);
		}


		// Standardized Device Profile entries
		
		// Digital Input Entries
		od = new OdEntry(0x6000, "Read Inputs");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.RO, "Inputs 0x1 to 0x8", (byte)(0xff));
		od.appendSub(v1);

		od = new OdEntry(0x6002, "Polarity Input");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Inputs 0x1 to 0x8", (byte)(0x0));
		od.appendSub(v1);


		// Digital Output Entries
		od = new OdEntry(0x6200, "Write Outputs");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Outputs 0x1 to 0x8", (byte)(0xff));
		od.appendSub(v1);

		od = new OdEntry(0x6202, "Change Polarity Outputs");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Outputs 0x1 to 0x8", (byte)(0x0));
		od.appendSub(v1);

		od = new OdEntry(0x6206, "Error Mode Outputs");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Outputs 0x1 to 0x8", (byte)(0));
		od.appendSub(v1);

		od = new OdEntry(0x6207, "Error Value Outputs");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Outputs 0x1 to 0x8", (byte)(0));
		od.appendSub(v1);


		// Analog Input
		od = new OdEntry(0x6401, "Analog Input");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.RO, "Analog In 1", (short)(0x1111));
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RO, "Analog In 2", (short)(0x2222));
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RO, "Analog In 3", (short)(0x3333));
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RO, "Analog In 4", (short)(0x4444));
		od.appendSub(v1);
		
		// Analog Output
		od = new OdEntry(0x6411, "Analog Output");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Out 1", (short)(0x1111));
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Out 2", (short)(0x2222));
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Out 3", (short)(0x3333));
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Out 4", (short)(0x4444));
		od.appendSub(v1);
		
		od = new OdEntry(0x6446, "Analog Output Offset");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Offset 1", (0));
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Offset 2", (0));
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Offset 3", (0));
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Offset 4", (0));
		od.appendSub(v1);

		od = new OdEntry(0x6447, "Analog Output Scale");
		objDict.insert(od);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Scale 1", (0x0000FFFF));
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Scale 2", (0x0000FFFF));
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Scale 3", (0x0000FFFF));
		od.appendSub(v1);
		v1 = new SubEntry(SubEntry.AccessType.RW, "Scale 4", (0x0000FFFF));
		od.appendSub(v1);
		

		return(objDict);
	}
}

