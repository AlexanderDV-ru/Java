package ru.alexandrdv.udpmessenger;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import ru.alexandrdv.components.EmailFormattedTextField;
import ru.alexandrdv.components.PhoneNumberFormattedTextField;
import ru.alexandrdv.udp.Encryptor;
import ru.alexandrdv.udp.Encryptor.EncryptionType;
import ru.alexandrdv.udp.MessageSystem;
import ru.alexandrdv.udp.Packet;
import ru.alexandrdv.udp.Packet.LoginPacket;
import ru.alexandrdv.udp.Sounds;
import ru.alexandrdv.udp.client.MessageClient;

public class Client
{
	public static Sounds sounds;
	@SuppressWarnings("unused")
	private static final Random random = new Random();
	private static MessageClient client;
	private static JPanel rightPanel;
	private static ButtonX contactsTab;
	private static ButtonX moreTab;
	private static JPanel morePanel;
	private static JPanel contactsPanel;
	private static JPanel leftPanel;
	private static JPanel bottomPanel;
	private static JPanel middlePanel;
	private static JRadioButton rdbtnNewRadioButton;
	private static JMenuBar menuBar;
	private static JMenu mnWindow;
	private static JMenu mnAccounts;
	private static JMenu mnSettings;
	private static JMenu mnHelp;
	private static JMenu mnForDevelopers;
	private static JFrame f;
	private static Account account = new Account(null, null, null, null, null, null, null, null, null, null);
	private static String lastLogin;
	private static String lastPassword;
	private static JDialog d;
	private static boolean hasDeveloperPermissions = false;
	private static JTextArea textField;
	private static JTextArea textArea;
	private static JTextField textField_1;
	private static JTextField textField_2;
	private static JMenuItem menuItem;
	private static JCheckBoxMenuItem mntmResizable;
	private static JMenuItem mntmSignIn;
	private static JMenuItem mntmSignUp;
	private static JMenuItem mntmAudio;
	private static JMenu mnLanguage;
	private static JMenuItem mntmgmailcom;
	private static JMenuItem mntmInfo;
	private static JMenuItem mntmSendPacket;

	public static void changeLanguage()
	{
		mnWindow.setText(MessageSystem.getStringByKey("window"));
		{
			mntmResizable.setText(MessageSystem.getStringByKey("resizable"));
		}
		mnAccounts.setText(MessageSystem.getStringByKey("accounts"));
		{
			mntmSignIn.setText(MessageSystem.getStringByKey("signIn"));
			mntmSignUp.setText(MessageSystem.getStringByKey("signUp"));
		}
		mnSettings.setText(MessageSystem.getStringByKey("settings"));
		{
			mntmAudio.setText(MessageSystem.getStringByKey("audio"));
			mnLanguage.setText(MessageSystem.getStringByKey("language"));
		}
		mnHelp.setText(MessageSystem.getStringByKey("help"));
		{
			mntmInfo.setText(MessageSystem.getStringByKey("privacyPolicy"));
		}
		mnForDevelopers.setText(MessageSystem.getStringByKey("forDevelopers"));
		{
			mntmSendPacket.setText(MessageSystem.getStringByKey("sendPacket"));
		}
	}

