# Diagrams

These diagrams are written with the [mermaid](https://mermaid-js.github.io/mermaid/#/) syntax.

This is not yet supported by GitHub, but the [Mermaid Live Editor](https://mermaid-js.github.io/mermaid-live-editor/) can
be used to render the diagrams.

```mermaid
classDiagram
BookingController --> BookingService : uses
BookingService --> BookingRepository : uses
BookingService --> BookingDateRepository : uses
BookingController : GET /bookings()
BookingController : POST /bookings()
BookingController : DELETE /bookings()
BookingController : GET /availabilities()
BookingRepository -- BookingEntity : entity
BookingDateRepository -- BookingDate : entity

BookingEntity : - *id*
BookingEntity : - version
BookingEntity : - email
BookingEntity : - fullname
BookingEntity : - arrivalDate
BookingEntity : - departureDate
BookingDate : - *date*

BookingController -- Booking : model
Booking : - id
Booking : - email
Booking : - fullname
Booking : - arrivalDate
Booking : - departureDate

BookingContraint --> Booking : annotates
BookingValidator -- BookingContraint
BookingValidator --> Booking : validates
```
[![](diagrams/mermaid-diagram-20210416171426.jpg)
