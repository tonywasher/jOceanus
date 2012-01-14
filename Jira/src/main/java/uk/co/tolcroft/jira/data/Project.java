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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import uk.co.tolcroft.jira.data.Security.User;
import uk.co.tolcroft.jira.data.Server.IssueType;
import uk.co.tolcroft.jira.soap.JiraSoapService;
import uk.co.tolcroft.jira.soap.RemoteComponent;
import uk.co.tolcroft.jira.soap.RemoteIssueType;
import uk.co.tolcroft.jira.soap.RemoteProject;
import uk.co.tolcroft.jira.soap.RemoteVersion;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;

public class Project {
	/**
	 * Self reference
	 */
	private final Project			theSelf	= this;

	/**
	 * Server
	 */
	private final Server			theServer;

	/**
	 * Service
	 */
	private final JiraSoapService	theService;

	/**
	 * Project 
	 */
	private final RemoteProject		theProject;

	/**
	 * Id of Project
	 */
	private final String			theId;

	/**
	 * Key of Project
	 */
	private final String			theKey;

	/**
	 * Name of Project
	 */
	private final String			theName;

	/**
	 * Description of Project
	 */
	private final String			theDesc;

	/**
	 * Project Lead
	 */
	private final User				theLead;

	/**
	 * Project URL
	 */
	private final String			theURL;

	/**
	 * IssueTypes
	 */
	private final List<IssueType>	theIssueTypes;

	/**
	 * Components
	 */
	private final List<Component>	theComponents;

	/**
	 * Versions
	 */
	private final List<Version>		theVersions;

	/**
	 * Get the underlying project
	 * @return the project
	 */
	public RemoteProject getProject() 	{ return theProject; }

	/**
	 * Get the id of the project
	 * @return the id
	 */
	public String getId() 				{ return theId; }

	/**
	 * Get the key of the project
	 * @return the key
	 */
	public String getKey() 				{ return theKey; }

	/**
	 * Get the name of the project
	 * @return the name
	 */
	public String getName() 			{ return theName; }

	/**
	 * Get the description of the project
	 * @return the description
	 */
	public String getDesc() 			{ return theDesc; }

	/**
	 * Get the lead of the project
	 * @return the lead
	 */
	public User getLead() 				{ return theLead; }

	/**
	 * Get the URL of the project
	 * @return the URL
	 */
	public String getURL() 				{ return theURL; }

