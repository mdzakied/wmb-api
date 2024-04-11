<h1 align="center" id="title">wmb-api</h1>


<p align="center" id="description">wmb-api is a demo rest api for managing data resto 'warung makan bahari'.</p>


<h2>🧐 Features</h2>

Here're some of the project's best features :

*  Authentication and Authorization :
    * Registration User
    * Registration Admin
    * Login
    
*  CRUD Data :
    * Menu
        * Add Menu with Upload Image
        * Get Menu with Pagination
        * Get Menu by Id
        * Get Menu with Filter :
            * Name
            * Price
            * Min Price
            * Max Price
            * Page
            * Size
            * Sort By
            * Direction
        * Edit Menu with Upload Image
        * Delete Menu
          
    * Table
        * Add Table
        * Get All Table with Pagination
        * Get Table by Id
        * Get Table with Filter :
            * Name
            * Page
            * Size
            * Sort By
            * Direction
        * Edit Table
        * Delete Table
          
    * User
        * Add Auto User when Registration User 
        * Get All User with Pagination
        * Get User by Id
        * Get User with Filter :          
            * Name
            * Phone Number
            * Page
            * Size
            * Sort By
            * Direction
        * Edit User
        * Delete User
     
    * Transaction
        * Add Transaction (Response Transaction with Payment to Midtrans)
        * Get All Transaction with Pagination
        * Get Transaction by Id
        * Get Transaction with Filter :          
            * User Name
            * Menu Name
            * Transaction Date
            * Start Transaction Date
            * End Transaction Date
            * Page
            * Size
            * Sort By
            * Direction
        * Export Data Transaction to CSV with Filter Above
        * Export Data Transaction to PDF with Filter Above

  
<h2>🛠️ Installation Steps :</h2>

<p>1. Clone Repository</p>

```
git clone https://github.com/mdzakied/wmb-api.git
```

<br />
<p>2. Prepare database wmb-api with Import dbwmb_api.sql </p>

[dbwmb_api.sql](src/main/resources/docs)

<br />
<p>3. Complete configuration in Application Properties</p>

 * Database config for connecting to database
 * Multipart config for custom max/request file size and path location (feature upload image)
 * Midtrans config for key and snap-url for connecting to midtrans (feature payment)
 * Json Web Token (JWT) config for authentication and authorization (feature security)
 * Auth config for auto create superadmin for authentication and authorization (feature security)

[Application Properties](src/main/resources/application.properties)

<br />
<p>4. Run Project for Development</p>

Run in [WmbApiApplication](src/main/java/com/enigma/wmb_api/WmbApiApplication.java)

<br />
<p>5. Run Project for Deployment</p>

* Build Maven without Test
  
  ```
  mvn package -DskipTests
  ```
  
* Run Project
  
  ```
  java -jar target/wmb_api-0.0.1-SNAPSHOT.jar
  ```
  
  
<h2>💻 Built with</h2>

Technologies used in the project :

*   PostgreSQL
*   Java
*   Maven
*   Spring Boot
*   Spring Boot Starter Data JPA
*   Spring Boot Starter Validation
*   Spring Boot Starter Web
*   Spring Boot Devtools
*   Spring Boot Starter Test
*   Spring Boot Starter Security
*   Spring Security Test
*   Lombok
*   Java JWT
*   Itextpdf
*   Spring Doc OpenAPI Starter Webmvc UI
