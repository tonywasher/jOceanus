package uk.co.tolcroft.finance.ui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JDialog;
import javax.swing.LayoutStyle;

public class PasswordDialog extends JDialog 
							implements ActionListener {
	/**
	 * Serial version ID	
	 */
	private static final long serialVersionUID = 5867685302365849587L;

	/**
	 *	Obtained password
	 */
	private char[] 			thePassword		= null;

	/**
	 * OK Button
	 */
	private JButton 		theOKButton;

	/**
	 * Password field
	 */
	private JPasswordField 	thePassField;

	/**
	 * Is the password set
	 */
	private boolean 		isPasswordSet	= false;

	/**
	 * Obtain the password
	 */
	public char[] getPassword() { return thePassword; }

	/**
	 * Constructor
	 * @param pParent the parent frame for the dialog
	 */
	public PasswordDialog(JFrame pParent, boolean isFailed) {
		/* Initialise the dialog (this calls dialogInit) */
		super(pParent, 
			  (isFailed) ? "Invalid password. Please re-enter"
					  	 : "Enter Password", 
			  true);
		setLocationRelativeTo(pParent);
	}

	/**
	 * Initialise dialog
	 */
	public void dialogInit() {
		JLabel 			myLabel;
		JPanel			myPanel;

		/* Create the components */
		myLabel 		= new JLabel("Password:");
		thePassField	= new JPasswordField("", 30);
		theOKButton		= new JButton("OK");

		/* Add the listener for item changes */
		theOKButton.addActionListener(this);
		thePassField.addActionListener(this);

		/* Initialise dialog */
		super.dialogInit();

		/* Create the panel */
		myPanel = new JPanel();

		/* Create the layout for the panel */
		GroupLayout myLayout = new GroupLayout(myPanel);
		myPanel.setLayout(myLayout);

		/* Set the layout */
		myLayout.setHorizontalGroup(
			myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(myLabel)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(thePassField)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(theOKButton)
					.addContainerGap())
		);
		myLayout.setVerticalGroup(
			myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
						.addComponent(myLabel)
						.addComponent(thePassField)
						.addComponent(theOKButton))
						.addContainerGap())
		);

		/* Set this to be the main panel */
		getContentPane().add(myPanel);
		pack();
	}

	/**
	 *  Perform a requested action
	 * @param evt the action event
	 */
	public void actionPerformed(ActionEvent evt) {

		/* If this event relates to the OK box or the password field */
		if ((evt.getSource() == (Object)theOKButton) ||
			(evt.getSource() == (Object)thePassField)) {
			/* Access the password */
			thePassword = thePassField.getPassword();

			/* Note that we have set the password */
			isPasswordSet = true;

			/* Close the dialog */
			setVisible(false);
		}			
	}

	/**
	 * show the dialog
	 */
	public boolean showDialog() {
		/* Show the dialog */
		setVisible(true);

		/* Return whether the password is set */
		return isPasswordSet;
	}
}
