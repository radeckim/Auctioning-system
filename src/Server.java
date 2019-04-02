import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.blocks.cs.Receiver;
import org.jgroups.util.*;
import java.nio.ByteBuffer;

/**
* A class providing simple server-side implementation.
* It creates a sceleton, which can be used as a stub.
* 
* "401 unauthorized"

If you can predict what the nonce is going to be (e.g. prev nonce + 1),
then you can use the oracle to establish the correct response in advance, 
then run the protocol against the victim and have the response ready.

An example where this could be realistic is if your challenge-response protocol is cryptographically weak and can be brute-forced, 
but it takes a long time (e.g. days with the attacker's resources) to do the brute-forcing. 
An unpredictable nonce would have prevented the vulnerability from being exploitable.
*/

public class Server implements Receiver{
	
	//variables for JGroups
	private static final String CLUSTER = "TEST";
	private static final int TIMEOUT = 5000;
	protected JChannel channel;
	protected RpcDispatcher disp;
	protected RequestOptions req;
	
	/**
	* Create a server and bind a service name to the registry.
	* It can be lookup by a client now.
	*
	*/
	
    public Server() throws Exception{

		try{

			this.channel = new JChannel();
			this.req= new RequestOptions(ResponseMode.GET_ALL, TIMEOUT);
			this.disp = new RpcDispatcher(this.channel, this);

			this.channel.connect(CLUSTER);
			this.channel.setDiscardOwnMessages(true);

			System.out.println("Connected. ");

			this.setState();
			
		} catch(Exception e){
			
			System.out.println("Failed cluster connection");
		}		
    }

    public void connect() throws Exception{}

	//Hashmap stores all of the auctions, ID is an unique keyword for each auction
	protected Map<Integer, Auction> auctionsMap = new HashMap<>();

	//track auction ID
	protected int currentID = 5;

    public void setState() throws Exception{

    	Object[] up = null;

		RspList<Object[]> rsps =  disp.callRemoteMethods(null, "getState", null,
				null, this.req);
		up = rsps.getFirst();

		System.out.println("Data replicated: " + up[0] + " " + up[1]);

		if(up != null){

			auctionsMap = (Map<Integer, Auction>) up[0];
			currentID = (int) up[1];
		}
	}

	public synchronized Object[] getState(){

    	return new Object[] {auctionsMap, currentID};
	}

    public static void main(String args[]){
		
		//Create an instance of a server and then run it
        try {
			
			new Server();
			
		} catch(Exception e){
			
			System.out.println("Remote exception: " + e);
		}
    }

	/**
	 * Allows the user to bid an auction, which is possible only and only if the auction is available.
	 *
	 * @param id to be bid
	 * @param newPrice new price of an auction
	 * @param email of the user
	 * @param name of the user
	 * @return a mode which determines if the bid was successful
	 */

	public synchronized int bidAuction(int id, int newPrice, String email, String name){

		Integer atomicID = new Integer(id);
		Auction a = auctionsMap.get(atomicID);
		Integer one = new Integer(1);

		if (atomicID.compareTo(currentID) == 1 || atomicID.compareTo(one) == -1) return 0;
		else if (a == null) return 1;
		else{


			if(newPrice <= auctionsMap.get(id).getCurrentPrice()) return 2;
			else{

				auctionsMap.get(id).updateBid(newPrice, name, email);
				return 3;
			}
		}
	}

	/**
	 * Returns a hasmap which contains an ID and current price of each available auction	 *
	 *
	 * @return auctionsDisplayed
	 */

	public synchronized HashMap<Integer, Integer> browseAuctions(){

		HashMap<Integer, Integer> auctionsDisplayed = new HashMap<Integer, Integer>();

		if(auctionsMap.isEmpty()){

			return auctionsDisplayed;
		}

		for (HashMap.Entry<Integer, Auction> entry : auctionsMap.entrySet()) {

			Integer id = entry.getKey();
			Auction auction = entry.getValue();

			auctionsDisplayed.put(id, (Integer) auction.getCurrentPrice());
		}

		return auctionsDisplayed;
	}

