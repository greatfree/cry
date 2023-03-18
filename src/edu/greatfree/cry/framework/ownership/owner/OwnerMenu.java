package edu.greatfree.cry.framework.ownership.owner;

/**
 * 
 * @author libing
 * 
 * 03/19/2022
 *
 */
class OwnerMenu
{
	public final static String TAB = "	";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";

	public final static String REQUEST_OWNERSHIP = TAB + "1) Request Ownership;";
	public final static String ABANDON_OWNERSHIP = TAB + "2) Abandon Ownership;";

	public final static String WRITE = TAB + "3) Write;";
	public final static String WRITE_SYMMETRICALLY = TAB + "4) Write Symmetrically;";
	public final static String WRITE_ASYMMETRICALLY = TAB + "5) Write Asymmetrically;";
	public final static String WRITE_BY_SIGNATURE = TAB + "6) Write By Signature;";
	public final static String WRITE_PRIVATELY = TAB + "7) Write Privately;";

	public final static String READ = TAB + "8) Read;";
	public final static String READ_SYMMETRICALLY = TAB + "9) Read Symmetrically;";
	public final static String READ_ASYMMETRICALLY = TAB + "10) Read Asymmetrically;";
	public final static String READ_BY_SIGNATURE = TAB + "11) Read By Signature;";
	public final static String READ_PRIVATELY = TAB + "12) Read Privately;";
	public final static String STOP_MACHINE_PRIVATELY = TAB + "13) Stop Machine Privately;";
	public final static String STOP_MACHINE_PUBLICLY = TAB + "14) Stop Machine Publicly;";

	public final static String QUIT = TAB + "0) Quit.";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	public final static String WRONG_OPTION = "Wrong option!";
}
