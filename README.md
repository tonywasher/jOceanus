# jOceanus Project

**jOceanus** is an overarching project that pulls together the underlying components of the embedded **MoneyWise**
project. The project is still under development, and should currently be viewed as a set of source examples,
with no pre-built downloadable files available.

The source is maintained on GitHub, having been migrated from **SourceForge**, although the namespace currently still references **SourceForge**.

The project webSite is available on GitHub.



# Project Structure

The underlying projects are as follows.

* **MoneyWise** application

  * a Personal Finance application providing functionality similar to **Quicken** and **MoneyDance**

* Utility classes (**Oceanus**)

  * a basic decimal class allowing values with fixed numbers of decimals to be manipulated without resorting to
    the **BigDecimal** class
  * a set of data conversion utilities

* Gui classes (**Tethys**)

  * a set of classes providing an abstraction layer on top of the Gui allowing both Swing and JavaFX implementations.

* Encryption classes (**GordianKnot**)

  * A set of classes providing triple encryption and extended support for encrypted zipFiles.

* Data classes (**Metis**/**Prometheus**)

  * Access classes for MySQL/PostgreSQL/SQLServer/MariaDB databases as well as OpenOffice/Excel spreadsheets.
  * Secure data model for database.
  * Generic versioned data model enabling easy undo/reset.
  * Integrated Data Viewer
  * Preferences Support.
  * a set of classes providing generic thread support on top of both javaFX and Swing

* Analysis classes (**Themis**)

  * Classes for analysing a source tree.

## Building the project

The project is built via **Maven** and requires **Java21**.

There is additional support for a **gradle** build, but this will not build the webSite.

A distribution directory is created as part of the build process including run scripts for windows/linux together with a cut-down JRE runtime.
The runtime is currently created from a hard-coded list, since **jdeps** does not seem to provide a complete list of java dependencies in a complex
multi-module modular system. It is an ongoing task to look to improve this situation in order to automatically build this list.

