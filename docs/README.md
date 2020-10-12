# Welcome to Helium's documentation

The documentation is built using
[sphinx](https://www.sphinx-doc.org/en/master/index.html) and the theme used is
[sphinx-rtd-theme](https://sphinx-rtd-theme.readthedocs.io/en/stable/).

## Setting up documentation locally

Ensure you have `python` and `pip` installed on your
system and then run this command in the project root:

```bash
pip install -r requirements-dev.txt
make -C docs/ html
```

This will install all development dependencies for the project and then build
the documentation in HTML format in `docs/_build/` directory. Open
`docs/_build/index.html` in your browser to see the documentation.
