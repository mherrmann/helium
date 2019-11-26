"""
The following code shows how Helium can be used to automatically post a tweet
on Twitter:
"""
from helium.api import *

email = raw_input("Twitter username: ")
password = raw_input("Twitter password: ")
message = "Trying web automation with #helium from @BugFreeSoftware. heliumhq.com"

start_chrome('twitter.com')
write(email, into="Phone, email or username")
write(password, into="Password")
click("Log in")
click("Tweet")
write(message)
click("Tweet")
kill_browser()