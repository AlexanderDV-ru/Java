package ru.alexandrdv.messenger.client;

import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

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

	JPanel contacts;
	Client client;
	JScrollBar scrollBar = new JScrollBar();
	JTabbedPane chats;
	public Interface(Client client)
	{
		this.client = client;
		getContentPane().setLayout(null);
		loadColors();

		chats = new JTabbedPane(JTabbedPane.TOP);
		chats.setBounds(159, 0, 517, 515);
		getContentPane().add(chats);
		setDefaultCloseOperation(3);

		setSize(692, 552);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				saveColors();

			}
		});
		setAlwaysOnTop(true);
		setVisible(true);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 156, 517);
		getContentPane().add(tabbedPane);

		JPanel settings = new JPanel();
		tabbedPane.addTab(" Settings", null, settings, null);
		settings.setLayout(new BoxLayout(settings, BoxLayout.X_AXIS));

		contacts = new JPanel();
		tabbedPane.addTab(" Contacts", null, contacts, null);
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

		contactBtns.setBounds(0, 0, 134, 489);
		contacts.add(contactBtns);
		contactBtns.setLayout(null);

		JScrollPane optionPaneScroller = new JScrollPane();
		optionPaneScroller.setBounds(0, 47, 156, 470);
		settings.add(optionPaneScroller);
		{
			JPanel optionPane = new JPanel();
			optionPaneScroller.setViewportView(optionPane);
			optionPane.setLayout(null);
			{
				int number = 0;
				JPanel colorOfYourMessages = new JPanel();
				colorOfYourMessages.setBounds(0, 0, 154, 46);
				optionPane.add(colorOfYourMessages);
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
				optionPane.add(textColorOfYourMessages);
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
				optionPane.add(colorOfOtherMessages);
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
				optionPane.add(textColorOfOtherMessages);
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
		}
		repaint();
	}

	public void addContactBtn(String ip)
	{

		for (Button b1 : contactBtnsList)
			if (b1.getLabel().equals(ip))
				return;
		if (ip == null)
			return;
		if (ip.equals(""))
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
		contactBtns.setSize(contactBtns.getWidth(), contactBtnsList.size() * 40);
		scrollBar.setMaximum(contactBtnsList.size());

		contactBtns.add(b);
		contactBtnsList.add(b);

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
		My(new Color(255, 217, 242), Color.white),
		Others(new Color(217, 242, 255), Color.white),
		Splitter(Color.white, Color.black);
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

	
}