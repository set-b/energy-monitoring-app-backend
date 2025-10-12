# Ecommerce REST API

## Description

This application is intended to demonstrate competency and developmental understanding in the areas
of data access, Java Spring Boot, REST API principles, and CRUD functionality. The application
allows users to send requests for all CRUD functions in order to manipulate the data of Customer,
User, Product, and Order models.

## Prerequisites

The instructions detailed hereafter assume that the user is using the IDE IntelliJ IDEA, and the
ensuing steps may not be applicable if the user is using another development environment, tool kit,
or editor. First, start up IntelliJ and open the root folder of the application (file -> open ->
rest-greeting). You may also access this application directory through the command line if the path
environment variable has been enabled on your operating system. Command line shortcuts for opening
the application may be found in this link: (<https://tinyurl.com/mr4d3ub4>).

After opening the application directory in IntelliJ, select "EcommerceApplication" from the drop-
down menu on the configuration pane. The pane can be found near the top of the IDE, on the toolbar.
Press the play button to launch the Spring Boot Application. The Tomcat server will start on port
"8080" by default.

After the application has been started successfully, you may also import the postman collection link
into Postman via the web or desktop application to manually test requests. Swagger-UI may also be
used in order to manually test the API's endpoints. You can find these links in under the links
section.

## Viewing the database

The application is currently configured to use a local in-memory H2 database for the purposes of quick local testing and demonstration.

The original data source provider for this application is MySQL. If you wish to run the application connected to a MySQL database instead of a local in-memory database, uncomment the lines for the MySQL database in the YAML file, and input your MySQL credentials, including the username and password fields, as well as the url of your database host. Ensure that the the H2 configurations are also commented out or removed prior to starting the application. You may also use the
command line, or start the MySQL Workbench UI to view the Customer, Order, User, Product, and Item entities.

## Overview of Classes

All classes have been separated on the basis of concerns via packages, with the exception of the
application runner, EcommerceApplication. Below you will find an overview of the packages and
classes in the project structure.

### Config

This contains configurations for swagger documentation and springfox swagger ui. It will also
contain security configurations in the future.

### Constants

This contains all the string messages of errors and endpoints to preserve maintainability across the
application. These are located in the StringConstants class.

### Controllers

This contains the CustomerController, OrderController, ProductController, and UserController
classes, which take methods from their respective ServiceImpl classes (i.e. CustomerServiceImpl)
via autowiring to the service interfaces. It is mapped to basic CRUD functionality
of the common HTTP verbs, GET, PUT, POST, and DELETE. Endpoints have been written to create a
uniform
identification of resources.

### Data

This contains the DataLoader class, used to load the all Objects once the application is
launched.

### Exceptions

This package contains all custom exception classes used throughout the application, as well as the
Exception Controller, which is used as an exception handler with a global scope. Most of these
exceptions have been incorporated into the logic of the service implementation classes.

### Models

This package contains the constructors and properties of the Customer, Order, Item, Product, and
User
models, as well as the Address embeddable.

### Repositories

This package contains the Customer, Order, Product, User, and Item Repositories, which extend the
JpaRepository to access a library of methods to manipulate stored Objects via CRUD operations.

### Services

This package contains the interfaces for all services, Customer, Order, Product, and User, which are
implemented accordingly in the ServiceImpl classes. 

### Test Packages

This package contains integration and unit tests for the Controllers and ServiceImpl classes, 
respectively.

In order to run these tests, select the configuration file containing the name of the class you wish
test and press the play button. Each testing method also has play buttons to test on the
method-level,
as well. Right-clicking the class itself will present alternative testing options through a tool
tip.
You can run tests with coverage as an option, for example. Current testing line coverage is 100% for
both classes.

### Linting

The code can be linted through the shortcut Ctrl+Alt+L, or you can right-click the directory or file
containing the code you wish to reformat, and select "Reformat Code". This can be used to lint and
optimize the imports of subdirectories, as well.

### Helpful Links

Swagger UI: <http://localhost:8080/swagger-ui/index.html>

Github: <https://github.com/set-b>

