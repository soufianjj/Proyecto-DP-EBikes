Project: E-Bike Tour
Authors: Soufian Jabbouri Jabbouri, Jaime Leno Herráez.

This project is an adaptation of the material for the Software Development
(Desarrollo de Programas) course at the University of Extremadura, based on
the initial structure of the book:

   Objects First with Java - A Practical Introduction using BlueJ
   Sixth edition
   David J. Barnes and Michael Kölling
   Pearson Education, 2016

This project simulates a network of charging stations for electric bikes in
a city (20x20 grid). Each ElectricBike travels from an initial location to a
target destination and, if its battery is not enough, goes first to the
BikeStation that minimizes the detour, recharges in a free Dock and
continues. The company (EBCompany) manages the bikes and the stations.

EXTRAS:
- Correct use of the Iterator.
- Correct use of the functionality provided by Collections and Comparators(implemented with lambdas:
  DOCK_COMPARATOR in BikeStation; BY_ID, BY_ARRIVAL, BY_RECHARGES and BY_STATION_ID in EBikeDemo).
- Unit tests for the project using JUnit.
- Use of the Git functionality provided by BlueJ.

HOW TO START: execute the static main() method of the EBikeDemo class.
The scenario is chosen with the DEMO constant (DemoType.SIMPLE, MEDIUM or
ADVANCED). The output of the three scenarios is included in the files
salida_simple.txt, salida_medium.txt and salida_advanced.txt.