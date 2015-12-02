/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jthemis.scm.maven;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.scm.maven.MvnProjectId.ProjectList;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Represents the definition of a project.
 * @author Tony Washer
 */
public class MvnProjectDefinition
        implements MetisDataContents {
    /**
     * POM name.
     */
    public static final String POM_NAME = "pom.xml";

    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MvnProjectDefinition.class.getSimpleName());

    /**
     * Id field id.
     */
    private static final MetisField FIELD_ID = FIELD_DEFS.declareEqualityField("Id");

    /**
     * Dependencies field id.
     */
    private static final MetisField FIELD_DEPS = FIELD_DEFS.declareEqualityField("Dependencies");

    /**
     * POM Model representation.
     */
    private Model theModel;

    /**
     * Main module identity.
     */
    private final MvnProjectId theDefinition;

    /**
     * Dependency identities.
     */
    private final ProjectList theDependencies;

    /**
     * SubModules.
     */
    private final List<MvnSubModule> theSubModules;

    /**
     * Constructor.
     * @param pInput the project definition file as input stream
     * @throws OceanusException on error
     */
    public MvnProjectDefinition(final InputStream pInput) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Parse the Project definition */
            MavenXpp3Reader myReader = new MavenXpp3Reader();
            theModel = myReader.read(pInput);

            /* Obtain the major definition */
            theDefinition = new MvnProjectId(theModel);

            /* Create the dependency list */
            theDependencies = new ProjectList();
            parseDependencies();

            /* Create the submodule list */
            theSubModules = new ArrayList<MvnSubModule>();
            parseSubModules();

            /* Catch exceptions */
        } catch (IOException | XmlPullParserException e) {
            /* Throw Exception */
            throw new JThemisIOException("Failed to parse Project file", e);
        }
    }

    @Override
    public String formatObject() {
        return theDefinition.formatObject();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_ID.equals(pField)) {
            return theDefinition;
        }
        if (FIELD_DEPS.equals(pField)) {
            return theDependencies;
        }

        /* Unknown */
        return MetisFieldValue.UNKNOWN;
    }

    /**
     * Get Project Identity.
     * @return the project identity
     */
    public MvnProjectId getDefinition() {
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
     * Get SubModules iterator.
     * @return the iterator
     */
    public Iterator<MvnSubModule> subIterator() {
        return theSubModules.iterator();
    }

    /**
     * Parse disk POM file.
     * @param pFile file to load
     * @return project definition.
     * @throws OceanusException on error
     */
    public static MvnProjectDefinition parseProjectFile(final File pFile) throws OceanusException {
        /* Protect against exceptions */
        try (FileInputStream myInFile = new FileInputStream(pFile);
             BufferedInputStream myInBuffer = new BufferedInputStream(myInFile)) {

            /* Parse the project definition */
            return new MvnProjectDefinition(myInBuffer);

            /* Catch exceptions */
        } catch (OceanusException
                | IOException e) {
            /* Throw Exception */
            throw new JThemisIOException("Failed to load Project file for " + pFile.getAbsolutePath(), e);
        }
    }

    /**
     * Parse dependencies.
     * @throws OceanusException on error
     */
    private void parseDependencies() throws OceanusException {
        /* Obtain the dependency list */
        List<Dependency> myDependencies = theModel.getDependencies();

        /* Iterate through the dependencies */
        Iterator<Dependency> myIterator = myDependencies.iterator();
        while (myIterator.hasNext()) {
            Dependency myDependency = myIterator.next();

            /* Build new project Id */
            MvnProjectId myDep = new MvnProjectId(myDependency, theDefinition);
            theDependencies.add(myDep);
        }
    }

    /**
     * Parse modules.
     */
    private void parseSubModules() {
        /* Obtain the module list */
        List<String> myModules = theModel.getModules();

        /* Iterate through the modules */
        Iterator<String> myIterator = myModules.iterator();
        while (myIterator.hasNext()) {
            String myModule = myIterator.next();

            /* Build new SubModule */
            MvnSubModule mySub = new MvnSubModule(myModule);
            theSubModules.add(mySub);
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
        return (myFile.exists())
                                ? myFile
                                : null;
    }

    /**
     * Write to file stream.
     * @param pFile the file to write to
     * @throws OceanusException on error
     */
    public void writeToFile(final File pFile) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* delete the file if it exists */
            if (pFile.exists() && !pFile.delete()) {
                throw new JThemisIOException("Failed to delete existing file");
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
            throw new JThemisIOException("Failed to write Project file", e);
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
    public void setNewVersion(final MvnProjectId pProjectId) {
        /* Loop through dependencies */
        Iterator<MvnProjectId> myIterator = theDependencies.iterator();
        while (myIterator.hasNext()) {
            /* Access dependency and set version */
            MvnProjectId myRef = myIterator.next();
            myRef.setNewVersion(pProjectId);
        }
    }

    /**
     * SubModule.
     */
    public static final class MvnSubModule {
        /**
         * Module name.
         */
        private final String theName;

        /**
         * Project Definition.
         */
        private MvnProjectDefinition theProject;

        /**
         * Constructor.
         * @param pName the name of the subModule
         */
        private MvnSubModule(final String pName) {
            theName = pName;
        }

        /**
         * Obtain the name.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Obtain the project definition.
         * @return the definition
         */
        public MvnProjectDefinition getProjectDefinition() {
            return theProject;
        }

        /**
         * Set the project definition.
         * @param pDef the definition
         */
        public void setProjectDefinition(final MvnProjectDefinition pDef) {
            theProject = pDef;
        }
    }
}
