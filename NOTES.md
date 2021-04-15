# Notes

- https://www.springboottutorial.com/hibernate-jpa-tutorial-with-spring-boot-starter-jpa
- https://www.baeldung.com/spring-data-jpa-query-by-date
- https://spring.io/guides/tutorials/rest/
- https://blog.mimacom.com/testing-pessimistic-locking-handling-spring-boot-jpa/

# Design

## Mandatory endpoints

```
GET /availabilities startDate endDate -> list of days available
POST /bookings email fullname arrivalDate departureDate -> OK (:id) / REFUSED
PUT /bookings/:id email fullname arrivalDate departureDate -> OK (:id) / REFUSED
DELETE /bookings/:id -> OK / NOT_FOUND
```

## More CRUD endpoints

```
GET /bookings
GET /bookings/available
GET /bookings/:id
```

## Config

- Max stay duration: 3
- Max booking plan: 30 days
- Min booking plan: 1 day

## Data

Booking

- id
- email
- fullname
- arrivalDate
- departureDate

## Insertion or modification

- Validate booking info -> REFUSED (400 bad request)  because too long, or too late, or too early.
- Start transaction
- Select booking between arrivalDate and departureDate
- if empty, insert new booking
- else REFUSED because already booked (409 conflict)
- Commit transaction
