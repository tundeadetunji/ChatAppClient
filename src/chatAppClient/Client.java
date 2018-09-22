package chatAppClient;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame {

	private JTextField chatMessage;
	private JTextArea messageHistory;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private String message = "";
	private String serverIPAddress;
	private Socket connection;

	public Client(String server) {
		super("Sample Chat App");
		serverIPAddress = server;
		chatMessage = new JTextField();
		chatMessage.setEditable(false);
		chatMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendMessageToPathway(event.getActionCommand());
				chatMessage.setText("");
			}
		});
		add(chatMessage, BorderLayout.SOUTH);
		messageHistory = new JTextArea();
		messageHistory.setEditable(false);
		add(new JScrollPane(messageHistory), BorderLayout.CENTER);
		setSize(400, 250);
		setVisible(true);

	}

	public void startNow() {
		try {
			standbyForCommunication();
			setupCommunicationPathway();
			performCommunication();
		} catch (EOFException eofException) {
			appendMessage("\nConnection terminated from the client");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			shutdownCommunicationPathway();
		}
	}

	private void standbyForCommunication() throws IOException {
		// serverIPAddress = (String) serverAddressField.getText();
		appendMessage("Attempting connection with server... \n");
		connection = new Socket(InetAddress.getByName(serverIPAddress), 6789);
		appendMessage("Connection established with server (" + connection.getInetAddress().getHostName() + ")");
	}

	private void setupCommunicationPathway() throws IOException {
		outputStream = new ObjectOutputStream(connection.getOutputStream());
		outputStream.flush();
		inputStream = new ObjectInputStream(connection.getInputStream());
		// app
	}

	private void performCommunication() throws IOException {
		chatMessage.setEditable(true);
		do {
			try {
				message = (String) inputStream.readObject();
				appendMessage("\n" + message);
			} catch (ClassNotFoundException classNotFoundException) {
				appendMessage("\nError in communication");
			}
		} while (!message.equals("Server - end"));

	}

	private void shutdownCommunicationPathway() {
		appendMessage("\nShutting down communication...");
		chatMessage.setEditable(false);
		try {
			outputStream.close();
			inputStream.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private void sendMessageToPathway(String message) //
	{
		try {
			outputStream.writeObject("Client - " + message);
			outputStream.flush();
			appendMessage("\nClient - " + message);
		} catch (IOException ioException) {
			messageHistory.append("\nError sending message.");
		}
	}

	private void appendMessage(final String str) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				messageHistory.append(str);
			}

		});
	}
}
