# Smart Pantry Manager (Mobile App Development 700)

**Smart Pantry Manager** is a complete, production-ready native Android application built entirely in **Java** using traditional **Android XML layouts**, **Android Views**, and persistent local storage powered by **SQLite (`SQLiteOpenHelper`)**.

---

## Key Features

1. **Home & Dashboard (`MainActivity`)**
   - Clean, modern Material 3 interface.
   - Real-time inventory overview statistics:
     - Total Pantry Items count.
     - "Can Cook Now" strictly matched recipes count.
     - "Expiring Soon" pantry item alerts.
   - Quick navigation cards to Pantry Management, Suggested Recipes, All Recipes, and Settings/Profile.

2. **Pantry Management (`PantryActivity` & `AddEditPantryDialog`)**
   - Complete CRUD (Create, Read, Update, Delete) management of pantry ingredients.
   - Live search filtering by ingredient name.
   - Fields: Name, Quantity, Unit, and Expiry Date.
   - Input validation enforcing non-empty names, positive quantities, valid units, and valid date formats (`YYYY-MM-DD`).
   - Integrated `DatePickerDialog` for user-friendly expiry date selection.
   - Visual highlighting for expired (`EXPIRED`) and expiring soon (`Expiring Soon`) items.
   - `RecyclerView` adapter with edit and delete dialog confirmations.

3. **SQLite Database Architecture (`DatabaseHelper`, `PantryDao`, `RecipeDao`)**
   - Structured local SQLite storage with proper foreign key constraints and cascading deletes.
   - Tables:
     - `pantry_items` (id, name, quantity, unit, expiry_date)
     - `recipes` (id, title, description, category, prep_time_minutes, cook_time_minutes, instructions)
     - `recipe_ingredients` (id, recipe_id, name, quantity, unit)
   - Preloads 20 realistic recipes upon first launch / database creation.

4. **20 Preloaded Realistic Recipes**
   - Pre-populated with 20 realistic culinary recipes across breakfast, lunch, dinner, snacks, and desserts (e.g., *Classic Pancakes, Spaghetti Bolognese, Chicken Fried Rice, Greek Salad, Creamy Tomato Soup, Fresh Guacamole, Scrambled Eggs on Toast, Garlic Bread, Banana Smoothie, Golden French Toast, Vegetable Stir Fry, Creamy Mashed Potatoes, Coconut Chicken Curry, Oatmeal with Honey, Classic Grilled Cheese, Caesar Salad, Fluffy Cheese Omelette, Creamy Mac and Cheese, Chili Con Carne, Chocolate Mug Cake*).

5. **Strict Recipe Matching Engine (`RecipeMatcher`, `UnitConverter`, `IngredientNormalizer`)**
   - **Strict Matching Rule:** Suggests a recipe **ONLY if EVERY required ingredient** is present in the pantry in **sufficient quantity**.
   - No partial matches allowed in the "Ready to Cook" filter.
   - **Ingredient Normalization:** Handles case-insensitivity, whitespace trimming, and plural/singular variations (e.g. "Tomatoes" -> "tomato", "Eggs" -> "egg").
   - **Unit Conversions:**
     - Weight: `kg` <-> `g` (1 kg = 1000 g), `mg`, `oz`, `lb`.
     - Volume: `l` <-> `ml` (1 L = 1000 ml), `tsp` (5 ml), `tbsp` (15 ml), `cup` (240 ml), `fl oz` (29.57 ml).
     - Discrete/Count: `pcs`, `piece`, `unit`, `cloves`, `slices`.
     - Strictly rejects conversion across incompatible dimensions (e.g. weight `g` vs volume `ml` or discrete `pcs`).

6. **Recipe Detail & Inventory Deduction (`RecipeDetailActivity`)**
   - Full display of recipe title, description, category, total time, preparation steps, and required ingredients list.
   - `RecipeIngredientAdapter` provides clear visual status for each ingredient:
     - Green checkmark (`ic_check_circle`) and availability status when sufficient in pantry.
     - Red warning (`ic_warning`) detailing required vs available quantities when missing or insufficient.
   - **Cook Recipe Feature:** Deducts required ingredient quantities directly from the SQLite pantry database upon cooking confirmation, updating available inventory in real time.

7. **Profile & Settings (`SettingsActivity`)**
   - App information and version details.
   - Database administration tools:
     - "Reset & Reseed Recipes (20)": Restores default 20 preloaded recipes.
     - "Clear All Pantry Items": Empties pantry inventory with safety confirmation.

