
# 00 : Installation

<div class="inline-container">
<img src="../images/noun_Stopwatch_14262_100.png">
<strong>
  Estimated time: 15 minutes
</strong>
</div>

The purpose of this lab is to help you setup your PC with the required software to run this lab.
![User Input](../images/noun_Computer_3477192_100.png)
![Shell Script](../images/noun_SH_File_272740_100.png)


## Install GraalVM Enterprise :rocket:
The instructions to install GraalVM can be found online[here](https://docs.oracle.com/en/graalvm/enterprise/20/docs/getting-started/installation-linux/)

This workshop assumes that you are using either Linux or Mac, but is equally applicable to Windows. You might just have to adpat it, for example the bash shell script sections etc may need to be updated.

```bash
# Untar in your prefered location
sudo tar -xvf ~/Downloads/graalvm-ee-java11-darwin-amd64-21.2.0.tar.gz -C /Library/Java/JavaVirtualMachines/
```

In case you already have the GraalVM installed, you can use sdkm man to reference it 

```bash
#Make sure the version is referernced by SDKMan
$ sdk install java  21.2.0-ee11 /Library/Java/JavaVirtualMachines/graalvm-ee-java11-21.2.0/Contents/Home/

Linking java 21.2.0-ee11 to /Library/Java/JavaVirtualMachines/graalvm-ee-java11-21.2.0/Contents/Home/
Done installing!
```

 
```bash
#Use the last Enterprise version
$ sdk use java 21.2.0-ee11
```


```bash
#Check the version you are using
java -version
java version "11.0.12" 2021-07-20 LTS
Java(TM) SE Runtime Environment GraalVM EE 21.2.0 (build 11.0.12+8-LTS-jvmci-21.2-b06)
Java HotSpot(TM) 64-Bit Server VM GraalVM EE 21.2.0 (build 11.0.12+8-LTS-jvmci-21.2-b06, mixed mode, sharing)
```

## Install GraalVM Extensions for Guest languages 

By default GraalVM comes with Javascript language extension, you need to install others languages support with Graal updater tool

```bash
# Install python, R ( mandatory ) , native-image(optionnal for this lab)
gu install python
gu install R
```

Run the following gu commands and accept the licences requirements when asked.
```bash
#Check GraalVM Component list
$  gu list
ComponentId              Version             Component name                Stability                     Origin
---------------------------------------------------------------------------------------------------------------------------------
graalvm                  21.2.0              GraalVM Core                  -
R                        21.2.0              FastR                         Experimental                  github.com
js                       21.2.0              Graal.js                      Supported
llvm-toolchain           21.2.0              LLVM.org toolchain            Supported                     github.com
native-image             21.2.0              Native Image                  Early adopter                 oca.opensource.oracle.com
python                   21.2.0              Graal.Python                  Experimental                  oca.opensource.oracle.com
```

## Install Helidon CLI

Use the following instructions to setup Helidon CLI for your target platform
[Helidon CLI setup ](https://github.com/oracle/helidon/blob/master/HELIDON-CLI.md)


Check the helidon version 
```
helidon version
build.date      2021-04-30 13:00:06 PDT
build.version   2.2.0
build.revision  17f7cba0
latest.helidon.version  2.3.2
```


## Others Tools and utilities

You may also like to install the following tools
* [Apache Maven](https://maven.apache.org/)
* [sdkman](https://sdkman.io/)
* [httpie](https://httpie.io/) or [curl](https://curl.se/docs/manpage.html) command
