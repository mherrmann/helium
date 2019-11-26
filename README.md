# Helium - Simple web automation

This repository contains the source code of Helium. Its purpose is to give
former customers of BugFree Software a way to continue to use Helium. The code
given here has had all former licensing restrictions and source code obfuscation
removed.

## Setting up the development environment

While Helium itself is cross-platform, its build currently requires you to run
Windows.

### 1. Install binary dependencies

#### Python
Install 32 bit Python 2.7.10 into `C:\Python27` and the 64 bit version into 
`C:\Python27x64`. Rename `C:\Python27x64\python.exe` to 
`C:\Python27x64\python27x64.exe`. Repeat for the other Python versions 2.6 and
3.3.

#### Other dependencies
 * [Microsoft Visual C++ Compiler for Python 2.7 ](https://www.microsoft.com/en-us/download/details.aspx?id=44266)
 * JDK 7
 * Apache Maven 3
 * MiKTeX 2.9.4813 - when asked whether to install missing packages on-the-fly,
   select *Yes*.
 * Git

### 2. Set environment variables ##
* `JAVA_HOME` should point to your JDK installation, eg. `C:\Program Files\Java\jdk1.6.0_45`.
* Add the following to your `PATH`:
  * The `bin/` directory of where you extracted Maven. Eg. `C:\Program Files\apache-maven-3.0.4\bin`.
  * The Python directories: `C:\Python27;c:\Python27\Scripts;c:\Python26;c:\Python26x64;c:\Python27x64;c:\Python33;c:\Python33x64`
  * `%JAVA_HOME%\bin`
  * Git

## 3. Check out the Git repository ##

## 4. Install Python dependencies ##

Look inside `pom.xml` to see which versions, and install them:

```
python -m pip install git+git://github.com/mherrmann/python-pkcs1@vPKCS1_VERSION
python -m pip install selenium==SELENIUM_VERSION
```

## 6. Test ##

```
mvn clean verify -Pie -Pfirefox -Pchrome
```

If you run into problems with IE, follow the relevant steps on
http://heliumhq.com/docs/internet_explorer.

---