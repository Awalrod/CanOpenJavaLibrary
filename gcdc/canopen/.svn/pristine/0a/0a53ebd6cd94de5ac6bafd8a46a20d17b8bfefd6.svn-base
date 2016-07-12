package com.gcdc.canopen;
import java.util.concurrent.atomic.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SubEntry
{
//	private AtomicInteger atomicInt;
//	ByteBuffer bbuf;
	// can open defined access types
	enum AccessType {
		RW(0), WO(0x01), RO(0x02), CONST(0x03);
		private int value;
		private AccessType(int value)
		{
			this.value = value;
		}

	};
	AccessType accessType;	// defines access type as defined by can open
	byte byte1;
        List<CanOpenListener> listeners;


	///************************* CONSTANTES **********************************
	//  this are static defined datatypes taken fCODE the canopen standard. They
	//  are located at index 0x0001 to 0x001B. As described in the standard, they
	//  are in the object dictionary for definition purpose only. a device does not
	//  to support all of this datatypes.
	enum DataType {
		bit(0x01), // is boolean in spec, but that is a reserved keyword in java
		int8(0x02), int16(0x03), int32(0x04), uint8(0x05), uint16(0x06), uint32(0x07), real32(0x08),
		visible_string(0x09), octet_string(0x0A), unicode_string(0x0B),
		time_of_day(0x0C), time_difference(0x0D),
		domain(0x0F),
		int24(0x10), real64(0x11), int40(0x12), int48(0x13), int56(0x14), int64(0x15), uint24(0x16),
		pdo_mapping(0x21), sdo_parameter(0x22), 	identity(0x23);
			// CanFestival is using 0x24 to 0xFF to define some types containing a value range (See how it works in objdict.c)

		private int value;
		private DataType(int value)
		{
			this.value = value;
		}
	};
	DataType dataType; 	// Defines of what datatype the entry is as defined by can open
	int size;      	// The size (in Bytes) of the variable as defined by can open
	Object pObject;  // This is the pointer of the Variable
	String pname;
	Boolean pdoMapping = false;


	SubEntry(AccessType accessT, DataType dataT, int size, String name)
	{
		this.accessType = accessT;
		this.dataType = dataT;
		this.size = size;
		this.pname = name.trim();
		listeners = new ArrayList<CanOpenListener>();
	}


	SubEntry(AccessType accessT, DataType dataT, int size, String name, Object obj)
	{
		this(accessT, dataT, size, name);
		pObject = obj;
	}


	SubEntry(AccessType accessT, String name, String val)
	{
		this(accessT, DataType.visible_string, val.length(), name);
		pObject = new String(val);
	}

	SubEntry(AccessType accessT, String name, int x)
	{
		this(accessT, DataType.uint32, 4, name);
		AtomicInteger i1 = new AtomicInteger(x);
		pObject = i1;
	}


	SubEntry(AccessType accessT, String name, short x)
	{
		this(accessT, DataType.uint16, 2, name);
		AtomicInteger i1 = new AtomicInteger((int)x);
		pObject = i1;
	}


	SubEntry(AccessType accessT, String name, byte x)
	{
		this(accessT, DataType.uint8, 1, name);
		byte1 = x;
		pObject = null;
	}


	SubEntry(AccessType accessT, String name, boolean x)
	{
		this(accessT, DataType.int8, 1, name);
		Boolean i1 = new Boolean(x);
		pObject = i1;
	}


	void dump()
	{
		System.out.println( " "+ accessType + "  "+ dataType + "  \tsize:"+ size);
	}


	public void addListener(CanOpenListener coListener)
	{
		listeners.add(coListener);
	}


	public void removeListener(CanOpenListener coListener)
	{
		listeners.remove(coListener);
	}


	private void notifyListeners()
	{
		Iterator<CanOpenListener> coli = listeners.iterator();
		while(coli.hasNext())
		{
			CanOpenListener coListener = coli.next();
			coListener.onObjDictChange(this);
		}
	}


	String toIniString(int index, int sub)
	{
		StringBuilder retval = new StringBuilder("[");
		retval.append(String.format("%04Xsub%d]\r\n", index, sub));
		retval.append("ParameterName=");
		retval.append(pname);
		retval.append("\r\n");
		retval.append("ObjectType=0x7\r\n");
		retval.append(toIniString());
		return(retval.toString());
	}



	String getStringValue()
	{
		if ( dataType == DataType.uint8)
		{
			return(String.format("%02x",byte1));
		}
		else if( dataType == DataType.uint32)
		{
			AtomicInteger i1 = (AtomicInteger)pObject;
			return( String.format("%08x",i1.intValue()) );
		}
		else if( dataType == DataType.uint16)
		{
			AtomicInteger i1 = (AtomicInteger)pObject;
			return( String.format("%04x",(i1.intValue() & 0x0000ffff)) );
		}
		return("Unable to cast datatype to int");
	}


	String toIniString()
	{
		StringBuilder retval = new StringBuilder("DataType=");
		retval.append(String.format("0x%04X",dataType.value));
		retval.append("\r\nAccessType=");
		String temp = new String(""+accessType);
		retval.append(temp.toLowerCase());
		retval.append("\r\nDefaultValue=0x" );
		retval.append(getStringValue());
		if(pdoMapping) retval.append("\r\nPDOMapping=0x1\r\n");
		else retval.append("\r\nPDOMapping=0x0\r\n");

		return(retval.toString());
	}

	public String toString()
	{
		return(toIniString());
	}


	ByteBuffer getByteBuffer() throws COException
	{
		ByteBuffer retval = ByteBuffer.allocate(size);
		retval.order(ByteOrder.LITTLE_ENDIAN);
		if ( dataType == DataType.uint8)
		{
			retval.put(byte1);
		}
		else if( dataType == DataType.uint32)
		{
			AtomicInteger i1 = (AtomicInteger)pObject;
			retval.putInt(i1.intValue());
		}
		else if( dataType == DataType.uint16)
		{
			AtomicInteger i1 = (AtomicInteger)pObject;
			short s =  (short)(i1.intValue() & 0x0000ffff);
			retval.putShort(s);
		}
		else if( dataType == DataType.visible_string)
		{
			String i1 = (String)pObject;
			retval.order(ByteOrder.BIG_ENDIAN);
			retval.put(i1.getBytes());
		}
		else
			throw COException.notMappable("FIXME unimplimented cast to ByteBuffer");
		return(retval);
	}


	public int getInt() throws COException
	{
		if ( dataType == DataType.uint8)
		{
			return(byte1);
		}
		else if( dataType == DataType.uint32)
		{
			AtomicInteger i1 = (AtomicInteger)pObject;
			return( i1.intValue() );
		}
		else if( dataType == DataType.uint16)
		{
			AtomicInteger i1 = (AtomicInteger)pObject;
			return( (i1.intValue() & 0x0000ffff) );
		}
		throw COException.invalidLength(String.format("dataType: 0x%x",dataType));
	}


	Object getIntReference()
	{
		return(pObject);
	}


	void set(int val) throws COException
	{
		if( (accessType == AccessType.CONST) || (accessType == AccessType.RO))
			throw COException.isReadOnly("Value cannot be written, Read only or Const");

		if ( dataType == DataType.uint8)
		{
			byte1 = (byte)val;
		}
		else if( (dataType == DataType.uint32) || (dataType == DataType.int32))
		{
			AtomicInteger i1 = (AtomicInteger)pObject;
			i1.set(val);
		}
		else if( dataType == DataType.uint16)
		{
			AtomicInteger i1 = (AtomicInteger)pObject;
			i1.set( val & 0x0000ffff);
		}
		else
		{
			throw COException.notMappable("unable to cast datatype from int is "+dataType);
		}
		notifyListeners();
	}

	void setIgnorePermissions(int val) throws COException
	{
		if ( dataType == DataType.uint8)
		{
			byte1 = (byte)val;
		}
		else if( (dataType == DataType.uint32) || (dataType == DataType.int32))
		{
			AtomicInteger i1 = (AtomicInteger)pObject;
			i1.set(val);
		}
		else if( dataType == DataType.uint16)
		{
			AtomicInteger i1 = (AtomicInteger)pObject;
			i1.set( val & 0x0000ffff);
		}
		else
		{
			throw COException.notMappable("unable to cast datatype from int is "+dataType);
		}
		notifyListeners();
	}


	void put(ByteBuffer val) throws COException
	{
		if( (accessType == AccessType.CONST) || (accessType == AccessType.RO))
			throw COException.isReadOnly("Value cannot be written, Read only or Const");

		if( dataType == DataType.uint8)
		{
			byte1 = val.get(0);
		}
		else if( (dataType == DataType.uint32) || (dataType == DataType.int32))
		{
			AtomicInteger i1 = (AtomicInteger)pObject;
//			System.out.println(getClass()+".put()  now");
			i1.set(val.getInt(0));
		}
		else if( dataType == DataType.uint16)
		{
			AtomicInteger i1 = (AtomicInteger)pObject;
			i1.set( val.getInt(0) & 0x0000ffff);
		}
		else
		{
			throw COException.notMappable("unable to cast datatype from ButBuffer to  "+dataType);
		}
		notifyListeners();
	}

}
