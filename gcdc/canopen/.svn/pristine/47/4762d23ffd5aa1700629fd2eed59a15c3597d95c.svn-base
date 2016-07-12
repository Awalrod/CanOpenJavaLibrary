package com.gcdc.canopen;

import com.gcdc.can.CanMessage;
import com.gcdc.can.Driver;
import java.util.TimerTask;
import java.util.Timer;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;


class COException extends Exception
{
	static final int SDOABT_TOGGLE_NOT_ALTERNED	= 0x05030000;
	static final int SDOABT_TIMED_OUT		= 0x05040000;
	static final int SDOABT_OUT_OF_MEMORY		= 0x05040005; // Size data exceed SDO_MAX_LENGTH_TRANSFER
	
	// definitions used for object dictionary access. ie SDO Abort codes . (See DS 301 v4.2 p 65)
	static final int OD_READ_NOT_ALLOWED		= 0x06010001;
	static final int OD_WRITE_NOT_ALLOWED		= 0x06010002;
	static final int OD_NO_SUCH_OBJECT		= 0x06020000;
	static final int OD_NOT_MAPPABLE		= 0x06040041;
	static final int OD_LENGTH_DATA_INVALID		= 0x06070010;
	static final int OD_NO_SUCH_SUBINDEX		= 0x06090011;
	static final int OD_VALUE_RANGE_EXCEEDED	= 0x06090030; // Value range test result
	static final int OD_VALUE_TOO_LOW		= 0x06090031; // Value range test result
	static final int OD_VALUE_TOO_HIGH		= 0x06090032; // Value range test result 

	static final int SDOABT_GENERAL_ERROR		= 0x08000000; // Error size of SDO message
	static final int SDOABT_LOCAL_CTRL_ERROR	= 0x08000021;

	
	int errorCode;

	COException( String message, int errorCode)
	{
		super(message);
		this.errorCode = errorCode;
	}

	COException( String message, Throwable cause, int errorCode)
	{
		super(message, cause);
		this.errorCode = errorCode;
	}

	public int getErrorCode()
	{
		return(errorCode);
	}
	
        public static COException noSubindex(String msg)
        {
        	return(new COException("Subindex out of range: "+msg, OD_NO_SUCH_SUBINDEX));
	}        

        public static COException noObject(String msg)
        {
        	return(new COException("No Such Object: "+msg, OD_NO_SUCH_OBJECT));
	}        

        public static COException invalidLength(String msg)
        {
        	return(new COException("Invalid Length: "+msg, OD_LENGTH_DATA_INVALID));
	}        

        public static COException noToggle(String msg)
        {
        	return(new COException("Toggle not alternated: "+msg, SDOABT_TOGGLE_NOT_ALTERNED));
	}        

        public static COException notMappable(String msg)
        {
        	return(new COException("Not mappable: "+msg, OD_NOT_MAPPABLE));
	}        

        public static COException isReadOnly(String msg)
        {
        	return(new COException("Object is Read Only: "+msg, OD_WRITE_NOT_ALLOWED));
	}        
}

