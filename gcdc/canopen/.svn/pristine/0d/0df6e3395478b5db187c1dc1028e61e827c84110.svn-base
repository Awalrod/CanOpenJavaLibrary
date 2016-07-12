package com.gcdc.canopen;

import com.gcdc.can.CanMessage;
import com.gcdc.can.Driver;
import java.util.TimerTask;
import java.util.Timer;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;


class Heartbeat extends Protocol
{
	private List<HeartbeatSession> rheartbeats;
	private CanOpen canOpen;  // needed to get the canopen state number

	private OdEntry odComm;
	private	Timer heartbeatTimer = null;

	Heartbeat( Driver driver, boolean DEBUG, ObjectDictionary od1, CanOpen co ) throws Exception
	{
		super(driver, DEBUG, "HEARTBEAT", od1);
		rheartbeats = new ArrayList<HeartbeatSession>();
		canOpen = co;
		odComm = od1.getEntry(0x1017);

//		startHbSession();
		debugPrint("new Heartbeat");
	}


	int getState()
	{
		return(canOpen.getCanOpenState());
	}

	@Override
	boolean start() throws java.io.IOException
	{
		super.start();
		if(heartbeatTimer != null)
			return(false);

		heartbeatTimer = new Timer();
		if(odComm == null)
			System.out.println("ERROR odComm is null");
		heartbeatTimer.schedule( new HeartbeatSession(this, odComm), 0 );
		return(true);
	}


	boolean stopHbSession()
	{
		if(heartbeatTimer == null)
			return(false);
		heartbeatTimer.cancel();
		heartbeatTimer = null;
		System.gc();
		return(true);
	}


	void appendRxHeartbeat(HeartbeatSession ps)
	{
		rheartbeats.add(ps);
	}


	void send(int cobid, byte data[]) throws java.io.IOException, java.lang.Exception
	{
		CanMessage msg = new CanMessage(cobid | canOpen.getNodeId(), 0, data);
		sendMessage(msg);
	}


	boolean processMessage(CanMessage msg)
	{
		if(super.processMessage(msg) == false)
			return(false);

		debugPrint("Heartbeat.processMessage()");
		if(debug)
			msg.dump();

		Iterator<HeartbeatSession> ps;
		ps = rheartbeats.iterator();

		while(ps.hasNext())
		{
			try
			{
				if(ps.next().processMessage(msg))
				{
					notifyListeners(msg);
					return(true);
				}
			}
			catch(Exception e)
			{
				System.out.println("ERROR: Heartbeat.processMessage() threw "+e);
			}
		}

		System.out.println("Warning: heartbeat cobId not found");
		return(false);
	}
}

