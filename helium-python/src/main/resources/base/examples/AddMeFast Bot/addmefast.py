"""
The following code uses Helium to implement a bot for the popular Likes-trading 
platform AddMeFast:
"""

from helium.api import *

ADDMEFAST_USER = raw_input("AddMeFast username: ")
ADDMEFAST_PW = raw_input("AddMeFast password: ")
TWITTER_USER = raw_input("Twitter username: ")
TWITTER_PW = raw_input("Twitter password: ")

start_chrome()

# First, ensure we're logged into Twitter:
go_to('twitter.com')
write(TWITTER_USER, into="Username or email")
write(TWITTER_PW, into="Password")
click("Sign in")

# Log in to AddMeFast:
go_to("addmefast.com")
write(ADDMEFAST_USER, into="Email")
write(ADDMEFAST_PW, into="Password")
click("Login")

# Now follow as many pages as possible:
go_to("http://addmefast.com/free_points/twitter")
Config.implicit_wait_secs = 10
while True:
    try:
        get_page_title = lambda: S('.fb_page_title').web_element.text
        page_title = get_page_title()
        click("Follow")
        already_following = Button("Following").exists()
        page_does_not_exist = Text("Sorry, that page").exists()
        if already_following or page_does_not_exist:
            get_driver().close()
            click("Skip")
        else:
            follow_btns = find_all(Button("Follow"))
            topmost_follow_btn = sorted(follow_btns, key=lambda btn: btn.y)[0]
            click(topmost_follow_btn)
            get_driver().close()
        wait_until(lambda: get_page_title() != page_title)
    except Exception as e:
        print('Got %r. Restarting...' % e)
        go_to("http://addmefast.com/free_points/twitter")