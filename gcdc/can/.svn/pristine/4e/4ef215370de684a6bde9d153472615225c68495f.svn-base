package com.gcdc.can;

import com.gcdc.can.dummy.DummyDriver;
import com.gcdc.can.socket.SocketDriver;
import com.gcdc.can.datagram.DatagramDriver;
//import com.gcdc.can.uart.UartDriver;
import com.gcdc.can.ConfigFile;


/**
 * @author flo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DriverManager
{
	public final int CAN_ERROR				= -1;
	public final int CAN_SUCCESSFULL 		= 0;
	public final int CAN_TRANSFER_SUCCESS	= 1;
	public final int CAN_TRANSFER_ERROR		= 2;
	public final int CAN_NOT_SUPPORTED		= 3;


	private ConfigFile cf;
	private Driver driver;

	private boolean loaded = false;

	private boolean DEBUG;

	public DriverManager( ConfigFile cf)//, TraceStorm canStorm )
	{
		this.cf = cf;
		DEBUG = new Boolean( cf.getEntry( "driver.debug" ) ).booleanValue();
		loadDriver( getDriverName() );
	}


	public DriverManager( String driver_name, String host, int port, boolean debug )
	{
		cf = null;

		DEBUG = debug;
		loaded = true;
		if(driver_name.toLowerCase().startsWith("datagram")  == true)
		{
			driver = new DatagramDriver( DEBUG );
		}
		else
		{
			driver = new SocketDriver(  DEBUG );
		}	
		driver.setSocketIP(host);
		driver.setSocketPort(port);
	}


	private void loadDriver( String DriverName )
	{
		loaded = true;
		if( DriverName.equals( "can_socket" ) )
		{
			driver = new SocketDriver(  DEBUG );
			driver.setSocketIP(cf.getEntry("driver.socket.ip"));
			driver.setSocketPort(Integer.parseInt(cf.getEntry("driver.socket.port")));
		}
		else if( DriverName.equals( "can_dummy" ) )
			driver = new DummyDriver( cf, DEBUG );
//		else if( DriverName.equals( "can_uart" ) )
//			driver = new UartDriver( cf, DEBUG );
		else
			System.out.println( "Driver not found" );
	}

	public void unloadDriver()
	{
		if( !loaded )
			return;

		driver = null;
		System.gc();
	}

	public void reloadDriver()
	{
		if( !loaded )
			return;

		unloadDriver();
		loadDriver( getDriverName() );
	}

	public Driver getDriver()
	{
		return driver;
	}


	String getDriverName()
	{
		return cf.getEntry( "driver.name" );
	}

//	public boolean isLoaded()
//	{
//		return loaded;
//	}
}