	public synchronized String deleteAuction(int id, String userName) throws IndexOutOfBoundsException{

		Auction a = auctionsMap.get(id);
		String s = "";

		if(a != null && a.getUserName().equals(userName)){

			auctionsMap.remove(id);
			System.out.println("The " + id + " auction " + " has been removed.");

			int curr = a.getCurrentPrice();
			int min =  a.getMinimumPrice();
			int start = a.getStartingPrice();

			if(curr == start) return "The auction has been successfuly removed - noone bid it, the item has not been sold. ";

			s = "The auction has been successfuly removed - the final price was: " + curr + "\n";

			if(curr < min){

				String s1 = "The final price is lower than a minumum price: " + min;
				s1 += "The item has not been sold. ";
				s += s1;

			} else{

				String s2 = "Winner details - " + a.getCurrentBidName() + " " + a.getCurrentBidMail();
				s += s2;
			}
		} else if (a == null){

			s = "The auction cannot be removed - the ID is incorrect. ";
		} else if (!a.getUserName().equals(userName)){

			s = "You don't have permission to delete this auction. ";
		}

		return s;
	}

	/**
	 * The method creates an auction by given parameters.
	 *
	 * @param startingPrice - starting price of an auction
	 * @param description - description of an auction
	 * @param minimumPrice - minimumP price of an auction
	 * @return unique key
	 */

	public synchronized int createAuction(int startingPrice, String description, int minimumPrice, String userName){

		currentID = currentID + 1;

		auctionsMap.put(currentID, new Auction(currentID, startingPrice, description, minimumPrice, userName));
		System.out.println("The auction " + currentID + " has been created.");

		System.out.println("ok");

		return currentID;
	}

	public void viewAccepted(View new_view) {

		System.out.println("** view: " + new_view);
	}

	@Override
	public void receive(Address address, byte[] bytes, int i, int i1) {

	}

	@Override
	public void receive(Address address, ByteBuffer byteBuffer) {

	}

	/**
	 * A private class (it's used only in server repiclas) which describes each auction
	 * It holds class parameters, doesn't have any particular functionality.
	 *
	 */

	private class Auction{

		private Integer id;
		private String userName;

		private int startingPrice;
		private String description;
		private int minimumPrice;
		private int currentPrice;
		private String currentBidName;
		private String currentBidMail;

		/**
		 * Creates an auction
		 *
		 * @param id
		 * @param startingPrice
		 * @param description
		 * @param minimumPrice
		 */

		public Auction(int id, int startingPrice, String description, int minimumPrice, String userName){

			this.id = id;
			this.startingPrice = startingPrice;
			this.description = description;
			this.minimumPrice = minimumPrice;
			this.userName = userName;

			currentPrice = startingPrice;
		}

		/**
		 * Getter for a starting price
		 *
		 * @return
		 */

		public int getStartingPrice() {

			return startingPrice;
		}

		/**
		 * Getter for a description
		 *
		 * @return
		 */

		public String getDescription() {

			return description;
		}

		/**
		 * Getter for a minimum price
		 *
		 * @return
		 */

		public int getMinimumPrice() {

			return minimumPrice;
		}

		/**
		 * Getter for a current price
		 *
		 * @return
		 */

		public int getCurrentPrice(){

			return currentPrice;
		}

		/**
		 * Getter for an ID
		 *
		 * @return
		 */

		public int getId(){

			return id;
		}

		/**
		 * Getter for a current bider name
		 *
		 * @return
		 */

		public String getCurrentBidName(){

			return currentBidName;
		}

		/**
		 * Getter for a current bider email
		 *
		 * @return
		 */

		public String getCurrentBidMail(){

			return currentBidMail;
		}

		public String getUserName(){

			return userName;
		}

		/**
		 * Update a highest bid of an auction
		 *
		 * @param newPrice
		 * @param name
		 * @param mail
		 */

		public void updateBid(int newPrice, String name, String mail){

			currentPrice = newPrice;
			currentBidName = name;
			currentBidMail = mail;
		}
	}
}
