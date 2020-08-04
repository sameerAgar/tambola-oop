
public class Player implements Runnable {
	
	// constructor and variables
	private GameData sharedData;
	private int id;
	public int count = 0;
	public int[] ticket = new int[Settings.totNumGenerate];
	public Player (GameData sharedData, int id) {
		this.sharedData = sharedData;
		this.id = id;
		TicketGenerator ticket = new TicketGenerator();
		this.ticket = ticket.ticketGen();
		printTicket(this.ticket);
	}
	public int[] winningNum = new int[Settings.numMatches];
	
	// printing the ticket of id player
	private void printTicket(int[] ticket) {
		System.out.print("Player No " + (this.id+1) + " ticket: ");
		for (int i=0; i<Settings.totTicketNum; i++) {
			System.out.print(ticket[i] + " ");
		}
		System.out.print("\n");
	}
	
	public void run() {
		// synchronized block with a lock shared among players and moderator
		synchronized(sharedData.lock) {
			// looping while the game is still on
			while (!sharedData.gameCompleteFlag) {
				
				// player checking whether the number is announced or
				// is this player already taken his/her turn or 
				// is the game complete
				while(!sharedData.numAnnouncedFlag || sharedData.playerChanceFlag[id]) {
					if(sharedData.gameCompleteFlag) break;
					try {
						sharedData.lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				// checking for the number provided game is not complete
				if(!sharedData.gameCompleteFlag) {
					for (int i = 0; i<Settings.totTicketNum; i++) {
						if (ticket[i] == sharedData.getLastNumGenerated()) {
							this.winningNum[this.count] = ticket[i];
							this.count++;
							ticket[i] = Settings.minValue-1;
							sharedData.countAll[this.id] = this.count;
							break;
						}
					}
					
					// checking whether this player got the winning count
					if(count == Settings.numMatches) {
						sharedData.winnerID = this.id;
						sharedData.winningNum = this.winningNum;
						sharedData.gameCompleteFlag = true;
						for(int i=0; i<Settings.numPlayers; i++) {
							sharedData.playerChanceFlag[i] = false;
						}
					}
					
					// marking that this player has taken his/her turn
					sharedData.playerChanceFlag[id] = true;
					
					// checking and setting whether every player has taken his/her chance
					sharedData.setEveryoneChanceFlag();
					
					// notifying other payers and moderator that this player has taken his/her chance
					sharedData.lock.notifyAll();
				}
			}
		}
	}
}