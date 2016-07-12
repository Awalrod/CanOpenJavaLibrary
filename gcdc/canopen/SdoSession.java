package com.gcdc.canopen;

import com.gcdc.can.CanMessage;
import com.gcdc.can.Driver;
//import com.gcdc.can.CanMessageConsumer;
import java.util.TimerTask;
import java.util.Timer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


class SdoSession
{
	SubEntry subEntry;
	int txCobId;
	boolean inProgress;
	Sdo sdo;
	CanMessage msg;
	int index;
	int subIndex;
	ByteBuffer bbSeg;
	int toggle;

	SdoSession( Sdo sdo, int cobid, SubEntry sub )
	{
		this.sdo = sdo;
		subEntry = sub;
		txCobId = cobid;
		inProgress = false;
		toggle = 0;
	}


	// SDO (un)packing macros
	// Returns the command specifier (cs, ccs, scs) top 3 bits from the first byte of the SDO
	private int extractCmdSpecifier()
	{
		return(msg.data[0]>>5);
	}


// Returns the number of bytes without data from the first byte of the SDO. Coded in 2 bits
//#define getSDOn2(byte) ((byte >> 2) & 3)
	private int extractN2()
	{
		return((msg.data[0]>>2)&0x03);
	}


	private int extractCommandSpecifier()
	{
		return((msg.data[0]>>5)&0x03);
	}


// Returns the number of bytes without data from the first byte of the SDO. Coded in 3 bits
//#define getSDOn3(byte) ((byte >> 1) & 7)
	private int extractN3()
	{
		return((msg.data[0]>>1)&0x07);
	}


// Returns the transfer type from the first byte of the SDO
//#define sdo_IsExpidited(byte) ((byte >> 1) & 1)
	private int extractExpidited()
	{
		return((msg.data[0]>>1)&0x01);
	}


// Returns the size indicator from the first byte of the SDO
//#define getSDOs(byte) (byte & 1)
	private int extractSizeInd()
	{
		return((msg.data[0])&0x01);
	}


// Returns the indicator of end transmission from the first byte of the SDO
//#define getSDOc(byte) (byte & 1)
	private int extractEndTrans()
	{
		return((msg.data[0])&0x01);
	}

// Returns the toggle from the first byte of the SDO
//#define getSDOt(byte) ((UNS8)((byte >> 4) & 1))
	private int extractToggle()
	{
		return((msg.data[0]>>4)&0x01);
	}


	private boolean segmentDownloadRequest() throws COException, java.io.IOException
	{
//	System.out.println(getClass()+".segmentDownloadRequest()");

		if(toggle != extractToggle())
		{
//			System.out.println("SDO error : Toggle error : " + extractToggle());
//			sdo.sendAbort( index, subIndex, Sdo.SDOABT_TOGGLE_NOT_ALTERNED);
			throw COException.noToggle("toggle: "+extractToggle());
//			return(false);
		}
		// Nb of data to be downloaded
		int len = 7 - extractN3();
		// Store the data in the transfer structure.
		bbSeg.put(msg.data,1,len);

		// Sending the SDO response, CS = 1
		byte can_data[] = new byte[8];
//		can_data[0] = (1 << 5) | (sdoCurrXfer->toggle << 4);
		can_data[0] = (byte)((1 <<5 ) | (toggle<<4));
		for (int i = 1 ; i < 8 ; i++)
			can_data[i] = 0;
		System.out.println("SDO. Send response to download request defined at index 0x120");
		sdo.send(txCobId, can_data);

		// Inverting the toggle for the next segment.
		if( toggle == 0)
			toggle = 1;
		else
			toggle = 0;

		return(false);
	}


	private boolean downloadRequest() throws COException, java.io.IOException
	{
//		System.out.println(getClass()+".downloadRequest()");
		index = Sdo.extractIndex(msg);
		subIndex = Sdo.extractSubIndex(msg);
		inProgress = true;

//		System.out.println("Received SDO Initiate Download (to store data) defined at index 0x1200");
//	System.out.println("Writing at index: 0x"+Protocol.toIndexFmt(index)+"  subIndex: "+ subIndex);

		if(extractExpidited() !=0)
		{ // SDO expedited transfer
			// nb of data to be downloaded
			int len = 4 - extractN2();
			/* Storing the data in the line structure. */
			ByteBuffer bb1 = ByteBuffer.allocate(4);
			bb1.put(msg.data, 4, len);
			bb1.order(ByteOrder.LITTLE_ENDIAN);
//			System.out.println("SDO downloadRequest is expedited val: "+ String.format("%08X", bb1.getInt(0)).toUpperCase());
			// SDO expedited -> transfer finished. Data can be stored in the dictionary.
//			System.out.println("SDO Initiate Download is an expedited transfer. Finished. "+ len);
			// Transfering line data to object dictionary.
			try
			{
//				System.out.println("preSubentry" + subEntry.toString());
				subEntry.put(bb1);
//				System.out.println("postSubentry" + subEntry.toString());
			}
			catch(Exception e)
			{
				System.out.println(getClass()+"downloadRequest() error : Unable to copy the data in the object dictionary "+e);
				try
				{
					sdo.sendAbort( index, subIndex, Sdo.SDOABT_GENERAL_ERROR);
				}
				catch( Exception e1)
				{
					System.out.println(getClass()+".downloadRequest()  error: Unable to send abort message "+e1);
				}
			}
		}
		else
		{ // set up for segmented transfer
			if(extractSizeInd() != 0)
			{
//				ByteBuffer bb1 = ByteBuffer.allocate(4);
//				bb1.put(msg.data,4,4);
				ByteBuffer bb1 = ByteBuffer.wrap(msg.data,4,4);
				bb1.order(ByteOrder.LITTLE_ENDIAN);
				int len = bb1.getInt(0);
				bbSeg = ByteBuffer.allocate(len);
			}
		}
		//Sending a SDO, cs=3
		byte[] can_data = new byte[8];
		can_data[0] = (byte)(3 << 5);
		can_data[1] = (byte)(index & 0xFF);        /* LSB */
		can_data[2] = (byte)((index >> 8) & 0xFF); /* MSB */
		can_data[3] = (byte)(subIndex);
		for (int i = 4 ; i < 8 ; i++)
			can_data[i] = (byte)(0);
		sdo.send(txCobId, can_data);

		return(false);
	}


