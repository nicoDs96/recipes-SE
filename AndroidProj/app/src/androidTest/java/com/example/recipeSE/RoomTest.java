package com.example.recipeSE;


import android.content.Context;

import com.example.recipeSE.savedRecipes.utils.SavedRecipesDAO;
import com.example.recipeSE.savedRecipes.utils.SavedRecipesRoomDB;
import com.example.recipeSE.search.utils.Recipe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class RoomTest {
    private SavedRecipesDAO recipesDao;
    private SavedRecipesRoomDB db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, SavedRecipesRoomDB.class).build();
        recipesDao = db.savedRecipesDAO();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeUserAndReadInList() throws Exception {
        HashMap<String, String> m = new HashMap<String,String>();
        m.put("pomodoro","qb");
        Recipe recipe = new Recipe("a",
                19,
                m,
                "www.mipiacitu.it",
                "Sample");


        recipesDao.save(recipe);
        LiveData<List<Recipe>> list = recipesDao.getSavedRecipes();
        System.out.println(list.toString());


        //assertThat(list.get(0), equalTo(recipe));

    }


}
