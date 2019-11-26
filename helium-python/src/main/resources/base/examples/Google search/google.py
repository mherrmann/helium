"""
This script uses Helium to automatically perform a Google search for the term
"Helium", and opens the Wikipedia article on the subject. If all goes well, it
prints "Test passed!". Otherwise, it prints "Test failed :(".
"""
from helium.api import *
start_chrome("google.com/?hl=en")
write("Helium")
press(ENTER)
click("Helium - Wikipedia")
if 'Wikipedia' in get_driver().title:
	print('Test passed!')
else:
	print('Test failed :(')
kill_browser()