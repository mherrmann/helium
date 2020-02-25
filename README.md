# Helium

Helium is a Python library for automating web browsers. For example:

```python
from helium import *
start_chrome('github.com') # or start_firefox()
click('Sign in')
write('mherrmann', into='Username')
write('my password', into='Password')
click('Sign in')
go_to('github.com/mherrmann/helium')
click('Star')
kill_browser()
```

Under the hood, Helium forwards each call to
[Selenium](https://www.selenium.dev/). The difference is that Helium's API is
much more high-level. In Selenium, you need to use HTML IDs, XPaths and CSS
selectors to identify web page elements. Helium on the other hand lets you refer
to elements by their user-visible labels. As a result, Helium scripts are 30-50%
shorter than similar Selenium scripts. What's more, they are easier to read and
more stable with respect to changes in the underlying web page.

Because Helium is simply a wrapper around Selenium, you can freely mix the two
libraries. For example:

```python
# A Helium function:
driver = start_chrome()
# A Selenium API:
driver.execute_script("alert('Hi!');")
```

So in other words, you don't lose anything by using Helium over pure Selenium.

In addition to its high-level API, Helium simplifies further tasks that are
traditionally painful in Selenium:

 * **Web driver management:** Helium ships with its own copies of ChromeDriver
   and geckodriver so you don't need to download and put them on your PATH.
 * **iFrames:** Unlike Selenium, Helium lets you interact with elements inside
   nested iFrames, without having to first "switch to" the iFrame.
 * **Window management.** Helium notices when popups open or close and focuses /
   defocuses them like a user would. You can also easily switch to a window by
   (parts of) its title. No more having to iterate over Selenium window handles.
 * **Implicit waits.** By default, if you try click on an element with Selenium
   and that element is not yet present on the page, your script fails. Helium by
   default waits up to 10 seconds for the element to appear.
 * **Explicit waits.** Helium gives you a much nicer API for waiting for a
   condition on the web page to become true. For example: To wait for an element
   to appear in Selenium, you would write:
   ```python
   element = WebDriverWait(driver, 10).until(
       EC.presence_of_element_located((By.ID, "myDynamicElement"))
   )
   ```
   With Helium, you can write:
   ```python
   wait_until(Button('Download').exists)
   ```