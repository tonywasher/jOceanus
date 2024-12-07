package net.sourceforge.joceanus.oceanus.junit.decimal;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateFormatter;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

/**
 * Decimal JUnit Tests.
 */
class TestDecimal {
    /**
     * Create the analysis test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    Stream<DynamicNode> decimalTests() throws OceanusException {
        return Stream.of(
                DynamicTest.dynamicTest("checkAdd", TestDecimal::additions),
                DynamicTest.dynamicTest("checkSubtract", TestDecimal::subtractions),
                DynamicTest.dynamicTest("checkMultiply", TestDecimal::multiplications),
                DynamicTest.dynamicTest("checkDivide", TestDecimal::divisions),
                DynamicTest.dynamicTest("checkBytes", TestDecimal::checkBytes)
        );
    }

    /**
     * Addition tests.
     */
    private static void additions() {
        checkAddition("1.0003", "2.156");
        checkAddition("1.0003", "-2.156");
        checkAddition("1.0103", "2.156888");
        checkAddition("1.0103", "-2.156888");
    }

    /**
     * Subtraction tests.
     */
    private static void subtractions() {
        checkSubtraction("1.0003", "2.156");
        checkSubtraction("1.0003", "-2.156");
        checkSubtraction("1.0103", "2.156888");
        checkSubtraction("1.0103", "-2.156888");
    }

    /**
     * Multiplication tests.
     */
    private static void multiplications() {
        checkMultiplication("1.0003", "2.156");
        checkMultiplication("1.0003", "-2.156");
        checkMultiplication("11.4567", "67.78657");
        checkMultiplication("11.4567", "-67.78657");
    }

    /**
     * Division tests.
     */
    private static void divisions() {
        checkDivision("1.0003", "2.156");
        checkDivision("1.0003", "-2.156");
        checkDivision("1234.0001675", "1234.0001676");
        checkDivision("1234.00016766", "1234.00016767");
        checkDivision("1234.000167677", "1234.000167678");
    }

    /**
     * Check addition.
     * @param pFirst the first value
     * @param pSecond the second value
     */
    private static void checkAddition(final String pFirst,
                                      final String pSecond) {
        /* Obtain the two decimals */
        final OceanusDecimal myD1 = new OceanusDecimal(pFirst);
        final OceanusDecimal myD2 = new OceanusDecimal(pSecond);
        final int myScale = Math.max(myD1.scale(), myD2.scale());

        /* Obtain the two BigDecimals */
        final BigDecimal myB1 = new BigDecimal(pFirst);
        final BigDecimal myB2 = new BigDecimal(pSecond);

        /* Add the two values */
        final BigDecimal myB3 = myB1.add(myB2);

        /* Check addition with rounding */
        final OceanusDecimal myD4 = new OceanusDecimal(myD1);
        myD4.addValue(myD2);

        /* Assert equality */
        Assertions.assertEquals(myB3.setScale(myD4.scale(), RoundingMode.HALF_UP), myD4.toBigDecimal(),
                "Failed Rounded Addition: " + pFirst + " + " + pSecond);

        /* Check addition with rounding */
        final OceanusDecimal myD5 = new OceanusDecimal(myD2);
        myD5.addValue(myD1);

        /* Assert equality */
        Assertions.assertEquals(myB3.setScale(myD5.scale(), RoundingMode.HALF_UP), myD5.toBigDecimal(),
                "Failed Rounded Addition: " + pFirst + " + " + pSecond);

        /* Assert equality */
        final OceanusDecimal myD3 = myD1.scale() > myD2.scale() ? myD4 : myD5;
        Assertions.assertEquals(myB3, myD3.toBigDecimal(), "Failed Addition: " + pFirst + " + " + pSecond);
    }

