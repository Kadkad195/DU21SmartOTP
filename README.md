# DU21SmartOTP
Easy implement Smart OTP into you projects.

## Step1:

*  @param period: otp will be expired after that period. Default 30000 = 30s
*  @param digit: length of otp value. Default 6
*  @param generateTime: how many otp will be generated. Default 2
```
val smartOTP = DU21SmartOTP(period, digit, generateTime)
```
```
val shareKey = smartOTP.createShareKey("YOUR_SECRET") //Should be your PIN code + your device id
```
Then send this secret to server to register. Your server will use this secret to generate OTP too


## Step2:
* register listenser and expired handler
```
smartOTP.listenerOTP: Listener will return current otp and seconds before expired
smartOTP.listenerOTPExpired: Listener invoke when otp is expired 
```

## Step3:
* Each time create otp, inout your secret (PIN, password,....) as you use to generate shareKey to generate right OTP
* If input wrong secret -> Your OTP will be wrong
```
smartOTP.createTOTP("YOUR_SECRET")
```

## Step4:
* Use clear() when stop using
```
smartOTP.clear()
```
## GRADLE
Add it in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Add the dependency
```
dependencies {
        implementation 'com.github.Kadkad195:DU21SmartOTP:1.1.2'
}
```
## MAVEN
Add the JitPack repository to your build file
```
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
```
Add the dependency
```
<dependency>
    <groupId>com.github.Kadkad195</groupId>
    <artifactId>DU21SmartOTP</artifactId>
    <version>1.1.2</version>
</dependency>
```

CMC GLOBAL DU21
