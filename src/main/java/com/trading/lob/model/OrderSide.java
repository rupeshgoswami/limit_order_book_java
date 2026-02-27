package com.trading.lob.model;

/**
 * Represents which side of the order book
 * an order belongs to.
 *
 * BID = Buy side  (left side of book)
 * ASK = Sell side (right side of book)
 *
 * Example:
 * "BUY  500 shares @ ₹2500" → BID
 * "SELL 300 shares @ ₹2501" → ASK
 */
public enum OrderSide {
    BID,  // Buy side
    ASK   // Sell side
}