DistanceAPI Service - Interview task

Jeff White 14/12/2020

I have created a HTTP4S API service to calculate the distances between any two cities 
using the GeoLocation service provided.

The base code is from an original HTTP4S distribution I set up some months ago 
for another project.
I have improved this distribution by adding OpenAPI endpoint code generation using  
GuardRails and created a static code handler and SwaggerUI support. 
I have also upgraded the original SBT and jar versions to resolve 
issues with Option/Default parameters in the original version of GuardRails.

To calculate the distance between the two locations using the Haversine formula 
I have adapted the code referenced in https://davidkeen.com/blog/2013/10/calculating-distance-with-scalas-foldleft/
I have "sanity checked" the calculation in a Scala Worksheet by checking that the arc distance
of one minute of latitude is 1 nautical mile.

To manage the intermittent response issues with the GeoLocator service I have added a "retry" process that will attempt 
to reconnect on failure and have also created a location cache that will supply the location of previously 
saved cities.

Service Endpoint

The is a welcome page provide at http://localhost:8081 and a SwaggerUI interface at http://localhost:8081/swagger-ui

The distance endpoint is http://localhost:8081/distance?city=Qutyini&city=Osgiliath&units=NMI

TODO

Due to time limitations over the weekend I have not been able to fully test 
the handling of the intermittent GeoLocator service nor thread safety
of the location cache. The retry process does not seem to be catching the connection exceptions correctly. 
I need to test this further. I also need to add Unit Tests.