package org.javarosa.xpath.expr;


import static org.javarosa.xpath.expr.Digests.HashEncodingMethod.BASE64;
import static org.javarosa.xpath.expr.Digests.HashEncodingMethod.HEX;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class HashEncodingMethodTest {
  @Parameterized.Parameter(value = 0)
  public String testName;

  @Parameterized.Parameter(value = 1)
  public Digests.HashEncodingMethod encodingMethod;

  @Parameterized.Parameter(value = 2)
  public String input;

  @Parameterized.Parameter(value = 3)
  public String expectedOutput;

  @Parameterized.Parameters(name = "{0}")
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {"Hexadecimal", HEX, "some text", "736f6d652074657874"},
        {"Base64", BASE64, "some text", "c29tZSB0ZXh0"},
    });
  }

  @Test
  public void encodes_byte_arrays() throws UnsupportedEncodingException {
    assertEquals(expectedOutput, encodingMethod.encode(input.getBytes("UTF-8")));
  }
}