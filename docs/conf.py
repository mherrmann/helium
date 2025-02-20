import os
import sys
from datetime import date

sys.path.insert(0, os.path.abspath('..'))


# -- Project information -----------------------------------------------------

project = 'helium'
copyright = '%s, Michael Herrmann' % date.today().year
author = 'Michael Herrmann'

# Also update ../setup.py when you change this:
release = '5.1.1'


# -- General configuration ---------------------------------------------------

# Add any Sphinx extension module names here, as strings. They can be
# extensions coming with Sphinx (named 'sphinx.ext.*') or your custom
# ones.
extensions = ['sphinx.ext.autodoc', 'sphinx.ext.githubpages']

# Add any paths that contain templates here, relative to this directory.
templates_path = ['_templates']

# List of patterns, relative to source directory, that match files and
# directories to ignore when looking for source files.
# This pattern also affects html_static_path and html_extra_path.
exclude_patterns = ['_build', 'Thumbs.db', '.DS_Store']

autodoc_member_order = 'bysource'