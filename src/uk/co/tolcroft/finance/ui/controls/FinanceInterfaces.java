package uk.co.tolcroft.finance.ui.controls;

import javax.swing.JComboBox;

import uk.co.tolcroft.finance.views.DebugManager;
import uk.co.tolcroft.finance.views.DebugManager.*;
import uk.co.tolcroft.models.DataItem;
import uk.co.tolcroft.models.EditState;

public class FinanceInterfaces {
	public enum financeCommand {
		OK,
		VALIDATEALL,
		RESETALL,
		VALIDATE,
		RESET,
		INSERTCR,
		INSERTDB,
		DELETE,
		RECOVER,
		UNDO,
		NEXT,
		PREV;
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
	
	public interface financeTable extends DataItem.tableHistory,
										  financePanel {
		public	void 		getRenderData(RenderData pData);
		public	JComboBox 	getComboBox(int row, int col);
		public	boolean		hasHeader();
		public	DataItem	extractItemAt(int row);
		public	int[]		getSelectedRows();
		public  int			getFieldForCol(int col);
	}
}
