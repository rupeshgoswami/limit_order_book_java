package com.trading.lob.model;

/**
 * Represents the type of order.
 *
 * LIMIT  = Execute only at specified price or better
 *          Goes into book and WAITS if no match
 *          Example: "BUY at ₹2500 or cheaper"
 *
 * MARKET = Execute immediately at best available price
 *          Never goes into book - always fills instantly
 *          Example: "BUY at whatever current price is"
 */
public enum OrderType {
    LIMIT,   // Wait for specific price
    MARKET   // Execute immediately
}