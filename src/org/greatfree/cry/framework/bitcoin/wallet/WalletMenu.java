package org.greatfree.cry.framework.bitcoin.wallet;

/**
 * 
 * @author Bing Li
 * 
 * 02/06/2022
 *
 */
class WalletMenu
{
	public final static String TAB = "	";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	
	public final static String REQUEST_OWNERSHIP = TAB + "1) Request Ownership;";
	public final static String CHECK_BALANCE = TAB + "2) Check Balance;";
	public final static String START_COIN_MINING = TAB + "3) Start Coin Mining;";
	public final static String STOP_COIN_MINING = TAB + "4) Stop Coin Mining;";

	public final static String QUIT = TAB + "0) Quit.";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	public final static String WRONG_OPTION = "Wrong option!";
}
