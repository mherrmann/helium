"""
This script presents how Helium can be used to automatically update your
Facebook status. When you run it, it first prompts you for your Facebook
username and password. Once you have entered this, Helium logs you in and
updates your status.
"""
from helium.api import *

email = raw_input("Email address registered with Facebook: ")
password = raw_input("Password registered with Facebook: ")

start_firefox("facebook.com")
write(email, into="Email or Phone")
write(password, into="Password")
click("Log In")
# Note: The next line only really works when the Firefox window has focus, ie.
# it doesn't work when pasting the contents of this file into python.exe.
write("Test", into="Update Status")
click("Post")