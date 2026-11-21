package com.example.smartpantrymanagerjava.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartpantrymanagerjava.model.Recipe;
import com.example.smartpantrymanagerjava.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for recipes and recipe_ingredients tables.
 */
public class RecipeDao {

    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_RECIPE_ID = "id";
    public static final String COLUMN_RECIPE_TITLE = "title";
    public static final String COLUMN_RECIPE_DESC = "description";
    public static final String COLUMN_RECIPE_CAT = "category";
    public static final String COLUMN_RECIPE_PREP = "prep_time_minutes";
    public static final String COLUMN_RECIPE_COOK = "cook_time_minutes";
    public static final String COLUMN_RECIPE_INSTRUCTIONS = "instructions";

    public static final String TABLE_INGREDIENTS = "recipe_ingredients";
    public static final String COLUMN_ING_ID = "id";
    public static final String COLUMN_ING_RECIPE_ID = "recipe_id";
    public static final String COLUMN_ING_NAME = "name";
    public static final String COLUMN_ING_QUANTITY = "quantity";
    public static final String COLUMN_ING_UNIT = "unit";

    public static final String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + " (" +
            COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_RECIPE_TITLE + " TEXT NOT NULL, " +
            COLUMN_RECIPE_DESC + " TEXT, " +
            COLUMN_RECIPE_CAT + " TEXT, " +
            COLUMN_RECIPE_PREP + " INTEGER, " +
            COLUMN_RECIPE_COOK + " INTEGER, " +
            COLUMN_RECIPE_INSTRUCTIONS + " TEXT" +
            ");";

    public static final String CREATE_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_INGREDIENTS + " (" +
            COLUMN_ING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ING_RECIPE_ID + " INTEGER NOT NULL, " +
            COLUMN_ING_NAME + " TEXT NOT NULL, " +
            COLUMN_ING_QUANTITY + " REAL NOT NULL, " +
            COLUMN_ING_UNIT + " TEXT NOT NULL, " +
            "FOREIGN KEY(" + COLUMN_ING_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + ") ON DELETE CASCADE" +
            ");";

    private final DatabaseHelper dbHelper;

    public RecipeDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insertRecipe(Recipe recipe, SQLiteDatabase writeDb) {
        SQLiteDatabase db = writeDb != null ? writeDb : dbHelper.getWritableDatabase();

        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COLUMN_RECIPE_TITLE, recipe.getTitle().trim());
        recipeValues.put(COLUMN_RECIPE_DESC, recipe.getDescription());
        recipeValues.put(COLUMN_RECIPE_CAT, recipe.getCategory());
        recipeValues.put(COLUMN_RECIPE_PREP, recipe.getPrepTimeMinutes());
        recipeValues.put(COLUMN_RECIPE_COOK, recipe.getCookTimeMinutes());
        recipeValues.put(COLUMN_RECIPE_INSTRUCTIONS, recipe.getInstructions());

        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);
        recipe.setId(recipeId);

        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ing : recipe.getIngredients()) {
                ContentValues ingValues = new ContentValues();
                ingValues.put(COLUMN_ING_RECIPE_ID, recipeId);
                ingValues.put(COLUMN_ING_NAME, ing.getName().trim());
                ingValues.put(COLUMN_ING_QUANTITY, ing.getQuantity());
                ingValues.put(COLUMN_ING_UNIT, ing.getUnit().trim());

                long ingId = db.insert(TABLE_INGREDIENTS, null, ingValues);
                ing.setId(ingId);
                ing.setRecipeId(recipeId);
            }
        }

        return recipeId;
    }

    public long insertRecipe(Recipe recipe) {
        return insertRecipe(recipe, null);
    }

    public Recipe getRecipeById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, COLUMN_RECIPE_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        Recipe recipe = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                recipe = cursorToRecipe(cursor);
                recipe.setIngredients(getIngredientsForRecipe(id, db));
            }
            cursor.close();
        }
        return recipe;
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null, COLUMN_RECIPE_TITLE + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Recipe recipe = cursorToRecipe(cursor);
                recipe.setIngredients(getIngredientsForRecipe(recipe.getId(), db));
                recipes.add(recipe);
            }
            cursor.close();
        }
        return recipes;
    }

    public List<RecipeIngredient> getIngredientsForRecipe(long recipeId, SQLiteDatabase readDb) {
        SQLiteDatabase db = readDb != null ? readDb : dbHelper.getReadableDatabase();
        List<RecipeIngredient> ingredients = new ArrayList<>();

        Cursor cursor = db.query(TABLE_INGREDIENTS, null, COLUMN_ING_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)}, null, null, COLUMN_ING_ID + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ING_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ING_NAME));
                double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ING_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ING_UNIT));

                ingredients.add(new RecipeIngredient(id, recipeId, name, qty, unit));
            }
            cursor.close();
        }
        return ingredients;
    }

    private Recipe cursorToRecipe(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_TITLE));
        String desc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_DESC));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_CAT));
        int prep = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_PREP));
        int cook = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_COOK));
        String instructions = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_INSTRUCTIONS));

        return new Recipe(id, title, desc, category, prep, cook, instructions, new ArrayList<>());
    }
}
