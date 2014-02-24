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
package net.sourceforge.joceanus.jthemis.git;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * Git test suite.
 */
public class TestGit {
    /**
     * Main entry point.
     * @param args the parameters
     */
    public static void main(String[] args) {
        try {
            /* Access repository */
            FileRepositoryBuilder myBuilder = new FileRepositoryBuilder();
            myBuilder.setWorkTree(new File("c:\\Users\\Tony\\GitTest"));
            myBuilder.readEnvironment();
            myBuilder.findGitDir();
            Repository myRepo = myBuilder.build();

            /* Create access */
            Git myGit = new Git(myRepo);
            PersonIdent myRepoId = new PersonIdent(myRepo);

            /* Declare all files to git */
            AddCommand myAdd = myGit.add();
            myAdd.addFilepattern(".");
            myAdd.call();

            /* Perform a commit */
            CommitCommand myCommit = myGit.commit();
            myCommit.setCommitter(new PersonIdent(myRepoId, new Date()));
            myCommit.setMessage("Trial commit");
            RevCommit myCommitId = myCommit.call();

            /* Create a branch from this point */
            CreateBranchCommand myBranch = myGit.branchCreate();
            myBranch.setStartPoint(myCommitId);
            myBranch.setName("v1.0.0");
            myBranch.call();

            /* Check the branch out */
            CheckoutCommand myCheckout = myGit.checkout();
            myCheckout.setName("v1.0.0");
            myCheckout.call();

            /* Create changes */
            /* Declare changes */
            /* Commit the branch */

            /* Check out a particular commit leaving detached head */
            myCheckout = myGit.checkout();
            myCheckout.setAllPaths(true);
            myCheckout.setStartPoint(myCommitId);
            myCheckout.call();

            /* Create changes */
            /* Declare changes */
            /* Commit the detached head */

            /* Create the tag */
            TagCommand myTag = myGit.tag();
            myTag.setName("v1.0.0-b1");
            myTag.setTagger(new PersonIdent(myRepoId, new Date()));
            myTag.setMessage("New Tag");
            myTag.call();

            System.exit(0);
        } catch (IOException
                | GitAPIException e) {
        }
    }

}
