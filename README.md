**DistanceAPI Service - Interview task**

**_Jeff White 14/12/2020_**

I have created a HTTP4S API service to calculate the distances between any two cities 
using the GeoLocation service that you provided.

The base code is from an original HTTP4S distribution that I set up some months ago 
for another project.
I improved the distribution code by adding OpenAPI endpoint code generation using  
GuardRails and created a static code handler and added SwaggerUI support. 
I have also upgraded the original SBT and jar versions to resolve 
issues with Option/Default parameters in the original version of GuardRails.

To calculate the distance between the two locations using the Haversine formula 
I have adapted the code referenced in https://davidkeen.com/blog/2013/10/calculating-distance-with-scalas-foldleft/
and I have "sanity checked" the calculation in a Scala Worksheet by checking that the arc distance
of one minute of latitude is 1 nautical mile.

To manage the intermittent response issues when using the GeoLocator service I have added a "retry" process that will attempt 
to reconnect on failure and have also created a location cache that will supply the location of previously 
saved cities if available. This should make the service more resilient.

**Service Endpoint**

There is a welcome page provide at http://localhost:8081 and a SwaggerUI interface at http://localhost:8081/swagger-ui

The distance endpoint is http://localhost:8081/distance?city=Qutyini&city=Osgiliath&units=NMI

**Recently completed TODO items**

I have fixed the retry service and confirmed the Location cache is thread safe.

I have added Unit Tests for the following;

_Distance calculation_ - check 1 minute of arc = 1 NMI

_Retry test on GeoLocator service_ - if service is down/intermittend the request will be retried for 30 seconds at 5 second intervals.
This can tested by stopping the service, if the cities are cached the service will continue, if the cities are not cached the
service will error.

_Cache Thread Safety_ - The location cache is tested with a large number of parallel "put" operations and the cache is checked for consistency.

_Cache Filled Test_ - More than the maximum number of city locations are added to the cache and the cache is checked for consistency.

