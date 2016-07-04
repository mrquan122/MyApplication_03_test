package com.example.administrator.app.test;

import android.test.InstrumentationTestCase;

/**
 * Created by Administrator on 2016/4/6.
 */
public class ExampleTest extends InstrumentationTestCase {

    public void test() throws Exception {
        final int expected =1;
        final int reality =5;
        assertEquals(expected,reality);
    }
}

