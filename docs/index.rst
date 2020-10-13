Welcome to Helium's documentation!
==================================

What is Helium?
^^^^^^^^^^^^^^^

Selenium-python but lighter

`Selenium-python <https://selenium-python.readthedocs.io/>`_  is great for web
automation. Helium makes it easier to use.

Under the hood, Helium forwards each call to Selenium. The difference is that
Helium's API is much more high-level. In Selenium, you need to use HTML IDs,
XPaths and CSS selectors to identify web page elements. Helium on the other hand
lets you refer to elements by user-visible labels. As a result, Helium scripts
are typically 30-50% shorter than similar Selenium scripts. What's more, they
are easier to read and more stable with respect to changes in the underlying web
page.

Because Helium is simply a wrapper around Selenium, you can freely mix the two
libraries. For example:


.. code-block:: python

   # A Helium function:
   driver = start_chrome()
   # A Selenium API:
   driver.execute_script("alert('Hi!');")

So in other words, you don't lose anything by using Helium over pure Selenium.

.. toctree::
   :maxdepth: 2
   :caption: Contents:

   installation.rst
   main.rst
   contributors.rst



Indices and tables
==================

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`
