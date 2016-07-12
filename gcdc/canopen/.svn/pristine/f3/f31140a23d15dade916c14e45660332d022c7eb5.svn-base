package com.gcdc.canopen;

//import com.gcdc.can.CanMessage;
//import com.gcdc.can.Driver;
//import com.gcdc.can.CanMessageConsumer;
//import java.util.TimerTask;
//import java.util.Timer;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;


public class ObjectDictionary
{
	HashMap<Integer, OdEntry> hmap = null;

	ObjectDictionary(  )
	{
		hmap = new HashMap<Integer, OdEntry>();
	}


	static  String toIndexFmt(int index)
	{
		return( String.format("%04X", index).toUpperCase());
	}

	public void addListener(int index, int subindex, CanOpenListener coListener) throws Exception
	{
		SubEntry se = getSubEntry(index, subindex);
		se.addListener(coListener);
	}


	OdEntry getEntry(int index) throws COException
	{
		OdEntry oe = hmap.get(index);
		if(oe == null)
		{
			throw COException.noObject("Index: 0x"+toIndexFmt(index));
		}
		return(oe);
	}

	public SubEntry getSubEntry(int index, int subindex) throws COException
	{
		OdEntry oe = getEntry(index);
		return( oe.getSub(subindex));
	}


	void insert(OdEntry odEntry)
	{
		hmap.put(odEntry.index, odEntry);
	}


	void dump()
	{
		/* Display content using Iterator*/
		Set set = hmap.entrySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext())
		{
			Map.Entry mentry = (Map.Entry)iterator.next();
			System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
			((OdEntry)mentry.getValue()).dump();
		}
	}
}

