package loderunner;

import sun.audio.*;

import java.io.*;

public class Sound {


	private String background;
	private String win;
	private String die;
	private String gold;
	private String guardAlarm;
	
	
	public Sound() {
			this.background= "src/newambientmix.au";
			this.gold = "src/goldsound.au";
			this.win="src/td4w.au";
			this.die = "src/youareded.au";
			this.guardAlarm = "src/guardalarm.au";
	  
	}
	
	private AudioStream soundHelper(String filePath){
		try {
			FileInputStream fileInputStream = new FileInputStream(filePath);
			AudioStream audioStream = new AudioStream(fileInputStream);
			return audioStream;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	public void startBackground(){
		AudioStream sound = soundHelper(this.background);
		AudioPlayer.player.start(sound);
	}
	
	
	public void startWin(){
		AudioStream sound = soundHelper(this.win);
		AudioPlayer.player.start(sound);
	}
	
	public void startDie(){
		AudioStream sound = soundHelper(this.die);
		AudioPlayer.player.start(sound);
	}
	
	public void startGold(){
		AudioStream sound = soundHelper(this.gold);
		AudioPlayer.player.start(sound);
	}
	
	public void startAlarm(){
		AudioStream sound = soundHelper(this.guardAlarm);
		AudioPlayer.player.start(sound);
	}
	
}
