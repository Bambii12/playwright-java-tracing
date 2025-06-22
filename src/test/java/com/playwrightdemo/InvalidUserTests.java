package com.playwrightdemo;

import org.testng.annotations.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


public class InvalidUserTests extends PlaywrightBaseTest {

    @Test
    public void invalidUserTest() {

        String invalidUsername = "invaliduser@gmail.com";
        String password = "password";
        String fullName = "John Smith";
        String phoneNumber = "+7(123)123-456-789";
        String address = "1101 Lincoln St";

        // Step 1: Sign in
        page.locator("#usernameInput").fill(invalidUsername);
        page.locator("#passwordInput").fill(password);
        page.click("button:has-text(\"Sign In\")");

        // Step 2: Add to Cart
        page.click("button:has-text(\"Add to Cart\")");
        assertThat(page.locator("#cartItems div").nth(0)).containsText("Playwright Tracing Course");

        // Step 3: Fill Checkout form
        page.fill("#fullNameInput", fullName);
        page.fill("#phoneInput", phoneNumber);
        page.fill("#addressInput", address);

        // Step 4: Submit Order
        page.click("button:has-text(\"Submit Order\")");
        assertThat(page.locator("#orderMessage")).containsText("Order placed");
    }
}
