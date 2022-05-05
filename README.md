# Selenium-python but lighter: Helium

[Selenium-python](https://selenium-python.readthedocs.io/) is great for web
automation. Helium makes it easier to use. For example:

![Helium Demo](docs/helium-demo.gif)

Under the hood, Helium forwards each call to Selenium. The difference is that
Helium's API is much more high-level. In Selenium, you need to use HTML IDs,
XPaths and CSS selectors to identify web page elements. Helium on the other hand
lets you refer to elements by user-visible labels. As a result, Helium scripts
are typically 30-50% shorter than similar Selenium scripts. What's more, they
are easier to read and more stable with respect to changes in the underlying web
page.

Because Helium is simply a wrapper around Selenium, you can freely mix the two
libraries. For example:

```python
# A Helium function:
driver = start_chrome()
# A Selenium API:
driver.execute_script("alert('Hi!');")
```

So in other words, you don't lose anything by using Helium over pure Selenium.

In addition to its more high-level API, Helium simplifies further tasks that are
traditionally painful in Selenium:

- **Web driver management:** Helium ships with its own copies of ChromeDriver
  and geckodriver so you don't need to download and put them on your PATH.
- **iFrames:** Unlike Selenium, Helium lets you interact with elements inside
  nested iFrames, without having to first "switch to" the iFrame.
- **Window management.** Helium notices when popups open or close and focuses /
  defocuses them like a user would. You can also easily switch to a window by
  (parts of) its title. No more having to iterate over Selenium window handles.
- **Implicit waits.** By default, if you try click on an element with Selenium
  and that element is not yet present on the page, your script fails. Helium by
  default waits up to 10 seconds for the element to appear.
- **Explicit waits.** Helium gives you a much nicer API for waiting for a
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

## Installation

To get started with Helium, you need Python 3 and Chrome or Firefox.

If you already know Python, then the following command should be all you need:

```bash
pip install helium
```

Otherwise - Hi! I would recommend you create a virtual environment in the
current directory. Any libraries you download (such as Helium) will be placed
there. Enter the following into a command prompt:

```bash
python3 -m venv venv
```

This creates a virtual environment in the `venv` directory. To activate it:

```bash
# On Mac/Linux:
source venv/bin/activate
# On Windows:
call venv\scripts\activate.bat
```

Then, install Helium using `pip`:

```bash
python -m pip install helium
```

Now enter `python` into the command prompt and (for instance) the commands in
the animation at the top of this page (`from helium import *`, ...).

## Your first script

I've compiled a [cheatsheet](docs/cheatsheet.md) that quickly teaches you all
you need to know to be productive with Helium.

## API Documentation

The documentation for this project can be found
[here](https://selenium-python-helium.readthedocs.io/en/latest/).

## Status of this project

I have too little spare time to maintain this project for free. If you'd like
my help, please go to my [web site](http://herrmann.io) to ask about my
consulting rates. Otherwise, unless it is very easy for me, I will usually not
respond to emails or issues on the issue tracker. I will however accept and
merge PRs. So if you add some functionality to Helium that may be useful for
others, do share it with us by creating a Pull Request. For instructions, please
see [Contributing](#Contributing) below.

## How you can help

I find Helium extremely useful in my own projects and feel it should be more
widely known. Here's how you can help with this:

- Star this project on GitHub.
- Tell your friends and colleagues about it.
- [Share it on Twitter with one click](https://twitter.com/intent/tweet?text=I%20find%20Helium%20very%20useful%20for%20web%20automation%20with%20Python%3A%20https%3A//github.com/mherrmann/helium)
- Share it on other social media
- Write a blog post about Helium.

With this, I think we can eventually make Helium the de-facto standard for web
automation in Python.

## Contributing

Pull Requests are very welcome. Please follow the same coding conventions as the
rest of the code, in particular the use of tabs over spaces. Also, read through my
[PR guidelines](https://gist.github.com/mherrmann/5ce21814789152c17abd91c0b3eaadca).
Doing this will save you (and me) unnecessary effort.

Before you submit a PR, ensure that the tests still work:

```bash
pip install -Ur requirements/test.txt
python setup.py test
```

This runs the tests against Chrome. To run them against Firefox, set the
environment variable `TEST_BROWSER` to `firefox`. Eg. on Mac/Linux:

```bash
TEST_BROWSER=firefox python setup.py test
```

On Windows:

```bash
set TEST_BROWSER=firefox
python setup.py test
```

If you do add new functionality, you should also add tests for it. Please see
the [`tests/`](tests) directory for what this might look like.

## History

I (Michael Herrmann) originally developed Helium in 2013 for a Polish IT startup
called BugFree software. (It could be that you have seen Helium before at
https://heliumhq.com.) We shut down the company at the end of 2019 and I felt it
would be a shame if Helium simply disappeared from the face of the earth. So I
invested some time to modernize it and bring it into a state suitable for open
source.

Helium used to be available for both Java and Python. But because I now only
use it from Python, I didn't have time to bring the Java implementation up to
speed as well. Similarly for Internet Explorer: Helium used to support it, but
since I have no need for it, I removed the (probably broken) old implementation.
