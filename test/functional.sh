#!/bin/bash

set -e

URL=${1-http://34.95.52.30}

http --check-status "$URL/bookings"
ARRIVAL=$( http --check-status "$URL/availabilities" start=="$(date -Idate --date="+1 day")" | jq -r .[0] )
DEPARTURE=$( http --check-status "$URL/availabilities" start=="$(date -Idate --date="+1 day")" | jq -r .[1] )
ID=$( http --check-status POST "$URL/bookings" fullname="Nicolas Brouard" email="nicolas.brouard@gmail.com" arrivalDate="$ARRIVAL" departureDate="$DEPARTURE" | jq '.id' )
http --check-status "$URL/bookings"
http --check-status "$URL/bookings/$ID"
http --check-status PUT "$URL/bookings/$ID" fullname="Nico" email="nico.brouard@gmail.com" arrivalDate="$ARRIVAL" departureDate="$DEPARTURE"
http --check-status "$URL/bookings/$ID"
http --check-status DELETE "$URL/bookings/$ID"
