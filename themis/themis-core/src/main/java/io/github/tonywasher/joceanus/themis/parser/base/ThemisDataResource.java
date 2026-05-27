/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.themis.parser.base;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleLoader;

import java.util.ResourceBundle;

public enum ThemisDataResource
        implements OceanusBundleId, MetisDataFieldId {
    /**
     * Data Source.
     */
    DATA_SOURCE("source"),

    /**
     * Data Solver.
     */
    DATA_SOLVER("solver"),

    /**
     * Data Stats.
     */
    DATA_STATS("stats"),

    /**
     * Data Name.
     */
    DATA_NAME("name"),

    /**
     * Data Project.
     */
    DATA_PROJECT("project"),

    /**
     * Data Module.
     */
    DATA_MODULE("module"),

    /**
     * Data Modules.
     */
    DATA_MODULES("modules"),

    /**
     * Data Packages.
     */
    DATA_PACKAGES("packages"),

    /**
     * Data Files.
     */
    DATA_FILES("files"),

    /**
     * Data Class.
     */
    DATA_CLASS("class"),

    /**
     * Data Classes.
     */
    DATA_CLASSES("classes"),

    /**
     * Data Methods.
     */
    DATA_METHODS("methods"),

    /**
     * Data Parent.
     */
    DATA_PARENT("parent"),

    /**
     * Data Children.
     */
    DATA_CHILDREN("children"),

    /**
     * Data Underlying.
     */
    DATA_UNDERLYING("underlying"),

    /**
     * Data Totals.
     */
    DATA_TOTALS("totals"),

    /**
     * Data RefType.
     */
    DATA_REFTYPE("refType"),

    /**
     * Data References.
     */
    DATA_REFERENCES("references"),

    /**
     * Data Referenced.
     */
    DATA_REFERENCED("referenced"),

    /**
     * Data LocalRefs.
     */
    DATA_LOCALREFS("localRefs"),

    /**
     * Data ImpliedRefs.
     */
    DATA_IMPLIEDREFS("impliedRefs"),

    /**
     * Data Standard.
     */
    DATA_STANDARD("standard"),

    /**
     * Data Circular.
     */
    DATA_CIRCULAR("circular"),

    /**
     * Data Incestuous.
     */
    DATA_INCESTUOUS("incestuous"),

    /**
     * Data GroupId.
     */
    DATA_GROUPID("groupId"),

    /**
     * Data ArtifactId.
     */
    DATA_ARTIFACTID("artifactId"),

    /**
     * Data Version.
     */
    DATA_VERSION("version"),

    /**
     * Data Classifier.
     */
    DATA_CLASSIFIER("classifier"),

    /**
     * Data Scope.
     */
    DATA_SCOPE("scope"),

    /**
     * Data Optional.
     */
    DATA_OPTIONAL("optional"),

    /**
     * Data Optional.
     */
    DATA_ID("id"),

    /**
     * Data Properties.
     */
    DATA_PROPERTIES("properties"),

    /**
     * Data xtraDirs.
     */
    DATA_XTRADIRS("xtraDirs"),

    /**
     * Data Versions.
     */
    DATA_VERSIONS("versions"),

    /**
     * Data Dependencies.
     */
    DATA_DIRECTDEPENDENCIES("directDependencies"),

    /**
     * Data Dependencies.
     */
    DATA_DEPENDENCIES("dependencies"),

    /**
     * Data ParsedModules.
     */
    DATA_PARSEDMODULES("parsedModules"),

    /**
     * Task DataLoad.
     */
    TASK_DATALOAD("task.dataLoad"),

    /**
     * Task ParseCode.
     */
    TASK_PARSECODE("task.parseCode"),

    /**
     * Task Solver.
     */
    TASK_SOLVER("task.solver"),

    /**
     * Task SolverPreProcess.
     */
    TASK_SOLVERPREPROCESS("task.solverPreProcess"),

    /**
     * Task SolverProcess.
     */
    TASK_SOLVERPROCESS("task.solverProcess"),

    /**
     * Task Resolving.
     */
    TASK_RESOLVING("task.resolving"),

    /**
     * Task Discover.
     */
    TASK_DISCOVER("task.discover"),

    /**
     * Task Discover Local.
     */
    TASK_DISCOVERLOCAL("task.discoverLocal"),

    /**
     * Task Discover Dependency.
     */
    TASK_DISCOVERDEPENDENCY("task.discoverDependency");

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(ThemisDataResource.class.getCanonicalName(),
            ResourceBundle::getBundle);

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     *
     * @param pKeyName the key name
     */
    ThemisDataResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "Themis.data";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    @Override
    public String getId() {
        return getValue();
    }
}
