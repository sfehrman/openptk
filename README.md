# openptk

> "the freedom to choose how user interfaces are built"

## Overview

OpenPTK is an open source project that provides a collection of tools and sample applications that Web and Java developers can use to integrate custom applications with user provisioning systems. Using industry standard interfaces, developers can build flexible user management applications that support Enterprise-class, department/ group level and Web 2.0 type user provisioning environments.

## Architecture

Project OpenPTK is a three-tier architecture (server side) which enables developers to focus on the business application interface, not on the underlying user data repositories. The "back-end" user data repository is abstracted through the "Service Tier". The "Framework Tier" integrates the Server and Service tiers while also managing configurations, logging/debugging and provisioning operations. The "Server Tier" provides the RESTful Web Service (supporting JSON and XML).  The RESTful Web Service offers multiple ways to "represent" the data between the Server and the applications. Bi-directional data can be represented using either JSON or XML syntax.  Read-only data (out bound) can also be represented as HTML (Web Browsers) or Plain Text (command line utilities like "curl").  The Server provides the core infrastructure and several applications are provided which use the Server.  There's a number of "Client Tier" interfaces, that extend the RESTful Web Service, which addresses various development options.  For example, the Identity Central application includes an integrated User Interface which leverages AJAX technologies to implement a dynamic - rich experience.  It provides User Registration, End-User Self Service, Account Management, and monitoring of the infrastructure.  Other applications and client which leverage the server are described in the table below.

Documentation Site ... http://docs.openptk.org/
