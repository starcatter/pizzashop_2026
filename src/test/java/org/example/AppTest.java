package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for pizza App.
 */
public class AppTest {
    PizzaShop shop = null;

    @BeforeEach
    public void resetShop() {
        shop = new PizzaShop(List.of(
                new PizzaRecipe("Margherita", List.of(Ingredient.Cheese)),
                new PizzaRecipe("Peperoni", List.of(Ingredient.Cheese, Ingredient.Peperoni))),
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

        assertEquals(PizzaState.Boxed, p.state);
        assertEquals(1, p.ingredientsOnPizza.size());
        assertEquals(Ingredient.Cheese, p.ingredientsOnPizza.getFirst());
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

        assertEquals(PizzaState.Boxed, p.state);
        assertEquals(2, p.ingredientsOnPizza.size());
        assertEquals(Ingredient.Cheese, p.ingredientsOnPizza.get(0));
        assertEquals(Ingredient.Peperoni, p.ingredientsOnPizza.get(1));
    }

    @Test
    public void testHotPizza() {
        PizzaShopHelper helper = new PizzaShopHelper(shop);

        var p = new Pizza(PizzaSize.L, null);
        p.state = PizzaState.Baking;
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
        PizzaFurnace furnace = shop.furnaces.getFirst();
        Pizza p = new Pizza(PizzaSize.L, shop.getMenu().getFirst());
        furnace.loadPizza(p);

        // Bake the pizza (BAKE_TIME ticks)
        try {
            for (int i = 0; i < PizzaFurnace.BAKE_TIME; i++) {
                assertEquals(PizzaState.Baking, p.state);
                furnace.update();
            }
            assertEquals(PizzaState.Baked, p.state);
        } catch (PizzaBurnedException e){
            fail("Pizza burned prematurely!", e);
        }

        // Leave it in the oven until it burns (TIME_TO_BURN ticks)
        try {
            for (int i = 0; i < PizzaFurnace.TIME_TO_BURN; i++) {
                assertEquals(PizzaState.Baked, p.state);
                furnace.update();
            }
            fail("Pizza did not burn!");
        } catch (PizzaBurnedException e) {
            assertTrue(e.getMessage().startsWith("Pizza was burned!"), "Unexpected outcome!");
        }
        assertEquals(PizzaState.Burned, p.state);
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
}