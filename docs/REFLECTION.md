# Reflection Log

This document captures reflections on the development of 3D geometric classes in Java, focusing on design patterns, principles, and lessons learned.

The pattern I chose to learn more about is the Value Object pattern. This pattern is used to create immutable objects that represent a value, such as a point in a 3D space in Project 1. The Value Object pattern
is beneficial because it allows for easy comparison of objects based on their attributes rather than a unique identifier. It is also useful for creating objects that are easy to create and makes sure that methods do not modify the state of the object. 
This allows for easier readability and maintainability of the code. An example can be seen in the Point3D class, where each point is represented by the x, y, and z coordinates. The Point3D class is immutable, meaning
that once the Point3D object is created, its state cannot be changed. This provides for easy comparison of other Point3D objects based on their coordinates. Using this pattern also provides thread safety, 
since the state of the object cannot be changed after it is created. There are many examples and use cases for the Value Object pattern in software development, such as representing currency values, addresses, or dates. Using
this pattern can help keep the codebase clean and consistent.