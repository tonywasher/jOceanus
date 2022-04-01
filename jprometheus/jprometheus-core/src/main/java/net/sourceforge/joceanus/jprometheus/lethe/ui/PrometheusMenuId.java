/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.lethe.ui;

/**
 * Prometheus Thread Ids.
 */
public enum PrometheusMenuId {
    /**
     * Data Menu.
     */
    DATA(PrometheusUIResource.MENU_DATA),

    /**
     * Backup Menu.
     */
    BACKUP(PrometheusUIResource.MENU_BACKUP),

    /**
     * Edit Menu.
     */
    EDIT(PrometheusUIResource.MENU_EDIT),

    /**
     * Security Menu.
     */
    SECURITY(PrometheusUIResource.MENU_SECURITY),

    /**
     * Help Menu.
     */
    HELP(PrometheusUIResource.MENU_HELP),

    /**
     * UnDo Task.
     */
    UNDO(PrometheusUIResource.MENUITEM_UNDO),

    /**
     * Reset Task.
     */
    RESET(PrometheusUIResource.MENUITEM_RESET),

    /**
     * Help Task.
     */
    SHOWHELP(PrometheusUIResource.MENUITEM_HELP),

    /**
     * DataViewer Task.
     */
    DATAVIEWER(PrometheusUIResource.MENUITEM_DATAVIEWER),

    /**
     * About Task.
     */
    ABOUT(PrometheusUIResource.MENUITEM_ABOUT);

    /**
     * The name.
     */
    private final String theName;

    /**
     * Constructor.
     * @param pId the id
     */
    PrometheusMenuId(final PrometheusUIResource pId) {
        theName = pId.getValue();
    }

    @Override
    public String toString() {
        return theName;
    }
}
