package net.sourceforge.joceanus.jtethys.junit.decimal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

/* *****************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

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
        final TethysDecimal myD1 = new TethysDecimal(pFirst);
        final TethysDecimal myD2 = new TethysDecimal(pSecond);
        final int myScale = Math.max(myD1.scale(), myD2.scale());

        /* Obtain the two BigDecimals */
        final BigDecimal myB1 = new BigDecimal(pFirst);
        final BigDecimal myB2 = new BigDecimal(pSecond);

        /* Add the two values */
        final BigDecimal myB3 = myB1.add(myB2);

        /* Check addition with rounding */
        final TethysDecimal myD4 = new TethysDecimal(myD1);
        myD4.addValue(myD2);

        /* Assert equality */
        Assertions.assertEquals(myB3.setScale(myD4.scale(), RoundingMode.HALF_UP), myD4.toBigDecimal(),
                "Failed Rounded Addition: " + pFirst + " + " + pSecond);

        /* Check addition with rounding */
        final TethysDecimal myD5 = new TethysDecimal(myD2);
        myD5.addValue(myD1);

        /* Assert equality */
        Assertions.assertEquals(myB3.setScale(myD5.scale(), RoundingMode.HALF_UP), myD5.toBigDecimal(),
                "Failed Rounded Addition: " + pFirst + " + " + pSecond);

        /* Assert equality */
        final TethysDecimal myD3 = myD1.scale() > myD2.scale() ? myD4 : myD5;
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
        final TethysDecimal myD1 = new TethysDecimal(pFirst);
        final TethysDecimal myD2 = new TethysDecimal(pSecond);

        /* Obtain the two BigDecimals */
        final BigDecimal myB1 = new BigDecimal(pFirst);
        final BigDecimal myB2 = new BigDecimal(pSecond);

        /* Subtract the two values */
        final BigDecimal myB3 = myB1.subtract(myB2);

        /* Check subtraction with rounding */
        final TethysDecimal myD4 = new TethysDecimal(myD1);
        myD4.subtractValue(myD2);

        /* Assert equality */
        Assertions.assertEquals(myB3.setScale(myD4.scale(), RoundingMode.HALF_UP), myD4.toBigDecimal(),
                "Failed Rounded Subtraction: " + pFirst + " - " + pSecond);

        /* Check subtraction with rounding */
        final TethysDecimal myD5 = new TethysDecimal(myD2);
        myD5.subtractValue(myD1);
        myD5.negate();

        /* Assert equality */
        Assertions.assertEquals(myB3.setScale(myD5.scale(), RoundingMode.HALF_UP), myD5.toBigDecimal(),
                "Failed Rounded Subtraction: -" + pSecond + " + " + pFirst);

        /* Assert equality */
        final TethysDecimal myD3 = myD1.scale() > myD2.scale() ? myD4 : myD5;
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
        final TethysDecimal myD1 = new TethysDecimal(pFirst);
        final TethysDecimal myD2 = new TethysDecimal(pSecond);

        /* Obtain the two BigDecimals */
        final BigDecimal myB1 = new BigDecimal(pFirst);
        final BigDecimal myB2 = new BigDecimal(pSecond);

        /* Multiply the two values */
        final TethysDecimal myD3 = new TethysDecimal(myD1).multiply(myD2);
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
        final TethysDecimal myD1 = new TethysDecimal(pFirst);
        final TethysDecimal myD2 = new TethysDecimal(pSecond);

        /* Obtain the two BigDecimals */
        final BigDecimal myB1 = new BigDecimal(pFirst);
        final BigDecimal myB2 = new BigDecimal(pSecond);

        /* Divide the two values */
        final TethysDecimal myD3 = new TethysDecimal(myD1).divide(myD2);
        final BigDecimal myB3 = myB1.divide(myB2, RoundingMode.HALF_UP);

        /* Assert equality */
        Assertions.assertEquals(myB3, myD3.toBigDecimal(), "Failed Division: " + pFirst + " / " + pSecond);
    }

    /**
     * Check bytes.
     */
    private static void checkBytes() {
        /* Obtain the two values */
        final TethysRate myRate = new TethysRate("0.25");
        final TethysUnits myUnits = new TethysUnits("25.678");
        final TethysRatio myRatio = new TethysRatio("5425.68");
        final TethysMoney myMoney = new TethysMoney("76.90");
        final TethysPrice myPrice = new TethysPrice("0.9856");
        final TethysDate myDate = new TethysDate();

        /* Check Rate */
        final TethysRate myRate2 = new TethysRate(myRate.toBytes());
        Assertions.assertEquals(myRate, myRate2, "Failed Rate");

        /* Check Units */
        final TethysUnits myUnits2 = new TethysUnits(myUnits.toBytes());
        Assertions.assertEquals(myUnits, myUnits2, "Failed Units");

        /* Check Ratio */
        final TethysRatio myRatio2 = new TethysRatio(myRatio.toBytes());
        Assertions.assertEquals(myRatio, myRatio2, "Failed Ratio");

        /* Check Money */
        final TethysMoney myMoney2 = new TethysMoney(myMoney.toBytes());
        Assertions.assertEquals(myMoney, myMoney2, "Failed Money");

        /* Check Price */
        final TethysPrice myPrice2 = new TethysPrice(myPrice.toBytes());
        Assertions.assertEquals(myPrice, myPrice2, "Failed Price");

        /* Check Date */
        final TethysDateFormatter myFormatter = new TethysDateFormatter();
        final TethysDate myDate2 = myFormatter.fromBytes(myFormatter.toBytes(myDate));
        Assertions.assertEquals(myDate, myDate2, "Failed Date");
    }
}
