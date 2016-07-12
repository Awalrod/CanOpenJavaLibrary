package com.gcdc.can;

import java.sql.Time;
import java.util.Date;

public class CanMessage
{
	public int id;
	public byte data[] = new byte[ 8 ];
	public int length;
	public int rtr;
	
	public CanMessage(int id, int rtr, byte data[])
	{
		this.id = id;
		this.length = data.length;
		this.rtr = rtr;
		int i=0;
		for(i=0;i<this.length; i++)
		{
			this.data[i] = data[i];
		}
	}

	public CanMessage()
	{
		this.id = 0;
		this.length = 0;
		this.rtr = 0;
		int i=0;
		for(i=0;i<data.length; i++)
		{
			data[i] = 0;
		}
	}
	
	private String toIdFmt(int n)
	{
		return String.format("%03X", n).toUpperCase();
	}

	
	private String toByteFmt(byte n)
	{
		return String.format("%02X", n);
	}
	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + length;
		result = prime * result + rtr;
		for(int i=0; i<length; i++)
		{
			result = prime * result + data[i];
		}
		return result;
	}


	@Override	
	public boolean equals(Object other)
	{
		if (!(other instanceof CanMessage)) 
		{
			return(false);
		}
		CanMessage that = (CanMessage)other;
		// Custom equality check here.
		if(this.id == that.id)
		{
			if(this.rtr != that.rtr)
				return(false);
		
			if(this.length == that.length)
			{
				for(int i=0;i<this.length;i++)
				{
					if(this.data[i] != that.data[i])
						return(false);
				}
				return(true);
			}
		}
		return(false);
	}

	
	public void dump()
	{
		int i;
		String sbuff = new String();
		for(i=0; i< length;i++)
		{	
			sbuff += " "+toByteFmt(data[i]);
		}
		System.out.println( toIdFmt(id) + "   [" + length +"] "+sbuff);
	}
}
