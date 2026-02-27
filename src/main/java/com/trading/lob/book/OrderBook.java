package com.trading.lob.book;

import com.trading.lob.model.Order;
import com.trading.lob.model.OrderSide;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * The core Order Book data structure.
 *
 * Maintains two sides:
 *
 * BID side (buys) - sorted HIGH to LOW
 * ┌──────────────────┐
 * │ ₹2500 → 1000 qty │ ← Best bid (highest)
 * │ ₹2499 →  500 qty │
 * │ ₹2498 →  750 qty │
 * └──────────────────┘
 *
 * ASK side (sells) - sorted LOW to HIGH
 * ┌──────────────────┐
 * │ ₹2501 →  300 qty │ ← Best ask (lowest)
 * │ ₹2502 →  500 qty │
 * │ ₹2503 →  200 qty │
 * └──────────────────┘
 *
 * Data structures used:
 * TreeMap → keeps prices sorted automatically
 * HashMap → instant order lookup by ID O(1)
 */
public class OrderBook {

    private final String symbol;

    // BID side: sorted HIGH to LOW
    // TreeMap with reverseOrder = highest price first
    private final TreeMap<Double, PriceLevel> bids;

    // ASK side: sorted LOW to HIGH
    // TreeMap with natural order = lowest price first
    private final TreeMap<Double, PriceLevel> asks;

    // Fast lookup: orderId → Order object
    // Enables O(1) cancel operation
    private final HashMap<Long, Order> orderMap;

    public OrderBook(String symbol) {
        this.symbol   = symbol;
        this.bids     = new TreeMap<>(java.util.Collections.reverseOrder());
        this.asks     = new TreeMap<>();
        this.orderMap = new HashMap<>();
    }

    // ─────────────────────────────────────────────────
    // ADD order to the book
    // O(log n) for TreeMap insertion
    // ─────────────────────────────────────────────────
    public void addOrder(Order order) {

        // Store in hashmap for fast cancel
        orderMap.put(order.getOrderId(), order);

        // Add to correct side
        if (order.getSide() == OrderSide.BID) {
            // Add to bid side
            bids.computeIfAbsent(
                order.getPrice(),
                p -> new PriceLevel(p)
            ).addOrder(order);
        } else {
            // Add to ask side
            asks.computeIfAbsent(
                order.getPrice(),
                p -> new PriceLevel(p)
            ).addOrder(order);
        }
    }

    // ─────────────────────────────────────────────────
    // CANCEL order from the book
    // O(1) lookup + O(n) removal from level
    // ─────────────────────────────────────────────────
    public boolean cancelOrder(long orderId) {

        // Find the order instantly
        Order order = orderMap.get(orderId);
        if (order == null) {
            System.out.println(
                "Order #" + orderId + " not found!"
            );
            return false;
        }

        // Remove from correct side
        if (order.getSide() == OrderSide.BID) {
            removeFromLevel(bids, order);
        } else {
            removeFromLevel(asks, order);
        }

        // Remove from hashmap
        orderMap.remove(orderId);
        return true;
    }

    // ─────────────────────────────────────────────────
    // Remove order from price level
    // Clean up empty levels
    // ─────────────────────────────────────────────────
    private void removeFromLevel(
            TreeMap<Double, PriceLevel> side, Order order) {

        PriceLevel level = side.get(order.getPrice());
        if (level != null) {
            level.removeOrder(order);
            // Remove empty price level
            if (level.isEmpty()) {
                side.remove(order.getPrice());
            }
        }
    }

    // ─────────────────────────────────────────────────
    // Remove empty level after matching
    // ─────────────────────────────────────────────────
    public void cleanEmptyLevel(OrderSide side, double price) {
        if (side == OrderSide.BID) {
            PriceLevel level = bids.get(price);
            if (level != null && level.isEmpty()) {
                bids.remove(price);
            }
        } else {
            PriceLevel level = asks.get(price);
            if (level != null && level.isEmpty()) {
                asks.remove(price);
            }
        }
    }

    // ─────────────────────────────────────────────────
    // Get BEST BID (highest buy price)
    // O(1) - TreeMap keeps it sorted
    // ─────────────────────────────────────────────────
    public double getBestBid() {
        if (bids.isEmpty()) return 0.0;
        return bids.firstKey();
    }

    // ─────────────────────────────────────────────────
    // Get BEST ASK (lowest sell price)
    // O(1) - TreeMap keeps it sorted
    // ─────────────────────────────────────────────────
    public double getBestAsk() {
        if (asks.isEmpty()) return 0.0;
        return asks.firstKey();
    }

    // ─────────────────────────────────────────────────
    // Get SPREAD = Best Ask - Best Bid
    // Tight spread = liquid market
    // Wide spread = illiquid market
    // ─────────────────────────────────────────────────
    public double getSpread() {
        if (bids.isEmpty() || asks.isEmpty()) return 0.0;
        return getBestAsk() - getBestBid();
    }

    // ─────────────────────────────────────────────────
    // Get MID PRICE = (Best Bid + Best Ask) / 2
    // Used as fair value estimate
    // ─────────────────────────────────────────────────
    public double getMidPrice() {
        if (bids.isEmpty() || asks.isEmpty()) return 0.0;
        return (getBestBid() + getBestAsk()) / 2.0;
    }

    // ─────────────────────────────────────────────────
    // Get best price level for matching
    // ─────────────────────────────────────────────────
    public PriceLevel getBestBidLevel() {
        if (bids.isEmpty()) return null;
        return bids.firstEntry().getValue();
    }

    public PriceLevel getBestAskLevel() {
        if (asks.isEmpty()) return null;
        return asks.firstEntry().getValue();
    }

    // Check if book has any orders
    public boolean hasBids() { return !bids.isEmpty(); }
    public boolean hasAsks() { return !asks.isEmpty(); }

    // Getters
    public String getSymbol()                          { return symbol;   }
    public TreeMap<Double, PriceLevel> getBids()       { return bids;     }
    public TreeMap<Double, PriceLevel> getAsks()       { return asks;     }
    public HashMap<Long, Order> getOrderMap()          { return orderMap; }
    public int getTotalOrders()                        { return orderMap.size(); }
}