# GraalVM Polyglot Labs

## Introduction 

The purpose of this lab series is to gradually build a complete GraalVM Polyglot application 
using  various languages:
* `Java` 
* `Javascript`
* `Python` 
* `R` 

Functionally, the application makes it possible to visualize the covid-19 hospitalizations  trends in a French in France department. This application relies on data provided by the French Pucblic Health Agency [Santé Publique France](https://www.data.gouv.fr/fr/datasets/donnees-hospitalieres-relatives-a-lepidemie-de-covid-19/).


## Stack

The application consists of a JAX-RS Controller with a set of  REST Helidon endpoints that enables users to visualize a Covid-19 evolution in France in a Polyglot way.

![](./images/polyglotintro.png)

 ## Table of Contents
* [01. Installation](./00/)
* [02. Building a Simple GraalVM Polyglot Application](./01/)
* [03. Interacting with Guest Language resources](./02/)
* [04. Computed Arrays Using Polyglot Proxies](./03/)


## Resources
*  The lab is built from the full github example https://github.com/nelvadas/helidon-polyglot-demo
*  GraalVM Polyglot Reference  https://www.graalvm.org/reference-manual/embed-languages/
*  Full Blog post  [Medium](https://medium.com/@nelvadas/polyglot-micro-service-for-visualizing-covid-19-trends-with-graalvm-helidon-java-r-python-c-a3dce4262eb3) 
*  Dataset Stable Link  [Covid-19 hospitalizations data file](https://www.data.gouv.fr/fr/datasets/r/6fadff46-9efd-4c53-942a-54aca783c30c)