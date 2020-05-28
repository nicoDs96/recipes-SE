package com.example.recipeSE.savedRecipes;

import android.content.Context;

import com.example.recipeSE.search.utils.Recipe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


@Database(entities = {Recipe.class}, version = 1, exportSchema = false)
@TypeConverters({IngredientQuantityRoomConverter.class})
public abstract class SavedRecipesRoomDB extends RoomDatabase {
    public abstract SavedRecipesDAO savedRecipesDAO();

    private static volatile SavedRecipesRoomDB INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static SavedRecipesRoomDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SavedRecipesRoomDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SavedRecipesRoomDB.class, "saved_recipes_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
