package ru.alexandrdv.messenger.client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTextPane;

public class Accounter
{

	public static void main(String[] args)
	{
		new Accounter();
	}

	public Accounter()
	{
		JFrame f = new JFrame();
		f.getContentPane().setLayout(null);
		f.setDefaultCloseOperation(3);
		ButtonX button = new ButtonX("LowWI");

		button.setBounds(175, 121, 80, 20);
		f.getContentPane().add(button);
		f.setVisible(true);
	}

}