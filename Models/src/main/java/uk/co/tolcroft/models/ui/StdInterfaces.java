/*******************************************************************************
 * JDataModel: Data models
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
package uk.co.tolcroft.models.ui;

import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import uk.co.tolcroft.models.data.EditState;

/**
 * Standard panel interfaces.
 * @author Tony Washer
 */
public class StdInterfaces {
    /**
     * Standard commands.
     */
    public enum stdCommand {
        /**
         * OK.
         */
        OK,

        /**
         * Reset all changes.
         */
        RESETALL;
    }

    /**
     * Standard panel.
     */
    public interface StdPanel {
        /**
         * Notify selection.
         * @param o source object
         */
        void notifySelection(final Object o);

        /**
         * Notify changes.
         */
        void notifyChanges();

        /**
         * Does the panel have updates?
         * @return true/false
         */
        boolean hasUpdates();

        /**
         * Print the panel.
         */
        void printIt();

        /**
         * Is the panel locked?
         * @return true/false
         */
        boolean isLocked();

        /**
         * Perform the command.
         * @param pCmd the command
         */
        void performCommand(final stdCommand pCmd);

        /**
         * Get the edit state.
         * @return the edit state
         */
        EditState getEditState();

        /**
         * Get the data manager.
         * @return the data manager
         */
        JDataManager getDataManager();

        /**
         * Get the data entry.
         * @return the data entry
         */
        JDataEntry getDataEntry();

        /**
         * Lock/unlock on error.
         * @param isError true/false
         */
        void lockOnError(final boolean isError);
    }
}
