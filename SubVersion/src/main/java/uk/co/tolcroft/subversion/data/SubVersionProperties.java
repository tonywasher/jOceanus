/*******************************************************************************
 * Copyright 2012 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package uk.co.tolcroft.subversion.data;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.PropertySet;

public class SubVersionProperties extends PropertySet {
	/**
	 * Registry name for Subversion Repository 
	 */
	public final static String 		nameSubVersionRepo	= "SubVersionRepo";

	/**
	 * Registry name for Subversion Repository User
	 */
	public final static String 		nameSubVersionUser	= "SubVersionUser";

	/**
	 * Registry name for Subversion Repository Password
	 */
	public final static String 		nameSubVersionPass	= "SubVersionPassword";

	/**
	 * Registry name for Subversion WorkSpace
	 */
	public final static String 		nameSubVersionWork	= "SubVersionWork";

	/**
	 * Registry name for Subversion BuildSpace
	 */
	public final static String 		nameSubVersionBuild	= "SubVersionBuild";

	/**
	 * Registry name for Subversion Directory
	 */
	public final static String 		nameSubVersionDir	= "SubVersionDir";

	/**
	 * Registry name for Repo Prefix
	 */
	public final static String 		nameRepoPfix		= "RepoPrefix";

	/**
	 * Display name for SubversionRepository
	 */
	protected final static String 	dispSubVersionRepo	= "Subversion Repository";

	/**
	 * Display name for SubversionUser
	 */
	protected final static String 	dispSubVersionUser	= "Subversion User";

	/**
	 * Display name for SubversionPassword
	 */
	protected final static String 	dispSubVersionPass	= "Subversion Password";

	/**
	 * Display name for SubversionWorkSpace
	 */
	protected final static String 	dispSubVersionWork	= "Subversion WorkSpace";

	/**
	 * Display name for SubversionBuildSpace
	 */
	protected final static String 	dispSubVersionBuild	= "Subversion BuildSpace";

	/**
	 * Display name for SubversionDirectory
	 */
	protected final static String 	dispSubVersionDir	= "Subversion Directory";

	/**
	 * Display name for Repository Prefix
	 */
	protected final static String 	dispRepoPfix		= "Repository Prefix";

	/**
	 * Default value for SubversionRepository
	 */
	private final static String		defSubVersionRepo	= "http://localhost/svn";

	/**
	 * Default value for SubversionUser
	 */
	private final static String		defSubVersionUser	= "User";

	/**
	 * Default value for SubversionPassword
	 */
	private final static String		defSubVersionPass	= "";

	/**
	 * Default value for SubversionWorkSpace
	 */
	private final static String		defSubVersionWork	= "C:\\";

	/**
	 * Default value for SubversionBuildSpace
	 */
	private final static String		defSubVersionBuild	= "C:\\Users\\Unknown";

	/**
	 * Default value for SubversionDirectory
	 */
	private final static String		defSubVersionDir	= "C:\\Program Files\\csvn\\data\\repositories";

	/**
	 * Default value for BackupPrefix
	 */
	private final static String		defRepoPfix			= "SvnRepo";

	/**
	 * Constructor
	 * @throws ModelException
	 */
	public SubVersionProperties() throws ModelException { super();	}

	@Override
	protected void defineProperties() {
		/* Define the properties */
		defineProperty(nameSubVersionRepo, 	PropertyType.String);
		defineProperty(nameSubVersionUser, 	PropertyType.String);
		defineProperty(nameSubVersionPass, 	PropertyType.String);
		defineProperty(nameSubVersionWork, 	PropertyType.Directory);
		defineProperty(nameSubVersionBuild, PropertyType.Directory);
		defineProperty(nameSubVersionDir, 	PropertyType.Directory);
		defineProperty(nameRepoPfix,   		PropertyType.String);
	}

	@Override
	protected Object getDefaultValue(String pName) {
		/* Handle default values */
		if (pName.equals(nameSubVersionRepo)) 	return defSubVersionRepo;
		if (pName.equals(nameSubVersionUser)) 	return defSubVersionUser;
		if (pName.equals(nameSubVersionPass)) 	return defSubVersionPass;
		if (pName.equals(nameSubVersionWork)) 	return defSubVersionWork;
		if (pName.equals(nameSubVersionBuild)) 	return defSubVersionBuild;
		if (pName.equals(nameSubVersionDir)) 	return defSubVersionDir;
		if (pName.equals(nameRepoPfix)) 		return defRepoPfix;
		return null;
	}
	
	@Override
	protected String getDisplayName(String pName) {
		/* Handle default values */
		if (pName.equals(nameSubVersionRepo)) 	return dispSubVersionRepo;
		if (pName.equals(nameSubVersionUser)) 	return dispSubVersionUser;
		if (pName.equals(nameSubVersionPass)) 	return dispSubVersionPass;
		if (pName.equals(nameSubVersionWork)) 	return dispSubVersionWork;
		if (pName.equals(nameSubVersionBuild)) 	return dispSubVersionBuild;
		if (pName.equals(nameSubVersionDir)) 	return dispSubVersionDir;
		if (pName.equals(nameRepoPfix)) 		return dispRepoPfix;
		return null;
	}
}
