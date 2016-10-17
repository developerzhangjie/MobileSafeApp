package com.example.sin.mobilesafe;

import org.junit.Test;

import utils.MD5Utils;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void md5() {
        String hahaha = MD5Utils.md5("123456");
        System.out.println(hahaha);
    }


}