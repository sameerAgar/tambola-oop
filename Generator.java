import java.util.Random;

public abstract class Generator {
	
	// random integer generator between the given minimum and maimum value
	public final static int generator() {
		Random rand = new Random();
		return rand.nextInt(Settings.maxValue - Settings.minValue + 1) + Settings.minValue;
	}	
	
	// abstract method generating the ticket of players
	abstract int[] ticketGen();
}