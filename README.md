BUPTWifiHelper
==============
This is a dirty but practical solution to programmly login and manipulate the __WIFI GATEWAY__ in BUPT campus.
It provides a more effective way for users to manage their devices online and offline.

# API
The core API is provided in AndroidWifiHelper and the coresponding interfaces are defined in WifiHelperInterface.
* `setProcessor`, set the handler which holds the AndroidWifiHelper instance.
* `loginGW`, once specified an account-password pair, the helper will automatically login the wifi gateway.
* `processWifiHelperStatusChanged`, this interface is designed to inform users what is going on in wifihelper for some time-consuming operation.
* `checkOnlineIps`, this function will be called when an account login successfully, and can be invoked to check the online IPs in the futrue.
* `processIpInUse`, this interface returns a list of IPs where an account shows up.
* `forceIpOffLine`, this is what the whole project try to do which is to force a specified IP offline.
* `processForceOfflineResponse`, this function is invoked after the `forceIpOffLine` function called, tell user if the force offline operation succeeds.
