package uk.co.tolcroft.models.views;

import java.util.HashMap;

import javax.swing.JFrame;

import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.Properties;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugManager.DebugEntry;
import uk.co.tolcroft.models.security.SecureManager;
import uk.co.tolcroft.models.sheets.SpreadSheet;
import uk.co.tolcroft.models.ui.StatusBar;
import uk.co.tolcroft.models.Exception;

public abstract class DataControl<T extends DataSet<?>> {
	/* Debug Names */
	public static final String DebugViews		= "DataViews";
	public static final String DebugData		= "UnderlyingData";
	public static final String DebugUpdates		= "DataUpdates";
	public static final String DebugAnalysis	= "Analysis";
	public static final String DebugError		= "Error";

	/* Properties */
	private T				theData			= null;
	private T				theUpdates		= null;
	private Exception		theError		= null;
	private StatusBar		theStatusBar	= null;
	private Properties		theProperties	= null;
	private JFrame			theFrame		= null;
    private SecureManager	theSecurity		= null;
    private DebugManager	theDebugMgr		= null;
   
    private HashMap<String, DebugEntry>	theMap	= null;
	
    /**
     * Constructor
     */
    protected DataControl() {
    	/* Create the Secure Manager */
    	theSecurity = new SecureManager();
    	
    	/* Create the Debug Map */
    	theMap = new HashMap<String, DebugEntry>();
    }
    
	/**
	 * Record new DataSet
	 * @param pData the new DataSet
	 */
	public void			setData(T pData)	{
		/* Store the data */
		theData  = pData;
		
		/* Update the Debug entry */
		DebugEntry myDebug = getDebugEntry(DebugData);
		myDebug.setObject(pData);
	}

	/**
	 * Obtain current DataSet
	 * @return the current DataSet
	 */
	public T			getData()		{ return theData; }

	/**
	 * Record new Updates
	 * @param pUpdates the new Updates
	 */
	protected void		setUpdates(T pUpdates)	{
		/* Store the updates */
		theUpdates  = pUpdates;
		
		/* Update the Debug entry */
		DebugEntry myDebug = getDebugEntry(DebugUpdates);
		myDebug.setObject(pUpdates);
	}

	/**
	 * Obtain current Updates
	 * @return the current Updates
	 */
	public T			getUpdates()	{ return theUpdates; }

	/**
	 * Set new Error
	 * @param pError the new Error
	 */
	protected void		setError(Exception pError)	{ theError  = pError; }

	/**
	 * Obtain current updates
	 * @return the current Updates
	 */
	public Exception	getError()		{ return theError; }

	/**
	 * Set StatusBar
	 * @param pStatusBar the StatusBar
	 */
	public void			setStatusBar(StatusBar pStatusBar)	{ theStatusBar = pStatusBar; }

	/**
	 * Obtain StatusBar
	 * @return the StatusBar
	 */
	public StatusBar	getStatusBar()	{ return theStatusBar; }

	/**
	 * Set Properties
	 * @param pProperties the Properties
	 */
	public void		setProperties(Properties pProperties)	{ theProperties = pProperties; }

	/**
	 * Obtain Properties
	 * @return the Properties
	 */
	public Properties	getProperties()	{ return theProperties; }

	/**
	 * Set Frame
	 * @param pProperties the Properties
	 */
	public void		setFrame(JFrame pFrame)	{ theFrame = pFrame; }

	/**
	 * Obtain Frame
	 * @return the Frame
	 */
	public JFrame		getFrame()		{ return theFrame; }

	/**
	 * Obtain Secure Manager
	 * @return the Secure Manager
	 */
	public SecureManager		getSecurity() 		{ return theSecurity; }
	
	/**
	 * Set Debug Manager
	 * @param pDebugMgr the Debug Manager
	 */
	protected void				setDebugMgr(DebugManager pDebug)	{ 
		/* Store the Manager */
		theDebugMgr = pDebug;
		
		/* Create Debug Entries */
		DebugEntry myViews		= getDebugEntry(DebugViews);
		DebugEntry myData		= getDebugEntry(DebugData);
		DebugEntry myUpdates	= getDebugEntry(DebugUpdates);
		DebugEntry myAnalysis	= getDebugEntry(DebugAnalysis);
		DebugEntry myError		= getDebugEntry(DebugError);
		
		/* Create the structure */
		myViews.addAsRootChild();
		myAnalysis.addAsRootChild();
		myData.addAsChildOf(myViews);
		myUpdates.addAsChildOf(myViews);
		myError.addAsRootChild();
		
		/* Hide the Error Entry */
		myError.hideEntry();
	}

	/**
	 * Obtain Debug Manager
	 * @return the Debug Manager
	 */
	public DebugManager			getDebugMgr() 		{ return theDebugMgr; }
	
	/**
	 * Add Debug Entry
	 * @param pName the Name of the entry
	 * @return the Debug Entry
	 */
	public DebugEntry			getDebugEntry(String pName) {
		/* Access any existing entry */
		DebugEntry myEntry = theMap.get(pName);
		
		/* If the entry does not exist */
		if (myEntry == null) {
			/* Build the entry and add to the map */
			myEntry = theDebugMgr.new DebugEntry(pName);
			theMap.put(pName, myEntry);
		}
		
		/* Return the entry */
		return myEntry;
	}
	
	/**
	 * Obtain SpreadSheet object
	 * @return SpreadSheet object
	 */
	public abstract SpreadSheet<T>	getSpreadSheet();

	/**
	 * Obtain Database object
	 * @return database object
	 */
	public abstract Database<T>		getDatabase() throws Exception;

	/**
	 * Obtain DataSet object
	 * @return dataSet object
	 */
	public abstract T				getNewData();
	
	/**
	 * Refresh the Windows 
	 */
	protected abstract void refreshWindow();

	/**
	 * Analyse the data in the view
	 * @param bPreserve preserve any error
	 */ 
	protected abstract boolean analyseData(boolean bPreserve);
}
