# Other Project Links #
<a href='http://www.panix.com/~melling/a/marketapp-debug.apk'>MarketApp apk</a><br />
<a href='http://spreadsheets.google.com/pub?key=0ArVkFagUZg7bdDd3Q3E0dm5qZi1kUi1mM0xfLUkwUXc&hl=en&output=html'>Task List</a><br />
<a href='http://picasaweb.google.com/mmellinger66/MarketApp#'>Screen Mockups</a><br />
[Detailed Component Design Notes](MarketAppComponentNotes.md)

# Introduction #

The **MarketApp** is a stock market application for the Android platform.  In short, a <a href='http://finance.yahoo.com'>Yahoo Finance</a> or <a href='http://finance.google.com'>Google Finance</a> for Android.

# Watchlist #

A watchlist is simply a list of tickers entered by the user.  For example:

  * AAPL
  * MSFT
  * GOOG
  * INTC

The Watchlist screen(s) will show this information:

  * Ticker
  * Company Name
  * Last Price
  * Daily Percent Change or  Daily Dollar Change

|AAPL|Apple Computer|248.21|3.14%|
|:---|:-------------|:-----|:----|
|GOOG|Google|502.21|-2.3%|
|INTC|Intel|22.54|2.3%|
|MSFT|Microsoft|19.21|-5.2%|

# Portfolio #

The portfolio screen(s) will contain the same information that is on the Watchlist screen.  In addition, the user can enter the quantity of shares long/short a stock, the average price of those shares, and the date purchased.

<table border='1'>
<tr><th>Ticker</th><th>Quantity</th><th>Avg Price</th><th>Purchase Date</th><th>Today's Dollar Chg</th><th>Dollar Change</th></tr>
<tr><td>INTC</td><td>100,000</td><td>22.82</td><td>5/26/2010</td></tr>
<tr><td>GOOG</td><td>-250,000</td><td>502.12</td><td>5/2/2010</td></tr>
</table>

# Yahoo Prices #

Equity pricing information can be obtained from Yahoo using a URL like the following:

<a href='http://download.finance.yahoo.com/d/quotes.csv?s=AAPL&f=sb2b3jkm6'><a href='http://download.finance.yahoo.com/d/quotes.csv?s=AAPL&f=sb2b3jkm6'>http://download.finance.yahoo.com/d/quotes.csv?s=AAPL&amp;f=sb2b3jkm6</a></a>

The fields used:<br />
  * s = symbol
  * b2 = ask real-time
  * b3 = bid real-time
  * j = 52 week low
  * k = 52 week high

## Returned Results ##
"AAPL",244.00,243.50,130.91,272.46,+11.17%

Symbol, ask, bid, 52 week low, 52 week high, % change from 200 day mavg<br />

## Misc Sources ##
<a href='http://www.seangw.com/wordpress/index.php/2010/01/formatting-stock-data-from-yahoo-finance/'>Comprehensive List of Yahoo Price Request Options</a><br />
<a href='http://answers.yahoo.com/question/index?qid=20060918121957AAZaIdi'>Yahoo Answers</a>

# Historical Yahoo Prices #

Historical prices can be retrieved from Yahoo using the following URL:

<a href='http://ichart.finance.yahoo.com/table.csv?s=AAPL&a=5&b=1&c=2010&d=05&e=17&f=2010&g=d'><a href='http://ichart.finance.yahoo.com/table.csv?s=AAPL&a=5&b=1&c=2010&d=05&e=17&f=2010&g=d'>http://ichart.finance.yahoo.com/table.csv?s=AAPL&amp;a=5&amp;b=1&amp;c=2010&amp;d=05&amp;e=17&amp;f=2010&amp;g=d</a></a>

  * a = start month - 1
  * b = start day
  * c = start year
  * d = end month  - 1
  * e = end day
  * f = end year
  * g = d(aily),w(eekly), m(onthly)