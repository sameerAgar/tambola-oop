
public class Tambola {
	public static void main(String[] args) {
		
		// creating the unique instance of GameData to be shared by moderator and players
		final GameData sharedData = GameData.getInstance();
		
		// creating the instance of moderator and all the players
		final Moderator mod  = new Moderator(sharedData);
		final Player[] player = new Player[Settings.numPlayers];
		for (int i=0; i<Settings.numPlayers; i++) {
			player[i] = new Player(sharedData, i);
		}
		
		// creating the threads for moderator and the players
		Thread modThread = new Thread(mod);
		Thread[] playerThread = new Thread[Settings.numPlayers];
		for (int i=0; i<Settings.numPlayers; i++) {
			playerThread[i] = new Thread(player[i]);
		}
		
		// running the moderator and the players thread
		modThread. start();
		for (int i=0; i<Settings.numPlayers; i++) {
			playerThread[i].start();
		}
	}
}