	private boolean segmentUploadRequest() throws COException, java.io.IOException
	{
//	System.out.println(getClass()+".segmentUploadRequest()");
		if(toggle != extractToggle())
		{
			System.out.println("SDO error : Toggle error : " + extractToggle());
			sdo.sendAbort( index, subIndex, Sdo.SDOABT_TOGGLE_NOT_ALTERNED);
			return(false);
		}
		// Nb of data to be downloaded
		int len = bbSeg.remaining();
		byte[] can_data = new byte[8];
		can_data[0] = (byte)(toggle <<4);
		// Inverting the toggle for the next segment.
		if( toggle == 0)
			toggle = 1;
		else
			toggle = 0;
		if(len > 7)
		{
			bbSeg.get(can_data, 1, 7);
			sdo.send(txCobId, can_data);
			return(true);
		}
		else
		{
			can_data[0] |= (byte)(1 | ((7-len)<<1));
			bbSeg.get(can_data,1, len);
			for (int i=len+1 ; i<8 ; i++)
				can_data[i] = 0;
			sdo.send(txCobId, can_data);
			return(false);
		}
	}


	private boolean uploadRequest() throws COException, java.io.IOException
	{
		index = Sdo.extractIndex(msg);
		subIndex = Sdo.extractSubIndex(msg);
		if(inProgress == true)
		{
			sdo.sendAbort( index, subIndex, Sdo.SDOABT_LOCAL_CTRL_ERROR);
		}
//	System.out.println(getClass()+".uploadRequest()");
		inProgress = true;
		bbSeg = subEntry.getByteBuffer();
		bbSeg.position(0);
		int len = bbSeg.remaining();
//		System.out.println("len: "+len);
		if(len >4)
		{// normal transfer. (segmented).
			byte[] can_data = new byte[8];
			can_data[0] = (byte)((2<<5) |1);
			can_data[1] = (byte)(index&0xff);
			can_data[2] = (byte)((index>>8) &0xFF);
			can_data[3] = (byte)(subIndex);
			can_data[4] = (byte)(len);
			can_data[5] = (byte)(len>>8);
			can_data[6] = (byte)(len>>16);
			can_data[7] = (byte)(len>>24);
			sdo.send(txCobId, can_data);
			return(true);
		}
		else
		{ // Expedited upload
			byte[] data = bbSeg.array();
			byte[] can_data = new byte[8];
			can_data[0] = (byte)((2<<5) | ((4-len)<<2) | 3);
			can_data[1] = (byte)(index&0xff);
			can_data[2] = (byte)((index>>8) &0xFF);
			can_data[3] = (byte)(subIndex);
			for(int i=0; i<4; i++)
			{
				if(i < data.length)
					can_data[4+i] = (byte)(data[i]);
				else
					can_data[4+i] = 0;
			}
			sdo.send(txCobId, can_data);
		}
		return(false);
	}


	boolean processMessage(CanMessage msg) throws COException, java.io.IOException
	{
		this.msg = msg;
		int cmd = extractCmdSpecifier();
//		System.out.println("SdoSession.processMessege "+cmd);
		switch(cmd)
		{
		case 0: // segment download request
			return(segmentDownloadRequest());
		case 1: // Download Request
			if(inProgress)
			{ // Search if a SDO transfer has already been initiated
				System.out.println(getClass()+"processMessage(), SDO error : Transmission already started.");
				sdo.sendAbort( index, subIndex, Sdo.SDOABT_LOCAL_CTRL_ERROR);
				return(false);
			}
			return(downloadRequest());
		case 2: // Upload Request
			return(uploadRequest());
		case 3: //segmented upload request
			return( segmentUploadRequest());
		case 4: // Received SDO abort code
		System.out.println("recieved abort code");
			return(false);
		case 5:
		case 6:
		default:
			System.out.println("Unimplemented SDO command specifier");
		break;
		}
		return(false);
	}
}

