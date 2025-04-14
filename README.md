# Library Management System

The project is a library management system based on Java and SpringBoot. It allows administrators to manage books and
allows users( library members ) to browse, borrow and return books. 


## Features

- **User Management:** The system has two initial roles, user as a regular library member, and
  admin as the administrator. It allows everyone to sign up as a user, and only allows admin to sign up a new admin.
- **Role-Based Access Control:** Different roles have different access level. Public can access APIs starts with ```/api/public/ ```  such as browsing books and searching books, and APIs for sign up as a user. Users can access APIs for sining in, borrow and return books. Only admin can access APIs starting with ```/api/admin/```, these are used to manage books and sign up new admins.
- **JWT authentication:** It implements JWT token for authentication, when a user or an admin sign in, a token will be generated for future usage, such as when borrowing or returning books.
- **Book Management:** It allows admin to manage books, including create, update and delete book entries. Book is created with a catogery associated with it. It also allows public to browse books and search books based on category, author and title.
- **Pagination:** Books are retrieved with pagination when browsing and searching.
- **Borrow and Return Function:** It allows users (members) to borrow books and return the books that they borrowed.


## Note on Branches
Right now there are two branches:
| Branch | Description |
|--------|-------------|
| `submitted-version` | Original version submitted for the assignment |
| `main` | Improved version after submission |

The `submitted-version` branch reflects exactly what was submitted during the assignment. It can be checked out with
```bash
git checkout submitted-version
```
The `main` branch contains improvements such as 
 - Add global exception handling and defined three different customized exceptions including ResourceNotFoundException, ValidationExceptoin and BusinessException. Refactor the relevant methods and tests to implement this handling.
 - Update category service. Category cannot be deleted if there is still books related to it. Category should be created and updated with a unique category name ignore case. Refactor and tests for this change.
 - Update book management and transaction service for soft deletion of books. Book will not be deleted from the database but to be set to inactive. Users cannot borrow an inactive book. Admins cannot update an inactive book. Refator and add tests for this change.
 - Update book management. Add partial update for books with PATCH. Make sure copie_borrowed, copies_available and copies_total are inconsistant when manage the book and also borrow and return books.

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
    - Password: No password needed


## About Testing and Documentation
- Mainly implemented unit tests for controller layer and service layer. 
Focused on testing the core functionalities such as category management, book management, borrow/return books and authentication.
- Have Jacoco to generate a unit test coverage report. Currently, the coverage is 72% of total instructions.
- I used AI to help with the code comments due to limited time.

## API Reference

### Authentication 

It has APIs for sign in and sign up.

#### Sign in 
User and Admin can sign in providing username and password. If they are valid, a JWT token will be generated for later usage.
```http
  POST /api/auth/signin
```

**request body**  
```json
{
    "username" : "user1",
    "password" : "password1"
}
```

#### Sign up as user
Public can sign up as a new user, provided with valid and unique username, email address and also password in the request body.
```http
  POST /api/auth/signup
```
**request body**  
```json
{
    "username" : "user",
    "password" : "password"
    "email" : "user1@test.com"
}
```

#### Sign up as Admin
Admin can sign up as a new admin, provided with valid and unique username, email address and also password in the request body. Only admin can access this, public and User cannot sign up a new admin.
```http
  POST /api/auth/admin/signup
```
**request body**  
```json
{
    "username" : "admin",
    "password" : "password"
    "email" : "admin@test.com"
}
```
### Borrow and Return Books

#### Borrow book
Authenticated user can borrow a book. 

```http
  POST /api/borrow/{bookId}
```

| PathVariable | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `bookId` | `Long` | **Required** The Id of the book to be borrowed |

#### Return Book
Authenticated user can return a book that they have borrowed. 

```http
  POST /api/borrow/{bookId}
```

| PathVariable | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `bookId`      | `Long` | **Required** The Id of the book to be returned |


### Book Management 

