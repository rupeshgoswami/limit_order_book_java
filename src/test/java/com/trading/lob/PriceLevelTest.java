package com.trading.lob;

import com.trading.lob.book.PriceLevel;
import com.trading.lob.model.Order;
import com.trading.lob.model.OrderSide;
import com.trading.lob.model.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for PriceLevel
 *
 * Tests the FIFO queue at each price level:
 * - Adding orders
 * - Removing orders
 * - Volume tracking
 * - FIFO order priority
 */
class PriceLevelTest {

    private PriceLevel level;

    @BeforeEach
    void setUp() {
        level = new PriceLevel(2500.00);
    }

    @Test
    @DisplayName("New price level should be empty")
    void testNewLevelIsEmpty() {
        assertTrue(level.isEmpty());
        assertEquals(0, level.getTotalVolume());
        assertEquals(0, level.getOrderCount());
    }

    @Test
    @DisplayName("Adding order should increase volume")
    void testAddOrderIncreasesVolume() {
        Order order = new Order(
            1L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT,
            2500.00, 500);

        level.addOrder(order);

        assertEquals(500, level.getTotalVolume());
        assertEquals(1, level.getOrderCount());
        assertFalse(level.isEmpty());
    }

    @Test
    @DisplayName("FIFO priority - first in first out")
    void testFIFOPriority() {
        Order first = new Order(
            1L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT,
            2500.00, 100);
        Order second = new Order(
            2L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT,
            2500.00, 200);
        Order third = new Order(
            3L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT,
            2500.00, 300);

        level.addOrder(first);
        level.addOrder(second);
        level.addOrder(third);

        // First added should be first out
        assertEquals(first, level.poll(),
            "First order should come out first");
        assertEquals(second, level.poll(),
            "Second order should come out second");
        assertEquals(third, level.poll(),
            "Third order should come out third");

        System.out.println("FIFO order confirmed");
    }

    @Test
    @DisplayName("Removing order should decrease volume")
    void testRemoveOrderDecreasesVolume() {
        Order order = new Order(
            1L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT,
            2500.00, 500);

        level.addOrder(order);
        level.removeOrder(order);

        assertEquals(0, level.getTotalVolume());
        assertTrue(level.isEmpty());
    }

    @Test
    @DisplayName("Multiple orders total volume should be correct")
    void testTotalVolumeMultipleOrders() {
        level.addOrder(new Order(1L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2500.00, 100));
        level.addOrder(new Order(2L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2500.00, 200));
        level.addOrder(new Order(3L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2500.00, 300));

        assertEquals(600, level.getTotalVolume(),
            "Total volume should be 100+200+300=600");
        System.out.println(
            "Total volume: " + level.getTotalVolume());
    }

    @Test
    @DisplayName("Peek should not remove order")
    void testPeekDoesNotRemove() {
        Order order = new Order(
            1L, "RELIANCE",
            OrderSide.BID, OrderType.LIMIT,
            2500.00, 500);

        level.addOrder(order);
        Order peeked = level.peek();

        assertEquals(order, peeked);
        assertEquals(1, level.getOrderCount(),
            "Peek should not remove order");
    }
}