# Design-And-Implementation-Of-Secured-Location-Tracking-Application
Location Tracking Android Application



### **Overview**
The **Design-And-Implementation-Of-Secured-Location-Tracking-Application** is a powerful and user-friendly application designed to ensure the safety and security of children by enabling real-time location tracking and activity monitoring. The system features two integrated modules: a **Parent Module** and a **Child Module**, providing seamless communication and oversight.

---

### **Features**

#### **Parent Module**
- **Sign-In Required**: Secure authentication for parents.
- **Child Profile Management**: Add and manage child profiles with detailed information.
- **Activity Monitoring**: Access real-time location, contacts, text messages, and call logs from the child's device.

#### **Child Module**
- **Child Login**: Secure access using credentials provided by the parent.
- **Calculator Functionality**: Built-in calculator application for everyday use.
- **Activity Sharing**: Automatically shares location, contacts, text messages, and call logs with the Parent Module.

---

### **Technology Stack**
- **Frontend**: XML
- **Backend Database**: MSSQL
- **Programming Language**: Java

---

### **Purpose**
This project aims to enhance child safety by providing parents with tools to monitor and track their child's activities, ensuring peace of mind and improved communication between family members.

---




## **Installation and Setup for Android Application with MS SQL Backend**

### **1. Install Android Studio**

To begin working on the Android app, you need to install **Android Studio**.

1. **Download Android Studio**:
   - Visit the official [Android Studio download page](https://developer.android.com/studio).
   - Download the installer for your operating system (Windows, macOS, or Linux).

2. **Install Android Studio**:
   - Follow the on-screen instructions for installation.
   - During installation, ensure you select the options to install the **Android SDK** and **Android Virtual Device (AVD)**.

3. **Launch Android Studio**:
   - Once installed, open **Android Studio** and complete the initial setup.
   - Android Studio may prompt you to install some additional SDK components. Accept the prompts and install them.

---

### **2. Install MS SQL Server**

To set up **MS SQL Server**, follow these steps:

1. **Download MS SQL Server**:
   - Go to the [Microsoft SQL Server download page](https://www.microsoft.com/en-us/sql-server/sql-server-downloads).
   - Download the **SQL Server Express** edition for free (or the full version if you have a license).

2. **Install MS SQL Server**:
   - Launch the installer and follow the installation wizard.
   - Choose the **Basic** installation option, which installs the default settings.
   - During installation, make note of the **SQL Server instance name**, **username**, and **password** you choose for accessing the database.

3. **Install SQL Server Management Studio (SSMS)** (Optional but recommended):
   - SSMS is a tool for managing your SQL Server instances. You can download it from the same page as SQL Server.
   - After installation, use SSMS to connect to your SQL Server instance and create your database.

4. **Create a Database**:
   - Open **SQL Server Management Studio (SSMS)** and connect to your server using the credentials you set up during installation.
   - Right-click **Databases** in the Object Explorer and select **New Database**.
   - Name it something like `child_tracking` and click **OK**.

---

### **3. Set Up MS SQL Database and Backend API**

The Android app communicates with the MS SQL database via an intermediary backend API.

---

### **4. Set Up the Android Application**

#### **A. Open the Project in Android Studio**
1. Launch **Android Studio**.
2. Open your project by going to **File > Open** and selecting the project folder.

#### **B. Install Dependencies**
Ensure all required Android dependencies are installed and up to date:

1. **Sync Gradle**:
   In Android Studio, click **Sync Now** (if prompted) or go to **File > Sync Project with Gradle Files** to download and set up dependencies from the `build.gradle` files.

   ```

#### **C. Set Up Network Calls to the Backend API**
Use **Retrofit** to send requests from the Android app to the backend API.


#### **D. Set Up Permissions**
Make sure your app has permissions to access the internet and location services. In **`AndroidManifest.xml`**, add:
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

---

### **5. Run the Application**

1. **Connect an Android Device**: Use a physical Android device or an emulator.
2. **Run the App**: Click the **Run** button (green triangle) in Android Studio, or press **Shift + F10**. Select the connected device or emulator to install and run the app.

---

### **6. Troubleshooting**

- **MS SQL Connection Issues**: Ensure the backend API can connect to the MS SQL Server instance using the correct credentials.
- **App Permissions**: Check that the app has the correct permissions to access location and the internet.
- **Backend API Errors**: Verify that your backend API is running and accessible from your Android device (check the API URL and port).

---

This setup guide should cover the essential steps for installing Android Studio, configuring MS SQL Server, and running your **Child Location Tracking** app with a backend API.


<img src="Images/child_calculatot_interface.jpeg" alt="Example Image" width="400" height="300">

