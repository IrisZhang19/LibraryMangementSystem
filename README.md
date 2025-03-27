# Library Management System

The project is a library management system based on Java and SpringBoot. It allows administrators to manage books and
allow users( library members ) to browse, borrow and return books. 


## Features

- **User Management:** The system has two initial roles, user as a regular library member, and
  admin as the administrator. It allows everyone to sign up as a user, and only allows admin to sign up a new admin.
- **Role-Based Access Control:** Different roles have different access level. Public can access APIs starts with ```/api/public/ ```  such as browsing books and searching books, and APIs for sign up as a user. Users can access APIs for sining in, borrow and return books. Only admin can access APIs starting with ```/api/admin/```, these are used to manage books and sign up new admins.
- **JWT authentication:** It implements JWT token for authentication, when a user or an admin sign in, a token will be generated for future usage, such as when borrowing or returning books.
- **Book Management:** It allows admin to manage books, including create, update and delete book entries. Book is created with a Catogry associated with it. It also allows public to browse books and search books based on category, author and title.
- **Pagination:** Books are retrieved with pagination when browsing and searching.
- **Borrow and Return Function:** It allows users (members) to borrow books and return the books that they borrowed.


## Installation

This project uses Java 17, Spring Boot and Maven, so should not have issue to set up, 
the IDE should configure it without any trouble. 
The following steps can be followed.

### Prerequisites
- **Java 11+** Check with `java -version`
- **Maven**  Check with `mvn -v`

### Set up and Run
1. **Clone the repository**
   ```bash
   git clone https://github.com/IrisZhang19/LibraryMangementSystem.git
   cd library-management-system
2. **Build the Project**
    ```bash
    ./mvnw clean install
3. **Run the Application**
    ```bash
    ./mvnw spring-boot:run
   
4. **Access the Application**
- API runs on ``http://localhost:8080``
- H2 Database Conslole: ``http://localhost:8080/h2_console``
    - JDBC URL: ``jdbc:h2:mem:test``
    - User Name: ``sa``
    - Password: No Password needed


## About Testing
- Mainly implemented unit tests for controller layer and service layer. 
Focused on testing the core functionalities such as category management, book management, borrow/return books and authentication.
- Have Jacoco to generate a unit test coverage report. Currently, the coverage is 72% of total instructions.

## API Reference 
### Borrow and Return Functions
#### Borrow book
Authenticated user can borrow a book.

```http
  POST /api/borrow/{bookId}
```

| PathVariable | Type   | Description                                    |
|:-------------|:-------|:-----------------------------------------------|
| `bookId`     | `Long` | **Required** The Id of the book to be borrowed |

#### Return Book
Authenticated user can return a book that they have borrowed.

```http
  POST /api/borrow/{bookId}
```

| PathVariable | Type   | Description                                    |
|:-------------|:-------|:-----------------------------------------------|
| `bookId`     | `Long` | **Required** The Id of the book to be returned |



## Next steps
- Test more. Due to limited time, I only managed to unit test the core functions, integration testing can be added if 
there is more time to test the whole flow of the functionalities. In addition, security testing is also important to have. 
- Expand functionalities. For example, a signed-in user should be able to see their own borrow/return history, 
and an admin should be able to see users' borrow/return history. 
- Add customized exception handler. For example right now if BadRequest is encountered, 
no specific error message is show in the response body, a global exception handler can be added to 
allow this function.
- The project uses H2, an in-memory database for fast development and testing. In the future, it can be migrated 
to a production level database. 
