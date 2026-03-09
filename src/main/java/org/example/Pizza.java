package org.example;

import java.util.ArrayList;
import java.util.List;

class Pizza {
    PizzaSize size;
    PizzaRecipe recipe;
    List<Ingredient> ingredientsOnPizza = new ArrayList<>();
    PizzaState state;
    String sauce = null;

    public Pizza(PizzaSize size, PizzaRecipe recipe) {
        this.size = size;
        this.recipe = recipe;
        this.state = PizzaState.Ordered;
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