package com.gcdc.canopen;

import com.gcdc.can.CanMessage;

/**
 * this interface provides a mechanism to let the canopen engine
 * send notification events to the user
 */
public interface CanOpenListener
{
	/**
	 * called when an object dictionary entry has changed
	 * SubEntry is the entry that has changed
	 */
	public void onObjDictChange(SubEntry se);

	/**
	* called when a message has been processed
	*/
	public void onMessage( CanMessage canMessage);

	/*
	* called when a state change occurs, or other event requiring notification
	* CanOpen is the instance that had an event
	* state is the new state the interface is in, or some other identifier TBD
	*/
	public void onEvent(CanOpen co, int state );
}
