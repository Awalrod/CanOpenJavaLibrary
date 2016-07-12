package com.gcdc.can;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author flo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConfigFile 
{
	private Properties configFile;

	private String confFile;
	private static final String confHeader = "CAN Trace Configuration File";
	
	public ConfigFile( String confFile )
	{
		this.confFile = confFile;
		configFile = new Properties();
		
		/* holle mir die Scrreneinstellungen */
		try
		{
			configFile.load( new FileInputStream( confFile ) );
		}
		catch( IOException io )
		{
			System.err.println( io.getMessage() );
			return;
		}
	}
	
	public void reloadConfigFile()
	{
		/* holle mir die Scrreneinstellungen */
		try
		{
			configFile.load( new FileInputStream( confFile ) );
		}
		catch( IOException io )
		{
			System.out.println( io.getMessage() );
			return;
		}
		
		
	}
	
	public String getEntry( String Entry )
	{
		return configFile.getProperty( Entry );
	}
	
	public void setEntry( String Entry, String value )
	{
		configFile.setProperty( Entry, value );
	}
	
	public void saveConfig()
	{
		try
		{
			configFile.store( new FileOutputStream( confFile ), confHeader );
		}
		catch( IOException io )
		{
			System.err.println( io.getMessage() );
		}
	}
}
