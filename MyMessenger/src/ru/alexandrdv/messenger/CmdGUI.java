package ru.alexandrdv.messenger;

import java.awt.Color;
import java.awt.Component;
import java.awt.ScrollPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public abstract class CmdGUI
{
	public JFrame f;
	public JTextArea area = new JTextArea();

	public CmdGUI()
	{
		f = new JFrame();
		f.setDefaultCloseOperation(3);
		f.setTitle(" - Console");
		f.setSize(758, 507);
		f.setResizable(false);
		f.setLocationRelativeTo(null);
		f.setLayout(null);

		area.setBackground(Color.BLACK);
		area.setEditable(false);
		area.setForeground(Color.LIGHT_GRAY);
		area.setBounds(0, 0, 500, 300);

		f.setVisible(true);
		area.setFocusable(true);
		ScrollPane pane = new ScrollPane();
		pane.setBackground(Color.BLACK);
		pane.add(area);
		pane.setBounds(0, 0, 754, 487);
		f.add(pane);
		addkl(area);
		area.setSelectionColor(Color.white);
		updateArea();
	}

	public String editable = "";
	public String notEditable = "Console by AlexandrDV";
	int pos = 0;

	public void addkl(Component c)
	{
		c.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent arg0)
			{

				switch (arg0.getKeyCode())
				{
					case KeyEvent.VK_DELETE:
					case KeyEvent.VK_BACK_SPACE:
						if (editable.length() > 0)
						{
							editable = editable.substring(0, editable.length() - 1);
							updateArea();
						}
						break;
					case KeyEvent.VK_CONTROL:
					case KeyEvent.VK_CAPS_LOCK:
					case KeyEvent.VK_SHIFT:
					case KeyEvent.VK_ALT:
					case KeyEvent.VK_NUM_LOCK:
						break;
					case KeyEvent.VK_DOWN:
						cmdspos--;
						if (cmdspos < 0)
							cmdspos = 0;
						if (cmdspos > cmds.size() - 1)
							cmdspos = cmds.size() - 1;
						editable = cmds.get(cmdspos);
						break;
					case KeyEvent.VK_UP:
						cmdspos++;
						if (cmdspos < 0)
							cmdspos = 0;
						if (cmdspos > cmds.size() - 1)
							cmdspos = cmds.size() - 1;
						editable = cmds.get(cmdspos);
						break;
					case KeyEvent.VK_LEFT:
					case KeyEvent.VK_RIGHT:
					case KeyEvent.VK_PAGE_DOWN:
					case KeyEvent.VK_PAGE_UP:
					case KeyEvent.VK_WINDOWS:
						break;
					case KeyEvent.VK_ENTER:
						if (cmds.contains(editable))
							cmds.remove(editable);
						else cmdspos++;
						cmds.add(editable);
						notEditable += "\n>" + editable;
						Command(editable.split(" "));
						editable = "";
						updateArea();
						break;
					default:
						editable += arg0.getKeyChar();
						updateArea();
						break;
				}

			}
		});
	}

	int cmdspos = 0;
	public ArrayList<String> cmds = new ArrayList<String>();

	public void updateArea()
	{
		area.setText(notEditable + "\n>" + editable);
	}
	public void print(Object o)
	{
		notEditable+=o.toString();
		updateArea();
	}
	public void print(String s)
	{
		notEditable+=s;
		updateArea();
	}
	public void println(Object o)
	{
		notEditable+=(notEditable.endsWith("\n")?"":"\n")+o.toString()+"\n";
		updateArea();
	}
	public void println(String s)
	{
		notEditable+=(notEditable.endsWith("\n")?"":"\n")+s+"\n";
		updateArea();
	}

	public abstract void Command(String[] args);

}
