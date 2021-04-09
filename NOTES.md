# Notes

- https://www.springboottutorial.com/hibernate-jpa-tutorial-with-spring-boot-starter-jpa
- https://www.baeldung.com/spring-data-jpa-query-by-date

# Design

## Mandatory endpoints
```
GET /available startDate endDate -> list of days available
POST /booking email fullname arrivalDate departureDate -> SUCCESS (:id) / REFUSED
PUT /booking/:id email fullname arrivalDate departureDate -> SUCCESS (:id) / REFUSED
DELETE /booking/:id
```

## More endpoints
```
GET /booking/list
GET /booking/available
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
- Validate booking info -> REFUSED because too long, or too late, or too early.
- Start transaction
- Select booking between arrivalDate and departureDate
- if empty, insert new booking
- else REFUSED because already booked  
- Commit transaction
