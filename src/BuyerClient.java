import java.rmi.RemoteException;
import java.rmi.Naming;
import java.util.Scanner;
import java.rmi.NotBoundException;
import java.util.HashMap;

/**
 * The class representing a single buyer client in the auctioning system.
 * It uses java RMI and stub provided by the Server.
 * The stub is located in a parent class - Client.
 *
 * The functionality allows to create a client, which is able to:
 *
 * 1) Bid an auction by given ID and then enter the price.
 * 2) Display all of the available auctions.*
 *
 */

public class BuyerClient extends Client{

	/**
	 * Creates a BuyerClient instance.
	 * The constructor is inherited from the Client classs.
	 */

	public BuyerClient(){
		
		super();
	}

	/**
	 * Allows the user to bid.	 *
	 * Ask user for ID of the auction he/she would like to bid and also his name/email.
	 *
	 * @throws RemoteException
	 */
	
	public void bid() throws RemoteException{
		
		int id = input.integerValidation("Please enter an ID of the auction you would like to bid: ");		
		int bidValue = input.integerValidation("Please enter a value you would like to bid: ");				
		String name = input.basicString("Please enter your name: ");				
		String email = input.basicString("Please enter your email: ");			
		
		int notificationFlag = stub.bidAuction(id, bidValue, email, name);		
		
		if (notificationFlag == 0) System.out.println("The auction ID is incorrect - the auction has never existed. ");
		else if (notificationFlag == 1) System.out.println("You're late! The auction has expiried. "); 
		else if (notificationFlag == 2) System.out.println("The proposed price is too low, please check the current price. ");
		else System.out.println("You successfully bid auction " + id + ". ");	        
	}

	/**
	 * Allows user to display all of the available auctions
	 *
	 * @throws RemoteException
	 */
	
	public void showAuctions() throws RemoteException{

		//The passed hashmap does not recognize its parametres types so the wildcard is used and then the objects are casted.
		
		HashMap<?, ?> display = stub.browseAuctions();

		System.out.println("ok");

		//Check if hashmap is empty
		
		if(display.isEmpty()){

			System.out.println("There are no available auctions to display.");
			return;
		}
		
		System.out.println("-----   id   -----    price    -----");

		//Iterate over the map to display
		
		for (HashMap.Entry<?, ?> entry : display.entrySet()) {
			
			Integer id = (Integer) entry.getKey();
			Integer price = (Integer) entry.getValue();
			
			System.out.println("-----    " + id + "   -----    " + price + "    -----");
		}
	}
}
