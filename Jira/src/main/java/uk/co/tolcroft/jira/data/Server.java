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

import java.awt.Color;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import uk.co.tolcroft.jira.data.Security.Group;
import uk.co.tolcroft.jira.data.Security.Role;
import uk.co.tolcroft.jira.data.Security.User;
import uk.co.tolcroft.jira.soap.AbstractRemoteConstant;
import uk.co.tolcroft.jira.soap.JiraSoapService;
import uk.co.tolcroft.jira.soap.JiraSoapServiceServiceLocator;
import uk.co.tolcroft.jira.soap.RemoteFilter;
import uk.co.tolcroft.jira.soap.RemoteIssue;
import uk.co.tolcroft.jira.soap.RemoteIssueType;
import uk.co.tolcroft.jira.soap.RemotePriority;
import uk.co.tolcroft.jira.soap.RemoteProject;
import uk.co.tolcroft.jira.soap.RemoteResolution;
import uk.co.tolcroft.jira.soap.RemoteStatus;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.PropertySet.PropertyManager;

public class Server {
	/**
	 * Jira Soap location
	 */
	private static final String		theLocation = "/rpc/soap/jirasoapservice-v2";
	
	/**
	 * Authorization Token
	 */
	private String					theAuthToken;

	/**
	 * The Soap Service
	 */
    private final JiraSoapService 	theService;
	
	/**
	 * The Security
	 */
    private final Security 			theSecurity;
	
	/**
	 * Issues
	 */
	private final List<Issue>		theIssues;

	/**
	 * Projects
	 */
	private final List<Project>		theProjects;

	/**
	 * Filters
	 */
	private final List<Filter>		theFilters;

	/**
	 * IssueTypes
	 */
	private final List<IssueType>	theIssueTypes;

	/**
	 * Statuses
	 */
	private final List<Status>		theStatuses;

	/**
	 * Resolutions
	 */
	private final List<Resolution>	theResolutions;

	/**
	 * Priorities
	 */
	private final List<Priority>	thePriorities;

	/**
	 * Obtain the service
	 * @return the service
	 */
	public JiraSoapService getService() { return theService; }
	
    /**
	 * Constructor
	 */
	public Server() throws ModelException {
		Properties myLogProp = new Properties();
		myLogProp.setProperty("log4j.rootLogger", "ERROR, A1");
		myLogProp.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
		myLogProp.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
		myLogProp.setProperty("log4j.appender.A1.layout.ConversionPattern", "%-4r [%t] %-5p %c %x - %m%n");
		PropertyConfigurator.configure(myLogProp);
		
		/* Allocate the lists */
		theIssues 		= new ArrayList<Issue>();
		theProjects 	= new ArrayList<Project>();
		theFilters 		= new ArrayList<Filter>();
		theIssueTypes 	= new ArrayList<IssueType>();
		theStatuses		= new ArrayList<Status>();
		theResolutions	= new ArrayList<Resolution>();
		thePriorities	= new ArrayList<Priority>();
		
		/* Allocate a service locator */
	    JiraSoapServiceServiceLocator myLocator = new JiraSoapServiceServiceLocator();

	    /* Protect against exceptions */
        try
        {
        	/* Access the Jira properties */
        	JiraProperties myProperties = 
        			(JiraProperties)PropertyManager.getPropertySet(JiraProperties.class);
        	String baseUrl = myProperties.getStringValue(JiraProperties.nameJiraServer) + theLocation;
        	
        	/* Locate the service */
        	theService = myLocator.getJirasoapserviceV2(new URL(baseUrl));
        	
        	/* Allocate the security class */
        	theSecurity = new Security(this);
        	
        	/* Load constants */
           	loadFilters();
        	loadIssueTypes();
        	loadStatuses();
        	loadResolutions();
        	loadPriorities();
        }
    	catch (ModelException e) { throw e; }
        catch (Throwable e)
        {
        	/* Pass the exception on */
            throw new ModelException(ExceptionClass.JIRA,
            						 "Failed to locate service", 
            						 e);
        }
	}
	
	/**
	 * Get Authentication Token
	 * @return the authentication token
	 */
    public String getAuthToken() throws ModelException
    {
    	/* If we have a token, return it */
    	if (theAuthToken != null) return theAuthToken;
    	
	    /* Protect against exceptions */
        try
        {
        	/* Access the Jira properties */
        	JiraProperties myProperties = 
        			(JiraProperties)PropertyManager.getPropertySet(JiraProperties.class);

        	/* Login to the service */
        	theAuthToken = theService.login(myProperties.getStringValue(JiraProperties.nameJiraUser),
        									myProperties.getStringValue(JiraProperties.nameJiraPass));
        	return theAuthToken;
        }
        catch (RemoteException e)
        {
        	/* Pass the exception on */
            throw new ModelException(ExceptionClass.JIRA,
            						 "Failed to login to server", 
            						 e);
        }
    }

