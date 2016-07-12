# CanOpenJavaLibrary
An interface between canopen and java
##Creating a CanOpen instance
```java
    private static void createNewInstance(String ipAddress, Integer portNum){
        System.out.println(ipAddress+":"+Integer.toString(portNum));
        DriverManager dm = new DriverManager("Datagram",ipAddress,portNum,true);
        System.out.println("Got driver manager");
        Driver driver =dm.getDriver();
        System.out.println("Got driver");
        ObjectDictionary objDict = DefaultOD.create(0x22);
        instance = new CanOpen(driver,objDict,0x22,true);
        System.out.println("Got Canopen");
    }
```

