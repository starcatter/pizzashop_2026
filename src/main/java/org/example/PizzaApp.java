package org.example;

import java.util.ArrayList;
import java.util.List;

public class PizzaApp {

    public static void main(String[] args) {
        PizzaShop shop = new PizzaShop(List.of(
                new PizzaRecipe("Margherita", List.of(Ingredient.Cheese)),
                new PizzaRecipe("Peperoni", List.of(Ingredient.Cheese, Ingredient.Peperoni))),
                1, 1, 1);

        List<Pizza> orderedPizzas = new ArrayList<>();
        for (PizzaRecipe recipe : shop.getMenu()) {
            orderedPizzas.add(shop.placeOrder(recipe, PizzaSize.L));
        }

        boolean success = shop.simulate(orderedPizzas, 100);

        System.out.println("--- Simulation complete ---");
        System.out.println("Success: " + success);
        System.out.println("Ticks: " + shop.getTicksPassed());
        System.out.println("Pizzas prepared: " + shop.getPizzasPrepared());
        System.out.println("Pizzas burned: " + shop.getPizzasBurned());

        for (Pizza pizza : orderedPizzas) {
            System.out.println(pizza);
        }
    }
}