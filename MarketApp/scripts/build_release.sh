#!/bin/sh -x

PATH=/Applications/NetBeans/NetBeans\ 6.8.app/Contents/Resources/NetBeans/java3/ant/bin:$PATH
ant clean
echo "Remember to set build.properties"
ant release

