/*
 * Themis: Java Project Framework
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.themis.parser.project;

import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadStatusReport;
import io.github.tonywasher.joceanus.themis.exc.ThemisIOException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisDataResource;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;
import io.github.tonywasher.joceanus.themis.parser.maven.ThemisMavenId;
import io.github.tonywasher.joceanus.themis.parser.maven.ThemisMavenLocation;
import io.github.tonywasher.joceanus.themis.parser.maven.ThemisMavenPom;
import io.github.tonywasher.joceanus.themis.parser.xmaven.ThemisXMavenId;
import io.github.tonywasher.joceanus.themis.parser.xmaven.ThemisXMavenParser;
import io.github.tonywasher.joceanus.themis.parser.xmaven.ThemisXMavenPom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Project.
 */
public class ThemisProject
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisProject> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisProject.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisDataResource.DATA_NAME, ThemisProject::getName);
        FIELD_DEFS.declareLocalField(ThemisDataResource.DATA_MODULES, ThemisProject::getModules);
        FIELD_DEFS.declareLocalField(ThemisDataResource.DATA_PARSEDMODULES, ThemisProject::getParsedModules);
    }

    /**
     * The project name.
     */
    private final String theName;

    /**
     * The location.
     */
    private final File theLocation;

    /**
     * The parser.
     */
    private final ThemisParserDef theParser;

    /**
     * The module map.
     */
    private final Map<ThemisMavenId, ThemisModule> theModules;

    /**
     * The Xmodule map.
     */
    private final Map<ThemisXMavenId, ThemisModule> theXModules;

    /**
     * The dependencies.
     */
    private final List<ThemisMavenId> theDependencies;

    /**
     * The Xdependencies.
     */
    private final List<ThemisXMavenId> theXDependencies;

    /**
     * The list of local modules.
     */
    private final List<ThemisXMavenPom> theParsedModules;

    /**
     * Constructor.
     *
     * @param pParser   the parser
     * @param pLocation the project location
     * @throws OceanusException on error
     */
    public ThemisProject(final ThemisParserDef pParser,
                         final File pLocation) throws OceanusException {
        /* Store the parser and location */
        theParser = pParser;
        theLocation = pLocation;

        /* Create the list */
        theModules = new LinkedHashMap<>();
        theXModules = new LinkedHashMap<>();
        theDependencies = new ArrayList<>();

        /* Build new Maven Map */
        final ThemisXMavenParser myParser = new ThemisXMavenParser(theParser.getReporter(), theLocation);
        theName = myParser.getName();
        theParsedModules = myParser.getParsedModules();
        theXDependencies = myParser.getProjectDependencies();
        for (ThemisXMavenPom myModule : theParsedModules) {
            theXModules.put(myModule.getId(), new ThemisModule(myModule));
        }

        /* Initiate search for modules */
        parseProjectFile(null, new File(theLocation, ThemisMavenPom.POM));

        /* Remove own mavenIds from dependency list */
        theDependencies.removeIf(theModules::containsKey);
        theXDependencies.removeIf(theXModules::containsKey);

        /* For all dependencies */
        final List<ThemisMavenId> myDependencies = new ArrayList<>(theDependencies);
        for (ThemisMavenId myId : myDependencies) {
            processDependency(myId);
        }
    }

    @Override
    public MetisFieldSet<ThemisProject> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return toString();
    }

    /**
     * Process dependency.
     *
     * @param pId the maven id
     */
    private void processDependency(final ThemisMavenId pId) throws OceanusException {
        /* Protect against exceptions */
        final File myFile = new File(ThemisMavenLocation.getLocalPomFileName(pId));
        try (InputStream myInStream = new FileInputStream(myFile)) {
            /* Parse the Project definition file */
            final ThemisMavenPom myPom = new ThemisMavenPom(null, myInStream);

            /* Add any unique dependencies */
            for (final ThemisMavenId myDepId : myPom.getDependencies()) {
                if (!theDependencies.contains(myDepId)
                        && myDepId.getVersion() != null) {
                    theDependencies.add(myDepId);
                    if (myDepId.getClassifier() == null) {
                        processDependency(myDepId);
                    }
                }
            }
        } catch (IOException e) {
            throw new ThemisIOException("Failed to parse pom file", e);
        }
    }

    /**
     * Obtain the name.
     *
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the location.
     *
     * @return the location
     */
    File getLocation() {
        return theLocation;
    }

    /**
     * Obtain the modules.
     *
     * @return the modules
     */
    public List<ThemisModule> getModules() {
        return new ArrayList<>(theModules.values());
    }

    /**
     * Obtain the modules.
     *
     * @return the modules
     */
    private List<ThemisXMavenPom> getParsedModules() {
        return theParsedModules;
    }

    /**
     * Obtain the dependencies.
     *
     * @return the dependencies
     */
    public List<ThemisMavenId> getDependencies() {
        return theDependencies;
    }

    @Override
    public String toString() {
        return theName;
    }

    /**
     * Parse the maven project file.
     *
     * @param pParent the parent
     * @param pPom    the project file
     * @return the artifact name
     * @throws OceanusException on error
     */
    private ThemisMavenPom parseProjectFile(final ThemisMavenPom pParent,
                                            final File pPom) throws OceanusException {
        /* If the pom file does not exist, just return */
        if (!pPom.exists()) {
            return null;
        }

        /* Protect against exceptions */
        try (InputStream myInStream = new FileInputStream(pPom)) {
            /* Parse the Project definition file */
            final ThemisMavenPom myPom = new ThemisMavenPom(pParent, myInStream);

            /* If source directory exists */
            final File mySrc = new File(pPom.getParent(), ThemisPackage.PATH_XTRA);
            if (mySrc.exists()
                    && mySrc.isDirectory()) {
                /* Add the module to the list */
                theModules.put(myPom.getMavenId(), new ThemisModule(new File(pPom.getParent()), myPom));

                /* Add any unique dependencies */
                for (final ThemisMavenId myDepId : myPom.getDependencies()) {
                    if (!theDependencies.contains(myDepId)) {
                        theDependencies.add(myDepId);
                    }
                }
            }

            /* Loop through the modules */
            for (final String myModuleName : myPom.getModules()) {
                /* Access module directory */
                final File myModuleDir = new File(pPom.getParentFile(), myModuleName);

                /* Process the project file */
                parseProjectFile(myPom, new File(myModuleDir, ThemisMavenPom.POM));
            }

            /* Return the POM */
            return myPom;

            /* Catch exceptions */
        } catch (IOException e) {
            /* Convert Exception */
            throw new ThemisIOException("Failed to parse Project file", e);
        }
    }

    /**
     * parse the java code.
     *
     * @throws OceanusException on error
     */
    public void parseJavaCode() throws OceanusException {
        /* Obtain the reporter */
        final TethysUIThreadStatusReport myReport = theParser.getReporter();
        myReport.initTask(ThemisDataResource.TASK_PARSECODE);
        myReport.setNumStages(theModules.size());

        /* Obtain the active profile */
        OceanusProfile myTask = myReport.getActiveTask();
        myTask = myTask.startTask(ThemisDataResource.TASK_PARSECODE);

        /* Loop through the modules */
        for (ThemisModule myModule : theModules.values()) {
            /* Process the module */
            final String myName = myModule.getName();
            myTask.startTask(myName);
            myReport.setNewStage(myName);
            myReport.setNumSteps(1);
            myReport.setNextStep();
            myModule.parseJavaCode(theParser);
        }

        /* End the task */
        myTask.end();
    }
}
