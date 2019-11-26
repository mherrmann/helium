"""
In this example Helium is used to open the ebay.com website and search for
'iPhone 5' using advanced search options.
"""
from helium.api import *
start_firefox("www.ebay.com")
click("Advanced")
write("iphone 5", into="Enter keywords or item number")
select("In this category:", "Cell Phones & Accessories")
click(CheckBox("Title and description"))
click(CheckBox("New"))
click(CheckBox("Free shipping"))
click(Link("Search"))