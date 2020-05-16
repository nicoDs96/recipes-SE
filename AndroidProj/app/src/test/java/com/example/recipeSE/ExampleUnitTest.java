package com.example.recipeSE;

import com.example.recipeSE.search.utils.SharedViewModel;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void testVM(){
        SharedViewModel svm = new SharedViewModel();

        svm.setRecipes("pasta tonno  pomodoro");

        assertEquals("4", svm.getRecipes().getValue());
    }

}