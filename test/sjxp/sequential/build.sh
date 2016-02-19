cp dist/sjxp-2.2.jar src/test/java/com/thebuzzmedia/sjxp/benchmark/.
cp lib/xpp3-1.1.4c.jar src/test/java/com/thebuzzmedia/sjxp/benchmark/.
cp  ~/vivek_work/work-stealing/rvm/production_ws/jksvm.jar src/test/java/com/thebuzzmedia/sjxp/benchmark/.
cd src/test/java/com/thebuzzmedia/sjxp/benchmark
javac -cp .:sjxp-2.2.jar:xpp3-1.1.4c.jar:jksvm.jar Benchmark.java
