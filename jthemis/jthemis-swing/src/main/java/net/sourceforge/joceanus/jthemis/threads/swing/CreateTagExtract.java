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
package net.sourceforge.joceanus.jthemis.threads.swing;

import java.io.File;
import java.util.Collection;

import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportTask;
import net.sourceforge.joceanus.jthemis.scm.tasks.Directory2;
import net.sourceforge.joceanus.jthemis.svn.data.SvnRepository;
import net.sourceforge.joceanus.jthemis.svn.data.SvnTag;
import net.sourceforge.joceanus.jthemis.svn.tasks.CheckOut;

/**
 * Thread to handle creation of working copy.
 * @author Tony Washer
 */
public class CreateTagExtract
        extends ScmThread {
    /**
     * Tags.
     */
    private final Collection<SvnTag> theTags;

    /**
     * Location.
     */
    private final File theLocation;

    /**
     * Report object.
     */
    private final ReportTask theReport;

    /**
     * The Repository.
     */
    private final SvnRepository theRepository;

    /**
     * The Error.
     */
    private JOceanusException theError = null;

    /**
     * Constructor.
     * @param pTags the tags to create the extract for
     * @param pLocation the location to create into
     * @param pReport the report object
     */
    public CreateTagExtract(final SvnTag[] pTags,
                            final File pLocation,
                            final ReportTask pReport) {
        /* Call super-constructor */
        super(pReport);

        /* Store parameters */
        theLocation = pLocation;
        theReport = pReport;
        theRepository = pTags[0].getRepository();

        /* protect against exceptions */
        try {
            /* Create new directory for extract */
            Directory2.createDirectory(pLocation);

            /* Access tag list for extract */
            // myTags = SvnTag.getTagMap(pTags).values();
        } catch (JOceanusException e) {
            /* Store the error and cancel thread */
            theError = e;
            cancel(true);
        }

        /* Record tags */
        theTags = null;
    }

    @Override
    public JOceanusException getError() {
        return theError;
    }

    @Override
    protected Void doInBackground() {
        /* Protect against exceptions */
        try {
            /* Check out the branches */
            CheckOut myCheckOut = new CheckOut(theRepository, this);
            myCheckOut.exportTags(theTags, theLocation);
        } catch (JOceanusException e) {
            /* Store the error */
            theError = e;
        } finally {
            /* Dispose of any connections */
            if (theRepository != null) {
                theRepository.dispose();
            }
        }

        /* Return null */
        return null;
    }

    @Override
    public void done() {
        /* Report task complete */
        theReport.completeTask(this);
    }
}