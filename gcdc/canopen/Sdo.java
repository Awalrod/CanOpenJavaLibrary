package com.gcdc.canopen;

import com.gcdc.can.CanMessage;
import com.gcdc.can.Driver;
//import com.gcdc.can.CanMessageConsumer;
import java.util.TimerTask;
import java.util.Timer;
import java.util.HashMap;

class Sdo extends Protocol
{
	static final int SDOABT_TOGGLE_NOT_ALTERNED	= 0x05030000;
	static final int SDOABT_TIMED_OUT		= 0x05040000;
	static final int SDOABT_OUT_OF_MEMORY		= 0x05040005; // Size data exceed SDO_MAX_LENGTH_TRANSFER
	static final int SDOABT_GENERAL_ERROR		= 0x08000000; // Error size of SDO message
	static final int SDOABT_LOCAL_CTRL_ERROR	= 0x08000021;

	// definitions used for object dictionary access. ie SDO Abort codes . (See DS 301 v.4.02 p.48)
	static final int OD_READ_NOT_ALLOWED		= 0x06010001;
	static final int OD_WRITE_NOT_ALLOWED		= 0x06010002;
	static final int OD_NO_SUCH_OBJECT		= 0x06020000;
	static final int OD_NOT_MAPPABLE		= 0x06040041;
	static final int OD_LENGTH_DATA_INVALID		= 0x06070010;
	static final int OD_NO_SUCH_SUBINDEX		= 0x06090011;
	static final int OD_VALUE_RANGE_EXCEEDED	= 0x06090030; // Value range test result
	static final int OD_VALUE_TOO_LOW		= 0x06090031; // Value range test result
	static final int OD_VALUE_TOO_HIGH		= 0x06090032; // Value range test result 
	

	HashMap<Integer, SdoSession> sessions = null;


	Sdo( Driver driver, boolean DEBUG, ObjectDictionary od1 ) throws Exception
	{
		super(driver, DEBUG, "SDO", od1);
		addCobId(0x1200,1);
		addCobId(0x1200,2);
		debugPrint("new Sdo");
		sessions = new HashMap<Integer, SdoSession>();
	}


	void sendAbort(int index, int subIndex, int abortCode) throws java.io.IOException
	{
		byte can_data[] = new byte[8];
		debugPrint("Sending SDO abort "+ abortCode);
		can_data[0] = (byte)(0x80);
		// Index
		can_data[1] = (byte)(index & 0xFF);     // LSB
		can_data[2] = (byte)((index >> 8) & 0xFF); // MSB
		// Subindex
		can_data[3] = (byte)(subIndex);
		// Data
		can_data[4] = (byte)(abortCode & 0xFF);
		can_data[5] = (byte)((abortCode >> 8) & 0xFF);
		can_data[6] = (byte)((abortCode >> 16) & 0xFF);
		can_data[7] = (byte)((abortCode >> 24) & 0xFF);
		int cobid = 0;
		try
		{
			cobid = getSubEntry(0x1200,2).getInt();
		}
		catch(Exception e)
		{
			System.out.println("Sdo.sendAbort(), error getting cobid "+e)	;
			return;
		}
		send( cobid, can_data);

	}


	void send(int cobid, byte data[]) throws java.io.IOException
	{
		CanMessage msg = new CanMessage(cobid, 0, data);
		sendMessage(msg);
	}


	boolean processMessage(CanMessage msg)
	{
		int index=0;
		int subIndex = 0;
		if(super.processMessage(msg) == false)
			return(false);

		// Test if the size of the SDO is ok
		if(msg.length != 8)
		{
			try
			{
				debugPrint("Error size SDO is "+msg.length);
				sendAbort(0, 0, SDOABT_GENERAL_ERROR );
			}
			catch(Exception e)
			{
				System.out.println("Sdo error when sending abort: "+e);
			}
			return(false);
		}


			// Look for an SDO transfer already initiated.
		try
		{
			SdoSession session = sessions.get(msg.id);
			if(session == null)
			{ // session not currently in progress, create new session
				index = extractIndex(msg);
				subIndex = extractSubIndex(msg);
				SubEntry se1 = getSubEntry(index,subIndex);
				int txCobId = getSubEntry(0x1200,2).getInt();
				session = new SdoSession(this, txCobId, se1);
				sessions.put(msg.id, session);
			}
			if(session.processMessage(msg) == false)
			{ // process the sdo message in the session
				sessions.remove(msg.id);
			}
		}
		catch(COException e1)
		{
			System.out.println(e1);
			try
			{
				sendAbort(index,subIndex, e1.getErrorCode());
			}
			catch( java.io.IOException e)
			{
				System.out.println("Sdo.processMessage: " +e);
			}
			return(false);
		}
		catch(java.io.IOException e)
		{
			System.out.println("Sdo.processMessage: " +e);
//			try
//			{
//				sendAbort(index,subIndex, OD_NO_SUCH_SUBINDEX);
//			}
//			catch(java.io.IOException e1)
//			{
//				System.out.println("Sdo.processMessage: " +e1);
//			}
			return(false);
		}
		notifyListeners(msg);
		return(true);
	}


	public void run()
	{
		debugPrint("Sdo expired timer");
	}
}

