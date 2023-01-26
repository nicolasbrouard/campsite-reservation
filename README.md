# Campsite reservation

![workflow status badge](https://github.com/nicolasbrouard/campsite-reservation/actions/workflows/gradle.yml/badge.svg)

## Links 

- [This GitHub repo](https://github.com/nicolasbrouard/campsite-reservation/)
- [SonarQube](https://sonarcloud.io/dashboard?id=nicolasbrouard_campsite-reservation)
- [Postman test](https://www.postman.com/nbrouard/workspace/camping-reservation)
- [Swagger UI](http://34.95.52.30/swagger-ui.html)
- [Gradle build scan](https://scans.gradle.com/s/nhc3bhyqgwz26)
- [Documentation](#documentation)

## Description of the challenge

Back-end Tech Challenge: Campsite reservation REST API service

An underwater volcano formed a new small island in the Pacific Ocean last month. All the conditions on the island seems
perfect, and it was decided to open it up for the public to experience the pristine uncharted territory.

The island is big enough to host a single campsite so everybody is very excited to visit. In order to regulate the
number of people on the island, it was decided to come up with an online web application to manage the reservations. You
are responsible for design and development of a REST API service that will manage the campsite reservations.

To streamline the reservations a few constraints need to be in place

- The campsite will be free for all.
- The campsite can be reserved for max 3 days.
- The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.
- Reservations can be cancelled anytime.
- For sake of simplicity assume the check-in & check-out time is 12:00 AM

### System Requirements

- The users will need to find out when the campsite is available. So the system should expose an API to provide
  information of the availability of the campsite for a given date range with the default being 1 month.
- Provide an end point for reserving the campsite. The user will provide his/her email & full name at the time of
  reserving the campsite along with intended arrival date and departure date. Return a unique booking identifier back to
  the caller if the reservation is successful.
- The unique booking identifier can be used to modify or cancel the reservation later on. Provide appropriate end
  point(s) to allow modification/cancellation of an existing reservation
- Due to the popularity of the island, there is a high likelihood of multiple users attempting to reserve the campsite
  for the same/overlapping date(s). Demonstrate with appropriate test cases that the system can gracefully handle
  concurrent requests to reserve the campsite.
- Provide appropriate error messages to the caller to indicate the error cases.
- In general, the system should be able to handle large volume of requests for getting the campsite availability.
- There are no restrictions on how reservations are stored as long as system constraints are not violated.

### Notes

Like any travel booking sites, there will likely be multiple requests coming in at the same time but there is only 1
campsite, so you'll need to come up with a concurrency solution to handle and sort through those multiple requests. The
things to keep in mind while you're working on the challenge are having a working and scalable concurrency solution to
be able to handle multiple instances, good test coverage, exception handling, validations, code division, packaging,
separation between entity and response, documentation, usage of tech like Spring Boot, Swagger, Lombok, H2, etc.

## Documentation

The campsite-reservation service is written in java 11 with Spring Boot.

This is a simple REST API service (developed with spring-boot-web) with a Postgres database accessed via spring-boot-data-jpa.

The application is packaged as a Helm chart and deployable in a Kubernetes cluster.

The application is currently deployed with 2 replicas in Google Kubernetes Engine (in Autopilot mode) and publicly accessible
at http://34.95.52.30/swagger-ui.html.

### Open source technologies

- **Java 11**
- **Gradle 7.0** to build
- **Lombok** to generate boilerplate code
- **Spring Boot** (spring-web, spring-webmvc, spring-data-joa)
- **Docker** to package the application in a container
- **[Distroless](https://github.com/GoogleContainerTools/distroless)** as a base docker image
- **Helm** to install the application
- **Swagger** to document the REST API and to test
- **H2** for unit test with in-memory database
- **Postgres** database used in GKE deployment
- **Mockito** for unit test
- **Assertj** for the unit test assertions

### GitHub workflow

The source code is hosted on GitHub with 2 configured workflows:

- [Java CI with gradle](.github/workflows/gradle.yml)
which:
  - [builds](https://scans.gradle.com/s/nhc3bhyqgwz26),
  - executes [unit tests](https://scans.gradle.com/s/nhc3bhyqgwz26/tests),
  - and analyses the code with [SonarQube](https://sonarcloud.io/dashboard?id=nicolasbrouard_campsite-reservation).
- [Build and Deploy to GKE](.github/workflows/google.yml)
which:
  - creates a bootJar (gradle plugin org.springframework.boot),
  - builds a docker image,
  - pushes the docker image to `gcr.io/nbrouard-campsite-reservation/campsite-reservation`,
  - deploy the helm chart to GKE (upgrade the current release),
  - executes the helm tests.

### Packages and Classes

The main package is `com.upgrade.interview.challenge.campsitereservation`.

#### Subpackage `persistence`

Each campsite reservation is represented by an entity `BookingEntity` and managed by the interface
`BookingRepository` (which is a `JpaRepository`).

The `BookingEntity` class contains the properties:
- id (primary key)
- version (serves as an optimistic lock value. The version is used to ensure integrity when performing the merge
  operation and for optimistic concurrency control).
- email
- fullname
- arrivalDate
- departureDate

Each date for which the campsite is reserved, is represented by an entity `BookingDate` and managed by the interface
`BookingDateRepository` (which is also a `JpaRepository`). This is not strictly necessary, but it 
simplifies the logic to check if a date is available. The `BookingDate` entity has one field `date` which
is the primary key.

The class `BookingService` contains the logic of the reservation system and allows the usage of transactions.

#### Subpackage `rest`

The `BookingController` manages the REST API. The POJO `Booking` is used in requests and responses, in 
addition to the class `java.time.LocalDate` which is used to represent the dates.

The `Booking` class contains the properties:
- id
- email
- fullname
- arrivalDate
- departureDate

See the section [About dates](#about-dates).

### Subpackage `validation`

`BookingConstraint` is an annotation which defines the constraints on the `Booking` class. The component `BookingValidator`
executes the validation logic and reports appropriate error message when the constraints are violated.

### Subpackage `exception`

This package contains the declaration of 3 custom exception classes that extends `RuntimeException`:
- `AlreadyBookedException`
- `BadRequestException`
- `BookingNotFoundException`

See the section [Exception handling](#exception-handling).

### Class diagram

![Class diagram](diagrams/mermaid-diagram-20210416171426.jpg)

### About dates

For sake of simplicity we assume the check-in & check-out time is 12:00 AM, so we don't need to store the time.
The class `java.time.LocalDate` is used everywhere to manipulate the date.

A reservation with an arrival date 2021-04-14 at 12:00 AM and a departure date 2021-04-17 at 12:00 AM is represented
by a `Booking` object with fields `arrivalDate = LocalDate("2021-04-14")` and `departudeDate = LocalDate("2021-04-17")`.

In terms of availabilities, the start date is included, and the end date is excluded. In the above example, it means
the days 2021-04-14, 2021-04-15, and 2021-04-16 are booked and 2021-04-17 is available.

### Configuration

There are 3 custom configurations:
```yaml
campsite:
  max-booking-duration-in-days: 3
  max-days-ahead-of-arrival: 31
  min-days-ahead-of-arrival: 1
```
- `campsite.max-booking-duration-in-days` specifies the maximum length of a reservation days.
- `campsite.max-days-ahead-of-arrival` specifies the maximum number of days before the arrival date of a booking.
- `campsite.min-days-ahead-of-arrival` specifies the minimum number of days before the arrival date of a booking.

### Validation

Input data validation is separated from the controller and the model via annotations. The annotation `@BookingConstraint`
is applied on the Booking object, and the validation is executed by the component `BookingValidator`.

Other standards validation annotations are used like `@Email`, `@Future`, `@NonBlank`, and `@NonNull`.

### Exception handling

The class `BookingControllerAdvice` is a `@ControllerAdvice` that declares several exception handlers. Error are returned
to the REST API user via a json string, and an appropriate status code.

Example of json error response:
```json
{
    "message": "Invalid date 'APRIL 31'",
    "status": "BAD_REQUEST"
}
```

HTTP status code that can be returned:
- 404 NOT_FOUND in case a booking by id cannot be found (for example for deletion)
- 409 CONFLICT in case a booking conflicts with another booking
- 400 BAD_REQUEST when request parameters or request json body are invalid
- 500 INTERNAL_SERVER_ERROR for other types of error

### REST API design

6 operations are exposed on the endpoint (detailed in the section [Internal logic, transaction and concurrency](#internal-logic-transaction-and-concurrency)).
- `GET /availabilities`: Get information of the availability of the campsite for a given date range with the default being 1 month.
  This operation returns the list of available dates.
  2 optional request parameters `start` and `end` can be used to specify the date range. The default value for `start` is today.
  The default value for `end` is 1 month since the start date.
- `GET /bookings`: Get the list of all reservations.
- `GET /booking/{id}`: Get the information of the reservation with the given id.
- `POST /booking`: Reserve the campsite with the information of the json body:
  ```json
  {
    "email": "email@gmail.com",
    "fullname": "Full name",
    "arrivalDate": "2021-04-17",
    "departureDate": "2021-04-20"
  }
  ```
- `PUT /booking/{id}`: Update the reservation with the given id with the information of the json body (see above).
- `DELETE /booking/{id}`: Cancel the reservation with the given id.

### Internal logic, Transaction and Concurrency

#### GET /availabilities - getAvailabilitiesBetween(start, end)

Thanks to the table `BookingDate`, the logic is simple:
- List all the dates between start and end dates -> list1
- With the `BookingDateRepository`, find all dates between start and end -> list2
- Remove all elements of list2 from list1.
- Return list1.

This logic is in the method `BookingService#getAvailabilities()` which is annotated with `@Transactional(readOnly = true)`.
It is a readonly transaction because it does not modify the values.

#### GET /bookings - getBookingList

This operation also uses a readonly transaction to find all booking from the `Booking` table sorted by arrival dates.

#### GET /bookings/{id} - getBooking(id)

This operation also uses a readonly transaction to find by id 1 booking from the `Booking` table.

#### POST /bookings - addBooking(booking)

This operation needs to be protected against concurrent access. A simple mutex cannot work when there
are multiple replicas of the application. I used several mechanisms. First, the `BookingService#add()` method
is annotated with `@Transactional(isolation = Isolation.SERIALIZABLE)` which prevents against dirty reads, 
phantom reads and non-repeatable read.

The logic is:
- Find all reserved date in the `BookingDate` table between the arrival date, and the departure date.
  This method is annotated with `@Lock(LockModeType.PESSIMISTIC_WRITE)` which allows using *select for update*.
  It can throw a CannotAcquireLockException in case of concurrent access on the same rows. 
- If at least 1 date is booked within this time range, throws AlreadyBookedException.
- Otherwise, save all the date within this time range in the `BookingDate` table. This can throw
  a DataIntegrityViolationException in case 
- And, save the booking in the `Booking` table. This method is annotated with `@Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)`
  which implements a pessimist write lock with version update. Indeed, the `BookingEntity` contains a version field
  to protect against concurrent updates.

In general, a TransientDataAccessException can be thrown indicating that the operation might be able to succeed
if it is retried.

#### PUT /bookings/{id} - updateBooking(id, booking)

Updating a booking is very similar to the creation of a booking. Actually, the only difference is that the booking dates
of the old booking are deleted before calling `addBooking(booking)`. The method `BookingService#update()` is also annotated
with `@Transactional(isolation = Isolation.SERIALIZABLE)`.

#### DELETE /bookings/{id} - deleteBooking(id)

In a regular transaction (`isolation.DEFAULT`), the booking dates, and the booking entity are deleted.

### Scalability and availability

The design of this system permits to have multiple replicas of the Spring Boot java component, thus giving high availability and scalability.

The PostgreSQL also provides high availability using streaming replication.

### Unit tests and Code coverage

The code is covered at 97% by unit tests. See the [SonarQube report](https://sonarcloud.io/dashboard?id=nicolasbrouard_campsite-reservation).

- The `BookingValidatorTest` class tests the validation of the constraints.
- The `BookingcontrollerTest` class tests the REST API with `MockMvc`. The repository classes are mocked.
- The `BookingServiceTest` class tests the `BookingService`, and the repository classes using an H2 in-memory database.
- The `BookingServiceConcurrencyTest` class contains special tests for testing concurrent access, using an H2 in-memory
  database. Test are written with an `ExecutorService` that submit 2 tasks. A delay is artificially added to make sure the second
  task is executed in the middle of the first task. This tests guarantees that the transactions are correctly managed.
  See the section [Transactions](#internal-logic-transaction-and-concurrency).

### Load testing

I used the tool [Hey](https://github.com/rakyll/hey) to test 2 replicas deployed in GKE.
I sent 300 requests `GET /availabilities` on 10 threads.

```shell
./hey_linux_amd64 -n 300 -c 10 http://34.95.52.30/availabilities

Summary:
Total:	3.4361 secs
Slowest:	0.2826 secs
Fastest:	0.0249 secs
Average:	0.1079 secs
Requests/sec:	87.3077

Response time histogram:
0.025 [1]	|
0.051 [20]	|■■■■■■
0.076 [8]	|■■
0.102 [132]	|■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
0.128 [84]	|■■■■■■■■■■■■■■■■■■■■■■■■■
0.154 [16]	|■■■■■
0.180 [13]	|■■■■
0.205 [14]	|■■■■
0.231 [6]	|■■
0.257 [5]	|■■
0.283 [1]	|
```

### Spring profiles

There are 4 profiles:
- **default** which uses h2 in memory-database
- **postgres** which uses a postgres database. The postgres database can be run in a docker image:
  ```shell
  docker run --network host --rm --name my-postgres -e POSTGRES_PASSWORD=mysecretpassword postgres
  ```
- **dev** which is similar to default
- **h2-db** which uses a h2 database. The database can be run in a docker image:
  ```shell
  docker run --network host --rm --name my-h2 buildo/h2database
  ```

### How to execute

#### Locally

Run:

```shell
./gradlew bootRun
```

The default profile uses h2 in-memory database.

Swagger UI is embedded and available at http://localhost:8080/swagger-ui.html.

#### On Kubernetes

When creating a release with GitHub, the workflow deploys the application, and a load balancer to Google Kubernetes Engine.

The load balancer has an external IP which allows to access the application with a public IP: http://34.95.52.30/swagger-ui.html

Here is the command to manually deploy with helm:

```shell
helm upgrade --install campsite-reservation src/main/helm/ \
  --set image.repository=gcr.io/nbrouard-campsite-reservation/campsite-reservation \
  --set image.tag=5c2c7d9b4d71a89d7e7c2a250c3a59dd4627b4d5
```

### Tests using [httpie](https://httpie.io/)

Some sample requests:

```shell
URL=http://localhost:8080
#URL=http://34.95.52.30
http $URL/bookings
http $URL/availabilities
http -v POST $URL/bookings fullname="Nicolas B" email="nbrouard@test.com" arrivalDate='2021-05-01' departureDate='2021-05-03'
http $URL/bookings
http $URL/bookings/1
http -v PUT $URL/bookings/1 fullname="Nicolas B" email="nbrouard@test.com" arrivalDate='2021-05-02' departureDate='2021-05-03'
http $URL/bookings/1
http DELETE $URL/bookings/1
```

The folder [test](test) contains 2 simple bash scripts for functional and load testing. 

### Reservation examples

```json
[
  {
    "email": "brouard1@gmail.com",
    "fullname": "Lison Brouard",
    "arrivalDate": "2021-04-17",
    "departureDate": "2021-04-20"
  },
  {
    "email": "KiwietBleuette@boutdchou.mimichou",
    "fullname": "Kiwi et Bleuette",
    "arrivalDate": "2021-04-28",
    "departureDate": "2021-04-30"
  },
  {
    "email": "Mirabelle@pusslt.mimichou",
    "fullname": "Mirabelle",
    "arrivalDate": "2021-05-08",
    "departureDate": "2021-05-11"
  }
]
```

### Swagger

Swagger UI, accessible at http://localhost:8080/swagger-ui.html or http://34.95.52.30/swagger-ui.html can be used
to read the REST API documentation and execute some requests.

### Testing with Postman

The postman public workspace is https://www.postman.com/nbrouard/workspace/camping-reservation and contains some test requests.

### SonarQube

Static analysis of the code: https://sonarcloud.io/dashboard?id=nicolasbrouard_campsite-reservation

### H2 console

H2-console can be enabled with the property `spring.h2.console.enabled=true` and accessible at http://localhost:8080/h2-console/.

### Known limitations

1. My GKE deployment is limited to 250m CPU and 1Gi of memory.

2. I found inconsistent behaviors between h2 in-memory and postgres DB:
  - With h2, I needed to add `@Transactional(isolation = Isolation.SERIALIZABLE)` to prevent concurrent creation of booking.
  - With postgres, this is not necessary because the "select for update" seems to work fine.
  - Adding `@Transactional(isolation = Isolation.SERIALIZABLE)` is not perfect because it also prevents concurrent creation of bookings
    that not in conflict. 
  - The H2 documentation says: "Serializable: Dirty reads, non-repeatable reads, and phantom reads aren't possible.
    Note that this isolation level in H2 currently doesn't ensure equivalence of concurrent and serializable execution of transactions."
    
3. Database password should be passed to the application via kubernetes secret.
