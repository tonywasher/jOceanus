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
package net.sourceforge.joceanus.jthemis.svn.threads;

import java.util.List;

import javax.swing.SwingWorker;

import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportStatus;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportTask;
import net.sourceforge.joceanus.jthemis.scm.tasks.ScmStatus;

/**
 * Standard thread skeleton.
 */
public abstract class ScmThread
        extends SwingWorker<Void, ScmStatus>
        implements ReportStatus {
    /**
     * Report object.
     */
    private final ReportTask theReport;

    /**
     * Status object.
     */
    private final ScmStatus theStatus;

    /**
     * The Error.
     */
    private JOceanusException theError = null;

    /**
     * Obtain the error.
     * @return the error
     */
    public JOceanusException getError() {
        return theError;
    }

    /**
     * Set the error.
     * @param pError the error
     */
    public void setError(final JOceanusException pError) {
        theError = pError;
    }

    /**
     * Constructor.
     * @param pReport the report object
     */
    protected ScmThread(final ReportTask pReport) {
        theReport = pReport;
        theStatus = new ScmStatus();
    }

    @Override
    public boolean initTask(final String pTask) {
        theStatus.setTask(pTask);
        publish(new ScmStatus(theStatus));
        return !isCancelled();
    }

    @Override
    public boolean setNewStage(final String pStage) {
        theStatus.setNewStage(pStage);
        publish(new ScmStatus(theStatus));
        return !isCancelled();
    }

    @Override
    public boolean setNewStep(final String pStep) {
        theStatus.setNewStep(pStep);
        publish(new ScmStatus(theStatus));
        return !isCancelled();
    }

    @Override
    public boolean setNumStages(final int pNumStages) {
        theStatus.setNumStages(pNumStages);
        return !isCancelled();
    }

    @Override
    public boolean setNumSteps(final int pNumSteps) {
        theStatus.setNumSteps(pNumSteps);
        return !isCancelled();
    }

    @Override
    public boolean setStepsDone(final int pNumSteps) {
        theStatus.setStepsDone(pNumSteps);
        publish(new ScmStatus(theStatus));
        return !isCancelled();
    }

    @Override
    public void process(final List<ScmStatus> pStatus) {
        for (ScmStatus myStatus : pStatus) {
            theReport.setNewStatus(myStatus);
        }
    }
}
