#!/bin/bash

set -e

NUM=${1-10}
URL=${2-http://34.95.52.30}

LENGTH=$( http --check-status "$URL/bookings" | jq '. | length' )
ARRIVAL=$( http --check-status "$URL/availabilities" start=="$(date -Idate --date="+1 day")" | jq -r .[0] )
DEPARTURE=$( http --check-status "$URL/availabilities" start=="$(date -Idate --date="+1 day")" | jq -r .[1] )

createBooking() {
  http --check-status --ignore-stdin POST "$URL/bookings" fullname="Nicolas Brouard" email="nicolas.brouard@gmail.com" arrivalDate="$ARRIVAL" departureDate="$DEPARTURE"
}

for (( i=0; i<NUM; i++ )); do
  createBooking &
done

wait

NEW_LENGTH=$( http --check-status "$URL/bookings" | jq '. | length' )
if (( LENGTH + 1 == NEW_LENGTH )); then
  echo SUCCESS
else
  echo FAILURE
  exit 1
fi
