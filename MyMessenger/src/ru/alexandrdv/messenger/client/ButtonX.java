package ru.alexandrdv.messenger.client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JTextPane;

public class ButtonX extends JTextPane
{
	private static final long serialVersionUID = 5629188079550741270L;
	private boolean selected = false, clicked = false, pressed = false;
	private Color selectedColor, pressedColor, normalColor, clickedColor, clickedSelectedColor, clickedPressedColor;
	private ArrayList<ActionListener> actionListeners;

	public ButtonX(Color normalColor, Color selectedColor, Color pressedColor, Color clickedColor, Color clickedSelectedColor, Color clickedPressedColor, String text)
	{
		actionListeners = new ArrayList<ActionListener>();
		setText(text);
		setEditable(false);
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				selected = true;
				update();
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				pressed = true;
				update();
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				clicked = true;
				pressed = false;
				update();
				ActionEvent ev = new ActionEvent(this, 45, "click");
				if (actionListeners != null)
					for (ActionListener listener : actionListeners)
						listener.actionPerformed(ev);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				selected = false;
				update();
			}
		});
		this.clickedColor = clickedColor;
		this.pressedColor = pressedColor;
		this.normalColor = normalColor;
		this.selectedColor = selectedColor;
		this.clickedSelectedColor = clickedSelectedColor;
		this.clickedPressedColor = clickedPressedColor;
	}

	public ButtonX(String text)
	{
		this(Color.white, Color.lightGray, Color.gray, new Color(0, 150, 255), new Color(0, 120, 220), new Color(0, 70, 170), text);
	}

	public ButtonX()
	{
		this("");
	}

	public void update()
	{
		setBackground(isPressed() ? (isClicked() ? clickedPressedColor : pressedColor) : (isClicked() ? (isSelected() ? clickedSelectedColor : clickedColor) : (isSelected() ? selectedColor : normalColor)));
	}

	public void addActionListener(ActionListener listener)
	{
		actionListeners.add(listener);
	}

	public static int clamp(int min, int val, int max)
	{
		return Math.max(min, Math.min(val, max));
	}

	public boolean isSelected()
	{
		return selected;
	}

	public void setSelected(boolean selected)
	{
		this.selected = selected;
		update();
	}

	public boolean isClicked()
	{
		return clicked;
	}

	public void setClicked(boolean clicked)
	{
		this.clicked = clicked;
		update();
	}

	public boolean isPressed()
	{
		return pressed;
	}

	public void setPressed(boolean pressed)
	{
		this.pressed = pressed;
		update();
	}

	public Color getSelectedColor()
	{
		return selectedColor;
	}

	public void setSelectedColor(Color selectedColor)
	{
		this.selectedColor = selectedColor;
		update();
	}

	public Color getPressedColor()
	{
		return pressedColor;
	}

	public void setPressedColor(Color pressedColor)
	{
		this.pressedColor = pressedColor;
		update();
	}

	public Color getNormalColor()
	{
		return normalColor;
	}

	public void setNormalColor(Color normalColor)
	{
		this.normalColor = normalColor;
		update();
	}

	public Color getClickedColor()
	{
		return clickedColor;
	}

	public void setClickedColor(Color clickedColor)
	{
		this.clickedColor = clickedColor;
		update();
	}

	public Color getClickedSelectedColor()
	{
		return clickedSelectedColor;
	}

	public void setClickedSelectedColor(Color clickedSelectedColor)
	{
		this.clickedSelectedColor = clickedSelectedColor;
		update();
	}

	public Color getClickedPressedColor()
	{
		return clickedPressedColor;
	}

	public void setClickedPressedColor(Color clickedPressedColor)
	{
		this.clickedPressedColor = clickedPressedColor;
		update();
	}

}
