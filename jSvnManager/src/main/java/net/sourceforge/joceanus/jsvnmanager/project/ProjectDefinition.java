/*******************************************************************************
 * jSvnManager: Java SubVersion Management
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jSvnManager.project;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jSvnManager.project.ProjectId.ProjectList;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Represents the definition of a project.
 * @author Tony Washer
 */
public class ProjectDefinition implements JDataContents {
    /**
     * POM name.
     */
    public static final String POM_NAME = "pom.xml";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(ProjectDefinition.class.getSimpleName());

    /**
     * Id field id.
     */
    private static final JDataField FIELD_ID = FIELD_DEFS.declareEqualityField("Id");

    /**
     * Dependencies field id.
     */
    private static final JDataField FIELD_DEPS = FIELD_DEFS.declareEqualityField("Dependencies");

    @Override
    public String formatObject() {
        return theDefinition.formatObject();
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_ID.equals(pField)) {
            return theDefinition;
        }
        if (FIELD_DEPS.equals(pField)) {
            return theDependencies;
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
    }

    /**
     * POM Model representation.
     */
    private Model theModel;

    /**
     * Main module identity.
     */
    private final ProjectId theDefinition;

    /**
     * Dependency identities.
     */
    private final ProjectList theDependencies;

    /**
     * Get Model.
     * @return the model
     */
    private Model getModel() {
        return theModel;
    }

    /**
     * Get Project Identity.
     * @return the project identity
     */
    public ProjectId getDefinition() {
        return theDefinition;
    }

    /**
     * Get Dependencies.
     * @return the project dependencies
     */
    public ProjectList getDependencies() {
        return theDependencies;
    }

    /**
     * Parse disk POM file.
     * @param pFile file to load
     * @return project definition.
     * @throws JDataException on error
     */
    public static ProjectDefinition parseProjectFile(final File pFile) throws JDataException {
        FileInputStream myInFile;
        BufferedInputStream myInBuffer = null;

        /* Protect against exceptions */
        try {
            /* Read the file */
            myInFile = new FileInputStream(pFile);
            myInBuffer = new BufferedInputStream(myInFile);

            /* Parse the project definition */
            return new ProjectDefinition(myInBuffer);

            /* Catch exceptions */
        } catch (JDataException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to load Project file for "
                    + pFile.getAbsolutePath(), e);

        } catch (IOException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to load Project file for "
                    + pFile.getAbsolutePath(), e);

        } finally {
            if (myInBuffer != null) {
                try {
                    myInBuffer.close();
                } catch (IOException i) {
                    myInBuffer = null;
                }
            }
        }
    }

    /**
     * Parse project definition file.
     * @param pInput the project definition file as input stream
     * @throws JDataException on error
     */
    public ProjectDefinition(final InputStream pInput) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Parse the Project definition */
            MavenXpp3Reader myReader = new MavenXpp3Reader();
            theModel = myReader.read(pInput);

            /* Obtain the major definition */
            theDefinition = new ProjectId(theModel);

            /* Create the dependency list */
            theDependencies = new ProjectList();

            /* Parse the dependencies */
            parseDependencies();

            /* Catch exceptions */
        } catch (IOException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to parse Project file", e);
        } catch (XmlPullParserException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to parse Project file", e);
        }
    }

    /**
     * Constructor for a duplicate project definition.
     * @param pDefinition the definition to copy
     * @throws JDataException on error
     */
    public ProjectDefinition(final ProjectDefinition pDefinition) throws JDataException {
        /* Clone the old model */
        theModel = pDefinition.getModel().clone();

        /* Copy project Id and dependencies */
        theDefinition = new ProjectId(theModel);
        theDependencies = new ProjectList();
        parseDependencies();
    }

    /**
     * Parse dependencies.
     * @throws JDataException on error
     */
    private void parseDependencies() throws JDataException {
        /* Obtain the dependency list */
        List<Dependency> myDependencies = theModel.getDependencies();

        /* Iterate through the dependencies */
        Iterator<Dependency> myIterator = myDependencies.iterator();
        while (myIterator.hasNext()) {
            Dependency myDependency = myIterator.next();

            /* Build new project Id */
            ProjectId myDep = new ProjectId(myDependency);
            theDependencies.add(myDep);
        }
    }

    /**
     * Obtain project definition file for location.
     * @param pLocation the location of the project
     * @return the project definition file or null
     */
    public static File getProjectDefFile(final File pLocation) {
        /* Build the file */
        File myFile = new File(pLocation, POM_NAME);

        /* Return the file */
        return (myFile.exists()) ? myFile : null;
    }

    /**
     * Write to file stream.
     * @param pFile the file to write to
     * @throws JDataException on error
     */
    public void writeToFile(final File pFile) throws JDataException {
        /* Protect against exceptions */
        try {
            /* delete the file if it exists */
            if (pFile.exists()) {
                pFile.delete();
            }

            /* Create the output streams */
            FileOutputStream myOutFile = new FileOutputStream(pFile);
            BufferedOutputStream myOutBuffer = new BufferedOutputStream(myOutFile);

            /* Parse the Project definition */
            MavenXpp3Writer myWriter = new MavenXpp3Writer();
            myWriter.write(myOutBuffer, theModel);

            /* Close the output file */
            myOutBuffer.close();

            /* Catch exceptions */
        } catch (IOException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to write Project file", e);
        }
    }

    /**
     * Set Snapshot Version.
     * @param pVersion the version
     */
    public void setSnapshotVersion(final String pVersion) {
        theDefinition.setSnapshotVersion(pVersion);
    }

    /**
     * Set New Version.
     * @param pVersion the version
     */
    public void setVersion(final String pVersion) {
        theDefinition.setVersion(pVersion);
    }

    /**
     * Set New Version for dependencies.
     * @param pProjectId the project Id
     */
    public void setNewVersion(final ProjectId pProjectId) {
        /* Loop through dependencies */
        Iterator<ProjectId> myIterator = theDependencies.iterator();
        while (myIterator.hasNext()) {
            /* Access dependency and set version */
            ProjectId myRef = myIterator.next();
            myRef.setNewVersion(pProjectId);
        }
    }
}
