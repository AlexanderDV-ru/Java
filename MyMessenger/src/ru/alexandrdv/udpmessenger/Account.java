package ru.alexandrdv.udpmessenger;

import java.io.Serializable;

public class Account implements Serializable
{
	private static final long serialVersionUID = -6922725450994272466L;
	public String login, password, name, surname, secondname, gender, age, state, phone, email;

	public Account(String login, String password, String name, String surname, String secondname, String gender, String age, String state, String phone,
			String email)
	{
		this.login = login;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.secondname = secondname;
		this.gender = gender;
		this.age = age;
		this.state = state;
		this.phone = phone;
		this.email = email;
	}

	public Account(Account account)
	{
		this.login = account.login;
		this.password = account.password;
		this.name = account.name;
		this.surname = account.surname;
		this.secondname = account.secondname;
		this.gender = account.gender;
		this.age = account.age;
		this.state = account.state;
		this.phone = account.phone;
		this.email = account.email;
	}

}
