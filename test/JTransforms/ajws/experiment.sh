
RVM=/Path/to/TryCatchWS/rvm"

#####################################################

if [ $# -lt 1 ]
then
	echo "USAGE: ./experiment.sh <WS_THREADS>>"
	exit
fi

###### STANDARD ARGUMENTS #########

nthread=1
threadsBegin2D=65636
threadsBegin3D=65636
niter=2
doWarmup="false"
doScaling="true"
nsize=1
nsize_1=1048576
nsize_2=1024
nsize_3=128

###################################

BENCHMARK="edu.emory.mathcs.jtransforms.AllTest"

$JIKESRVM -Xms1024M -X:gc:threads=1 -X:gc:variableSizeHeap=false -X:gc:fullHeapSystemGC=true -Xws:stats=true -Xws:autoThreads=true -Xws:procs=$1 $2 -cp jtransforms-ws.jar $BENCHMARK $nthread $threadsBegin2D $threadsBegin3D $niter $doWarmup $doScaling $nsize $nsize_1 $nsize_2 $nsize_3

