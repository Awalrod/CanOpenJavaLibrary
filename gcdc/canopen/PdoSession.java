package com.gcdc.canopen;

import com.gcdc.can.CanMessage;
//import com.gcdc.can.Driver;
//import com.gcdc.can.CanMessageConsumer;
//import java.util.TimerTask;
//import java.util.Timer;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
import java.util.concurrent.atomic.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
//import net.magik6k.bitbuffer.*;

class PdoSession
{
	AtomicInteger cobId;
	Pdo pdo;
	OdEntry params;
	OdEntry mapping;

	// differnt types of transmission types
	static final int TRANS_SYNC_ACYCLIC = 0;
	static final int TRANS_SYNC_MIN = 1;
	static final int TRANS_SYNC_MAX = 240;
	static final int TRANS_RTR_SYNC = 252;
	static final int TRANS_RTR = 253;
	static final int TRANS_EVENT_SPECIFIC = 254;
	static final int TRANS_EVENT_PROFILE = 255;

	private int transSyncCount = 0;
	private CanMessage lastMsg = null;

	PdoSession( Pdo pdo, OdEntry params, OdEntry mapping ) throws COException
	{
		this.pdo = pdo;
		this.params = params;
		this.mapping = mapping;
		cobId = (AtomicInteger)params.getSub(1).getIntReference();
		debugPrint("pdo cobId: "+String.format("%04X", cobId.get()).toUpperCase());
		pdo.addCobId(cobId);
	}
void debugPrint(String msg)
{

	//System.out.println(msg);
	//for extra info on stdout uncomment above line
}

	boolean processMessage(CanMessage msg) throws Exception
	{
		if(msg.id != cobId.intValue())
			return(false);
debugPrint("PdoSession cobId match "+  String.format("%04X", msg.id).toUpperCase());
		int numMaps = mapping.getSub(0).getInt();
		int totalBits=0;
		ByteBuffer bb1 = ByteBuffer.allocate(8);
		bb1.put(msg.data, 0, msg.length);
		bb1.order(ByteOrder.LITTLE_ENDIAN);
		bb1.position(0);
		for(int i=1;i<=numMaps; i++)
		{
			int map = mapping.getSub(i).getInt();
			debugPrint("PdoSession.processMessage() map: 0x"+ String.format("%08x", map).toUpperCase());
			int index = 0x0000ffff&(map>>16);
			int sub = 0x000000ff&(map>>8);
			int bits = 0x000000ff&(map);
			SubEntry se = pdo.getSubEntry(index,sub);
			if(bits == 8)
			{
				byte b1 = bb1.get();
				se.set(b1);
			}
			else if(bits == 16)
			{
				short s1 = bb1.getShort();
debugPrint("PdoSession.processMessage() setting Object Dict entry to 0x"+ String.format("%04x", s1).toUpperCase());
				se.set(s1);
			}
			else if(bits == 32)
			{
				int i1 = bb1.getInt();
				se.set(i1);
			}
			else
			{
				System.out.println("Error: PdoSession.processMsg() "+bits+" not supported, Fixme!");
			}
			totalBits += bits;
			if(totalBits >64)
				break;
		}
		return(true);
	}


	private static long mask(final int bitsPerValue)
	{
		return (bitsPerValue == 64) ? -1 : ((1L << bitsPerValue) - 1L);
	}


	private CanMessage buildMessage() throws Exception
	{
//		BitBuffer bb2 = BitBuffer.allocate(8*8); // msg size is 8 bytes max
		ByteBuffer bb1 = ByteBuffer.allocate(8);
		bb1.clear();
		int numMaps = mapping.getSub(0).getInt();
		int totalBits=0;
		for(int i=1;i<=numMaps; i++)
		{
			int map = mapping.getSub(i).getInt();
//			System.out.println("PdoSession.buildMessage() map: 0x"+ String.format("%08x", map).toUpperCase());
			int index = 0x0000ffff&(map>>16);
			int sub = 0x000000ff&(map>>8);
			int bits = 0x000000ff&(map);
			int val = pdo.getSubEntry(index,sub).getInt();
//	System.out.println("PdoSession.buildMessage() val: 0x"+ String.format("%04x", val).toUpperCase()+" bits:"+bits);

//			bb2.putInt(val,bits);
			long bitmask = mask(bits);
			if(bits == 8)
			{
				byte b1 = (byte)val;
				bb1.put(b1);
			}
			else if(bits == 16)
			{
				short s1 = (short)val;
				bb1.putShort(s1);
			}
			else if(bits == 32)
			{
				bb1.putInt(val);
			}
			else
			{
				System.out.println("Error: PdoSession.buildPdo() "+bits+" not supported, Fixme!");
			}
			totalBits += bits;
			if(totalBits >64)
				break;
		}
		bb1.order(ByteOrder.LITTLE_ENDIAN);
		int numBytes = totalBits/8;
		if( (totalBits%8) !=0)
			numBytes++;
		if(numBytes>8)
		{
			numBytes = 8;
			totalBits = 8*8;
			System.out.println("Error mapping results in msg overflow, truncating");
		}
		byte[] msgd = new byte[numBytes];
//		bb2.setPosition(0);
//		bb2.get(msgd,0,msgd.length,totalBits);
		bb1.position(0);
		bb1.get(msgd,0,numBytes);
		return(new CanMessage(cobId.intValue(), 0, msgd));
	}


	boolean syncEvent() throws Exception
	{
		int id = cobId.intValue();
		if( (id & 0x80000000)== 0x80000000)
			return(false);
		int transType = params.getSub(2).getInt();
		if( (transType>=TRANS_SYNC_MIN) && (transType<=TRANS_SYNC_MAX) )
		{//cyclic synchronous type
			transSyncCount++;
			if(transSyncCount >= transType)
			{
				transSyncCount = 0;
				CanMessage msg = buildMessage();
				lastMsg = msg;
//				lastMsg.dump();
				pdo.sendMessage(lastMsg);
			}
		}
		else if(transType == TRANS_RTR_SYNC)
		{
			CanMessage msg = buildMessage();
			lastMsg = msg;
//			pdo.sendMessage(lastMsg);
		}
		else if( transType == TRANS_SYNC_ACYCLIC)
		{
			CanMessage msg = buildMessage();
			if(!msg.equals(lastMsg))
			{
				lastMsg = msg;
				pdo.sendMessage(lastMsg);
			}
		}
//		System.out.println("PdoSession.syncEvent() cobId: 0x"+ String.format("%04X", cobId.intValue()).toUpperCase());
		return(true);
	}
}

