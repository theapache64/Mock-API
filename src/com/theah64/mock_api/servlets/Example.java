package com.theah64.mock_api.servlets;

/**
 * Created by theapache64 on 13/11/17.
 */
public class Example {

    private final int cartCount;
    private final String cartPrice;

    public Example(int cartCount, String cartPrice) {
        this.cartCount = cartCount;
        this.cartPrice = cartPrice;
    }

    public int getCartCount() {
        return cartCount;
    }

    public String getCartPrice() {
        return cartPrice;
    }

}
