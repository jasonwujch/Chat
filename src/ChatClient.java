import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ChatClient extends Frame {

	TextField tfTxt = new TextField();
	TextArea taContent = new TextArea();
	Socket s = null;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	boolean connected = false;
	Thread temp =null;
	

	public static void main(String[] args) {

		new ChatClient().launchFrame();
		
	}

	public void launchFrame() {

		setLocation(400, 300);
		this.setSize(300, 300);
		setTitle("Dasinong Chat Room");
		add(tfTxt, BorderLayout.SOUTH);
		add(taContent, BorderLayout.NORTH);
		pack();
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				disconnect();
				System.exit(0);
			}
			
		});
		tfTxt.addActionListener(new TFListener());
		setVisible(true);

		connect();
	}
	
	private class TFListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			String str = tfTxt.getText().trim();
//			taContent.setText(str);
			tfTxt.setText("");
			
			//Send msg to Server
			
			try {
				dos.writeUTF(str);
				dos.flush();
//				dos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		
	} 
	
	public void connect() {
		try {
			s = new Socket("localhost",12345);
			connected = true;
			taContent.setText("Connected to Dasinong Chatroom!");
			Server server = new Server();
			temp = new Thread(server);
			temp.start();
			dos = new DataOutputStream(s.getOutputStream());
			
System.out.println("connected!");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			connected = false;
			dos.close();
			dis.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	class Server implements Runnable{
				
		public void run() {
			try {
				while (connected) {
					dis = new DataInputStream(s.getInputStream());
					String str = dis.readUTF();
					if (taContent.getText().equals("")) {
						taContent.setText(str);
					}
					else {
						taContent.setText(taContent.getText()+"\n"+str); 
					}
				}
				
			} catch (SocketException e){
				System.out.println("Exited!");
//				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				try {
					if (dis!=null) dis.close();
					if (dos!=null) dos.close();
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
