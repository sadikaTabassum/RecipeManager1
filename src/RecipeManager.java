import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class RecipeManager {
    private final Scanner scanner;
    private final RecipeFileHandler fileHandler;
    private List<Recipe> recipes;

    public RecipeManager(Scanner scanner, String fileName) throws IOException {
        this.scanner = scanner;
        this.fileHandler = new RecipeFileHandler(fileName);
        this.recipes = fileHandler.loadRecipes();
    }

    // menu interface
    public void run() throws IOException {
        while (true) {
            try {
                System.out.println("Choose option: 1-Add, 2-Edit, 3-Delete, 4-Search, 5-Exit");
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> addRecipe();
                    case 2 -> editRecipe();
                    case 3 -> deleteRecipe();
                    case 4 -> searchRecipe();
                    case 5 -> {
                        System.out.println("Exiting Recipe Manager.");
                        return;
                    }
                    default -> System.out.println("Invalid choice");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (IOException e) {
                System.out.println("Error during file operation: " + e.getMessage());
                throw e; // Re-throw to be caught in the main method
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

//add recipe
    private void addRecipe() throws IOException {
        System.out.print("Enter recipe name: ");
        String name = scanner.nextLine();
        System.out.print("Enter servings (2, 3, 4, 6, or 8): ");
        int servings = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter calories: ");
        int calories = Integer.parseInt(scanner.nextLine());
        List<String> ingredients = getIngredientsFromUser();
        System.out.print("Enter procedure: ");
        String procedure = scanner.nextLine();

        Recipe recipe = new Recipe(name, servings, calories, ingredients, procedure);
        fileHandler.addRecipe(recipe);
        recipes.add(recipe);
        System.out.println("Recipe added successfully!");
    }


    private List<String> getIngredientsFromUser() {
        List<String> ingredients = new ArrayList<>();
        System.out.println("Enter ingredients (type 'done' to finish):");
        while (true) {
            String ingredient = scanner.nextLine();
            if (ingredient.equalsIgnoreCase("done")) {
                break;
            }
            ingredients.add(ingredient);
        }
        return ingredients;
    }

    private void editRecipe() throws IOException {
        System.out.print("Enter recipe name to edit: ");
        String nameToEdit = scanner.nextLine();

        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getName().equalsIgnoreCase(nameToEdit)) {
                Recipe recipe = recipes.get(i);
                editRecipeDetails(recipe);
                fileHandler.saveRecipes(recipes);
                System.out.println("Recipe updated successfully.");
                return;
            }
        }
        System.out.println("Recipe not found.");
    }

    private void editRecipeDetails(Recipe recipe) {
        System.out.println("What would you like to edit? (name/servings/calories/ingredients/recipe)");
        String choice = scanner.nextLine().trim().toLowerCase();

        switch (choice) {
            case "name" -> {
                System.out.print("Enter new name: ");
                recipe.setName(scanner.nextLine());
            }
            case "servings" -> {
                System.out.print("Enter new servings: ");
                recipe.setServings(Integer.parseInt(scanner.nextLine()));
            }
            case "calories" -> {
                System.out.print("Enter new calories: ");
                recipe.setCalories(Integer.parseInt(scanner.nextLine()));
            }
            case "ingredients" -> {
                recipe.setIngredients(getIngredientsFromUser());
            }
            case "recipe" -> {
                System.out.print("Enter new procedure: ");
                recipe.setProcedure(scanner.nextLine());
            }
            default -> System.out.println("Invalid edit option.");
        }
    }

    private void deleteRecipe() throws IOException {
        System.out.print("Enter recipe name to delete: ");
        String nameToDelete = scanner.nextLine();
        boolean removed = recipes.removeIf(r -> r.getName().equalsIgnoreCase(nameToDelete));
        if (removed) {
            fileHandler.saveRecipes(recipes);
            System.out.println("Recipe deleted.");
        } else {
            System.out.println("Recipe not found.");
        }
    }

    private void searchRecipe() throws IOException {
        System.out.println("Search by: 1-Servings, 2-Ingredient, 3-Calorie Range");
        int choice = Integer.parseInt(scanner.nextLine());
        boolean found = false;

        switch (choice) {
            case 1 -> {
                System.out.print("Enter serving size: ");
                int size = Integer.parseInt(scanner.nextLine());
                for (Recipe r : recipes) {
                    if (r.getServings() == size) {
                        r.display();
                        found = true;
                    }
                }
            }
            case 2 -> {
                System.out.print("Enter ingredient: ");
                String ingredient = scanner.nextLine().toLowerCase();
                for (Recipe r : recipes) {
                    if (r.getIngredients().stream().anyMatch(i -> i.toLowerCase().contains(ingredient))) {
                        r.display();
                        found = true;
                    }
                }
            }
            case 3 -> {
                System.out.print("Enter calorie range (less/greater number): ");
                String[] parts = scanner.nextLine().split(" ");
                if (parts.length != 2) {
                    System.out.println("Invalid input format. Please use: less 300 or greater 600");
                    return;
                }
                String operation = parts[0];
                try {
                    int value = Integer.parseInt(parts[1]);
                    for (Recipe r : recipes) {
                        if ((operation.equalsIgnoreCase("less") && r.getCalories() < value) ||
                                (operation.equalsIgnoreCase("greater") && r.getCalories() > value)) {
                            r.display();
                            found = true;
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format.");
                    return;
                }
            }
            default -> System.out.println("Invalid search option.");
        }

        if (!found) {
            System.out.println("No matching recipes found.");
        }
    }
}
