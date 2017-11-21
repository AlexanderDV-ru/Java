package ru.alexandrdv.udpmessenger;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Smiles
{
	private static HashMap<String, BufferedImage> smiles = new HashMap<String, BufferedImage>();
	private static HashMap<String, BufferedImage> negSmiles = new HashMap<String, BufferedImage>();
	private static String smilesPage1;
	static
	{
		if (new File("SmilesPage1.png").exists())
		{
			try
			{
				BufferedImage smilesImage = ImageIO.read(new File("SmilesPage1.png"));
				int smileSize = 36;
				int offset = 1;
				smilesPage1 = "";
				for (int y = 0; y < 5; y++, smilesPage1 += "\n")
					for (int x = 0; x < 10; x++)
					{
						String smile = "" + (char) (63744 + y * 10 + x);
						smiles.put(smile, smilesImage.getSubimage(x * (offset + smileSize), y * (offset + smileSize), smileSize + offset * 2, smileSize + offset
								* 2));
						negSmiles.put(smile, convertToNegative(smilesImage.getSubimage(x * (offset + smileSize), y * (offset + smileSize), smileSize + offset
								* 2, smileSize + offset * 2)));
						smilesPage1 += smile;
					}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static BufferedImage convertToNegative(BufferedImage img)
	{
		BufferedImage bi = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < img.getHeight(); i++)
			for (int j = 0; j < img.getWidth(); j++)
				bi.setRGB(j, i, ((img.getRGB(j, i) & 0xFFFFFF00)) | Math.min(((img.getRGB(j, i) & 0xFF0000) + 200), 255));
		return bi;
	}

	public static Image getSmile(String smileString, boolean negative)
	{
		return (negative ? negSmiles : smiles).get(smileString);
	}

	public static Collection<String> getSmileSymbols()
	{
		return smiles.keySet();
	}

	public static String getSmilesPage1()
	{
		return smilesPage1;
	}

}
