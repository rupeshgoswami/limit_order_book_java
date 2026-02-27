package com.trading.lob;

import com.trading.lob.book.MatchingEngine;
import com.trading.lob.book.OrderBook;
import com.trading.lob.display.BookDisplay;
import com.trading.lob.model.Order;
import com.trading.lob.model.OrderSide;
import com.trading.lob.model.OrderType;
import com.trading.lob.model.Trade;

import java.util.List;

/**
 * Main runner for the Limit Order Book
 *
 * Demonstrates 5 scenarios:
 * 1. Building the book with limit orders
 * 2. Full match - trade executes completely
 * 3. Partial match - trade executes partially
 * 4. Market order - instant execution
 * 5. Cancel order - remove from book
 */
public class Main {

    public static void main(String[] args) {

        System.out.println(
            "==========================================");
        System.out.println(
            "      LIMIT ORDER BOOK ENGINE             ");
        System.out.println(
            "==========================================");

        // Create order book for RELIANCE stock
        OrderBook book    = new OrderBook("RELIANCE");
        MatchingEngine me = new MatchingEngine(book);

        // ── SCENARIO 1: Build the Book ─────────────
        System.out.println("\n--- SCENARIO 1: Building the Book ---");

        // Add BID (buy) orders
        List<Trade> t1 = me.submitOrder(new Order(
            me.getNextOrderId(), "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2500.00, 500));

        List<Trade> t2 = me.submitOrder(new Order(
            me.getNextOrderId(), "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2499.00, 1000));

        List<Trade> t3 = me.submitOrder(new Order(
            me.getNextOrderId(), "RELIANCE",
            OrderSide.BID, OrderType.LIMIT, 2498.00, 750));

        // Add ASK (sell) orders
        List<Trade> t4 = me.submitOrder(new Order(
            me.getNextOrderId(), "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT, 2501.00, 300));

        List<Trade> t5 = me.submitOrder(new Order(
            me.getNextOrderId(), "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT, 2502.00, 500));

        List<Trade> t6 = me.submitOrder(new Order(
            me.getNextOrderId(), "RELIANCE",
            OrderSide.ASK, OrderType.LIMIT, 2503.00, 200));

        System.out.println("Book built with 6 orders:");
        BookDisplay.printBook(book);

        // ── SCENARIO 2: Full Match ─────────────────
        System.out.println(
            "\n--- SCENARIO 2: Full Match ---");
        System.out.println(
            "Submitting: BUY 300 @ 2501.00");
        System.out.println(
            "Expected  : Full match with SELL 300 @ 2501");

        List<Trade> trades2 = me.submitOrder(new Order(
            me.getNextOrderId(), "RELIANCE",
            OrderSide.BID, OrderType.LIMIT,
            2501.00, 300));

        System.out.println(
            "Book after full match:");
        BookDisplay.printBook(book);

        // ── SCENARIO 3: Partial Match ──────────────
        System.out.println(
            "\n--- SCENARIO 3: Partial Match ---");
        System.out.println(
            "Submitting: BUY 700 @ 2502.00");
        System.out.println(
            "Expected  : Match 500 @ 2502, remaining " +
            "200 goes into book");

        List<Trade> trades3 = me.submitOrder(new Order(
            me.getNextOrderId(), "RELIANCE",
            OrderSide.BID, OrderType.LIMIT,
            2502.00, 700));

        System.out.println(
            "Book after partial match:");
        BookDisplay.printBook(book);

        // ── SCENARIO 4: Market Order ───────────────
        System.out.println(
            "\n--- SCENARIO 4: Market Order ---");
        System.out.println(
            "Submitting: SELL MARKET 200 shares");
        System.out.println(
            "Expected  : Matches best available bid");

        List<Trade> trades4 = me.submitOrder(new Order(
            me.getNextOrderId(), "RELIANCE",
            OrderSide.ASK, OrderType.MARKET,
            0.0, 200));

        System.out.println(
            "Book after market order:");
        BookDisplay.printBook(book);

        // ── SCENARIO 5: Cancel Order ───────────────
        System.out.println(
            "\n--- SCENARIO 5: Cancel Order ---");
        System.out.println(
            "Cancelling Order #2 (BUY 1000 @ 2499)");

        boolean cancelled = me.cancelOrder(2);
        System.out.println(
            "Cancel result: " +
            (cancelled ? "SUCCESS" : "FAILED"));

        System.out.println(
            "Book after cancellation:");
        BookDisplay.printBook(book);

        // ── TRADE HISTORY ──────────────────────────
        System.out.println(
            "\n--- ALL TRADES EXECUTED ---");
        BookDisplay.printTrades(me.getTradeHistory());

        System.out.println(
            "==========================================");
        System.out.printf(
            "  Total Orders Processed : %d%n",
            me.getNextOrderId() - 1);
        System.out.printf(
            "  Total Trades Executed  : %d%n",
            me.getTotalTrades());
        System.out.println(
            "==========================================");
    }
}