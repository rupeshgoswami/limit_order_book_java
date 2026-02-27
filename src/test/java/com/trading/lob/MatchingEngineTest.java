package com.trading.lob;

import com.trading.lob.book.MatchingEngine;
import com.trading.lob.book.OrderBook;
import com.trading.lob.model.Order;
import com.trading.lob.model.OrderSide;
import com.trading.lob.model.OrderType;
import com.trading.lob.model.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Unit Tests for MatchingEngine
 *
 * Tests all matching scenarios:
 * - No match (order goes to book)
 * - Full match (complete trade)
 * - Partial match (partial fill)
 * - Market order matching
 * - Multiple fills at same price
 */
class MatchingEngineTest {

    private OrderBook book;
    private MatchingEngine engine;

    @BeforeEach
    void setUp() {
        book   = new OrderBook("RELIANCE");
        engine = new MatchingEngine(book);
    }

    // ─────────────────────────────────────────────────
    // NO MATCH TESTS
    // ─────────────────────────────────────────────────

    @Test
    @DisplayName("Limit order with no match should go to book")
    void testNoMatchGoesToBook() {
        // Add sell at 2501
        engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT, 2501.00, 300));

        // Buy at 2500 - no match (2500 < 2501)
        List<Trade> trades = engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2500.00, 500));

        assertTrue(trades.isEmpty(),
            "No trades should execute");
        assertEquals(2, book.getTotalOrders(),
            "Both orders should be in book");
        System.out.println("No match - order went to book");
    }

    // ─────────────────────────────────────────────────
    // FULL MATCH TESTS
    // ─────────────────────────────────────────────────

    @Test
    @DisplayName("Full match should execute one trade")
    void testFullMatch() {
        // Add sell 300 @ 2501
        engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT, 2501.00, 300));

        // Buy 300 @ 2501 - full match!
        List<Trade> trades = engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2501.00, 300));

        assertEquals(1, trades.size(),
            "One trade should execute");
        assertEquals(300, trades.get(0).getQuantity(),
            "Trade qty should be 300");
        assertEquals(2501.00, trades.get(0).getPrice(),
            "Trade price should be 2501");

        System.out.println(
            "Full match: " + trades.get(0));
    }

    @Test
    @DisplayName("After full match book should be empty")
    void testBookEmptyAfterFullMatch() {
        engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT, 2501.00, 300));

        engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2501.00, 300));

        assertFalse(book.hasAsks(),
            "Ask side should be empty after full match");
        System.out.println(
            "Book correctly empty after full match");
    }

    // ─────────────────────────────────────────────────
    // PARTIAL MATCH TESTS
    // ─────────────────────────────────────────────────

    @Test
    @DisplayName("Partial match should leave remainder in book")
    void testPartialMatch() {
        // Add sell 300 @ 2501
        engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT, 2501.00, 300));

        // Buy 500 @ 2501 - partial match
        // 300 fills, 200 remains in book
        List<Trade> trades = engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2501.00, 500));

        assertEquals(1, trades.size(),
            "One trade should execute");
        assertEquals(300, trades.get(0).getQuantity(),
            "Only 300 should fill");
        assertTrue(book.hasBids(),
            "Remaining 200 should be in book");

        System.out.println(
            "Partial match: " + trades.get(0));
    }

    @Test
    @DisplayName("Buy higher than ask should match at ask price")
    void testBuyHigherThanAskMatchesAtAsk() {
        // Sell at 2501
        engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT, 2501.00, 300));

        // Buy at 2505 - should match at 2501 (ask price)
        List<Trade> trades = engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2505.00, 300));

        assertEquals(1, trades.size());
        assertEquals(2501.00, trades.get(0).getPrice(),
            "Trade should execute at ask price 2501");

        System.out.println(
            "Trade at ask price: " + trades.get(0).getPrice());
    }

    // ─────────────────────────────────────────────────
    // MARKET ORDER TESTS
    // ─────────────────────────────────────────────────

    @Test
    @DisplayName("Market buy should match best ask immediately")
    void testMarketBuyMatchesBestAsk() {
        // Add sell orders
        engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT, 2501.00, 300));

        // Market buy
        List<Trade> trades = engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.BID, OrderType.MARKET, 0.0, 300));

        assertEquals(1, trades.size(),
            "Market order should match immediately");
        assertEquals(2501.00, trades.get(0).getPrice(),
            "Should match at best ask price");

        System.out.println(
            "Market buy matched at: " +
            trades.get(0).getPrice());
    }

    @Test
    @DisplayName("Market sell should match best bid immediately")
    void testMarketSellMatchesBestBid() {
        // Add buy order
        engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2500.00, 500));

        // Market sell
        List<Trade> trades = engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.ASK, OrderType.MARKET, 0.0, 200));

        assertEquals(1, trades.size(),
            "Market sell should match immediately");
        assertEquals(2500.00, trades.get(0).getPrice(),
            "Should match at best bid price");

        System.out.println(
            "Market sell matched at: " +
            trades.get(0).getPrice());
    }

    // ─────────────────────────────────────────────────
    // CANCEL TESTS
    // ─────────────────────────────────────────────────

    @Test
    @DisplayName("Cancel order should remove from book")
    void testCancelOrder() {
        Order order = new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2500.00, 500);

        engine.submitOrder(order);
        boolean result = engine.cancelOrder(
            order.getOrderId());

        assertTrue(result, "Cancel should succeed");
        assertEquals(0, book.getTotalOrders(),
            "Book should be empty after cancel");

        System.out.println("Order cancelled successfully");
    }

    // ─────────────────────────────────────────────────
    // TRADE HISTORY TESTS
    // ─────────────────────────────────────────────────

    @Test
    @DisplayName("Trade history should record all trades")
    void testTradeHistoryRecorded() {
        engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT, 2501.00, 300));

        engine.submitOrder(new Order(
            engine.getNextOrderId(), "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2501.00, 300));

        assertEquals(1, engine.getTotalTrades(),
            "One trade should be in history");

        System.out.println(
            "Trades recorded: " + engine.getTotalTrades());
    }
}