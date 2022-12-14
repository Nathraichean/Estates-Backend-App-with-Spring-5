# Estates Backend App with Java Spring 5

This is the project that I made using the knowledge gained from the Java Srping academy I attended that was organized by Modis.

It uses Spring 5 with Java and a MySQL database. The database is autogenerated every time the app is launched and the creation is handled via Hibernate annotations.

This app cointains the following global entities - Images, Users, Agencies and Estates.

Images and Users are standalone whereas Agencies and Estates depend on Users.

All entities follow a standard CRUD model, using DTOs.

The most interesting part of the project is the ability to return filtered estates from the DB based on any amount of filters applied (ex. return estates with Build Year between "X" and "Y"). Pagination can also be set - page size and page number can be chosen for the results.

The api request mappings can be found through the postman_collection file. Just import it into Postman and you will see GET,POST,DELETE,UPDATE requests available with sample data within. Currently, the only CRUD operation that doesn't work is the Estate Delete due to a bug.

Not all functionality is implemented in this project. What needs to be implemented is :
- Security (Logged in users, role access restrictions, authentication token, etc.)
- Change it so that all controller methods return a "ResponseEntity" object.
- Improve the error exeption handling.
- Clean up the code a bit and add comments where necessary.