    /**
     * Obtain Project
     * @param pKey the key for the project
     * @return the Project
     */
    public Project getProject(String pKey) throws ModelException {
    	/* Return an existing project if found in list */
    	Iterator<Project> myIterator = theProjects.iterator();
    	while (myIterator.hasNext()) {
    		Project myProject = myIterator.next();
    		if (myProject.getKey().equals(pKey)) return myProject;
    	}
    	
    	/* Protect against exceptions */
    	try {
    		/* Access the authorization token */
    		String myToken = getAuthToken();
    		
    		/* Access the project and add to list */
    		RemoteProject myProjDtl = theService.getProjectByKey(myToken, pKey);
    		Project myProject = new Project(this, myProjDtl);
   			theProjects.add(myProject);
   			return myProject;
    	}
    	catch (ModelException e) { throw e; }
        catch (RemoteException e)
        {
        	/* Pass the exception on */
            throw new ModelException(ExceptionClass.JIRA,
            						 "Failed to load project " + pKey, 
            						 e);
        }
    }
    
    /**
     * Load Issues from Filter
     * @param pFilter the filter name
     */
    public void loadIssuesFromFilter(String pFilter) throws ModelException {
    	/* Access the filter */
    	Filter myFilter = null;
    	Iterator<Filter> myIterator = theFilters.iterator();
    	while (myIterator.hasNext()) {
    		myFilter = myIterator.next();
    		if (myFilter.getName().equals(pFilter)) break;
    		myFilter = null;
    	}

    	/* Handle filter not found */
    	if (myFilter == null) {
        	/* Pass the exception on */
            throw new ModelException(ExceptionClass.JIRA,
            						 "Filter does not exists " + pFilter);    		
    	}
    	
    	/* Protect against exceptions */
    	try {
    		/* Access the authorization token */
    		String myToken = getAuthToken();
    		
    		/* Access the issues */
    		RemoteIssue[] myIssues = theService.getIssuesFromFilter(myToken, myFilter.getId());
    		
    		/* Loop through the issues */
    		for(RemoteIssue myIssue: myIssues) {
    			/* Add new issue to list */
    			theIssues.add(new Issue(this, myIssue));
    		}
    	}
    	catch (ModelException e) { throw e; }
        catch (RemoteException e)
        {
        	/* Pass the exception on */
            throw new ModelException(ExceptionClass.JIRA,
            						 "Failed to load issues from filter", 
            						 e);
        }
    	
    }
    
    /**
     * Obtain User
     * @param pName the name of the user
     * @return the User
     */
    public User getUser(String pName) throws ModelException {
    	/* Pass the call to the security service */
    	return theSecurity.getUser(pName);
    }
    
    /**
     * Obtain Group
     * @param pName the name of the group
     * @return the Group
     */
    public Group getGroup(String pName) throws ModelException {
    	/* Pass the call to the security service */
    	return theSecurity.getGroup(pName);
    }
    
    /**
     * Obtain Role
     * @param pId the Id of the role
     * @return the Role
     */
    public Role getRole(long pId) throws ModelException {
    	/* Pass the call to the security service */
    	return theSecurity.getRole(pId);
    }
    
    /**
     * Obtain IssueType
     * @param pId the id of the IssueType
     * @return the IssueType
     */
    public IssueType getIssueType(String pId) throws ModelException {
    	/* Return an existing issue type if found in list */
    	Iterator<IssueType> myIterator = theIssueTypes.iterator();
    	while (myIterator.hasNext()) {
    		IssueType myType = myIterator.next();
    		if (myType.getId().equals(pId)) return myType;
    	}
    	
    	/* throw exception */
        throw new ModelException(ExceptionClass.JIRA,
        						 "Invalid IssueTypeId " + pId);
    }
    
    /**
     * Obtain Status
     * @param pId the id of the Status
     * @return the Status
     */
    public Status getStatus(String pId) throws ModelException {
    	/* Return an existing status if found in list */
    	Iterator<Status> myIterator = theStatuses.iterator();
    	while (myIterator.hasNext()) {
    		Status myStatus = myIterator.next();
    		if (myStatus.getId().equals(pId)) return myStatus;
    	}
    	
    	/* throw exception */
        throw new ModelException(ExceptionClass.JIRA,
        						 "Invalid StatusId " + pId);
    }
    
