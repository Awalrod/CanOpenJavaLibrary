package com.gcdc.canopen;

import com.gcdc.can.CanMessage;
import com.gcdc.can.Driver;
//import com.gcdc.can.CanMessageConsumer;
import java.util.TimerTask;
import java.util.Timer;


class Sync extends Protocol
{
	private Pdo pdo = null;

	private void sendSync() throws java.io.IOException, Exception
	{
		byte dataBytes[] = new byte[0];
		int id = getSubEntry(0x1005,1).getInt();
		CanMessage msg = new CanMessage(id, 0, dataBytes);
		sendMessage(msg);
	}


	Sync( Driver driver, boolean DEBUG, Pdo pdo, ObjectDictionary od1 )
	{
		super(driver, DEBUG, "SYNC", od1);
		debugPrint("new Sync");
		this.pdo = pdo;
	}


	boolean processMessage(CanMessage msg)
	{
		if( (super.processMessage(msg) == false)  && (msg.id != 0x080))
			return(false);

//		debugPrint("Sync.processMessage()");
		boolean retval = pdo.sendSyncEvents();

		notifyListeners(msg);
		return(retval);
	}


	boolean start() throws java.io.IOException
	{
		if(super.start())
		{
			debugPrint("sync starting");
			return(true);
		}
		return(false);
	}
}

