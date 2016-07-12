package com.gcdc.canopen;

import com.gcdc.can.CanMessage;
import com.gcdc.can.Driver;
import com.gcdc.can.CanMessageConsumer;
import java.util.TimerTask;
import java.util.Timer;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.*;


class Protocol extends TimerTask
{
	private  Driver busDriver;
	protected boolean debug = false;
	protected boolean isEnabled = false;

	private Timer timer;
	protected String name;
	//  Function Codes
	//   ---------------
	//     defined in the canopen DS301
	static final int NMT =     0x0;
	static final int SYNC =      0x1;
	static final int TIME_STAMP= 0x2;
	static final int PDO1tx =    0x3;
	static final int PDO1rx =    0x4;
	static final int PDO2tx =    0x5;
	static final int PDO2rx =    0x6;
	static final int PDO3tx =    0x7;
	static final int PDO3rx =    0x8;
	static final int PDO4tx =    0x9;
	static final int PDO4rx =    0xA;
	static final int SDOtx =     0xB;
	static final int SDOrx =     0xC;
	static final int NODE_GUARD = 0xE;
	static final int LSS = 0xF;

	private List<AtomicInteger> cobIdList;

	private ObjectDictionary objDict;

        private List<CanOpenListener> messageListeners;

	void debugPrint(String v)
	{
		if(debug == true)
			System.out.println(v);
	}


	static int extractIndex(CanMessage msg)
	{
		int retval = ((((int)msg.data[2] << 8)& 0x0000FF00) | (((int)msg.data[1])& 0x000FF));
		return(retval);
	}


	static int extractSubIndex(CanMessage msg)
	{
		return(((int)msg.data[3])& 0x000FF);
	}


	static String toIndexFmt(int index)
	{
		return( String.format("%04X", index).toUpperCase());
	}


	Protocol( Driver driver, boolean DEBUG, String name, ObjectDictionary od1)
	{
		if( DEBUG )
		{
			debug = true;
		}
		debugPrint("new Protocol "+name);
		busDriver = driver;
		cobIdList = new ArrayList<AtomicInteger>();
		this.name = name;
		objDict = od1;
		messageListeners = new ArrayList<CanOpenListener>();
	}


	boolean start() throws java.io.IOException
	{
		if(isEnabled == true)
			return(false);

		isEnabled = true;
		debugPrint("Protocol timers starting for "+name);
		timer = new Timer();
		timer.schedule( this, 10000, 10000 );
		return(true);
	}


	boolean stop()
	{
		if(isEnabled == false)
			return(false);
		isEnabled = false;
		timer.cancel();
		return(true);
	}


	boolean processMessage(CanMessage msg)
	{
		if(isEnabled == false)
			return (false);

		boolean retval = isValidCobId(msg.id);

//		if(retval)
//			debugPrint("protocol unimplemented for "+name+" but cob-id exists "+String.format("%04X", msg.id));
//		else
//			debugPrint("protocol unimplemented for "+name+" and no cob-id");

		return(retval);
	}


	void sendMessage(CanMessage msg) throws java.io.IOException
	{
		busDriver.sendMessage(msg);
	}


	void addCobId(int index, int subIndex) throws Exception
	{
		OdEntry od = objDict.getEntry(index);
		addCobId(od, subIndex);
	}

	void addCobId(OdEntry od, int subentry) throws COException
	{
		try {
			Object ref = od.getSub(subentry).getIntReference();
			addCobId((AtomicInteger) ref);
		}catch(COException c){
			c.printStackTrace();
		}
	}


	void addCobId(AtomicInteger cobId)
	{
		cobIdList.add(cobId);
	}


	SubEntry getSubEntry(int index, int subindex) throws COException
	{
//		OdEntry od = objDict.getEntry(index);
		return(objDict.getSubEntry(index, subindex));
	}


	boolean isValidCobId(int cobId)
	{
		Iterator<AtomicInteger> cl = cobIdList.iterator();
		while(cl.hasNext())
		{
			int listCobId = cl.next().intValue();
//		System.out.println("incomming cobId: 0x"+toIndexFmt(cobId)+"  list: 0x"+toIndexFmt(listCobId));
			if(cobId == listCobId)
			{
//				System.out.println("match found");
				return(true);
			}
		}
//		System.out.println("No match found");
		return(false);
	}


	public void run()
	{
		debugPrint("CanOpen Protocol timer expired for "+name);
	}

	public void addListener(CanOpenListener coListener)
	{
		messageListeners.add(coListener);
	}


	public void removeListener(CanOpenListener coListener)
	{
		messageListeners.remove(coListener);
	}


	void notifyListeners(CanMessage msg)
	{
		Iterator<CanOpenListener> coli = messageListeners.iterator();
		while(coli.hasNext())
		{
			CanOpenListener coListener = coli.next();
			coListener.onMessage(msg);
		}
	}

}

