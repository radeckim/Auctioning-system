import java.io.*;

public class ClientRequest implements Serializable{
	
	private String userName;
	private String nonce;

	public ClientRequest(String userName, String nonce){
		
		this.userName = userName;
		this.nonce = nonce;
	}
	
	public String getUserName(){
		
		return userName;
	}
	
	public String getNonce(){
		
		return nonce;
	}
}
