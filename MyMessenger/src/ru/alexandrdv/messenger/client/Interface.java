package ru.alexandrdv.messenger.client;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ru.alexandrdv.messenger.Encryptor;
import ru.alexandrdv.messenger.Encryptor.EncryptionType;
import ru.alexandrdv.messenger.Packet.SignPacket;
import javax.swing.border.BevelBorder;
import javax.swing.UIManager;
import javax.swing.JRadioButton;

public class Interface extends JFrame
{
	private static final long serialVersionUID = -7199428079194503335L;
	// JPanel chat;
	JPanel contactBtns;

	public int parseI(String s)
	{
		String i = "";
		for (char c : s.toCharArray())
			if (Character.isDigit(c))
				i += c;
			else if ((c == '-') && i.equals(""))
				i += c;
		return Integer.parseInt(i);
	}

	Interface i;
	JPanel contacts;
	Client client;
	JScrollBar scrollBar = new JScrollBar();
	JTabbedPane chats;
	JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	public Interface(Client client)
	{
		i = this;
		this.client = client;
		getContentPane().setLayout(null);
		loadColors();
		setDefaultCloseOperation(3);

		setSize(1145, 629);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				saveColors();

			}
		});
		setVisible(true);

		tabbedPane.setBounds(0, 20, 156, 517);
		getContentPane().add(tabbedPane);

		JPanel settings = new JPanel();
		tabbedPane.addTab(" Settings", null, settings, null);
		settings.setLayout(new BoxLayout(settings, BoxLayout.X_AXIS));

		contacts = new JPanel();
		contacts.setLayout(null);

		contactBtns = new JPanel();
		scrollBar.setMaximum(0);

		scrollBar.addAdjustmentListener(new AdjustmentListener()
		{

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e)
			{
				contactBtns.setLocation(contactBtns.getLocation().x, -scrollBar.getValue() * 40);
				scrollBar.getModel().setExtent(12);

			}
		});

		scrollBar.setBounds(134, 0, 17, 489);
		contacts.add(scrollBar);

		contactBtns.setBounds(0, 20, 134, 489);
		contacts.add(contactBtns);
		contactBtns.setLayout(null);

		textField = new JTextField();
		textField.setBounds(0, 0, 100, 20);
		contacts.add(textField);

		Button button = new Button("Find");
		button.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				client.sendQuery(client.writer, "getips", null, EncryptionType.None, client.getAddress());

			}
		});
		button.setBounds(100, 0, 32, 20);
		contacts.add(button);

		settings.setLayout(null);
		{
			JPanel colorOfYourMessages = new JPanel();
			colorOfYourMessages.setBounds(0, 0, 154, 46);
			settings.add(colorOfYourMessages);
			colorOfYourMessages.setLayout(null);
			{
				JLabel colorOfYourMessagesText = new JLabel("Color of your messages");
				colorOfYourMessagesText.setFont(new Font("Times New Roman", 0, 12));
				colorOfYourMessagesText.setBounds(0, 5, 154, 14);
				colorOfYourMessages.add(colorOfYourMessagesText);
				colorOfYourMessagesText.setHorizontalAlignment(SwingConstants.CENTER);

				int defaultR = LineType.My.defaultBackground.getRed();
				int defaultG = LineType.My.defaultBackground.getGreen();
				int defaultB = LineType.My.defaultBackground.getBlue();

				JFormattedTextField r = new JFormattedTextField(LineType.My.background.getRed());
				r.setBounds(8, 20, 26, 20);
				colorOfYourMessages.add(r);

				JFormattedTextField g = new JFormattedTextField(LineType.My.background.getGreen());
				g.setBounds(38, 20, 26, 20);
				colorOfYourMessages.add(g);

				JFormattedTextField b = new JFormattedTextField(LineType.My.background.getBlue());
				b.setBounds(68, 20, 26, 20);
				colorOfYourMessages.add(b);

				Button reset = new Button("Reset");
				reset.setBackground(Color.WHITE);
				reset.setBounds(98, 20, 46, 20);
				colorOfYourMessages.add(reset);
				reset.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						r.setText(defaultR + "");
						g.setText(defaultG + "");
						b.setText(defaultB + "");
						LineType.My.background = new Color(defaultR, defaultG, defaultB);
						repaintAll();

					}
				});

				FocusListener l = new FocusAdapter()
				{
					@Override
					public void focusLost(FocusEvent e)
					{
						r.setText(Math.max(0, Math.min(parseI(r.getText()), 255)) + "");
						g.setText(Math.max(0, Math.min(parseI(g.getText()), 255)) + "");
						b.setText(Math.max(0, Math.min(parseI(b.getText()), 255)) + "");
						LineType.My.background = new Color(parseI(r.getText()), parseI(g.getText()), parseI(b.getText()));
						repaintAll();
					}
				};
				r.addFocusListener(l);
				g.addFocusListener(l);
				b.addFocusListener(l);

			}
			JPanel textColorOfYourMessages = new JPanel();
			textColorOfYourMessages.setLayout(null);
			textColorOfYourMessages.setBounds(0, 46, 154, 46);
			settings.add(textColorOfYourMessages);
			{
				JLabel textColorOfYourMessagesText = new JLabel("Text color of your messages");
				textColorOfYourMessagesText.setFont(new Font("Times New Roman", 0, 12));
				textColorOfYourMessagesText.setHorizontalAlignment(SwingConstants.CENTER);
				textColorOfYourMessagesText.setBounds(0, 5, 154, 14);
				textColorOfYourMessages.add(textColorOfYourMessagesText);

				int defaultR = LineType.My.defaultForeground.getRed();
				int defaultG = LineType.My.defaultForeground.getGreen();
				int defaultB = LineType.My.defaultForeground.getBlue();

				JFormattedTextField r = new JFormattedTextField(LineType.My.foreground.getRed());
				r.setBounds(8, 20, 26, 20);
				textColorOfYourMessages.add(r);

				JFormattedTextField g = new JFormattedTextField(LineType.My.foreground.getGreen());
				g.setBounds(38, 20, 26, 20);
				textColorOfYourMessages.add(g);

				JFormattedTextField b = new JFormattedTextField(LineType.My.foreground.getBlue());
				b.setBounds(68, 20, 26, 20);
				textColorOfYourMessages.add(b);

				Button reset = new Button("Reset");
				reset.setBackground(Color.WHITE);
				reset.setBounds(98, 20, 46, 20);
				textColorOfYourMessages.add(reset);
				reset.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						r.setText(defaultR + "");
						g.setText(defaultG + "");
						b.setText(defaultB + "");
						LineType.My.foreground = new Color(defaultR, defaultG, defaultB);
						repaintAll();

					}
				});

				FocusListener l = new FocusAdapter()
				{
					@Override
					public void focusLost(FocusEvent e)
					{
						r.setText(Math.max(0, Math.min(parseI(r.getText()), 255)) + "");
						g.setText(Math.max(0, Math.min(parseI(g.getText()), 255)) + "");
						b.setText(Math.max(0, Math.min(parseI(b.getText()), 255)) + "");
						LineType.My.foreground = new Color(parseI(r.getText()), parseI(g.getText()), parseI(b.getText()));
						repaintAll();
					}
				};
				r.addFocusListener(l);
				g.addFocusListener(l);
				b.addFocusListener(l);
			}
			JPanel colorOfOtherMessages = new JPanel();
			colorOfOtherMessages.setLayout(null);
			colorOfOtherMessages.setBounds(0, 92, 154, 46);
			settings.add(colorOfOtherMessages);
			{
				JLabel colorOfOtherMessagesText = new JLabel("Color of other messages");
				colorOfOtherMessagesText.setFont(new Font("Times New Roman", 0, 12));
				colorOfOtherMessagesText.setHorizontalAlignment(SwingConstants.CENTER);
				colorOfOtherMessagesText.setBounds(0, 5, 154, 14);
				colorOfOtherMessages.add(colorOfOtherMessagesText);

				int defaultR = LineType.Others.defaultBackground.getRed();
				int defaultG = LineType.Others.defaultBackground.getGreen();
				int defaultB = LineType.Others.defaultBackground.getBlue();

				JFormattedTextField r = new JFormattedTextField(LineType.Others.background.getRed());
				r.setBounds(8, 20, 26, 20);
				colorOfOtherMessages.add(r);

				JFormattedTextField g = new JFormattedTextField(LineType.Others.background.getGreen());
				g.setBounds(38, 20, 26, 20);
				colorOfOtherMessages.add(g);

				JFormattedTextField b = new JFormattedTextField(LineType.Others.background.getBlue());
				b.setBounds(68, 20, 26, 20);
				colorOfOtherMessages.add(b);

				Button reset = new Button("Reset");
				reset.setBackground(Color.WHITE);
				reset.setBounds(98, 20, 46, 20);
				colorOfOtherMessages.add(reset);
				reset.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						r.setText(defaultR + "");
						g.setText(defaultG + "");
						b.setText(defaultB + "");
						LineType.Others.background = new Color(defaultR, defaultG, defaultB);
						repaintAll();

					}
				});

				FocusListener l = new FocusAdapter()
				{
					@Override
					public void focusLost(FocusEvent e)
					{
						r.setText(Math.max(0, Math.min(parseI(r.getText()), 255)) + "");
						g.setText(Math.max(0, Math.min(parseI(g.getText()), 255)) + "");
						b.setText(Math.max(0, Math.min(parseI(b.getText()), 255)) + "");
						LineType.Others.background = new Color(parseI(r.getText()), parseI(g.getText()), parseI(b.getText()));
						repaintAll();
					}
				};
				r.addFocusListener(l);
				g.addFocusListener(l);
				b.addFocusListener(l);
			}
			JPanel textColorOfOtherMessages = new JPanel();
			textColorOfOtherMessages.setLayout(null);
			textColorOfOtherMessages.setBounds(0, 138, 154, 46);
			settings.add(textColorOfOtherMessages);
			{
				JLabel textColorOfOtherMessagesText = new JLabel("Text color of other messages");
				textColorOfOtherMessagesText.setFont(new Font("Times New Roman", 0, 12));
				textColorOfOtherMessagesText.setHorizontalAlignment(SwingConstants.CENTER);
				textColorOfOtherMessagesText.setBounds(0, 5, 154, 14);
				textColorOfOtherMessages.add(textColorOfOtherMessagesText);

				int defaultR = LineType.Others.defaultForeground.getRed();
				int defaultG = LineType.Others.defaultForeground.getGreen();
				int defaultB = LineType.Others.defaultForeground.getBlue();

				JFormattedTextField r = new JFormattedTextField(LineType.Others.foreground.getRed());
				r.setBounds(8, 20, 26, 20);
				textColorOfOtherMessages.add(r);

				JFormattedTextField g = new JFormattedTextField(LineType.Others.foreground.getGreen());
				g.setBounds(38, 20, 26, 20);
				textColorOfOtherMessages.add(g);

				JFormattedTextField b = new JFormattedTextField(LineType.Others.foreground.getBlue());
				b.setBounds(68, 20, 26, 20);
				textColorOfOtherMessages.add(b);

				Button reset = new Button("Reset");
				reset.setBackground(Color.WHITE);
				reset.setBounds(98, 20, 46, 20);
				textColorOfOtherMessages.add(reset);

				reset.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						r.setText(defaultR + "");
						g.setText(defaultG + "");
						b.setText(defaultB + "");
						LineType.Others.foreground = new Color(defaultR, defaultG, defaultB);
						repaintAll();

					}
				});

				FocusListener l = new FocusAdapter()
				{
					@Override
					public void focusLost(FocusEvent e)
					{
						r.setText(Math.max(0, Math.min(parseI(r.getText()), 255)) + "");
						g.setText(Math.max(0, Math.min(parseI(g.getText()), 255)) + "");
						b.setText(Math.max(0, Math.min(parseI(b.getText()), 255)) + "");
						LineType.Others.foreground = new Color(parseI(r.getText()), parseI(g.getText()), parseI(b.getText()));
						repaintAll();
					}
				};
				r.addFocusListener(l);
				g.addFocusListener(l);
				b.addFocusListener(l);
			}
		}

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 676, 21);
		getContentPane().add(menuBar);

		JMenu mnNewMenu = new JMenu("Accounts");
		menuBar.add(mnNewMenu);
		mntmSignIn = new JMenuItem("Sign in");
		mntmSignIn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				openSignInWindow(false);
			}
		});
		mnNewMenu.add(mntmSignIn);

		JMenuItem mntmSignUp = new JMenuItem("Sign up");
		mntmSignUp.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				openSignInWindow(true);
			}
		});

		mntmAccount = new JMenuItem("Account Info");
		mntmAccount.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				openAccountInfoWindow();
			}
		});
		mntmAccount.setVisible(false);
		mnNewMenu.add(mntmAccount);
		mnNewMenu.add(mntmSignUp);

		messages = new JPanel();
		messages.setVisible(false);
		messages.setBounds(159, 20, 517, 574);
		getContentPane().add(messages);
		messages.setLayout(null);

		JScrollBar scrollBar_1 = new JScrollBar();
		scrollBar_1.setBounds(500, 2, 17, 484);
		messages.add(scrollBar_1);

		chats = new JTabbedPane(JTabbedPane.TOP);
		chats.setBounds(0, 2, 500, 484);
		messages.add(chats);

		JButton btnNewButton = new JButton("Send");
		btnNewButton.setBackground(Color.WHITE);
		btnNewButton.setBounds(452, 545, 65, 29);
		messages.add(btnNewButton);

		JTextArea textArea = new JTextArea();
		textArea.setBackground(Color.WHITE);
		textArea.setBorder(UIManager.getBorder("PasswordField.border"));
		textArea.setBounds(0, 489, 451, 85);
		messages.add(textArea);
		
		
		btnNewButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{

				if (client.signedIn)
				{

					{
						String msg = textArea.getText();
						client.sendTo(client.writer, ((Chat) i.chats.getSelectedComponent()).user, Encryptor.encrypt(msg, client.encryptionKey, EncryptionType.None, EncryptionType.Client), EncryptionType.Client);
						((Chat) i.chats.getSelectedComponent()).addMsg(msg.split("\n"), true);
						textArea.setText("");
					}
				}
				else client.println("You weren't sign in!");

			}
		});
		repaint();
	}

	JMenuItem mntmAccount;
	JMenuItem mntmSignIn;
	JPanel messages;
	JDialog d;

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
						client.lastLogin = loginField.getText();
						client.lastPassword = new String(passwordField.getPassword());
						String encryptedLogin = Encryptor.encrypt(loginField.getText(), client.encryptionKey, EncryptionType.None, EncryptionType.Client);
						String encryptedPassword = Encryptor.encrypt(new String(passwordField.getPassword()), client.encryptionKey, EncryptionType.None, EncryptionType.Client);
						client.writer.writeObject(new SignPacket(encryptedLogin, encryptedPassword, EncryptionType.Client, client.getAddress(), signUp));
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
		d.setLocation(getLocation().x + getSize().width / 2, getLocation().y + getSize().height / 2);
		d.setSize(240, 200);
		d.setVisible(true);
	}

	public void openAccountInfoWindow()
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

		JTextField loginField = new JTextField(client.login);
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
		for (int i = 0; i < client.password.length(); i++)
			pass += "*";
		JPasswordField passwordField = new JPasswordField(pass);
		passwordField.setEditable(false);
		password.add(passwordField);

		d = pane.createDialog("Account's Info");
		d.setLocation(getLocation().x + getSize().width / 2, getLocation().y + getSize().height / 2);
		d.setSize(240, 200);
		d.setVisible(true);
	}

	public void addContactBtn(String ip)
	{

		for (Button b1 : contactBtnsList)
			if (b1.getLabel().equals(ip))
				return;
		Button b = new Button(ip);
		b.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				client.Command(new String[] { "joinTo", b.getLabel() });

			}
		});
		if (contactBtnsList.size() != 0)
			b.setLocation(0, contactBtnsList.get(contactBtnsList.size() - 1).getLocation().y + 40);
		b.setSize(contactBtns.getWidth(), 40);
		contactBtnsList.add(b);
		chatsList.put(ip, new Chat(i.chats, ip, i));
		contactBtns.setSize(contactBtns.getWidth(), contactBtnsList.size() * 40);

		contactBtns.add(b);
		scrollBar.setMaximum(contactBtnsList.size());

	}

	ArrayList<Button> contactBtnsList = new ArrayList<Button>();

	public void repaintAll()
	{
		repaint();

	}

	public void saveColors()
	{
		try
		{
			File f = new File("settings.cfg");
			if (!f.exists())
				f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(new Color[] { LineType.My.background, LineType.My.foreground, LineType.Others.background, LineType.Others.foreground });
			oos.close();
			fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public String getTime()
	{
		Calendar c = Calendar.getInstance();
		String hour = c.get(Calendar.HOUR) + "";
		String min = c.get(Calendar.MINUTE) + "";
		String sec = c.get(Calendar.SECOND) + "";
		for (; hour.length() < 2;)
			hour = "0" + hour;
		for (; min.length() < 2;)
			min = "0" + min;
		for (; sec.length() < 2;)
			sec = "0" + sec;
		return hour + ":" + min + ":" + sec;
	}

	public void loadColors()
	{
		try
		{
			File f = new File("settings.cfg");
			if (!f.exists())
				f.createNewFile();
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object[] objects = (Object[]) ois.readObject();
			LineType.My.background = ((Color) objects[0]);
			LineType.My.foreground = ((Color) objects[1]);
			LineType.Others.background = ((Color) objects[2]);
			LineType.Others.foreground = ((Color) objects[3]);
			ois.close();
			fis.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public static class Line extends JLabel
	{
		public LineType type;

		public Line(Container f, int y, String text, LineType t, boolean isMy)
		{
			super();
			setText(text);
			setLocation(10, y);
			setSize(f.getSize().width - 20, 20);
			setHorizontalAlignment(isMy ? SwingConstants.RIGHT : SwingConstants.LEFT);
			setForeground(t.foreground);
			f.add(this);
			this.type = t;
		}

		@Override
		public void paint(Graphics g)
		{
			setForeground(type.foreground);
			super.paint(g);
		}

	}

	static enum LineType
	{
		My(new Color(235, 197, 222), Color.black),
		Others(new Color(187, 210, 235), Color.black),
		Splitter(Color.BLACK, new Color(255, 255, 255));
		LineType(Color background, Color foreground)
		{
			this.background = background;
			this.foreground = foreground;
			this.defaultBackground = background;
			this.defaultForeground = foreground;
		}

		public Color background, foreground, defaultBackground, defaultForeground;
	}

	static class RoundedRect extends JPanel
	{
		Color c;

		public RoundedRect(Container f, Color c, int x, int y, int w, int h, int rounding)
		{
			super();
			this.c = c;
			setBounds(x, y, w, h);
			size = rounding;
			f.add(this);
		}

		int size;

		@Override
		public void paint(Graphics g)
		{
			g.setColor(c);
			g.fillOval(0, 0, size * 2, size * 2);
			g.fillOval(getWidth() - size * 2 - 2, 0, size * 2, size * 2);
			g.fillOval(getWidth() - size * 2 - 2, getHeight() - size * 2 - 2, size * 2, size * 2);
			g.fillOval(0, getHeight() - size * 2 - 2, size * 2, size * 2);
			g.fillRect(size, 0, getWidth() - size * 2 - 1, getHeight() - 1);
			g.fillRect(0, size, getWidth() - 1, getHeight() - size * 2 - 1);
		}
	}

	static class MsgContainer extends JPanel
	{
		boolean my;

		public MsgContainer(Container f, boolean isMy, int x, int y, int w, int h, int rounding)
		{
			super();
			my = isMy;
			setBounds(x, y, w, h);
			size = rounding;
			f.add(this);
		}

		int size;

		@Override
		public void paint(Graphics g)
		{
			g.setColor(my ? LineType.My.background : LineType.Others.background);
			g.fillOval(0, 0, size * 2, size * 2);
			g.fillOval(getWidth() - size * 2 - 2, 0, size * 2, size * 2);
			g.fillOval(getWidth() - size * 2 - 2, getHeight() - size * 2 - 2, size * 2, size * 2);
			g.fillOval(0, getHeight() - size * 2 - 2, size * 2, size * 2);
			g.fillRect(size, 0, getWidth() - size * 2 - 1, getHeight() - 1);
			g.fillRect(0, size, getWidth() - 1, getHeight() - size * 2 - 1);
		}
	}

	HashMap<String, Chat> chatsList = new HashMap<String, Chat>();
	private JTextField textField;
	static class MyScrollPane extends JPanel
	{
		JScrollBar horizontalScroller;
		JScrollBar verticalScroller;
		JPanel contentPane;
		public MyScrollPane(int width,int height,int scroll)
		{
			setLayout(null);
			setSize(width, height);
			
			horizontalScroller = new JScrollBar();
			horizontalScroller.setOrientation(JScrollBar.HORIZONTAL);
			horizontalScroller.setBounds(0, height-16, width-16, 16);
			horizontalScroller.getModel().setExtent(horizontalScroller.getWidth()/scroll);
			add(horizontalScroller);
			
			verticalScroller = new JScrollBar();
			verticalScroller.setOrientation(JScrollBar.VERTICAL);
			verticalScroller.setBounds(width-16, 0, 16, height-16);
			verticalScroller.getModel().setExtent(verticalScroller.getHeight()/scroll);
			add(verticalScroller);
			
			JPanel viewport = new JPanel();
			viewport.setLayout(null);
			viewport.setBounds(0, 0, width-16, height-16);
			add(viewport);
			
			contentPane = new JPanel();
			contentPane.setLayout(null);
			contentPane.setBounds(0, 0, width-16, height-16);
			viewport.add(contentPane);
			
			AdjustmentListener l=new AdjustmentListener()
			{
				@Override
				public void adjustmentValueChanged(AdjustmentEvent e)
				{
					contentPane.setLocation(-horizontalScroller.getValue()*scroll, -verticalScroller.getValue()*scroll);
				}
			};
			horizontalScroller.addAdjustmentListener(l);
			verticalScroller.addAdjustmentListener(l);
		}
	}
}
