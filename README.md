[![Build Status](https://jenkins.flowingcode.com/job/RssItems-14-addon/badge/icon)](https://jenkins.flowingcode.com/job/RssItems-14-addon)
[![Javadoc](https://img.shields.io/badge/javadoc-00b4f0)](https://javadoc.flowingcode.com/artifact/com.flowingcode.addons/rss-items)

# RSS Items Addon

Vaadin 14+ RSS display component based on https://github.com/TherapyChat/rss-items

## Online demo

[Online demo here](http://addonsv14.flowingcode.com/rss-items)

## Building and running demo

- git clone repository
- mvn clean install jetty:run

To see the demo, navigate to http://localhost:8080/

## Release notes

- **Version 2.0.0** Initial release for Vaadin 14+ NPM mode
- **Version 1.0.0** Initial Version

## Issue tracking

The issues for this add-on are tracked on its github.com page. All bug reports and feature requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:

- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Refer to the fixed issue in commit
- Send a pull request for the original project
- Comment on the original issue that you have implemented a fix for it

## License & Author

This add-on is distributed under Apache License 2.0. For license terms, see LICENSE.txt.

RssItems Addon is written by Flowing Code S.A.


# Developer Guide

## Using the component

- Call constructor
```
RssItems items = new RssItems("https://www.flowingcode.com/feeds/posts/default?alt=rss");
```
- Advanced constructor
```
RssItems items = new RssItems("https://www.flowingcode.com/feeds/posts/default?alt=rss",6,100,100);
```
