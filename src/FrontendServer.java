import java.io.DataInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jgroups.*;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.blocks.cs.Receiver;
import org.jgroups.util.RspList;
import org.jgroups.util.Util;


class FrontendServer extends UnicastRemoteObject implements RemoteInterface, Receiver{

	// a service name used in a lookup
	private static final String SERVICE_NAME = "55";

	//variables for JGroups
	private static final String CLUSTER = "Cluster";
	private static final int TIMEOUT = 1000;
	private JChannel channel;
	private RpcDispatcher disp;
	private RequestOptions req;

	//variables used for user authentication
	private KeyGen keyGen;
	private PrivateKey privKey;
	private PublicKey pubKey;
	private InputValidator validator;
	private HashMap<String, String> challenges = new HashMap<>();

	private List<Object> states;
		
	public FrontendServer() throws RemoteException{

		try {

			//Instantiate an object of the implementation class
			Registry registry = LocateRegistry.createRegistry(8888);
			registry.bind(SERVICE_NAME, this);
			System.out.println("RMI connection ok");


			this.states = new LinkedList<Object>();

		} catch(RemoteException | AlreadyBoundException re){}

		try{

			this.channel = new JChannel();
			this.req = new RequestOptions(ResponseMode.GET_ALL, 5000);
			this.disp = new RpcDispatcher(this.channel, this);
			channel.setDiscardOwnMessages(true);
			channel.connect(CLUSTER);

			this.keyGen = new KeyGen();
			validator = new InputValidator();
			privKey = validator.readPrivKey("\\\\lancs\\homes\\42\\radecki\\My Desktop\\x\\src\\myKeyprivate.key");

			System.out.println("Connected. ");

		} catch(Exception e){}
	}

	public synchronized byte[] verifyConnection(String nonce) throws Exception, InvalidKeyException {

		byte[] signed = keyGen.sign(privKey, nonce);

		return 	signed;
	}

	public synchronized String sendChallenge(String userName){

		String nonce = validator.generateSafeToken();
		challenges.put(userName, nonce);

		return nonce;
	}

	public boolean verifyAuthent(byte[] toBeChecked, String userName) throws Exception{

		String nonce = challenges.get(userName);

		PublicKey userPubKey = validator.readKey("\\\\lancs\\homes\\42\\radecki\\My Desktop\\x\\src\\" + userName + "public.key");
		return keyGen.verifySign(nonce, toBeChecked, userPubKey);
	}

	public int bidAuction(int id, int newPrice, String email, String name) throws RemoteException{

		try{

			RspList<Integer> rsps =  disp.callRemoteMethods(null, "bidAuction", new Object[] {id, newPrice, email, name },
					new Class[] {int.class, int.class, String.class, String.class}, this.req);

			return rsps.getFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public HashMap<Integer, Integer> browseAuctions(){

		try {

			RspList<HashMap<Integer, Integer>> rsps = disp.callRemoteMethods(null, "browseAuctions", new Object[]{},
					new Class[]{}, this.req);

			return rsps.getFirst();
		} catch (Exception e) {

			e.printStackTrace();
		}

		return new HashMap<>();
	}

	public String deleteAuction(int id, String userName) throws IndexOutOfBoundsException{

		try {

			RspList<String> rsps = disp.callRemoteMethods(null, "deleteAuction", new Object[]{id, userName},
					new Class[]{int.class, String.class}, this.req);

			return rsps.getFirst();
		} catch (Exception e) {

			e.printStackTrace();
		}

		return " ";
	}

	public int createAuction(int startingPrice, String description, int minimumPrice, String userName) throws RemoteException{

		try{

			System.out.println("ok");
			RspList<Integer> rsps = disp.callRemoteMethods(null, "createAuction", new Object[]{startingPrice, description, minimumPrice, userName},
					new Class[]{int.class, String.class, int.class, String.class}, this.req);


			return rsps.getFirst();

		} catch (Exception e) {

			e.printStackTrace();
		}

		return 0;
	}

	public static void main(String args[]) throws Exception {

		FrontendServer fe = new FrontendServer();

	}

	public void viewAccepted(View new_view) {
		System.out.println("** view: " + new_view);

	}

	public void receive(Message message) {
		System.out.println(message.toString());
	}


	public void setState(InputStream inputStream) throws Exception {
		String s = (String) Util.objectFromStream(new DataInputStream(inputStream));
		System.out.println(s);
	}

	@Override
	public void receive(Address address, byte[] bytes, int i, int i1) {

	}

	@Override
	public void receive(Address address, ByteBuffer byteBuffer) {

	}
}