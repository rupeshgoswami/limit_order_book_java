package com.trading.lob.display;

import com.trading.lob.book.OrderBook;
import com.trading.lob.book.PriceLevel;
import com.trading.lob.model.Trade;

import java.util.List;
import java.util.Map;

/**
 * Displays the Order Book in a readable format.
 *
 * Example output:
 * ╔══════════════════════════════════════════╗
 * ║       LIMIT ORDER BOOK - RELIANCE        ║
 * ╠══════════════════════════════════════════╣
 * ║   BID (BUY)        ASK (SELL)            ║
 * ║   Qty    Price  |  Price    Qty          ║
 * ╠══════════════════════════════════════════╣
 * ║  1000   2500.00 | 2501.00   300          ║
 * ║   500   2499.00 | 2502.00   500          ║
 * ║   750   2498.00 | 2503.00   200          ║
 * ╚══════════════════════════════════════════╝
 */
public class BookDisplay {

    // Number of price levels to show
    private static final int DISPLAY_LEVELS = 5;

    // ─────────────────────────────────────────────────
    // Print the full order book
    // ─────────────────────────────────────────────────
    public static void printBook(OrderBook book) {

        System.out.println();
        System.out.println(
            "+------------------------------------------+");
        System.out.printf(
            "|    LIMIT ORDER BOOK - %-18s|%n",
            book.getSymbol());
        System.out.println(
            "+------------------------------------------+");
        System.out.printf(
            "| %-20s | %-20s|%n",
            "   BID (BUY)", "ASK (SELL)");
        System.out.printf(
            "| %-20s | %-20s|%n",
            "  Qty      Price", "Price      Qty");
        System.out.println(
            "+--------------------+---------------------+");

        // Get bid and ask levels as arrays
        Object[] bidEntries = book.getBids()
            .entrySet().toArray();
        Object[] askEntries = book.getAsks()
            .entrySet().toArray();

        int levels = Math.max(
            Math.min(bidEntries.length, DISPLAY_LEVELS),
            Math.min(askEntries.length, DISPLAY_LEVELS)
        );

        if (levels == 0) {
            System.out.println(
                "|          Book is Empty               " +
                "     |");
        }

        for (int i = 0; i < levels; i++) {

            // Bid side
            String bidStr = "                    ";
            if (i < bidEntries.length) {
                Map.Entry<Double, PriceLevel> bidEntry =
                    (Map.Entry<Double, PriceLevel>)
                    bidEntries[i];
                bidStr = String.format(
                    "%6d   %9.2f",
                    bidEntry.getValue().getTotalVolume(),
                    bidEntry.getKey()
                );
            }

            // Ask side
            String askStr = "                    ";
            if (i < askEntries.length) {
                Map.Entry<Double, PriceLevel> askEntry =
                    (Map.Entry<Double, PriceLevel>)
                    askEntries[i];
                askStr = String.format(
                    "%9.2f   %-6d",
                    askEntry.getKey(),
                    askEntry.getValue().getTotalVolume()
                );
            }

            System.out.printf(
                "| %-20s | %-20s|%n",
                bidStr, askStr);
        }

        System.out.println(
            "+--------------------+---------------------+");

        // Print market stats
        printStats(book);
        System.out.println();
    }

    // ─────────────────────────────────────────────────
    // Print market statistics
    // ─────────────────────────────────────────────────
    public static void printStats(OrderBook book) {

        System.out.println(
            "+------------------------------------------+");

        if (!book.hasBids() && !book.hasAsks()) {
            System.out.println(
                "| No market data available               " +
                "   |");
        } else {
            System.out.printf(
                "| Best Bid  : %-28.2f|%n",
                book.getBestBid());
            System.out.printf(
                "| Best Ask  : %-28.2f|%n",
                book.getBestAsk());
            System.out.printf(
                "| Spread    : %-28.2f|%n",
                book.getSpread());
            System.out.printf(
                "| Mid Price : %-28.2f|%n",
                book.getMidPrice());
            System.out.printf(
                "| Orders    : %-28d|%n",
                book.getTotalOrders());
        }

        System.out.println(
            "+------------------------------------------+");
    }

    // ─────────────────────────────────────────────────
    // Print all executed trades
    // ─────────────────────────────────────────────────
    public static void printTrades(List<Trade> trades) {

        if (trades.isEmpty()) {
            System.out.println("No trades executed yet.");
            return;
        }

        System.out.println();
        System.out.println(
            "+------------------------------------------+");
        System.out.printf(
            "| %-40s|%n", "  TRADE HISTORY");
        System.out.println(
            "+--------+---------+--------+--------------+");
        System.out.printf(
            "| %-6s | %-7s | %-6s | %-12s |%n",
            "ID", "Price", "Qty", "Value");
        System.out.println(
            "+--------+---------+--------+--------------+");

        double totalValue = 0;
        int totalQty = 0;

        for (Trade trade : trades) {
            System.out.printf(
                "| %-6d | %7.2f | %6d | %12.2f |%n",
                trade.getTradeId(),
                trade.getPrice(),
                trade.getQuantity(),
                trade.getValue()
            );
            totalValue += trade.getValue();
            totalQty   += trade.getQuantity();
        }

        System.out.println(
            "+--------+---------+--------+--------------+");
        System.out.printf(
            "| %-6s   %9s  %6d   %12.2f |%n",
            "TOTAL", "", totalQty, totalValue);
        System.out.println(
            "+------------------------------------------+");
        System.out.println();
    }

    // ─────────────────────────────────────────────────
    // Print a single trade as it happens
    // ─────────────────────────────────────────────────
    public static void printTradeAlert(Trade trade) {
        System.out.println(
            "  >> TRADE: " + trade.getQuantity() +
            " shares @ " + trade.getPrice() +
            " | Value: " + trade.getValue()
        );
    }
}