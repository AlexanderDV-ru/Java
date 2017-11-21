package ru.alexandrdv.udp;

import java.util.HashMap;

public class MessageSystem
{
	private static String language = "en_uk";
	private static final HashMap<String, HashMap<String, String>> messages = new HashMap<String, HashMap<String, String>>();
	static
	{
		messages.put("en_uk", new HashMap<String, String>());
		{
			String language="en_uk";
			messages.get(language).put("privacyPolicyText", "Privacy Policy\n"
					+ "In the event you read this Privacy Policy in any language other than English, you agree that in the event of any discrepancies, the English version shall prevail.");
			messages.get(language).put("window", "Window");
			messages.get(language).put("accounts", "Accounts");
			messages.get(language).put("settings", "Settings");
			messages.get(language).put("help", "Help");
			messages.get(language).put("forDevelopers", "For developers");
			
			messages.get(language).put("resizable", "Resizable");
			messages.get(language).put("signIn", "Sign in");
			messages.get(language).put("profile", "Profile");
			messages.get(language).put("signUp", "Sign up");
			messages.get(language).put("audio", "Audio");
			messages.get(language).put("language", "Language");
			messages.get(language).put("privacyPolicy", "Privacy Policy");
			messages.get(language).put("sendPacket", "Send packet");
		}
		
		messages.put("ru_ru", new HashMap<String, String>());
		{
			String language="ru_ru";
			messages.get(language).put("privacyPolicyText", "�������� ������������������\n"
					+ "���� �� ������� ��������� �������� ������������������ �� �� ���������� �����, �� ������������ � ���, ���, � ������ ����� �����������, ���������������� ���� ����� ����� ���������� ������.");
			messages.get(language).put("window", "����");
			messages.get(language).put("accounts", "��������");
			messages.get(language).put("settings", "���������");
			messages.get(language).put("help", "�������");
			messages.get(language).put("forDevelopers", "��� �������������");
			
			messages.get(language).put("resizable", "�����������");
			messages.get(language).put("signIn", "�����");
			messages.get(language).put("profile", "�������");
			messages.get(language).put("signUp", "������������������");
			messages.get(language).put("audio", "�����");
			messages.get(language).put("language", "����");
			messages.get(language).put("privacyPolicy", "�������� ������������������");
			messages.get(language).put("sendPacket", "��������� �����");
		}
	}

	public static String getStringByKey(String key)
	{
		if (!messages.get(language).containsKey(key))
			return "null";
		return messages.get(language).get(key);
	}

	/**
	 * @return the language
	 */
	public static String getLanguage()
	{
		return language;
	}
	
	/**
	 * @param language the language to set
	 */
	public static void setLanguage(String language)
	{
		MessageSystem.language = language;
	}

}
