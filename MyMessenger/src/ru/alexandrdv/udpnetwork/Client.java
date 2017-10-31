package ru.alexandrdv.udpnetwork;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;

import ru.alexandrdv.messenger.Packet.SignPacket;
import ru.alexandrdv.udpnetwork.Encryptor;
import ru.alexandrdv.udpnetwork.Encryptor.EncryptionType;
import ru.alexandrdv.udpnetwork.UDPClient.Packet;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JCheckBoxMenuItem;

public class Client
{
	private MessageClient client;
	private JPanel rightPanel;
	private ButtonX contactsTab;
	private ButtonX moreTab;
	private JPanel morePanel;
	private JPanel contactsPanel;
	private JPanel leftPanel;
	private JPanel bottomPanel;
	private JPanel middlePanel;
	private JButton btnNewButton;
	private JRadioButton rdbtnNewRadioButton;
	private JMenuBar menuBar;
	private JMenu mnWindow;
	private JMenu mnAccounts;
	private JMenu mnOther;
	private JMenu mnHelp;
	private JMenu mnForDevelopers;
	private JFrame f;

	public void resize()
	{
		System.out.println("Resizing...");
		menuBar.setSize(f.getContentPane().getSize().width, menuBar.getHeight());
		middlePanel.setSize(f.getContentPane().getSize().width, f.getContentPane().getSize().height - (menuBar.getHeight() + 1) - bottomPanel.getHeight());
		leftPanel.setSize(leftPanel.getWidth(), middlePanel.getHeight() - 2);
		rightPanel.setSize(middlePanel.getWidth() - leftPanel.getWidth() - 2, middlePanel.getHeight() - 2);
		bottomPanel.setLocation(bottomPanel.getX(), f.getContentPane().getSize().height - bottomPanel.getHeight());

		mnForDevelopers.setEnabled(hasDeveloperPermissions);
		mnForDevelopers.setVisible(hasDeveloperPermissions);
	}

