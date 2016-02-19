# FIRST SET THE PATH TO RVM OF TryCatchWS BELOW:

RVM="/home/vivek/vivek_work/work-stealing/rvm/production_ws"

####################################################################

if [ $# -ne 1 ]; then
	echo "USAGE: ./run.sh <WS_THREADS>"
	exit
fi

PWD=`pwd`
TEST="$PWD/src/test/java/com/thebuzzmedia/sjxp/benchmark"

############# First copy jars at relavent places and then build the benchmark

cp dist/sjxp-2.2.jar src/test/java/com/thebuzzmedia/sjxp/benchmark/.
cp lib/xpp3-1.1.4c.jar src/test/java/com/thebuzzmedia/sjxp/benchmark/.
cp ../jars/jksvm.jar src/test/java/com/thebuzzmedia/sjxp/benchmark/.
cd src/test/java/com/thebuzzmedia/sjxp/benchmark
javac -cp .:sjxp-2.2.jar:xpp3-1.1.4c.jar:jksvm.jar Benchmark.java

######## Now Run the Benchmark ##########
$RVM/rvm -Xms1024M -X:gc:variableSizeHeap=false -X:gc:threads=1 -Xws:procs=$1 -Xws:autoThreads=true -classpath .:sjxp-2.2.jar:xpp3-1.1.4c.jar Benchmark
