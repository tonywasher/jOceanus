package uk.co.tolcroft.models.help;

public class DebugDetail {
	/* Constants */
	private static final String theForwardLink	= "Forward";
	private static final String theBackwardLink	= "Backward";
	
	/* Properties */
	private int				theNextId	= 0;
	private DebugLink		theLinks	= null;
	private DebugDetail		theForward	= null;
	private DebugDetail		theBackward	= null;
	private StringBuilder	theBuilder	= null;
	
	/**
	 * Get the Debug Detail
	 * @return the Detail
	 */
	protected StringBuilder getDebugDetail() { return theBuilder; }
	
	/**
	 * Constructor
	 */
	protected DebugDetail(DebugObject pObject) {
		/* Obtain the detail for this object */
		if (pObject != null)
			theBuilder  = pObject.buildDebugDetail(this);
	}
	
	/**
	 * Obtain History Links
	 * @return the formatted links
	 */
	public StringBuilder getHistoryLinks() {
		/* Ignore if no history */
		if ((theBackward == null) && (theForward == null)) return null;
		
		/* Create the StringBuilder */
		StringBuilder myBuilder = new StringBuilder(1000);
		
		/* Build the links */
		myBuilder.append("<table border=\"1\" width=\"90%\" align=\"center\">");
		myBuilder.append("<thead><th>Links</th>");
		
		/* Handle Backward Link */
		if (theBackward != null) {
			myBuilder.append("<th><a href=\"#");
			myBuilder.append(theBackwardLink);
			myBuilder.append("\">Backwards</a></th>");
		}
		
		/* Handle Forward Link */
		if (theForward != null) {
			myBuilder.append("<th><a href=\"#");
			myBuilder.append(theForwardLink);
			myBuilder.append("\">Forwards</a></th>");
		}
		
		/* Return the details */
		myBuilder.append("</thead></table>");
		return myBuilder;
	}
	
	/**
	 * Obtain Forward Debug Link
	 * @param pName the Name
	 * @return the corresponding object
	 */
	protected DebugDetail	getDebugLink(String pName) {
		DebugLink myLink = theLinks;
		
		/* Shift over # */
		if (pName.startsWith("#"))
			pName = pName.substring(1);
	
		/* Handle forward/backward links */
		if (pName.compareTo(theForwardLink) == 0) 	return theForward;
		if (pName.compareTo(theBackwardLink) == 0) 	return theBackward;
		
		/* Loop through the links */
		while (myLink != null) {
			/* If we have the link, break the loop */
			if (pName.compareTo(myLink.theName) == 0) break;
			
			/* Move to next link */
			myLink = myLink.theNext;
		}
		
		/* If we have a forward link */
		if (myLink != null) {
			/* Record and return the object */
			theForward = new DebugDetail(myLink.theObject);
			theForward.theBackward = this;
			return theForward;
		}
		
		/* Return no link */
		return null;
	}
	
	/**
	 * Format a debug link
	 * @param pItem the object for the link
	 * @param pText the text for the link
	 * @return the debug link
	 */
	public String addDebugLink(Object	pItem,
							   String	pText) {
		/* Return text if item is null */
		if (pItem == null) return pText;
		
		/* If the item is a Debug Object */
		if (pItem instanceof DebugObject) {
			StringBuilder 	myBuilder 	= new StringBuilder(100);
			
			/* Create the Debug Link */
			DebugLink myLink = new DebugLink((DebugObject)pItem);
			
			/* Add the link into the buffer */
			myBuilder.append("<a href=\"#");
			myBuilder.append(myLink.theName);
			myBuilder.append("\">");

			/* Add the text into the buffer */
			myBuilder.append(pText);

			/* Close link and return */
			myBuilder.append("</a>");
			return myBuilder.toString();
		}

		/* Else just return the text */
		else return pText;
	}
	
	/**
	 * Link Class
	 */
	private class DebugLink {
		/* Properties */
		private DebugObject	theObject	= null;
		private	String		theName		= null;
		private DebugLink	theNext		= null;
		
		/**
		 * Create standard object link
		 */
		private DebugLink(DebugObject pObject) {
			/* Store object */
			theObject 	= pObject;
			
			/* Assign name */
			theName 	= "Object" + ++theNextId;
			
			/* Add to the links */
			theNext 	= theLinks;
			theLinks	= this;
		}
	}
}
