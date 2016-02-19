
RVM="/home/vivek/vivek_work/work-stealing/rvm/production_base"
LOGDIR="/home/vivek/vivek_work/atomicset/logs"
PWD=`pwd`
TEST="$PWD/src/test/java/com/thebuzzmedia/sjxp/benchmark"
####################################################################

echo 
echo "-----"
echo "mkdir -p $TEST 2>/dev/null; cd $TEST; $TEST/timedrun -t 100 $RVM/rvm -Xms1024M -X:gc:variableSizeHeap=false -X:gc:threads=1  -classpath .:sjxp-2.2.jar:xpp3-1.1.4c.jar Benchmark"
echo "OS: "`uname -a`
echo "cpu: "`cat /proc/cpuinfo | grep 'model name' | head -1`
echo "number of cores: "`cat /proc/cpuinfo | grep MHz | wc -l`


cd $TEST
$RVM/rvm -Xms1024M -X:gc:variableSizeHeap=false -X:gc:threads=1 -classpath .:sjxp-2.2.jar:xpp3-1.1.4c.jar Benchmark
