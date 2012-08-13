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
package net.sourceforge.JSvnManager;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JPreferenceSet.PreferenceManager;
import net.sourceforge.JSvnManager.data.Branch;
import net.sourceforge.JSvnManager.data.Repository;
import net.sourceforge.JSvnManager.data.WorkingCopy.WorkingCopySet;
import net.sourceforge.JSvnManager.tasks.VersionMgr;

public class TestSubVersion {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Repository myRepository = new Repository(new PreferenceManager(), "Finance");
            WorkingCopySet myWorkingSet = myRepository.getWorkingSet();
            VersionMgr myVersionMgr = new VersionMgr(myRepository);
            Branch myBranch = myWorkingSet.getActiveBranch("JDateButton");
            myVersionMgr.createNextTag(myBranch, null);
        } catch (JDataException e) {
        }
    }
}
