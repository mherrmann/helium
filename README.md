# Selenium-python, 50% easier.

![Helium Demo](demo.gif)

[Selenium's Python bindings](https://selenium-python.readthedocs.io/)
are great for web automation. But they are too difficult to use:
 
 * To identify elements on a web page, Selenium requires you to use HTML IDs
   such as `myBtn`, XPaths such as `//button[text()="Some text"]` or CSS
   selectors such as `p.content`. These do not just make your scripts hard to
   read, they also break easily when the web site changes.
 * You need to manually download and manage the respective WebDrivers. So if you
   want to automate Chrome for instance, it's not enough to simply install
   Selenium. You are also forced to download ChromeDriver and place it on your
   `PATH`.
 * Many Selenium scripts are unstable with respect to timing issues:
   By default, if you try to click on an element with Selenium and that element
   is not yet visible, your script fails. People try to fix this by adding
   `sleep` statements to their code. But they too sometimes fail, and make the
   code less readable.
 * Selenium does let you explicitly wait for certain conditions, such as an
   element to appear. However, its
   [API for doing so](https://selenium-python.readthedocs.io/waits.html#explicit-waits)
   is way too complicated for such a simple and common task.
 * You cannot interact with elements in nested iFrames unless you first
   instruct Selenium to "switch to" the respective iFrame. This makes working
   with iFrames in Selenium extremely (and unnecessarily) tedious.
 * Similarly for popups and windows, Selenium often requires you to deal with
   arcane "window handles" to switch between windows.

Helium wraps around Selenium to offer a more high-level API that solves all of
the above problems. As a result, Helium scripts are typically 50% shorter,
easier to read and more stable than corresponding Selenium scripts.

At the same, because Helium is merely a _wrapper_ around Selenium, you can
freely mix the two libraries. For example:

```python
# A Helium function:
driver = start_chrome()
# A Selenium API:
driver.execute_script("alert('Hi!');")
```

So in other words, you don't lose anything by using Helium over Selenium alone.

## Getting started

To get started with Helium, all you need is Python 3 and Chrome or Firefox.

If you already know Python, then the following command should get you started:

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

Now enter `python` into the command prompt and (for instance) the commands at
the top of this page (`from helium import *`, ...).

## API Documentation

If you use an IDE such as PyCharm, you should get auto-completion and
documentation for Helium's various functions. Otherwise, please look at
[this Python file](helium/__init__.py). It lists all of Helium's public
functions. I have not yet had time to bring this into a more readable state,
sorry.

## Status of this project

I have too little spare time to maintain this project for free. If you'd like
my help, please go to my [web site](http://herrmann.io) to ask about my
consulting rates. Otherwise, unless it is very easy for me, I will usually not
respond to emails or issues on the issue tracker. I will however accept and
merge PRs. So if you add some functionality to Helium that may be useful for
others, do share it with us by creating a Pull Request. For instructions, please
see below.

## Contributing

Pull Requests are very welcome. Please follow the same coding conventions as the
rest of the code, in particular the use of tabs over spaces.

Before you submit a PR, ensure that the tests still work:

```bash
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

Helium used to be available for both Java and Python. But I because I now only
use it from Python, I didn't have time to bring the Java implementation up to
speed as well. Similarly for Internet Explorer: Helium used to support it, but
since I have no need for it, I removed the (probably broken) old implementation.