    /**
     * Obtain Resolution
     * @param pId the id of the Resolution
     * @return the Resolution
     */
    public Resolution getResolution(String pId) throws ModelException {
    	/* Return an existing resolution if found in list */
    	Iterator<Resolution> myIterator = theResolutions.iterator();
    	while (myIterator.hasNext()) {
    		Resolution myRes = myIterator.next();
    		if (myRes.getId().equals(pId)) return myRes;
    	}
    	
    	/* throw exception */
        throw new ModelException(ExceptionClass.JIRA,
        						 "Invalid ResolutionId " + pId);
    }
    
    /**
     * Obtain Priority
     * @param pId the id of the Priority
     * @return the Priority
     */
    public Priority getPriority(String pId) throws ModelException {
    	/* Return an existing priority if found in list */
    	Iterator<Priority> myIterator = thePriorities.iterator();
    	while (myIterator.hasNext()) {
    		Priority myPriority = myIterator.next();
    		if (myPriority.getId().equals(pId)) return myPriority;
    	}
    	
    	/* throw exception */
        throw new ModelException(ExceptionClass.JIRA,
        						 "Invalid PriorityId " + pId);
    }
    
    /**
     * Load Filters
     */
    private void loadFilters() throws ModelException {
    	/* Protect against exceptions */
    	try {
    		/* Access the authorization token */
    		String myToken = getAuthToken();
    		
    		/* Access the filters */
    		RemoteFilter[] myFilters = theService.getSavedFilters(myToken);
    		
    		/* Loop through the filters */
    		for(RemoteFilter myFilter: myFilters) {
    			/* Add new filter to list */
    			theFilters.add(new Filter(myFilter));
    		}
    	}
    	catch (ModelException e) { throw e; }
        catch (RemoteException e)
        {
        	/* Pass the exception on */
            throw new ModelException(ExceptionClass.JIRA,
            						 "Failed to load filters", 
            						 e);
        }
    }
    
    /**
     * Load IssueTypes
     */
    private void loadIssueTypes() throws ModelException {
    	/* Protect against exceptions */
    	try {
    		/* Access the authorization token */
    		String myToken = getAuthToken();
    		
    		/* Access the issue types */
    		RemoteIssueType[] myTypes = theService.getIssueTypes(myToken);
    		
    		/* Loop through the issue types */
    		for(RemoteIssueType myType: myTypes) {
    			/* Add new type to list */
    			theIssueTypes.add(new IssueType(myType));
    		}

    		/* Access the sub issue types */
    		myTypes = theService.getSubTaskIssueTypes(myToken);
    		
    		/* Loop through the issue types */
    		for(RemoteIssueType myType: myTypes) {
    			/* Add new type to list */
    			theIssueTypes.add(new IssueType(myType));
    		}
    	}
    	catch (ModelException e) { throw e; }
        catch (RemoteException e)
        {
        	/* Pass the exception on */
            throw new ModelException(ExceptionClass.JIRA,
            						 "Failed to load issue types", 
            						 e);
        }
    }
    
    /**
     * Load Statuses
     */
    private void loadStatuses() throws ModelException {
    	/* Protect against exceptions */
    	try {
    		/* Access the authorization token */
    		String myToken = getAuthToken();
    		
    		/* Access the statuses */
    		RemoteStatus[] myStatuses = theService.getStatuses(myToken);
    		
    		/* Loop through the statuses */
    		for(RemoteStatus myStatus: myStatuses) {
    			/* Add new status to list */
    			theStatuses.add(new Status(myStatus));
    		}
    	}
    	catch (ModelException e) { throw e; }
        catch (RemoteException e)
        {
        	/* Pass the exception on */
            throw new ModelException(ExceptionClass.JIRA,
            						 "Failed to load statuses", 
            						 e);
        }
    }
    
    /**
     * Load Resolutions
     */
    private void loadResolutions() throws ModelException {
    	/* Protect against exceptions */
    	try {
    		/* Access the authorization token */
    		String myToken = getAuthToken();
    		
    		/* Access the resolutions */
    		RemoteResolution[] myResolutions = theService.getResolutions(myToken);
    		
    		/* Loop through the resolutions */
    		for(RemoteResolution myResolution: myResolutions) {
    			/* Add new resolution to list */
    			theResolutions.add(new Resolution(myResolution));
    		}
    	}
    	catch (ModelException e) { throw e; }
        catch (RemoteException e)
        {
        	/* Pass the exception on */
            throw new ModelException(ExceptionClass.JIRA,
            						 "Failed to load resolutions", 
            						 e);
        }
    }
    
