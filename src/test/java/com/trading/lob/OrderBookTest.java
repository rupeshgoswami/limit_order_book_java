package com.trading.lob;

import com.trading.lob.book.OrderBook;
import com.trading.lob.model.Order;
import com.trading.lob.model.OrderSide;
import com.trading.lob.model.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for OrderBook
 *
 * Tests all core order book operations:
 * - Adding orders to bid and ask sides
 * - Cancelling orders
 * - Best bid and ask calculations
 * - Spread and mid price calculations
 * - Empty book edge cases
 */
class OrderBookTest {

    private OrderBook book;

    // Runs before EACH test
    // Creates a fresh empty book
    @BeforeEach
    void setUp() {
        book = new OrderBook("RELIANCE");
    }

    // ─────────────────────────────────────────────────
    // ADD ORDER TESTS
    // ─────────────────────────────────────────────────

    @Test
    @DisplayName("Adding bid order should appear on bid side")
    void testAddBidOrder() {
        Order order = new Order(
            1L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT,
            2500.00, 500);

        book.addOrder(order);

        assertEquals(1, book.getTotalOrders());
        assertEquals(2500.00, book.getBestBid());
        assertTrue(book.hasBids());
        assertFalse(book.hasAsks());
    }

    @Test
    @DisplayName("Adding ask order should appear on ask side")
    void testAddAskOrder() {
        Order order = new Order(
            1L, "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT,
            2501.00, 300);

        book.addOrder(order);

        assertEquals(1, book.getTotalOrders());
        assertEquals(2501.00, book.getBestAsk());
        assertFalse(book.hasBids());
        assertTrue(book.hasAsks());
    }

    @Test
    @DisplayName("Multiple bids should be sorted high to low")
    void testBidsAreSortedHighToLow() {
        book.addOrder(new Order(1L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2498.00, 100));
        book.addOrder(new Order(2L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2500.00, 200));
        book.addOrder(new Order(3L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2499.00, 300));

        // Best bid should be highest price
        assertEquals(2500.00, book.getBestBid());
        System.out.println("Best Bid: " + book.getBestBid());
    }

    @Test
    @DisplayName("Multiple asks should be sorted low to high")
    void testAsksAreSortedLowToHigh() {
        book.addOrder(new Order(1L, "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT, 2503.00, 100));
        book.addOrder(new Order(2L, "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT, 2501.00, 200));
        book.addOrder(new Order(3L, "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT, 2502.00, 300));

        // Best ask should be lowest price
        assertEquals(2501.00, book.getBestAsk());
        System.out.println("Best Ask: " + book.getBestAsk());
    }

    // ─────────────────────────────────────────────────
    // CANCEL ORDER TESTS
    // ─────────────────────────────────────────────────

    @Test
    @DisplayName("Cancelling existing order should remove it")
    void testCancelExistingOrder() {
        book.addOrder(new Order(1L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2500.00, 500));

        assertTrue(book.cancelOrder(1L));
        assertEquals(0, book.getTotalOrders());
        assertFalse(book.hasBids());
        System.out.println("Order cancelled successfully");
    }

    @Test
    @DisplayName("Cancelling non-existent order should return false")
    void testCancelNonExistentOrder() {
        assertFalse(book.cancelOrder(999L));
        System.out.println(
            "Non-existent cancel correctly returned false");
    }

    @Test
    @DisplayName("Cancelling one order should not affect others")
    void testCancelOneOrderLeavesOthers() {
        book.addOrder(new Order(1L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2500.00, 500));
        book.addOrder(new Order(2L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2499.00, 300));

        book.cancelOrder(1L);

        assertEquals(1, book.getTotalOrders());
        assertEquals(2499.00, book.getBestBid());
    }

    // ─────────────────────────────────────────────────
    // SPREAD AND MID PRICE TESTS
    // ─────────────────────────────────────────────────

    @Test
    @DisplayName("Spread should be ask minus bid")
    void testSpreadCalculation() {
        book.addOrder(new Order(1L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2500.00, 500));
        book.addOrder(new Order(2L, "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT, 2501.00, 300));

        assertEquals(1.00, book.getSpread(), 0.001);
        System.out.println("Spread: " + book.getSpread());
    }

    @Test
    @DisplayName("Mid price should be average of bid and ask")
    void testMidPriceCalculation() {
        book.addOrder(new Order(1L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2500.00, 500));
        book.addOrder(new Order(2L, "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT, 2501.00, 300));

        assertEquals(2500.50, book.getMidPrice(), 0.001);
        System.out.println("Mid Price: " + book.getMidPrice());
    }

    // ─────────────────────────────────────────────────
    // EMPTY BOOK TESTS
    // ─────────────────────────────────────────────────

    @Test
    @DisplayName("Empty book should return zero for best bid")
    void testEmptyBookBestBid() {
        assertEquals(0.0, book.getBestBid());
    }

    @Test
    @DisplayName("Empty book should return zero for best ask")
    void testEmptyBookBestAsk() {
        assertEquals(0.0, book.getBestAsk());
    }

    @Test
    @DisplayName("Empty book should return zero spread")
    void testEmptyBookSpread() {
        assertEquals(0.0, book.getSpread());
    }
}