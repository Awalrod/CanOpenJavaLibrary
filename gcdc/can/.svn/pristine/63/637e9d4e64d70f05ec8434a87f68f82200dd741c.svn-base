package com.gcdc.can.datagram;

//import java.util.Calendar;
//import java.util.GregorianCalendar;
//import java.util.Random;
import java.util.TimerTask;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.gcdc.can.CanMessage;
import com.gcdc.can.CanMessageConsumer;

public class Task extends Thread
{
	DatagramSocket socket;
	private CanMessageConsumer cmc = null;

	private byte[] inBuff;
	DatagramPacket inPacket;

	InetAddress address;
	int port;
	
	Task( String ipAddr, int port) throws Exception, java.net.ConnectException
	{
		this.port = port;
		address = InetAddress.getByName(ipAddr);
//System.out.println("ipAddr: "+ipAddr);
//System.out.println("addr: "+address);
//System.out.println("port: "+port);
		socket = new DatagramSocket();
		inBuff = new byte[256];
		inPacket = new DatagramPacket(inBuff, inBuff.length, address, port);
		
//		System.out.println("url: "+address);
	}


	public void setCmc(CanMessageConsumer x)
	{
		cmc = x;
	}


	private String toByteFmt(byte n)
	{
		return String.format("%02X", n);
	}


	private CanMessage convertRaw(byte[] rawIn)
	{
		int rtr = 0;

		ByteBuffer buffer = ByteBuffer.wrap(rawIn, 0, 4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int id = buffer.getInt();

		if( (id & 0x80000000) != 0)
		{// extended frame format flag, 29 bit instead of 11bit
		}
		if( (id & 0x40000000) != 0)
		{// rtr, remote transmission request flag
			rtr = 1;
		}
		if( (id & 0x20000000) != 0)
		{// error fram flag,
		}
		id &= 0x1FFFFFFF;

		int length = (int)(rawIn[4]) & 0x0F;

		byte[] data = new byte[length];
		for( int i=0; i<data.length; i++)
		{
			data[i] = rawIn[i+8];
		}

		CanMessage telegram = new CanMessage(id, rtr , data);
		return(telegram);
	}


	private byte[] formatTxMsg(CanMessage msg)
	{
		byte[] retval = new byte[16];
		ByteBuffer buffer = ByteBuffer.wrap(retval, 0, 4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int id = msg.id;
		if(msg.rtr != 0)
			id |= 0x40000000;
		buffer.putInt(id);
		retval[4] = (byte)(msg.length);
		for( int i=0; i<msg.length; i++)
		{
			retval[8+i] = msg.data[i];
		}
		return(retval);
	}


	public void sendMsg(CanMessage msg) throws java.io.IOException
	{
		byte[] rawOut = formatTxMsg(msg);
		DatagramPacket packet = new DatagramPacket(rawOut, rawOut.length, address, port);
		socket.send(packet);
//		System.out.println("datagram sent to: "+address+" port: "+port );
	}


	public void run()
	{
		try
		{
			while(true)
			{
				socket.receive(inPacket);
				
				CanMessage msg = convertRaw(inPacket.getData() );
				if(cmc != null)
				{ // eventually replace cmc with a list of message consumers, so one driver can be used
				// with lots of consumers, but this works for now
					cmc.processMessage(msg);
				}
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}

