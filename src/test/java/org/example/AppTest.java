package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    PizzaShop shop = null;

    @BeforeEach
    public void resetShop() {
        shop = new PizzaShop(List.of(
                new PizzaRecipe("Margherita", List.of(Ingredient.Cheese)),
                new PizzaRecipe("Peperoni", List.of(Ingredient.Cheese, Ingredient.Peperoni)),
                new PizzaRecipe("Hawaiian", List.of(Ingredient.Cheese, Ingredient.Ham, Ingredient.Pineapple))),
                1, 1, 1);
    }

    @Test
    public void testMargherita() {
        var m = shop.getMenu();
        assertFalse(m.isEmpty());

        var r = m.getFirst();
        assertEquals("Margherita", r.name());

        var p = shop.placeOrder(r, PizzaSize.L);

        boolean success = shop.simulate(List.of(p), 50);
        assertTrue(success);

        assertEquals(PizzaState.Boxed, p.getState());
        assertEquals(1, p.getIngredientsOnPizza().size());
        assertEquals(Ingredient.Cheese, p.getIngredientsOnPizza().getFirst());
    }

    @Test
    public void testPeperoni() {
        var m = shop.getMenu();
        assertFalse(m.isEmpty());

        var r = m.get(1);
        assertEquals("Peperoni", r.name());

        var p = shop.placeOrder(r, PizzaSize.L);

        boolean success = shop.simulate(List.of(p), 50);
        assertTrue(success);

        assertEquals(PizzaState.Boxed, p.getState());
        assertEquals(2, p.getIngredientsOnPizza().size());
        assertEquals(Ingredient.Cheese, p.getIngredientsOnPizza().get(0));
        assertEquals(Ingredient.Peperoni, p.getIngredientsOnPizza().get(1));
    }

    @Test
    public void testHawaiian() {
        assertEquals(3, shop.getMenu().size());

        PizzaRecipe hawaiian = null;
        for (PizzaRecipe recipe : shop.getMenu()) {
            if (recipe.name().equals("Hawaiian")) {
                hawaiian = recipe;
            }
        }
        assertNotNull(hawaiian, "Menu should contain Hawaiian pizza");

        Pizza p = shop.placeOrder(hawaiian, PizzaSize.M);
        boolean success = shop.simulate(List.of(p), 50);
        assertTrue(success);
        assertEquals(3, p.getIngredientsOnPizza().size());
    }

    @Test
    public void testHotPizza() {
        // workedPizza is protected — accessible from same package
        PizzaShopHelper helper = new PizzaShopHelper(shop);

        var p = new Pizza(PizzaSize.L, null);
        p.setState(PizzaState.Baking);
        helper.workedPizza = p;

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                helper::update,
                "Expected to throw, but didn't"
        );

        assertTrue(thrown.getMessage().contains("Hot pizza!"));
    }

    @Test
    public void testPizzaBurns() {
        PizzaOven oven = shop.getOvens().getFirst();
        Pizza p = new Pizza(PizzaSize.L, shop.getMenu().getFirst());
        oven.loadPizza(p);

        try {
            for (int i = 0; i < PizzaOven.BAKE_TIME; i++) {
                assertEquals(PizzaState.Baking, p.getState());
                oven.update();
            }
            assertEquals(PizzaState.Baked, p.getState());
        } catch (PizzaBurnedException e) {
            fail("Pizza burned prematurely!", e);
        }

        try {
            for (int i = 0; i < PizzaOven.TIME_TO_BURN; i++) {
                assertEquals(PizzaState.Baked, p.getState());
                oven.update();
            }
            fail("Pizza did not burn!");
        } catch (PizzaBurnedException e) {
            assertTrue(e.getMessage().startsWith("Pizza was burned!"));
        }
        assertEquals(PizzaState.Burned, p.getState());
    }

    @Test
    public void testStatistics() {
        Pizza p = shop.placeOrder(shop.getMenu().getFirst(), PizzaSize.M);

        boolean success = shop.simulate(List.of(p), 50);

        assertTrue(success);
        assertEquals(1, shop.getPizzasPrepared());
        assertEquals(0, shop.getPizzasBurned());
        assertTrue(shop.getTicksPassed() > 0);
    }

    @Test
    public void testPizzaCanHaveSauce() {
        Pizza p = new Pizza(PizzaSize.L, shop.getMenu().getFirst());
        p.setSauce("Tomato");
        assertEquals("Tomato", p.getSauce());
    }

    @Test
    public void testSauceAppliedDuringPreparation() {
        Pizza p = shop.placeOrder(shop.getMenu().getFirst(), PizzaSize.L);
        assertNull(p.getSauce());

        boolean success = shop.simulate(List.of(p), 50);
        assertTrue(success);
        assertNotNull(p.getSauce(), "Sauce should be applied during preparation");
    }

    @Test
    public void testBusyShop() {
        PizzaShop busyShop = new PizzaShop(List.of(
                new PizzaRecipe("Margherita", List.of(Ingredient.Cheese))),
                3, 1, 2);

        List<Pizza> orders = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            orders.add(busyShop.placeOrder(busyShop.getMenu().getFirst(), PizzaSize.L));
        }

        boolean success = busyShop.simulate(orders, 200);

        assertTrue(success, "All pizzas should be boxed");
        assertEquals(0, busyShop.getPizzasBurned(), "No pizzas should be burned");
        assertEquals(6, busyShop.getPizzasPrepared(), "All 6 pizzas should be prepared");
    }

    @Test
    public void testTamperedPizzaDetected() {
        PizzaRecipe recipe = shop.getMenu().get(1); // Peperoni: Cheese + Peperoni
        Pizza tampered = new Pizza(PizzaSize.L, recipe);
        tampered.setState(PizzaState.Filled);
        tampered.addIngredient(Ingredient.Cheese);
        // Missing Peperoni — only 1 of 2 ingredients!

        shop.returnPizza(tampered);
        shop.simulate(List.of(), 1);

        // After 1 tick the helper should have detected the incomplete pizza
        // and reset it — no oven should contain it
        for (PizzaOven oven : shop.getOvens()) {
            assertTrue(oven.isEmpty(),
                    "Tampered pizza should not be loaded into an oven");
        }
    }

    @Test
    public void testHungryHelper() {
        PizzaShop hungryShop = new PizzaShop(List.of(
                new PizzaRecipe("Margherita", List.of(Ingredient.Cheese))),
                0, 0, 2);
        HungryHelper hungryHelper = new HungryHelper(hungryShop);
        hungryShop.addWorker(hungryHelper);

        List<Pizza> pizzas = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Pizza p = new Pizza(PizzaSize.L, hungryShop.getMenu().getFirst());
            p.setState(PizzaState.Filled);
            p.addIngredient(Ingredient.Cheese);
            hungryShop.returnPizza(p);
            pizzas.add(p);
        }

        // Tick 1: picks up pizza 0 (pickupCount=1, normal)
        // Tick 2: loads pizza 0 into oven
        // Tick 3: picks up pizza 1 (pickupCount=2, normal)
        // Tick 4: loads pizza 1 into oven
        // Tick 5: picks up pizza 2 (pickupCount=3, nibble!)
        for (int i = 0; i < 5; i++) {
            hungryShop.update();
        }

        assertEquals(3, hungryHelper.pickupCount, "Should have picked up 3 pizzas");

        assertEquals(PizzaState.Baking, pizzas.get(0).getState());
        assertEquals(PizzaState.Baking, pizzas.get(1).getState());

        assertTrue(pizzas.get(2).getIngredientsOnPizza().isEmpty(),
                "Third pizza should have its ingredient eaten");
    }
}