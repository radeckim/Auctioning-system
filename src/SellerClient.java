import java.rmi.RemoteException;
import java.util.LinkedList;
import java.rmi.NotBoundException;

/**
* A class which provides a functionality of a seller client in the auctioning system.
* It provides a simple functionality such as:
* 1) Delete an auction
* 2) Create an auction
*
* Each of them takes an user input and then validate it using an utility class.
* A child class of the Client class, uses its stub to establish a connection to the auctioning server.
*/

public class SellerClient extends Client{

	/**
	* Create a seller client.
	* The constructor is inherited from the client class
	*
	*/
	
	public SellerClient() {
	
		super();
	}
	
	/**
	* Delete an auction from the stub, basing on the user input.
	* The input must be an positive integer.
	*
	*/	
	
	public void deleteAuction() throws RemoteException{
		
		System.out.println(userName);	
		
		int integer = input.integerValidation("Please enter an ID of the auction you would like to close. ");	
		Integer id = new Integer (integer);		
		
		System.out.println(userName);
		
		String a = stub.deleteAuction(id, userName);		
		System.out.println(a);		
	}
	
	/**
	*  Creates an auction on the stub object.
	*  Each input must be validated:
	*  - starting price and min price must be integers
	*  - the description cannot be longer than 400 characters and cannot be null 	
	*
	*/

	public void createAuction() throws RemoteException{			
		
		int startingPrice = input.integerValidation("Please enter a starting price: ");
		int minimumPrice;	
		String description;		
        
        do{
			
			minimumPrice = input.integerValidation("Please enter a minimum acceptable price: ");
			if(startingPrice > minimumPrice) System.out.println("Please note that a minimum price should be greater than the starting price. ");  	
			
			
			//iterate until starting price input is bigger than min price
		}while(startingPrice > minimumPrice);

		do{
			
			String message = "Please provide a short description (it should not exceed 400 characters): ";			
			description = input.basicString(message);			
			
			//iterate until the length is greater than 1 and less than 400
		}while(description.length() <= 1 || description.length() >= 400); 	
		
		stub.createAuction(startingPrice, description, minimumPrice, userName);
	}	
		
}