	public static void main(String[] args)
	{
		loadValues();
		f = new JFrame();
		f.setSize(500, 577);
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

		mnWindow = new JMenu();
		menuBar.add(mnWindow);

		mntmResizable = new JCheckBoxMenuItem(MessageSystem.getStringByKey("resizable"));
		mntmResizable.setSelected(true);
		mntmResizable.addActionListener(e -> f.setResizable(mntmResizable.isSelected()));
		mnWindow.add(mntmResizable);

		mnAccounts = new JMenu(MessageSystem.getStringByKey("accounts"));
		menuBar.add(mnAccounts);

		mntmSignIn = new JMenuItem(MessageSystem.getStringByKey("signIn"));
		mntmSignIn.addActionListener((e) ->
		{
			if (account != null && account.login != null)
				openProfileWindow();
			else openSignInWindow(false);

		});
		mnAccounts.add(mntmSignIn);

		mntmSignUp = new JMenuItem(MessageSystem.getStringByKey("signUp"));
		mntmSignUp.addActionListener((e) -> openSignInWindow(true));
		mnAccounts.add(mntmSignUp);

		mnSettings = new JMenu(" Settings ");
		menuBar.add(mnSettings);

		mntmAudio = new JMenuItem("Audio");
		mnSettings.add(mntmAudio);

		mnLanguage = new JMenu("Language");
		mnSettings.add(mnLanguage);

		ActionListener languagePickListener = (e) ->
		{
			MessageSystem.setLanguage(((JMenuItem) e.getSource()).getText().split("\n")[1]);
			changeLanguage();
		};

		JMenuItem mntmEnglish = new JMenuItem("English (\nen_uk\n)");
		mntmEnglish.addActionListener(languagePickListener);
		mnLanguage.add(mntmEnglish);

		menuItem = new JMenuItem("Русский (\nru_ru\n)");
		menuItem.addActionListener(languagePickListener);
		mnLanguage.add(menuItem);

		mnHelp = new JMenu(MessageSystem.getStringByKey("help"));
		menuBar.add(mnHelp);

		mntmgmailcom = new JMenuItem("@gmail.com");
		mnHelp.add(mntmgmailcom);

		mntmInfo = new JMenuItem("Info");
		mntmInfo.addActionListener(e -> openPrivacyPolicyWindow());
		mnHelp.add(mntmInfo);

		mnForDevelopers = new JMenu(" For Developers ");
		mnForDevelopers.setVisible(false);
		mnForDevelopers.setEnabled(false);
		menuBar.add(mnForDevelopers);

		mntmSendPacket = new JMenuItem("Send Packet");
		mntmSendPacket.addActionListener((e) -> openPacketSendWindow());
		mnForDevelopers.add(mntmSendPacket);

		bottomPanel = new JPanel();
		bottomPanel.setBounds(0, 434, 484, 105);
		f.getContentPane().add(bottomPanel);
		bottomPanel.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(293, 0, 191, 105);
		bottomPanel.add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JTextArea textArea_1 = new ChatArea();
		textArea_1.setEditable(false);
		textArea_1.setText(Smiles.getSmilesPage1());
		textArea_1.setFont(new Font("Verdana", Font.PLAIN, 18));
		panel.add(textArea_1);

		textField_2 = new JTextField();
		textField_2.addFocusListener(new FocusAdapter()
		{

			@Override
			public void focusLost(FocusEvent e)
			{
				// client.udp.send(new ImagePack(new File(textField_2.getText())));

			}
		});
		textField_2.setBounds(10, 38, 86, 20);
		bottomPanel.add(textField_2);
		textField_2.setColumns(10);

		JButton btnScreen = new JButton("Screen");
		btnScreen.addActionListener(event ->
		{
			try
			{
				f.setState(Frame.ICONIFIED);
				Image img = new Robot().createScreenCapture(new Rectangle(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit()
						.getScreenSize().height));
				f.setState(Frame.NORMAL);
				BufferedImage newBufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
				newBufferedImage.createGraphics().drawImage(img, 0, 0, null);
				sendImage(newBufferedImage, false);
			}
			catch (HeadlessException e)
			{
				e.printStackTrace();
			}
			catch (AWTException e)
			{
				e.printStackTrace();
			}
		});
		btnScreen.setBounds(106, 37, 89, 23);
		bottomPanel.add(btnScreen);

		middlePanel = new JPanel();
		middlePanel.setBorder(UIManager.getBorder("Tree.editorBorder"));
		middlePanel.setBounds(0, 22, 484, 412);
		f.getContentPane().add(middlePanel);
		middlePanel.setLayout(null);

		leftPanel = new JPanel();
		leftPanel.setBounds(1, 1, 144, 410);
		middlePanel.add(leftPanel);
		leftPanel.setLayout(null);

		ActionListener tabsListener = (e) ->
		{
			contactsTab.setClicked(morePanel.isVisible());
			contactsPanel.setBackground(contactsTab.getClickedColor());
			contactsPanel.setVisible(morePanel.isVisible());
			moreTab.setClicked(!morePanel.isVisible());
			morePanel.setBackground(moreTab.getClickedColor());
			morePanel.setVisible(!morePanel.isVisible());
		};

		moreTab = new ButtonX("...", false, new boolean[] { true, true, true, true });
		moreTab.setTextColor(Color.BLACK);
		moreTab.setFont(new Font("Ms Comic Sans", 0, 20));
		moreTab.setBounds(0, 0, 57, 30);
		moreTab.addActionListener(tabsListener);
		leftPanel.add(moreTab);

		morePanel = new JPanel();
		morePanel.setBounds(0, 30, 144, 380);
		leftPanel.add(morePanel);
		morePanel.setLayout(null);

		rdbtnNewRadioButton = new JRadioButton("New radio button");
		rdbtnNewRadioButton.setBounds(6, 7, 109, 23);
		morePanel.add(rdbtnNewRadioButton);
		mntmAudio.addActionListener((e) ->
		{
			openAudioSettingsWindow();
		});

		contactsTab = new ButtonX("Contacts", false, new boolean[] { true, true, true, true });

		contactsTab.setTextColor(new Color(0, 0, 0));
		contactsTab.setFont(new Font("Tahoma", Font.PLAIN, 16));
		contactsTab.setBounds(59, 0, 85, 30);
		contactsTab.addActionListener(tabsListener);
		leftPanel.add(contactsTab);

		contactsPanel = new JPanel();
		contactsPanel.setBounds(0, 30, 144, 380);
		leftPanel.add(contactsPanel);
		contactsPanel.setLayout(null);

		tabsListener.actionPerformed(new ActionEvent("", 0, ""));

		rightPanel = new JPanel();
		rightPanel.setBounds(145, 1, 341, 410);
		middlePanel.add(rightPanel);
		rightPanel.setLayout(null);

		textArea = new ChatArea();
		textArea.setFont(new Font("Verdana", Font.PLAIN, 16));
		textArea.setEditable(false);
		textArea.setBounds(0, 0, 338, 362);
		rightPanel.add(textArea);

		textField = new ChatArea();
		textField.setText("");
		textField.setFont(new Font("Verdana", Font.PLAIN, 18));
		textField.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		textField.setBounds(0, 367, 260, 43);
		rightPanel.add(textField);
		textField.setColumns(10);

		JButton btnSend = new JButton("Send");
		btnSend.setBackground(Color.LIGHT_GRAY);
		btnSend.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		btnSend.setBounds(262, 387, 76, 23);
		rightPanel.add(btnSend);

		textField_1 = new JTextField();
		textField_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		textField_1.setBounds(262, 367, 76, 18);
		rightPanel.add(textField_1);
		textField_1.setColumns(10);
		btnSend.addActionListener((e) ->
		{
			client.send("msg", textField.getText(), account.login, textField_1.getText());
			textField.setText("");
		});
		f.setVisible(true);
		client = new MessageClient((e) ->
		{
			Object source = e.getSource();
			if (source instanceof Packet)
			{
				Packet p = ((Packet) source);
				switch (p.args.get("type"))
				{
					case "msg":
						addMsg(p.args.get("sender"), p.args.get("msg"));
						break;
					case "dev":
						hasDeveloperPermissions = true;
						resize();
						break;
					case "signup":
						if (p.args.get("msg").equals("accountAlreadyExists"))
							JOptionPane.showMessageDialog(null, "Account already exists!");
						if (p.args.get("msg").equals("successfullySignedUp"))
							JOptionPane.showMessageDialog(null, "Successfully signed up!");

						break;
					case "update":
						client.send("update", null, account.login, null);
						client.send("onlines", null, account.login, null);
						break;
					case "onlines":
						contactsPanel.removeAll();
						int posY = 1;
						for (String account : p.args.get("msg").split("\n"))
						{
							ButtonX button = new ButtonX(new Color(240, 240, 255), new Color(190, 190, 205), new Color(140, 140, 155), new Color(240, 240, 255),
									new Color(190, 190, 205), new Color(140, 140, 155), account, true, new boolean[] { true, true, true, true });
							button.setFont(new Font("Verdana", 0, 12));
							button.setTextColor(Color.BLACK);
							button.setLocation(0, posY);
							button.setSize(contactsPanel.getWidth(), 29);
							button.addActionListener((ev) ->
							{
								textField_1.setText(account);
							});
							posY += 30;
							contactsPanel.add(button);
						}
						break;
					case "signin":
						if (p.args.get("msg").equals("accountNotExists"))
							JOptionPane.showMessageDialog(null, "Account not exists!");
						if (p.args.get("msg").equals("wrongPassword"))
							JOptionPane.showMessageDialog(null, "Wrong password!");
						if (p.args.get("msg").equals("successfullySignedIn"))
							JOptionPane.showMessageDialog(null, "Successfully signed in!");
						if (p.args.get("msg").equals("youAlreadySignedIn"))
							JOptionPane.showMessageDialog(null, "You are already signed in!");
						if (p.args.get("msg").equals("successfullySignedIn") || p.args.get("msg").equals("youAlreadySignedIn"))
						{
							account.login = lastLogin;
							account.password = lastPassword;
							mntmSignIn.setText("Profile");
						}
						lastPassword = "";
						lastLogin = "";
						break;
					case "changeaccountinfo":
						if (p.args.get("msg").equals("accountNotExists"))
							JOptionPane.showMessageDialog(null, "Account not exists!");
						if (p.args.get("msg").equals("wrongPassword"))
							JOptionPane.showMessageDialog(null, "Wrong password!");
						if (p.args.get("msg").startsWith("accountInfo"))
						{
							account = new Account(p.args.get("login"), p.args.get("password"), p.args.get("name"), p.args.get("surname"), p.args.get(
									"secondname"), p.args.get("gender"), p.args.get("age"), p.args.get("state"), p.args.get("phone"), p.args.get("email"));
							if (d != null)
							{
								d.setVisible(false);
								d = null;
								openProfileWindow();
							}
							if (p.args.get("msg").contains("Changed"))
								JOptionPane.showMessageDialog(null, "Account info changed!");
						}
						if (p.args.get("msg").equals("passwordChanged"))
						{
							mntmSignIn.setText("Sign In");
							account = new Account(null, null, null, null, null, null, null, null, null, null);
							d.setVisible(false);
							d = null;
							JOptionPane.showMessageDialog(null, "Password changed!");

						}

						break;
				}
			}
			else if (source instanceof ImagePack)
			{
				ImagePack imagePack = (ImagePack) source;
				try
				{
					if (imagePack.initial)
					{
						client.lastImgBytes = new int[imagePack.w * imagePack.h];
						client.lastImgIndex = 0;
					}
					for (int i = 0; i < imagePack.pos; i++)
						client.lastImgBytes[i + imagePack.index - imagePack.pos] = imagePack.img[i];
					if (imagePack.ending)
					{
						BufferedImage img = new BufferedImage(imagePack.w, imagePack.h, BufferedImage.TYPE_INT_ARGB);
						Thread[] threads = new Thread[10];
						for (int p = 0; p < threads.length; p++)
						{
							int j = p;
							threads[p] = new Thread(() ->
							{
								for (int i = j; i < client.lastImgBytes.length; i += threads.length)
									img.setRGB(i / imagePack.h, i % imagePack.h, client.lastImgBytes[i]);
							});
							threads[p].start();
							threads[p].join();
						}
						JOptionPane.showMessageDialog(null, new ImageIcon(img));
						System.out.println("Recieve ending");
					}
					client.lastImgIndex += imagePack.img.length;
				}
				catch (Exception exc)
				{
					exc.printStackTrace();
				}
			}

		});
		client.otherSoundClient = Integer.parseInt(JOptionPane.showInputDialog("Your port: " + client.udp.port + ", enter other port: "));
		client.sounds.listen(client);
		f.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent arg0)
			{
				saveValues();
			}
		});
		changeLanguage();
	}

	public static void sendImage(BufferedImage img, boolean thread)
	{
		if (img == null)
			return;
		if (img.getWidth() > 3000 || img.getHeight() > 1500)
			JOptionPane.showMessageDialog(null, "Image can't be bigger then 3000x1500");
		if (!thread)
		{
			new Thread(() -> sendImage(img, true)).start();
			return;
		}
		final int size = (Packet.packetSize / 4) / 64 * 63;
		boolean initial = true;
		int pixPos = 0, index = 0;
		int[] pixels = new int[size];
		long time = Calendar.getInstance().getTimeInMillis();
		for (int x = 0; x < img.getWidth(); x++)
			for (int y = 0; y < img.getHeight(); y++)
			{
				pixels[pixPos] = img.getRGB(x, y);
				pixPos++;
				index++;
				if ((pixPos % size) == 0 || (x == img.getWidth() - 1 && y == img.getHeight() - 1))
				{
					client.udp.send(new ImagePack(pixels, index, pixPos, initial, (x == img.getWidth() - 1 && y == img.getHeight() - 1), img.getWidth(), img
							.getHeight(), textField_1.getText()));
					try
					{
						Thread.sleep(5);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					initial = false;
					if ((x == img.getWidth() - 1 && y == img.getHeight() - 1))
						System.out.println("Sender ending");
					pixPos %= size;
				}
			}
		System.out.println("Time: " + (Calendar.getInstance().getTimeInMillis() - time));
	}

	public static void resize()
	{
		menuBar.setSize(f.getContentPane().getSize().width, menuBar.getHeight());
		middlePanel.setSize(f.getContentPane().getSize().width, f.getContentPane().getSize().height - (menuBar.getHeight() + 1) - bottomPanel.getHeight());
		leftPanel.setSize(leftPanel.getWidth(), middlePanel.getHeight() - 2);
		rightPanel.setSize(middlePanel.getWidth() - leftPanel.getWidth() - 2, middlePanel.getHeight() - 2);
		bottomPanel.setLocation(bottomPanel.getX(), f.getContentPane().getSize().height - bottomPanel.getHeight());

		mnForDevelopers.setEnabled(hasDeveloperPermissions);
		mnForDevelopers.setVisible(hasDeveloperPermissions);
	}

	public static void openPrivacyPolicyWindow()
	{
		Color color = new Color(220, 220, 230);
		JOptionPane pane = new JOptionPane(null, -1, 0, null, new Object[0], null);
		pane.removeAll();
		pane.setBackground(color);
		pane.setLayout(null);

		JTextArea textArea = new JTextArea();
		textArea.setSize(512 - 8, 512 - 30);
		textArea.setEditable(false);
		textArea.setBackground(Color.white);
		textArea.setText(MessageSystem.getStringByKey("privacyPolicyText"));
		textArea.setColumns(textArea.getWidth() / textArea.getFont().getSize());
		textArea.setRows(textArea.getHeight() / (int) (textArea.getFont().getSize() * 1.5));
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		pane.add(textArea);

		d = pane.createDialog(MessageSystem.getStringByKey("help") + "/" + MessageSystem.getStringByKey("privacyPolicy"));
		d.setLocationRelativeTo(f);
		d.setSize(512, 512);

		d.setVisible(true);
	}

	public static void openAudioSettingsWindow()
	{
		Color color = new Color(220, 220, 230);
		JOptionPane pane = new JOptionPane(null, -1, 0, null, new Object[0], null);
		pane.removeAll();
		pane.setBackground(color);
		pane.setLayout(null);

		JSlider slider = new JSlider();
		slider.setBackground(color);
		slider.setOrientation(SwingConstants.VERTICAL);
		slider.setBounds(10, 39, 30, 212);
		pane.add(slider);
		slider.addChangeListener((e) ->
		{
			client.sounds.all = (float) Math.sqrt(slider.getValue()) / 10f;
		});

		JLabel lblLeft = new JLabel("All");
		lblLeft.setHorizontalAlignment(SwingConstants.CENTER);
		lblLeft.setBounds(10, 14, 30, 14);
		pane.add(lblLeft);

		JSlider slider_1 = new JSlider();
		slider_1.setBackground(color);
		slider_1.setOrientation(SwingConstants.VERTICAL);
		slider_1.setBounds(60, 39, 30, 212);
		pane.add(slider_1);
		slider_1.addChangeListener((e) ->
		{
			client.sounds.left = (float) Math.sqrt(slider_1.getValue()) / 10f;
		});

		JLabel lblRight = new JLabel("Left");
		lblRight.setHorizontalAlignment(SwingConstants.CENTER);
		lblRight.setBounds(60, 14, 30, 14);
		pane.add(lblRight);

		JSlider slider_2 = new JSlider();
		slider_2.setBackground(color);
		slider_2.setOrientation(SwingConstants.VERTICAL);
		slider_2.setBounds(100, 39, 30, 212);
		pane.add(slider_2);
		slider_2.addChangeListener((e) ->
		{
			client.sounds.right = (float) Math.sqrt(slider_2.getValue()) / 10f;
		});

		JLabel label = new JLabel("Right");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(100, 14, 30, 14);
		pane.add(label);

		Checkbox btnNewButton = new Checkbox("Headphones");
		btnNewButton.setFont(new Font("Arial", Font.PLAIN, 9));
		btnNewButton.setBounds(10, 262, 64, 23);
		pane.add(btnNewButton);
		btnNewButton.addItemListener((e) ->
		{
			client.sounds.headphonesEnabled = btnNewButton.getState();
		});

		Checkbox button = new Checkbox("Microphone");
		button.setFont(new Font("Arial", Font.PLAIN, 9));
		button.setBounds(78, 262, 62, 23);
		pane.add(button);
		button.addItemListener((e) ->
		{
			client.sounds.microphoneEnabled = button.getState();
		});

		d = pane.createDialog(MessageSystem.getStringByKey("settings") + "/" + MessageSystem.getStringByKey("audio"));
		d.setLocationRelativeTo(f);
		d.setSize(160, 320);
		d.setVisible(true);
	}

	public static void openProfileWindow()
	{
		Color color = new Color(220, 220, 230);
		JOptionPane pane = new JOptionPane(null, -1, 0, null, new Object[0], null);
		pane.removeAll();
		pane.setBackground(color);

		Panel fields = new Panel();
		fields.setBounds(10, 11, 291, 199);
		pane.add(fields);
		fields.setLayout(new GridLayout(10, 2, 5, 5));

		JLabel lblLogin = new JLabel("Login");
		lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogin.setBounds(10, 11, 46, 14);
		fields.add(lblLogin);

		JTextField fieldLogin = new JTextField(account.login);
		fieldLogin.setEditable(false);
		fieldLogin.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldLogin.setBounds(339, 8, 86, 20);
		fields.add(fieldLogin);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblPassword.setBounds(10, 36, 46, 14);
		fields.add(lblPassword);

		JPasswordField fieldPassword = new JPasswordField(account.password);
		fieldPassword.setEditable(false);
		fieldPassword.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldPassword.setBounds(339, 33, 86, 20);
		fields.add(fieldPassword);

		JLabel lblSurname = new JLabel("Surname");
		lblSurname.setHorizontalAlignment(SwingConstants.CENTER);
		lblSurname.setBounds(10, 86, 46, 14);
		fields.add(lblSurname);

		JTextField fieldSurname = new JTextField(account.surname);
		fieldSurname.setEditable(false);
		fieldSurname.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldSurname.setBounds(339, 83, 86, 20);
		fields.add(fieldSurname);

		JLabel lblName = new JLabel("Name");
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		lblName.setBounds(10, 61, 46, 14);
		fields.add(lblName);

		JTextField fieldName = new JTextField(account.name);
		fieldName.setEditable(false);
		fieldName.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldName.setBounds(339, 58, 86, 20);
		fields.add(fieldName);

		JLabel lblSecondName = new JLabel("Second Name");
		lblSecondName.setHorizontalAlignment(SwingConstants.CENTER);
		lblSecondName.setBounds(10, 111, 46, 14);
		fields.add(lblSecondName);

		JTextField fieldSecondName = new JTextField(account.secondname);
		fieldSecondName.setEditable(false);
		fieldSecondName.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldSecondName.setBounds(339, 108, 86, 20);
		fields.add(fieldSecondName);

		JLabel lblGender = new JLabel("Gender");
		lblGender.setHorizontalAlignment(SwingConstants.CENTER);
		lblGender.setBounds(10, 136, 46, 14);
		fields.add(lblGender);

		Choice choiceGender = new Choice();
		choiceGender.setEnabled(false);
		fields.add(choiceGender);
		choiceGender.addItem("Man");
		choiceGender.addItem("Woman");
		choiceGender.addItem("Hiden");
		choiceGender.addItem("Another");
		choiceGender.select(account.gender = ("man".equalsIgnoreCase(account.gender) ? "Man"
				: "woman".equalsIgnoreCase(account.gender) ? "Woman" : "hiden".equalsIgnoreCase(account.gender) ? "Hiden" : "Other"));

		JLabel lblAge = new JLabel("Age");
		lblAge.setHorizontalAlignment(SwingConstants.CENTER);
		lblAge.setBounds(10, 161, 46, 14);
		fields.add(lblAge);

		byte age = 0;
		JFormattedTextField fieldAge = new JFormattedTextField(age);
		fieldAge.setEditable(false);
		fieldAge.setText(account.age);
		fieldAge.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldAge.setBounds(339, 183, 86, 20);
		fields.add(fieldAge);

		JLabel lblState = new JLabel("State");
		lblState.setHorizontalAlignment(SwingConstants.CENTER);
		lblState.setBounds(10, 186, 46, 14);
		fields.add(lblState);

		JTextField fieldState = new JTextField(account.state);
		fieldState.setEditable(false);
		fieldState.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldState.setBounds(339, 158, 86, 20);
		fields.add(fieldState);

		JLabel lblPhone = new JLabel("Phone number");
		lblPhone.setHorizontalAlignment(SwingConstants.CENTER);
		lblPhone.setBounds(10, 186, 46, 14);
		fields.add(lblPhone);

		JTextField fieldPhone = new PhoneNumberFormattedTextField(account.phone);
		fieldPhone.setEditable(false);
		fieldPhone.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldPhone.setBounds(339, 158, 86, 20);
		fields.add(fieldPhone);

		JLabel lblEmail = new JLabel("E-mail");
		lblEmail.setHorizontalAlignment(SwingConstants.CENTER);
		lblEmail.setBounds(10, 186, 46, 14);
		fields.add(lblEmail);

		JTextField fieldEmail = new EmailFormattedTextField(account.email);
		fieldEmail.setEditable(false);
		fieldEmail.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldEmail.setBounds(339, 158, 86, 20);
		fields.add(fieldEmail);

		Checkbox checkbox = new Checkbox("Edit");
		checkbox.addItemListener((e) ->
		{
			boolean editable = checkbox.getState();
			fieldPassword.setEditable(editable);
			fieldName.setEditable(editable);
			fieldSurname.setEditable(editable);
			fieldSecondName.setEditable(editable);
			choiceGender.setEnabled(editable);
			fieldAge.setEditable(editable);
			fieldState.setEditable(editable);
			fieldPhone.setEditable(editable);
			fieldEmail.setEditable(editable);
		});
		checkbox.setBounds(93, 216, 60, 22);
		pane.add(checkbox);

		Button button = new Button("Save");
		button.setBounds(159, 216, 60, 22);
		pane.add(button);
		button.addActionListener((e) ->
		{
			if (JOptionPane.showInputDialog("Enter login: ").equals(account.login))
				if (JOptionPane.showInputDialog("Enter password: ").equals(account.password))
					if (new String(fieldPassword.getPassword()).equals(account.password) ? true
							: JOptionPane.showInputDialog("Enter password: ").equals(account.password))
					{
						LoginPacket lpack = new LoginPacket("changeaccountinfo", account.login, account.login + "\n" + account.password, new InetSocketAddress(
								client.udp.clientAddress, client.udp.port), null, new Account(account.login, new String(fieldPassword.getPassword()), fieldName
										.getText(), fieldSurname.getText(), fieldSecondName.getText(), choiceGender.getSelectedItem(), fieldAge.getText(),
										fieldState.getText(), fieldPhone.getText(), fieldEmail.getText()));
						client.udp.send(Encryptor.encryptPacket(lpack, client.clientkey, EncryptionType.None, EncryptionType.Client));
					}
					else JOptionPane.showMessageDialog(null, "Password is wrong!");
				else JOptionPane.showMessageDialog(null, "Password is wrong!");
			else JOptionPane.showMessageDialog(null, "Login is wrong!");
		});
		f.setVisible(true);

		d = pane.createDialog(MessageSystem.getStringByKey("accounts") + "/" + MessageSystem.getStringByKey("profile"));
		d.setLocationRelativeTo(f);
		d.setSize(320, 360);
		d.setVisible(true);
	}

	public static void openPacketSendWindow()
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
		okBtn.addActionListener((e) ->
		{
			client.send(type.getText(), msg.getText(), account.login, reciever.getText());
			d.setVisible(false);
			d = null;
		});
		d = pane.createDialog(MessageSystem.getStringByKey("forDevelopers") + "/" + MessageSystem.getStringByKey("sendPacket"));
		d.setLocationRelativeTo(f);
		d.setSize(240, 160);
		d.setVisible(true);
	}

	public static void openSignInWindow(boolean signUp)
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
		okBtn.addActionListener((e) ->
		{
			if (new String(passwordField.getPassword()).equals(new String(passwordRepeatField.getPassword())) || !signUp)
			{
				lastLogin = loginField.getText();
				lastPassword = new String(passwordField.getPassword());
				client.send("sign" + (signUp ? "up" : "in"), lastLogin + "\n" + lastPassword, account.login, "server");
				d.setVisible(false);
				d = null;
			}
			else JOptionPane.showMessageDialog(null, "Password repeat is wrong!");
		});

		okBtn.setBackground(Color.WHITE);
		pane.add(okBtn);

		d = pane.createDialog(MessageSystem.getStringByKey("accounts") + "/" + MessageSystem.getStringByKey((signUp ? "signUp" : "signIn")));
		d.setLocationRelativeTo(f);
		d.setSize(240, 200);
		d.setVisible(true);
	}

	private static int width(String text, Font font)
	{
		return (int) font.getStringBounds(text, new FontRenderContext(null, true, true)).getWidth();
	}

	public static void addMsg(String user, String msg)
	{
		for (String line : msg.split("\n"))
			addLine(user, line);
	}

	public static void addLine(String user, String line)
	{
		line = user + (user.equals(account.login) ? " (You)" : "") + ": " + line;
		for (; width(line, textArea.getFont()) < textArea.getWidth() && user.equals(account.login);)
			line = " " + line;
		textArea.setText((textArea.getText().equals("") ? "" : textArea.getText() + "\n") + line);
	}

	static String propertiesPath = "properties.cfg";

	public static void saveValues()
	{
		Properties props = getPropsByValues();
		try
		{
			File file = new File(propertiesPath);
			if (!file.exists())
				file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(props);
			oos.close();
			fos.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void loadValues()
	{
		Properties props = new Properties();
		try
		{
			File file = new File(propertiesPath);
			if (!file.exists())
				file.createNewFile();
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			props = (Properties) ois.readObject();
			ois.close();
			fis.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		setValuesByProps(props);
	}

	public static void setValuesByProps(Properties props)
	{
		Client.sounds = new Sounds(props.microphoneEnabled, props.headphonesEnabled, props.left, props.right, props.all);
	}

	public static Properties getPropsByValues()
	{
		return new Properties(client.sounds.microphoneEnabled, client.sounds.headphonesEnabled, client.sounds.left, client.sounds.right, client.sounds.all);
	}

	static class Properties implements Serializable
	{
		private static final long serialVersionUID = -8948833866610289458L;
		boolean microphoneEnabled;
		boolean headphonesEnabled;
		float left;
		float right;
		float all;

		public Properties()
		{
			this.microphoneEnabled = false;
			this.headphonesEnabled = true;
			this.left = 100;
			this.right = 80;
			this.all = 100;
		}

		public Properties(boolean microphoneEnabled, boolean headphonesEnabled, float left, float right, float all)
		{
			this.microphoneEnabled = microphoneEnabled;
			this.headphonesEnabled = headphonesEnabled;
			this.left = left;
			this.right = right;
			this.all = all;
		}
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
		}

		public ButtonX(Color normalColor, Color selectedColor, Color pressedColor, Color clickedColor, Color clickedSelectedColor, Color clickedPressedColor,
				String text, boolean hasFrame, boolean[] rect)
		{
			super(normalColor, selectedColor, pressedColor, clickedColor, clickedSelectedColor, clickedPressedColor, text, hasFrame, rect);
		}

	}
}
