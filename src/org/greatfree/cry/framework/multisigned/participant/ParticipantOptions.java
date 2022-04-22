package org.greatfree.cry.framework.multisigned.participant;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
class ParticipantOptions
{
	public final static int NO_OPTION = -1;
	
	public final static int REQUEST_OWNERSHIP = 1;

	public final static int UNI_CONSENSUS_NOTIFICATION = 2;
	public final static int ANY_CONSENSUS_NOTIFICATION = 3;
	public final static int BROAD_CONSENSUS_NOTIFICATION = 4;
	
	public final static int UNI_CONSENSUS_REQUEST = 5;
	public final static int ANY_CONSENSUS_REQUEST = 6;
	public final static int BROAD_CONSENSUS_REQUEST = 7;
	
	public final static int STOP_SERVER = 8;

	public final static int QUIT = 0;
}
