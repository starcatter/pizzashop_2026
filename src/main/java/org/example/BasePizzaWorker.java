package org.example;

abstract class BasePizzaWorker implements PizzaShopWorker {
    protected final PizzaShop shop;
    protected Pizza workedPizza = null;

    protected BasePizzaWorker(PizzaShop shop) {
        this.shop = shop;
    }

    protected void pickUp(Pizza pizza) {
        workedPizza = pizza;
        shop.removePizza(pizza);
    }

    protected void putBack(Pizza pizza) {
        shop.returnPizza(pizza);
        workedPizza = null;
    }

    protected void complete(Pizza pizza) {
        shop.completePizza(pizza);
        workedPizza = null;
    }
}