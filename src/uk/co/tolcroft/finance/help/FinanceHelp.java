package uk.co.tolcroft.finance.help;

import uk.co.tolcroft.help.*;

public class FinanceHelp extends HelpModule {
	/**
	 * Declare the Help entities 
	 */
	private final static HelpEntry[] myChapterOne = {
		new HelpEntry("First", "FirstHelpModule", "FirstHelp.txt")
	};
	private final static HelpEntry[] myChapterTwo = {
		new HelpEntry("Second", "SecondHelpModule", "SecondHelp.txt")						
	};
	private final static HelpEntry[] myEntries = {
		new HelpEntry("ChapterOne", "FirstHelpChapter", myChapterOne),
		new HelpEntry("ChapterTwo", "SecondHelpChapter", myChapterTwo)						
	};
	
	/**
	 * Constructor
	 */
	public FinanceHelp() throws Exception {
		super();
	}
	
	/**
	 * Declare the help entities
	 */
	public HelpEntry[] getHelpEntries() { return myEntries; }
	
	/**
	 * Declare the initial name
	 */
	public String getInitialName() { return "First"; }	
	
	/**
	 * Declare the title
	 */
	public String getTitle() { return "Personal Finance Help"; }	
}
