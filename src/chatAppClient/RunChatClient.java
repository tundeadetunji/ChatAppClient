package chatAppClient;

import javax.swing.JFrame;

public class RunChatClient {

	public static void main(String[] args) {
		// Client ClientWindow = new Client("127.0.0.1");
		Client ClientWindow = new Client("127.0.0.1");
		ClientWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ClientWindow.startNow();
	}

}
