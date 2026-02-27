package com.trading.lob.book;

import com.trading.lob.model.Order;
import com.trading.lob.model.OrderSide;
import com.trading.lob.model.OrderType;
import com.trading.lob.model.Trade;

import java.util.ArrayList;
import java.util.List;

/**
 * The Matching Engine - Heart of the Order Book
 *
 * Responsible for:
 * 1. Receiving new orders
 * 2. Checking if they match existing orders
 * 3. Executing trades when match found
 * 4. Handling partial fills
 *
 * Matching Rules:
 * → BID price >= ASK price = MATCH!
 * → Trade happens at the RESTING order price
 * → FIFO priority at same price level
 *
 * Example:
 * Book: SELL 300 @ ₹2501
 * New:  BUY  500 @ ₹2502
 * → Match! Trade 300 @ ₹2501
 * → Remaining 200 BUY goes into book
 */
public class MatchingEngine {

    private final OrderBook orderBook;

    // All executed trades stored here
    private final List<Trade> tradeHistory;

    // Auto incrementing order ID
    private long nextOrderId = 1;

    public MatchingEngine(OrderBook orderBook) {
        this.orderBook    = orderBook;
        this.tradeHistory = new ArrayList<>();
    }

    // ─────────────────────────────────────────────────
    // SUBMIT new order to the engine
    // This is the main entry point
    // ─────────────────────────────────────────────────
    public List<Trade> submitOrder(Order order) {

        List<Trade> newTrades = new ArrayList<>();

        if (order.getType() == OrderType.MARKET) {
            // Market order: match immediately
            newTrades = matchMarketOrder(order);
        } else {
            // Limit order: try to match first
            newTrades = matchLimitOrder(order);

            // If not fully filled: add to book
            if (!order.isFilled()) {
                orderBook.addOrder(order);
            }
        }

        // Store all new trades in history
        tradeHistory.addAll(newTrades);
        return newTrades;
    }

    // ─────────────────────────────────────────────────
    // MATCH a limit order
    //
    // BUY limit order matches if:
    // buy price >= best ask price
    //
    // SELL limit order matches if:
    // sell price <= best bid price
    // ─────────────────────────────────────────────────
    private List<Trade> matchLimitOrder(Order order) {

        List<Trade> trades = new ArrayList<>();

        if (order.getSide() == OrderSide.BID) {
            // BUY order - match against asks
            while (!order.isFilled() && orderBook.hasAsks()) {

                double bestAsk = orderBook.getBestAsk();

                // Check if prices match
                // BUY price must be >= SELL price
                if (order.getPrice() < bestAsk) {
                    break; // No match possible
                }

                // Execute trade at ask price
                Trade trade = executeTrade(
                    order,
                    orderBook.getBestAskLevel().peek(),
                    bestAsk
                );

                if (trade != null) {
                    trades.add(trade);
                    // Clean up empty levels
                    orderBook.cleanEmptyLevel(
                        OrderSide.ASK, bestAsk);
                }
            }
        } else {
            // SELL order - match against bids
            while (!order.isFilled() && orderBook.hasBids()) {

                double bestBid = orderBook.getBestBid();

                // Check if prices match
                // SELL price must be <= BUY price
                if (order.getPrice() > bestBid) {
                    break; // No match possible
                }

                // Execute trade at bid price
                Trade trade = executeTrade(
                    orderBook.getBestBidLevel().peek(),
                    order,
                    bestBid
                );

                if (trade != null) {
                    trades.add(trade);
                    // Clean up empty levels
                    orderBook.cleanEmptyLevel(
                        OrderSide.BID, bestBid);
                }
            }
        }

        return trades;
    }

    // ─────────────────────────────────────────────────
    // MATCH a market order
    // Executes at whatever price is available
    // ─────────────────────────────────────────────────
    private List<Trade> matchMarketOrder(Order order) {

        List<Trade> trades = new ArrayList<>();

        if (order.getSide() == OrderSide.BID) {
            // Market BUY - eat through ask levels
            while (!order.isFilled() && orderBook.hasAsks()) {

                double bestAsk = orderBook.getBestAsk();
                Trade trade = executeTrade(
                    order,
                    orderBook.getBestAskLevel().peek(),
                    bestAsk
                );

                if (trade != null) {
                    trades.add(trade);
                    orderBook.cleanEmptyLevel(
                        OrderSide.ASK, bestAsk);
                }
            }
        } else {
            // Market SELL - eat through bid levels
            while (!order.isFilled() && orderBook.hasBids()) {

                double bestBid = orderBook.getBestBid();
                Trade trade = executeTrade(
                    orderBook.getBestBidLevel().peek(),
                    order,
                    bestBid
                );

                if (trade != null) {
                    trades.add(trade);
                    orderBook.cleanEmptyLevel(
                        OrderSide.BID, bestBid);
                }
            }
        }

        return trades;
    }

    // ─────────────────────────────────────────────────
    // EXECUTE a single trade between two orders
    //
    // Determines fill quantity:
    // fillQty = min(buyQty, sellQty)
    //
    // Example:
    // BUY  500 shares
    // SELL 300 shares
    // fillQty = min(500, 300) = 300
    // After: BUY has 200 remaining, SELL is done
    // ─────────────────────────────────────────────────
    private Trade executeTrade(Order buyOrder,
                                Order sellOrder,
                                double tradePrice) {

        if (buyOrder == null || sellOrder == null) {
            return null;
        }

        // Fill quantity = minimum of both orders
        int fillQty = Math.min(
            buyOrder.getQuantity(),
            sellOrder.getQuantity()
        );

        if (fillQty <= 0) return null;

        // Fill both orders
        buyOrder.fill(fillQty);
        sellOrder.fill(fillQty);

        // Update price level volumes
        PriceLevel level;
        if (sellOrder.isFilled()) {
            // Remove fully filled order from ask level
            level = orderBook.getAsks().get(tradePrice);
            if (level != null) {
                level.poll(); // Remove from front of queue
            }
        } else {
            // Partially filled - just reduce volume
            level = orderBook.getAsks().get(tradePrice);
            if (level != null) {
                level.reduceVolume(0); // Already updated via fill
            }
        }

        // Remove fully filled buy order from book
        if (buyOrder.isFilled()) {
            orderBook.getOrderMap().remove(
                buyOrder.getOrderId()
            );
        }

        // Remove fully filled sell order from book
        if (sellOrder.isFilled()) {
            orderBook.getOrderMap().remove(
                sellOrder.getOrderId()
            );
        }

        // Create trade record
        Trade trade = new Trade(
            orderBook.getSymbol(),
            buyOrder.getOrderId(),
            sellOrder.getOrderId(),
            tradePrice,
            fillQty
        );

        System.out.printf(
            "  TRADE EXECUTED: %d shares @ %.2f " +
            "(Buy#%d vs Sell#%d)%n",
            fillQty, tradePrice,
            buyOrder.getOrderId(),
            sellOrder.getOrderId()
        );

        return trade;
    }

    // ─────────────────────────────────────────────────
    // Generate next unique order ID
    // ─────────────────────────────────────────────────
    public long getNextOrderId() {
        return nextOrderId++;
    }

    // ─────────────────────────────────────────────────
    // Cancel an existing order
    // ─────────────────────────────────────────────────
    public boolean cancelOrder(long orderId) {
        return orderBook.cancelOrder(orderId);
    }

    // Getters
    public List<Trade> getTradeHistory() { return tradeHistory; }
    public OrderBook getOrderBook()      { return orderBook;    }
    public int getTotalTrades()          { return tradeHistory.size(); }
}