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
		Throwable			myException  = this;
		StringBuilder		myObj	 	 = new StringBuilder(10000);
		int					myNumDetail  = 2;
		
		/* Initialise the string with an item name */
		myString.append("<table border=\"1\" width=\"90%\" align=\"center\">");
		myString.append("<thead><th>Exception</th>");
		myString.append("<th>Field</th><th>Value</th></thead><tbody>");
		
		/* Access this exception */
		myException = this;
		
		/* while the current exception is non-null */
		while (myException != null) {
			/* If the exception is an instance of us */
			if (myException instanceof Exception) {
				Exception myExc = (Exception)myException;
				
				/* Add the message details */
				myDetail.append("<tr><td>Class</td><td>");
				myDetail.append(myExc.theClass);
				myDetail.append("</td></tr>");

				/* If there is an associated object */
				if (myExc.theObject != null) {
					/* Format the object */
					myObj.append("<p>");
					myObj.append(myExc.theObject.toHTMLString());
				}
			}
			
			/* else this is a java exception */
			else {
				/* Record the underlying message */
				myDetail.append("<tr><td>ExceptionType</td><td>");
				myDetail.append(myException.getClass().getName());
				myDetail.append("</td></tr>");
			}

			/* Record the message details */
			myDetail.append("<tr><td>Message</td><td>");
			myDetail.append(myException.getMessage());
			myDetail.append("</td></tr>");
			myNumDetail+=2;
			
			/* Access the stack trace and the next cause */
			myTrace 	= myException.getStackTrace();
			myException	= myException.getCause();
		}
		
		/* Add the details */
		myString.append("<tr><th rowspan=\"");
		myString.append(myNumDetail);
		myString.append("\">Detail</th></tr>");
		myString.append(myDetail);
		myString.append("</tbody></table>");
		
		/* Add any objects */
		myString.append(myObj);
		
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
		 * Exception from Cryptographic library
		 */
		CRYPTO,
		
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
		LOGIC,
		
		/**
		 * Exception from subversion
		 */
		SUBVERSION;
	}	
}