	/**
	 * Constructor
	 * @param pServer the server
	 * @param pProject the underlying project
	 */
	protected Project(Server 		pServer,
					  RemoteProject	pProject) throws ModelException {
		/* Store parameters */
		theServer 	= pServer;
		theService 	= theServer.getService();

		/* Access parameters */
		theProject	= pProject;
		theId		= pProject.getId();
		theKey		= pProject.getKey();
		theName		= pProject.getName();
		theDesc		= pProject.getDescription();
		theURL		= pProject.getUrl();
		
		/* Determine the project lead */
		theLead		= theServer.getUser(pProject.getLead());
		
		/* Allocate the lists */
		theIssueTypes 	= new ArrayList<IssueType>();
		theComponents 	= new ArrayList<Component>();
		theVersions 	= new ArrayList<Version>();
		
		/* Load IssueTypes etc */
		loadIssueTypes();
		loadComponents();
		loadVersions();
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
     * Obtain Component
     * @param pId the id of the Component
     * @return the Component
     */
    public Component getComponent(String pId) throws ModelException {
    	/* Return an existing component if found in list */
    	Iterator<Component> myIterator = theComponents.iterator();
    	while (myIterator.hasNext()) {
    		Component myComp = myIterator.next();
    		if (myComp.getId().equals(pId)) return myComp;
    	}
    	
    	/* throw exception */
        throw new ModelException(ExceptionClass.JIRA,
        						 "Invalid ComponentId " + pId);
    }
    
	/**
     * Obtain Component by Name
     * @param pName the name of the Component
     * @return the Component
     */
    public Component getComponentByName(String pName) {
    	/* Return an existing component if found in list */
    	Iterator<Component> myIterator = theComponents.iterator();
    	while (myIterator.hasNext()) {
    		Component myComp = myIterator.next();
    		if (myComp.getName().equals(pName)) return myComp;
    	}
    	return null;
    }
    
	/**
     * Obtain Version
     * @param pId the id of the Version
     * @return the Version
     */
    public Version getVersion(String pId) throws ModelException {
    	/* Return an existing version if found in list */
    	Iterator<Version> myIterator = theVersions.iterator();
    	while (myIterator.hasNext()) {
    		Version myVers = myIterator.next();
    		if (myVers.getId().equals(pId)) return myVers;
    	}
    	
    	/* throw exception */
        throw new ModelException(ExceptionClass.JIRA,
        						 "Invalid VersionId " + pId);
    }
    
	/**
     * Obtain Version by Name
     * @param pName the name of the Version
     * @return the Version
     */
    public Version getVersionByName(String pName) {
    	/* Return an existing component if found in list */
    	Iterator<Version> myIterator = theVersions.iterator();
    	while (myIterator.hasNext()) {
    		Version myVers = myIterator.next();
    		if (myVers.getName().equals(pName)) return myVers;
    	}
    	return null;
    }
    
    /**
     * Load IssueTypes
     */
    private void loadIssueTypes() throws ModelException {
    	/* Protect against exceptions */
    	try {
        	/* Access the authorization token */
    		String myToken = theServer.getAuthToken();
     		
    		/* Access the issue types */
    		RemoteIssueType[] myTypes = theService.getIssueTypesForProject(myToken, theId);
    		
    		/* Loop through the issue types */
    		for(RemoteIssueType myType: myTypes) {
    			/* Add new type to list */
    			theIssueTypes.add(theServer.getIssueType(myType.getId()));
    		}

    		/* Access the sub issue types */
    		myTypes = theService.getSubTaskIssueTypesForProject(myToken, theId);
    		
    		/* Loop through the issue types */
    		for(RemoteIssueType myType: myTypes) {
    			/* Add new type to list */
    			theIssueTypes.add(theServer.getIssueType(myType.getId()));
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
     * Load Components
     */
    private void loadComponents() throws ModelException {
    	/* Protect against exceptions */
    	try {
        	/* Access the authorization token */
    		String myToken = theServer.getAuthToken();
     		
    		/* Access the components */
    		RemoteComponent[] myComps = theService.getComponents(myToken, theKey);
    		
    		/* Loop through the components */
    		for(RemoteComponent myComp: myComps) {
    			/* Add new component to list */
    			theComponents.add(new Component(myComp));
    		}
    	}
    	catch (ModelException e) { throw e; }
        catch (RemoteException e)
        {
        	/* Pass the exception on */
            throw new ModelException(ExceptionClass.JIRA,
            						 "Failed to load components", 
            						 e);
        }
    }
    
    /**
     * Load Versions
     */
    private void loadVersions() throws ModelException {
    	/* Protect against exceptions */
    	try {
        	/* Access the authorization token */
    		String myToken = theServer.getAuthToken();
     		
    		/* Access the versions */
    		RemoteVersion[] myVers = theService.getVersions(myToken, theKey);
    		
    		/* Loop through the versions */
    		for(RemoteVersion myVersion: myVers) {
    			/* Add new version to list */
    			theVersions.add(new Version(myVersion));
    		}
    	}
    	catch (ModelException e) { throw e; }
        catch (RemoteException e)
        {
        	/* Pass the exception on */
            throw new ModelException(ExceptionClass.JIRA,
            						 "Failed to load versions", 
            						 e);
        }
    }
    
	/**
	 * Add new Version
	 * @param pVersion the version to add
	 */
	public void addVersion(String pVersion) throws ModelException {
    	/* Check whether the version is already linked */
    	Iterator<Version> myIterator = theVersions.iterator();
    	while (myIterator.hasNext()) {
    		Version myVers = myIterator.next();
    		if (myVers.getName().equals(pVersion)) return;
    	}
    	
		/* Protect against exceptions */
		try {
			/* Create the remote field array */
			RemoteVersion myVersion = new RemoteVersion();
			myVersion.setName(pVersion);
			
			/* Access the authorization token */
			String myToken = theServer.getAuthToken();		
		
			/* Add it to the issue */
			myVersion = theService.addVersion(myToken, theId, myVersion);
			
			/* Add the Version */
			theVersions.add(new Version(myVersion));
		}

		catch (ModelException e) { throw e; }
        catch (RemoteException e)
        {
        	/* Pass the exception on */
            throw new ModelException(ExceptionClass.JIRA,
            						 "Failed to link component", 
            						 e);
        }
	}

	/**
	 * Component class
	 */
	public class Component {
		/**
		 * The underlying remote component
		 */
		private final RemoteComponent	theComp;
		
		/**
		 * The id of the component
		 */
		private final String			theId;

		/**
		 * The name of the component
		 */
		private final String			theName;
		
		/**
		 * Get the underlying component
		 * @return the component
		 */
		public RemoteComponent getComp()	{ return theComp; }

		/**
		 * Get the id of the component
		 * @return the id
		 */
		public String getId() 				{ return theId; }

		/**
		 * Get the name of the component
		 * @return the name
		 */
		public String getName() 			{ return theName; }

		/**
		 * Constructor
		 * @param pComp the underlying component
		 */
		private Component(RemoteComponent pComp) {
			/* Access the details */
			theComp		= pComp;
			theId 		= pComp.getId();
			theName 	= pComp.getName();
		}
	}
    
    /**
	 * Version class
	 */
	public class Version {
		/**
		 * The underlying remote version
		 */
		private final RemoteVersion		theVers;
		
		/**
		 * The id of the component
		 */
		private final String			theId;

		/**
		 * The name of the component
		 */
		private final String			theName;
		
		/**
		 * Release Date of version
		 */
		private Calendar				theReleaseDate;

		/**
		 * is the version archived
		 */
		private boolean					isArchived;
		
		/**
		 * is the version released
		 */
		private boolean					isReleased;
		
		/**
		 * Get the underlying version
		 * @return the version
		 */
		public RemoteVersion getVersion()	{ return theVers; }

		/**
		 * Get the id of the version
		 * @return the id
		 */
		public String getId() 				{ return theId; }

		/**
		 * Get the name of the version
		 * @return the name
		 */
		public String getName() 			{ return theName; }

		/**
		 * Get the releaseDate of the version
		 * @return the releaseDate
		 */
		public Calendar getReleaseDate() 	{ return theReleaseDate; }

		/**
		 * Is the version archived
		 * @return true/false
		 */
		public boolean isArchived() 		{ return isArchived; }

		/**
		 * Is the version released
		 * @return true/false
		 */
		public boolean isReleased() 		{ return isReleased; }

		/**
		 * Constructor
		 * @param pVers the underlying version
		 */
		private Version(RemoteVersion pVers) {
			/* Access the details */
			theVers			= pVers;
			theId 			= pVers.getId();
			theName 		= pVers.getName();
			theReleaseDate	= pVers.getReleaseDate();
			isArchived		= pVers.isArchived();
			isReleased		= pVers.isReleased();
		}
		
		/**
		 * Archive the version
		 * @param doArchive archive/restore version
		 */
		public void setArchive(boolean doArchive) throws ModelException {
			/* Protect against exceptions */
			try {
				/* Ignore if already in correct state */
				if (doArchive == isArchived) return;
				
				/* Access the authorization token */
				String myToken = theServer.getAuthToken();		
		
				/* Call the service */
				theService.archiveVersion(myToken, theSelf.getId(), theId, doArchive);
				
				/* Update status */
				isArchived = doArchive;
				theVers.setArchived(doArchive);
			}

			catch (ModelException e) { throw e; }
	        catch (RemoteException e)
	        {
	        	/* Pass the exception on */
	            throw new ModelException(ExceptionClass.JIRA,
	            						 "Failed to archive version", 
	            						 e);
	        }
		}

		/**
		 * Release the version
		 */
		public void setReleased() throws ModelException {
			/* Protect against exceptions */
			try {
				/* Ignore if already in correct state */
				if (isReleased) return;
				
				/* Access the authorization token */
				String myToken = theServer.getAuthToken();		
		
				/* Update status */
				isReleased = true;
				isArchived = false;
				theReleaseDate = Calendar.getInstance();
				theVers.setReleased(true);
				theVers.setArchived(false);
				theVers.setReleaseDate(theReleaseDate);

				/* Call the service */
				theService.releaseVersion(myToken, theSelf.getId(), theVers);
			}

			catch (ModelException e) { throw e; }
	        catch (RemoteException e)
	        {
	        	/* Pass the exception on */
	            throw new ModelException(ExceptionClass.JIRA,
	            						 "Failed to archive version", 
	            						 e);
	        }
		}
	}
}
