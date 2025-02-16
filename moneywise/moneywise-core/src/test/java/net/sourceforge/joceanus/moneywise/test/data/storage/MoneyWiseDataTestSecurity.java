package net.sourceforge.joceanus.moneywise.test.data.storage;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.prometheus.security.PrometheusSecurityPasswordManager;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

/**
 * Test security.
 */
public class MoneyWiseDataTestSecurity {
    /**
     * The DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * Constructor.
     * @param pDataSet the DataSet
     */
    public MoneyWiseDataTestSecurity(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
    }

    /**
     * Initialise security.
     * @param pView the view
     */
    public void initSecurity(final MoneyWiseView pView) throws OceanusException {
        /* Access the Password manager and disable prompting */
        final PrometheusSecurityPasswordManager myManager = theDataSet.getPasswordMgr();
        myManager.setDialogController(new MoneyWiseNullPasswordDialog());

        /* Create the cloneSet and initialise security */
        final MoneyWiseDataSet myNullData = pView.getNewData();

        /* Create the control data */
        final TethysUIThreadStatusReport myReport = new MoneyWiseNullThreadStatusReport();
        theDataSet.getControlData().addNewControl(0);
        theDataSet.initialiseSecurity(myReport, myNullData);
        theDataSet.reBase(myReport, myNullData);
    }
}
