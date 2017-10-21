package ru.alexandrdv.messenger.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class MyScrollPane extends JPanel
{
	JScrollBar horizontalScroller;
	JScrollBar verticalScroller;
	JPanel contentPane, viewport;
	HBarType ht;
	VBarType vt;
	private int scroll;
	int height2,width2;
	public MyScrollPane(int width, int height, int scroll, HBarType ht, VBarType vt)
	{
		this.scroll = scroll;
		this.vt = vt;
		this.ht = ht;
		setLayout(null);
		setSize(width, height);
		setBackground(Color.WHITE);
		height2=height - (ht == HBarType.None ? 0 : 16);
		width2=width - (vt == VBarType.None ? 0 : 16);

		viewport = new JPanel();
		viewport.setLayout(null);
		viewport.setBackground(Color.WHITE);
		viewport.setBounds((vt == VBarType.Left ? 16 : 1), (ht == HBarType.Top ? 16 : 1), width2, height2);
		super.add(viewport);

		contentPane = new JPanel();
		contentPane.setLayout(null);
		contentPane.setBackground(Color.WHITE);
		contentPane.setBounds(0, 0, width2, height2);
		viewport.add(contentPane);

		if (ht != HBarType.None)
		{
			horizontalScroller = new JScrollBar();
			horizontalScroller.setOrientation(JScrollBar.HORIZONTAL);
			super.add(horizontalScroller);
		}
		if (vt != VBarType.None)
		{
			verticalScroller = new JScrollBar();
			verticalScroller.setOrientation(JScrollBar.VERTICAL);
			super.add(verticalScroller);
		}
		AdjustmentListener l = new AdjustmentListener()
		{
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e)
			{
				contentPane.setLocation((ht != HBarType.None ? -horizontalScroller.getValue() * scroll : 0), (vt != VBarType.None ? -verticalScroller.getValue() * scroll : 0));
				update();
			}
		};
		if (ht != HBarType.None)
			horizontalScroller.addAdjustmentListener(l);
		if (vt != VBarType.None)
			verticalScroller.addAdjustmentListener(l);
		update();
	}

	public void update()
	{
		height2=getHeight() - (ht == HBarType.None ? 0 : 16);
		width2=getWidth() - (vt == VBarType.None ? 0 : 16);
		viewport.setBounds((vt == VBarType.Left ? 16 : 1), (ht == HBarType.Top ? 16 : 1), width2, height2);
		contentPane.setSize(Math.max(viewport.getWidth(),width2), Math.max(viewport.getHeight(),height2));
		if (ht != HBarType.None)
		{
			horizontalScroller.setBounds(0, (ht == HBarType.Bottom ? height2 : 0), width2, 16);
			horizontalScroller.setMaximum((int)((double)contentPane.getWidth() / scroll));
			horizontalScroller.getModel().setExtent((int)((double)horizontalScroller.getWidth() / scroll));
		}

		if (vt != VBarType.None)
		{
			verticalScroller.setBounds((vt == VBarType.Right ? width2 : 0), 0, 16, height2);
			verticalScroller.setMaximum((int)((double)contentPane.getHeight() / scroll));
			verticalScroller.getModel().setExtent((int)((double)verticalScroller.getHeight() / scroll));
		}
		repaint();
	}

	public void updateComponent(Component c)
	{
		contentPane.setSize(Math.max(contentPane.getWidth(), c.getX() + c.getWidth()), Math.max(contentPane.getHeight(), c.getY() + c.getHeight()));
	}

	@Override
	public Component add(Component c)
	{
		updateComponent(c);
		update();
		return contentPane.add(c);
	}

	public static enum VBarType
	{
		Left,
		Right,
		None
	}

	public static enum HBarType
	{
		Top,
		Bottom,
		None
	}
}
