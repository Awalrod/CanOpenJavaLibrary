package com.gcdc.canopen;

import com.gcdc.can.CanMessage;
import com.gcdc.can.Driver;
//import com.gcdc.can.CanMessageConsumer;
import java.util.TimerTask;
import java.util.Timer;


class Nmt extends Protocol
{
	private CanOpen canOpen = null;
	static final int START	 	= 1;
	static final int STOP 		= 2;
	static final int PREOP 		= 128;
	static final int RESET_NODE	= 129;
	static final int RESET_COMM	= 130;

	private void sendBootUp() throws java.io.IOException, Exception
	{
		byte dataBytes[] = new byte[1];
		dataBytes[0] = (byte)canOpen.STATE_INIT;
		int id = NODE_GUARD << 7 | canOpen.getNodeId();
System.out.println("sending bootup message: "+String.format("%02x",id));
		CanMessage msg = new CanMessage(id, 0, dataBytes);
		sendMessage(msg);
	}


	Nmt( Driver driver, boolean DEBUG, CanOpen coDevice, ObjectDictionary od1 )
	{
		super(driver, DEBUG, "NMT", od1);
		debugPrint("new Nmt");
		canOpen = coDevice;
	}


	boolean processMessage(CanMessage msg)
	{
		if( (super.processMessage(msg) == false)  && (msg.id != 0))
			return(false);

		debugPrint("Nmt.processMessage()");
		if(debug)
			msg.dump();
		int nodeId = (0x000000FF & msg.data[1]);

//		debugPrint("in nodid :0x"+toIndexFmt(nodeId));
//		debugPrint("nodid :0x"+toIndexFmt(canOpen.getNodeId()));
		if(nodeId != canOpen.getNodeId())
			return(false);

		int cmd = (0x000000FF & msg.data[0]);
		debugPrint("cmd :"+cmd+" (0x"+toIndexFmt(cmd)+")");
		switch(cmd)
		{
		case START:
			debugPrint("Start");
			canOpen.toOperationalState();
		break;
		case STOP:
			debugPrint("Stop");
			canOpen.toStoppedState();
		break;
		case PREOP:
			debugPrint("Preop");
			canOpen.toPreoperationalState();
		break;
		case RESET_NODE:
			debugPrint("reset node");
		break;
		case RESET_COMM:
			debugPrint("reset comm");
		break;
		default:
			System.out.println("ERROR unknown cmd 0x"+toIndexFmt(cmd));
			return(false);
		}
		notifyListeners(msg);
		return(true);
	}


	boolean start() throws java.io.IOException
	{
		if(super.start())
		{
			debugPrint("sending boot up message");
			try
			{
				sendBootUp();
			}
			catch(Exception e)
			{
				System.out.println("ERROR; starting nmt no nodeid "+e);
				return(false);
			}
			return(true);
		}
		return(false);
	}
}

