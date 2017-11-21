package ru.alexandrdv.udpmessenger;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;

import javax.swing.ImageIcon;
import javax.swing.JTextArea;

public class ChatArea extends JTextArea
{
	public ChatArea()
	{
		setSelectionColor(new Color(50, 150, 255));
		setSelectedTextColor(Color.white);
	}
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		int y = 0;
		String lines = "";
		for (String line : getText().split("\n"))
		{
			int x = 0;
			for (char c : line.toCharArray())
			{
				for (String smile : Smiles.getSmileSymbols())
				{
					int size = Math.min(width(smile, g), height(line.substring(0, x), g));
					if (c == smile.charAt(0))
					{
						g.setColor(getSelectionStart() <= lines.length() + x && getSelectionEnd() > lines.length() + x ? getSelectionColor() : getBackground());
						int xPos = getBorder().getBorderInsets(this).left + width(line.substring(0, x), g);
						int yPos = getBorder().getBorderInsets(this).top + (int) ((height(line, g) + 2) * (y + 0.5)) - size / 2;
						g.fillRect(xPos, yPos, size, size);
						g.drawImage(Smiles.getSmile(smile,g.getColor()==getSelectionColor()), xPos, yPos, size, size, null);
					}
				}
				x++;
			}
			lines += line + "\n";
			y++;
		}
	}

	private int width(String text, Graphics g)
	{
		return (int) getFont().getStringBounds(text, new FontRenderContext(null, false, false)).getWidth();
	}

	private int height(String text, Graphics g)
	{
		return (int) getFont().getStringBounds(text, new FontRenderContext(null, false, false)).getHeight();
	}
}
