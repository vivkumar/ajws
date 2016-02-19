
PWD=`pwd`
TEST="$PWD/src/test/java/com/thebuzzmedia/sjxp/benchmark"

############# First copy jars at relavent places and then build the benchmark

cp dist/sjxp-2.2.jar src/test/java/com/thebuzzmedia/sjxp/benchmark/.
cp lib/xpp3-1.1.4c.jar src/test/java/com/thebuzzmedia/sjxp/benchmark/.
cp ../jars/jksvm.jar src/test/java/com/thebuzzmedia/sjxp/benchmark/.
cd src/test/java/com/thebuzzmedia/sjxp/benchmark
javac -cp .:sjxp-2.2.jar:xpp3-1.1.4c.jar:jksvm.jar Benchmark.java

######## Now Run the Benchmark ##########
java -classpath .:sjxp-2.2.jar:xpp3-1.1.4c.jar Benchmark
