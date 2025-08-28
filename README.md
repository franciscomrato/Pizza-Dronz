# Pizza Dronz
Back-end for a Pizza Delivery system using drones. Data is retrieved from a REST-server and then validated. Orders are then processed and the drone's flightpath is calculated. System then returns a list of every move made by the drone, every order and its outcome and a GeoJson file outlining the drone's flightpath.

To run the project, run the following command:
```bash
java -jar target/PizzaDronz-1.0-SNAPSHOT.jar <date> <rest-url>
