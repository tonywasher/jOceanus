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
package uk.co.tolcroft.subversion;

import net.sourceforge.JDataManager.ModelException;
import uk.co.tolcroft.subversion.data.Branch;
import uk.co.tolcroft.subversion.data.Repository;
import uk.co.tolcroft.subversion.data.WorkingCopy.WorkingCopySet;
import uk.co.tolcroft.subversion.tasks.VersionMgr;

public class TestSubVersion {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Repository myRepository = new Repository("Finance");
            WorkingCopySet myWorkingSet = myRepository.getWorkingSet();
            VersionMgr myVersionMgr = new VersionMgr(myRepository);
            Branch myBranch = myWorkingSet.getActiveBranch("JDateButton");
            myVersionMgr.createNextTag(myBranch, null);
        } catch (ModelException e) {
        }
    }
}
