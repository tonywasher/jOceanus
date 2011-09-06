package uk.co.tolcroft.models.ui;

import uk.co.tolcroft.models.data.EditState;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugManager.*;

public class FinanceInterfaces {
	public enum financeCommand {
		OK,
		RESETALL;
	}

	public interface financePanel {
		public	void			notifySelection(Object o);
		public	boolean			hasUpdates();
		public	void			printIt();
		public	boolean			isLocked();
		public	void			performCommand(financeCommand pCmd);
		public	EditState		getEditState();
		public  DebugManager	getDebugManager();
		public  DebugEntry		getDebugEntry();
		public  void			lockOnError(boolean isError);
	}
}
