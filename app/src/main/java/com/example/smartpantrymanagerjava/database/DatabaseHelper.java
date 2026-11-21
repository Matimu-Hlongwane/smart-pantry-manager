package com.example.smartpantrymanagerjava.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanagerjava.model.PantryItem;
import com.example.smartpantrymanagerjava.model.Recipe;
import com.example.smartpantrymanagerjava.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SQLiteOpenHelper managing smart_pantry.db database setup, migrations, and preloaded recipe seeding.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PantryDao.CREATE_TABLE);
        db.execSQL(RecipeDao.CREATE_RECIPES_TABLE);
        db.execSQL(RecipeDao.CREATE_INGREDIENTS_TABLE);

        seedPreloadedRecipes(db);
        seedSamplePantryItems(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecipeDao.TABLE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + RecipeDao.TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + PantryDao.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Reseed database recipes (used in Settings if needed).
     */
    public void resetRecipes(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + RecipeDao.TABLE_INGREDIENTS);
        db.execSQL("DELETE FROM " + RecipeDao.TABLE_RECIPES);
        seedPreloadedRecipes(db);
    }

    private void seedSamplePantryItems(SQLiteDatabase db) {
        PantryDao pantryDao = new PantryDao(this);
        // Pre-populate sensible starting pantry items for instant usability
        List<PantryItem> samples = Arrays.asList(
                new PantryItem("Flour", 1.0, "kg", "2027-06-01"),
                new PantryItem("Milk", 1.0, "l", "2027-05-15"),
                new PantryItem("Egg", 12, "pcs", "2027-05-20"),
                new PantryItem("Sugar", 500, "g", "2027-12-31"),
                new PantryItem("Butter", 250, "g", "2027-06-10"),
                new PantryItem("Bread", 1, "pcs", "2027-05-12"),
                new PantryItem("Garlic", 5, "cloves", "2027-07-01"),
                new PantryItem("Tomato Sauce", 800, "ml", "2027-09-01"),
                new PantryItem("Spaghetti", 500, "g", "2027-10-15"),
                new PantryItem("Ground Beef", 500, "g", "2027-05-18"),
                new PantryItem("Onion", 3, "pcs", "2027-06-30"),
                new PantryItem("Cheddar Cheese", 200, "g", "2027-05-25")
        );

        for (PantryItem item : samples) {
            pantryDao.insertPantryItem(item, db);
        }
    }

    private void seedPreloadedRecipes(SQLiteDatabase db) {
        RecipeDao recipeDao = new RecipeDao(this);

        List<Recipe> recipes = new ArrayList<>();

        // 1. Classic Pancakes
        recipes.add(new Recipe("Classic Pancakes", "Fluffy and golden breakfast pancakes.", "Breakfast", 10, 15,
                "1. Whisk flour, sugar, eggs, and milk until smooth.\n2. Melt butter in a skillet over medium heat.\n3. Pour batter and cook until bubbles form, then flip.\n4. Serve hot with syrup.",
                Arrays.asList(
                        new RecipeIngredient("Flour", 200, "g"),
                        new RecipeIngredient("Milk", 300, "ml"),
                        new RecipeIngredient("Egg", 2, "pcs"),
                        new RecipeIngredient("Sugar", 30, "g"),
                        new RecipeIngredient("Butter", 30, "g")
                )));

        // 2. Spaghetti Bolognese
        recipes.add(new Recipe("Spaghetti Bolognese", "Hearty Italian pasta in rich meat sauce.", "Dinner", 15, 30,
                "1. Boil spaghetti in salted water until al dente.\n2. Sauté chopped onion and garlic in olive oil.\n3. Add ground beef and cook until browned.\n4. Stir in tomato sauce, simmer 15 mins, and toss with pasta.",
                Arrays.asList(
                        new RecipeIngredient("Spaghetti", 250, "g"),
                        new RecipeIngredient("Ground Beef", 400, "g"),
                        new RecipeIngredient("Tomato Sauce", 400, "ml"),
                        new RecipeIngredient("Onion", 1, "pcs"),
                        new RecipeIngredient("Garlic", 2, "cloves")
                )));

        // 3. Chicken Fried Rice
        recipes.add(new Recipe("Chicken Fried Rice", "Quick and savory wok-fried rice with chicken and veggies.", "Main", 10, 15,
                "1. Heat oil in a pan, cook diced chicken breast until golden.\n2. Push chicken to the side, scramble eggs.\n3. Add cooked rice, peas, and carrots.\n4. Drizzle soy sauce and toss on high heat.",
                Arrays.asList(
                        new RecipeIngredient("Rice", 300, "g"),
                        new RecipeIngredient("Chicken Breast", 250, "g"),
                        new RecipeIngredient("Soy Sauce", 30, "ml"),
                        new RecipeIngredient("Egg", 2, "pcs"),
                        new RecipeIngredient("Carrot", 1, "pcs"),
                        new RecipeIngredient("Peas", 100, "g")
                )));

        // 4. Greek Salad
        recipes.add(new Recipe("Greek Salad", "Crisp Mediterranean salad with feta cheese and olives.", "Salad", 10, 0,
                "1. Chop cucumber, tomatoes, and onion into bite-sized pieces.\n2. Combine in a large bowl with feta cheese and black olives.\n3. Drizzle with olive oil and serve fresh.",
                Arrays.asList(
                        new RecipeIngredient("Cucumber", 1, "pcs"),
                        new RecipeIngredient("Tomato", 2, "pcs"),
                        new RecipeIngredient("Feta Cheese", 150, "g"),
                        new RecipeIngredient("Olive Oil", 30, "ml"),
                        new RecipeIngredient("Black Olives", 50, "g")
                )));

        // 5. Creamy Tomato Soup
        recipes.add(new Recipe("Creamy Tomato Soup", "Comforting warm soup made with sweet tomatoes.", "Soup", 10, 20,
                "1. Sauté chopped onion and garlic in olive oil.\n2. Pour in tomato sauce and simmer for 15 minutes.\n3. Blend until smooth and stir in heavy cream.\n4. Season with salt and pepper.",
                Arrays.asList(
                        new RecipeIngredient("Tomato Sauce", 500, "ml"),
                        new RecipeIngredient("Onion", 1, "pcs"),
                        new RecipeIngredient("Garlic", 2, "cloves"),
                        new RecipeIngredient("Olive Oil", 15, "ml"),
                        new RecipeIngredient("Heavy Cream", 50, "ml")
                )));

        // 6. Fresh Guacamole
        recipes.add(new Recipe("Fresh Guacamole", "Zesty avocado dip made with lime and salt.", "Snack", 10, 0,
                "1. Scoop avocado flesh into a bowl and mash with a fork.\n2. Stir in finely diced onion and lime juice.\n3. Season with salt and chopped cilantro.",
                Arrays.asList(
                        new RecipeIngredient("Avocado", 2, "pcs"),
                        new RecipeIngredient("Lime", 1, "pcs"),
                        new RecipeIngredient("Onion", 0.5, "pcs"),
                        new RecipeIngredient("Salt", 5, "g"),
                        new RecipeIngredient("Cilantro", 10, "g")
                )));

        // 7. Scrambled Eggs on Toast
        recipes.add(new Recipe("Scrambled Eggs on Toast", "Simple, creamy scrambled eggs over toasted bread.", "Breakfast", 5, 5,
                "1. Toast bread slices.\n2. Whisk eggs with milk and salt.\n3. Melt butter in skillet over low heat, gently scramble eggs.\n4. Serve immediately over warm toast.",
                Arrays.asList(
                        new RecipeIngredient("Egg", 3, "pcs"),
                        new RecipeIngredient("Butter", 15, "g"),
                        new RecipeIngredient("Bread", 2, "slices"),
                        new RecipeIngredient("Milk", 30, "ml")
                )));

        // 8. Garlic Bread
        recipes.add(new Recipe("Garlic Bread", "Crispy roasted bread topped with garlic herb butter.", "Appetizer", 10, 10,
                "1. Mix softened butter with minced garlic and parsley.\n2. Spread butter evenly over bread slices.\n3. Bake at 200°C for 10 minutes until golden and crispy.",
                Arrays.asList(
                        new RecipeIngredient("Bread", 1, "pcs"),
                        new RecipeIngredient("Garlic", 3, "cloves"),
                        new RecipeIngredient("Butter", 50, "g"),
                        new RecipeIngredient("Parsley", 10, "g")
                )));

        // 9. Banana Smoothie
        recipes.add(new Recipe("Banana Smoothie", "Refreshing energy smoothie packed with fruit.", "Beverage", 5, 0,
                "1. Add sliced banana, milk, honey, and yogurt to blender.\n2. Blend on high speed for 1 minute until smooth.\n3. Pour into tall glass and enjoy.",
                Arrays.asList(
                        new RecipeIngredient("Banana", 2, "pcs"),
                        new RecipeIngredient("Milk", 250, "ml"),
                        new RecipeIngredient("Honey", 15, "ml"),
                        new RecipeIngredient("Yogurt", 100, "g")
                )));

        // 10. Golden French Toast
        recipes.add(new Recipe("Golden French Toast", "Rich egg-soaked bread cooked to golden perfection.", "Breakfast", 10, 10,
                "1. Whisk eggs, milk, and cinnamon in a shallow bowl.\n2. Dip bread slices to soak evenly.\n3. Melt butter in pan and cook slices 3 mins each side until golden.",
                Arrays.asList(
                        new RecipeIngredient("Bread", 4, "slices"),
                        new RecipeIngredient("Egg", 2, "pcs"),
                        new RecipeIngredient("Milk", 100, "ml"),
                        new RecipeIngredient("Cinnamon", 5, "g"),
                        new RecipeIngredient("Butter", 20, "g")
                )));

        // 11. Vegetable Stir Fry
        recipes.add(new Recipe("Vegetable Stir Fry", "Colorful crisp vegetables tossed in soy sauce.", "Main", 10, 10,
                "1. Chop broccoli, carrots, and bell pepper.\n2. Heat olive oil in wok over high heat.\n3. Stir fry veggies for 5-7 mins until tender-crisp.\n4. Drizzle soy sauce before serving.",
                Arrays.asList(
                        new RecipeIngredient("Broccoli", 200, "g"),
                        new RecipeIngredient("Carrot", 2, "pcs"),
                        new RecipeIngredient("Bell Pepper", 1, "pcs"),
                        new RecipeIngredient("Soy Sauce", 45, "ml"),
                        new RecipeIngredient("Olive Oil", 15, "ml")
                )));

        // 12. Creamy Mashed Potatoes
        recipes.add(new Recipe("Creamy Mashed Potatoes", "Smooth and buttery mashed potatoes.", "Side Dish", 15, 20,
                "1. Peel and chop potatoes, boil in salted water for 15 mins until soft.\n2. Drain water thoroughly.\n3. Add warm butter, milk, and salt.\n4. Mash until fluffy and creamy.",
                Arrays.asList(
                        new RecipeIngredient("Potato", 500, "g"),
                        new RecipeIngredient("Butter", 40, "g"),
                        new RecipeIngredient("Milk", 100, "ml"),
                        new RecipeIngredient("Salt", 5, "g")
                )));

        // 13. Coconut Chicken Curry
        recipes.add(new Recipe("Coconut Chicken Curry", "Aromatic chicken curry cooked in coconut milk.", "Dinner", 15, 25,
                "1. Sauté diced onion and garlic in pan.\n2. Add chicken breast and curry powder, cook for 5 mins.\n3. Pour in coconut milk and simmer for 15 mins until sauce thickens.",
                Arrays.asList(
                        new RecipeIngredient("Chicken Breast", 500, "g"),
                        new RecipeIngredient("Coconut Milk", 400, "ml"),
                        new RecipeIngredient("Curry Powder", 15, "g"),
                        new RecipeIngredient("Onion", 1, "pcs"),
                        new RecipeIngredient("Garlic", 2, "cloves")
                )));

        // 14. Oatmeal with Honey
        recipes.add(new Recipe("Oatmeal with Honey", "Warm, comforting oatmeal with honey and fresh fruit.", "Breakfast", 3, 7,
                "1. Combine oats and milk in a pot over medium heat.\n2. Simmer for 5 minutes stirring constantly.\n3. Top with honey and banana slices.",
                Arrays.asList(
                        new RecipeIngredient("Oats", 100, "g"),
                        new RecipeIngredient("Milk", 250, "ml"),
                        new RecipeIngredient("Honey", 20, "ml"),
                        new RecipeIngredient("Banana", 1, "pcs")
                )));

        // 15. Classic Grilled Cheese
        recipes.add(new Recipe("Classic Grilled Cheese", "Golden toasted sandwich filled with gooey cheddar.", "Snack", 5, 8,
                "1. Butter outer sides of bread slices.\n2. Place cheddar cheese between bread slices.\n3. Grill on pan over medium-low heat 4 mins per side until cheese melts.",
                Arrays.asList(
                        new RecipeIngredient("Bread", 2, "slices"),
                        new RecipeIngredient("Cheddar Cheese", 50, "g"),
                        new RecipeIngredient("Butter", 15, "g")
                )));

        // 16. Caesar Salad
        recipes.add(new Recipe("Caesar Salad", "Crisp romaine lettuce with grilled chicken and dressing.", "Salad", 10, 10,
                "1. Grill chicken breast and slice into strips.\n2. Toss lettuce with Caesar dressing and parmesan cheese.\n3. Top with chicken and croutons made from bread.",
                Arrays.asList(
                        new RecipeIngredient("Lettuce", 200, "g"),
                        new RecipeIngredient("Chicken Breast", 150, "g"),
                        new RecipeIngredient("Caesar Dressing", 45, "ml"),
                        new RecipeIngredient("Parmesan Cheese", 30, "g"),
                        new RecipeIngredient("Bread", 2, "slices")
                )));

        // 17. Fluffy Cheese Omelette
        recipes.add(new Recipe("Fluffy Cheese Omelette", "Egg omelette stuffed with melted cheese and veggies.", "Breakfast", 5, 8,
                "1. Whisk eggs with a pinch of salt.\n2. Melt butter in pan, sauté onion and bell pepper 2 mins.\n3. Pour eggs, cook until set, sprinkle cheese, fold in half.",
                Arrays.asList(
                        new RecipeIngredient("Egg", 3, "pcs"),
                        new RecipeIngredient("Cheddar Cheese", 40, "g"),
                        new RecipeIngredient("Onion", 0.5, "pcs"),
                        new RecipeIngredient("Bell Pepper", 0.5, "pcs"),
                        new RecipeIngredient("Butter", 10, "g")
                )));

        // 18. Creamy Mac and Cheese
        recipes.add(new Recipe("Creamy Mac and Cheese", "Classic baked macaroni in rich cheddar sauce.", "Dinner", 15, 20,
                "1. Boil macaroni pasta until al dente.\n2. Melt butter, whisk in flour, then stir in milk until thickened.\n3. Stir in cheddar cheese until melted sauce forms.\n4. Toss with macaroni.",
                Arrays.asList(
                        new RecipeIngredient("Macaroni", 200, "g"),
                        new RecipeIngredient("Cheddar Cheese", 150, "g"),
                        new RecipeIngredient("Milk", 200, "ml"),
                        new RecipeIngredient("Butter", 30, "g"),
                        new RecipeIngredient("Flour", 15, "g")
                )));

        // 19. Chili Con Carne
        recipes.add(new Recipe("Chili Con Carne", "Rich spicy beef stew with kidney beans and tomato.", "Dinner", 15, 35,
                "1. Brown ground beef in pot with chopped onion.\n2. Add tomato sauce, kidney beans, and chili powder.\n3. Simmer covered for 30 minutes until deep flavor develops.",
                Arrays.asList(
                        new RecipeIngredient("Ground Beef", 500, "g"),
                        new RecipeIngredient("Kidney Beans", 250, "g"),
                        new RecipeIngredient("Tomato Sauce", 400, "ml"),
                        new RecipeIngredient("Onion", 1, "pcs"),
                        new RecipeIngredient("Chili Powder", 10, "g")
                )));

        // 20. Chocolate Mug Cake
        recipes.add(new Recipe("Chocolate Mug Cake", "Quick 2-minute microwaved chocolate dessert.", "Dessert", 3, 2,
                "1. In a microwave-safe mug, whisk flour, sugar, and cocoa powder.\n2. Add melted butter and milk, whisk until batter is smooth.\n3. Microwave on high for 70 seconds. Let cool 1 min.",
                Arrays.asList(
                        new RecipeIngredient("Flour", 40, "g"),
                        new RecipeIngredient("Sugar", 30, "g"),
                        new RecipeIngredient("Cocoa Powder", 15, "g"),
                        new RecipeIngredient("Milk", 45, "ml"),
                        new RecipeIngredient("Butter", 15, "g")
                )));

        for (Recipe r : recipes) {
            recipeDao.insertRecipe(r, db);
        }
    }
}
