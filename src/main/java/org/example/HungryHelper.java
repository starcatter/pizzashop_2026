package org.example;

class HungryHelper extends PizzaShopHelper {
    int pickupCount = 0;

    public HungryHelper(PizzaShop shop) {
        super(shop);
    }

    @Override
    protected void pickUpFilledPizza() {
        super.pickUpFilledPizza();

        if (workedPizza != null) {
            pickupCount++;
            if (pickupCount % 3 == 0 && !workedPizza.ingredientsOnPizza.isEmpty()) {
                workedPizza.ingredientsOnPizza.removeLast();
                shop.pizzas.add(workedPizza);
                workedPizza = null;
            }
        }
    }
}