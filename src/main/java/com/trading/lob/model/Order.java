package com.trading.lob.model;

import java.time.LocalDateTime;

/**
 * Represents a single order in the order book.
 *
 * Every order has:
 * orderId    → unique ID to track and cancel it
 * symbol     → stock ticker e.g. "RELIANCE"
 * side       → BID (buy) or ASK (sell)
 * type       → LIMIT or MARKET
 * price      → limit price (0 for market orders)
 * quantity   → total shares requested
 * filledQty  → shares already traded
 * timestamp  → when order was placed (for priority)
 *
 * Example:
 * Order #1001: BUY 500 RELIANCE @ ₹2500 LIMIT
 */
public class Order {

    private final long orderId;
    private final String symbol;
    private final OrderSide side;
    private final OrderType type;
    private final double price;
    private int quantity;        // remaining quantity
    private int filledQuantity;  // how much has been filled
    private final LocalDateTime timestamp;

    public Order(long orderId, String symbol,
                 OrderSide side, OrderType type,
                 double price, int quantity) {
        this.orderId         = orderId;
        this.symbol          = symbol;
        this.side            = side;
        this.type            = type;
        this.price           = price;
        this.quantity        = quantity;
        this.filledQuantity  = 0;
        this.timestamp       = LocalDateTime.now();
    }

    // ─────────────────────────────────────────────────
    // Fill this order (partial or full)
    // Called when a trade is executed
    // ─────────────────────────────────────────────────
    public void fill(int fillQty) {
        if (fillQty > quantity) {
            throw new IllegalArgumentException(
                "Fill quantity " + fillQty +
                " exceeds remaining quantity " + quantity
            );
        }
        this.filledQuantity += fillQty;
        this.quantity       -= fillQty;
    }

    // ─────────────────────────────────────────────────
    // Check if order is completely filled
    // ─────────────────────────────────────────────────
    public boolean isFilled() {
        return quantity == 0;
    }

    // ─────────────────────────────────────────────────
    // Check if order is partially filled
    // ─────────────────────────────────────────────────
    public boolean isPartiallyFilled() {
        return filledQuantity > 0 && quantity > 0;
    }

    // Getters
    public long getOrderId()        { return orderId;        }
    public String getSymbol()       { return symbol;         }
    public OrderSide getSide()      { return side;           }
    public OrderType getType()      { return type;           }
    public double getPrice()        { return price;          }
    public int getQuantity()        { return quantity;       }
    public int getFilledQuantity()  { return filledQuantity; }
    public LocalDateTime getTimestamp() { return timestamp;  }

    @Override
    public String toString() {
        return String.format(
            "Order{id=%d, %s, %s, %s, price=%.2f, qty=%d, filled=%d}",
            orderId, symbol, side, type, price,
            quantity, filledQuantity
        );
    }
}