package com.trading.lob.model;

import java.time.LocalDateTime;

/**
 * Represents an executed trade.
 * Created when a BUY order matches a SELL order.
 *
 * Example:
 * Trader A: BUY  500 @ ₹2500
 * Trader B: SELL 300 @ ₹2500
 * → Trade: 300 shares @ ₹2500
 *   buyOrderId  = Trader A's order ID
 *   sellOrderId = Trader B's order ID
 *   quantity    = 300 (the matched amount)
 *   price       = ₹2500 (the matched price)
 */
public class Trade {

    private static long tradeCounter = 1;

    private final long tradeId;
    private final String symbol;
    private final long buyOrderId;
    private final long sellOrderId;
    private final double price;
    private final int quantity;
    private final LocalDateTime timestamp;

    public Trade(String symbol, long buyOrderId,
                 long sellOrderId, double price,
                 int quantity) {
        this.tradeId     = tradeCounter++;
        this.symbol      = symbol;
        this.buyOrderId  = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price       = price;
        this.quantity    = quantity;
        this.timestamp   = LocalDateTime.now();
    }

    // ─────────────────────────────────────────────────
    // Calculate total value of this trade
    // Value = price * quantity
    // ─────────────────────────────────────────────────
    public double getValue() {
        return price * quantity;
    }

    // Getters
    public long getTradeId()      { return tradeId;     }
    public String getSymbol()     { return symbol;      }
    public long getBuyOrderId()   { return buyOrderId;  }
    public long getSellOrderId()  { return sellOrderId; }
    public double getPrice()      { return price;       }
    public int getQuantity()      { return quantity;    }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format(
            "Trade{id=%d, %s, buyOrder=%d, sellOrder=%d, " +
            "price=%.2f, qty=%d, value=%.2f}",
            tradeId, symbol, buyOrderId, sellOrderId,
            price, quantity, getValue()
        );
    }
}