    /**
     * Check subtraction.
     * @param pFirst the first value
     * @param pSecond the second value
     */
    private static void checkSubtraction(final String pFirst,
                                         final String pSecond) {
        /* Obtain the two decimals */
        final OceanusDecimal myD1 = new OceanusDecimal(pFirst);
        final OceanusDecimal myD2 = new OceanusDecimal(pSecond);

        /* Obtain the two BigDecimals */
        final BigDecimal myB1 = new BigDecimal(pFirst);
        final BigDecimal myB2 = new BigDecimal(pSecond);

        /* Subtract the two values */
        final BigDecimal myB3 = myB1.subtract(myB2);

        /* Check subtraction with rounding */
        final OceanusDecimal myD4 = new OceanusDecimal(myD1);
        myD4.subtractValue(myD2);

        /* Assert equality */
        Assertions.assertEquals(myB3.setScale(myD4.scale(), RoundingMode.HALF_UP), myD4.toBigDecimal(),
                "Failed Rounded Subtraction: " + pFirst + " - " + pSecond);

        /* Check subtraction with rounding */
        final OceanusDecimal myD5 = new OceanusDecimal(myD2);
        myD5.subtractValue(myD1);
        myD5.negate();

        /* Assert equality */
        Assertions.assertEquals(myB3.setScale(myD5.scale(), RoundingMode.HALF_UP), myD5.toBigDecimal(),
                "Failed Rounded Subtraction: -" + pSecond + " + " + pFirst);

        /* Assert equality */
        final OceanusDecimal myD3 = myD1.scale() > myD2.scale() ? myD4 : myD5;
        Assertions.assertEquals(myB3, myD3.toBigDecimal(), "Failed Subtraction: " + pFirst + " - " + pSecond);
    }

    /**
     * Check multiplication.
     * @param pFirst the first value
     * @param pSecond the second value
     */
    private static void checkMultiplication(final String pFirst,
                                            final String pSecond) {
        /* Obtain the two decimals */
        final OceanusDecimal myD1 = new OceanusDecimal(pFirst);
        final OceanusDecimal myD2 = new OceanusDecimal(pSecond);

        /* Obtain the two BigDecimals */
        final BigDecimal myB1 = new BigDecimal(pFirst);
        final BigDecimal myB2 = new BigDecimal(pSecond);

        /* Multiply the two values */
        final OceanusDecimal myD3 = new OceanusDecimal(myD1).multiply(myD2);
        final BigDecimal myB3 = myB1.multiply(myB2);

        /* Assert equality */
        Assertions.assertEquals(myB3, myD3.toBigDecimal(), "Failed Multiplication: " + pFirst + " * " + pSecond);
    }

    /**
     * Check division.
     * @param pFirst the first value
     * @param pSecond the second value
     */
    private static void checkDivision(final String pFirst,
                                      final String pSecond) {
        /* Obtain the two decimals */
        final OceanusDecimal myD1 = new OceanusDecimal(pFirst);
        final OceanusDecimal myD2 = new OceanusDecimal(pSecond);

        /* Obtain the two BigDecimals */
        final BigDecimal myB1 = new BigDecimal(pFirst);
        final BigDecimal myB2 = new BigDecimal(pSecond);

        /* Divide the two values */
        final OceanusDecimal myD3 = new OceanusDecimal(myD1).divide(myD2);
        final BigDecimal myB3 = myB1.divide(myB2, RoundingMode.HALF_UP);

        /* Assert equality */
        Assertions.assertEquals(myB3, myD3.toBigDecimal(), "Failed Division: " + pFirst + " / " + pSecond);
    }

    /**
     * Check bytes.
     */
    private static void checkBytes() {
        /* Obtain the two values */
        final OceanusRate myRate = new OceanusRate("0.25");
        final OceanusUnits myUnits = new OceanusUnits("25.678");
        final OceanusRatio myRatio = new OceanusRatio("5425.68");
        final OceanusMoney myMoney = new OceanusMoney("76.90");
        final OceanusPrice myPrice = new OceanusPrice("0.9856");
        final OceanusDate myDate = new OceanusDate();

        /* Check Rate */
        final OceanusRate myRate2 = new OceanusRate(myRate.toBytes());
        Assertions.assertEquals(myRate, myRate2, "Failed Rate");

        /* Check Units */
        final OceanusUnits myUnits2 = new OceanusUnits(myUnits.toBytes());
        Assertions.assertEquals(myUnits, myUnits2, "Failed Units");

        /* Check Ratio */
        final OceanusRatio myRatio2 = new OceanusRatio(myRatio.toBytes());
        Assertions.assertEquals(myRatio, myRatio2, "Failed Ratio");

        /* Check Money */
        final OceanusMoney myMoney2 = new OceanusMoney(myMoney.toBytes());
        Assertions.assertEquals(myMoney, myMoney2, "Failed Money");

        /* Check Price */
        final OceanusPrice myPrice2 = new OceanusPrice(myPrice.toBytes());
        Assertions.assertEquals(myPrice, myPrice2, "Failed Price");

        /* Check Date */
        final OceanusDateFormatter myFormatter = new OceanusDateFormatter();
        final OceanusDate myDate2 = myFormatter.fromBytes(myFormatter.toBytes(myDate));
        Assertions.assertEquals(myDate, myDate2, "Failed Date");
    }
}
