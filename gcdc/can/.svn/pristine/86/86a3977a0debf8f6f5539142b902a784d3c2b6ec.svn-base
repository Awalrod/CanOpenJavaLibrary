package com.gcdc.can.dummy;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimerTask;

import com.gcdc.can.CanMessage;
//import com.gcdc.can.TraceStorm;

public class Task extends TimerTask
{
//	private TraceStorm canStorm;

	private int nr_frame = 0;
	private int highID;
	
	Random rand = new Random( 2048  );
	
	Task( int highID )
	{
//		this.canStorm = canStorm;
		this.highID = highID;
		
		nr_frame = 0;
	}

	public void run()
	{
		CanMessage telegram = new CanMessage();
		
		GregorianCalendar date = new GregorianCalendar();
		
//		telegram.nr_telegram = nr_frame++; 
//		telegram.mesg_fill = true;
		telegram.id = rand.nextInt( highID );
//		telegram.time = lenCheck( date.get( Calendar.HOUR ) ) + ":" +
//						lenCheck( date.get( Calendar.MINUTE ) ) + ":" +
//						lenCheck( date.get( Calendar.SECOND ) );
//		telegram.date = lenCheck( date.get( Calendar.DAY_OF_MONTH ) ) + "." +
//						lenCheck( date.get( Calendar.MONTH ))  + "." +
//						lenCheck( date.get( Calendar.YEAR ) );
		
		for( byte i = 0; i < 8; i++ )
			telegram.data[ i ] = i;
		
//		canStorm.canTelegramListener( telegram );
	}
	
	private String lenCheck( int toCheck )
	{
		if( toCheck <= 9 )
			return ( "0" + toCheck );
		else
			return ( "" + toCheck );
	}
}

