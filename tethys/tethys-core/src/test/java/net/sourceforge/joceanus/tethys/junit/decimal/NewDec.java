package net.sourceforge.joceanus.tethys.junit.decimal;

import net.sourceforge.joceanus.tethys.decimal.TethysNewDecimal;

public class NewDec {
    public static void main(final String[] pArgs) {
        /* Create a new decimal */
        TethysNewDecimal myFirst = new TethysNewDecimal(2, 9, -1, 2);

        /* Create a new decimal */
        TethysNewDecimal mySecond = new TethysNewDecimal(2, 92, -1, 2);

        myFirst.add(mySecond);
        myFirst = null;
    }
}
