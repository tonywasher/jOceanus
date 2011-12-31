package uk.co.tolcroft.backup;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.PropertySet;

public class BackupProperties extends PropertySet {
	/**
	 * Registry name for Backup Directory
	 */
	public final static String 		nameBackupDir		= "BackupDir";

	/**
	 * Registry name for Backup Prefix
	 */
	public final static String 		nameBackupPfix		= "BackupPrefix";

	/**
	 * Registry name for Archive FileName
	 */
	public final static String 		nameArchiveFile		= "ArchiveFile";

	/**
	 * Registry name for Subversion Repository Location
	 */
	public final static String 		nameSubVersionRepo	= "SubVersionRepo";

	/**
	 * Registry name for Repo Prefix
	 */
	public final static String 		nameRepoPfix		= "RepoPrefix";

	/**
	 * Registry name for Backup TimeStamp
	 */
	public final static String 		nameBackupTime		= "BackupTimeStamp";

	/**
	 * Display name for BackupDirectory
	 */
	protected final static String 	dispBackupDir		= "Backup Directory";

	/**
	 * Display name for BackupPrefix
	 */
	protected final static String 	dispBackupPfix		= "Backup Prefix";

	/**
	 * Display name for BackupDirectory
	 */
	protected final static String 	dispArchiveFile		= "Archive File";

	/**
	 * Display name for SubversioDirectory
	 */
	protected final static String 	dispSubVersionRepo	= "Subversion Repositories";

	/**
	 * Display name for Repository Prefix
	 */
	protected final static String 	dispRepoPfix		= "Repository Prefix";

	/**
	 * Display name for Backup Timestamp
	 */
	protected final static String 	dispBackupTime		= "Backup TimeStamps";

	/**
	 * Default value for BackupDirectory
	 */
	private final static String		defBackupDir		= "C:\\";

	/**
	 * Default value for BackupPrefix
	 */
	private final static String		defBackupPfix		= "FinanceBackup";

	/**
	 * Default value for Archive File
	 */
	private final static String		defArchiveFile		= "C:\\Archive.xls";

	/**
	 * Default value for SubversionDirectory
	 */
	private final static String		defSubVersionRepo	= "C:\\Program Files\\csvn\\data\\repositories";

	/**
	 * Default value for BackupPrefix
	 */
	private final static String		defRepoPfix			= "SvnRepo";

	/**
	 * Default value for Archive File
	 */
	private final static Boolean	defBackupTime		= Boolean.FALSE;

	/**
	 * Constructor
	 * @throws ModelException
	 */
	public BackupProperties() throws ModelException { super();	}

	@Override
	protected void defineProperties() {
		/* Define the properties */
		defineProperty(nameBackupDir, 		PropertyType.Directory);
		defineProperty(nameBackupPfix, 		PropertyType.String);
		defineProperty(nameArchiveFile, 	PropertyType.File);
		defineProperty(nameSubVersionRepo, 	PropertyType.Directory);
		defineProperty(nameRepoPfix,   		PropertyType.String);
		defineProperty(nameBackupTime, 		PropertyType.Boolean);
	}

	@Override
	protected Object getDefaultValue(String pName) {
		/* Handle default values */
		if (pName.equals(nameBackupDir)) 		return defBackupDir;
		if (pName.equals(nameBackupPfix)) 		return defBackupPfix;
		if (pName.equals(nameArchiveFile)) 		return defArchiveFile;
		if (pName.equals(nameSubVersionRepo)) 	return defSubVersionRepo;
		if (pName.equals(nameRepoPfix)) 		return defRepoPfix;
		if (pName.equals(nameBackupTime)) 		return defBackupTime;
		return null;
	}
	
	@Override
	protected String getDisplayName(String pName) {
		/* Handle default values */
		if (pName.equals(nameBackupDir)) 		return dispBackupDir;
		if (pName.equals(nameBackupPfix)) 		return dispBackupPfix;
		if (pName.equals(nameArchiveFile)) 		return dispArchiveFile;
		if (pName.equals(nameSubVersionRepo)) 	return dispSubVersionRepo;
		if (pName.equals(nameRepoPfix)) 		return dispRepoPfix;
		if (pName.equals(nameBackupTime)) 		return dispBackupTime;
		return null;
	}
}
