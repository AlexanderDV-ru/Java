package ru.alexandrdv.messenger.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ru.alexandrdv.messenger.client.Interface.Line;
import ru.alexandrdv.messenger.client.Interface.LineType;
import ru.alexandrdv.messenger.client.Interface.MsgContainer;
import ru.alexandrdv.messenger.client.MyScrollPane.HBarType;
import ru.alexandrdv.messenger.client.MyScrollPane.VBarType;

public class Chat extends JPanel
{
	public ArrayList<Line> lines = new ArrayList<Line>();
	public ArrayList<MsgContainer> rects = new ArrayList<MsgContainer>();
	public String user;
	//public JPanel left, right;
	Interface i;

	public Chat(JPanel chats, String user, Interface i)
	{
		lines.add(new Line(this, 0, i.getTime(), LineType.Splitter, false));

		setBackground(i.selectedContact);
		this.user = user;
		setLayout(null);
		//left = new JPanel();
		//add(left);
		//left.setSize(getSize());
		//left.setLayout(null);
		//left.setBackground(new Color(0, 0, 0, 0));
		//right = new JPanel();
		//add(right);
		//right.setSize(getSize());
		//right.setLayout(null);
		//right.setBackground(new Color(0, 0, 0, 0));
		scroll = new MyScrollPane(chats.getWidth(), chats.getHeight(), 10, HBarType.None, VBarType.Right);
		setSize(scroll.contentPane.getWidth(), scroll.contentPane.getHeight());
		scroll.add(this);
		chats.add(scroll);
		i.chatsList.put(user, this);
		this.i = i;
	}

	public void update()
	{
		//left.setSize(getSize());
		//right.setSize(getSize());
		for(MsgContainer m:rects)
			if(m.my)
				m.setLocation(getWidth()-m.getWidth(), m.getY());
		for(Line l:lines)
			l.setSize(getWidth()-20, l.getHeight());
		scroll.updateComponent(this);
		scroll.update();
	}

	MyScrollPane scroll;

	public void repaintAll()
	{
		repaint();
		for (Line in : lines)
			in.repaint();
	}

	public void addMsg(String[] text, boolean my)
	{
		JPanel side = this;
		Container r = this;
		JPanel chat = this;
		lines.add(new Line(side, lines.get(lines.size() - 1).getLocation().y + 20, "", LineType.Splitter, my));
		setFont(lines.get(lines.size() - 1).getFont());
		lines.add(new Line(side, lines.get(lines.size() - 1).getLocation().y + 20, i.getTime(), LineType.Splitter, my));
		int y = lines.get(lines.size() - 1).getLocation().y - 2;
		String mt = lines.get(lines.size() - 1).getText();
		for (String line : text)
			if (length(line,getFont())+50 > chat.getWidth())
			{
				//i.setMinimumSize(new Dimension(i.getWidth(), (int) i.getMinimumSize().getHeight()));
				String txt = "";
				for (char c : line.toCharArray())
				{
					if (length(txt + c,getFont()) + 50 > chat.getWidth())
					{
						lines.add(new Line(side, lines.get(lines.size() - 1).getLocation().y + 20, " " + txt, my ? LineType.My : LineType.Others, my));
						if ((txt + c).length() > mt.length())
							mt = txt + c;
						txt = "";
					}
					txt += c;
				}
				lines.add(new Line(side, lines.get(lines.size() - 1).getLocation().y + 20, " " + txt, my ? LineType.My : LineType.Others, my));
				if (length(txt,getFont()) > length(mt,getFont()))
					mt = txt;
			}
			else
			{
				lines.add(new Line(side, lines.get(lines.size() - 1).getLocation().y + 20, " " + line, my ? LineType.My : LineType.Others, my));
				if (length(line,getFont()) > length(mt,getFont()))
					mt = line;
			}
		rects.add(new MsgContainer(side, my, my ? r.getWidth() - (int) length(mt,getFont())+20 : 4, y, (int) length(mt,getFont())+20, lines.get(lines.size() - 1).getLocation().y + 2 - y + 20, 13));
		setSize(getWidth(), Math.max(getHeight(), 10 + rects.get(rects.size() - 1).getY() + rects.get(rects.size() - 1).getHeight()));
		update();
		repaint();
	}

	   private int length(String text, Font font)
	   {
	       return (int)font.getStringBounds(text, new FontRenderContext(null, true, true)).getWidth();
	   }
}