8. **Navigation & Screen Flow**
   - At least 5 distinct Activity screens (`MainActivity`, `PantryActivity`, `RecipesActivity`, `RecipeDetailActivity`, `SettingsActivity`).
   - Standard Android back navigation (`parentActivityName` support) and customized empty states for search/filtering.

---

## Unit Conversion Matrix

| Dimension | Standard Base Unit | Supported Conversions |
| :--- | :--- | :--- |
| **Weight** | Gram (`g`) | `1 kg = 1000 g`, `1 mg = 0.001 g`, `1 oz = 28.35 g`, `1 lb = 453.59 g` |
| **Volume** | Milliliter (`ml`) | `1 L = 1000 ml`, `1 tsp = 5 ml`, `1 tbsp = 15 ml`, `1 cup = 240 ml`, `1 fl oz = 29.57 ml` |
| **Count** | Piece (`pcs`) | `1 pc = 1 unit = 1 item` |

---

## Database Schema

```sql
CREATE TABLE pantry_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    quantity REAL NOT NULL,
    unit TEXT NOT NULL,
    expiry_date TEXT
);

CREATE TABLE recipes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    category TEXT,
    prep_time_minutes INTEGER,
    cook_time_minutes INTEGER,
    instructions TEXT
);

CREATE TABLE recipe_ingredients (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    recipe_id INTEGER NOT NULL,
    name TEXT NOT NULL,
    quantity REAL NOT NULL,
    unit TEXT NOT NULL,
    FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);
```

---

## Project Structure

```
app/src/main/
├── AndroidManifest.xml
├── java/com/example/smartpantrymanagerjava/
│   ├── adapter/
│   │   ├── PantryAdapter.java
│   │   ├── RecipeAdapter.java
│   │   └── RecipeIngredientAdapter.java
│   ├── database/
│   │   ├── DatabaseHelper.java
│   │   ├── PantryDao.java
│   │   └── RecipeDao.java
│   ├── model/
│   │   ├── MatchResult.java
│   │   ├── PantryItem.java
│   │   ├── Recipe.java
│   │   └── RecipeIngredient.java
│   ├── ui/
│   │   ├── MainActivity.java
│   │   ├── PantryActivity.java
│   │   ├── RecipeDetailActivity.java
│   │   ├── RecipesActivity.java
│   │   └── SettingsActivity.java
│   └── util/
│       ├── DateUtils.java
│       ├── IngredientNormalizer.java
│       ├── RecipeMatcher.java
│       └── UnitConverter.java
└── res/
    ├── drawable/
    ├── layout/
    └── values/
```

---

## Testing & Quality Assurance

### Automated Unit Tests (`app/src/test/`)
- `UnitConverterTest`: Validates weight (`kg` <-> `g`), volume (`l` <-> `ml`, `tbsp`/`tsp`/`cup` <-> `ml`), count matching, and rejection of incompatible dimensions (`g` vs `ml`).
- `IngredientNormalizerTest`: Validates trimming, lowercasing, and plural singularization ("Tomatoes" -> "tomato", "Eggs" -> "egg").
- `RecipeMatcherTest`: Tests strict matching rules (100% required ingredients present and sufficient quantity), handling of insufficient quantities, missing ingredients, and zero quantity pantry items.
- `DateUtilsTest`: Tests ISO date parsing (`YYYY-MM-DD`), validation, expiration checks, and 3-day threshold "expiring soon" alerts.

### Instrumented Android Tests (`app/src/androidTest/`)
- `DatabaseHelperTest`: Tests SQLite database creation, table creation, foreign key constraints, seeding of 20 preloaded recipes, pantry item CRUD operations, and DB-backed recipe matching.

### Executing Tests
```bash
# Run unit tests
./gradlew app:testDebugUnitTest

# Build debug APK
./gradlew app:assembleDebug
```

---

## Requirements Verification Summary

- **Java Only:** 100% Java codebase; no Kotlin or Compose used.
- **SQLite Storage:** Persistent storage using `SQLiteOpenHelper`, `PantryDao`, and `RecipeDao`.
- **XML & Traditional Views:** Clean Material Design XML layouts (`ConstraintLayout`, `MaterialCardView`, `RecyclerView`).
- **20 Preloaded Recipes:** Realistic recipes seeded in SQLite with detailed prep steps, cooking times, and ingredient requirements.
- **Strict Matching:** Verified via 14 unit tests and device manual testing.
