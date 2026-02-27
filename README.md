\# 📊 Limit Order Book Engine



!\[Java](https://img.shields.io/badge/Java-17-orange)

!\[Maven](https://img.shields.io/badge/Maven-3.x-blue)

!\[Tests](https://img.shields.io/badge/Tests-27%20Passing-brightgreen)

!\[License](https://img.shields.io/badge/License-MIT-green)

!\[Status](https://img.shields.io/badge/Status-Complete-brightgreen)



A production-grade Limit Order Book engine implemented

in Java — the core component of every stock exchange

(NSE, BSE, NYSE, NASDAQ).



---



\## 📌 Overview



Every trade you make on a stock exchange goes through

a Limit Order Book. This engine:



\- Accepts buy and sell orders from traders

\- Organizes them by price and time priority

\- Automatically matches buyers with sellers

\- Executes trades when prices agree

\- Handles partial fills and cancellations



---



\## ✨ Features



\- ✅ Limit order placement (buy and sell)

\- ✅ Market order instant execution

\- ✅ Automatic order matching engine

\- ✅ Partial fill handling

\- ✅ Order cancellation by ID

\- ✅ Price-Time priority (FIFO)

\- ✅ Real-time spread and mid price

\- ✅ Full trade history recording

\- ✅ 27 unit tests — all passing



---



\## 📐 How It Works



\*\*Price-Time Priority:\*\*

```

Same price? → First order placed wins (FIFO)

Different price? → Best price wins

BUY:  Higher price = better priority

SELL: Lower price  = better priority

```



\*\*Matching Rule:\*\*

```

TRADE happens when:

BID price >= ASK price



Trade executes at RESTING order price

```



\*\*Order Types:\*\*

```

LIMIT  → Wait for specific price or better

MARKET → Execute immediately at best price

```



---



\## 🗂️ Project Structure

```

limit\_order\_book\_java/

├── pom.xml

├── README.md

├── data/

│   └── sample\_orders.csv

└── src/

&nbsp;   ├── main/java/com/trading/lob/

&nbsp;   │   ├── Main.java

&nbsp;   │   ├── model/

&nbsp;   │   │   ├── Order.java

&nbsp;   │   │   ├── OrderSide.java

&nbsp;   │   │   ├── OrderType.java

&nbsp;   │   │   └── Trade.java

&nbsp;   │   ├── book/

&nbsp;   │   │   ├── OrderBook.java

&nbsp;   │   │   ├── PriceLevel.java

&nbsp;   │   │   └── MatchingEngine.java

&nbsp;   │   └── display/

&nbsp;   │       └── BookDisplay.java

&nbsp;   └── test/java/com/trading/lob/

&nbsp;       ├── OrderBookTest.java

&nbsp;       ├── MatchingEngineTest.java

&nbsp;       └── PriceLevelTest.java

```



---



\## 🚀 How to Run



\*\*1. Clone the repository\*\*

```bash

git clone https://github.com/rupeshgoswami/limit\_order\_book\_java.git

cd limit\_order\_book\_java

```



\*\*2. Compile\*\*

```bash

mvn compile

```



\*\*3. Run\*\*

```bash

mvn exec:java

```



\*\*4. Test\*\*

```bash

mvn test

```



---



\## 📊 Sample Output

```

==========================================

&nbsp;     LIMIT ORDER BOOK ENGINE

==========================================



--- SCENARIO 1: Building the Book ---

+------------------------------------------+

|    LIMIT ORDER BOOK - RELIANCE           |

+------------------------------------------+

|   BID (BUY)         | ASK (SELL)         |

|  Qty      Price     | Price      Qty     |

+--------------------+---------------------+

|   500   2500.00     | 2501.00   300      |

|  1000   2499.00     | 2502.00   500      |

|   750   2498.00     | 2503.00   200      |

+--------------------+---------------------+

| Best Bid  : 2500.00                      |

| Best Ask  : 2501.00                      |

| Spread    : 1.00                         |

| Mid Price : 2500.50                      |

| Orders    : 6                            |

+------------------------------------------+



--- SCENARIO 2: Full Match ---

&nbsp; TRADE EXECUTED: 300 shares @ 2501.00



--- SCENARIO 3: Partial Match ---

&nbsp; TRADE EXECUTED: 500 shares @ 2502.00



--- SCENARIO 4: Market Order ---

&nbsp; TRADE EXECUTED: 200 shares @ 2500.00



--- SCENARIO 5: Cancel Order ---

&nbsp; Cancel result: SUCCESS



==========================================

&nbsp; Total Orders Processed : 9

&nbsp; Total Trades Executed  : 3

==========================================

```



---



\## 🧪 Test Results

```

-----------------------------------------------

&nbsp;T E S T S

-----------------------------------------------

OrderBookTest        → Tests run: 11, Failures: 0

MatchingEngineTest   → Tests run: 9,  Failures: 0

PriceLevelTest       → Tests run: 7,  Failures: 0

-----------------------------------------------

Total: 27 | Failures: 0 | Errors: 0

BUILD SUCCESS

-----------------------------------------------

```



---



\## 🛠️ Data Structures



| Structure | Java Type | Purpose | Performance |

|-----------|-----------|---------|-------------|

| Bid side | TreeMap (reverse) | Sorted high to low | O(log n) |

| Ask side | TreeMap (natural) | Sorted low to high | O(log n) |

| Order lookup | HashMap | Find order by ID | O(1) |

| Price level queue | LinkedList | FIFO order priority | O(1) |



---



\## 📚 Key Operations



| Operation | Performance | Description |

|-----------|-------------|-------------|

| Add order | O(log n) | Insert at correct price level |

| Cancel order | O(1) | Remove by order ID |

| Match orders | O(1) | Best bid vs best ask |

| Get best bid | O(1) | Highest buy price |

| Get best ask | O(1) | Lowest sell price |

| Get spread | O(1) | Ask minus bid |



---



\## 🛠️ Tech Stack



| Tool | Version | Purpose |

|------|---------|---------|

| Java | 17 | Core language |

| Maven | 3.x | Build management |

| JUnit 5 | 5.10.0 | Unit testing |



---



\## 👤 Author



\*\*Rupesh Goswami\*\*

\- GitHub: \[@rupeshgoswami](https://github.com/rupeshgoswami)



---



\## 📄 License



This project is licensed under the MIT License.

```

