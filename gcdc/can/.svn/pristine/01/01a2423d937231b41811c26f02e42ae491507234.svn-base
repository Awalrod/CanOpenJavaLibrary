package com.gcdc.can.dummy;
import java.util.Timer;

import com.gcdc.can.CanMessage;
//import com.gcdc.can.TraceStorm;
import com.gcdc.can.Driver;
import com.gcdc.can.ConfigFile;
import com.gcdc.can.CanMessageConsumer; 


public class DummyDriver implements Driver
{
//	private TraceStorm canStorm;
//	private boolean DEBUG;
	private CanMessageConsumer cmc;		
	private ConfigFile cf;
	
	private Timer timer;
	public DummyDriver( ConfigFile cf, boolean DEBUG )
	{
		this.cf = cf;
//		this.canStorm = canStorm;
		
		if( DEBUG )
			System.out.println( "Dummy Driver Loaded" );
	}
	

	public int startTransfer()
	{
		int reloadTime = new Integer( cf.getEntry( "driver.dummy.intervall" ) ).intValue();
		
		timer = new Timer();
	    timer.schedule( new Task( new Integer( cf.getEntry( "driver.dummy.highID" ) ).intValue() ),
	    				reloadTime, reloadTime );
	    
//	    canStorm.isStarted( true );
	    
		return 0; 
	}
	
	public void stopTransfer()
	{
		timer.cancel();
//		canStorm.isStarted( false );
	}
	public void sendMessage( CanMessage telegramm ){}
	public CanMessage getMessage(){ return null; }
	public int setCanSpeed( int speed ){ return 0; }
	public int getCanSpeed(){ return 0; }
	public int setFilter( int id_filter ){ return 0; }
	public int[] getFilter(){ return null; }
	public int deleteFilter( int id_filterd ){ return 0; }
	public int setSocketIP( String ip ){ return 0; }
	public String getSocketIP(){ return "0.0.0.0"; }
	public int setSocketPort( int port ){ return 0; }
	public int getSocketPort()
	{
		return 33333;
	}


	public void setMessageConsumer(CanMessageConsumer cmc)  
	{
		this.cmc = cmc;
	}
}
