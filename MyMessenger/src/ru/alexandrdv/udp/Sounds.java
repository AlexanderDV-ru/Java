package ru.alexandrdv.udp;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.TargetDataLine;

import ru.alexandrdv.udp.client.MessageClient;
import ru.alexandrdv.udp.client.UDPClient;
import ru.alexandrdv.udp.client.UDPClient.Sound;

public class Sounds
{
	public static final int CHUNK_SIZE = 30000;
	public static final AudioFormat FORMAT = new AudioFormat(CHUNK_SIZE, 16, 2, true, true);
	public boolean microphoneEnabled = true, headphonesEnabled = true;
	public float left = 1.0f, right = 0.8f, all = 0.8f;
	
	/**
	 * 
	 * @param microphoneEnabled
	 * @param headphonesEnabled
	 * @param left
	 * @param right
	 * @param all
	 */
	public Sounds(boolean microphoneEnabled, boolean headphonesEnabled, float left, float right, float all)
	{
		this.microphoneEnabled = microphoneEnabled;
		this.headphonesEnabled = headphonesEnabled;
		this.left = left;
		this.right = right;
		this.all = all;
	}

	public void listen(MessageClient msgClient)
	{
		new Thread(()->
			{
				try
				{
					byte[] data = new byte[CHUNK_SIZE];
					TargetDataLine microphone = AudioSystem.getTargetDataLine(FORMAT);
					microphone.open();
					microphone.start();
					for (microphone.read(data, 0, CHUNK_SIZE); true; microphone.read(data, 0, CHUNK_SIZE))
						if (microphoneEnabled)
							msgClient.udp.send(new Sound(data, msgClient.otherSoundClient, 0));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		).start();
	}

	public void playClip(byte[] data)
	{
		if (!headphonesEnabled)
			return;
		try
		{
			Clip clipLeft = AudioSystem.getClip();
			clipLeft.open(Sounds.FORMAT, data, 0, data.length);

			FloatControl controller2 = (FloatControl) clipLeft.getControl(FloatControl.Type.BALANCE);
			controller2.setValue(-1);

			FloatControl controller = (FloatControl) clipLeft.getControl(FloatControl.Type.MASTER_GAIN);
			controller.setValue(controller.getMinimum() + (controller.getMaximum() - controller.getMinimum()) * left * all);
			clipLeft.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			Clip clipRight = AudioSystem.getClip();
			clipRight.open(Sounds.FORMAT, data, 0, data.length);

			FloatControl controller2 = (FloatControl) clipRight.getControl(FloatControl.Type.BALANCE);
			controller2.setValue(1);

			FloatControl controller = (FloatControl) clipRight.getControl(FloatControl.Type.MASTER_GAIN);
			controller.setValue(controller.getMinimum() + (controller.getMaximum() - controller.getMinimum()) * right * all);
			clipRight.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}