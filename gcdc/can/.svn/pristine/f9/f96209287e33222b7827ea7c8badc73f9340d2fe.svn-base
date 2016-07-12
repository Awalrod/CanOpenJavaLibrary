package com.gcdc.can;

/**
 * This is the Interface for the CAN-Bus Driver.<p>
 * Now with this interfaces you can easy implement your own Driver for the
 * CAN - Bus.
 * @author Florian Kristen
 *
 */
public interface Driver
{
	/**
	 * Start the Trace Transfer Now
	 */
	public int startTransfer() throws Exception;

	/**
	 * Stop the Trace Transfer NOW
	 */
	public void stopTransfer();


	/**
	 * Send a CAN Telegramm to the Bus.<br>
	 * <li>11 Bit ID</li>
	 * <li>29 Bit ID</li>
	 * <li>8 Byte CAN Data</li>
	 * @param telegramm
	 */
	public void sendMessage( CanMessage telegramm ) throws java.io.IOException;

	/**
	 * Return the aktuell CAN TElegram that the Driver get.
	 * @return
	 */
	public CanMessage getMessage();

	// interface that processes incoming messages
	public void setMessageConsumer(CanMessageConsumer cmc);

	/**
	 * Set a ID-Filter on the Driver.<br>
	 * To ways a Supportet:
	 * <li>Hardware Filter (driver depence)</li>
	 * <li>Software Filter</li>
	 * If the Driver Support Hardwarefiltering - Disabel Software Filtering.
	 * @return Status
	 */
	public int setFilter( int id_filter );

	/**
	 * Return a list for Filtert ID's.
	 * @return
	 */
	public int[] getFilter();

	/**
	 * Delete a already Set Filter.
	 * @param id_filterd
	 * @return Status
	 */
	public int deleteFilter( int id_filterd );


	/**
	 * Set the CAN Speed on the Driver
	 * @param speed
	 * @return
	 */
	public int setCanSpeed( int speed );

	/**
	 * Return the CAN Speed that the Driver run now
	 * @return
	 */
	public int getCanSpeed();


	/**
	 * Set the Socket IP-Address for the Server that i will Connect to.
	 * @param ip
	 * @return
	 */
	public int setSocketIP( String ip );

	/**
	 * Return the Aktuell Server Socket IP-Addresse
	 * @return
	 */
	public String getSocketIP();


	/**
	 * Set the Socket Port to connect to the Driver Server.
	 * @param port
	 * @return
	 */
	public int setSocketPort( int port );

	/**
	 * Return the akktuall Socket Port.
	 * @return
	 */
	public int getSocketPort();
}
