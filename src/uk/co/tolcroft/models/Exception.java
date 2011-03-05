package uk.co.tolcroft.models;

public class Exception 	extends java.lang.Exception
						implements htmlDumpable {
	/**
	 * Required serialisation field
	 */
	private static final long serialVersionUID = 3100519617218144798L;
	
	/**
	 * The class of this exception
	 */
    private ExceptionClass theClass    = null;
    
    /**
     * The associated object
     */
    private htmlDumpable   theObject   = null;
    
    /**
     * Get the class of the exception 
     * @return the class
     */
    public ExceptionClass  getExceptionClass() { return theClass; }
    
    /**
     * Get the associated object
     * @return the associated object
     */
    public Object          getObject()         { return theObject; }
    
	/**
	 * Create a new Exception object based on a string and class
	 * @param ec the exception class
	 * @param s the description of the exception
	 */
	public Exception(ExceptionClass ec, String s) { 
		super(s); 
		theClass = ec;
		fillInStackTrace();
	}

	/**
	 * Create a new Exception object based on a string and a known exception type
	 * @param ec the exception class
	 * @param s the description of the exception
	 * @param c	the underlying exception
	 */
	public Exception(ExceptionClass ec, String s, Throwable c) {
		super(s, c);
		theClass = ec;
	}
	
	/**
	 * Create a new Exception object based on a string and an object
	 * @param ec the exception class
	 * @param o	the associated object
	 * @param s the description of the exception
	 */
	public Exception(ExceptionClass ec,
			         htmlDumpable   o,
			         String         s) {
		super(s);
		theClass    = ec;
		theObject   = o;
		fillInStackTrace();
	}
	
	/**
	 * Create a new Exception object based on a string an object and a known exception type
	 * @param ec the exception class
	 * @param o	the associated object
	 * @param s the description of the exception
	 * @param c	the underlying exception
	 */
	public Exception(ExceptionClass ec,
			         htmlDumpable   o,
			         String         s,
			         Throwable		c) {
		super(s, c);
		theClass    = ec;
		theObject   = o;
	}
	
	/**
	 * Format the exception
	 * @return the formatted string
	 */
	public StringBuilder toHTMLString() {
		StringBuilder		myString     = new StringBuilder(10000);
		StringBuilder		myDetail	 = new StringBuilder(10000);	
		StackTraceElement[] myTrace      = null;
		Object				myUnderLying = null;
		StringBuilder		myXtra	 	 = null;
		int					myNumDetail  = 4;
		
		/* Initialise the string with an item name */
		myString.append("<table border=\"1\" width=\"90%\" align=\"center\">");
		myString.append("<thead><th>Exception</th>");
		myString.append("<th>Field</th><th>Value</th></thead><tbody>");
		
		/* Add the message details */
		myDetail.append("<tr><td>Message</td><td>");
		myDetail.append(getMessage());
		myDetail.append("</td></tr>");
		myDetail.append("<tr><td>Class</td><td>");
		myDetail.append(theClass);
		myDetail.append("</td></tr>");
		
		/* Access the underlying cause */
		myUnderLying = getCause();
		
		/* If the underlying cause is non-null */
		if (myUnderLying != null) {
			/* If the underlying cause is another instance of us */
			if (myUnderLying instanceof Exception) {
				/* Format the underlying exception */
				myXtra = ((Exception)myUnderLying).toHTMLString();
			}
			
			/* else we need to access the message and the stack trace */
			else {
				/* Record the underlying message */
				myDetail.append("<tr><td>ExceptionType</td><td>");
				myDetail.append(myUnderLying.getClass().getName());
				myDetail.append("</td></tr>");
				myNumDetail++;
				myDetail.append("<tr><td>Message</td><td>");
				myDetail.append(getCause().getMessage());
				myDetail.append("</td></tr>");
				myNumDetail++;
				
				/* Access the stack trace */
				myTrace = getCause().getStackTrace();
			}
		}
		
		/* Else we are the original exception */
		else {
			/* Access the stack trace */
			myTrace = getStackTrace();
		}
		
		/* Add the details */
		myString.append("<tr><th rowspan=\"");
		myString.append(myNumDetail);
		myString.append("\">Detail</th></tr>");
		myString.append(myDetail);
		myString.append("</tbody></table>");
		
		/* If there is an associated object */
		if (theObject != null) {
			/* Format the object */
			myString.append("<p>");
			myString.append(theObject.toHTMLString());
		}
		
		/* If there is a stack trace */
		if (myTrace != null) {
			/* Add the stack trace */
			myString.append("<p><table border=\"1\" width=\"90%\" align=\"center\">");
			myString.append("<thead><th>Stack Trace</th></thead><tbody>");
			
			/* Loop through the elements */
			for (StackTraceElement st : myTrace) {
				/* Add the stack trace */
				myString.append("<tr><td>");
				myString.append(st.toString());
				myString.append("</td></tr>");
			}
			
			/* Terminate the table */
			myString.append("</tbody></table>");
		}
		
		/* If there is an underlying exception */
		if (myXtra != null) {
			/* Format the Xtra detail*/
			myString.append("<p>");
			myString.append(myXtra);
		}
		
		return myString;
	}
	
	/**
	 * Enumeration of Exception classes
	 */
	public static enum ExceptionClass {
		/**
		 * Exception from SQL server
		 */
		SQLSERVER,
		
		/**
		 * Exception from SQL Server
		 */
		EXCEL,
		
		/**
		 * Exception from Encryption library
		 */
		ENCRYPT,
		
		/**
		 * Exception from Data
		 */
		DATA,
		
		/**
		 * Exception from validation
		 */
		VALIDATE,
		
		/**
		 * Exception from preferences
		 */
		PREFERENCE,
		
		/**
		 * Exception from logic
		 */
		LOGIC;
	}	
}