This corrosponds to book controller, where books can be retrieved, created, updated and deleted by public or authenticated users and admins.

#### Create a Book

Admin can create a book under an existing category with path variable `categoryId` and a request body which includes book details. Title and Author fields should not be blank. Total copies should be no less than the available copies. And it returns the book created.
```http
  POST /api//admin/categories/{categoryId}/book
```
| PathVariable | Type     | Description             |
| :-------- | :------- | :------------------------- |
| `categoryId` | `Long` | Indicates the category this book should belong to |

**request body**
```json
{
    "title" : "book1",
    "author" : "author1",
    "copiesTotal" : 3,
    "copiesAvailable" : 3,
    "description" : "description for book 1 from author 1"
} 
```

#### Update a Book
Admin can update an exisiting book, such as the title, the author, and the availablility. And it returns the updated book. 
```http
  PUT /api//admin/book/{bookId}
```
| PathVariable | Type     | Description             |
| :-------- | :------- | :------------------------- |
| `bookId` | `Long` | Indicates the book to be updated|

**request body**  
```json
{
    "title" : "book1 title changed",
    "author" : "author1",
    "copiesTotal" : 5,
    "copiesAvailable" : 3,
    "description" : "description for book 1 from author 1 changed",
    "category" : {
        "categoryId" : 1
    }
}
```
#### Update a Book with PATCH
Admin can update an exisiting book, such as the title, the author, and the availablility. And it returns the updated book. 
```http
  PATCH /api//admin/book/{bookId}
```
| PathVariable | Type     | Description             |
| :-------- | :------- | :------------------------- |
| `bookId` | `Long` | Indicates the book to be updated|

**request body**  
```json
{
    "title" : "book1 title changed",
}
```
#### Delete a Book
Admin can delete an exisiting book and it returns the deleted book. It does not delete the book in the database, it sets the book to inactive so that the users cannot borrow this book.
```http
  PUT /api//admin/book/{bookId}
```
| PathVariable | Type     | Description             |
| :-------- | :------- | :------------------------- |
| `bookId` | `Long` | Indicates the book to be deleted|

#### Get All Books
People including public can retrieve all the books with infomation such as book title, author, description, available copies and etc.

```http
  GET /api/public/books
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `pageNumber` | `Integer` | The page to be retrieved, default to 0 |
| `pageSize` | `Integer` | The books of one page, default to 3|
| `sortBy` | `String` | Indicates how to display books, default to book Id|
| `sortOrder` | `String` | Indicate the order to display, can be `asc` or `dsc`, default to asceding|

Similar book retrieving APIs including get books by category: 
```http
  GET /api/public/categories/{categoryId}/books
```
get books by author
```http
  GET /api/public/author
```
with extra request parameter 
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `author` | `String` | The author name to be searched by |

get books by title, partially matching
```http
  GET /api/public/books/title
```
with extra request parameter 
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `title` | `String` | The tile of the book to be searched by |

**request body**
```json
{
    "title" : "book1",
    "author" : "author1",
    "copiesTotal" : 3,
    "copiesAvailable" : 3,
    "description" : "description for book 1 from author 1 updated"
} 
```

#### Category Management

Similar to book management, category can be retrieved, created, updated and deleted by users.

The APIs are 
```http
  GET /api/public/categories
```
```http
  POST /api/admin/categories
```
```http
  PUT /api/admin/categories/{categoryId}
```
```http
  DELETE /api/admin/categories/{categoryId}
```

## Next steps
- Test more. Due to limited time, I only managed to unit test the core functions, integration testing can be added if 
there is more time to test the whole flow of the functionalities. In addition, security testing is also important to have. 
- Expand functionalities. For example, a signed-in user should be able to see their own borrow and return history, 
and an admin should be able to see users' borrow and return history. 
- Deployment. The project uses H2, an in-memory database for fast development and testing. In the future, it can be migrated 
to a production level database such as PostGreSQL.
