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
import io.github.tonywasher.joceanus.themis.parser.base.ThemisDataResource;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;
import io.github.tonywasher.joceanus.themis.parser.maven.ThemisMavenId;
import io.github.tonywasher.joceanus.themis.parser.maven.ThemisMavenParser;
import io.github.tonywasher.joceanus.themis.parser.maven.ThemisMavenPom;

import java.io.File;
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
     * The dependencies.
     */
    private final List<ThemisMavenId> theDependencies;

    /**
     * The list of local modules.
     */
    private final List<ThemisMavenPom> theParsedModules;

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

        /* Build new Maven Map */
        final ThemisMavenParser myParser = new ThemisMavenParser(theParser.getReporter(), theLocation);
        theName = myParser.getName();
        theParsedModules = myParser.getParsedModules();
        theDependencies = myParser.getProjectDependencies();
        for (ThemisMavenPom myModule : theParsedModules) {
            theModules.put(myModule.getId(), new ThemisModule(myModule));
        }

        /* Remove own mavenIds from dependency list */
        theDependencies.removeIf(theModules::containsKey);
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
    private List<ThemisMavenPom> getParsedModules() {
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
