package com.ritesh.imagetopdf.utils;

import com.ritesh.imagetopdf.domain.utils.Utils;

import junit.framework.TestCase;

public class UtilsTest extends TestCase {


    public void testConvertFileSize() {
        String ans = Utils.convertFileSize((long)1000000);
        assert ans.equals("1.0 MB");
    }
}