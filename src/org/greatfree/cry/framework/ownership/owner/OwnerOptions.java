package org.greatfree.cry.framework.ownership.owner;

/**
 * 
 * @author libing
 * 
 * 03/19/2022
 *
 */
class OwnerOptions
{
	public final static int NO_OPTION = -1;

	public final static int REQUEST_OWNERSHIP = 1;

	public final static int WRITE = 2;
	public final static int WRITE_SYMMETRICALLY = 3;
	public final static int WRITE_ASYMMETRICALLY = 4;
	public final static int WRITE_BY_SIGNATURE = 5;
	public final static int WRITE_PRIVATELY = 6;
	
	public final static int READ = 7;
	public final static int READ_SYMMETRICALLY = 8;
	public final static int READ_ASYMMETRICALLY = 9;
	public final static int READ_BY_SIGNATURE = 10;
	public final static int READ_PRIVATELY = 11;
	public final static int STOP_MACHINE_PRIVATELY = 12;
	public final static int STOP_MACHINE_PUBLICLY = 13;

	public final static int QUIT = 0;
}
