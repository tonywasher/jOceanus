/*
 * Tethys: GUI Utilities
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
package io.github.tonywasher.joceanus.tethys.api.factory;

import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIIcon;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIIconId;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIProgram;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIType;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIValueSet;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIButtonFactory;
import io.github.tonywasher.joceanus.tethys.api.chart.TethysUIChartFactory;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUIControlFactory;
import io.github.tonywasher.joceanus.tethys.api.dialog.TethysUIDialogFactory;
import io.github.tonywasher.joceanus.tethys.api.field.TethysUIFieldFactory;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIMenuFactory;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIPaneFactory;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableFactory;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadFactory;

/**
 * UI Factory.
 *
 * @param <C> the color
 */
public interface TethysUIFactory<C> {
    /**
     * Obtain the gui type.
     *
     * @return the type
     */
    TethysUIType getGUIType();

    /**
     * Obtain the formatter.
     *
     * @return the formatter
     */
    OceanusDataFormatter getDataFormatter();

    /**
     * Obtain a new formatter.
     *
     * @return the formatter
     */
    OceanusDataFormatter newDataFormatter();

    /**
     * Obtain the valueSet.
     *
     * @return the valueSet
     */
    TethysUIValueSet getValueSet();

    /**
     * Resolve Icon.
     *
     * @param pIconId the mapped IconId
     * @param pWidth  the icon width
     * @return the icon
     */
    TethysUIIcon resolveIcon(TethysUIIconId pIconId, int pWidth);

    /**
     * Obtain the logSink.
     *
     * @return the logSink
     */
    TethysUILogTextArea getLogSink();

    /**
     * Activate logSink.
     */
    void activateLogSink();

    /**
     * Obtain the program definition.
     *
     * @return the definition
     */
    TethysUIProgram getProgramDefinitions();

    /**
     * Create new profile.
     *
     * @param pTask the name of the task
     * @return the new profile
     */
    OceanusProfile getNewProfile(String pTask);

    /**
     * Obtain the active profile.
     *
     * @return the active profile
     */
    OceanusProfile getActiveProfile();

    /**
     * Obtain the active task.
     *
     * @return the active task
     */
    OceanusProfile getActiveTask();

    /**
     * Obtain the button factory.
     *
     * @return the factory
     */
    TethysUIButtonFactory<C> buttonFactory();

    /**
     * Obtain the chart factory.
     *
     * @return the factory
     */
    TethysUIChartFactory chartFactory();

    /**
     * Obtain the control factory.
     *
     * @return the factory
     */
    TethysUIControlFactory controlFactory();

    /**
     * Obtain the dialog factory.
     *
     * @return the factory
     */
    TethysUIDialogFactory dialogFactory();

    /**
     * Obtain the field factory.
     *
     * @return the factory
     */
    TethysUIFieldFactory fieldFactory();

    /**
     * Obtain the menu factory.
     *
     * @return the factory
     */
    TethysUIMenuFactory menuFactory();

    /**
     * Obtain the pane factory.
     *
     * @return the factory
     */
    TethysUIPaneFactory paneFactory();

    /**
     * Obtain the thread factory.
     *
     * @return the factory
     */
    TethysUITableFactory tableFactory();

    /**
     * Obtain the thread factory.
     *
     * @return the factory
     */
    TethysUIThreadFactory threadFactory();
}
