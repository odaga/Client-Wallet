# Android Clean Architecture Template

## Project structure

### Data module
Contains the logic of bringing data either from local source or remote server. 
All network implementations including retrofit services and api client are implemented here

### Domain module
Contains the logic of business: convert, filter, mix and sort raw data that comes from Data layer
to be ready and easy to handle in Presentation layer.

### App mudule
Contains UI components and the logic that related to user interactions or navigation in order to get data from the user.

### Core module
Contains all util and data classes