	public Client()
	{

		f = new JFrame();
		f.setSize(500, 500);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(3);
		f.getContentPane().setLayout(null);
		f.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				resize();
			}
		});

		menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 484, 21);
		f.getContentPane().add(menuBar);

		mnWindow = new JMenu(" Window ");
		menuBar.add(mnWindow);

		JCheckBoxMenuItem chckbxmntmResizable = new JCheckBoxMenuItem("Resizable");
		chckbxmntmResizable.setSelected(true);
		chckbxmntmResizable.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				f.setResizable(chckbxmntmResizable.isSelected());
			}
		});
		mnWindow.add(chckbxmntmResizable);

		mnAccounts = new JMenu(" Accounts ");
		menuBar.add(mnAccounts);

		JMenuItem mntmSignIn = new JMenuItem("Sign In");
		mntmSignIn.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				openSignInWindow(false);

			}
		});
		mnAccounts.add(mntmSignIn);

		JMenuItem mntmSignUp = new JMenuItem("Sign Up");
		mntmSignUp.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (!signedIn)
					openSignInWindow(true);
				else openProfileWindow();
			}
		});
		mnAccounts.add(mntmSignUp);

		mnOther = new JMenu(" Other ");
		menuBar.add(mnOther);

		mnHelp = new JMenu(" Help ");
		menuBar.add(mnHelp);

		JMenuItem mntmgmailcom = new JMenuItem("@gmail.com");
		mnHelp.add(mntmgmailcom);

		mnForDevelopers = new JMenu(" For Developers ");
		mnForDevelopers.setVisible(false);
		mnForDevelopers.setEnabled(false);
		menuBar.add(mnForDevelopers);

		JMenuItem mntmSendPacket = new JMenuItem("Send Packet");
		mntmSendPacket.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				openPacketSendWindow();
			}
		});
		mnForDevelopers.add(mntmSendPacket);

		bottomPanel = new JPanel();
		bottomPanel.setBounds(0, 434, 484, 31);
		f.getContentPane().add(bottomPanel);
		bottomPanel.setLayout(null);

		middlePanel = new JPanel();
		middlePanel.setBorder(UIManager.getBorder("Tree.editorBorder"));
		middlePanel.setBounds(0, 22, 484, 412);
		f.getContentPane().add(middlePanel);
		middlePanel.setLayout(null);

		leftPanel = new JPanel();
		leftPanel.setBounds(1, 1, 144, 410);
		middlePanel.add(leftPanel);
		leftPanel.setLayout(null);

		ActionListener tabsListener = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				contactsTab.setClicked(morePanel.isVisible());
				contactsPanel.setBackground(contactsTab.getClickedColor());
				contactsPanel.setVisible(morePanel.isVisible());
				moreTab.setClicked(!morePanel.isVisible());
				morePanel.setBackground(moreTab.getClickedColor());
				morePanel.setVisible(!morePanel.isVisible());
			}
		};

		moreTab = new ButtonX("...", false, new boolean[] { true, true, true, true });
		moreTab.setTextColor(Color.BLACK);
		moreTab.setFont(new Font("Ms Comic Sans", 0, 20));
		moreTab.setBounds(0, 0, 57, 29);
		moreTab.addActionListener(tabsListener);
		leftPanel.add(moreTab);

		morePanel = new JPanel();
		morePanel.setBounds(0, 26, 144, 385);
		leftPanel.add(morePanel);
		morePanel.setLayout(null);

		rdbtnNewRadioButton = new JRadioButton("New radio button");
		rdbtnNewRadioButton.setBounds(6, 7, 109, 23);
		morePanel.add(rdbtnNewRadioButton);

		contactsTab = new ButtonX("Contacts", false, new boolean[] { true, true, true, true });
		contactsTab.setTextColor(Color.BLACK);
		contactsTab.setFont(new Font("Ms Comic Sans", 0, 18));
		contactsTab.setBounds(59, 0, 85, 29);
		contactsTab.addActionListener(tabsListener);
		leftPanel.add(contactsTab);

		contactsPanel = new JPanel();
		contactsPanel.setBounds(0, 26, 144, 385);
		leftPanel.add(contactsPanel);

		tabsListener.actionPerformed(new ActionEvent("", 0, ""));

		btnNewButton = new JButton("New button");
		btnNewButton.setBounds(143, 60, 341, 403);
		contactsPanel.add(btnNewButton);

		rightPanel = new JPanel();
		rightPanel.setBounds(143, 1, 341, 410);
		middlePanel.add(rightPanel);
		rightPanel.setLayout(null);
		f.setVisible(true);
		client = new MessageClient(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Packet p = ((Packet) e.getSource());
				switch (p.args.get("type"))
				{
					case "msg":
						addMsg("", "'" + p.ip.getHostAddress() + ":" + p.port + "'" + ": " + p.args.get("msg"));
						break;
					case "info":
						addMsg("", "You: " + client.msgs.remove(p.args.get("id")));
						break;
					case "joinPack":

						Timer t = new Timer(0, new ActionListener()
						{

							@Override
							public void actionPerformed(ActionEvent e)
							{
								System.out.println(client.udp.clientAddress.getHostAddress() + ":" + client.udp.port);
							}
						});
						t.start();
						t.setRepeats(false);

						break;
					case "dev":
						hasDeveloperPermissions = true;
						break;
					case "signup":
						if (p.args.get("msg").equals("accountAlreadyExists"))
							JOptionPane.showMessageDialog(null, "Account already exists!");
						if (p.args.get("msg").equals("successfullySignedUp"))
							JOptionPane.showMessageDialog(null, "Successfully signed up!");
						break;
					case "signin":
						if (p.args.get("msg").equals("accountNotExists"))
							JOptionPane.showMessageDialog(null, "Account not exists!");
						if (p.args.get("msg").equals("wrongPassword"))
							JOptionPane.showMessageDialog(null, "Wrong password!");
						if (p.args.get("msg").equals("successfullySignedIn"))
							JOptionPane.showMessageDialog(null, "Successfully signed in!");
						break;
				}

			}
		});
	}

	boolean signedIn = false;
	private String password;
	private String login;

	public void openProfileWindow()
	{
		Color color = new Color(220, 220, 230);
		JOptionPane pane = new JOptionPane(null, -1, 0, null, new Object[0], null);
		pane.removeAll();
		pane.setBackground(color);
		pane.setLayout(new GridLayout(5, 1, 0, 0));

		JPanel login = new JPanel();
		login.setBackground(color);
		pane.add(login);
		login.setLayout(new GridLayout(0, 2, 0, 0));

		JLabel loginLabel = new JLabel("Login:");
		loginLabel.setBackground(color);
		loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
		login.add(loginLabel);

		JTextField loginField = new JTextField(this.login);
		loginField.setEditable(false);
		login.add(loginField);

		pane.add(new JLabel(""));

		JPanel password = new JPanel();
		password.setBackground(color);
		pane.add(password);
		password.setLayout(new GridLayout(0, 2, 0, 0));

		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setBackground(color);
		passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
		password.add(passwordLabel);

		String pass = "";
		for (int i = 0; i < this.password.length(); i++)
			pass += "*";
		JPasswordField passwordField = new JPasswordField(pass);
		passwordField.setEditable(false);
		password.add(passwordField);

		d = pane.createDialog("Account's Info");
		d.setLocationRelativeTo(null);
		d.setSize(240, 200);
		d.setVisible(true);
	}

	public void openPacketSendWindow()
	{
		Color color = new Color(220, 220, 230);
		JOptionPane pane = new JOptionPane(null, -1, 0, null, new Object[0], null);
		pane.removeAll();
		pane.setBackground(color);
		pane.setLayout(new GridLayout(5, 1, 0, 0));

		JLabel title2 = new JLabel("Fill fields:");
		title2.setHorizontalAlignment(SwingConstants.CENTER);
		pane.add(title2);

		JPanel login = new JPanel();
		login.setBackground(color);
		pane.add(login);
		login.setLayout(new GridLayout(0, 2, 0, 0));

		JLabel loginLabel = new JLabel("Type:");
		loginLabel.setBackground(color);
		loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
		login.add(loginLabel);

		JTextField type = new JTextField();
		login.add(type);

		JPanel password = new JPanel();
		password.setBackground(color);
		pane.add(password);
		password.setLayout(new GridLayout(0, 2, 0, 0));

		JLabel passwordLabel = new JLabel("Reciever:");
		passwordLabel.setBackground(color);
		passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
		password.add(passwordLabel);

		JTextField reciever = new JTextField();
		password.add(reciever);

		JPanel passwordRepeat = new JPanel();
		passwordRepeat.setBackground(color);
		passwordRepeat.setLayout(new GridLayout(0, 2, 0, 0));

		JLabel passwordRepeatLabel = new JLabel("Message:");
		passwordRepeatLabel.setBackground(color);
		passwordRepeatLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JTextField msg = new JTextField();
		pane.add(passwordRepeat);
		passwordRepeat.add(passwordRepeatLabel);
		passwordRepeat.add(msg);

		JButton okBtn = new JButton("Ok");

		okBtn.setBackground(Color.WHITE);
		pane.add(okBtn);
		okBtn.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{

					client.send(type.getText(), msg.getText(), reciever.getText(), UUID.randomUUID().toString());

					System.out.println("dasd");
					d.setVisible(false);
					d = null;

				}
				catch (Exception exc)
				{
					exc.printStackTrace();
				}

			}
		});
		d = pane.createDialog("Accounts");
		d.setLocationRelativeTo(f);
		d.setSize(240, 160);
		d.setVisible(true);
	}

	public void openSignInWindow(boolean signUp)
	{
		Color color = new Color(220, 220, 230);
		JOptionPane pane = new JOptionPane(null, -1, 0, null, new Object[0], null);
		pane.removeAll();
		pane.setBackground(color);
		pane.setLayout(new GridLayout(signUp ? 7 : 6, 1, 0, 0));

		JLabel title2 = new JLabel(signUp ? "Signing up:" : "Signing in:");
		title2.setHorizontalAlignment(SwingConstants.CENTER);
		pane.add(title2);

		JPanel login = new JPanel();
		login.setBackground(color);
		pane.add(login);
		login.setLayout(new GridLayout(0, 2, 0, 0));

		JLabel loginLabel = new JLabel("Login:");
		loginLabel.setBackground(color);
		loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
		login.add(loginLabel);

		JTextField loginField = new JTextField();
		login.add(loginField);

		pane.add(new JLabel(""));

		JPanel password = new JPanel();
		password.setBackground(color);
		pane.add(password);
		password.setLayout(new GridLayout(0, 2, 0, 0));

		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setBackground(color);
		passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
		password.add(passwordLabel);

		JPasswordField passwordField = new JPasswordField();
		password.add(passwordField);

		JPanel passwordRepeat = new JPanel();
		passwordRepeat.setBackground(color);
		passwordRepeat.setLayout(new GridLayout(0, 2, 0, 0));

		JLabel passwordRepeatLabel = new JLabel("Password Repeat:");
		passwordRepeatLabel.setBackground(color);
		passwordRepeatLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JPasswordField passwordRepeatField = new JPasswordField();
		if (signUp)
		{
			pane.add(passwordRepeat);
			passwordRepeat.add(passwordRepeatLabel);
			passwordRepeat.add(passwordRepeatField);
		}

		pane.add(new JLabel(""));

		JButton okBtn = new JButton("Ok");
		okBtn.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (new String(passwordField.getPassword()).equals(new String(passwordRepeatField.getPassword())) || !signUp)
					{
						lastLogin = loginField.getText();
						lastPassword = new String(passwordField.getPassword());
						client.send("sign" + (signUp ? "up" : "in"), loginField.getText() + "\n" + new String(passwordField.getPassword()), "server", "");
						d.setVisible(false);
						d = null;
					}
					else JOptionPane.showMessageDialog(null, "Password repeat is wrong!");

				}
				catch (Exception exc)
				{
					exc.printStackTrace();
				}

			}
		});

		okBtn.setBackground(Color.WHITE);
		pane.add(okBtn);

		d = pane.createDialog("Accounts");
		d.setLocationRelativeTo(null);
		d.setSize(240, 200);
		d.setVisible(true);
	}

	private String lastLogin;
	private String lastPassword;

	JDialog d;
	boolean hasDeveloperPermissions = false;

	public void addMsg(String user, String msg)
	{
		System.out.println(user + ":" + msg);
	}

	public static void main(String[] args)
	{
		new Client();
	}

	/**
	 * 
	 * @author AlexandrDV
	 *
	 */
	static class ButtonX extends ru.alexandrdv.components.ButtonX
	{
		private static final long serialVersionUID = -1371893745622504892L;

		public ButtonX(String text, boolean hasFrame, boolean[] rect)
		{
			super(text, hasFrame, rect);
			// TODO Auto-generated constructor stub
		}

	}
}
