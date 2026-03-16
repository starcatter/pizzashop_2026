package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Pizza {
    private final PizzaSize size;
    private final PizzaRecipe recipe;
    private final List<Ingredient> ingredientsOnPizza = new ArrayList<>();
    private PizzaState state;
    private String sauce = null;

    public Pizza(PizzaSize size, PizzaRecipe recipe) {
        this.size = size;
        this.recipe = recipe;
        this.state = PizzaState.Ordered;
    }

    public PizzaSize getSize() { return size; }
    public PizzaRecipe getRecipe() { return recipe; }
    public PizzaState getState() { return state; }
    public void setState(PizzaState state) { this.state = state; }
    public String getSauce() { return sauce; }
    public void setSauce(String sauce) { this.sauce = sauce; }

    public List<Ingredient> getIngredientsOnPizza() {
        return Collections.unmodifiableList(ingredientsOnPizza);
    }

    public void addIngredient(Ingredient ingredient) {
        ingredientsOnPizza.add(ingredient);
    }

    public void clearIngredients() {
        ingredientsOnPizza.clear();
    }

    public Ingredient removeLastIngredient() {
        return ingredientsOnPizza.removeLast();
    }

    @Override
    public String toString() {
        return "Pizza{" +
                "size=" + size +
                ", recipe=" + recipe +
                ", ingredientsOnPizza=" + ingredientsOnPizza +
                ", state=" + state +
                ", sauce=" + sauce +
                '}';
    }
}