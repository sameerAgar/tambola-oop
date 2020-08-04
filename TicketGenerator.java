
public class TicketGenerator extends Generator {
	
	// ticket generator for players using the template generator
	public int[] ticketGen() {
		int[] ticket = new int[Settings.totTicketNum];
		for (int i=0; i<Settings.totTicketNum; i++) {
			ticket[i] = super.generator();
		}
		return ticket;
	}
}