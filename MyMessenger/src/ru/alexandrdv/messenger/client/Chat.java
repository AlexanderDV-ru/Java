package ru.alexandrdv.messenger.client;

import java.awt.Color;
import java.awt.Container;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ru.alexandrdv.messenger.client.Interface.Line;
import ru.alexandrdv.messenger.client.Interface.LineType;
import ru.alexandrdv.messenger.client.Interface.MsgContainer;

public class Chat extends JPanel
{
	public ArrayList<Line> lines = new ArrayList<Line>();
	public ArrayList<MsgContainer> rects = new ArrayList<MsgContainer>();
	public String user;
	Interface i;
	public Chat(JTabbedPane chats, String user,Interface i)
	{
		this.lines.add(new Line(this, 0, i.getTime(), LineType.Splitter, false));

		setBackground(Color.WHITE);
		this.user = user;
		this.setLayout(null);
		chats.addTab(user, this);
		i.chatsList.put(user, this);
		this.i=i;
	}

	public void repaintAll()
	{
		repaint();
		for (Line in : lines)
			in.repaint();
	}

	public void addMsg(String[] lines, boolean my)
	{
		Container r = this;
		JPanel chat = this;
		this.lines.add(new Line(chat, this.lines.get(this.lines.size() - 1).getLocation().y + 20, "", LineType.Splitter, my));
		this.lines.add(new Line(chat, this.lines.get(this.lines.size() - 1).getLocation().y + 20, i.getTime(), LineType.Splitter, my));
		int y = this.lines.get(this.lines.size() - 1).getLocation().y - 2;
		String mt = this.lines.get(this.lines.size() - 1).getText();
		for (String line : lines)
		{
			if ((getFontMetrics(getFont()).stringWidth(line)) + 50 >chat.getWidth())
			{
				String txt = "";
				for (char c : line.toCharArray())
				{

					if ((getFontMetrics(getFont()).stringWidth(txt + c)) + 50 > chat.getWidth())
					{
						this.lines.add(new Line(chat, this.lines.get(this.lines.size() - 1).getLocation().y + 20, " " + txt, my ? LineType.My : LineType.Others, my));
						if ((txt + c).length() > mt.length())
							mt = txt + c;
						txt = "";
					}
					txt += c;

				}
				this.lines.add(new Line(chat, this.lines.get(this.lines.size() - 1).getLocation().y + 20, " " + txt, my ? LineType.My : LineType.Others, my));
				if (txt.length() > mt.length())
					mt = txt;
			}
			else
			{
				this.lines.add(new Line(chat, this.lines.get(this.lines.size() - 1).getLocation().y + 20, " " + line, my ? LineType.My : LineType.Others, my));
				if (line.length() > mt.length())
					mt = line;
			}

		}
		rects.add(new MsgContainer(chat, my, my ? r.getSize().width - (int) (getFontMetrics(getFont()).stringWidth(mt) * 1.05f + 20) : 4, y, (int) (getFontMetrics(getFont()).stringWidth(mt) * 1.05f + 20), this.lines.get(this.lines.size() - 1).getLocation().y + 2 - y + 20, 10));

		if (this.lines.size() > 100000)
			for (Line line : this.lines)
				line.setLocation(line.getLocation().x, line.getLocation().y - 20 * (lines.length + 1));
		if (this.lines.size() > 100000)
			for (MsgContainer rect : this.rects)
				rect.setLocation(rect.getLocation().x, rect.getLocation().y - 20 * (lines.length + 1));
		repaint();
	}
}
