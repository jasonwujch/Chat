import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


public class ChatServer {
	
	boolean started = false;
	ServerSocket ss = null;
	List<Client> clients = new ArrayList<Client>(); 
	
	public static void main(String[] args) {
		new ChatServer().start();
	}
	
	public void start() {
		try {
			ss = new ServerSocket(12345);
			started = true;
		} catch (BindException e) {
			System.out.println("Port being used!");
			System.exit(0);
		} catch (IOException e){
			e.printStackTrace();
		}
		
		try{
			while (started) {
				Socket s = ss.accept();
				Client c = new Client(s);
				clients.add(c);
System.out.println("A client connected!");
				new Thread(c).start();
				
				}
//				dis.close();
		 
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	class Client implements Runnable{
		private Socket s;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean bConnected = false;
		
		public Client(Socket s){
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				bConnected = true;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void send(String str){
			try {
				dos.writeUTF(str);
			} catch (SocketException e) {
				
				clients.remove(this);
				System.out.println("A Client Quit!");
			
			
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		public void run() {
			Client c = null;
			try {
				while(bConnected) {
					String str = dis.readUTF();
					System.out.println(str);
					for (int i = 0; i < clients.size(); i++) {
						c = clients.get(i);
						c.send(str);
					}
				}
				
			} catch (SocketException e) {
				
					clients.remove(this);
					System.out.println("A Client Quit!");
				
				
			} catch (EOFException e){
				System.out.println("Client Closed!");
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				try {
					if (dis!=null) dis.close();
					if (dos!=null) dos.close();
					if (s !=null) {
						s.close();
						s = null;
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
			
			
		}
	}
	
}