    /**
     * Load Priorities
     */
    private void loadPriorities() throws ModelException {
    	/* Protect against exceptions */
    	try {
    		/* Access the authorization token */
    		String myToken = getAuthToken();
    		
    		/* Access the priorities */
    		RemotePriority[] myPriorities = theService.getPriorities(myToken);
    		
    		/* Loop through the priorities */
    		for(RemotePriority myPriority: myPriorities) {
    			/* Add new priority to list */
    			thePriorities.add(new Priority(myPriority));
    		}
    	}
    	catch (ModelException e) { throw e; }
        catch (RemoteException e)
        {
        	/* Pass the exception on */
            throw new ModelException(ExceptionClass.JIRA,
            						 "Failed to load priorities", 
            						 e);
        }
    }

    /**
	 * Server constant class
	 */
	private abstract class ServerConstant {
		/**
		 * The id of the constant
		 */
		private final String			theId;

		/**
		 * The name of the constant
		 */
		private final String			theName;
		
		/**
		 * The description of the constant
		 */
		private final String			theDesc;
		
		/**
		 * Get the id of the constant
		 * @return the id
		 */
		public String getId() 		{ return theId; }

		/**
		 * Get the name of the constant
		 * @return the name
		 */
		public String getName() 	{ return theName; }

		/**
		 * Get the description of the constant
		 * @return the description
		 */
		public String getDesc() 	{ return theDesc; }

		/**
		 * Constructor
		 * @param pConstant the underlying constant
		 */
		private ServerConstant(AbstractRemoteConstant pConstant) {
			/* Access the details */
			theId 		= pConstant.getId();
			theName 	= pConstant.getName();
			theDesc 	= pConstant.getDescription();
		}
	}

	/**
	 * IssueType class
	 */
	public class IssueType extends ServerConstant {
		/**
		 * Is this IssueType a sub-task
		 */
		private final boolean			isSubTask;
		
		/**
		 * Is the Issue type a subTask
		 * @return true/false
		 */
		public boolean isSubTask() 	{ return isSubTask; }

		/**
		 * Constructor
		 * @param pIssueType the underlying issue type
		 */
		private IssueType(RemoteIssueType pIssueType) {
			/* Access the details */
			super(pIssueType);
			isSubTask	= pIssueType.isSubTask();
		}
	}

	/**
	 * Status class
	 */
	public class Status extends ServerConstant {
		/**
		 * Constructor
		 * @param pStatus the underlying status
		 */
		private Status(RemoteStatus pStatus) {
			/* Access the details */
			super(pStatus);
		}
	}

	/**
	 * Resolution class
	 */
	public class Resolution extends ServerConstant {
		/**
		 * Constructor
		 * @param pResolution the underlying resolution
		 */
		private Resolution(RemoteResolution pResolution) {
			/* Access the details */
			super(pResolution);
		}
	}
	
	/**
	 * Priority class
	 */
	public class Priority extends ServerConstant {
		/**
		 * The color for this priority
		 */
		private final Color			theColor;
		
		/**
		 * Obtain the color for this priority
		 * @return the color
		 */
		public Color getColor() 	{ return theColor; }

		/**
		 * Constructor
		 * @param pPriority the underlying priority
		 */
		private Priority(RemotePriority pPriority) {
			/* Access the details */
			super(pPriority);
			theColor = Color.decode(pPriority.getColor());
		}
	}

	/**
	 * Filter class
	 */
	public class Filter {
		/**
		 * The underlying remote filter
		 */
		private final RemoteFilter		theFilter;
		
		/**
		 * The id of the filter
		 */
		private final String			theId;

		/**
		 * The name of the filter
		 */
		private final String			theName;
		
		/**
		 * The description of the filter
		 */
		private final String			theDesc;
		
		/**
		 * Get the underlying filter
		 * @return the version
		 */
		public RemoteFilter getFilter()	{ return theFilter; }

		/**
		 * Get the id of the filter
		 * @return the id
		 */
		public String getId() 		{ return theId; }

		/**
		 * Get the name of the filter
		 * @return the name
		 */
		public String getName() 	{ return theName; }

		/**
		 * Get the description of the filter
		 * @return the description
		 */
		public String getDesc() 	{ return theDesc; }

		/**
		 * Constructor
		 * @param pFilter the underlying filter
		 */
		private Filter(RemoteFilter pFilter) {
			/* Access the details */
			theFilter	= pFilter;
			theId 		= pFilter.getId();
			theName 	= pFilter.getName();
			theDesc 	= pFilter.getDescription();
		}
	}
}
