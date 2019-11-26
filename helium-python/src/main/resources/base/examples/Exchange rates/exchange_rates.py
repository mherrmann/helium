"""
In the script below we present how Helium can be used to automatically
read data from websites and make it available for further processing.
"""
from helium.api import *
 
def get_exchange_rate(base_currency, counter_currency):
    currency_pair = base_currency + counter_currency
    go_to("http://finance.yahoo.com/q?s=%s=X" % currency_pair)
    return S('.time_rtq_ticker').web_element.text
 
start_firefox()
print(get_exchange_rate("EUR", "USD"))
print(get_exchange_rate("USD", "JPY"))
kill_browser()