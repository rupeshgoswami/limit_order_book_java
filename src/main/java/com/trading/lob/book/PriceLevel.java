package com.trading.lob.book;

import com.trading.lob.model.Order;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents ALL orders sitting at ONE specific price.
 *
 * Example: Price Level @ ₹2500
 * ┌─────────────────────────────────────┐
 * │ Price: ₹2500                        │
 * │ Orders (FIFO queue):                │
 * │   Order #1001: BUY 500 shares       │
 * │   Order #1002: BUY 300 shares       │
 * │   Order #1005: BUY 200 shares       │
 * │ Total Volume: 1000 shares           │
 * └─────────────────────────────────────┘
 *
 * Uses LinkedList as FIFO queue for
 * PRICE-TIME PRIORITY:
 * → First order placed = first to trade
 */
public class PriceLevel {

    private final double price;

    // LinkedList gives O(1) add to back
    // and O(1) remove from front
    // This ensures time priority (FIFO)
    private final Queue<Order> orders;

    // Total volume cached for O(1) lookup
    private int totalVolume;

    public PriceLevel(double price) {
        this.price       = price;
        this.orders      = new LinkedList<>();
        this.totalVolume = 0;
    }

    // ─────────────────────────────────────────────────
    // Add new order to back of queue
    // O(1) operation
    // ─────────────────────────────────────────────────
    public void addOrder(Order order) {
        orders.add(order);
        totalVolume += order.getQuantity();
    }

    // ─────────────────────────────────────────────────
    // Remove specific order from queue
    // O(n) but rare operation
    // ─────────────────────────────────────────────────
    public boolean removeOrder(Order order) {
        boolean removed = orders.remove(order);
        if (removed) {
            totalVolume -= order.getQuantity();
        }
        return removed;
    }

    // ─────────────────────────────────────────────────
    // Peek at first order without removing
    // Used during matching to check availability
    // ─────────────────────────────────────────────────
    public Order peek() {
        return orders.peek();
    }

    // ─────────────────────────────────────────────────
    // Remove and return first order
    // Called when order is fully matched
    // ─────────────────────────────────────────────────
    public Order poll() {
        Order order = orders.poll();
        if (order != null) {
            totalVolume -= order.getQuantity();
        }
        return order;
    }

    // ─────────────────────────────────────────────────
    // Update volume when order is partially filled
    // ─────────────────────────────────────────────────
    public void reduceVolume(int amount) {
        totalVolume -= amount;
    }

    // ─────────────────────────────────────────────────
    // Check if this price level has any orders
    // ─────────────────────────────────────────────────
    public boolean isEmpty() {
        return orders.isEmpty();
    }

    // ─────────────────────────────────────────────────
    // Number of orders at this price level
    // ─────────────────────────────────────────────────
    public int getOrderCount() {
        return orders.size();
    }

    // Getters
    public double getPrice()       { return price;       }
    public int getTotalVolume()    { return totalVolume; }
    public Queue<Order> getOrders(){ return orders;      }

    @Override
    public String toString() {
        return String.format(
            "PriceLevel{price=%.2f, orders=%d, volume=%d}",
            price, orders.size(), totalVolume
        );
    }
}