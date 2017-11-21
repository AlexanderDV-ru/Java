package ru.alexandrdv.udpmessenger;

import java.awt.Image;
import java.io.File;
import java.io.Serializable;

import javax.swing.ImageIcon;

import ru.alexandrdv.udp.Packet;

public class ImagePack implements Serializable
{
	public static final int size = (Packet.packetSize / 4) / 64 * 63;
	private static final long serialVersionUID = -1717816689721279468L;
	public int[] img;
	public boolean initial,ending;
	public int w,h,pos,index;
	public String login;

	public ImagePack(int[] img,int index, int pos,boolean initial,boolean ending, int w, int h,String login)
	{
		super();
		this.img = img;
		this.initial=initial;
		this.ending=ending;
		this.w=w;
		this.h=h;
		this.pos=pos;
		this.index=index;
		this.login=login;
	}
}
