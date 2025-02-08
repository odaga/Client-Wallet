# Android Clean Architecture Template

## Project Structure

### Data Module
Contains the logic of bringing data either from a local source or a remote server. All network implementations, including Retrofit services and API clients, are implemented here.

### Domain Module
Contains the business logic: converting, filtering, mixing, and sorting raw data that comes from the Data layer to make it ready and easy to handle in the Presentation layer.

### App Module
Contains UI components and logic related to user interactions or navigation to get data from the user.

### Core Module
Contains all utility and data classes.

## Adding Firebase to the Project

## Managing Secret Keys and Sensitive Data
To securely store sensitive data such as API keys and credentials:
1. Create a file named `hidden.properties` in the root of your project.
2. Add all necessary credentials in the following format:
   ```properties
   
   CLIENT_VERSION_URL= client_version_url
   CLIENT_DEV_URL=client_dev_url_here
   CLIENT_BASE_URL=client_base_url_here
   CLIENT_STG_URL=client_stg_url_here
   CLIENT_KYC_BASE_URL=client_kyc_base_url_here

   ```
3. Ensure that `hidden.properties` is added to `.gitignore` to prevent accidental exposure.
4. Modify your `gradle.properties` or `build.gradle` to load these values securely.
To integrate Firebase into the project:
1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2. Download the `google-services.json` file from Firebase.
3. Place the `google-services.json` file inside the `app` module directory.
4. Add the required Firebase dependencies in your `build.gradle` files.

## Download Sample APK
You can download the latest sample APK from [this link](https://drive.google.com/file/d/13a5LD78Xc8hoo8-9PYI8ZeSdMT9kBXzo/view?usp=sharing). The link will be updated here once the APK is available, or a temporary location can be provided upon request.
