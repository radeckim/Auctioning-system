/**
* A class providing a simple CONSOLE interface for the auctioning system.
*
* It allows the user to choose the mode (seller/buyer)
* and then use a methods:
* - for the seller: 
*	- create an auction
*	- delete an auction
* - for the buyer
*	- bid
*	- display
*
*/

public class UserInterface{
	
	private int mode;
	private BuyerClient clientBuy;
	private SellerClient clientSell;
	private InputValidator input;
	private int choice;
	
	/**
	*	Create an interface
	*
	*/
	
	public UserInterface(){
		
		input = new InputValidator();	
		clientSell = new SellerClient();
		clientBuy = new BuyerClient();	
	}
	
	/**
	* It allows the user to choose a mode or exit
	*
	*/
	
	public void modeChoice(){
		
		System.out.println("Please state if you are a seller or a buyer");
		System.out.println("1. I am a seller. ");
		System.out.println("2. I am a buyer. ");
		System.out.println("3. Exit");	
		choice = input.userChoiceValidator();
	}
	
	/**
	* Allows the user to choose an option after choosing a mode.
	*
	*/	

	public void play(){
		
		int nestedChoice = 0;	
			
		System.out.println(choice);	
		
		if(choice == 2){

			boolean logged = false;
			
			while(true){
				
				if(!logged){
					
					boolean log = clientBuy.login();
					logged = true;
					
					if(!log){
					
						System.out.println("Server authentication failed!");
						break;
					}
					
					System.out.println("Server authentication success!");	
				}						
			
				System.out.println("Please choose one of the following options: ");	
				System.out.println("1. Display available auctions. ");
				System.out.println("2. Bid an auction. ");
				System.out.println("3. Exit ");				
				nestedChoice = input.userChoiceValidator();
				

				try{ 
				
					if(nestedChoice == 1) clientBuy.showAuctions();				
					else if(nestedChoice == 2) clientBuy.bid();
					else if(nestedChoice == 3) break;
					
				} catch (Exception e){
					
					e.printStackTrace();			
				}
			} 			
						
		} else if (choice == 1){					
			
			boolean logged = false;
			
			while(true){
				
				if(!logged){
								
					boolean log = clientSell.login();
					logged = true;
					
					if(!log){
					
					System.out.println("Server authentication failed!");
					break;
					}
					
				System.out.println("Server authentication success!");	
				}						
				
				System.out.println("Please choose one of the following options: ");	
				System.out.println("1. Create a new auction. ");
				System.out.println("2. Close an auction. ");
				System.out.println("3. Exit");			
				nestedChoice = input.userChoiceValidator();
						
				try{ 
				
					if(nestedChoice == 1) clientSell.createAuction();				
					else if(nestedChoice == 2) clientSell.deleteAuction();
					else if(nestedChoice == 3) break;
					
				} catch (Exception e){
					
					e.printStackTrace();			
				}
			} while (nestedChoice == 1 || nestedChoice == 2);		
		}	
	}	
}