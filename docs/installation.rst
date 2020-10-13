Installing Helium
=================

You need Python 3 and Chrome or Firefox.

If you already know Python, then the following command should be all you need:


.. code-block:: bash

    pip install helium

Otherwise - Hi! I would recommend you create a virtual environment in the
current directory. Any libraries you download (such as Helium) will be placed
there. Enter the following into a command prompt:

.. code-block:: bash

    python3 -m venv venv

This creates a virtual environment in the `venv/` directory. To activate it:

.. code-block:: bash

    # On Mac/Linux, bash shell:
    source venv/bin/activate
    # On Windows:
    call venv\Scripts\activate.bat

Then, install Helium using `pip`:

.. code-block:: bash

    python -m pip install helium

Now enter :code:`python` into the command prompt and the command :code:`from
helium import *` and you are ready to get started!
