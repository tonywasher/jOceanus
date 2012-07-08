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
package uk.co.tolcroft.jira.data;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.PropertySet;

public class JiraProperties extends PropertySet {
	/**
	 * Registry name for Jira Server 
	 */
	public final static String 		nameJiraServer		= "JiraServer";

	/**
	 * Registry name for Jira User
	 */
	public final static String 		nameJiraUser		= "JiraUser";

	/**
	 * Registry name for Jira Password
	 */
	public final static String 		nameJiraPass		= "JiraPassword";

	/**
	 * Registry name for Jira Prefix
	 */
	public final static String 		nameJiraPfix		= "JiraPrefix";

	/**
	 * Display name for JiraServer
	 */
	protected final static String 	dispJiraServer		= "Jira Server";

	/**
	 * Display name for JiraUser
	 */
	protected final static String 	dispJiraUser		= "Jira User";

	/**
	 * Display name for JiraPassword
	 */
	protected final static String 	dispJiraPass		= "Jira Password";

	/**
	 * Display name for JiraPrefix
	 */
	protected final static String 	dispJiraPfix		= "Jira Prefix";

	/**
	 * Default value for JiraServer
	 */
	private final static String		defJiraServer		= "http://localhost:8080";

	/**
	 * Default value for JiraUser
	 */
	private final static String		defJiraUser			= "User";

	/**
	 * Default value for JiraPassword
	 */
	private final static String		defJiraPass			= "";

	/**
	 * Default value for JiraPrefix
	 */
	private final static String		defJiraPfix			= "Issue #:";

	/**
	 * Constructor
	 * @throws ModelException
	 */
	public JiraProperties() throws ModelException { super();	}

	@Override
	protected void defineProperties() {
		/* Define the properties */
		defineProperty(nameJiraServer, 	PropertyType.String);
		defineProperty(nameJiraUser, 	PropertyType.String);
		defineProperty(nameJiraPass, 	PropertyType.String);
		defineProperty(nameJiraPfix, 	PropertyType.String);
	}

	@Override
	protected Object getDefaultValue(String pName) {
		/* Handle default values */
		if (pName.equals(nameJiraServer)) 	return defJiraServer;
		if (pName.equals(nameJiraUser)) 	return defJiraUser;
		if (pName.equals(nameJiraPass)) 	return defJiraPass;
		if (pName.equals(nameJiraPfix)) 	return defJiraPfix;
		return null;
	}
	
	@Override
	protected String getDisplayName(String pName) {
		/* Handle default values */
		if (pName.equals(nameJiraServer)) 	return dispJiraServer;
		if (pName.equals(nameJiraUser)) 	return dispJiraUser;
		if (pName.equals(nameJiraPass)) 	return dispJiraPass;
		if (pName.equals(nameJiraPfix)) 	return dispJiraPfix;
		return null;
	}
}
