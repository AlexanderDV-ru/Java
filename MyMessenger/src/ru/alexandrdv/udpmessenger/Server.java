package ru.alexandrdv.udpmessenger;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class Server
{

	public Server()
	{
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args)
	{
		JFrame f=new JFrame("");
		f.setSize(327, 317);
		f.setDefaultCloseOperation(3);
		
		JPanel pane=new JPanel();
		f.getContentPane().add(pane);
		pane.setLayout(null);
		pane.setBackground(new Color(230, 230, 240));
		
		Panel fields = new Panel();
		fields.setBounds(10, 11, 291, 199);
		pane.add(fields);
		fields.setLayout(new GridLayout(8, 2, 5, 5));
		
		JLabel lblLogin = new JLabel("Login");
		lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogin.setBounds(10, 11, 46, 14);
		fields.add(lblLogin);
		
		JTextField fieldLogin = new JTextField();
		fieldLogin.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldLogin.setBounds(339, 8, 86, 20);
		fields.add(fieldLogin);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblPassword.setBounds(10, 36, 46, 14);
		fields.add(lblPassword);
		
		JPasswordField fieldPassword = new JPasswordField();
		fieldPassword.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldPassword.setBounds(339, 33, 86, 20);
		fields.add(fieldPassword);
		
		JLabel lblName = new JLabel("Name");
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		lblName.setBounds(10, 61, 46, 14);
		fields.add(lblName);
		
		JTextField fieldName = new JTextField();
		fieldName.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldName.setBounds(339, 58, 86, 20);
		fields.add(fieldName);
		
		JLabel lblSurname = new JLabel("Surname");
		lblSurname.setHorizontalAlignment(SwingConstants.CENTER);
		lblSurname.setBounds(10, 86, 46, 14);
		fields.add(lblSurname);
		
		JTextField fieldSurname = new JTextField();
		fieldSurname.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldSurname.setBounds(339, 83, 86, 20);
		fields.add(fieldSurname);
		
		JLabel lblSecondName = new JLabel("Second Name");
		lblSecondName.setHorizontalAlignment(SwingConstants.CENTER);
		lblSecondName.setBounds(10, 111, 46, 14);
		fields.add(lblSecondName);
		
		JTextField fieldSecondName = new JTextField();
		fieldSecondName.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldSecondName.setBounds(339, 108, 86, 20);
		fields.add(fieldSecondName);
		
		JLabel lblGender = new JLabel("Gender");
		lblGender.setHorizontalAlignment(SwingConstants.CENTER);
		lblGender.setBounds(10, 136, 46, 14);
		fields.add(lblGender);
		
		Choice choiceGender = new Choice();
		fields.add(choiceGender);
		choiceGender.addItem("Man");
		choiceGender.addItem("Woman");
		choiceGender.addItem("Hiden");
		choiceGender.addItem("Another");
		
		JLabel lblAge = new JLabel("Age");
		lblAge.setHorizontalAlignment(SwingConstants.CENTER);
		lblAge.setBounds(10, 161, 46, 14);
		fields.add(lblAge);
		
		JTextField fieldAge = new JTextField();
		fieldAge.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldAge.setBounds(339, 183, 86, 20);
		fields.add(fieldAge);
		
		JLabel lblState = new JLabel("State");
		lblState.setHorizontalAlignment(SwingConstants.CENTER);
		lblState.setBounds(10, 186, 46, 14);
		fields.add(lblState);
		
		JTextField fieldState = new JTextField();
		fieldState.setBorder(UIManager.getBorder("Tree.editorBorder"));
		fieldState.setBounds(339, 158, 86, 20);
		fields.add(fieldState);
		
		Checkbox checkbox = new Checkbox("Edit");
		checkbox.addItemListener(new ItemListener()
		{
			
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				boolean editable=checkbox.getState();
				fieldLogin.setEditable(editable);
				fieldPassword.setEditable(editable);
				fieldName.setEditable(editable);
				fieldSurname.setEditable(editable);
				fieldSecondName.setEditable(editable);
				fieldAge.setEditable(editable);
				fieldState.setEditable(editable);
				choiceGender.setEnabled(editable);
			}
		});
		checkbox.setBounds(93, 216, 60, 22);
		pane.add(checkbox);
		
		Button button = new Button("Save");
		button.setBounds(159, 216, 60, 22);
		pane.add(button);
		f.setVisible(true);
	}
}
