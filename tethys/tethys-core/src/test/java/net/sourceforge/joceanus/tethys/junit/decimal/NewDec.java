package net.sourceforge.joceanus.tethys.junit.decimal;

import net.sourceforge.joceanus.oceanus.decimal.OceanusNewDecimal;

public class NewDec {
    public static void main(final String[] pArgs) {
        /* Create a new decimal */
        OceanusNewDecimal myFirst = new OceanusNewDecimal(2, 9, -1, 2);

        /* Create a new decimal */
        OceanusNewDecimal mySecond = new OceanusNewDecimal(2, 92, -1, 2);

        myFirst.add(mySecond);
        myFirst = null;
    }
}
