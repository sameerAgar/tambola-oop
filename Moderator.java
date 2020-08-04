
public class Moderator implements Runnable {
	
	// constructor and variables
	private GameData sharedData;
	public Moderator(GameData sharedData) {
		this.sharedData = sharedData;
		this.sharedData.setCountAll();
	}
	
	// to reset the appropriate flags after completing one iteration
	// i.e. generating and checking of number by all player and when any of the player win
	private void resetFlags() {
		for(int i=0; i<Settings.numPlayers; i++) {
			sharedData.playerChanceFlag[i] = false;
		}
		sharedData.everyoneChanceFlag = false;
	}
	
	// print the count of all players after the game gets over
	private void printCountAll() {
		for (int i=0; i<Settings.numPlayers; i++) {
			System.out.println("Total Numbers Striked out for Player " + (i+1) + ": " + sharedData.countAll[i]);
		}
	}
	
	public void run() {
		// synchronized block with a lock shared among players and moderator
		synchronized(sharedData.lock) {			
			// looping while the game is still on
			while (!sharedData.gameCompleteFlag) {
				this.resetFlags();
				
				// generate and announce number
				sharedData.addNumGenerated(Generator.generator());
				System.out.println("Moderator Announced Number: " + sharedData.getLastNumGenerated());
				sharedData.numAnnouncedFlag = true;
				
				// notify player waiting threads of the announced number
				sharedData.lock.notifyAll();
				
				// wait for some time (here 1s) to let players check their numbers
				try {
					Thread.sleep(Settings.waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// wait till every player has checked their numbers or till someone win
				while (!(sharedData.gameCompleteFlag || sharedData.everyoneChanceFlag)) {
					try {
						sharedData.lock.wait(); 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				// printing the winning player info and count of every player
				if (sharedData.gameCompleteFlag) {
					System.out.println("-----------------------------------");
					System.out.print("PLAYER " + (sharedData.winnerID+1) + " WON WITH ");
					for (int i=0; i<Settings.numMatches; i++) {
						System.out.print(sharedData.winningNum[i] + " ");
					}
					System.out.print("!! \n");
					this.printCountAll();
				}
				
				// incrementing the moderator count of number announced by 1
				sharedData.countNumGenerated++;
				
				// printing count of every player if all the numbers are generated and none of the player won
				if (!sharedData.gameCompleteFlag) {
					if (sharedData.countNumGenerated == Settings.totNumGenerate) {
						sharedData.gameCompleteFlag = true;
						System.out.println("-----------------------------------");
						System.out.println("None of the player won!");
						this.printCountAll();
						this.resetFlags();
					}
				}
				
				// setting the number announced flag
				if (sharedData.countNumGenerated == Settings.totNumGenerate) {
					sharedData.numAnnouncedFlag = true;
				} else sharedData.numAnnouncedFlag = false;
				
				// notifying the player whether the game is still on or not
				sharedData.lock.notifyAll();
			}
		}		
	}
	
}