# Campsite reservation

![workflow status badge](https://github.com/nicolasbrouard/campsite-reservation/actions/workflows/gradle.yml/badge.svg)

https://github.com/nicolasbrouard/campsite-reservation/

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

## System Requirements

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

## Notes

LocalDate is used to store date of the booking.

Booking from start (ex: 2021-04-14) to end (ex: 2021-04-17) means the arrival datetime is 2021-04-14 at 12:00 AM
and the departure date is 2021-04-17 at 12:00 AM. 

In terms of availabilities, it means the days 14th, 15th and 16th are booked and 17th is available.

## How to execute

```shell
./gradlew bootRun
```

Swagger UI is embedded and available at http://localhost:8080/swagger-ui.html.

## Sample requests using [httpie](https://httpie.io/)

```shell
http :8080/bookings
http :8080/availabilities
http -v POST :8080/bookings fullname="Nicolas Brouard" email="nicolas.brouard@gmail.com" arrivalDate='2021-05-01' departureDate='2021-05-03'
http :8080/bookings
http :8080/booking/1
http -v PUT :8080/bookings/1 fullname="Nicolas Brouard" email="nicolas.brouard@gmail.com" arrivalDate='2021-05-02' departureDate='2021-05-03'
http :8080/booking/1
http DELETE :8080/bookings/1
```
 
## Testing with Swagger UI

Visit http://localhost:8080/swagger-ui.html or http://34.95.46.35/swagger-ui.html.

## Testing with Postman

The postman public workspace is https://www.postman.com/nbrouard/workspace/camping-reservation.

## Deployment to Kubernetes

When creating a release with GitHub, the workflow deploys the application, and a load balancer to Google Cloud Engine.

The load balancer has an external IP which allows to access the application with a public IP.

## SonarQube

Static analysis of the code: https://sonarcloud.io/dashboard?id=nicolasbrouard_campsite-reservation