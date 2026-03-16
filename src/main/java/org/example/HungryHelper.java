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
            if (pickupCount % 3 == 0
                    && !workedPizza.getIngredientsOnPizza().isEmpty()) {
                workedPizza.removeLastIngredient();
                putBack(workedPizza);
            }
        }
    }
}